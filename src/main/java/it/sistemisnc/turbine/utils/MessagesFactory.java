package it.sistemisnc.turbine.utils;

import it.sistemisnc.turbine.data.Message;

import java.util.UUID;

/**
 * Factory for create messages
 */
public class MessagesFactory {

    public static Message buildMessage(Class<?> senderClass, int messageType, Object data)
    {
        Message message = new Message(UUID.randomUUID().toString());
        message.setMessageType(messageType);
        message.setData(data);
        message.setSenderClass(senderClass.getName());

        return message;
    }
}
