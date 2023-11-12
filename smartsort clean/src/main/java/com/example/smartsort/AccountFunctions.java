package com.example.smartsort;

import java.util.ArrayList;
import java.util.Map;

public class AccountFunctions {

    // Increments the number of sorts or random sorts an account has completed
    // 0 = sort, 1 = random sort
    public static void incrementSortCount(String username, int type) {
        // Finds the type of the sort
        String typeS = "sort";
        if (type == 1) {
            typeS = "randomSort";
        }
        // Create an instance of FireStoreHelper and set the document reference
        FireStoreHelper fSH = new FireStoreHelper();
        fSH.setFileReference("users", username);
        // Read the current sort count value
        Map<String, Object> userData = fSH.readFile();
        Long sortCount = userData.containsKey(typeS + "Count") ? (Long) userData.get(typeS + "Count") : 0;
        // Increment the sort count
        sortCount++;
        // Prepare the data to write
        ArrayList<String> ids = new ArrayList<>();
        ids.add(typeS + "Count");
        ArrayList<Object> values = new ArrayList<>();
        values.add(sortCount);
        // Write the incremented sort count to Firestore
        fSH.writeToFile(ids, values);
        // Close the FireStoreHelper
        fSH.close();
    }
    
}
