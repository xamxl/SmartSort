package com.example.smartsort;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.*;

public class FireStoreHelper {

    // A fire store database
    Firestore db;

    // The current document reference
    DocumentReference documentReference;

    // Gets an instance of the data base
    public FireStoreHelper() {
        try {
            // Gets credentials from file
            FileInputStream serviceAccount = new FileInputStream ("./smart-sort-392323-9924fe9e7db8.json");
            GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);
            // Creates fire store instance and saves it to db
            FirestoreOptions firestoreOptions =
            FirestoreOptions.getDefaultInstance().toBuilder()
                .setProjectId("smart-sort-392323")
                .setCredentials(credentials)
                .build();
            db = firestoreOptions.getService();
        } catch (IOException e) {
            System.out.println("FireStoreHelper could not be constructed.");
        }
    }

    // Sets the document reference to a new file
    public void setFileReference(String collectionID, String fileID) {
        documentReference = db.collection(collectionID).document(fileID);
    }

    // Checks if a file exits
    public boolean doesFileExist() {
        try {
            // Gets the file
            ApiFuture<DocumentSnapshot> future = documentReference.get();
            DocumentSnapshot documentSnapshot = future.get(); // This will block until the result is available
            // Checks and returns the output
            return documentSnapshot.exists();
        } catch (InterruptedException | ExecutionException e) {
            System.out.println("doesFileExist has failed.");
        }
        return false;
    }

    // Writes to the current file using id Strings and values of type Object
    public void writeToFile(ArrayList<String> ids, ArrayList<Object> values) {
        // Creates a new Map and fills it with the provided values
        Map<String, Object> data = new HashMap<>();
        for (int i = 0; i < ids.size(); i++)
            data.put(ids.get(i), values.get(i));
        // Writes to the file
        // A file is created if none exists
        documentReference.set(data);
    }  

    public Map<String, Object> readFile() {
        // Asynchronously retrieve the document
        ApiFuture<DocumentSnapshot> future = documentReference.get();
        // Gets the file
        DocumentSnapshot document = null;
        try {
            document = future.get();
        } catch (InterruptedException | ExecutionException e) {
            System.out.println("readFile has failed.");
        }
        return document.getData();
    }
    
}
