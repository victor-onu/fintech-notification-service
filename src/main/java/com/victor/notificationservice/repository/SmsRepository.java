package com.victor.notificationservice.repository;

import com.victor.notificationservice.domain.Sms;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Sms entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SmsRepository extends JpaRepository<Sms, Long> {}
