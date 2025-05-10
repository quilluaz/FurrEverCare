package cit.edu.furrevercare.service;

import cit.edu.furrevercare.entity.ScheduledTask;
import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class ScheduledTaskService {

    private static final String USERS_COLLECTION = "users";
    private static final String PETS_COLLECTION = "pets";
    private static final String TASKS_COLLECTION = "scheduledTasks";
    private final Firestore firestore = FirestoreClient.getFirestore();

    private CollectionReference getTasksCollection(String userID, String petID) {
        return firestore.collection(USERS_COLLECTION).document(userID)
                .collection(PETS_COLLECTION).document(petID)
                .collection(TASKS_COLLECTION);
    }

    public String addScheduledTask(String userID, String petID, ScheduledTask task) throws ExecutionException, InterruptedException {
        DocumentReference taskDoc = getTasksCollection(userID, petID).document();
        task.setTaskID(taskDoc.getId());
        task.setUserID(userID); // Ensure userID and petID are set
        task.setPetID(petID);
        if (task.getStatus() == null) {
            task.setStatus(ScheduledTask.TaskStatus.PENDING); // Default status
        }
        taskDoc.set(task).get(); // Use .get() to wait for completion
        // Add logic here to schedule alerts if needed (e.g., using AlertService)
        return task.getTaskID();
    }

    public ScheduledTask getScheduledTaskById(String userID, String petID, String taskID) throws ExecutionException, InterruptedException {
        DocumentReference taskDoc = getTasksCollection(userID, petID).document(taskID);
        DocumentSnapshot document = taskDoc.get().get();
        return document.exists() ? document.toObject(ScheduledTask.class) : null;
    }

    // Get tasks for a specific date range (e.g., for the tracker/timeline)
    public List<ScheduledTask> getTasksByDateRange(String userID, String petID, Timestamp startDate, Timestamp endDate) throws ExecutionException, InterruptedException {
        List<ScheduledTask> tasks = new ArrayList<>();
        Query query = getTasksCollection(userID, petID)
                .whereGreaterThanOrEqualTo("scheduledDateTime", startDate)
                .whereLessThanOrEqualTo("scheduledDateTime", endDate)
                .orderBy("scheduledDateTime", Query.Direction.ASCENDING); // Order by time

        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        for (DocumentSnapshot document : querySnapshot.get().getDocuments()) {
            tasks.add(document.toObject(ScheduledTask.class));
        }
        // TODO: Add logic here to fetch recurring tasks that fall within the range
        return tasks;
    }

     // Get tasks for a specific date (useful for daily tracker view)
     public List<ScheduledTask> getTasksByDate(String userID, String petID, Timestamp date) throws ExecutionException, InterruptedException {
         // Firestore doesn't directly support querying just by date part of Timestamp easily.
         // A common workaround is to query for a 24-hour range.
         Timestamp startOfDay = Timestamp.of(date.toDate()); // Assumes date is start of day
         Timestamp endOfDay = Timestamp.ofTimeSecondsAndNanos(startOfDay.getSeconds() + (24*60*60) -1, 999999999);

        return getTasksByDateRange(userID, petID, startOfDay, endOfDay);
     }


    public List<ScheduledTask> getUpcomingTasks(String userID, String petID, int limit) throws ExecutionException, InterruptedException {
         List<ScheduledTask> tasks = new ArrayList<>();
         Query query = getTasksCollection(userID, petID)
                 .whereGreaterThanOrEqualTo("scheduledDateTime", Timestamp.now()) // Tasks from now onwards
                 .whereEqualTo("status", ScheduledTask.TaskStatus.PENDING) // Only pending tasks
                 .orderBy("scheduledDateTime", Query.Direction.ASCENDING)
                 .limit(limit);

         ApiFuture<QuerySnapshot> querySnapshot = query.get();
         for (DocumentSnapshot document : querySnapshot.get().getDocuments()) {
             tasks.add(document.toObject(ScheduledTask.class));
         }
         return tasks;
    }

    // Get all tasks for a pet, ordered by scheduled time
    public List<ScheduledTask> getAllTasks(String userID, String petID) throws ExecutionException, InterruptedException {
        List<ScheduledTask> tasks = new ArrayList<>();
        Query query = getTasksCollection(userID, petID)
                .orderBy("scheduledDateTime", Query.Direction.ASCENDING); // Order by time

        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        for (DocumentSnapshot document : querySnapshot.get().getDocuments()) {
            tasks.add(document.toObject(ScheduledTask.class));
        }
        return tasks;
    }

    public String updateScheduledTask(String userID, String petID, String taskID, ScheduledTask task) throws ExecutionException, InterruptedException {
        DocumentReference taskDoc = getTasksCollection(userID, petID).document(taskID);
        // Ensure IDs are not overwritten if task object is reused from request body
        task.setTaskID(taskID);
        task.setUserID(userID);
        task.setPetID(petID);
        taskDoc.set(task, SetOptions.merge()).get(); // Merge to update fields without overwriting others
        return "Task updated successfully!";
    }

     public String updateTaskStatus(String userID, String petID, String taskID, ScheduledTask.TaskStatus status) throws ExecutionException, InterruptedException {
        DocumentReference taskDoc = getTasksCollection(userID, petID).document(taskID);
        DocumentSnapshot snapshot = taskDoc.get().get();
        if (!snapshot.exists()) {
            return "Task not found.";
        }

        WriteBatch batch = firestore.batch();
        batch.update(taskDoc, "status", status);

        if (status == ScheduledTask.TaskStatus.COMPLETED) {
            batch.update(taskDoc, "completedAt", Timestamp.now());
        } else {
             // Ensure completedAt is removed if status changes from COMPLETED
             batch.update(taskDoc, "completedAt", FieldValue.delete());
        }

        batch.commit().get();
        return "Task status updated successfully!";
    }

    public String deleteScheduledTask(String userID, String petID, String taskID) throws ExecutionException, InterruptedException {
        DocumentReference taskDoc = getTasksCollection(userID, petID).document(taskID);
        taskDoc.delete().get();
        return "Task deleted successfully!";
    }

    // TODO: Implement logic for handling recurring tasks.
    // This might involve creating future instances upon completion of a recurring task
    // or querying based on the recurrence rule and start date.
}