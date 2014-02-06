package it.sistemisnc.turbine.threads.network.client;

import com.google.gson.Gson;
import it.sistemisnc.turbine.TurbineQueue;
import it.sistemisnc.turbine.data.Message;

import it.sistemisnc.turbine.network.TurbineNetworkClient;
import it.sistemisnc.turbine.utils.CompressUtil;
import it.sistemisnc.turbine.utils.MessagesIds;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.DataInputStream;
import java.net.Socket;

/**
 * Thread runner for receive/process incoming messages
 */
public class ClientHandlerReceiver implements Runnable {

    private static Logger logger = Logger.getLogger(ClientHandlerSender.class);

    private Gson gson;

    private Socket socket;
    private boolean started = false;


    public ClientHandlerReceiver(Socket socket)
    {
        this.socket = socket;
        this.gson = new Gson();
        this.started = true;
    }

    @Override
    public void run() {
        while(started)
        {
            try
            {
                DataInputStream inFromClient = new DataInputStream(socket.getInputStream());

                try
                {
                    while(true)
                    {
                        int length = inFromClient.readInt();
                         // read length of incoming message
                        if(length>0)
                        {
                            byte[] message = new byte[length];
                            inFromClient.readFully(message, 0, message.length); // read the message
                            handleBuffer(message);
                        }
                    }
                }
                catch(Exception ex)
                {
                    ex.printStackTrace();
                }
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }

    private void handleBuffer(byte[] byteBuffer) throws Exception
    {
        String json = new String(CompressUtil.decompress(byteBuffer));
        Message message = gson.fromJson(json, Message.class);


        if (message.getMessageType() == MessagesIds.SESSION_BEGIN)
        {
            TurbineNetworkClient.getInstance().setSessionId((String)message.getExtra().get("sessionId"));
            log(Level.INFO, "SessionId: %s", message.getExtra().get("sessionId") );
            TurbineNetworkClient.getInstance().startClientSender();
        }
        else
        {
            TurbineQueue.getInstance().sendMessage(message.getQueueName(), message);
        }
    }


    protected void log(Level level, String text, Object ... args)
    {
        logger.log(level, String.format("%s - %s",socket.getRemoteSocketAddress() ,String.format(text, args)));

    }

    protected void logException(Exception ex, String text, Object ... args)
    {
        logger.log(Level.FATAL, String.format("Exception encurred\r\n %s ==> %s",String.format(text, args), ex.getMessage()));
    }
}
