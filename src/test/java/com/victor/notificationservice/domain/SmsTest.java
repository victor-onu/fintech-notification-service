package com.victor.notificationservice.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.victor.notificationservice.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SmsTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Sms.class);
        Sms sms1 = new Sms();
        sms1.setId(1L);
        Sms sms2 = new Sms();
        sms2.setId(sms1.getId());
        assertThat(sms1).isEqualTo(sms2);
        sms2.setId(2L);
        assertThat(sms1).isNotEqualTo(sms2);
        sms1.setId(null);
        assertThat(sms1).isNotEqualTo(sms2);
    }
}
