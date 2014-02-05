package it.sistemisnc.turbine.utils;

import it.sistemisnc.turbine.data.Message;

import java.util.UUID;

/**
 * Created by squid on 05/02/14.
 */
public class MessagesFactory {

    public static Message buildMessage(Class<?> senderClass, int messageType, Object data)
    {
        Message message = new Message(UUID.randomUUID().toString());
        message.setMessageType(messageType);
        message.setData(data);

        return message;
    }
}
