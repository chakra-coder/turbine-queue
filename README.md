#Turbine queue

## FAQS

1. Q: **What is turbineQueue?**
2. R: Turbine queue is messaging queue with high performance process
3. Q: **Is opensource?**
4. R: Yes, off course



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





