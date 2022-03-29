package com.victor.notificationservice.web.rest;

import com.victor.notificationservice.repository.SmsRepository;
import com.victor.notificationservice.service.SmsService;
import com.victor.notificationservice.service.dto.SmsDTO;
import com.victor.notificationservice.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.victor.notificationservice.domain.Sms}.
 */
@RestController
@RequestMapping("/api")
public class SmsResource {

    private final Logger log = LoggerFactory.getLogger(SmsResource.class);

    private static final String ENTITY_NAME = "notificationServiceSms";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SmsService smsService;

    private final SmsRepository smsRepository;

    public SmsResource(SmsService smsService, SmsRepository smsRepository) {
        this.smsService = smsService;
        this.smsRepository = smsRepository;
    }

    /**
     * {@code POST  /sms} : Create a new sms.
     *
     * @param smsDTO the smsDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new smsDTO, or with status {@code 400 (Bad Request)} if the sms has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/sms")
    public ResponseEntity<SmsDTO> createSms(@RequestBody SmsDTO smsDTO) throws URISyntaxException {
        log.debug("REST request to save Sms : {}", smsDTO);
        if (smsDTO.getId() != null) {
            throw new BadRequestAlertException("A new sms cannot already have an ID", ENTITY_NAME, "idexists");
        }
        SmsDTO result = smsService.save(smsDTO);
        return ResponseEntity
            .created(new URI("/api/sms/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /sms/:id} : Updates an existing sms.
     *
     * @param id the id of the smsDTO to save.
     * @param smsDTO the smsDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated smsDTO,
     * or with status {@code 400 (Bad Request)} if the smsDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the smsDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/sms/{id}")
    public ResponseEntity<SmsDTO> updateSms(@PathVariable(value = "id", required = false) final Long id, @RequestBody SmsDTO smsDTO)
        throws URISyntaxException {
        log.debug("REST request to update Sms : {}, {}", id, smsDTO);
        if (smsDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, smsDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!smsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        SmsDTO result = smsService.save(smsDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, smsDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /sms/:id} : Partial updates given fields of an existing sms, field will ignore if it is null
     *
     * @param id the id of the smsDTO to save.
     * @param smsDTO the smsDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated smsDTO,
     * or with status {@code 400 (Bad Request)} if the smsDTO is not valid,
     * or with status {@code 404 (Not Found)} if the smsDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the smsDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/sms/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<SmsDTO> partialUpdateSms(@PathVariable(value = "id", required = false) final Long id, @RequestBody SmsDTO smsDTO)
        throws URISyntaxException {
        log.debug("REST request to partial update Sms partially : {}, {}", id, smsDTO);
        if (smsDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, smsDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!smsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SmsDTO> result = smsService.partialUpdate(smsDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, smsDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /sms} : get all the sms.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of sms in body.
     */
    @GetMapping("/sms")
    public ResponseEntity<List<SmsDTO>> getAllSms(Pageable pageable) {
        log.debug("REST request to get a page of Sms");
        Page<SmsDTO> page = smsService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /sms/:id} : get the "id" sms.
     *
     * @param id the id of the smsDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the smsDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/sms/{id}")
    public ResponseEntity<SmsDTO> getSms(@PathVariable Long id) {
        log.debug("REST request to get Sms : {}", id);
        Optional<SmsDTO> smsDTO = smsService.findOne(id);
        return ResponseUtil.wrapOrNotFound(smsDTO);
    }

    /**
     * {@code DELETE  /sms/:id} : delete the "id" sms.
     *
     * @param id the id of the smsDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/sms/{id}")
    public ResponseEntity<Void> deleteSms(@PathVariable Long id) {
        log.debug("REST request to delete Sms : {}", id);
        smsService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
