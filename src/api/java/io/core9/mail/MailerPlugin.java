package io.core9.mail;

import java.util.List;

import javax.mail.Message;
import javax.mail.MessagingException;

import io.core9.core.plugin.Core9Plugin;
import io.core9.plugin.server.VirtualHost;

public interface MailerPlugin extends Core9Plugin {
	
	List<MailerProfile> getMailerProfiles(VirtualHost vhost);
	
	MailerProfile getProfile(VirtualHost vhost, String id);
	
	Message create(MailerProfile profile);

	MailerPlugin send(MailerProfile profile, Message mail) throws MessagingException;
	
}
