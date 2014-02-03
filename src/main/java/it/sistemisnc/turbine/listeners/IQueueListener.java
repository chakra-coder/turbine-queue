package it.sistemisnc.turbine.listeners;

import it.sistemisnc.turbine.data.Message;

/**
 * Created by squid on 03/02/14.
 */
public interface IQueueListener {

    void onMessage(String queueName, Message message);

    void onReply(String queueName, Message message);

    Message onDirectMessage(String queueName, Message message);
}
