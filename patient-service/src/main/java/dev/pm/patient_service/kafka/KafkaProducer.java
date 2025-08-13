package dev.pm.patient_service.kafka;

import billing.PatientEvent;
import dev.pm.patient_service.grpc.BillingServiceGrpcClient;
import dev.pm.patient_service.model.Patient;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import java.util.Base64;

@Service
public class KafkaProducer {

    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    private static final Logger log = LoggerFactory.getLogger(BillingServiceGrpcClient.class);

    public KafkaProducer(KafkaTemplate<String, byte[]> kafkaTemplate){
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEvent(Patient patient){
        PatientEvent event = PatientEvent.newBuilder()
                .setPatientId(patient.getId().toString())
                .setName(patient.getName())
                .setEmail(patient.getEmail())
                .setEventType("PATIENT_CREATED")
                .build();


        byte[] payload = event.toByteArray();
        String base64Payload = Base64.getEncoder().encodeToString(payload);

        try{
            log.info("Sending PatientCreated event (Base64 encoded): {}", base64Payload);
            kafkaTemplate.send("patient", event.toByteArray());
        } catch(Exception e){
            log.error("Error sending PatientCreated event : {}", event);
        }
    }

}
