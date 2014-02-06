package it.sistemisnc.turbine.listeners;

import it.sistemisnc.turbine.data.Message;

/**
 * Listener for turbine queue
 */
public interface IQueueListener {

    void onMessage(String queueName, Message message);

    void onReply(String queueName, Message message);

    Message onDirectMessage(String queueName, Message message);
}
