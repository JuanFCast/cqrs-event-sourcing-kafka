package com.techbank.account.query.infrastructure.consumers;

// Ya no se importan eventos específicos aquí
import com.techbank.account.query.infrastructure.handlers.EventHandler;
import com.techbank.cqrs.core.events.BaseEvent; // <-- CAMBIO: Importar BaseEvent
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class AccountEventConsumer implements EventConsumer {

    @Autowired
    private EventHandler eventHandler;

    // <-- CAMBIO: Un único @KafkaListener que escucha el topic único del application.yml
    @KafkaListener(topics = "${spring.kafka.topic}", groupId = "${spring.kafka.consumer.group-id}")
    @Override
    public void consume(@Payload BaseEvent event, Acknowledgment ack) {
        // <-- CAMBIO: Lógica de reflexión para invocar el método 'on' correcto en el EventHandler
        try {
            // Encuentra el método 'on' en EventHandler que coincida con el tipo de evento recibido
            var handlerMethod = eventHandler.getClass().getDeclaredMethod("on", event.getClass());
            handlerMethod.setAccessible(true); // Asegura acceso si el método no es público (aunque debería serlo)
            handlerMethod.invoke(eventHandler, event); // Invoca el método encontrado (ej: on(AccountOpenedEvent))
            ack.acknowledge(); // Confirma el mensaje a Kafka solo si el procesamiento fue exitoso
        } catch (Exception e) {
            // Loggear el error sería ideal aquí
            // Lanzar RuntimeException asegura que el error no pase desapercibido
            // y permite que el contenedor de Kafka maneje el reintento o DLQ si está configurado.
            throw new RuntimeException("Error while consuming event: " + event.getClass().getSimpleName(), e);
        }
    }

    // Se eliminan los métodos consume específicos para cada evento y sus @KafkaListener individuales
}