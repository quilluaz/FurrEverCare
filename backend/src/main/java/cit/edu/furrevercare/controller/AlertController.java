package cit.edu.furrevercare.controller;

import cit.edu.furrevercare.entity.Alert;
import cit.edu.furrevercare.service.AlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
// Assuming alerts are nested under pets for context
@RequestMapping("/api/users/{userID}/pets/{petID}/alerts")
public class AlertController {

    @Autowired
    private AlertService alertService;

     // Helper method for authorization check
     private void checkAuthorization(String userID) {
         Authentication auth = SecurityContextHolder.getContext().getAuthentication();
         if (auth == null || !auth.getName().equals(userID)) {
             throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized access to user resources");
         }
    }

    // Note: POST endpoint might not be exposed directly if alerts are only system-generated.
    // Exposing it allows manual creation if needed, otherwise remove it.
    @PostMapping
    public ResponseEntity<String> addAlert(@PathVariable String userID, @PathVariable String petID, @RequestBody Alert alert) {
        checkAuthorization(userID);
        try {
            // Ensure IDs are set correctly
            alert.setUserID(userID);
            alert.setPetID(petID);
            String alertID = alertService.addAlert(userID, petID, alert);
            return ResponseEntity.status(HttpStatus.CREATED).body(alertID);
        } catch (ExecutionException | InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding alert: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error adding alert: " + e.getMessage());
        }
    }


    @GetMapping
    public ResponseEntity<List<Alert>> getAlerts(
            @PathVariable String userID,
            @PathVariable String petID,
            @RequestParam(required = false) Boolean unread // Filter for ?unread=true
    ) {
        checkAuthorization(userID);
        try {
            Boolean readStatusFilter = (unread != null && unread) ? Boolean.FALSE : null; // If unread=true, filter for readStatus=false
            List<Alert> alerts = alertService.getAlerts(userID, petID, readStatusFilter);
            return ResponseEntity.ok(alerts);
        } catch (ExecutionException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving alerts", e);
        }
    }

     @GetMapping("/{alertID}")
     public ResponseEntity<Alert> getAlertById(@PathVariable String userID, @PathVariable String petID, @PathVariable String alertID) {
         checkAuthorization(userID);
         try {
             Alert alert = alertService.getAlertById(userID, petID, alertID);
             return (alert != null) ? ResponseEntity.ok(alert) : ResponseEntity.notFound().build();
         } catch (ExecutionException | InterruptedException e) {
             Thread.currentThread().interrupt();
             throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving alert", e);
         }
     }

    @PatchMapping("/{alertID}/read") // Endpoint to mark as read
    public ResponseEntity<String> markAlertAsRead(@PathVariable String userID, @PathVariable String petID, @PathVariable String alertID) {
        checkAuthorization(userID);
        try {
            String result = alertService.markAlertAsRead(userID, petID, alertID);
            return ResponseEntity.ok(result);
        } catch (ExecutionException | InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error marking alert as read: " + e.getMessage());
        }
    }

    @DeleteMapping("/{alertID}")
    public ResponseEntity<String> deleteAlert(@PathVariable String userID, @PathVariable String petID, @PathVariable String alertID) {
         checkAuthorization(userID);
         try {
             String result = alertService.deleteAlert(userID, petID, alertID);
             return ResponseEntity.ok(result);
         } catch (ExecutionException | InterruptedException e) {
             Thread.currentThread().interrupt();
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting alert: " + e.getMessage());
         }
    }
}