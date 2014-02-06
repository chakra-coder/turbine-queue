package it.sistemisnc.turbine.network;

import it.sistemisnc.turbine.data.Message;
import it.sistemisnc.turbine.threads.network.server.TurbineNetworkServerRunner;
import it.sistemisnc.turbine.utils.NetworkConsts;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * Start turbine server
 */
public class TurbineNetworkServer  {

    private static class Holder { static final TurbineNetworkServer INSTANCE = new TurbineNetworkServer(); }
    public static TurbineNetworkServer getInstance() { return Holder.INSTANCE;  }

    private Thread serverThread;
    private TurbineNetworkServerRunner networkServerListener;



    private Logger logger = Logger.getLogger(this.getClass());


    /**
     * Starts new server with default server port (NetworkConsts.SERVER_PORT) and default max connections
     * (NetworkConsts.SERVER_MAX_CONNECTIONS)
     * @throws Exception
     */
    public void start() throws Exception
    {
       start(NetworkConsts.SERVER_PORT, NetworkConsts.SERVER_MAX_CONNECTIONS);

    }

    /**
     * Starts new server passing server port and maxconnections
     * @param serverPort
     * @param maxConnections
     * @throws Exception
     */
    public void start(int serverPort, int maxConnections) throws Exception
    {
        networkServerListener = new TurbineNetworkServerRunner(serverPort, maxConnections);
        serverThread =  new Thread(networkServerListener);

        serverThread.start();
    }
    /**
     * Broadcast message to all client, If allClients = false the message don't be
     * @param allClients
     * @param message
     */
    public void broadcastMessage(boolean allClients, Message message)
    {
        networkServerListener.broadcastMessage(allClients, message);
    }
    /**
     * Send message to client passing uid
     * @param uid
     * @param message
     */
    public void sendMessageToId(String uid, Message message)
    {
        networkServerListener.sendMessageToClientId(uid, message);
    }

    /**
     * Disconnect client from server passing uid
     * @param uid
     */
    public void disconnectClient(String uid)
    {
        networkServerListener.disconnectClient(uid);
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
