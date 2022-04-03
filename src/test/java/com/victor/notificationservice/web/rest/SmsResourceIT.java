package com.victor.notificationservice.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.victor.notificationservice.IntegrationTest;
import com.victor.notificationservice.domain.Sms;
import com.victor.notificationservice.domain.enumeration.DeliveryStatus;
import com.victor.notificationservice.repository.SmsRepository;
import com.victor.notificationservice.service.dto.SmsDTO;
import com.victor.notificationservice.service.mapper.SmsMapper;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link SmsResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SmsResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_MESSAGE = "AAAAAAAAAA";
    private static final String UPDATED_MESSAGE = "BBBBBBBBBB";

    private static final DeliveryStatus DEFAULT_STATUS = DeliveryStatus.DELIVERED;
    private static final DeliveryStatus UPDATED_STATUS = DeliveryStatus.NOT_DELIVERED;

    private static final String DEFAULT_SENDER = "AAAAAAAAAA";
    private static final String UPDATED_SENDER = "BBBBBBBBBB";

    private static final String DEFAULT_RECEIVER = "AAAAAAAAAA";
    private static final String UPDATED_RECEIVER = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/sms";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private SmsRepository smsRepository;

    @Autowired
    private SmsMapper smsMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSmsMockMvc;

    private Sms sms;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Sms createEntity(EntityManager em) {
        Sms sms = new Sms()
            .title(DEFAULT_TITLE)
            .message(DEFAULT_MESSAGE)
            .status(DEFAULT_STATUS)
            .sender(DEFAULT_SENDER)
            .receiver(DEFAULT_RECEIVER);
        return sms;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Sms createUpdatedEntity(EntityManager em) {
        Sms sms = new Sms()
            .title(UPDATED_TITLE)
            .message(UPDATED_MESSAGE)
            .status(UPDATED_STATUS)
            .sender(UPDATED_SENDER)
            .receiver(UPDATED_RECEIVER);
        return sms;
    }

    @BeforeEach
    public void initTest() {
        sms = createEntity(em);
    }

    @Test
    @Transactional
    void createSms() throws Exception {
        int databaseSizeBeforeCreate = smsRepository.findAll().size();
        // Create the Sms
        SmsDTO smsDTO = smsMapper.toDto(sms);
        restSmsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(smsDTO)))
            .andExpect(status().isCreated());

        // Validate the Sms in the database
        List<Sms> smsList = smsRepository.findAll();
        assertThat(smsList).hasSize(databaseSizeBeforeCreate + 1);
        Sms testSms = smsList.get(smsList.size() - 1);
        assertThat(testSms.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testSms.getMessage()).isEqualTo(DEFAULT_MESSAGE);
        assertThat(testSms.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testSms.getSender()).isEqualTo(DEFAULT_SENDER);
        assertThat(testSms.getReceiver()).isEqualTo(DEFAULT_RECEIVER);
    }

    @Test
    @Transactional
    void createSmsWithExistingId() throws Exception {
        // Create the Sms with an existing ID
        sms.setId(1L);
        SmsDTO smsDTO = smsMapper.toDto(sms);

        int databaseSizeBeforeCreate = smsRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSmsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(smsDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Sms in the database
        List<Sms> smsList = smsRepository.findAll();
        assertThat(smsList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllSms() throws Exception {
        // Initialize the database
        smsRepository.saveAndFlush(sms);

        // Get all the smsList
        restSmsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(sms.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].sender").value(hasItem(DEFAULT_SENDER)))
            .andExpect(jsonPath("$.[*].receiver").value(hasItem(DEFAULT_RECEIVER)));
    }

    @Test
    @Transactional
    void getSms() throws Exception {
        // Initialize the database
        smsRepository.saveAndFlush(sms);

        // Get the sms
        restSmsMockMvc
            .perform(get(ENTITY_API_URL_ID, sms.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(sms.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.message").value(DEFAULT_MESSAGE))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.sender").value(DEFAULT_SENDER))
            .andExpect(jsonPath("$.receiver").value(DEFAULT_RECEIVER));
    }

    @Test
    @Transactional
    void getNonExistingSms() throws Exception {
        // Get the sms
        restSmsMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewSms() throws Exception {
        // Initialize the database
        smsRepository.saveAndFlush(sms);

        int databaseSizeBeforeUpdate = smsRepository.findAll().size();

        // Update the sms
        Sms updatedSms = smsRepository.findById(sms.getId()).get();
        // Disconnect from session so that the updates on updatedSms are not directly saved in db
        em.detach(updatedSms);
        updatedSms.title(UPDATED_TITLE).message(UPDATED_MESSAGE).status(UPDATED_STATUS).sender(UPDATED_SENDER).receiver(UPDATED_RECEIVER);
        SmsDTO smsDTO = smsMapper.toDto(updatedSms);

        restSmsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, smsDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(smsDTO))
            )
            .andExpect(status().isOk());

        // Validate the Sms in the database
        List<Sms> smsList = smsRepository.findAll();
        assertThat(smsList).hasSize(databaseSizeBeforeUpdate);
        Sms testSms = smsList.get(smsList.size() - 1);
        assertThat(testSms.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testSms.getMessage()).isEqualTo(UPDATED_MESSAGE);
        assertThat(testSms.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testSms.getSender()).isEqualTo(UPDATED_SENDER);
        assertThat(testSms.getReceiver()).isEqualTo(UPDATED_RECEIVER);
    }

    @Test
    @Transactional
    void putNonExistingSms() throws Exception {
        int databaseSizeBeforeUpdate = smsRepository.findAll().size();
        sms.setId(count.incrementAndGet());

        // Create the Sms
        SmsDTO smsDTO = smsMapper.toDto(sms);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSmsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, smsDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(smsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Sms in the database
        List<Sms> smsList = smsRepository.findAll();
        assertThat(smsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSms() throws Exception {
        int databaseSizeBeforeUpdate = smsRepository.findAll().size();
        sms.setId(count.incrementAndGet());

        // Create the Sms
        SmsDTO smsDTO = smsMapper.toDto(sms);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSmsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(smsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Sms in the database
        List<Sms> smsList = smsRepository.findAll();
        assertThat(smsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSms() throws Exception {
        int databaseSizeBeforeUpdate = smsRepository.findAll().size();
        sms.setId(count.incrementAndGet());

        // Create the Sms
        SmsDTO smsDTO = smsMapper.toDto(sms);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSmsMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(smsDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Sms in the database
        List<Sms> smsList = smsRepository.findAll();
        assertThat(smsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSmsWithPatch() throws Exception {
        // Initialize the database
        smsRepository.saveAndFlush(sms);

        int databaseSizeBeforeUpdate = smsRepository.findAll().size();

        // Update the sms using partial update
        Sms partialUpdatedSms = new Sms();
        partialUpdatedSms.setId(sms.getId());

        partialUpdatedSms
            .title(UPDATED_TITLE)
            .message(UPDATED_MESSAGE)
            .status(UPDATED_STATUS)
            .sender(UPDATED_SENDER)
            .receiver(UPDATED_RECEIVER);

        restSmsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSms.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSms))
            )
            .andExpect(status().isOk());

        // Validate the Sms in the database
        List<Sms> smsList = smsRepository.findAll();
        assertThat(smsList).hasSize(databaseSizeBeforeUpdate);
        Sms testSms = smsList.get(smsList.size() - 1);
        assertThat(testSms.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testSms.getMessage()).isEqualTo(UPDATED_MESSAGE);
        assertThat(testSms.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testSms.getSender()).isEqualTo(UPDATED_SENDER);
        assertThat(testSms.getReceiver()).isEqualTo(UPDATED_RECEIVER);
    }

    @Test
    @Transactional
    void fullUpdateSmsWithPatch() throws Exception {
        // Initialize the database
        smsRepository.saveAndFlush(sms);

        int databaseSizeBeforeUpdate = smsRepository.findAll().size();

        // Update the sms using partial update
        Sms partialUpdatedSms = new Sms();
        partialUpdatedSms.setId(sms.getId());

        partialUpdatedSms
            .title(UPDATED_TITLE)
            .message(UPDATED_MESSAGE)
            .status(UPDATED_STATUS)
            .sender(UPDATED_SENDER)
            .receiver(UPDATED_RECEIVER);

        restSmsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSms.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSms))
            )
            .andExpect(status().isOk());

        // Validate the Sms in the database
        List<Sms> smsList = smsRepository.findAll();
        assertThat(smsList).hasSize(databaseSizeBeforeUpdate);
        Sms testSms = smsList.get(smsList.size() - 1);
        assertThat(testSms.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testSms.getMessage()).isEqualTo(UPDATED_MESSAGE);
        assertThat(testSms.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testSms.getSender()).isEqualTo(UPDATED_SENDER);
        assertThat(testSms.getReceiver()).isEqualTo(UPDATED_RECEIVER);
    }

    @Test
    @Transactional
    void patchNonExistingSms() throws Exception {
        int databaseSizeBeforeUpdate = smsRepository.findAll().size();
        sms.setId(count.incrementAndGet());

        // Create the Sms
        SmsDTO smsDTO = smsMapper.toDto(sms);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSmsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, smsDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(smsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Sms in the database
        List<Sms> smsList = smsRepository.findAll();
        assertThat(smsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSms() throws Exception {
        int databaseSizeBeforeUpdate = smsRepository.findAll().size();
        sms.setId(count.incrementAndGet());

        // Create the Sms
        SmsDTO smsDTO = smsMapper.toDto(sms);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSmsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(smsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Sms in the database
        List<Sms> smsList = smsRepository.findAll();
        assertThat(smsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSms() throws Exception {
        int databaseSizeBeforeUpdate = smsRepository.findAll().size();
        sms.setId(count.incrementAndGet());

        // Create the Sms
        SmsDTO smsDTO = smsMapper.toDto(sms);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSmsMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(smsDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Sms in the database
        List<Sms> smsList = smsRepository.findAll();
        assertThat(smsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSms() throws Exception {
        // Initialize the database
        smsRepository.saveAndFlush(sms);

        int databaseSizeBeforeDelete = smsRepository.findAll().size();

        // Delete the sms
        restSmsMockMvc.perform(delete(ENTITY_API_URL_ID, sms.getId()).accept(MediaType.APPLICATION_JSON)).andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Sms> smsList = smsRepository.findAll();
        assertThat(smsList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
