package io.core9.mail;

import io.core9.plugin.database.repository.CrudRepository;
import io.core9.plugin.database.repository.NoCollectionNamePresentException;
import io.core9.plugin.database.repository.RepositoryFactory;
import io.core9.plugin.server.VirtualHost;

import java.util.List;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;

import net.xeoh.plugins.base.annotations.PluginImplementation;
import net.xeoh.plugins.base.annotations.events.PluginLoaded;

@PluginImplementation
public class MailerPluginImpl implements MailerPlugin {
	
	private CrudRepository<MailerProfile> profiles;
	
	@PluginLoaded
	public void onRepositoryFactory(RepositoryFactory factory) throws NoCollectionNamePresentException {
		profiles = factory.getRepository(MailerProfile.class);
	}

	@Override
	public MailerPlugin send(MailerProfile profile, Message mail) throws MessagingException {
		Transport.send(mail);
		return this;
	}

	@Override
	public Message create(MailerProfile profile) {
		Session session = Session.getInstance(MailerProfile.parseProperties(profile), 
			new javax.mail.Authenticator() {
				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(profile.getUsername(), profile.getPassword());
				}
			}
		);
		return new MimeMessage(session);
	}

	@Override
	public List<MailerProfile> getMailerProfiles(VirtualHost vhost) {
		return profiles.getAll(vhost);
	}

	@Override
	public MailerProfile getProfile(VirtualHost vhost, String id) {
		return profiles.read(vhost, id);
	}

}
