package net.chrotos.chrotoscloud.messaging.queue;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rabbitmq.client.*;
import lombok.NonNull;
import net.chrotos.chrotoscloud.Cloud;
import net.chrotos.chrotoscloud.CloudConfig;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.UUID;

public class RabbitQueueAdapter implements QueueAdapter, AutoCloseable {
    public static final String CLOUD_EXCHANGE = "cloud";

    private Gson gson;
    private ConnectionFactory factory;
    private Connection connection;
    private Channel publishOnlyChannel;

    public void configure(CloudConfig config) {
        gson = new GsonBuilder().create();

        factory = new ConnectionFactory();
        factory.setHost(config.getQueueHost());
        factory.setPort(config.getQueuePort());
        factory.setUsername(config.getQueueUser());
        factory.setPassword(config.getQueuePassword());
        factory.setAutomaticRecoveryEnabled(true);
    }

    public void initialize() {
        if (factory == null) {
            throw new IllegalStateException("Queue is not configured!");
        }

        try {
            connection = factory.newConnection();
            publishOnlyChannel = connection.createChannel();

            publishOnlyChannel.exchangeDeclare(CLOUD_EXCHANGE, "fanout", true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <E,T> Registration<E,T> register(@NonNull Listener<E,T> listener, @NonNull String channel) throws IOException {
        if (listener.getReplyClass() == null && listener.getMessageClass() == null) {
            throw new IllegalArgumentException("At least a message or a reply class has to be defined.");
        }

        checkConnected();

        final String wantedChannel = channel;
        final Channel mqChannel = connection.createChannel();

        if (listener.getMessageClass() != null && listener.getMessageClass() != Void.class) {
            final String queue = Cloud.getInstance().getHostname() + ":" + UUID.randomUUID();
            mqChannel.queueDeclare(queue, false, true, true, null);
            mqChannel.queueBind(queue, CLOUD_EXCHANGE, "");

            DefaultConsumer messageConsumer = new DefaultConsumer(mqChannel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
                    String messageChannel = properties.getHeaders().get("channel").toString();
                    String sender = properties.getHeaders().get("sender").toString();
                    System.out.println(messageChannel + "(" + sender + ";" + properties.getReplyTo() + "): " + new String(body, StandardCharsets.UTF_8)); // TODO remove
                    // Channel header does not contain this channel
                    if (!messageChannel.equals(wantedChannel)
                            // or message came from this sender
                            || sender.equals(Cloud.getInstance().getHostname())) {

                        try {
                            mqChannel.basicAck(envelope.getDeliveryTag(), false);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return;
                    }

                    try {
                        System.out.println("Message"); // TODO remove
                        listener.onMessage(makeMessage(mqChannel, wantedChannel, properties.getReplyTo(),
                                gson.fromJson(new String(body, StandardCharsets.UTF_8), listener.getMessageClass())), sender);
                        mqChannel.basicAck(envelope.getDeliveryTag(), false);
                    } catch (Exception e) {
                        try {
                            mqChannel.basicNack(envelope.getDeliveryTag(), false, true);
                        } catch (Exception e2) {
                            e.printStackTrace();
                        }
                        e.printStackTrace();
                    }
                }
            };

            mqChannel.basicConsume(queue, messageConsumer);
        }

        if (listener.getReplyClass() != null && listener.getReplyClass() != Void.class) {
            DefaultConsumer replyConsumer = new DefaultConsumer(mqChannel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
                    String messageChannel = properties.getHeaders().get("channel").toString();
                    String sender = properties.getHeaders().get("sender").toString();
                    // Channel header does not contain this channel
                    if (!messageChannel.equals(wantedChannel)) {
                        return;
                    }

                    try {
                        System.out.println("Reply"); // TODO remove
                        listener.onReply(makeMessage(mqChannel, wantedChannel, properties.getReplyTo(),
                                gson.fromJson(new String(body, StandardCharsets.UTF_8), listener.getReplyClass())), sender);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };

            mqChannel.basicConsume("amq.rabbitmq.reply-to", true, replyConsumer);
        }

        return new Registration<>() {
            @Override
            public void close() throws Exception {
                unsubscribe();
            }

            private boolean unsubscribed = false;

            @Override
            public void unsubscribe() {
                try {
                    if (!isSubscribed()) {
                        return;
                    }

                    unsubscribed = true;
                    mqChannel.close();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public boolean isSubscribed() {
                return !unsubscribed && isConnected();
            }

            @Override
            public boolean isConnected() {
                return mqChannel.isOpen();
            }

            @Override
            public Listener<E, T> getListener() {
                return listener;
            }

            @Override
            public boolean publish(@NonNull Object message) throws IOException {
                if (!isConnected()) {
                    return false;
                }

                RabbitQueueAdapter.this.publish(mqChannel, wantedChannel, "", message);
                return true;
            }
        };
    }

    private <E> CloudMessage<E> makeMessage(Channel mqChannel, String channel, String replyTo, E message) {
        return new CloudMessage<>() {
            @Override
            protected Channel getMQChannel() {
                return mqChannel;
            }

            @Override
            @NonNull
            public E getMessage() {
                return message;
            }

            @Override
            public void replyTo(@NonNull Object reply) throws IOException {
                publish(mqChannel, "", channel, replyTo, reply);
            }
        };
    }

    @Override
    public <E> void publish(@NonNull String channel, @NonNull E object) {
        publish(channel, "", object);
    }

    @Override
    public <E> void publish(@NonNull String channel, @NonNull String routingKey, @NonNull E object) {
        checkConnected();

        try {
            publish(publishOnlyChannel, channel, "", object);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private <E> void publish(@NonNull Channel mqChannel, @NonNull String channel, @NonNull String routingKey, @NonNull E object) throws IOException {
        publish(mqChannel, CLOUD_EXCHANGE, channel, routingKey, object);
    }

    private <E> void publish(@NonNull Channel mqChannel, @NonNull String exchange, @NonNull String channel,
                             @NonNull String routingKey, @NonNull E object) throws IOException {

        mqChannel.basicPublish(exchange, routingKey, getAMQPProperties(channel, !exchange.isBlank()),
                gson.toJson(object).getBytes(StandardCharsets.UTF_8));
    }

    private AMQP.BasicProperties getAMQPProperties(String channel, boolean fastReply) {
        HashMap<String, Object> headers = new HashMap<>();
        headers.put("channel", channel);
        headers.put("sender", Cloud.getInstance().getHostname());

        return new AMQP.BasicProperties.Builder()
                    .replyTo(fastReply ? "amq.rabbitmq.reply-to" : null)
                    .contentType("application/json")
                    .headers(headers)
                    .build();
    }

    private void checkConnected() {
        if (connection == null || !connection.isOpen()) {
            throw new QueueUnconnectedException();
        }
    }

    @Override
    public void close() throws Exception {
        publishOnlyChannel.close();
        connection.close();
    }
}
