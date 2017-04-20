package me.rami.component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.rami.services.AWSSimpleQueueServiceUtil;
import me.rami.Application;
import me.rami.config.SqsConfig;
import me.rami.model.Event;
import me.rami.model.Task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.JmsException;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import java.io.IOException;
import java.util.Random;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
@Component
@Slf4j
public class TaskProcessor  {
	private static final Logger log = LoggerFactory.getLogger(TaskProcessor.class);
	
	SqsConfig sqsConfig;
    
    private final ObjectMapper mapper;

    private Random random = new Random();

    @Autowired
    public TaskProcessor(SqsConfig sqsConfig, ObjectMapper mapper) {
    	this.sqsConfig=sqsConfig.getInstance();
        this.mapper = mapper;
    }

    @Async
  
    public void onMessage(String message) throws JMSException, JmsException, InterruptedException {
        log.debug("Got a message <{}>", message);
        try {
            Task task = mapper.readValue(message, Task.class);
            onMessage(task);
        } catch (IOException ex) {
            log.error("Encountered error while parsing message.", ex);
            throw new JMSException("Encountered error while parsing message.");
        }
    }

    @SneakyThrows
    private void onMessage(Task task) throws  JsonProcessingException, InterruptedException {
        log.info("Got a task: {}", task);

        // task started notification
        sendEvent(new Event(task.getId(), task.getType(), Event.Status.STARTED));

        // emulate task processing
        Thread.sleep(10000);

        // task finished notification
        sendEvent(new Event(
                task.getId(),
                task.getType(),
                random.nextInt(2) == 1 ? Event.Status.SUCCESSFUL : Event.Status.FAILED
        ));
    }

    @SneakyThrows
    private void sendEvent(Event event) throws JmsException, JsonProcessingException {
    	 sqsConfig.sendMessageToQueue(sqsConfig.getQueueProcessorName(), mapper.writeValueAsString(event));
          }
 

}