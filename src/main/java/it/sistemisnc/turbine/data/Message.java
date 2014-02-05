package it.sistemisnc.turbine.data;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by squid on 03/02/14.
 */
@Data
@AllArgsConstructor
public class Message implements Serializable {


    public enum MessageFlow { INPUT, OUTPUT }

    private int messageType;
    private String queueName;

    private boolean remoteMessage;

    private NetworkMessage remoteMessageInfo = new NetworkMessage();

    private int priority = 10;
    private String senderClass;
    private String targetClass;
    private String guid;
    private MessageFlow messageFlow = MessageFlow.INPUT;
    private boolean replyToSender = false;
    private HashMap<String, Object> extra = new HashMap<String, Object>();

    private Object data;

    public Message(String senderClass, Serializable data)
    {
        this.guid = UUID.randomUUID().toString();
    }

    public Message(String guid)
    {
        this.guid = guid;
    }


}
