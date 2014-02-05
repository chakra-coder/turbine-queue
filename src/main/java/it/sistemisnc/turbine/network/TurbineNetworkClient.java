package it.sistemisnc.turbine.network;

import it.sistemisnc.turbine.data.Message;
import it.sistemisnc.turbine.threads.network.client.ClientHandlerReceiver;
import it.sistemisnc.turbine.threads.network.client.ClientHandlerSender;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.net.Socket;

/**
 * Created by squid on 04/02/14.
 */
public class TurbineNetworkClient  {

    private static class Holder { static final TurbineNetworkClient INSTANCE = new TurbineNetworkClient(); }
    public static TurbineNetworkClient getInstance() { return Holder.INSTANCE;  }

    private Logger logger = Logger.getLogger(this.getClass());


    @Getter @Setter
    private String sessionId;


    private Thread thClientSender;
    private Thread thClientReceiver;


    private ClientHandlerSender clientHandlerSender;
    private ClientHandlerReceiver clientHandlerReceiver;




    public void start(String address, int port)
    {

        try
        {

            Socket socket = new Socket(address, port);

            clientHandlerSender = new ClientHandlerSender(socket);
            clientHandlerReceiver = new ClientHandlerReceiver(socket);

            thClientSender = new Thread(clientHandlerSender);
            thClientSender.start();

            thClientReceiver = new Thread(clientHandlerReceiver);
            thClientReceiver.start();


        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }


    }


    public void enqueueMessage(String queueName, Message message) throws Exception {

        message.getExtra().put("sessionId", getSessionId());
        message.setQueueName(queueName);
        clientHandlerSender.enqueue(message);

    }

    public void startClientSender()
    {
        clientHandlerSender.start();
    }



    protected void log(Level level, String text, Object ... args)
    {
        logger.log(level, String.format(text, args));

    }

    protected void logException(Exception ex, String text, Object ... args)
    {
        logger.log(Level.FATAL, String.format("Exception encurred\r\n %s ==> %s",String.format(text, args), ex.getMessage()));
    }


}
