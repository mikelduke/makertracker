package org.tenbitworks.email;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
	private static final String CLAZZ = EmailService.class.getName();
	private static final Logger LOGGER = Logger.getLogger(CLAZZ);

	@Value("${makertracker.email.enabled:false}")
	boolean sendEmailEnabled;
	
	@Autowired
	JavaMailSender emailSender;
	
	@Async("sendEmailExecutor")
	public void sendEmail(SimpleMailMessage message) {
		if (sendEmailEnabled) {
			try {
				emailSender.send(message);
			} catch (MailException me) {
				LOGGER.logp(Level.SEVERE, CLAZZ, "sendEmail", 
						"Error sending email: " + message.getSubject() + " to " + Arrays.toString(message.getTo()), me);
			}
		}
	}
}
