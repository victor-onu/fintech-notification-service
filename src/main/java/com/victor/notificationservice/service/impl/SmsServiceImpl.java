package com.victor.notificationservice.service.impl;

import com.victor.notificationservice.domain.Sms;
import com.victor.notificationservice.repository.SmsRepository;
import com.victor.notificationservice.service.SmsService;
import com.victor.notificationservice.service.dto.SmsDTO;
import com.victor.notificationservice.service.mapper.SmsMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Sms}.
 */
@Service
@Transactional
public class SmsServiceImpl implements SmsService {

    private final Logger log = LoggerFactory.getLogger(SmsServiceImpl.class);

    private final SmsRepository smsRepository;

    private final SmsMapper smsMapper;

    public SmsServiceImpl(SmsRepository smsRepository, SmsMapper smsMapper) {
        this.smsRepository = smsRepository;
        this.smsMapper = smsMapper;
    }

    @Override
    public SmsDTO save(SmsDTO smsDTO) {
        log.debug("Request to save Sms : {}", smsDTO);
        Sms sms = smsMapper.toEntity(smsDTO);
        sms = smsRepository.save(sms);
        return smsMapper.toDto(sms);
    }

    @Override
    public Optional<SmsDTO> partialUpdate(SmsDTO smsDTO) {
        log.debug("Request to partially update Sms : {}", smsDTO);

        return smsRepository
            .findById(smsDTO.getId())
            .map(existingSms -> {
                smsMapper.partialUpdate(existingSms, smsDTO);

                return existingSms;
            })
            .map(smsRepository::save)
            .map(smsMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SmsDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Sms");
        return smsRepository.findAll(pageable).map(smsMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SmsDTO> findOne(Long id) {
        log.debug("Request to get Sms : {}", id);
        return smsRepository.findById(id).map(smsMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Sms : {}", id);
        smsRepository.deleteById(id);
    }
}
