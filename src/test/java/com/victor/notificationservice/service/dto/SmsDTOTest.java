package com.victor.notificationservice.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.victor.notificationservice.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SmsDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SmsDTO.class);
        SmsDTO smsDTO1 = new SmsDTO();
        smsDTO1.setId(1L);
        SmsDTO smsDTO2 = new SmsDTO();
        assertThat(smsDTO1).isNotEqualTo(smsDTO2);
        smsDTO2.setId(smsDTO1.getId());
        assertThat(smsDTO1).isEqualTo(smsDTO2);
        smsDTO2.setId(2L);
        assertThat(smsDTO1).isNotEqualTo(smsDTO2);
        smsDTO1.setId(null);
        assertThat(smsDTO1).isNotEqualTo(smsDTO2);
    }
}
