package me.rami.services;

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
import org.springframework.jms.JmsException;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
@Service
@Slf4j
public class TaskServices {
	private static final Logger log = LoggerFactory.getLogger(TaskServices.class);
   
    private final ObjectMapper mapper;
    SqsConfig sqsConfig;

    @Autowired
    public TaskServices(SqsConfig sqsConfig, ObjectMapper mapper) {
        this.sqsConfig= sqsConfig.getInstance();
        this.mapper = mapper;
    }
    
    
    public void onMessage(String message) throws JMSException {
        log.debug("Got a message <{}>", message);
        try {
            Event event = mapper.readValue(message, Event.class);
            onMessage(event);
        } catch (Exception ex) {
            log.error("Encountered error while parsing message.",ex);
            throw new JMSException("Encountered error while parsing message.");
        }
    }

    private void onMessage(Event event) {
        log.info("Got an event: {}", event);
    }

    @Async
    @SneakyThrows
    public void start(Task task) throws  JsonProcessingException {
       sqsConfig.sendMessageToQueue(sqsConfig.getQueueUrl(sqsConfig.getQueueName()), mapper.writeValueAsString(task));
       
    }

}
