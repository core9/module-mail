package io.core9.mail.widget;

import io.core9.plugin.widgets.datahandler.DataHandlerDefaultConfig;

public class MailDataHandlerConfig extends DataHandlerDefaultConfig {
	
	private String profile;
	private String to;
	private String subject;
	private String from;

	public String getProfile() {
		return profile;
	}

	public void setProfile(String profile) {
		this.profile = profile;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getSubject() {
		return subject;
	}
	
	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getFrom() {
		return from;
	}
	
	public void setFrom(String from) {
		this.from = from;
	}

}
