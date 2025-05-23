package cit.edu.furrevercare.entity;

// import com.google.cloud.Timestamp; // Remove this import
import java.time.Instant; // Add this import

public class TreatmentPlan {
    private String planID; // Auto-generated by Firestore or Service
    private String petID;
    private String userID;
    private String name; // e.g., "Skin Allergy Management Plan"
    private String description;
    // private Timestamp startDate; // Change this
    private Instant startDate;      // To this
    // private Timestamp endDate; // Change this (Optional)
    private Instant endDate;        // To this (Optional)
    private String goal; // e.g., "Reduce itching by 80%"
    private PlanStatus status; // Enum: ACTIVE, COMPLETED, CANCELLED
    private int progressPercentage; // 0-100
    private String notes;

    // Enum for Plan Status
    public enum PlanStatus {
        ACTIVE, COMPLETED, CANCELLED
    }

    // Constructors
    public TreatmentPlan() {}

    // Modify the constructor parameter types
    public TreatmentPlan(String planID, String petID, String userID, String name, String description,
                         Instant startDate, Instant endDate, // Change Timestamp to Instant here
                         String goal, PlanStatus status, int progressPercentage, String notes) {
        this.planID = planID;
        this.petID = petID;
        this.userID = userID;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.goal = goal;
        this.status = status;
        this.progressPercentage = progressPercentage;
        this.notes = notes;
    }

    // Getters and Setters - Modify return types and parameter types

    public String getPlanID() { return planID; }
    public void setPlanID(String planID) { this.planID = planID; }

    public String getPetID() { return petID; }
    public void setPetID(String petID) { this.petID = petID; }

    public String getUserID() { return userID; }
    public void setUserID(String userID) { this.userID = userID; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    // Change Timestamp to Instant in getter and setter
    public Instant getStartDate() { return startDate; }
    public void setStartDate(Instant startDate) { this.startDate = startDate; }

    // Change Timestamp to Instant in getter and setter
    public Instant getEndDate() { return endDate; }
    public void setEndDate(Instant endDate) { this.endDate = endDate; }

    public String getGoal() { return goal; }
    public void setGoal(String goal) { this.goal = goal; }

    public PlanStatus getStatus() { return status; }
    public void setStatus(PlanStatus status) { this.status = status; }

    public int getProgressPercentage() { return progressPercentage; }
    public void setProgressPercentage(int progressPercentage) { this.progressPercentage = progressPercentage; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}