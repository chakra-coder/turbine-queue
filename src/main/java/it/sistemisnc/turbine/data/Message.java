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
/**
 * Message for the queue
 */
public class Message implements Serializable {

    /**
     * Message type of message
     * types 100...199 are reserved for network messages
     */
    private int messageType;
    /**
     * Queue name
     */
    private String queueName;
    /**
     * When message pass though the server, automatically set remoteMessage = true
     */
    private boolean remoteMessage;
    /**
     * Remote message info, senderUID and targetUID
     */
    private NetworkMessage remoteMessageInfo = new NetworkMessage();
    /**
     * Priority is set to default at 10, for high priority set 1
     */
    private int priority = 10;
    private String senderClass;
    private String targetClass;
    private String guid;
    private MessageFlowType messageFlow = MessageFlowType.INPUT;
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
