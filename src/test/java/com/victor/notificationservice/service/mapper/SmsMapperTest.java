package com.victor.notificationservice.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SmsMapperTest {

    private SmsMapper smsMapper;

    @BeforeEach
    public void setUp() {
        smsMapper = new SmsMapperImpl();
    }
}
