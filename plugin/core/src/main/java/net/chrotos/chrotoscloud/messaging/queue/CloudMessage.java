package net.chrotos.chrotoscloud.messaging.queue;

import com.rabbitmq.client.Channel;

public abstract class CloudMessage<E> implements Message<E> {
    protected abstract Channel getMQChannel();
}
