package com.victor.notificationservice.service.dto;

import com.victor.notificationservice.domain.enumeration.DeliveryStatus;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.victor.notificationservice.domain.Sms} entity.
 */
public class SmsDTO implements Serializable {

    private Long id;

    private String title;

    private String message;

    private DeliveryStatus status;

    private String sender;

    private String receiver;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public DeliveryStatus getStatus() {
        return status;
    }

    public void setStatus(DeliveryStatus status) {
        this.status = status;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SmsDTO)) {
            return false;
        }

        SmsDTO smsDTO = (SmsDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, smsDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SmsDTO{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", message='" + getMessage() + "'" +
            ", status='" + getStatus() + "'" +
            ", sender='" + getSender() + "'" +
            ", receiver='" + getReceiver() + "'" +
            "}";
    }
}
