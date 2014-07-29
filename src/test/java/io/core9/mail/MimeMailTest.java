package io.core9.mail;

import static org.junit.Assert.fail;
import io.core9.core.PluginRegistryImpl;
import io.core9.core.boot.BootstrapFramework;

import javax.mail.Message;
import javax.mail.internet.InternetAddress;

import org.junit.Before;
import org.junit.Test;

public class MimeMailTest {
	
	MailerProfile profile;
	MailerPlugin mailer;
	
	private void setupMailProfile() {
		profile = new MailerProfile();
		profile.setHost("smtp.gmail.com");
		profile.setPort(456);
		profile.setAuthentication(true);
		profile.setUsername("core9.mail.api@gmail.com");
		profile.setPassword(System.getenv("CORE9_MAIL_PWD"));
	}
	
	private void setupModules() {
		BootstrapFramework.run();
		mailer = (MailerPlugin) PluginRegistryImpl
									.getInstance()
									.getPlugin(MailerPluginImpl.class);
	}
	
	@Before
	public void setup() {
		setupMailProfile();
		setupModules();
	}
	
	@Test
	public void testSendMail() {
		try {
			Message message = mailer.create(profile);
			message.setSubject("Test");
			message.setFrom(new InternetAddress("core9.mail.api@gmail.com"));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse("core9.mail.api@gmail.com"));
			message.setText("Just a random test message");
			mailer.send(profile, message);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
}
