package it.sistemisnc.turbine.utils;

import it.sistemisnc.turbine.TurbineQueue;
import it.sistemisnc.turbine.data.Message;
import it.sistemisnc.turbine.threads.CallableMessageListener;

import java.io.Serializable;
import java.util.concurrent.*;

/**
 * Class for send messages in async
 */
public class AsyncListener {



    public static Message sendAndReceiveAsync(final String queueName, final Message message)
    {
        try
        {
             ExecutorService executor = Executors.newFixedThreadPool(1);

            FutureTask<Message> futureTask = new FutureTask<Message>(new CallableMessageListener(queueName, message));

            executor.submit(futureTask);

            Message outMessage = futureTask.get(10, TimeUnit.SECONDS);


            executor.shutdown();


            return outMessage;

        }
        catch (Exception ex)
        {

        }
        return null;


    }

}
