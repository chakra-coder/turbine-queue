#Turbine queue
---
##### Table of Contents  
1. [FAQs](#faqs)  
2. [License](#license)  
3. [Dependencies](#deps)  
4. [Installation](#installation)  
5. [Example](#example)



![alt text](http://www.auto-on.it/images/stories/turbina%20garrett%20testata.jpg "Turbine queue")

## FAQs 
<a name="faqs"/>

**What is turbineQueue?**
Turbine queue is messaging queue with high performance process

## License 
<a name="license"/>

[GPLv2](http://www.gnu.org/licenses/gpl-2.0.html)

## Dependencies
<a name="deps"/>

Turbine queue uses:

1.   Apache log4j 1.4
2.   Lombok library (for more details - http://www.projectlombok.org)
3.   JUnit for test


## Installation
<a name"installation" />

Open your command line:

```bash
git clone https://github.com/tgiachi/turbine-queue.git
cd turbine-queue/
mvn compile
mvn install
```

Add in your ***pom.xml***
```xml
<dependency>
  <groupId>org.github.tgiachi</groupId>
  <artifactId>turbinequeue</artifactId>
  <version>1.0-FINAL</version>
  <packaging>jar</packaging>
</dependency>
```

## Example
<a name"example" />

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
```

