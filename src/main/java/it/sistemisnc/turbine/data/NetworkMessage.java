package it.sistemisnc.turbine.data;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by squid on 04/02/14.
 */
@Data
public class NetworkMessage implements Serializable {

    private String queueName;
    private String senderUid;

    private String messageClassName;

    private String message;

}
