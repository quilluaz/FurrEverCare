package cit.edu.furrevercare.controller;

import cit.edu.furrevercare.entity.EmergencyProfile;
import cit.edu.furrevercare.service.EmergencyProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/users/{userID}/pets/{petID}/emergencyProfile")
public class EmergencyProfileController {

    @Autowired
    private EmergencyProfileService emergencyProfileService;

    @PostMapping
    public String addEmergencyProfile(@PathVariable String userID, @PathVariable String petID, @RequestBody EmergencyProfile profile) throws ExecutionException, InterruptedException {
        return emergencyProfileService.addEmergencyProfile(userID, petID, profile);
    }

    @GetMapping
    public EmergencyProfile getEmergencyProfile(@PathVariable String userID, @PathVariable String petID) throws ExecutionException, InterruptedException {
        return emergencyProfileService.getEmergencyProfile(userID, petID);
    }

    @PutMapping
    public String updateEmergencyProfile(@PathVariable String userID, @PathVariable String petID, @RequestBody EmergencyProfile profile) throws ExecutionException, InterruptedException {
        return emergencyProfileService.updateEmergencyProfile(userID, petID, profile);
    }

    @DeleteMapping
    public String deleteEmergencyProfile(@PathVariable String userID, @PathVariable String petID) throws ExecutionException, InterruptedException {
        return emergencyProfileService.deleteEmergencyProfile(userID, petID);
    }
}
