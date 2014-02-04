package it.sistemisnc.turbine.threads;

import it.sistemisnc.turbine.TurbineQueue;
import it.sistemisnc.turbine.data.Message;
import it.sistemisnc.turbine.listeners.IQueueListener;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * Created by squid on 04/02/14.
 */
public class MessagesDebugger implements IQueueListener {

    private Logger logger = Logger.getLogger(this.getClass()) ;

    private String queueName;



    public MessagesDebugger(String queueName)
    {
        this.queueName = queueName;
        TurbineQueue.getInstance().addQueueListener(queueName, this);

        log(Level.INFO, "[%s] Message debugger is active on queue ", queueName);

    }



    @Override
    public void onMessage(String queueName, Message message) {

        log(Level.INFO, "Incoming message from class %s  ==> %s", message.getSenderClass(), message );

    }

    @Override
    public void onReply(String queueName, Message message) {

    }

    @Override
    public Message onDirectMessage(String queueName, Message message) {
        return null;
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
