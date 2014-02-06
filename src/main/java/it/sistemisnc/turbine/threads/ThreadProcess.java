package it.sistemisnc.turbine.threads;


import it.sistemisnc.turbine.TurbineQueue;
import it.sistemisnc.turbine.annotation.TurbineQueueFilter;
import it.sistemisnc.turbine.data.Message;
import it.sistemisnc.turbine.data.MessageFlowType;
import it.sistemisnc.turbine.data.NetworkMessage;
import it.sistemisnc.turbine.listeners.IQueueListener;
import it.sistemisnc.turbine.network.TurbineNetworkClient;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Queue;

/**
 * Core of messaging processor
 */
public class ThreadProcess implements Runnable {

    private Logger logger = Logger.getLogger(this.getClass()) ;

    private Queue<Message> watchQueue;
    private String queueName;

    private TurbineQueue turbineQueue;



    private boolean started = false;





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


                    if (message.isReplyToSender())
                        processMessageWithReply(message);
                    else
                    if (message.getMessageFlow() == MessageFlowType.OUTPUT)
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

                replyMessage.setMessageFlow(MessageFlowType.OUTPUT);
                replyMessage.setReplyToSender(false);
                replyMessage.setTargetClass(message.getSenderClass());
                replyMessage.setSenderClass(message.getTargetClass());

                if (replyMessage.isRemoteMessage())
                {
                    NetworkMessage networkMessage = new NetworkMessage();
                    networkMessage.setTargetUid(replyMessage.getRemoteMessageInfo().getSenderUid());

                    try
                    {
                        TurbineNetworkClient.getInstance().enqueueMessage(message.getQueueName(), message);

                    }
                    catch (Exception ex)
                    {

                    }
                }
                else
                {
                     watchQueue.add(replyMessage);
                }

            }
        }
    }

    private void processBroadcastMessage(Message message)
    {
        for (IQueueListener listener: getListeners())
        {

            if (listener.getClass().isAnnotationPresent(TurbineQueueFilter.class) == true)
            {
                 TurbineQueueFilter annotation = listener.getClass().getAnnotation(TurbineQueueFilter.class);

                if (isMessageTypePresent(annotation, message))
                    listener.onMessage(queueName, message);
            }
            else
            {
                listener.onMessage(queueName, message);
            }


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

    private boolean isMessageTypePresent(TurbineQueueFilter annotation, Message message)
    {
        for (int i=0;i<annotation.filters().length;i++)
        {
            if (annotation.filters()[i] == message.getMessageType())
                return  true;
        }

        return  false;

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
