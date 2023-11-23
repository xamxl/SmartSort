package com.example.smartsort;

import java.util.ArrayList;
import java.util.Arrays;

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

}
