package me.Rami.config;

import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.*;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.internal.StaticCredentialsProvider;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClient;
import com.amazonaws.services.securitytoken.model.AssumeRoleRequest;
import com.amazonaws.services.securitytoken.model.AssumeRoleResult;

import me.Rami.listener.SQSListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

@Configuration
public class JMSSQSConfig {
	
	private String endpoint="http://localhost:9324";
	
	private String queueName="QueueR";
	@Autowired
	private SQSListener sqsListener;
	
	@Bean
	public DefaultMessageListenerContainer jmsListenerContainer() {
		SQSConnectionFactory sqsConnectionFactory = SQSConnectionFactory.builder()
				.withAWSCredentialsProvider(new DefaultAWSCredentialsProviderChain())
				.withEndpoint(endpoint)
				.withAWSCredentialsProvider(new StaticCredentialsProvider(
						 getCredentials()))
				.withNumberOfMessagesToPrefetch(10).build();
		DefaultMessageListenerContainer dmlc = new DefaultMessageListenerContainer();
		dmlc.setConnectionFactory(sqsConnectionFactory);
		dmlc.setDestinationName(queueName);
		dmlc.setMessageListener(sqsListener);
		return dmlc;
	}
	@Bean
 	public JmsTemplate createJMSTemplate() {
	 SQSConnectionFactory sqsConnectionFactory = SQSConnectionFactory.builder()
			 .withAWSCredentialsProvider(new StaticCredentialsProvider(
					 getCredentials()
		             ))
			 .withEndpoint(endpoint)
			 .withNumberOfMessagesToPrefetch(10).build();
	JmsTemplate jmsTemplate = new JmsTemplate(sqsConnectionFactory);
	jmsTemplate.setDefaultDestinationName(queueName);
	jmsTemplate.setDeliveryPersistent(false);
	return jmsTemplate;
	}
		@Bean
		public AWSCredentials getCredentials() {
			
			
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
		            .withRoleArn("arn:aws:iam::347970623729:role/dae_from_support")
		            .withDurationSeconds(3600)
		            .withRoleSessionName("blabla");

		    AssumeRoleResult assumeResult = stsClient.assumeRole(assumeRequest);
		    BasicSessionCredentials temporaryCredentials =
		            new BasicSessionCredentials(
		                        assumeResult.getCredentials().getAccessKeyId(),
		                        assumeResult.getCredentials().getSecretAccessKey(),
		                        assumeResult.getCredentials().getSessionToken());
		    return temporaryCredentials;
		    
		    
		
		
	};
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}