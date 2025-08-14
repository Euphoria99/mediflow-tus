package dev.pavanbhat.analytics_service.kafka;

import billing.PatientEvent;
import com.google.protobuf.InvalidProtocolBufferException;
import dev.pavanbhat.analytics_service.AnalyticsServiceApplication;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;

@Service
public class KafkaConsumer {

    private static final Logger log = LoggerFactory.getLogger(AnalyticsServiceApplication.class);

    @KafkaListener(topics = "patient", groupId = "analytics-service")
    public void consumeEvent(byte[] event){

        try {
            PatientEvent patientEvent = PatientEvent.parseFrom(event);
            //
            log.info("Received Patient Event: [PatientId={}, PatientName={}, " + "PatientEmail={} ]",
                    patientEvent.getPatientId(),
                    patientEvent.getName(),
                    patientEvent.getEmail());
        } catch (InvalidProtocolBufferException e) {
            log.error("Error deserializing event{}", e.getMessage());
        }
    }
}
