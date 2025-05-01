package cit.edu.furrevercare.controller;

import cit.edu.furrevercare.entity.TreatmentPlan;
import cit.edu.furrevercare.service.TreatmentPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/users/{userID}/pets/{petID}/treatmentPlans")
public class TreatmentPlanController {

    @Autowired
    private TreatmentPlanService treatmentPlanService;

    // Helper method for authorization check
    private void checkAuthorization(String userID) {
         Authentication auth = SecurityContextHolder.getContext().getAuthentication();
         if (auth == null || !auth.getName().equals(userID)) {
             throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized access to user resources");
         }
    }


    @PostMapping
    public ResponseEntity<String> addTreatmentPlan(@PathVariable String userID, @PathVariable String petID, @RequestBody TreatmentPlan plan) {
        checkAuthorization(userID);
        try {
            String planID = treatmentPlanService.addTreatmentPlan(userID, petID, plan);
            return ResponseEntity.status(HttpStatus.CREATED).body(planID);
        } catch (ExecutionException | InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding plan: " + e.getMessage());
        } catch (Exception e) {
             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error adding plan: " + e.getMessage());
        }
    }

    @GetMapping("/{planID}")
    public ResponseEntity<TreatmentPlan> getTreatmentPlanById(@PathVariable String userID, @PathVariable String petID, @PathVariable String planID) {
         checkAuthorization(userID);
         try {
             TreatmentPlan plan = treatmentPlanService.getTreatmentPlanById(userID, petID, planID);
             return (plan != null) ? ResponseEntity.ok(plan) : ResponseEntity.notFound().build();
         } catch (ExecutionException | InterruptedException e) {
             Thread.currentThread().interrupt();
             throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving plan", e);
         }
    }

    @GetMapping
    public ResponseEntity<List<TreatmentPlan>> getTreatmentPlans(
            @PathVariable String userID,
            @PathVariable String petID,
            @RequestParam(required = false) TreatmentPlan.PlanStatus status // Optional filter by status (e.g., ?status=ACTIVE)
    ) {
         checkAuthorization(userID);
         try {
             List<TreatmentPlan> plans = treatmentPlanService.getTreatmentPlans(userID, petID, status);
             return ResponseEntity.ok(plans);
         } catch (ExecutionException | InterruptedException e) {
             Thread.currentThread().interrupt();
             throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving plans", e);
         }
    }

    @PutMapping("/{planID}")
     public ResponseEntity<String> updateTreatmentPlan(@PathVariable String userID, @PathVariable String petID, @PathVariable String planID, @RequestBody TreatmentPlan plan) {
         checkAuthorization(userID);
         try {
             plan.setPlanID(planID); // Ensure path ID is used
             String result = treatmentPlanService.updateTreatmentPlan(userID, petID, planID, plan);
             return ResponseEntity.ok(result);
         } catch (ExecutionException | InterruptedException e) {
             Thread.currentThread().interrupt();
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating plan: " + e.getMessage());
         } catch (Exception e) {
             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error updating plan: " + e.getMessage());
         }
    }

     @PatchMapping("/{planID}/progress") // Endpoint to specifically update progress
     public ResponseEntity<String> updatePlanProgress(
            @PathVariable String userID,
            @PathVariable String petID,
            @PathVariable String planID,
            @RequestBody Map<String, Integer> payload) { // Expect {"progress": value}

         checkAuthorization(userID);
         Integer progress = payload.get("progress");
         if (progress == null) {
             return ResponseEntity.badRequest().body("Missing 'progress' field in request body.");
         }

         try {
             String result = treatmentPlanService.updatePlanProgress(userID, petID, planID, progress);
             return ResponseEntity.ok(result);
         } catch (IllegalArgumentException e) {
              return ResponseEntity.badRequest().body(e.getMessage());
         } catch (ExecutionException | InterruptedException e) {
             Thread.currentThread().interrupt();
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating plan progress: " + e.getMessage());
         }
     }


    @DeleteMapping("/{planID}")
    public ResponseEntity<String> deleteTreatmentPlan(@PathVariable String userID, @PathVariable String petID, @PathVariable String planID) {
         checkAuthorization(userID);
         try {
             String result = treatmentPlanService.deleteTreatmentPlan(userID, petID, planID);
             return ResponseEntity.ok(result);
         } catch (ExecutionException | InterruptedException e) {
             Thread.currentThread().interrupt();
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting plan: " + e.getMessage());
         }
    }
}