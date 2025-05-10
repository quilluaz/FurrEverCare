package cit.edu.furrevercare.controller;

import cit.edu.furrevercare.entity.ScheduledTask;
import cit.edu.furrevercare.service.ScheduledTaskService;
import com.google.cloud.Timestamp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import java.util.Date; // Use java.util.Date for @RequestParam binding
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/users/{userID}/pets/{petID}/scheduledTasks")
public class ScheduledTaskController {

    @Autowired
    private ScheduledTaskService scheduledTaskService;

    // Helper method for authorization check
    private void checkAuthorization(String userID) {
         Authentication auth = SecurityContextHolder.getContext().getAuthentication();
         if (auth == null || !auth.getName().equals(userID)) {
              // Consider using Spring Security's @PreAuthorize or a security filter instead
              throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized access to user resources");
         }
    }


    @PostMapping
    public ResponseEntity<String> addScheduledTask(@PathVariable String userID, @PathVariable String petID, @RequestBody ScheduledTask task) {
        checkAuthorization(userID);
        try {
            String taskID = scheduledTaskService.addScheduledTask(userID, petID, task);
            return ResponseEntity.status(HttpStatus.CREATED).body(taskID);
        } catch (ExecutionException | InterruptedException e) {
            Thread.currentThread().interrupt(); // Restore interrupt status
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding task: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error adding task: " + e.getMessage());
        }
    }

    @GetMapping("/{taskID}")
    public ResponseEntity<ScheduledTask> getScheduledTaskById(@PathVariable String userID, @PathVariable String petID, @PathVariable String taskID) {
         checkAuthorization(userID);
         try {
              ScheduledTask task = scheduledTaskService.getScheduledTaskById(userID, petID, taskID);
              return (task != null) ? ResponseEntity.ok(task) : ResponseEntity.notFound().build();
         } catch (ExecutionException | InterruptedException e) {
              Thread.currentThread().interrupt();
              throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving task", e);
         }
    }

    // Get tasks for a specific day or date range
    @GetMapping
    public ResponseEntity<List<ScheduledTask>> getTasks(
            @PathVariable String userID,
            @PathVariable String petID,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date date, // e.g., ?date=2025-04-30
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date startDate, // e.g., ?startDate=2025-04-30T00:00:00Z
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date endDate) {
         checkAuthorization(userID);
         try {
              List<ScheduledTask> tasks;
              if (date != null) {
                   tasks = scheduledTaskService.getTasksByDate(userID, petID, Timestamp.of(date));
              } else if (startDate != null && endDate != null) {
                   tasks = scheduledTaskService.getTasksByDateRange(userID, petID, Timestamp.of(startDate), Timestamp.of(endDate));
              } else {
                   // Default: No date params means get ALL tasks for the pet
                   tasks = scheduledTaskService.getAllTasks(userID, petID);
              }
              return ResponseEntity.ok(tasks);
         } catch (ExecutionException | InterruptedException e) {
              Thread.currentThread().interrupt();
              throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving tasks", e);
         }
    }

     // Get upcoming tasks
     @GetMapping("/upcoming")
     public ResponseEntity<List<ScheduledTask>> getUpcomingTasks(
            @PathVariable String userID,
            @PathVariable String petID,
            @RequestParam(defaultValue = "5") int limit) { // Default to 5 upcoming tasks
         checkAuthorization(userID);
         try {
              List<ScheduledTask> tasks = scheduledTaskService.getUpcomingTasks(userID, petID, limit);
              return ResponseEntity.ok(tasks);
         } catch (ExecutionException | InterruptedException e) {
              Thread.currentThread().interrupt();
              throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving upcoming tasks", e);
         }
     }


    @PutMapping("/{taskID}")
    public ResponseEntity<String> updateScheduledTask(@PathVariable String userID, @PathVariable String petID, @PathVariable String taskID, @RequestBody ScheduledTask task) {
         checkAuthorization(userID);
         try {
              // Ensure the taskID from the path is used, not potentially from the body
              task.setTaskID(taskID);
              String result = scheduledTaskService.updateScheduledTask(userID, petID, taskID, task);
              return ResponseEntity.ok(result);
         } catch (ExecutionException | InterruptedException e) {
              Thread.currentThread().interrupt();
              return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating task: " + e.getMessage());
         } catch (Exception e) {
              return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error updating task: " + e.getMessage());
         }
    }

     @PatchMapping("/{taskID}/status") // Use PATCH for partial updates like status
     public ResponseEntity<String> updateTaskStatus(@PathVariable String userID, @PathVariable String petID, @PathVariable String taskID, @RequestParam ScheduledTask.TaskStatus status) {
         checkAuthorization(userID);
         try {
              String result = scheduledTaskService.updateTaskStatus(userID, petID, taskID, status);
              return ResponseEntity.ok(result);
         } catch (ExecutionException | InterruptedException e) {
              Thread.currentThread().interrupt();
              return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating task status: " + e.getMessage());
         } catch (Exception e) {
              return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error updating task status: " + e.getMessage());
         }
     }


    @DeleteMapping("/{taskID}")
    public ResponseEntity<String> deleteScheduledTask(@PathVariable String userID, @PathVariable String petID, @PathVariable String taskID) {
         checkAuthorization(userID);
         try {
              String result = scheduledTaskService.deleteScheduledTask(userID, petID, taskID);
              return ResponseEntity.ok(result);
         } catch (ExecutionException | InterruptedException e) {
              Thread.currentThread().interrupt();
              return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting task: " + e.getMessage());
         }
    }
}