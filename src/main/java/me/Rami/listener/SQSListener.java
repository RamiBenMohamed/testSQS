package me.Rami.listener;
import org.slf4j.Logger;
import com.amazon.sqs.javamessaging.message.SQSTextMessage;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import javax.jms.JMSException;
import javax.jms.Message;
import org.springframework.jms.core.*;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.jms.Session;
import org.springframework.beans.factory.annotation.Autowired;
@Component
public class SQSListener implements MessageListener {
	@Autowired
	private JmsTemplate jmsTemplate;
 private static final Logger LOGGER = LoggerFactory.getLogger(SQSListener.class);


 private String getMessageText(SQSTextMessage message) throws Exception {
        try {
            return message.getText();
        } catch (JMSException e) {
            throw new Exception("Failed to get message text from message", e);
        }
    }


@Override 
public void onMessage(Message arg0) {
	String messageText = null;
	try {
		messageText = getMessageText((SQSTextMessage) arg0);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
        LOGGER.info("Message SQS reçu : {}", messageText);
		 
		 
	 }
}
