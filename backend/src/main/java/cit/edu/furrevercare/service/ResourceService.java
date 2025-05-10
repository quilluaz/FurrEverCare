package cit.edu.furrevercare.service;

import cit.edu.furrevercare.entity.Resource;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class ResourceService {

    private static final String USERS_COLLECTION = "users";
    private static final String RESOURCES_SUBCOLLECTION = "resources"; // Subcollection under each user
    private final Firestore firestore;

    public ResourceService(Firestore firestore) {
        this.firestore = firestore;
    }

    private CollectionReference getResourcesCollection(String userID) {
        return firestore.collection(USERS_COLLECTION).document(userID).collection(RESOURCES_SUBCOLLECTION);
    }

    public Resource createResource(String userID, Resource resource) throws ExecutionException, InterruptedException {
        // Ensure userID is set on the resource object
        resource.setUserID(userID);

        DocumentReference documentReference = getResourcesCollection(userID).document();
        resource.setResourceID(documentReference.getId()); // Set the auto-generated ID
        documentReference.set(resource).get(); // Use .get() to wait for completion
        return resource; // Return the created resource with its ID
    }

    public List<Resource> getAllResourcesByUserId(String userID) throws ExecutionException, InterruptedException {
        List<Resource> resources = new ArrayList<>();
        ApiFuture<QuerySnapshot> future = getResourcesCollection(userID).get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        for (QueryDocumentSnapshot document : documents) {
            resources.add(document.toObject(Resource.class));
        }
        return resources;
    }

    public Resource getResourceById(String userID, String resourceID) throws ExecutionException, InterruptedException {
        DocumentReference documentReference = getResourcesCollection(userID).document(resourceID);
        ApiFuture<DocumentSnapshot> future = documentReference.get();
        DocumentSnapshot document = future.get();
        if (document.exists()) {
            return document.toObject(Resource.class);
        } else {
            return null; // Or throw a ResourceNotFoundException
        }
    }

    public Resource updateResource(String userID, String resourceID, Resource resource) throws ExecutionException, InterruptedException {
        // Ensure IDs are consistent
        resource.setUserID(userID);
        resource.setResourceID(resourceID);

        DocumentReference documentReference = getResourcesCollection(userID).document(resourceID);
        // Check if document exists before updating (optional, set() will create if not exists)
        // DocumentSnapshot snapshot = documentReference.get().get();
        // if (!snapshot.exists()) {
        //     throw new RuntimeException("Resource not found with ID: " + resourceID); // Or handle appropriately
        // }
        documentReference.set(resource).get(); // Overwrites the document
        return resource; // Return the updated resource
    }

    public String deleteResource(String userID, String resourceID) throws ExecutionException, InterruptedException {
        ApiFuture<WriteResult> writeResult = getResourcesCollection(userID).document(resourceID).delete();
        // You can inspect writeResult.get() if needed, or just assume success if no exception
        writeResult.get();
        return "Resource with ID " + resourceID + " deleted successfully.";
    }
}