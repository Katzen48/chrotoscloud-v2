package net.chrotos.chrotoscloud.messaging.queue;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.rabbitmq.client.*;
import lombok.NonNull;
import net.chrotos.chrotoscloud.Cloud;
import net.chrotos.chrotoscloud.CloudConfig;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

public class RabbitQueueAdapter implements QueueAdapter, AutoCloseable {
    public static final String CLOUD_EXCHANGE = "cloud";

    private Gson gson;
    private ConnectionFactory factory;
    private Connection connection;
    private Channel channel;

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
            channel = connection.createChannel();

            channel.queueDeclare(Cloud.getInstance().getHostname(), false, true, true, null);
            channel.exchangeDeclare("cloud", "fanout", true);
            channel.queueBind(Cloud.getInstance().getHostname(), "cloud", "");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <E,T> Registration<E,T> register(@NonNull Listener<E,T> listener, @NonNull String channel) {
        checkConnected();

        DefaultConsumer consumer = new DefaultConsumer(this.channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
                JsonObject message = gson.fromJson(new String(body, StandardCharsets.UTF_8), JsonObject.class);

                        // If does not have a channel, which matches requested channel
                if (!((message.has("channel") && channel.equals(message.get("channel").getAsString()))
                            // And channel header does not contain this channel
                            || (properties.getHeaders().containsKey("channel")
                            && properties.getHeaders().get("channel").equals(channel)))
                        // or this message has no object
                        || (!message.has("object"))) {
                    return;
                }

                String sender = message.has("sender") ? message.get("sender").getAsString() : null;

                try {
                    if (envelope.getExchange().equals("")) {
                        listener.onReply(makeMessage(channel, properties.getReplyTo(),
                                        gson.fromJson(message.get("object"), listener.getReplyClass())), sender);
                    } else {
                        listener.onMessage(makeMessage(channel, properties.getReplyTo(),
                                        gson.fromJson(message.get("object"), listener.getMessageClass())), sender);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        try {
            this.channel.basicConsume(CLOUD_EXCHANGE, consumer);
            this.channel.basicConsume("amq.rabbitmq.reply-to", true, consumer);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return new Registration<>() {
            private boolean unsubscribed = false;

            @Override
            public void unsubscribe() {
                try {
                    unsubscribed = true;
                    RabbitQueueAdapter.this.channel.basicCancel(consumer.getConsumerTag());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public boolean isSubscribed() {
                return !unsubscribed && RabbitQueueAdapter.this.channel.isOpen();
            }

            @Override
            public Listener<E, T> getListener() {
                return listener;
            }
        };
    }

    private <E> Message<E> makeMessage(String channel, String replyTo, E object) {
        return new Message<>() {
            @Override
            public E getMessage() {
                return object;
            }

            @Override
            public void replyTo(@NonNull Object object) {
                publish("", channel, replyTo, object);
            }
        };
    }

    @Override
    public <E> void publish(@NonNull String channel, @NonNull E object) {
        publish(channel, "", object);
    }

    @Override
    public <E> void publish(@NonNull String channel, @NonNull String routingKey, @NonNull E object) {
        publish(CLOUD_EXCHANGE, channel, routingKey, object);
    }

    private <E> void publish(@NonNull String exchange, @NonNull String channel, @NonNull String routingKey,
                             @NonNull E object) {
        checkConnected();

        try {
            this.channel.basicPublish(exchange, routingKey, getAMQPProperties(channel),
                    gson.toJson(object).getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private AMQP.BasicProperties getAMQPProperties(String channel) {
        return new AMQP.BasicProperties.Builder()
                    .replyTo("amq.rabbitmq.reply-to")
                    .contentType("application/json")
                    .headers(Collections.singletonMap("channel", channel))
                    .build();
    }

    private void checkConnected() {
        if (channel == null || !channel.isOpen()) {
            throw new QueueUnconnectedException();
        }
    }

    @Override
    public void close() throws Exception {
        channel.close();
        connection.close();
    }
}
