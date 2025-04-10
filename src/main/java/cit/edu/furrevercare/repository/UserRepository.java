package cit.edu.furrevercare.repository;

import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import cit.edu.furrevercare.entity.User;
import org.springframework.stereotype.Repository;

import java.util.concurrent.ExecutionException;

@Repository
public class UserRepository {

    private static final String COLLECTION_NAME = "users";

    private CollectionReference getCollection() {
        Firestore db = FirestoreClient.getFirestore();
        return db.collection(COLLECTION_NAME);
    }

    public String saveUser(User user) throws ExecutionException, InterruptedException {
        WriteResult result = getCollection().document(user.getUserID()).set(user).get();
        return "User saved at: " + result.getUpdateTime();
    }

    public User getUserById(String userID) throws ExecutionException, InterruptedException {
        DocumentSnapshot document = getCollection().document(userID).get().get();
        return document.exists() ? document.toObject(User.class) : null;
    }


    public String deleteUser(String userID) throws ExecutionException, InterruptedException {
        getCollection().document(userID).delete().get();
        return "User deleted";
    }
}
