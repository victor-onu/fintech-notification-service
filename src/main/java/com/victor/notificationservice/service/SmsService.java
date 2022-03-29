package com.victor.notificationservice.service;

import com.victor.notificationservice.service.dto.SmsDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.victor.notificationservice.domain.Sms}.
 */
public interface SmsService {
    /**
     * Save a sms.
     *
     * @param smsDTO the entity to save.
     * @return the persisted entity.
     */
    SmsDTO save(SmsDTO smsDTO);

    /**
     * Partially updates a sms.
     *
     * @param smsDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<SmsDTO> partialUpdate(SmsDTO smsDTO);

    /**
     * Get all the sms.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<SmsDTO> findAll(Pageable pageable);

    /**
     * Get the "id" sms.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<SmsDTO> findOne(Long id);

    /**
     * Delete the "id" sms.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
