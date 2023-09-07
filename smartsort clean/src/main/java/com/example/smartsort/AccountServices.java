package com.example.smartsort;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.*;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

// TODO: Read through all of this to make sure it is safe
public class AccountServices {

    // Creates an account, assuming that it does not already exist and that inputs are valid
    public static void createAccount(String username, String password) {
        // Creates a FireStoreHelper and sets the file to our new account
        FireStoreHelper fSH = new FireStoreHelper();
        fSH.setFileReference("users", username);
        // Adds the hashed password and its salt to the file, creating it
        ArrayList<String> ids = new ArrayList<>();
        ids.add("passwordHash");
        ids.add("salt");
        ArrayList<Object> values = new ArrayList<>();
        String[] hashAndSalt = {};
        try {
            hashAndSalt = generateHashedPassword(password);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        values.add(hashAndSalt[0]);
        values.add(hashAndSalt[1]);
        fSH.writeToFile(ids, values);
        // Closes the FireStoreHelper
        fSH.close();
    }

    // Creates and returns a hashed password and its salt
    public static String[] generateHashedPassword(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        // Generates the salt
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        // Preps for the hash and adds the salt
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        // Generates the hash
        byte[] hash = factory.generateSecret(spec).getEncoded();
        // Converts the hash and salt to strings and returns it
        return new String[]{Base64.getEncoder().encodeToString(hash), Base64.getEncoder().encodeToString(salt)};
    }

    // Creates and returns a hashed password
    public static String generateHashWithSalt(String password, String saltStr) throws NoSuchAlgorithmException, InvalidKeySpecException {
        // Decode the salt string to byte array
        byte[] salt = Base64.getDecoder().decode(saltStr);
        // Prep for the hash
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        // Generate the hash
        byte[] hash = factory.generateSecret(spec).getEncoded();
        // Convert hash to string and return
        return Base64.getEncoder().encodeToString(hash);
    }

    // Checks that a username and password for account creation are valid
    public static String checkUsernameAndPasswordCA(String username, String password, String password1) {
        // Checks if the username is already used
        FireStoreHelper fSH = new FireStoreHelper();
        fSH.setFileReference("users", username);
        if(fSH.doesFileExist()) {
            // Closes the FireStoreHelper
            fSH.close();
            return "This username is already in use.";
        }
        // Closes the FireStoreHelper
        fSH.close();
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
        if(! fSH.doesFileExist()) {
            // Closes the FireStoreHelper
            fSH.close();
            return false;
        }
        // Check if the password matches
        Map<String, Object> userFile = fSH.readFile();
        // Closes the FireStoreHelper
        fSH.close();
        try {
            if (! userFile.get("passwordHash").equals(generateHashWithSalt(password, userFile.get("salt").toString()))) {
                return false;
            }
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            return false;
        }
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
        // Closes the FireStoreHelper
        fSH.close();
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
        // Closes the FireStoreHelper
        fSH.close();
        if (userFile == null || ! userFile.containsKey("tempPass") || ! userFile.get("tempPass").equals(key) || userFile.get("tempPass").equals("")) {
            return false;
        }
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
        // Closes the FireStoreHelper
        fSH.close();
    }

    // Deletes the account
    public static void deleteAccount(String username) {
        // Selects the user file
        FireStoreHelper fSH = new FireStoreHelper();
        fSH.setFileReference("users", username);
        // Deletes the file
        fSH.deleteFile();
        // Closes the FireStoreHelper
        fSH.close();
    }
    
}
