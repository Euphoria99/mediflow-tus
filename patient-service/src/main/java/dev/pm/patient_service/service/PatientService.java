package dev.pm.patient_service.service;

import billing.BillingServiceGrpc;
import dev.pm.patient_service.dto.PatientRequestDTO;
import dev.pm.patient_service.dto.PatientResponseDTO;
import dev.pm.patient_service.exception.EmailAlreadyExistsException;
import dev.pm.patient_service.exception.PatientNotFoundException;
import dev.pm.patient_service.grpc.BillingServiceGrpcClient;
import dev.pm.patient_service.mapper.PatientMapper;
import dev.pm.patient_service.model.Patient;
import dev.pm.patient_service.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final BillingServiceGrpcClient billingServiceGrpcClient;

    public PatientService(PatientRepository patientRepository,BillingServiceGrpcClient billingServiceGrpcClient ) {
        this.patientRepository = patientRepository;
        this.billingServiceGrpcClient = billingServiceGrpcClient;
    }

    public List<PatientResponseDTO> getPatients() {
        List<Patient> patients = patientRepository.findAll();

        List<PatientResponseDTO> patientResponseDTOs = patients.stream()
                .map(patient -> PatientMapper.toDTO(patient)).toList();

        return patientResponseDTOs;
    }

    public PatientResponseDTO createPatient(PatientRequestDTO patientRequestDTO){

        if(patientRepository.existsByEmail(patientRequestDTO.getEmail())){
            throw new EmailAlreadyExistsException("A Patient with this email " + patientRequestDTO.getEmail() + " already exists");
        }
        Patient newPatient = patientRepository.save(PatientMapper.toModel(patientRequestDTO));

        billingServiceGrpcClient.createBillingAccount(newPatient.getId().toString(),
                newPatient.getName(), newPatient.getEmail());
        
        return PatientMapper.toDTO(newPatient);
    }

    public PatientResponseDTO updatePatient(UUID id, PatientRequestDTO patientRequestDTO){

        Patient patient = patientRepository.findById(id).orElseThrow( ()-> new PatientNotFoundException("Patient not found with ID: "+  id));

        if(patientRepository.existsByEmailAndIdNot(patientRequestDTO.getEmail(), id)){
            throw new EmailAlreadyExistsException("A Patient with this email " + patientRequestDTO.getEmail() + " already exists");
        }

        patient.setName(patientRequestDTO.getName());
        patient.setAddress(patient.getAddress());
        patient.setEmail(patient.getEmail());
        patient.setDateOfBirth(LocalDate.parse(patientRequestDTO.getDateOfBirth()));

        Patient updatedatient = patientRepository.save(patient);

        return PatientMapper.toDTO(updatedatient);
    }

    public void deletePatient(UUID id){
        patientRepository.deleteById(id);
    }
}