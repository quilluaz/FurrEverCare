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
import org.springframework.lang.Nullable; // Import Nullable

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/users/{userID}/pets/{petID}/treatmentPlans")
public class TreatmentPlanController {

    private final TreatmentPlanService treatmentPlanService;

    // Constructor injection for TreatmentPlanService
    @Autowired
    public TreatmentPlanController(TreatmentPlanService treatmentPlanService) {
        this.treatmentPlanService = treatmentPlanService;
    }

    // Enhanced helper method for authorization check
    private void checkAuthorization(String userID, String petID, @Nullable String planID) {
         Authentication auth = SecurityContextHolder.getContext().getAuthentication();
         System.out.println("DEBUG - TreatmentPlanController - Auth in security context: " + (auth != null ? auth.getName() : "null"));
         System.out.println("DEBUG - TreatmentPlanController - UserID in path: " + userID);
         System.out.println("DEBUG - TreatmentPlanController - PetID in path: " + petID);
         System.out.println("DEBUG - TreatmentPlanController - PlanID in path: " + (planID != null ? planID : "N/A"));

         // 1. Check if authenticated user matches the userID in the path
         if (auth == null || !auth.getName().equals(userID)) {
             System.out.println("DEBUG - TreatmentPlanController - Authorization failed (User mismatch) - throwing 403 Forbidden");
             throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized access to user resources");
         }

         // 2. Verify pet ownership (Assuming TreatmentPlanService has this method)
         try {
             treatmentPlanService.verifyPetOwnership(userID, petID);
             System.out.println("DEBUG - TreatmentPlanController - Pet ownership verified for petID: " + petID);
         } catch (ResponseStatusException e) {
             System.out.println("DEBUG - TreatmentPlanController - Authorization failed (Pet ownership) - throwing " + e.getStatusCode());
             throw e; // Re-throw the exception from the service
         } catch (Exception e) { // Catch other potential exceptions from the service method
             System.out.println("DEBUG - TreatmentPlanController - Error during pet ownership verification: " + e.getMessage());
             throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error verifying resource access", e);
         }

         // 3. Verify plan ownership if planID is provided (Assuming TreatmentPlanService has this method)
        /**  if (planID != null) {
             try {
                 treatmentPlanService.verifyPlanOwnership(petID, planID);
                 System.out.println("DEBUG - TreatmentPlanController - Plan ownership verified for planID: " + planID);
             } catch (ResponseStatusException e) {
                 System.out.println("DEBUG - TreatmentPlanController - Authorization failed (Plan ownership) - throwing " + e.getStatusCode());
                 throw e; // Re-throw the exception from the service
             } catch (Exception e) { // Catch other potential exceptions from the service method
                 System.out.println("DEBUG - TreatmentPlanController - Error during plan ownership verification: " + e.getMessage());
                 throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error verifying resource access", e);
             }
         }

         System.out.println("DEBUG - TreatmentPlanController - Authorization successful for userID: " + userID + ", petID: " + petID + (planID != null ? ", planID: " + planID : ""));
    }
**/

    @PostMapping("/add")
    public ResponseEntity<String> addTreatmentPlan(@PathVariable String userID, @PathVariable String petID, @RequestBody TreatmentPlan plan) {
        checkAuthorization(userID, petID, null); // Check user and pet ownership
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
         checkAuthorization(userID, petID, planID); // Check user, pet, and plan ownership
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
         checkAuthorization(userID, petID, null); // Check user and pet ownership
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
         checkAuthorization(userID, petID, planID); // Check user, pet, and plan ownership
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

         checkAuthorization(userID, petID, planID); // Check user, pet, and plan ownership
         Integer progress = payload.get("progress");
         if (progress == null) {
             return ResponseEntity.badRequest().body("Missing 'progress' field in request body.");
         }
         // Add validation for progress value (0-100)
         if (progress < 0 || progress > 100) {
             return ResponseEntity.badRequest().body("'progress' must be between 0 and 100.");
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
         checkAuthorization(userID, petID, planID); // Check user, pet, and plan ownership
         try {
             String result = treatmentPlanService.deleteTreatmentPlan(userID, petID, planID);
             return ResponseEntity.ok(result);
         } catch (ExecutionException | InterruptedException e) {
             Thread.currentThread().interrupt();
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting plan: " + e.getMessage());
         }
    }


}