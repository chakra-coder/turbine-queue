#Turbine queue

## FAQS

**What is turbineQueue?**
Turbine queue is messaging queue with high performance process

## License

GPL v2

## Dependecies
Turbine queue uses:
1. Apache log4j 1.4
2. Lombok library (for more details - http://www.projectlombok.org)


## Example

```java
//Initilize new queue
TurbineQueue turbineQueue = TurbineQueue.getInstance();

//Create new queue called "testqueue"
turbineQueue.addQueue("testqueue");

//Add class (implements IQueueListener) to Listeners
turbineQueue.addQueueListener("testqueue", this);

//Send test message
turbineQueue.sendMessage("testqueue", new Message(UUID.randomUUID().toString()));

[...]

@Override
public void onMessage(String queueName, Message message) {
    System.out.println("Message received from queueName  " + queueName + " type = " + message.getClass().getName() + " uid " + message.getGuid());
}

@Override
public void onReply(String queueName, Message message) {
    System.out.println("Message's reply " + message.getData());

}

@Override
public Message onDirectMessage(String queueName, Message message) {
   System.out.println("Receiving message with reply request from " + message.getSenderClass() );
   message.setData("Hello, I love, Won't you tell me your name?");

   return message;
}



