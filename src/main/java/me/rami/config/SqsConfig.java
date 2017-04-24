package me.rami.config;


import com.amazon.sqs.javamessaging.AmazonSQSMessagingClientWrapper;
import com.amazon.sqs.javamessaging.SQSConnection;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.internal.StaticCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cognitoidentity.model.Credentials;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClient;
import com.amazonaws.services.securitytoken.model.AssumeRoleRequest;
import com.amazonaws.services.securitytoken.model.AssumeRoleResult;
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
	 BasicSessionCredentials tcredentials = null;
	com.amazonaws.services.securitytoken.model.Credentials credentials;
	public SqsConfig() throws JMSException{
		
		
		AWSSecurityTokenServiceClient sts = new AWSSecurityTokenServiceClient();
		
	    int credsDuration = 3600;
	    AWSCredentials credentials = null;
        try {
            credentials = new ProfileCredentialsProvider().getCredentials();
        } catch (Exception e) {
            throw new AmazonClientException(
                    "Cannot load the credentials from the credential profiles file. " +
                    "Please make sure that your credentials file is at the correct " +
                    "location (~/.aws/credentials), and is in valid format.",
                    e);
        }

        AWSSecurityTokenServiceClient stsClient = new  AWSSecurityTokenServiceClient(credentials);
        AssumeRoleRequest assumeRequest = new AssumeRoleRequest()
                .withRoleArn("")
                .withDurationSeconds(3600)
                .withRoleSessionName("bla bla");

        AssumeRoleResult assumeResult = stsClient.assumeRole(assumeRequest);

	   
        BasicSessionCredentials temporaryCredentials =
                new BasicSessionCredentials(
                            assumeResult.getCredentials().getAccessKeyId(),
                            assumeResult.getCredentials().getSecretAccessKey(),
                            assumeResult.getCredentials().getSessionToken());

		
		
		
		
			this.connectionFactory =
			 SQSConnectionFactory.builder()
			 .withRegion(Region.getRegion(Regions.EU_CENTRAL_1))
			 .withAWSCredentialsProvider(new StaticCredentialsProvider(
					 temporaryCredentials
             ))
			 .build();
			// Create the connection.
			this.connection = connectionFactory.createConnection();
			AmazonSQSMessagingClientWrapper client = connection.getWrappedAmazonSQSClient();
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
