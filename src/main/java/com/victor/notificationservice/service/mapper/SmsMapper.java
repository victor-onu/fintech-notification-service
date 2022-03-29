package com.victor.notificationservice.service.mapper;

import com.victor.notificationservice.domain.Sms;
import com.victor.notificationservice.service.dto.SmsDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Sms} and its DTO {@link SmsDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface SmsMapper extends EntityMapper<SmsDTO, Sms> {}
