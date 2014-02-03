package it.sistemisnc.turbine.data;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by squid on 03/02/14.
 */
@Data
@AllArgsConstructor
public class Message implements Serializable {


    public enum MessageFlow {
        INPUT, OUTPUT
    }

    private int messageType;
    private int priority = 10;
    private String senderClass;
    private String targetClass;
    private String guid;
    private MessageFlow messageFlow = MessageFlow.INPUT;
    private boolean replyToSender = false;



    private Serializable data;


    public Message(String senderClass, Serializable data)
    {
        this.guid = UUID.randomUUID().toString();
    }

    public Message(String guid)
    {
        this.guid = guid;
    }


}
