package cit.edu.furrevercare.service;

import cit.edu.furrevercare.entity.TreatmentPlan;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class TreatmentPlanService {

    private static final String USERS_COLLECTION = "users";
    private static final String PETS_COLLECTION = "pets";
    private static final String PLANS_COLLECTION = "treatmentPlans";
    private final Firestore firestore = FirestoreClient.getFirestore();

     private CollectionReference getPlansCollection(String userID, String petID) {
        return firestore.collection(USERS_COLLECTION).document(userID)
                .collection(PETS_COLLECTION).document(petID)
                .collection(PLANS_COLLECTION);
    }

    public String addTreatmentPlan(String userID, String petID, TreatmentPlan plan) throws ExecutionException, InterruptedException {
        DocumentReference planDoc = getPlansCollection(userID, petID).document();
        plan.setPlanID(planDoc.getId());
        plan.setUserID(userID);
        plan.setPetID(petID);
        if (plan.getStatus() == null) {
            plan.setStatus(TreatmentPlan.PlanStatus.ACTIVE); // Default status
        }
        planDoc.set(plan).get();
        return plan.getPlanID();
    }

     public TreatmentPlan getTreatmentPlanById(String userID, String petID, String planID) throws ExecutionException, InterruptedException {
        DocumentReference planDoc = getPlansCollection(userID, petID).document(planID);
        DocumentSnapshot document = planDoc.get().get();
        return document.exists() ? document.toObject(TreatmentPlan.class) : null;
    }

     public List<TreatmentPlan> getTreatmentPlans(String userID, String petID, TreatmentPlan.PlanStatus statusFilter) throws ExecutionException, InterruptedException {
        List<TreatmentPlan> plans = new ArrayList<>();
        Query query = getPlansCollection(userID, petID);

        // Optionally filter by status (e.g., get only ACTIVE plans)
        if (statusFilter != null) {
            query = query.whereEqualTo("status", statusFilter);
        }

        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        for (DocumentSnapshot document : querySnapshot.get().getDocuments()) {
            plans.add(document.toObject(TreatmentPlan.class));
        }
        return plans;
     }


    public String updateTreatmentPlan(String userID, String petID, String planID, TreatmentPlan plan) throws ExecutionException, InterruptedException {
        DocumentReference planDoc = getPlansCollection(userID, petID).document(planID);
        plan.setPlanID(planID); // Ensure IDs are correct
        plan.setUserID(userID);
        plan.setPetID(petID);
        planDoc.set(plan, SetOptions.merge()).get(); // Merge to update
        return "Treatment plan updated successfully!";
    }

     public String updatePlanProgress(String userID, String petID, String planID, int progressPercentage) throws ExecutionException, InterruptedException {
        DocumentReference planDoc = getPlansCollection(userID, petID).document(planID);
         if (progressPercentage < 0 || progressPercentage > 100) {
             throw new IllegalArgumentException("Progress percentage must be between 0 and 100.");
         }
         planDoc.update("progressPercentage", progressPercentage).get();
         // Optionally update status if progress reaches 100%
         // TreatmentPlan plan = getTreatmentPlanById(userID, petID, planID);
         // if (progressPercentage == 100 && plan != null && plan.getStatus() == TreatmentPlan.PlanStatus.ACTIVE) {
         //     planDoc.update("status", TreatmentPlan.PlanStatus.COMPLETED).get();
         // }
        return "Treatment plan progress updated successfully!";
    }

    public String deleteTreatmentPlan(String userID, String petID, String planID) throws ExecutionException, InterruptedException {
         DocumentReference planDoc = getPlansCollection(userID, petID).document(planID);
         planDoc.delete().get();
        return "Treatment plan deleted successfully!";
    }
}