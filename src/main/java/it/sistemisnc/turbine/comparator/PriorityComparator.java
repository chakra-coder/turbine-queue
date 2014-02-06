package it.sistemisnc.turbine.comparator;

import it.sistemisnc.turbine.data.Message;

import java.util.Comparator;

/**
 * Comparator for messages priorities
 */
public class PriorityComparator implements Comparator<Message> {


    @Override
    public int compare(Message o1, Message o2) {
        return (int) (o1.getPriority() - o2.getPriority());
    }



}
