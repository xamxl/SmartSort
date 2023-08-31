package com.example.smartsort;

import java.util.*;

public class AccountServices {

    // TODO: Add salted hash
    // Creates an account, assuming that it does not already exist and that inputs are valid
    public static void createAccount(String username, String password) {
        // Creates a FireStoreHelper and sets the file to our new account
        FireStoreHelper fSH = new FireStoreHelper();
        fSH.setFileReference("users", username);
        // Adds the password to the file, creating it
        ArrayList<String> ids = new ArrayList<>();
        ids.add("password");
        ArrayList<Object> values = new ArrayList<>();
        values.add(password);
        fSH.writeToFile(ids, values);
    }

    // Checks that a username and password for account creation are valid
    public static String checkUsernameAndPasswordCA(String username, String password, String password1) {
        // Checks if the username is already used
        FireStoreHelper fSH = new FireStoreHelper();
        fSH.setFileReference("users", username);
        if(fSH.doesFileExist())
            return "This username is already in use.";
        // Checks that your username contains an @ which is not the last character
        if (username.indexOf("@") == -1 || username.indexOf("@") == username.length() - 1)
            return "This email does not contain an \\\"@\\\" with characters after it.";
        // Checks that the two passwords are equal
        if (! password.equals(password1))
            return "The two passwords you entered are not equal.";
        // Returns that the username and password are valid
        return "VALID";
    }

    // Checks that a user name and password are valid for login
    public static boolean checkUsernameAndPasswordL(String username, String password) {
        // Checks that your username contains an @ which is not the last character
        if (username.indexOf("@") == -1 || username.indexOf("@") == username.length() - 1)
            return false;
        // Checks if the username is used
        FireStoreHelper fSH = new FireStoreHelper();
        fSH.setFileReference("users", username);
        if(! fSH.doesFileExist())
            return false;
        // Check if the password matches
        Map<String, Object> userFile = fSH.readFile();
        if (! userFile.get("password").equals(password))
            return false;
        // Returns true
        return true;
    }

    // Logs the user in, returning the login key
    public static String login(String username) {
        // Selects the user file
        FireStoreHelper fSH = new FireStoreHelper();
        fSH.setFileReference("users", username);
        // Generates a random key of length 10
        String key = generateRandomKey(10);
        // Sets the user's temporary password to key
        ArrayList<String> ids = new ArrayList<>();
        ids.add("tempPass");
        ArrayList<Object> values = new ArrayList<>();
        values.add(key);
        fSH.writeToFile(ids, values);
        // Returns the key
        return key;
    }

    // Checks that the user is validly logged in with the provided key
    public static boolean verifyLogin(String username, String key) {
        // Selects the user file
        FireStoreHelper fSH = new FireStoreHelper();
        fSH.setFileReference("users", username);
        // Checks if the key exists and matches
        Map<String, Object> userFile = fSH.readFile();
        if (! userFile.containsKey("tempPass") || ! userFile.get("tempPass").equals(key))
            return false;
        // Returns true
        return true;
    }

    // Generates a random key
    private static String generateRandomKey(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder result = new StringBuilder(length);
        // Picks characters to add to the key
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            result.append(characters.charAt(index));
        }
        // Returns the key
        return result.toString();
    }

    // Signs the user out
    public static void signout(String username) {
        // Selects the user file
        FireStoreHelper fSH = new FireStoreHelper();
        fSH.setFileReference("users", username);
        // Sets the user's temporary password to empty
        ArrayList<String> ids = new ArrayList<>();
        ids.add("tempPass");
        ArrayList<Object> values = new ArrayList<>();
        values.add("");
        fSH.writeToFile(ids, values);
    }
    
}
