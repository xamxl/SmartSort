package com.example.smartsort;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileStore;
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
    // The current collection reference
    CollectionReference collectionReference;

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

    /*
    * Sets the collection reference, which can be a top-level collection or nested within documents.
    * The path should alternate between collection names and document IDs, ending with a collection name.
    */
    public void setCollectionReference(String... path) {
        if (path.length < 1 || path.length % 2 == 0) {
            throw new IllegalArgumentException("Invalid path. Path must end with a collection name.");
        }
        collectionReference = db.collection(path[0]);
        for (int i = 1; i < path.length; i += 2) {
            collectionReference = collectionReference.document(path[i]).collection(path[i + 1]);
        }
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

    // Deletes the current collection
    public void deleteCollection() {
        // Gets the names of all files to delete
        String[] fileNames = getFileNames();
        // Loops through those files, selecting and deleting them
        for (String name : fileNames) {
            documentReference = collectionReference.document(name);
            deleteFile();
        }  
    }

    // Returns the names of all files in the current collection
    // TODO: redo other functions to make this more efficient
    public String[] getFileNames() {
        List<String> fileNamesList = new ArrayList<>();
        // asynchronously retrieve all documents
        ApiFuture<QuerySnapshot> future = collectionReference.get();
        try {
            // future.get() blocks on response
            // Gets the document id's and adds them to the list
            for (QueryDocumentSnapshot document : future.get().getDocuments()) {
                fileNamesList.add(document.getId());
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        // Convert the list to an array
        return fileNamesList.toArray(new String[0]);
    }
    
}
