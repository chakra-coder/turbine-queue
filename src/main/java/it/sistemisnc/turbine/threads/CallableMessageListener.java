package it.sistemisnc.turbine.threads;

import it.sistemisnc.turbine.TurbineQueue;
import it.sistemisnc.turbine.data.Message;
import it.sistemisnc.turbine.listeners.IQueueListener;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Callable;

/**
 * Created by squid on 03/02/14.
 */
public class CallableMessageListener implements Callable<Message>, IQueueListener{

    private String targetId;
    private String queueName;

    private Message inMessage;
    private Message message;

    private String expectedId;


    public CallableMessageListener(String queueName, Message message)
    {

        this.queueName = queueName.toUpperCase();
        this.expectedId = UUID.randomUUID().toString();


        message.setGuid(expectedId);

        message.setSenderClass(getClass().getName());
        message.setReplyToSender(true);

        TurbineQueue.getInstance().addQueueListener(this.queueName, this);

        TurbineQueue.getInstance().sendMessage(this.queueName, message);



    }

    @Override
    public Message call() throws Exception {
        while (inMessage == null)
        {
            Thread.sleep(100);
        }


        TurbineQueue.getInstance().removeQueueListener(queueName, this);



        return inMessage;
    }

    @Override
    public Message onDirectMessage(String queueName, Message message) {
       return message;

    }

    @Override
    public void onMessage(String queueName, Message message) {

    }

    @Override
    public void onReply(String queueName, Message message) {
        if (message.getGuid().equals(expectedId))
        {
            this.inMessage = message;
        }

    }
}
