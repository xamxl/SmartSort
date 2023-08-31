package com.example.smartsort;

public class Identifier {
    
    // The input identifier and the clean version
    private String input;
    private String output;

    // The number of letter pairs and letters different then the output, -1 by default
    private int outputTotalDifference;

    // The number of each letter
    private int[] numAlpha;

    // The number of each letter pair
    private int[] numAlphaPair;

    // Constructor which takes in the input
    public Identifier(String input) {
        this.input = input;
        this.output = input;
        outputTotalDifference = -1;
        createNumAlpha();
        createNumAlphaPair();
    }

    // Creates and fills numAlpha
    private void createNumAlpha() {
        numAlpha = new int[26];
        for (char c : input.toCharArray()) {
            if (Character.isLetter(c)) {
                char lowerC = Character.toLowerCase(c); // Convert the character to lowercase
                int index = lowerC - 'a'; // Calculate the index in the array for this letter
                if (index >= 0 && index < numAlpha.length) {
                    numAlpha[index]++; // Increment the count for this letter
                }
            }
        }
    }

    // Creates and fills numAlphaPair
    private void createNumAlphaPair() {
        numAlphaPair = new int[26 * 26];
        char[] chars = input.toLowerCase().toCharArray();
        for (int i = 0; i < chars.length - 1; i++) {
            char c1 = chars[i];
            char c2 = chars[i + 1];
            if (Character.isLetter(c1) && Character.isLetter(c2)) {
                int index1 = c1 - 'a';
                int index2 = c2 - 'a';
                if (index1 >= 0 && index1 < 26 && index2 >= 0 && index2 < 26) {
                    int index = index1 * 26 + index2; // Calculate the index in the array for this letter pair
                    numAlphaPair[index]++; // Increment the count for this letter pair
                }
            }
        }
    }

    // Returns the difference between the number of letters and letter pairs two Identifiers have
    private int lettersAndPairDifference(Identifier otherIdentifier) {
        int differenceCount = 0;

        // Iterate through the numAlpha arrays of both identifiers
        // and find the difference count for each letter
        for (int i = 0; i < this.numAlpha.length; i++) {
            differenceCount += Math.abs(this.numAlpha[i] - otherIdentifier.numAlpha[i]);
        }

        // Iterate through the numAlphaPair arrays of both identifiers
        // and find the difference count for each letter pair
        for (int i = 0; i < this.numAlphaPair.length; i++) {
            differenceCount += Math.abs(this.numAlphaPair[i] - otherIdentifier.numAlphaPair[i]);
        }

        return differenceCount;
    }

    // Returns the length of the maximum substring between two Identifiers
    public int longestCommonSubstring(Identifier otherIdentifier) {
        String str1 = input.toLowerCase();
        String str2 = otherIdentifier.input.toLowerCase();
    
        int m = str1.length();
        int n = str2.length();
        int[][] dp = new int[m + 1][n + 1];
        int maxLength = 0;
        int endingIndex = -1;
    
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (str1.charAt(i - 1) == str2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                    if (dp[i][j] > maxLength) {
                        maxLength = dp[i][j];
                        endingIndex = i;
                    }
                }
            }
        }
    
        return (endingIndex != -1) ? str1.substring(endingIndex - maxLength, endingIndex).length() : 0;
    }


    // If an Identifier is better then the current output, resets it
    // Returns true if an exact match is found
    public boolean compareIdentifiers(Identifier otherIdentifier) {
        // If an exact math is found sets it as the output and returns true
        if (input == otherIdentifier.input) {
            outputTotalDifference = 0;
            output = input;
            return true;
        }
        // If this identifier has more letters in common then the current output, resets the output
        int dif = lettersAndPairDifference(otherIdentifier);
        // Adds the distance to the longest possible substring to dif
        int difToLongestSubstring = input.length() - longestCommonSubstring(otherIdentifier);
        dif += difToLongestSubstring;

        if (outputTotalDifference == -1 || dif < outputTotalDifference) {
            outputTotalDifference = dif;
            output = otherIdentifier.input;
        }
        return false;
    }

    // Returns the output
    public String getOutput() {
        return output;
    }

}