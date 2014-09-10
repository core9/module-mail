package io.core9.mail.widget;

import io.core9.mail.MailerPlugin;
import io.core9.mail.MailerProfile;
import io.core9.plugin.server.request.Request;
import io.core9.plugin.template.closure.ClosureTemplateEngine;
import io.core9.plugin.widgets.datahandler.DataHandler;
import io.core9.plugin.widgets.datahandler.DataHandlerFactoryConfig;
import io.core9.plugin.widgets.datahandler.factories.CustomVariable;

import java.util.HashMap;
import java.util.Map;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import net.xeoh.plugins.base.annotations.PluginImplementation;
import net.xeoh.plugins.base.annotations.injections.InjectPlugin;

@PluginImplementation
public class MailDataHandlerImpl implements MailDataHandler<MailDataHandlerConfig> {
	
	@InjectPlugin
	private MailerPlugin mailer;
	
	@InjectPlugin
	private ClosureTemplateEngine engine;

	@Override
	public String getName() {
		return "Mail";
	}

	@Override
	public Class<? extends DataHandlerFactoryConfig> getConfigClass() {
		return MailDataHandlerConfig.class;
	}

	@Override
	public DataHandler<MailDataHandlerConfig> createDataHandler(DataHandlerFactoryConfig options) {
		MailDataHandlerConfig config = (MailDataHandlerConfig) options;
		return new DataHandler<MailDataHandlerConfig>() {

			@Override
			public Map<String, Object> handle(Request req) {
				Map<String, Object> result = new HashMap<String, Object>();
				switch(req.getMethod()) {
				case POST:
					sendMail(config, req);
					result.put("sent", true);
				default:
					if(config.getCustomVariables() != null) {
						for(CustomVariable var : config.getCustomVariables()) {
							if(var.isManual()) {
								req.getResponse().addGlobal(var.getKey(), var.getValue());
							} else {
								req.getResponse().addGlobal(var.getKey(), req.getParams().get(var.getValue()));
							}
						}
					}
				}
				return result;
			}

			@Override
			public MailDataHandlerConfig getOptions() {
				return config;
			}
			
		};
	}
	
	/**
	 * Setup and set the email message
	 * @param config
	 * @param req
	 */
	protected void sendMail(final MailDataHandlerConfig config, final Request req) {
		MailerProfile profile = mailer.getProfile(req.getVirtualHost(), config.getProfile());
		Map<String,Object> body = req.getBodyAsMap().toBlocking().last();
		MimeMessage message = (MimeMessage) mailer.create(profile);
		try {
			message.setSubject((String) body.getOrDefault("subject", config.getSubject()));
			try {
				message.setText(engine.render(req.getVirtualHost(), config.getTemplate(), body), "utf-8", "html");
			} catch (Exception e) {
				message.setText(body.toString());
			}
			message.setFrom(new InternetAddress((String) body.getOrDefault("from", config.getFrom())));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(config.getTo()));
			mailer.send(profile, message);
		} catch (MessagingException e) {
			e.printStackTrace();
			req.getResponse().setStatusCode(500);
			req.getResponse().end("Something went wrong: " + e.getMessage());
		}
	}
}
