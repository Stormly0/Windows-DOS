package Library.DataHandler;

import Config.Theme;
import Library.EventHandler.ErrorHandler;
import Library.GraphicsHandler.Console;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.Base64;

//TODO: Make sure to change from KeyStore to a custom made key storing solution
public class Security {
    //Private Instances
    private static final ErrorHandler ErrorHandle = new ErrorHandler(); // Error Handler of the system
    // Private Variables 
    private static int EncryptionShift = 5; // The amount of characters that the encryption table is shifted by
    private static final String Algorithm = "AES";
    private static final String Transformation = "AES/CBC/PKCS5Padding";
    private static String SecretKey = null; // The secret key that is used to encrypt the data
    private static SecretKeySpec LockedKey = null; // The locked key that is used to encrypt the data

    private static SecretKeySpec DecryptKey = null; // The secret key that is used to decrypt the data


    //Private Methods

    /**
     * Generates a spec key given a string/Password
     * @param Password - The password to generate the key from
     * @return SecretKeySpec - The secret key spec
     */

    private SecretKeySpec GenerateSpecKey(String Password){
        //Generates salt
        SecureRandom SecureRandomizer = new SecureRandom();
        byte[] Salt = new byte[64];
        SecureRandomizer.nextBytes(Salt);


        //Generates a spec key salt
        KeySpec SpecKey = new PBEKeySpec(Password.toCharArray(),Salt,65536,256);
        try{
            SecretKeyFactory KeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] KeyBytes = KeyFactory.generateSecret(SpecKey).getEncoded();
            SecretKeySpec KeySpec = new SecretKeySpec(KeyBytes,Algorithm);
            return KeySpec; //Returns the key spec
        }
        catch(NoSuchAlgorithmException | InvalidKeySpecException e){
            ErrorHandle.log("Key Generation Failed","Error: " + e.getMessage(),2);
            return null;
        }
    }

    /**
     * Encrypts a given string using AES-256
     * @param Data - The data to encrypt
     * @return Encrypted Data
     */
    private String EncryptAES(String Data){

        //Checks if the data is empty
        if(Data == null || Data.isEmpty()){
            ErrorHandle.log("File Encryption Failed","The file data is empty",1);
            return "";
        }

        try{
            Cipher Encryptor = Cipher.getInstance(Transformation);

            Encryptor.init(Cipher.ENCRYPT_MODE,LockedKey,new IvParameterSpec(new byte[16])); //Initializes the encryptor
            byte[] EncryptedBytes = Encryptor.doFinal(Data.getBytes(StandardCharsets.UTF_8)); //Encrypts the data
            return Base64.getEncoder().encodeToString(EncryptedBytes); //Returns the encrypted data
        }
        catch(NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException | InvalidAlgorithmParameterException e){
            //This should never happen because AES is a valid algorithm and literally nobody can change that
            ErrorHandle.log("Encryption Failed","Error: " + e.getMessage(), 2);
            return null;
        }
    }

    /**
     * Decrypts a given string using AES-256
     * @param Data Encrypted Data
     * @return Decrypted Data
     */
    private String DecryptAES(String Data){

        //Checks if the data is empty
        if(Data == null || Data.isEmpty()){
            ErrorHandle.log("File Decryption Failed","The file data is empty",1);
            return "";
        }

        try{
            Cipher Decryptor = Cipher.getInstance(Transformation);

            Decryptor.init(Cipher.DECRYPT_MODE,DecryptKey,new IvParameterSpec(new byte[16])); //Initializes the decrypt
            byte[] Decoded64 = Base64.getDecoder().decode(Data);
            byte[] DecryptedBytes = Decryptor.doFinal(Decoded64); //Decrypts the data
            return new String(DecryptedBytes, StandardCharsets.UTF_8); //Returns the decrypted data
        }
        catch(NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException | InvalidAlgorithmParameterException e){
            //This should never happen because AES is a valid algorithm and literally nobody can change that
            ErrorHandle.log("Decryption Failed","Error: " + e.getMessage(), 2);
            return null;
        }
    }

    // Encrypts the word using caesar cipher. uses recursion to complete the encryption
    private String Encrypt(String Word){
        
        //Base Case
        if(Word.length() == 0){
            return "";
        }

        // Gets the first letter of the word 
        char Letter = Word.charAt(0); 
    
        // Checks if the letter is a space
        if(Letter == ' '){
            return " " + Encrypt(Word.substring(1)); 
        }

        // Encrypts the letter using a predefined shift 
        Letter = (char)(Letter + EncryptionShift);

        // Returns the encrypted letter and the rest of the word 
        return Letter + Encrypt(Word.substring(1));
    }

    // Decrypts the word that was passed in using the HashKey generated. Uses recursion to complete the decryption 
    private String Decrypt(String Word){
        //Base Case
        if(Word.length() == 0){
            return "";
        }

        // Gets the first letter of the word 
        char Letter = Word.charAt(0); 

        // Checks if the letter is a space
        if(Letter == ' '){
            return " " + Decrypt(Word.substring(1)); 
        }

        // Decrypts the letter using a predefined shift 
        Letter = (char)(Letter - EncryptionShift);

        // Returns the decrypted letter and the rest of the word 
        return Letter + Decrypt(Word.substring(1));
    }

    /**
     * Generates an SHA-256 Hash Key for a given String
     * @return byte[] - The hash key
     */
    private byte[] GenerateHashKey(String Input){
        try{
            //Gets the instance method
            MessageDigest Digestor = MessageDigest.getInstance("SHA-256");
            return Digestor.digest(Input.getBytes(StandardCharsets.UTF_8));
        }
        catch(NoSuchAlgorithmException e){
            //This should never happen because SHA-256 is a valid algorithm and literally nobody can change that
            ErrorHandle.log("Invalid Hash Algorithm","The hash algorithm that was specified is invalid. Error: " + e.getMessage(), 2);
            return null;
        }
    }

    // Public Functions

    /**
     * Converts a byte array to a hex string to be used for the hash key as a string
     * @return String - The hash key as a string
     */
    public String GenerateHash(String Input){
        // Generates the Hash Key
        byte[] HashKey = GenerateHashKey(Input);

        //Checks if the hash key is null
        if(HashKey == null){
            ErrorHandle.log("Hash Key Generation Failed","The Hash key is null",2);
            return null;
        }

        //Logs the action
        ErrorHandle.action("Hash key Generated","A hashkey has been generated successfully");

        //Converts the hash key to a string
        StringBuilder HashKeyString = new StringBuilder();
        for (byte b : HashKey) {
            HashKeyString.append(String.format("%02x", b));
        }
        //Returns the hash key as a string
        return HashKeyString.toString();
    }

    /**
     * Generates a random number given the min and max values
     * @return int - Random number
     */
    public int Random(int Min, int Max){
        return (int)(Math.random() * (Max - Min + 1) + Min);
    }

    // Generates a 6-Digit security code
    public String GenerateSecurityCode(){
        String Code = ""; // Stores the security code

        // Loops through the code and generates a random number between 0 and 9
        for(int i = 0; i < 6; i++){
            Code += (int)(Math.random() * 10);
        }

        // Returns the security code
        return Code;
    }

    /**
     * Sets a new encryption key
     * @param Key - Sets the new encryption key
     */
    public void SetEncryptionKey(String Key){
        //Checks if there is no secret key or locked key already
        if(SecretKey != null || LockedKey != null){
            ErrorHandle.log("Encryption Key Already Set","The encryption key has already been set",1);
            return;
        }
        SecretKey = Key;
        LockedKey = GenerateSpecKey(SecretKey);
        DecryptKey = LockedKey; // Sets the decrypt key to the locked key
    }

    /**
     * Sets a Decrypt key that is different from the locked key which should only be used to decrypt data
     */
    public void SetDecryptKey(SecretKeySpec Key){
        DecryptKey = Key;
    }

    /**
     * Resets the decryption key to the locked key
     */
    public void ResetDecryptKey(){
        DecryptKey = LockedKey;
    }

    /**
     * Resets the encryption key to null in order to input another key
     */
    public void ResetEncryptionKey(){
        SecretKey = null;
        LockedKey = null;
    }

    /**
     * Encrypts a specified array list of data
     * @param Data - The data to be encrypted
     * @return ArrayList - The encrypted data
     */
    public ArrayList<String> EncryptListAES(ArrayList<String> Data){
        //Checks if the secret key or locked key is null
        if(SecretKey == null || LockedKey == null){
            ErrorHandle.log("Encryption Key Not Set","The encryption key has not been set",1);
            return null;
        }

        ArrayList<String> EncryptedData = new ArrayList<>(); // Stores the encrypted data

        //Loops through the data and encrypts the data
        for(String Shard : Data){
            EncryptedData.add(EncryptAES(Shard));
        }

        //Logs the action
        ErrorHandle.action("Data Encrypted","The data has been encrypted successfully");

        // Returns the encrypted data
        return EncryptedData;
    }

    /**
     * Decrypts a specified array list of data
     * @param Data - The data to be decrypted
     * @return ArrayList - The decrypted data
     */
    public ArrayList<String> DecryptListAES(ArrayList<String> Data) {
        //Checks if the secret key or locked key is null
        if (SecretKey == null || LockedKey == null) {
            ErrorHandle.log("Encryption Key Not Set", "The encryption key has not been set", 1);
            return null;
        }

        ArrayList<String> DecryptedData = new ArrayList<>(); // Stores the decrypted data

        //Loops through the data and decrypts the data
        for (String Shard : Data) {
            String DecryptedShard = DecryptAES(Shard);

            //Checks if encryption has failed
            if(DecryptedShard == null){
                //Logs that the decryption has failed
                ErrorHandle.log("Decryption Failed","The decryption has failed",1);
                return null;
            }

            DecryptedData.add(DecryptAES(Shard));
        }

        //Logs the action
        ErrorHandle.action("Data Decrypted","The data has been decrypted successfully");

        // Returns the decrypted data
        return DecryptedData;
    }

    // Encrypts a specified array list of words
    public ArrayList<String> EncryptListCaesar(ArrayList<String> File){
        ArrayList<String> EncryptedFile = new ArrayList<>(); // Stores the encrypted file

        // Stores the encrypted word
        String EncryptedWord = "";

        // Performs a temporary caeser cipher to display to the console

        // Loops through the file and encrypts each word
        for (String s : File) {
            EncryptedWord = Encrypt(s); // Encrypts the word
            EncryptedFile.add(EncryptedWord);
        }

        // Returns the encrypted file
        return EncryptedFile;
    }

    // Decrypts a specified array list of words
    public ArrayList<String> DecryptListCaesar(ArrayList<String> File){
        ArrayList<String> DecryptedFile = new ArrayList<>(); // Stores the decrypted file

        // Loops through the file and decrypts each word
        for (String s : File) {
            DecryptedFile.add(Decrypt(s));
        }

        // Returns the decrypted file
        return DecryptedFile;
    }

    // Allows the user to set an encryption key to encrypt and decrypt files with 
    public void SetEncryptionShift(String UserKey){
        int Key = 0; 
        try{
            Key =  Integer.parseInt(UserKey); // Stores the key that the user entered
        }
        catch(NumberFormatException e){
            Console.log("The key entered is too large! Please enter a smaller key", Theme.Error()); 
            return;
        }
        
        // Checks if the key entered is valid 
        if(UserKey.isEmpty() || (!UserKey.matches("^[0-9]+$"))){
            Console.log("Invalid key entered!", Theme.Error()); 
            return;
        }

        // Checks if the key is greater than 26 
        if(Integer.parseInt(UserKey) > 26){
            Key = Integer.parseInt(UserKey) % 26; // Sets the key to the remainder of the key divided by 26
        }

        // Sets the key 
        EncryptionShift = Key;
        // Informs the user 
        Console.log("Encryption shift set to: " + Key, Theme.Success());
    }

    // Returns the current encryption key 
    public int GetEncryptionKey(){
        return EncryptionShift;
    }

    /**
     * Gets the Locked Encryption Key
     * @return LockedKey - The Locked Encryption Key
     */
    public SecretKeySpec GetLockedKey(){
        return LockedKey;
    }

    /**
     * Gets the Secret Encryption Key
     * @return SecretKey - The Secret Encryption Key
     */
    public String GetSecretKey(){
        return SecretKey;
    }

    public static void main(String[] args) {
        Security Security = new Security(); // Creates a new security object

        Security.SetEncryptionKey("Hello");

        String Data = Security.EncryptAES("Hello World! This is a very long piece of data that is going to be encrypted using AES");

        System.out.println(Data);

        String DecryptedData = Security.DecryptAES(Data);

        System.out.println(DecryptedData);


    }
 
}
