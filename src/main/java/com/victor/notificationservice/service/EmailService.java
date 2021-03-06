package com.victor.notificationservice.service;

import com.victor.notificationservice.service.dto.AccountOwnerDTO;
import com.victor.notificationservice.service.dto.EmailDTO;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Optional;
import javax.mail.MessagingException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.victor.notificationservice.domain.Email}.
 */
public interface EmailService {
    /**
     * Save a email.
     *
     * @param emailDTO the entity to save.
     * @return the persisted entity.
     */
    EmailDTO save(EmailDTO emailDTO);

    /**
     * Partially updates a email.
     *
     * @param emailDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<EmailDTO> partialUpdate(EmailDTO emailDTO);

    /**
     * Get all the emails.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<EmailDTO> findAll(Pageable pageable);

    /**
     * Get the "id" email.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<EmailDTO> findOne(Long id);

    /**
     * Delete the "id" email.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    Boolean SendMail(String fromName, String subject, String to, String mail) throws MessagingException, UnsupportedEncodingException;

    String buildTemplate(HashMap<String, String> variables, String template);

    Boolean sendAccountRegistrationNotificationMail(AccountOwnerDTO accountOwnerDTO);

    //    void sendSimpleMail(String toEmail, String subject, String body);

    Boolean sendSimpleAccountRegistrationNotificationMail(AccountOwnerDTO accountOwnerDTO);
}
