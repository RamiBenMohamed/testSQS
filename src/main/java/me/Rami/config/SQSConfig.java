package me.Rami.config;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClient;
import com.amazonaws.services.securitytoken.model.AssumeRoleRequest;
import com.amazonaws.services.securitytoken.model.AssumeRoleResult;
import com.amazonaws.services.sqs.AmazonSQSClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class SQSConfig {



public BasicSessionCredentials getCredentials(){
		AmazonSQSClient sqs;
		 
		 AWSCredentials credentials;
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

	}

 @Bean
 public AmazonSQSClient createSQSClient() {
	 
	 AmazonSQSClient sqs;	
		sqs = new AmazonSQSClient(getCredentials());
		sqs.setRegion(Region.getRegion(Regions.EU_CENTRAL_1));
		sqs.createQueue("QueueR");

 return sqs;
 }
}
