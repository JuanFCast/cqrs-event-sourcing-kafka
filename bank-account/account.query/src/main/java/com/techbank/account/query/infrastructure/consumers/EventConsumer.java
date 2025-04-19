package com.techbank.account.query.infrastructure.consumers;

// Ya no se importan eventos específicos aquí
import com.techbank.cqrs.core.events.BaseEvent; // <-- CAMBIO: Importar BaseEvent
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;

public interface EventConsumer {
    // <-- CAMBIO: Un solo método consume que acepta BaseEvent
    void consume(@Payload BaseEvent event, Acknowledgment ack);
}