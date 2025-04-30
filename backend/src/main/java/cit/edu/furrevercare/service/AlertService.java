package cit.edu.furrevercare.service;

import cit.edu.furrevercare.entity.Alert;
import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class AlertService {

    private static final String USERS_COLLECTION = "users";
    // Decide if Alerts live under pets or directly under users
    private static final String PETS_COLLECTION = "pets";
    private static final String ALERTS_COLLECTION = "alerts";
    private final Firestore firestore = FirestoreClient.getFirestore();

    // Assuming alerts are under pets for now
    private CollectionReference getAlertsCollection(String userID, String petID) {
        return firestore.collection(USERS_COLLECTION).document(userID)
                .collection(PETS_COLLECTION).document(petID)
                .collection(ALERTS_COLLECTION);
    }

     // If alerts are user-specific, use this:
     /*
     private CollectionReference getAlertsCollectionUser(String userID) {
        return firestore.collection(USERS_COLLECTION).document(userID)
                .collection(ALERTS_COLLECTION);
     }
     */

    // Method to manually add an alert (e.g., by another service)
    public String addAlert(String userID, String petID, Alert alert) throws ExecutionException, InterruptedException {
        CollectionReference alertsCollection = getAlertsCollection(userID, petID); // Or getAlertsCollectionUser(userID);
        DocumentReference alertDoc = alertsCollection.document();
        alert.setAlertID(alertDoc.getId());
        alert.setUserID(userID); // Ensure IDs are set
        alert.setPetID(petID);
        if (alert.getCreatedAt() == null) {
             alert.setCreatedAt(Timestamp.now()); // Set creation time
        }
        alertDoc.set(alert).get();
        return alert.getAlertID();
    }

    // Get alerts, optionally filtering by read status
    public List<Alert> getAlerts(String userID, String petID, Boolean readStatusFilter) throws ExecutionException, InterruptedException {
        List<Alert> alerts = new ArrayList<>();
        Query query = getAlertsCollection(userID, petID)
                     .orderBy("createdAt", Query.Direction.DESCENDING); // Show newest first

        if (readStatusFilter != null) {
             query = query.whereEqualTo("readStatus", readStatusFilter);
        }

        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        for (DocumentSnapshot document : querySnapshot.get().getDocuments()) {
             alerts.add(document.toObject(Alert.class));
        }
        return alerts;
    }

     public Alert getAlertById(String userID, String petID, String alertID) throws ExecutionException, InterruptedException {
        DocumentReference alertDoc = getAlertsCollection(userID, petID).document(alertID);
        DocumentSnapshot document = alertDoc.get().get();
        return document.exists() ? document.toObject(Alert.class) : null;
    }


    public String markAlertAsRead(String userID, String petID, String alertID) throws ExecutionException, InterruptedException {
        DocumentReference alertDoc = getAlertsCollection(userID, petID).document(alertID);
        alertDoc.update("readStatus", true).get();
        return "Alert marked as read.";
    }

    public String deleteAlert(String userID, String petID, String alertID) throws ExecutionException, InterruptedException {
        DocumentReference alertDoc = getAlertsCollection(userID, petID).document(alertID);
        alertDoc.delete().get();
        return "Alert deleted successfully.";
    }

    // Logic for *generating* alerts (e.g., based on upcoming tasks) would typically
    // be triggered elsewhere, perhaps by ScheduledTaskService or a background job,
    // which would then call addAlert().
}