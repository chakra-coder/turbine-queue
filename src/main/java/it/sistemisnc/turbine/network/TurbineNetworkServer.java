package it.sistemisnc.turbine.network;

import it.sistemisnc.turbine.data.Message;
import it.sistemisnc.turbine.threads.network.server.TurbineNetworkServerListener;
import it.sistemisnc.turbine.utils.NetworkConsts;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * Created by squid on 05/02/14.
 */
public class TurbineNetworkServer  {

    private static class Holder { static final TurbineNetworkServer INSTANCE = new TurbineNetworkServer(); }
    public static TurbineNetworkServer getInstance() { return Holder.INSTANCE;  }

    private Thread serverThread;
    private TurbineNetworkServerListener networkServerListener;



    private Logger logger = Logger.getLogger(this.getClass());


    protected void log(Level level, String text, Object ... args)
    {
        logger.log(level, String.format(text, args));

    }

    protected void logException(Exception ex, String text, Object ... args)
    {
        logger.log(Level.FATAL, String.format("Exception encurred\r\n %s ==> %s",String.format(text, args), ex.getMessage()));
    }


    public void start() throws Exception
    {

        networkServerListener = new TurbineNetworkServerListener(NetworkConsts.SERVER_PORT, NetworkConsts.SERVER_MAX_CONNECTIONS);
        serverThread =  new Thread(networkServerListener);

        serverThread.start();
    }


    public void broadcastMessage(boolean allClients, Message message)
    {
        networkServerListener.broadcastMessage(allClients, message);
    }



}
