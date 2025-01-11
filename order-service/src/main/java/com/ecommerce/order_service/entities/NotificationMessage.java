package com.ecommerce.order_service.entities;

import java.io.Serializable;

public class NotificationMessage implements Serializable {

    private String email;
    private String message;

    public NotificationMessage(String email, String message) {
        this.email = email;
        this.message = message;
    }

    // Getters and Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "NotificationMessage{" +
                "email='" + email + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
