package me.rami.config;


import com.amazon.sqs.javamessaging.AmazonSQSMessagingClientWrapper;
import com.amazon.sqs.javamessaging.SQSConnection;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.internal.StaticCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.FileInputStream;
import java.util.List;
import java.util.Properties;

import javax.jms.JMSException;

import org.springframework.context.annotation.Bean;

import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Configuration
@EnableJms
public class SqsConfig {
	SQSConnectionFactory connectionFactory;
	SQSConnection connection;
	public SqsConfig(@Value("${aws.access-key}") String awsAccessKey,
            @Value("${aws.secret-key}") String awsSecretKey) throws JMSException{
			this.connectionFactory =
			 SQSConnectionFactory.builder()
			 .withRegion(Region.getRegion(Regions.EU_CENTRAL_1))
			 .withAWSCredentialsProvider(new StaticCredentialsProvider(
                     new BasicAWSCredentials(awsAccessKey, awsSecretKey)
             ))
			 .build();
			// Create the connection.
			this.connection = connectionFactory.createConnection();
			AmazonSQSMessagingClientWrapper client = connection.getWrappedAmazonSQSClient();
			// Create an SQS queue named 'TestQueue' – if it does not already exist.
			if (!client.queueExists("ramiQa")) {
				System.out.println("client ramiQa inexistant");
			 client.createQueue("ramiQa");
			}
			if (!client.queueExists("ramiQ")) {
				System.out.println("cleint ramiQ inexistant");
				 client.createQueue("ramiQ");
				}

	}
	public SQSConnection getConnetion(){
		return connection;
	}
	 @Bean
	    public JmsTemplate jmsTemplate() {
	    	
	        return new JmsTemplate(connectionFactory);
	    }

}
