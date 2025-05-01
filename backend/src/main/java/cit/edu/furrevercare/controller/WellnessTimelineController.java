package cit.edu.furrevercare.controller;

import cit.edu.furrevercare.dto.WellnessTimelineData; // Need to create this DTO
import cit.edu.furrevercare.entity.MedicalRecord;
import cit.edu.furrevercare.entity.ScheduledTask;
import cit.edu.furrevercare.service.MedicalRecordService;
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


import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users/{userID}/pets/{petID}/wellnessTimeline")
public class WellnessTimelineController {

    @Autowired
    private ScheduledTaskService scheduledTaskService;

    @Autowired
    private MedicalRecordService medicalRecordService; // May need medical records too

    // Inject other services like TreatmentPlanService if needed

     // Helper method for authorization check
     private void checkAuthorization(String userID) {
         Authentication auth = SecurityContextHolder.getContext().getAuthentication();
         if (auth == null || !auth.getName().equals(userID)) {
             throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized access to user resources");
         }
    }


    @GetMapping
    public ResponseEntity<WellnessTimelineData> getTimelineData(
            @PathVariable String userID,
            @PathVariable String petID,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date startDate, // Require start and end dates
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date endDate) {

        checkAuthorization(userID);

        try {
            Timestamp startTimestamp = Timestamp.of(startDate);
            Timestamp endTimestamp = Timestamp.of(endDate);

            // 1. Fetch Scheduled Tasks in the range
            List<ScheduledTask> tasks = scheduledTaskService.getTasksByDateRange(userID, petID, startTimestamp, endTimestamp);

            // 2. Fetch Medical Records in the range (Assuming MedicalRecord needs a date field)
            // NOTE: Your MedicalRecord entity currently lacks a date field. You'd need to add one (e.g., 'visitDate' or 'recordDate')
            // and update the service to query by it. For now, we'll skip this part.
            // List<MedicalRecord> records = medicalRecordService.getMedicalRecordsByDateRange(userID, petID, startTimestamp, endTimestamp);

            // 3. Fetch Treatment Plan info if relevant (e.g., start/end dates falling in range)
            // List<TreatmentPlan> plans = treatmentPlanService.getActivePlansInRange(...) // Requires new service method

            // 4. Fetch Upcoming tasks (using existing service method)
            List<ScheduledTask> upcomingTasks = scheduledTaskService.getUpcomingTasks(userID, petID, 5); // Get next 5

            // 5. Calculate Progress Metrics (Example: Tasks completed today)
            // This logic might be more complex depending on requirements
             long completedTodayCount = tasks.stream()
                 .filter(task -> task.getStatus() == ScheduledTask.TaskStatus.COMPLETED && isToday(task.getCompletedAt()))
                 .count();
             long pendingTodayCount = tasks.stream()
                 .filter(task -> task.getStatus() == ScheduledTask.TaskStatus.PENDING && isToday(task.getScheduledDateTime()))
                 .count();
             WellnessTimelineData.ProgressMetrics metrics = new WellnessTimelineData.ProgressMetrics(completedTodayCount, pendingTodayCount + completedTodayCount);


            // 6. Construct the DTO
            WellnessTimelineData timelineData = new WellnessTimelineData();
            timelineData.setScheduledTasks(tasks);
            // timelineData.setMedicalRecords(records); // Add when MedicalRecord has date
            timelineData.setUpcomingTasks(upcomingTasks);
            timelineData.setProgressMetrics(metrics);


            return ResponseEntity.ok(timelineData);

        } catch (ExecutionException | InterruptedException e) {
             Thread.currentThread().interrupt();
             throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving timeline data", e);
        } catch (Exception e) {
             throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error retrieving timeline data: " + e.getMessage(), e);
        }
    }

     // Helper to check if a Firestore Timestamp corresponds to today
     private boolean isToday(Timestamp ts) {
         if (ts == null) return false;
         long nowMillis = System.currentTimeMillis();
         long tsMillis = ts.toDate().getTime();
         long dayInMillis = 24 * 60 * 60 * 1000;
         // Simple check: if timestamp falls within today's boundaries (in UTC)
         return (tsMillis / dayInMillis) == (nowMillis / dayInMillis);
     }
}