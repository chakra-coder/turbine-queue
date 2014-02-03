package it.sistemisnc.turbine;

import com.google.common.base.Strings;
import it.sistemisnc.turbine.comparator.PriorityComparator;
import it.sistemisnc.turbine.data.Message;
import it.sistemisnc.turbine.listeners.IQueueListener;
import it.sistemisnc.turbine.threads.ThreadProcess;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Created by squid on 03/02/14.
 */
public class TurbineQueue  {

    private static class Holder { static final TurbineQueue INSTANCE = new TurbineQueue(); }
    public static TurbineQueue getInstance() { return Holder.INSTANCE;  }


    private Logger logger = Logger.getLogger(this.getClass());

    private HashMap<String, Queue<Message>> queues = new HashMap<String, Queue<Message>>();

    private HashMap<String, List<IQueueListener>> listeners = new HashMap<String, List<IQueueListener>>();


    private HashMap<String, ExecutorService> queueExecutors = new HashMap<String, ExecutorService>();



    public boolean addQueue(String queueName)
    {
        return addQueue(queueName, 5);

    }

    public boolean addQueue(String queueName, int poolSize)
    {
        queueName = queueName.toUpperCase();
        try
        {

            if (queues.get(queueName) == null)
            {

                queues.put(queueName, new PriorityBlockingQueue<Message>(10, new PriorityComparator()));
                log(Level.INFO, "Queue %s created", queueName);


                log(Level.INFO, "Inizializing thread Pool with %s threads", poolSize);
                queueExecutors.put(queueName, Executors.newFixedThreadPool(poolSize));
                initQueueThreads(queueName, poolSize);

            }
            else
            {
                log(Level.WARN, "Queue %s already exits...", queueName);
                return false;
            }

        }
        catch (Exception ex)
        {
            logException(ex, "Error during initializing queue name %s", queueName);
        }

        return  false;
    }

    public List<IQueueListener> getListenersByQueue(String queueName)
    {
        return listeners.get(queueName.toUpperCase());

    }


    private void initQueueThreads(String queueName, int poolSize)
    {
        try
        {
            for (int i=0; i<poolSize; i++)
            {
                queueExecutors.get(queueName).submit(new ThreadProcess(queues.get(queueName), queueName, this));
            }

        }
        catch (Exception ex)
        {
            logException(ex, "Error during initializing queue threads!");
        }

    }

    public boolean addQueueListener(String queueName, IQueueListener listener)
    {
        queueName = queueName.toUpperCase();

        if (listeners.get(queueName) == null)
        {
            listeners.put(queueName, new LinkedList<IQueueListener>());
        }

        listeners.get(queueName).add(listener);
        log(Level.TRACE, "Added listeners %s to queue %s", listener.getClass().getName(), queueName );

        return true;

    }

    public synchronized boolean sendMessage(String queueName, Message message)
    {
        queueName = queueName.toUpperCase();
        try
        {

            if (queues.get(queueName) != null)
            {
                if (Strings.isNullOrEmpty(message.getGuid()))
                    message.setGuid(UUID.randomUUID().toString());


                queues.get(queueName).add(message);
            }
            else
            {
                throw new Exception("Queue " + queueName +" don't exists, can't add message!");
            }

        }
        catch (Exception ex)
        {
            logException(ex, "Message %s cannot put in queue", message.getClass());
        }

        return  false;


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
