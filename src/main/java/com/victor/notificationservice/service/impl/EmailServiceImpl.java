package com.victor.notificationservice.service.impl;

import com.victor.notificationservice.config.ApplicationProperties;
import com.victor.notificationservice.domain.Email;
import com.victor.notificationservice.domain.enumeration.DeliveryStatus;
import com.victor.notificationservice.repository.EmailRepository;
import com.victor.notificationservice.service.EmailService;
import com.victor.notificationservice.service.dto.AccountOwnerDTO;
import com.victor.notificationservice.service.dto.EmailDTO;
import com.victor.notificationservice.service.mapper.EmailMapper;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;

/**
 * Service Implementation for managing {@link Email}.
 */
@Service
@Transactional
public class EmailServiceImpl implements EmailService {

    private final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);

    private final EmailRepository emailRepository;

    private final EmailMapper emailMapper;

    private final ITemplateEngine templateEngine;
    private final ApplicationProperties applicationProperties;

    private final JavaMailSender mailSender;

    public EmailServiceImpl(
        EmailRepository emailRepository,
        EmailMapper emailMapper,
        ITemplateEngine templateEngine,
        ApplicationProperties applicationProperties,
        JavaMailSender mailSender
    ) {
        this.emailRepository = emailRepository;
        this.emailMapper = emailMapper;
        this.templateEngine = templateEngine;
        this.applicationProperties = applicationProperties;
        this.mailSender = mailSender;
    }

    @Override
    public EmailDTO save(EmailDTO emailDTO) {
        log.debug("Request to save Email : {}", emailDTO);
        Email email = emailMapper.toEntity(emailDTO);
        email = emailRepository.save(email);
        return emailMapper.toDto(email);
    }

    @Override
    public Optional<EmailDTO> partialUpdate(EmailDTO emailDTO) {
        log.debug("Request to partially update Email : {}", emailDTO);

        return emailRepository
            .findById(emailDTO.getId())
            .map(existingEmail -> {
                emailMapper.partialUpdate(existingEmail, emailDTO);

                return existingEmail;
            })
            .map(emailRepository::save)
            .map(emailMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmailDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Emails");
        return emailRepository.findAll(pageable).map(emailMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<EmailDTO> findOne(Long id) {
        log.debug("Request to get Email : {}", id);
        return emailRepository.findById(id).map(emailMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Email : {}", id);
        emailRepository.deleteById(id);
    }

    @Override
    public Boolean SendMail(String fromName, String subject, String to, String mail)
        throws MessagingException, UnsupportedEncodingException {
        // Create a Properties object to contain connection configuration information.
        Properties props = System.getProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.port", applicationProperties.getSmtpPort());
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.auth", "true");

        // Create a Session object to represent a mail session with the specified properties.
        Session session = Session.getDefaultInstance(props);

        // Create a message with the specified information.
        MimeMessage msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(applicationProperties.getSmtpFrom(), fromName));
        msg.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
        msg.setSubject(subject);
        msg.setContent(mail, "text/html");

        // Add a configuration set header. Comment or delete the
        // next line if you are not using a configuration set
        //        msg.setHeader("X-SES-CONFIGURATION-SET", CONFIGSET);

        // Create a transport.
        Transport transport = session.getTransport();

        // Send the message.
        try {
            System.out.println("Sending...");

            // Connect to Amazon SES using the SMTP username and password you specified above.
            transport.connect(
                applicationProperties.getSmtpHost(),
                applicationProperties.getSmtpUsername(),
                applicationProperties.getSmtpPassword()
            );

            // Send the email.
            transport.sendMessage(msg, msg.getAllRecipients());
            System.out.println("Email sent!");
        } catch (Exception ex) {
            System.out.println("The email was not sent.");
            System.out.println("Error message: " + ex.getMessage());
            return false;
        } finally {
            // Close and terminate the connection.
            transport.close();
        }
        return true;
    }

    @Override
    public String buildTemplate(HashMap<String, String> variables, String template) {
        Context context = new Context();
        for (Map.Entry variable : variables.entrySet()) {
            context.setVariable(variable.getKey().toString(), variable.getValue());
        }
        return templateEngine.process(template, context);
    }

    @Override
    public Boolean sendAccountRegistrationNotificationMail(AccountOwnerDTO accountOwnerDTO) {
        HashMap<String, String> variables = new HashMap<>();
        variables.put("name", accountOwnerDTO.getFirstName());

        try {
            String mail = buildTemplate(variables, "account_registration.html");
            this.SendMail("Fintech", "ACCOUNT REGISTRATION NOTIFICATION", accountOwnerDTO.getEmail(), mail);
        } catch (Exception exception) {
            log.debug("Exception occurred when trying to send a mail {}", exception.getMessage());
            return false;
        }

        return true;
    }

    @Override
    public Boolean sendSimpleAccountRegistrationNotificationMail(AccountOwnerDTO accountOwnerDTO) {
        HashMap<String, String> variables = new HashMap<>();
        variables.put("name", accountOwnerDTO.getFirstName());

        EmailDTO emailDTO = new EmailDTO();
        emailDTO.setReceiver(accountOwnerDTO.getEmail());
        emailDTO.setSender(applicationProperties.getSmtpFrom());

        DeliveryStatus deliveryStatus;

        try {
            String mail = buildTemplate(variables, "account_registration.html");

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            helper.setFrom(applicationProperties.getSmtpFrom());
            helper.setSubject("ACCOUNT REGISTRATION NOTIFICATION");
            emailDTO.setTitle("ACCOUNT REGISTRATION NOTIFICATION");
            helper.setText(mail, true);
            helper.setTo(accountOwnerDTO.getEmail());

            mailSender.send(mimeMessage);
            deliveryStatus = DeliveryStatus.DELIVERED;
            System.out.println("Email sent!");
        } catch (Exception exception) {
            log.debug("Exception occurred when trying to send a mail {}", exception.getMessage());
            emailDTO.setResponseMessage(exception.getMessage());
            return false;
        }
        emailDTO.setStatus(deliveryStatus);
        this.save(emailDTO);

        return true;
    }
}
