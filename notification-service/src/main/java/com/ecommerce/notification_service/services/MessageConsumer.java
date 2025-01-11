package com.ecommerce.notification_service.services;

import com.ecommerce.notification_service.configs.RabbitMQConfig;
import com.ecommerce.notification_service.entities.NotificationMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class MessageConsumer {

    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void receiveNotification(NotificationMessage notificationMessage) {
        System.out.println("Notification received: " + notificationMessage);

        // Process the notification
        sendEmail(notificationMessage.getEmail(), notificationMessage.getMessage());
    }

    private void sendEmail(String email, String message) {
        System.out.println("Sending email to: " + email);
        System.out.println("Email content: " + message);
    }
}




