package com.illam.chiya.utils;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Component
public class MailUtils {
	@Autowired
	private JavaMailSender javaMailSender;

	public void sendEmail(String email, String otp, String subject) {

		SimpleMailMessage msg = new SimpleMailMessage();
		msg.setTo(email);
		msg.setSubject(subject);
		msg.setText(otp);
		javaMailSender.send(msg);

	}

	public void sendEmailWithAttachment(String email, String body, String subject, File attachment) {
		MimeMessage message = javaMailSender.createMimeMessage();
		try {
			MimeMessageHelper helper = new MimeMessageHelper(message, true);
			helper.setTo(email);
			helper.setSubject(subject);
			helper.setText(body);
			helper.addAttachment(attachment.getName(), attachment);
			javaMailSender.send(message);
		} catch (MessagingException e) {
			e.printStackTrace(); 
		}
	}
}
