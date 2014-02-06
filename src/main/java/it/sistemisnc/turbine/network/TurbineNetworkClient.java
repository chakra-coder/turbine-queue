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
 * Network client for turbine queue, for now you can subscribe one queue
 *
 *
 * Example:
 *
 * TurbineNetworkClient.getInstance().connect(address, port);
 */
public class TurbineNetworkClient  {

    private static class Holder { static final TurbineNetworkClient INSTANCE = new TurbineNetworkClient(); }
    public static TurbineNetworkClient getInstance() { return Holder.INSTANCE;  }

    private Logger logger = Logger.getLogger(this.getClass());


    @Getter @Setter
    /**
     * When you connect, the server reply back with sessionId
     */
    private String sessionId;


    private Thread thClientSender;
    private Thread thClientReceiver;


    private ClientHandlerSender clientHandlerSender;
    private ClientHandlerReceiver clientHandlerReceiver;


    /**
     * Connect to turbine network server
     * @param address of server
     * @param port of server, default is NetworkConsts.SERVER_PORT
     * @throws Exception
     */
    public void start(String address, int port) throws Exception
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

    /**
     * Send new message to server
     * @param queueName
     * @param message
     * @throws Exception
     */
    public void enqueueMessage(String queueName, Message message) throws Exception {


        if (clientHandlerSender != null)
        {
             message.setQueueName(queueName);
             clientHandlerSender.enqueue(message);
        }
    }

    /**
     * When client receive sessionId from server, starts messages queue
     */
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
