package me.rami.services;

import com.amazonaws.services.elasticbeanstalk.model.Queue;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.rami.Application;
import me.rami.component.TaskProcessor;
import me.rami.config.SqsConfig;
import me.rami.model.Event;
import me.rami.model.Task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.jms.JmsException;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;


@Component
@Slf4j
public class TaskServices implements MessageListener {
	private static final Logger log = LoggerFactory.getLogger(TaskServices.class);
   
    private final ObjectMapper mapper;
    SqsConfig sqsConfig;

    @Autowired
    public TaskServices(SqsConfig sqsConfig, ObjectMapper mapper) {
        this.sqsConfig= sqsConfig;
        this.mapper = mapper;
    }
    
    

    private void onMessage(Event event) {
        log.info("Got an event: {}", event);
    }

    @Async
    @SneakyThrows
    public void start(Task task) throws  JsonProcessingException {
    	Session session = sqsConfig.getConnetion().createSession(false, Session.AUTO_ACKNOWLEDGE);
    	Queue queue = (Queue) session.createQueue("ramiQa");
    	// Create a producer for the 'TestQueue'.
    	MessageProducer producer = session.createProducer((Destination) queue);
    	TextMessage message = session.createTextMessage(mapper.writeValueAsString(task));
    	// Send the message.
    	producer.send(message);
    	System.out.println("JMS Message " + message.getJMSMessageID());
      
       
    }
    
	
	@Async
	public void onMessage(javax.jms.Message message) {
		// TODO Auto-generated method stub
		 log.debug("Got a message <{}>", message);
		 Session session;
		 Queue queue = (Queue) session.createQueue("ramiQ");
		 MessageConsumer consumer;
		 try {
			 // Cast the received message as TextMessage and print the text to screen.
			 if (message != null) {
			 System.out.println("Received: " + ((TextMessage) message).getText());
			  consumer = session.createConsumer((Destination) queue);
			// Instantiate and set the message listener for the consumer.
			consumer.setMessageListener(new TaskProcessor(sqsConfig, mapper));
			// Start receiving incoming messages.
			sqsConfig.getConnetion().start();
			// Wait for 1 second. The listener onMessage() method will be invoked when a message is
			
			Thread.sleep(1000);

			 }
		 }
		 catch (JMSException e) {
			 e.printStackTrace();
		}
			 
		
	}

}
