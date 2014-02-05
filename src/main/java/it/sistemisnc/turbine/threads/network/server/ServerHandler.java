package it.sistemisnc.turbine.threads.network.server;

import com.google.gson.Gson;
import it.sistemisnc.turbine.data.Message;
import it.sistemisnc.turbine.network.TurbineNetworkServer;
import it.sistemisnc.turbine.utils.CompressUtil;
import it.sistemisnc.turbine.utils.NetworkConsts;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

/**
 * Created by squid on 05/02/14.
 */
public class ServerHandler implements Runnable {
    private static Logger logger = Logger.getLogger(ServerHandler.class);

    private Socket socket;

    private boolean started = false;
    private Gson gson;
    private DataOutputStream dataOutput;







    public ServerHandler(Socket socket)
    {
        this.socket = socket;

        this.gson = new Gson();
        this.started = true;

        try
        {
             this.dataOutput = new DataOutputStream(socket.getOutputStream());
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
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

                        int length = inFromClient.readInt();                    // read length of incoming message
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



    public void sendMessage(Message message)
    {
       try
       {
           message.setRemoteMessage(true);
           dataOutput.write(NetworkConsts.covertMessageToByteArray(message));
       }
       catch (Exception ex)
       {
          ex.printStackTrace();
       }
    }


    public void handleBuffer(byte[] byteBuffer) throws Exception
    {
        String json = new String(CompressUtil.decompress(byteBuffer));
        Message message = gson.fromJson(json, Message.class);

        //log(Level.INFO, "Uid: %s - Flow: %s Data: %s", message.getGuid(), message.getMessageFlow(), message.getData() );


        TurbineNetworkServer.getInstance().broadcastMessage(true, message);


        if (!message.getRemoteMessageInfo().getTargetUid().isEmpty())
        {
            TurbineNetworkServer.getInstance().sendMessageToId(message.getRemoteMessageInfo().getTargetUid(), message);
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
