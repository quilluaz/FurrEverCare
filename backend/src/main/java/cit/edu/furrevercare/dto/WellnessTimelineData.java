// --- DTO for WellnessTimeline Response ---
package cit.edu.furrevercare.dto; // Make sure package is correct

import cit.edu.furrevercare.entity.MedicalRecord;
import cit.edu.furrevercare.entity.ScheduledTask;
import cit.edu.furrevercare.entity.TreatmentPlan; // If plans are included

import java.util.List;

public class WellnessTimelineData {
    private List<ScheduledTask> scheduledTasks;
    private List<MedicalRecord> medicalRecords; // Will be null if not fetched
    private List<TreatmentPlan> treatmentPlans; // Will be null if not fetched
    private List<ScheduledTask> upcomingTasks;
    private ProgressMetrics progressMetrics;

    // Inner class for Progress Metrics
    public static class ProgressMetrics {
        private long tasksCompletedToday;
        private long tasksTotalToday;

        public ProgressMetrics(long tasksCompletedToday, long tasksTotalToday) {
            this.tasksCompletedToday = tasksCompletedToday;
            this.tasksTotalToday = tasksTotalToday;
        }
        // Getters and Setters
        public long getTasksCompletedToday() { return tasksCompletedToday; }
        public void setTasksCompletedToday(long tasksCompletedToday) { this.tasksCompletedToday = tasksCompletedToday; }
        public long getTasksTotalToday() { return tasksTotalToday; }
        public void setTasksTotalToday(long tasksTotalToday) { this.tasksTotalToday = tasksTotalToday; }
    }

    // Getters and Setters for WellnessTimelineData
    public List<ScheduledTask> getScheduledTasks() { return scheduledTasks; }
    public void setScheduledTasks(List<ScheduledTask> scheduledTasks) { this.scheduledTasks = scheduledTasks; }
    public List<MedicalRecord> getMedicalRecords() { return medicalRecords; }
    public void setMedicalRecords(List<MedicalRecord> medicalRecords) { this.medicalRecords = medicalRecords; }
    public List<TreatmentPlan> getTreatmentPlans() { return treatmentPlans; }
    public void setTreatmentPlans(List<TreatmentPlan> treatmentPlans) { this.treatmentPlans = treatmentPlans; }
    public List<ScheduledTask> getUpcomingTasks() { return upcomingTasks; }
    public void setUpcomingTasks(List<ScheduledTask> upcomingTasks) { this.upcomingTasks = upcomingTasks; }
    public ProgressMetrics getProgressMetrics() { return progressMetrics; }
    public void setProgressMetrics(ProgressMetrics progressMetrics) { this.progressMetrics = progressMetrics; }
}