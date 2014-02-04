package it.sistemisnc.turbine.threads;


import it.sistemisnc.turbine.TurbineQueue;
import it.sistemisnc.turbine.data.Message;
import it.sistemisnc.turbine.listeners.IQueueListener;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Queue;

/**
 * Created by squid on 03/02/14.
 */
public class ThreadProcess implements Runnable {


    private Queue<Message> watchQueue;
    private String queueName;

    private TurbineQueue turbineQueue;



    private boolean started = false;


    private Logger logger = Logger.getLogger(this.getClass()) ;



    public ThreadProcess(Queue<Message> queue, String queueName,TurbineQueue turbineQueue )
    {
        this.watchQueue = queue;
        this.queueName = queueName;

        this.turbineQueue = turbineQueue;

        log(Level.INFO, "Starting queue thread for queue %s, waiting for messages...", queueName);

        started = true;


    }


    @Override
    public void run()
    {

        while(started)
        {
            try
            {

                Message message = watchQueue.poll();

                if (message != null)
                {

                   // log(Level.INFO, "Received message %s", message);
                    if (message.isReplyToSender())
                        processMessageWithReply(message);
                    else
                    if (message.getMessageFlow() == Message.MessageFlow.OUTPUT)
                        processReplyMessage(message);
                    else
                        processBroadcastMessage(message);


                }

            }
            catch (Exception ex)
            {
                logException(ex, "Error during process message...");
            }

            try
            {
                //Sleep for CPU overload
                Thread.sleep(10);
            }
            catch (Exception ex)
            {

            }

        }


    }

    private void processMessageWithReply(Message message)
    {
        for (IQueueListener listener : getListeners())
        {
            if (listener.getClass().getName().equals(message.getTargetClass()))
            {
                Message replyMessage = listener.onDirectMessage(queueName, message);

                replyMessage.setMessageFlow(Message.MessageFlow.OUTPUT);
                replyMessage.setReplyToSender(false);
                replyMessage.setTargetClass(message.getSenderClass());
                replyMessage.setSenderClass(message.getTargetClass());

                watchQueue.add(replyMessage);

            }
        }
    }

    private void processBroadcastMessage(Message message)
    {
        for (IQueueListener listener: getListeners())
        {
            listener.onMessage(queueName, message);

        }

    }

    private void processReplyMessage(Message message)
    {
        if (message.getTargetClass() != null || (!message.getTargetClass().isEmpty()))
        {

            for (IQueueListener listener : getListeners())
            {
                if (listener.getClass().getName().equals(message.getTargetClass()))
                    listener.onReply(queueName, message);

            }
        }
        else
        {
            log(Level.FATAL, "Can't delivery message id %s (%s) because the message don't have targetClass property!", message.getGuid(), message.getClass().getName());
        }

    }

    public void stop()
    {
        this.started = false;
    }


    private List<IQueueListener> getListeners()
    {
        return turbineQueue.getListenersByQueue(queueName);

    }

    protected void log(Level level, String text, Object ... args)
    {
        logger.log(level, String.format(text, args));

    }

    protected void logException(Exception ex, String text, Object ... args)
    {
        logger.log(Level.FATAL, String.format("Exception encurred\r\n %s ==> %s",String.format(text, args), ex.getMessage()));
        ex.printStackTrace();
    }

}
