package com.example.smartsort;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.List;

public class Forms {

    // Returns the file reference for a form's document
    public static FireStoreHelper getFormReference (String user, String formName) {
        // Create an instance of FireStoreHelper and set the document reference
        FireStoreHelper fSH = new FireStoreHelper();
        fSH.setFileReference("users", user, "forms", formName);
        return fSH;
    }
    
    // Returns true if the form exists, false otherwise
    public static boolean doesFormExist(String user, String formName) {
        // Gets the reference to the form's document
        FireStoreHelper fSH = getFormReference(user, formName);
        // Checks if the file exists
        return fSH.doesFileExist();
    }

    // Creates a new form
    public static void createForm(String user, String formName, String[] sortTypes, String[] options, String[] texts, String idInstruct, String[] inputTypes) {
        // Gets the reference ot the form's document
        FireStoreHelper fSH = getFormReference(user, formName);
        // Collects the ids
        ArrayList<String> ids = new ArrayList<>();
        ids.add("formName");
        ids.add("sortTypes");
        ids.add("options");
        ids.add("texts");
        ids.add("idInstruct");
        ids.add("inputTypes");
        // Collects the values
        ArrayList<Object> values = new ArrayList<>();
        values.add(formName);
        values.add(new ArrayList<>(Arrays.asList(sortTypes)));
        values.add(new ArrayList<>(Arrays.asList(options)));
        values.add(new ArrayList<>(Arrays.asList(texts)));
        values.add(idInstruct);
        values.add(new ArrayList<>(Arrays.asList(inputTypes)));
        // Creates and writes to the file
        fSH.writeToFile(ids, values);
    }

    // Returns all the names of the user's forms
    // TODO: Make sure this works if the user has no forms or no collection of forms
    public static String[] getUserFormNames(String user) {
        // Creates an instance of FireStoreHelper & sets it to the user's form collection
        FireStoreHelper fSH = new FireStoreHelper();
        fSH.setCollectionReference("users", user, "forms");
        // Gets and returns the names of all the user's forms
        return fSH.getFileNames();
    }

    // Deletes a form
    public static void deleteForm(String user, String formName) {
        // Gets a reference to the form
        FireStoreHelper fSH = getFormReference(user, formName);
        // Deletes the file
        fSH.deleteFile();
    }

    // Returns the contents of a form
    public static Map<String, Object> getForm(String user, String formName) {
        // Gets the reference to the form's document
        FireStoreHelper fSH = getFormReference(user, formName);
        // Reads the file and returns its content
        return fSH.readFile();
    }

    // Writes a submission to a form
    public static void writeSubmission(String user, String formName, ArrayList<String> submission) {
        // Gets the reference to the submission's new file
        FireStoreHelper fSH = new FireStoreHelper();
        fSH.setFileReference("users", user, "forms", formName, "submissions", submission.get(0));
        // Creates a new submission file and writes to it
        ArrayList<Object> data = new ArrayList<>();
        data.add(submission);
        ArrayList<String> key = new ArrayList<>();
        key.add("data");
        fSH.writeToFile(key, data);
    }

    // Returns true if a response contains a unique name & refers to a form that exists
    public static boolean validIdentifierAndForm(String user, String formName, ArrayList<String> submission) {
        // Returns false if the id is empty
        if (submission.get(0).length() == 0)
            return false;
        // Selects the form
        FireStoreHelper fSH = new FireStoreHelper();
        fSH.setFileReference("users", user, "forms", formName);
        // Returns false if the form does not exist
        if (! fSH.doesFileExist())
            return false;
        // Selects the non existent response
        fSH.setFileReference("users", user, "forms", formName, "submissions", submission.get(0));
        // Returns false if the name has already been used
        return ! fSH.doesFileExist();
    }

    // Returns true if a response if properly formatted
    public static boolean validResponse(String user, String formName, ArrayList<String> submission) {
        // Selects the form
        FireStoreHelper fSH = new FireStoreHelper();
        fSH.setFileReference("users", user, "forms", formName);
        // Gets the length of the form
        int formLength = ((List<?>) fSH.readFile().get("inputTypes")).size();
        // Returns true if the result is the right length, false otherwise
        return (formLength + 1) == submission.size();
    }
}
