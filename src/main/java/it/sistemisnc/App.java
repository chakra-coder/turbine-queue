package it.sistemisnc;

import it.sistemisnc.turbine.TurbineQueue;
import it.sistemisnc.turbine.data.Message;
import it.sistemisnc.turbine.listeners.IQueueListener;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

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
        System.out.println( "Hello World!" );

        TurbineQueue turbineQueue = TurbineQueue.getInstance();

        turbineQueue.addQueue("testqueue");
        turbineQueue.addQueueListener("testqueue", this);


        for (int i=0; i<100;i++)
        {
                turbineQueue.sendMessage("testqueue", new Message(UUID.randomUUID().toString()));


        }

        Message message = new Message(UUID.randomUUID().toString());
        message.setSenderClass(this.getClass().getName());
        message.setTargetClass(this.getClass().getName());
        message.setReplyToSender(true);
        message.setPriority(1);


        turbineQueue.sendMessage("testqueue", message);



    }

    @Override
    public void onMessage(String queueName, Message message) {
        System.out.println("Message received from queueName  " + queueName + " type = " + message.getClass().getName() + " uid " + message.getGuid());
    }

    @Override
    public void onReply(String queueName, Message message) {

        System.out.println("Risposta message " + message.getData());

    }

    @Override
    public Message onDirectMessage(String queueName, Message message) {

        System.out.println("Ricevuto messaggio da " + message.getSenderClass() );
        message.setData("CIAO RIPOSTA!");

        return message;
    }
}