package it.sistemisnc.turbine.data;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by squid on 04/02/14.
 */
@Data
/**
 * Network info message,
 * Is automatically fill when send/receive form server
 */
public class NetworkMessage implements Serializable {

    private String senderUid = "";
    private String targetUid = "";

}
