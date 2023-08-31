package com.example.smartsort;

import java.util.ArrayList;

public class Cleaner {
    
    // The identifiers to clean and the clean identifers
    private Identifier[] identifiersToClean;
    private Identifier[] cleanIdentifers;

    // The dirty input and the column to clean
    private ArrayList<ArrayList<String>> inputToClean;
    private int dirtyColumnIndex;

    // Constructor which takes in data from reading two xlsx files
    // ArrayList<ArrayList<String>> of the cleaned data in one column
    // ArrayList<ArrayList<String>> of the dirty data in a column with other columns
    // int indexing what column the dirty data is in 
    public Cleaner(ArrayList<ArrayList<String>> cleanIdentifers, ArrayList<ArrayList<String>> identifiersToClean, int dirtyColumnIndex) {
        // Fills this.cleanIdentifers with Identifiers made from cleanIdentifers
        this.cleanIdentifers = new Identifier[cleanIdentifers.size()];
        for (int i = 0; i < cleanIdentifers.size(); i++) {
            this.cleanIdentifers[i] = new Identifier(cleanIdentifers.get(i).get(0));
        }
        // Fills this.identifiersToClean with Identifiers made from dirtyColumnIndex column of identifiersToClean
        this.identifiersToClean = new Identifier[identifiersToClean.size()];
        for (int i = 0; i < identifiersToClean.size(); i++) {
            this.identifiersToClean[i] = new Identifier(identifiersToClean.get(i).get(dirtyColumnIndex));
        }
        inputToClean = identifiersToClean;
        this.dirtyColumnIndex = dirtyColumnIndex;
    }

    // Cleans all Identifiers
    private void cleanAllIdentifiers() {
        // Loops through all dirty Identifiers
        for (Identifier identifierToClean : identifiersToClean) {
            // Loops through all clean Identifiers
            for (Identifier cleanIdentifer : cleanIdentifers) {
                // Checks if the current Identifier is a good match
                // If the match is exact moves onto the next dirty Identifier
                if (identifierToClean.compareIdentifiers(cleanIdentifer)) {
                    break;
                }
            }
        }
    }

    // Cleans the dirty input and returns the changes through reference
    public void cleanInput() {
        cleanAllIdentifiers();
        // Goes through all the specified cells and replaces them with the cleaned output
        for (int r = 0; r < inputToClean.size(); r++) {
            inputToClean.get(r).set(dirtyColumnIndex, identifiersToClean[r].getOutput());
        }
    }

}
