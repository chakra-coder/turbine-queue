package it.sistemisnc.turbine.threads.network.server;

import it.sistemisnc.turbine.data.Message;

import it.sistemisnc.turbine.utils.MessagesIds;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by squid on 05/02/14.
 */
public class TurbineNetworkServerListener implements Runnable {

    private boolean started = false;
    private static Logger logger = Logger.getLogger(TurbineNetworkServerListener.class);


    private ServerSocket serverSocket;
    public static HashMap<String, ServerHandler> connectedClient = new HashMap<String, ServerHandler>();
    private Socket incoming = null;
    private ExecutorService thPool;

    private int listenPort;
    private int maxConnections;

    public TurbineNetworkServerListener(int listenPort, int maxConnections)
    {
        this.started = true;
        this.listenPort = listenPort;
        this.maxConnections = maxConnections;

        log(Level.INFO, "Starting TCP server on port %s", listenPort);
        log(Level.INFO, "Max connections: %s", maxConnections);
        initThreadPool();
    }

    private void initThreadPool()
    {
        thPool = Executors.newFixedThreadPool(maxConnections);
    }

    @Override
    public void run() {

        try
        {
             serverSocket = new ServerSocket(listenPort);

             log(Level.INFO, "TCP Server started...");

        }
        catch (Exception ex)
        {
            logException(ex, "Error during start server on port %s", listenPort);
        }


        while(started)
        {
            try
            {
                incoming = serverSocket.accept();

                if (connectedClient.size() < maxConnections)
                {

                    log(Level.INFO, "Connected client %s",incoming.getRemoteSocketAddress());


                    String uid = UUID.randomUUID().toString();
                    ServerHandler clientThread =  new ServerHandler(incoming);

                    connectedClient.put(uid, clientThread);

                    thPool.submit(clientThread);

                    sendPresentationMessage(uid);
                }
                else
                {
                    log(Level.WARN, "Reject client %s. Too many connections (maxConnections = %s)", incoming.getRemoteSocketAddress(), maxConnections);
                    incoming.close();
                }
            }
            catch (Exception ex)
            {
                logException(ex, "Error during socket listen");
            }
        }

    }


    protected void sendPresentationMessage(String clientID)
    {
        Message message = new Message(UUID.randomUUID().toString());
        message.setMessageType(MessagesIds.SESSION_BEGIN);
        message.getExtra().put("sessionId", clientID);

       sendMessageToClientId(clientID, message);

    }





    public void sendMessageToClientId(String guid, Message message)
    {
        try
        {
            ServerHandler socket = connectedClient.get(guid);

            if (socket != null)
            {
                   socket.sendMessage(message);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }




    public void broadcastMessage(boolean allClients, Message message)
    {


        if (!allClients)
        {
            String senderUid = (String)message.getExtra().get("sessionId");

            for (String uid : connectedClient.keySet())
            {
                if (!uid.equals(senderUid))
                    connectedClient.get(uid).sendMessage(message);
            }
        }
        else
        {
            for (String uid : connectedClient.keySet())
            {

              connectedClient.get(uid).sendMessage(message);
            }
        }

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
