package it.sistemisnc.turbine.threads.network.client;

import com.google.gson.Gson;
import it.sistemisnc.turbine.data.Message;
import it.sistemisnc.turbine.utils.NetworkConsts;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by squid on 05/02/14.
 */
public class ClientHandlerSender implements Runnable {

    private static Logger logger = Logger.getLogger(ClientHandlerSender.class);

    private volatile  boolean started = false;
    private Socket socket;

    private DataOutputStream outBuffer;


    private Queue<Message> messagesToSend = new LinkedBlockingQueue<Message>();
    private Gson gson;



    public ClientHandlerSender(Socket socket)
    {
        this.socket = socket;

    }



    @Override
    public void run()
    {

        try
        {

            gson = new Gson();
            outBuffer = new DataOutputStream(socket.getOutputStream());


        }
        catch (Exception ex)
        {
             ex.printStackTrace();
        }



        while (true)
        {


                while (started)
                {
                        Message message = messagesToSend.poll();

                        if (message != null)
                        {

                            try
                            {
                                 outBuffer.write(NetworkConsts.covertMessageToByteArray(message));
                            }
                            catch (Exception ex)
                            {
                                ex.printStackTrace();
                            }

                        }

                        try
                        {
                            Thread.sleep(10);
                        }
                        catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                 }
        }



    }


    public void enqueue(Message message)
    {
        message.setRemoteMessage(true);
        messagesToSend.add(message);
    }

    public void start()
    {
        started = true;
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
