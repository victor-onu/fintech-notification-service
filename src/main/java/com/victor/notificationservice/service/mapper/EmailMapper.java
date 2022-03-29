package com.victor.notificationservice.service.mapper;

import com.victor.notificationservice.domain.Email;
import com.victor.notificationservice.service.dto.EmailDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Email} and its DTO {@link EmailDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface EmailMapper extends EntityMapper<EmailDTO, Email> {}
