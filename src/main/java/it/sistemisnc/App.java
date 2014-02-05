package it.sistemisnc;

import it.sistemisnc.turbine.TurbineQueue;
import it.sistemisnc.turbine.data.Message;
import it.sistemisnc.turbine.listeners.IQueueListener;
import it.sistemisnc.turbine.network.TurbineNetworkClient;
import it.sistemisnc.turbine.network.TurbineNetworkServer;
import it.sistemisnc.turbine.utils.AsyncListener;
import it.sistemisnc.turbine.utils.NetworkConsts;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import java.util.Random;
import java.util.UUID;

/**
 * Hello world!
 *
 */
public class App implements IQueueListener
{
    public static void main( String[] args )
    {
        //Define log pattern layout

        PatternLayout layout = new PatternLayout("%d{ISO8601} [%t] %-5p %c %x - %m%n");

//Add console appender to root logger

        Logger.getRootLogger().addAppender(new ConsoleAppender(layout));
        new App();



    }


    public App()
    {

        TurbineQueue turbineQueue = TurbineQueue.getInstance();
        turbineQueue.addQueue("testqueue");
        turbineQueue.addQueueListener("testqueue", this);

        try
        {
            TurbineNetworkServer.getInstance().start();

            TurbineNetworkClient.getInstance().start("127.0.0.1", NetworkConsts.SERVER_PORT);




            final long start = System.nanoTime();
            for (int i=0;i<15;i++)
            {
                Message message = new Message(UUID.randomUUID().toString());
                message.setData("Message N. " + i);

                TurbineNetworkClient.getInstance().enqueueMessage("testqueue", message );

                try
                {
                   // Thread.sleep(new Random().nextInt(5000) + 2000);
                }
                catch (Exception ex)
                {

                }

            }

            Message message = new Message(UUID.randomUUID().toString());
            TurbineQueue.getInstance().sendMessage("testqueue", message);


            final long end = System.nanoTime();

            System.out.println("Took: " + ((end - start) / 1000000) + "ms");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }

    @Override
    public void onMessage(String queueName, Message message) {
        System.out.println("Message received from queueName  " + queueName + " type = " + message.getClass().getName() + " uid " + message.getGuid() + " Remote " + message.isRemoteMessage());
    }

    @Override
    public void onReply(String queueName, Message message) {

        System.out.println("reply form message " + message.getData());

    }

    @Override
    public Message onDirectMessage(String queueName, Message message) {

        System.out.println("direct message from " + message.getSenderClass() );
        message.setData("This is reply " + new Random().nextInt());

        return message;
    }
}
