package cit.edu.furrevercare.controller;

import cit.edu.furrevercare.entity.MedicalRecord;
import cit.edu.furrevercare.service.MedicalRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/users/{userID}/pets/{petID}/medicalRecords")
public class MedicalRecordController {

    @Autowired
    private MedicalRecordService medicalRecordService;

    @PostMapping
    public String addMedicalRecord(@PathVariable String userID, @PathVariable String petID, @RequestBody MedicalRecord record) throws ExecutionException, InterruptedException {
        return medicalRecordService.addMedicalRecord(userID, petID, record);
    }

    @GetMapping
    public List<MedicalRecord> getMedicalRecords(@PathVariable String userID, @PathVariable String petID) throws ExecutionException, InterruptedException {
        return medicalRecordService.getMedicalRecords(userID, petID);
    }

    @PutMapping("/{recordID}")
    public String updateMedicalRecord(@PathVariable String userID, @PathVariable String petID, @PathVariable String recordID, @RequestBody MedicalRecord record) throws ExecutionException, InterruptedException {
        return medicalRecordService.updateMedicalRecord(userID, petID, recordID, record);
    }

    @DeleteMapping("/{recordID}")
    public String deleteMedicalRecord(@PathVariable String userID, @PathVariable String petID, @PathVariable String recordID) throws ExecutionException, InterruptedException {
        return medicalRecordService.deleteMedicalRecord(userID, petID, recordID);
    }
}
