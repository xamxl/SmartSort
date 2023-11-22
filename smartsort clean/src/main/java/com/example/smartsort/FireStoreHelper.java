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
            FileInputStream serviceAccount = new FileInputStream ("./smart-sort-392323-c1a78e38811c.json");
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

    // Closes the FireStoreHelper
    public void close() {
        try {
            db.close();
        } catch (Exception e) {}
    }

    /*
     * Sets the document reference to a document, which can be nested.
     * The path should alternate between collection names and document IDs
     */
    public void setFileReference(String... path) {
        if (path.length < 2 || path.length % 2 != 0) {
            throw new IllegalArgumentException("Invalid path. Path must be of even length.");
        }

        documentReference = db.collection(path[0]).document(path[1]);
        for (int i = 2; i < path.length; i += 2) {
            documentReference = documentReference.collection(path[i]).document(path[i + 1]);
        }
    }

    // Checks if a file exits
    public boolean doesFileExist() {
        try {
            System.out.println();
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

    // Writes to the current file using id Strings and values of type Object, creating it if it does not exist
    public void writeToFile(ArrayList<String> ids, ArrayList<Object> values) {
        // Creates a new Map and fills it with the provided values
        Map<String, Object> data = new HashMap<>();
        for (int i = 0; i < ids.size(); i++)
            data.put(ids.get(i), values.get(i));
        // Checks if the document exists
        if (doesFileExist()) {
            // Update the document if it exists
            documentReference.update(data);
        } else {
            // Set the document if it doesn't exist
            documentReference.set(data);
        }
    }

    // Reads the current file
    public Map<String, Object> readFile() {
        // Asynchronously retrieve the document
        ApiFuture<DocumentSnapshot> future = documentReference.get();
        // Gets the file
        DocumentSnapshot document = null;
        try {
            document = future.get();
        } catch (InterruptedException | ExecutionException e) {}
        return document.getData();
    }

    // Deletes the current file
    public void deleteFile() {
        // Asynchronously delete the document
        ApiFuture<WriteResult> future = documentReference.delete();
        try {
            // Confirm the deletion
            future.get();
        } catch (InterruptedException | ExecutionException e) {}
    }    
    
}
