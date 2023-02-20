package Library.DataHandler;

import Config.Theme;
import Core.SystemStatus;
import Library.EventHandler.ErrorHandler;
import Library.GraphicsHandler.Console;
import org.json.simple.JSONObject;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

// This class is the representation of the data that is stored from within the system's user database
// tl;dr: This class represents the user directory

public final class DataShard extends DataHistory{
    //Private Instances 
    private final static FileHandler FileHandle = new FileHandler(); // Creates a new file handler instance
    private final static ErrorHandler ErrorHandle = new ErrorHandler(); // Creates a new error handler instance
    private final static Security SecurityHandle = new Security(); // Creates a new security handler instance

    //Private Variables
    private boolean Locked = false; // Indicates whether the setter functions can be used or not
    private static File ParentFolder = null; // The parent folder of the user directory
    private static File DataFolder = null; // The data folder of the user directory

    //private File
    private static File SelectedDirectory = null; // The selected directory of the user directory
    private static File SelectedFile = null; // The selected file that is being referenced
    private static ArrayList<String> FileData = new ArrayList<>(); // The data stored in the current selected file

    private static ArrayList<String> FileDataCache = new ArrayList<>(); // The data stored in the current selected file that does not change as it stores the original contents of the file
    private final static ArrayList<File> DirectoryPath = new ArrayList<>(); // The directory path of the user directory that the user has navigated through. Allows the user to go back

    //** ENCRYPTION VARIABLES **\\
    private static File EncryptedFilesParentPath = null; // The parent path of the encrypted files
    private static JSONObject EncryptedFiles = null; // The encrypted files that are stored in the user directory
    private static File KeyStorePath = null; // The path of the key store that is used to store the keys that are used to encrypt the files

    //Constructor
    public DataShard(){

        // Checks if the current user has logged in
        if(!SystemStatus.LoggedIn){
            this.Locked = true; // Locks the setter functions
            return; // Returns the function since the user has not logged in
        }

        // Sets the current selected directory to the user's directory 
        ParentFolder = (SystemStatus.OperatingSystem.equalsIgnoreCase("windows")) ? new File("src\\Users\\" + SystemStatus.CurrentUsername) : new File("Users/" + SystemStatus.CurrentUsername);

        // Sets the current Data folder of the current user's directory
        DataFolder = (SystemStatus.OperatingSystem.equalsIgnoreCase("windows")) ? new File("src\\Users\\" + SystemStatus.CurrentUsername + "\\Data") : new File("Users/" + SystemStatus.CurrentUsername + "/Data");

        // Sets the selected Directory to the current data folder of the user
        SelectedDirectory = new File(DataFolder.getPath()); // Creates a new file since we do not want a reference

        //Checks if the current directory path is empty
        if(DirectoryPath.isEmpty()){
            this.SetDirectory(SelectedDirectory); // Sets the current selected directory to the user's directory
           // DirectoryPath.add(SelectedDirectory); // Adds the current selected directory to the directory path history
        }

        // Gets the encrypted files that are stored in the user directory
        EncryptedFilesParentPath = (SystemStatus.OperatingSystem.equalsIgnoreCase("windows")) ? new File("src\\Users\\" + SystemStatus.CurrentUsername + "\\System\\Log\\EncryptedFiles.json") : new File("Users/" + SystemStatus.CurrentUsername + "/System/Log/EncryptedFiles.json");
        EncryptedFiles = FileHandle.ReadJSON(EncryptedFilesParentPath);
        KeyStorePath = (SystemStatus.OperatingSystem.equalsIgnoreCase("windows")) ? new File("src\\Users\\" + SystemStatus.CurrentUsername + "\\System\\Log\\KeyStore.jks") : new File("Users/" + SystemStatus.CurrentUsername + "/System/Log/KeyStore.jks");
    }

    // The constructor that indicates that the set functions are not allowed to be used
    public DataShard(boolean Locked){
        this.Locked = Locked;
    }

    //Private Methods

    /**
     * Adds an encrypted file to the encrypted file list which also updates the encrypted file list on the system
     * @param CurrentFile The file that is being encrypted
     */
    private void AddEncryptedFile(File CurrentFile) {
        // Adds the data into the encrypted files
        LinkedHashMap<String, Object> Metadata = new LinkedHashMap<>(); // Creates a new linked hash map

        // Puts in the metadata of the file
        Metadata.put("Path", SelectedFile.getPath()); // Adds the path to the JSON object
        Metadata.put("Key", SecurityHandle.GenerateHash(SecurityHandle.GetSecretKey())); // Adds the key to the JSON object

        try {
            // Saves the key that is used to encrypt the file into the KeyStore
            KeyStore KeyStorage = KeyStore.getInstance(KeyStore.getDefaultType());

            char[] Password = SecurityHandle.GenerateHash(SecurityHandle.GetSecretKey()).toCharArray(); // Gets the password of the key store
            SecretKey Secret = SecurityHandle.GetLockedKey(); // Gets the secret key that is used to encrypt the file
            KeyStore.SecretKeyEntry SecretEntry = new KeyStore.SecretKeyEntry(Secret); // Creates a new secret key entry

            // Loads the key store file or creates a new keystore file if one does not exist
            try (FileInputStream KeyStoreStreamIn = new FileInputStream(KeyStorePath)) {
                KeyStorage.load(KeyStoreStreamIn, Password);
            } catch (FileNotFoundException e) {
                KeyStorage.load(null, Password);
                //Creates a new keystore file
                ErrorHandle.action("Key Store created","New Keystore file created at " + KeyStorePath.getPath()); // Logs the action
            }

            // Adds the key to the key store
            KeyStorage.setEntry(CurrentFile.getPath(), SecretEntry, new KeyStore.PasswordProtection(Password));

            // Puts the secret key into the key store
            try (FileOutputStream KeyStoreStreamOut = new FileOutputStream(KeyStorePath)) {
                KeyStorage.store(KeyStoreStreamOut, Password);
            }catch(FileNotFoundException e){
                // Logs the error
                ErrorHandle.log("Key Store Failed", "KeyStore access Rejected due to FileNotFoundException | Error: " + e.getMessage(), 3);
            }
        } catch (KeyStoreException e) {
            ErrorHandle.log("Key Store Failed", "KeyStore access Rejected due to KeyStoreException | Error: " + e.getMessage(), 3);
            return;
        } catch (IOException e) {
            ErrorHandle.log("Key Store Failed", "KeyStore access Rejected due to IOException | Error: " + e.getMessage(), 3);
            return;
        } catch (NoSuchAlgorithmException e) {
            ErrorHandle.log("Key Store Failed", "KeyStore access Rejected due to NoSuchAlgorithmException | Error: " + e.getMessage(), 3);
            return;
        } catch (Exception e) {
            ErrorHandle.log("Key Store Failed", "KeyStore access Rejected due to unknown reasons | Error: " + e.getMessage(), 3);
            return;
        }

        // Adds the json object to the encrypted file list
        EncryptedFiles.put(CurrentFile.getName(), Metadata);

        // Writes to the JSON file
        FileHandle.WriteJSON(EncryptedFilesParentPath, EncryptedFiles); // Writes the JSON object to the JSON file
    }

    /**
     * Removes an encrypted file from the encrypted file list which also updates the encrypted file list on the system
     * @param CurrentFile The file that is being decrypted
     */
    public void RemoveEncryptedFile(File CurrentFile){
        //Removes the key from the KeyStore

        try{
            //Saves the key that is used to encrypt the file into the KeyStore
            KeyStore KeyStorage = KeyStore.getInstance(KeyStore.getDefaultType());

            FileInputStream KeyStoreStreamIn = new FileInputStream(KeyStorePath); //Gets the key store file
            FileOutputStream KeyStoreStreamOut = new FileOutputStream(KeyStorePath); //Gets the key store file

            char[] Password = SecurityHandle.GetSecretKey().toCharArray();

            //Gets the secret key that was used to encrypt the password
            KeyStorage.load(KeyStoreStreamIn,Password); // Loads the key store file

            //Removes the key from the key store
            KeyStorage.deleteEntry(SecurityHandle.GetSecretKey()); // Removes the secret key from the key store

            // Puts the secret key into the key store
            KeyStorage.store(KeyStoreStreamOut,Password); // Stores the key store file

            //Closes the streams
            KeyStoreStreamIn.close();
            KeyStoreStreamOut.close();
        }
        catch(KeyStoreException e){
            ErrorHandle.log("Key Store Failed", "KeyStore access Rejected due to KeyStoreException | Error: " + e.getMessage(),3);
            return;
        }
        catch(IOException e){
            ErrorHandle.log("Key Store Failed", "KeyStore access Rejected due to IOException | Error: " + e.getMessage(),3);
            return;
        }
        catch(NoSuchAlgorithmException e){
            ErrorHandle.log("Key Store Failed", "KeyStore access Rejected due to NoSuchAlgorithmException | Error: " + e.getMessage(),3);
            return;
        }
        catch(Exception e){
            ErrorHandle.log("Key Store Failed", "KeyStore access Rejected due to unknown reasons | Error: " + e.getMessage(),3);
            return;
        }

        //Removes the data from the encrypted files
        EncryptedFiles.remove(CurrentFile.getName()); //Removes the file from the encrypted files

        //Writes to the JSON file
        FileHandle.WriteJSON(EncryptedFilesParentPath,EncryptedFiles); //Writes the JSON object to the JSON file
    }

    /**
     * Gets the key from the keystore
     * @return Key - The key that is used to encrypt the file
     */
    private Key GetSecretKey(String Alias,String Password){
        try{
            //Initializes the keystore
            KeyStore KeyStorage = KeyStore.getInstance(KeyStore.getDefaultType());

            //Loads the file
            KeyStorage.load(new FileInputStream(KeyStorePath),Password.toCharArray());

            //Gets the key
            Key SecretKey = KeyStorage.getKey(Alias,Password.toCharArray());
            return SecretKey;
        }
        catch(Exception e){
            ErrorHandle.log("Key Store Failed", "KeyStore access Rejected due to unknown reasons | Error: " + e.getMessage(),3);
            return null;
        }
    }

    // Public Methods

    /**
     * Initializes the datashard by setting the current user directory and the current data folder
     */
    public void Initialize(){
        // Unlocks the datashard
        // Sets the locked variable to false
        this.Locked = false;

        // Sets the current selected directory to the user's directory
        ParentFolder = (SystemStatus.OperatingSystem.equalsIgnoreCase("windows")) ? new File("src\\Users\\" + SystemStatus.CurrentUsername) : new File("Users/" + SystemStatus.CurrentUsername);

        // Sets the current Data folder of the current user's directory
        DataFolder = (SystemStatus.OperatingSystem.equalsIgnoreCase("windows")) ? new File("src\\Users\\" + SystemStatus.CurrentUsername + "\\Data") : new File("Users/" + SystemStatus.CurrentUsername + "/Data");

        // Sets the selected Directory to the current data folder of the user
        SelectedDirectory = new File(DataFolder.getPath()); // Creates a new file since we do not want a reference

        //Checks if the current directory path is empty
        if(DirectoryPath.isEmpty()){
            this.SetDirectory(SelectedDirectory); // Sets the current directory to the selected directory
            //DirectoryPath.add(SelectedDirectory); // Adds the current selected directory to the directory path history
        }

        // Gets the encrypted files that are stored in the user directory
        EncryptedFilesParentPath = (SystemStatus.OperatingSystem.equalsIgnoreCase("windows")) ? new File("src\\Users\\" + SystemStatus.CurrentUsername + "\\System\\Log\\EncryptedFiles.json") : new File("Users/" + SystemStatus.CurrentUsername + "/System/Log/EncryptedFiles.json");
        EncryptedFiles = FileHandle.ReadJSON(EncryptedFilesParentPath);
        KeyStorePath = (SystemStatus.OperatingSystem.equalsIgnoreCase("windows")) ? new File("src\\Users\\" + SystemStatus.CurrentUsername + "\\System\\Log\\KeyStore.jks") : new File("Users/" + SystemStatus.CurrentUsername + "/System/Log/KeyStore.jks");
    }

    /**
     * Uninitializes the datashard by setting the current user directory and the current data folder to null
     */
    public void Uninitialize(){
        // Locks the datashard
        this.Locked = true;

        // Sets all the variables to null
        ParentFolder = null;
        DataFolder = null;
        SelectedDirectory = null;
        SelectedFile = null;
        EncryptedFiles = null;

        //Flushes out the file data caches
        FileDataCache.clear();
        FileData.clear();
        DirectoryPath.clear();

        //Flushes out all changes that were made
        DiscardChanges();
    }

    /**
     * Checks if a directory exists in the users directory
     * @param Name Name of the directory
     * @return boolean - <i>Indicates whether a directory exists or not</i>
     */
    public boolean CheckDirectoryExists(String Name){
        // Checks if the name passed in has a file extension
        if(Name.matches(".+(\\.\\w+)")){
            Console.log("File extension is not allowed",Theme.Error());
            ErrorHandle.log("File Path","File extension is not allowed for the path to a directory! | Path: " + Name,2); // Logs the error
            return false; // Returns false if the name has a file extension
        }

        String CurrentPath = SelectedDirectory.getPath() + "\\" + Name; // Gets the current path of the user directory

        return FileHandle.CheckPath(CurrentPath); // Checks if the user directory exists
    }

    /**
     * Checks if a file exists in the users directory
     * @param Name Name of the file
     * @return boolean - <i>Indicates whether a file exists or not</i>
     */
    public boolean CheckFileExists(String Name){
        // Checks if the name passed in has a file extension
        if(!Name.matches(".+(\\.\\w+)")){
            Console.log("File extension is required",Theme.Error());
            ErrorHandle.log("File Path","File extension is required for the path to a file! | Path: " + Name,2); // Logs the error
            return false; // Returns false if the name has a file extension
        }

        String CurrentPath = (SystemStatus.OperatingSystem.equalsIgnoreCase("windows")) ? "src\\Users\\" + SystemStatus.CurrentUsername + "\\Data\\" + Name : "Users/" + SystemStatus.CurrentUsername + "/Data/" + Name; // Gets the current path of the user directory
        return FileHandle.CheckPath(CurrentPath); // Checks if the user directory exists
    }

    /**
     * Checks if the user directory exists
     * @param Name Name of the directory
     * @return boolean - <i>Indicates whether a file directory exists or not</i>
     */
    public boolean CheckUserDirectory(String Name){
        // Checks if the name passed in has a file extension
        if(Name.matches(".+(\\.\\w+)")){
            Console.log("File extension is not allowed",Theme.Error());
            ErrorHandle.log("File Path","File extension is not allowed for the path to a directory! | Path: " + Name,2); // Logs the error
            return false; // Returns false if the name has a file extension
        }

        String CurrentPath = (SystemStatus.OperatingSystem.equalsIgnoreCase("windows")) ? "src\\Users\\" + Name : "Users/" + Name; // Gets the current path of the user directory
        return FileHandle.CheckPath(CurrentPath); // Checks if the user directory exists
    }

    /**
     * Checks if the user file exists
     * @param Name Name of the file
     * @return boolean - <i>Indicates whether the file exists or not</i>
     */
    public boolean CheckUserFile(String Name){
        // Checks if the name passed in has a file extension
        if(!Name.matches(".+(\\.\\w+)")){
            // Replaces the ending of the file name with a .txt file extension
            // Removes the ending of the file name and replaces it with a .txt file extension
            Name = Name.substring(0,Name.lastIndexOf(".")) + ".txt";
        }

        String CurrentPath = (SystemStatus.OperatingSystem.equalsIgnoreCase("windows")) ? "src\\Users\\" + SystemStatus.CurrentUsername + "\\Data\\" + Name : "Users/" + SystemStatus.CurrentUsername + "/Data/" + Name; // Gets the current path of the user directory
        return FileHandle.CheckPath(CurrentPath); // Checks if the user directory exists
    }

    // Creates a new user Directory
    public boolean CreateDirectory(String Name){
        // Creates a new user directory
        try{
            File NewDirectory = new File(SelectedDirectory,Name); // Creates a new file object
            boolean Status = NewDirectory.mkdir(); // Creates the directory

            // Checks if the directory was created
            if(!Status){
                // Logs the error
                ErrorHandle.log("Directory Creation Failed",Name + " already exists as a directory!",2); // Logs the error
                return false; // Returns false if the directory was not created
            }

            // Logs the action that the user has done
            ErrorHandle.action("Created Directory","New Directory " + Name + " has been created");
            return true; // Returns true if the directory was created
        }
        catch(Exception e){
            // Logs the error
            ErrorHandle.log("Directory Creation Error",e.getMessage(),3);
            return false; // Returns false if the directory was not created
        }
    }

    // Common Methods \\

    //Checks if the current selected file is a root directory
    public boolean IsRootDirectory(){
        if(SelectedDirectory == null){
            ErrorHandle.log("Root_Dir_Check_Rejected","No directory selected",1); // Logs the error
            return false; // Returns false if the user has not selected a directory
        }

        // Checks if the selected directory is a root directory
        // Returns false if the selected directory is not a root directory
        // Returns true if the selected directory is a root directory
        return SelectedDirectory.getPath().matches("src\\\\Users\\\\" + SystemStatus.CurrentUsername + "\\\\Data") || SelectedDirectory.getPath().matches("Users/" + SystemStatus.CurrentUsername + "/Data");
    }

    // Creates a new file
    public boolean CreateFile(String Name){
        //Checks if it already has a txt file extension
        if(!Name.matches(".+(\\.\\w+)")){
            // Replaces the ending of the file name with a .txt file extension
            Name = Name + ".txt";
        }

        // Creates a new file
        return FileHandle.Create(Name,SelectedDirectory);
    }

    // Adds data to the selected file
    public void Add(String Data){
        // Checks if there is a selected file
        if(SelectedFile == null){
            ErrorHandle.log("File Add Rejected","No file selected",1); // Logs the error
            return; // Returns if the user has not selected a file
        }

        //Checks if the file is encrypted
        if(EncryptedFiles.containsKey(SelectedFile.getName())){
            // Logs the error
            ErrorHandle.log("File Add Rejected","File is encrypted",1);
            return; // Returns if the file is encrypted
        }

        // Adds data to the file cache
        FileData.add(Data);

        // Adds the change to the file cache
        AddChange(Data);

        // Logs the action that the user has done
        ErrorHandle.action("Data Add Request","Data has been added to the file cache");
    }

    // Removes data from the selected file
    public void Remove(String Data){
        //Checks if there is a selected file
        if(SelectedFile == null){
            ErrorHandle.log("File Remove Rejected","No file selected",1); // Logs the error
            return; // Returns if the user has not selected a file
        }

        // Checks if the data is in the file cache
        if(FileData.contains(Data)){
            FileData.remove(Data); // Removes the data from the file cache

            // Removes the change from the file cache
            RemoveChange(Data);

            // Logs the action that the user has done
            ErrorHandle.action("Data Remove Request","Data has been removed from the file cache");
        }
        else{
            // Logs the error
            ErrorHandle.log("Data Remove Request","Data is not in the file cache",1);
        }
    }

    /**
     * Deletes a file when given a file name
     * @return boolean - <i>Indicates whether the file was deleted or not</i>
     */
    public boolean DeleteFile(File SelectedFile){
        //Checks if the file exists
        if(!CheckFileExists(SelectedFile.getName())){
            ErrorHandle.log("File Select Delete Rejected","File does not exists",1); // Logs the error
            return false;
        }

        //Deletes the file
        return FileHandle.Delete(SelectedFile);
    }

    //Deletes the current selected file
    public boolean DeleteFile(){
        //Checks if the user has selected a file
        if(SelectedFile == null){
            ErrorHandle.log("File Delete Rejected","No file selected",1); // Logs the error
            return false; // Returns if the user has not selected a file
        }

        boolean Status = FileHandle.Delete(SelectedFile); // Deletes the current selected file

        //Discards all changes
        DiscardChanges();

        //Sets the current selected file to null as the file has been deleted
        SelectedFile = null;

        // Returns the status of the file deletion
        return Status;
    }

    //Deletes the current selected directory
    public boolean DeleteDirectory(){
        //Checks if the user has selected a directory
        if(SelectedDirectory == null){
            ErrorHandle.log("Directory Delete Rejected","No directory selected",1); // Logs the error
            return false; // Returns if the user has not selected a directory
        }

        // Deletes the current selected directory
        boolean Status = FileHandle.Delete(SelectedDirectory);

        //Removes the current directory path from the directory path list
        DirectoryPath.remove(DirectoryPath.size() - 1);

        //Sets the current selected directory to the previous directory
        SelectedDirectory = new File(DirectoryPath.get(DirectoryPath.size() - 1).getPath());

        return Status;
    }

    // Moves the current selected file to a new directory
    public boolean MoveFile(File NewDirectory){
        // Checks if the user has selected a file
        if(SelectedFile == null){
            ErrorHandle.log("File Move Rejected","No file selected",1); // Logs the error
            return false; // Returns if the user has not selected a file
        }

        boolean Status = FileHandle.Move(SelectedFile,NewDirectory); // Moves the current selected file to a new directory

        //Sets the current selected file to null as the file has been moved
        SelectedFile = null;

        return Status;
    }

    //Discards the changes that have been made to the file
    public void Discard(){
        // Checks if the user has selected a file
        if(SelectedFile == null){
            ErrorHandle.log("File Discard Rejected","No file selected",1); // Logs the error
            return; // Returns if the user has not selected a file
        }

        // Checks if any changes have been made to the file
        if(GetNumberOfChanges() == 0){
            ErrorHandle.log("File Discard Rejected","No changes have been made to the file",1); // Logs the error
            Console.log("No changes have been made to " + SelectedFile.getName(),Theme.Error());
            return; // Returns if no changes have been made to the file
        }

        // Discards the changes that have been made to the file
        FileData = new ArrayList<>(FileDataCache);

        //Discards all changes made to the file
        DiscardChanges();

        // Logs the action that the user has done
        ErrorHandle.action("File Discard Request","Changes have been discarded");
    }

    // Saves the current data into the user's current selected file
    public boolean Save(){
        // Checks if the user has selected a file
        if(SelectedFile == null){
            ErrorHandle.log("File Save Rejected","No file selected",1); // Logs the error
            return false; // Returns if the user has not selected a file
        }

        // Checks if any changes have been made to the file
        if(GetNumberOfChanges() == 0){
            ErrorHandle.log("File Save Rejected","No changes have been made to the file",1); // Logs the error
            //Checks if debug mode is enabled
            if(SystemStatus.DebugMode){
                Console.log("No changes have been made to " + SelectedFile.getName(),Theme.Error());
            }
            return false; // Returns if no changes have been made to the file
        }

        // Sets the changes to zero as the file has been saved
        DiscardChanges();

        // Sets the last modified date of the file to the current date
        SetLastModifiedDate(SelectedFile);

        // Gets the current selected file and writes the data into the file
        return FileHandle.Write(SelectedFile.getAbsolutePath(),FileData); // Writes the data into the file
    }

    // Saves the current data as a new file
    public boolean SaveAs(String FileName){
        // Checks if the user has selected a file
        if(SelectedFile == null){
            ErrorHandle.log("File Save Rejected","No file selected",1); // Logs the error
            return false; // Returns if the user has not selected a file
        }

        // Saves the file as a new file
        return FileHandle.WriteAs(FileName,SelectedFile,FileData); // Writes the data into the file
    }

    /**
     * Encrypts the current selected file
     * @return boolean - <i>Indicates whether the file was encrypted or not</i>
     */
    public boolean EncryptFile(){
        // Checks if the user has selected a file
        if(SelectedFile == null){
            ErrorHandle.log("File Encryption Rejected","No file selected",1); // Logs the error
            return false; // Returns if the user has not selected a file
        }

        //Checks if there is an encryption key set
        if(SecurityHandle.GetLockedKey() == null){
            ErrorHandle.log("File Encryption Rejected","No encryption key set",1); // Logs the error
            return false; // Returns if there is no encryption key set
        }

        //Checks to make sure that the parent folder of the file is Data within the user directory and not in the system's root directory
        // This is just to make sure that the program isn't encrypting anything that it isn't supposed to because you probably won't be able to decrypt it once it has been encrypted
        if(!SelectedFile.getPath().matches("^src\\\\Users\\\\Guest\\\\Data\\\\.+$")) {
            ErrorHandle.log("File Encryption Rejected", "File is not in the Data folder", 1); // Logs the error
            return false; // Returns if the file is not in the Data folder
        }

        // Encrypts the selected file
        ArrayList<String> EncryptedData = SecurityHandle.EncryptListAES(FileData);

        //Checks if the encrypted data is null
        if(EncryptedData == null || EncryptedData.isEmpty()){
            ErrorHandle.log("File Encryption Rejected","Encrypted data is null or empty",1); // Logs the error
            return false; // Returns if the encrypted data is null or empty
        }

        // Writes the encrypted data into the file
        boolean Status = FileHandle.Write(SelectedFile,EncryptedData);

        //Checks the status
        if(Status){
            // Logs the action that the user has done
            ErrorHandle.action("File Encryption Request","File has been encrypted");

            // Adds the file to the list of encrypted files
            AddEncryptedFile(SelectedFile); // Adds the file to the list of encrypted files
        }
        else{
            ErrorHandle.log("File Encryption Rejected","File could not be encrypted",1); // Logs the error
        }
        return Status;
    }

    /**
     * Decrypts the current selected file
     * @return boolean - <i>Indicates whether the file was decrypted or not</i>
     */
    public boolean DecryptFile(String Password){
        // Checks if the user has selected a file
        if(SelectedFile == null){
            ErrorHandle.log("File Decryption Rejected","No file selected",1); // Logs the error
            return false; // Returns if the user has not selected a file
        }

        //Checks if there is an encryption key set
        if(SecurityHandle.GetLockedKey() == null){
            ErrorHandle.log("File Decryption Rejected","No encryption key set",1); // Logs the error
            return false; // Returns if there is no encryption key set
        }

        //Checks to make sure that the parent folder of the file is Data within the user directory and not in the system's root directory
        // This is just to make sure that the program isn't encrypting anything that it isn't supposed to because you probably won't be able to decrypt it once it has been encrypted
        if(!SelectedFile.getPath().matches("^src\\\\Users\\\\Guest\\\\Data\\\\.+$")){
            ErrorHandle.log("File Decryption Rejected","File is not in the Data folder",1); // Logs the error
            return false; // Returns if the file is not in the Data folder
        }

        //Creates a hash key
        String HashKey = SecurityHandle.GenerateHash(Password);

        //Gets the decryption key from the keystore
        SecretKeySpec DecryptionKey = (SecretKeySpec) GetSecretKey(SelectedFile.getPath(),HashKey); // Gets the decryption key from the keystore

        //Sets the decryption key to the security handle
        SecurityHandle.SetDecryptKey(DecryptionKey);

        // Decrypts the selected file
        ArrayList<String> DecryptedData = SecurityHandle.DecryptListAES(FileData);

        //Resets the decryption key
        SecurityHandle.ResetDecryptKey();

        //Checks if the decrypted data is null
        if(DecryptedData == null || DecryptedData.isEmpty()){
            ErrorHandle.log("File Decryption Rejected","Decrypted data is null or empty",1); // Logs the error
            return false; // Returns if the decrypted data is null or empty
        }

        // Writes the decrypted data into the file
        boolean Status = FileHandle.Write(SelectedFile,DecryptedData);

        //Checks the status
        if(Status){
            // Logs the action that the user has done
            ErrorHandle.action("File Decryption Request",SelectedFile.getName() + " has been decrypted");

            // Removes the file from the list of encrypted files
            RemoveEncryptedFile(SelectedFile); // Removes the file from the list of encrypted files
        }
        else{
            ErrorHandle.log("File Decryption Rejected",SelectedFile.getName() + " could not be decrypted",1); // Logs the error
        }
        return Status;
    }

    /**
     * Sets an encryption key
     * @param Key - <i>The encryption key that is used to encrypt the file</i>
     * @return
     */
    public void SetEncryptionKey(String Key){
        SecurityHandle.SetEncryptionKey(Key);
    }

    /**
     * Removes the encryption key
     */
    public void RemoveEncryptionKey(){
        SecurityHandle.ResetEncryptionKey();
    }

    //Checks whether the file is a directory or a file
    public boolean IsDirectory(String Filename){
        //Checks whether the file is a directory or a file
        File file = new File(SelectedDirectory,Filename); //Creates a new file object
        return file.isDirectory(); //Returns whether the file is a directory or a file
    }

    //Checks whether the file is a file
    public boolean IsFile(String Filename){
        File file = new File(SelectedDirectory,Filename); //Creates a new file object
        return file.isFile(); //Returns whether the file is a file
    }

    // Setters \\

    // Sets the current selected file to the file that is being selected
    public void SetFile(String FileName){
        //Checks if the locked variable is true
        if(Locked){
            ErrorHandle.log("File Selection Rejected","Datashard is locked",1); // Logs the error
            return; // Returns if the file is locked
        }

        // Checks if a directory is selected
        if(SelectedDirectory == null){
            ErrorHandle.log("File Selection Rejected","No directory selected",1); // Logs the error
            return; // Returns if no directory is selected
        }

        SelectedFile = new File(SelectedDirectory,FileName); // Sets the current selected file to the file that is being selected

        //Sets the last accessed file to the current selected file
        SetLastAccessedDate(SelectedFile);

        // Reads the data from the selected file
        FileData = FileHandle.Read(SelectedFile); // Reads the data from the selected file

        FileDataCache = new ArrayList<>(FileData); // Creates a new file data cache
    }

    // Sets the current selected directory to the directory that is being selected
    public void SetDirectory(String DirectoryName){
        // Checks the shard is locked
        if(Locked){
            ErrorHandle.log("Directory Selection Rejected","Datashard is locked",1); // Logs the error
            return; // Returns if the file is locked
        }

        // Resets the selected file to null as the user is changing file directories
        SelectedFile = null;

        //Gets the last value in the directory path
        File DataFolder = DirectoryPath.get(DirectoryPath.size() - 1);

        // Sets the current selected directory to the directory that is being selected
        SelectedDirectory = new File(DataFolder,DirectoryName);

        // Sets the last accessed date of the directory to the current date
        SetLastAccessedDate(SelectedDirectory);

        // Adds the files in the directory to the file list
        DirectoryPath.add(SelectedDirectory);

    }

    //Sets a specific directory given the directory file object
    public void SetDirectory(File Directory){
        // Checks the shard is locked
        if(Locked){
            ErrorHandle.log("Directory Selection Rejected","Datashard is locked",1); // Logs the error
            return; // Returns if the file is locked
        }

        // Resets the selected file to null as the user is changing file directories
        SelectedFile = null;

        //Checks the current operating system to determine the file path hierarchy
        String Regex = (SystemStatus.OperatingSystem.equalsIgnoreCase("Windows")) ? "\\\\" : "/";


        //Splits the directory path into an array
        String[] DirectoryPathHistory = Directory.getPath().split(Regex);


        //Clears the directory path
        DirectoryPath.clear();

        boolean DataDirFound = false; // Sets the data directory found variable to false as the data directory has not been found
        String FormattedPath = ""; // Creates a new formatted path variable
        // Adds the files in the directory to the file list
        for(String Path : DirectoryPathHistory){

            // Checks if the data directory has been found
            if(!DataDirFound && Path.equalsIgnoreCase("Data")){
                DataDirFound = true; // Sets the data directory found variable to true as the data directory has been found
            }

            //Checks if the data directory is found
            if(DataDirFound){
                // Adds the data directory to the formatted path
                FormattedPath += Path + ((SystemStatus.OperatingSystem.equalsIgnoreCase("Windows")) ? "\\" : "/");

                // Selects the first directory in the data directory path (Data since the data directory is the root directory)
                SelectedDirectory = new File(ParentFolder,FormattedPath);

                DirectoryPath.add(SelectedDirectory); // Adds the path to the directory path
            }
        }

        // Sets the selected directory to the directory that is being selected
        SelectedDirectory = Directory;
    }

    // Returns to the previous directory
    public String ReturnDirectory(){
        // Checks if the directory path is empty
        if(DirectoryPath.isEmpty()) {
            // Logs the error
            ErrorHandle.log("Directory Path", "Directory path is empty", 2); // Logs the error
            return null; // Returns if the directory path is empty
        }

        // Gets the last directory in the directory path
        String LastDirectory = DirectoryPath.get(DirectoryPath.size() - 1).getName(); // Gets the last directory in the directory path

        // Removes the last directory in the directory path
        DirectoryPath.remove(DirectoryPath.size() - 1); // Removes the last directory in the directory path

        // Sets the current selected file to null
        SelectedFile = null;

        // Checks if the directory path is empty
        if(DirectoryPath.isEmpty()){
            // Sets the selected directory to null as the user is in the root directory
            SelectedDirectory = DataFolder;
            return SelectedDirectory.getName(); // Returns if the directory path is empty
        }

        // Sets the selected directory to the last directory in the directory path
        SelectedDirectory = DirectoryPath.get(DirectoryPath.size() - 1); // Sets the selected directory to the last directory in the directory path
        return LastDirectory; // Returns the last directory in the directory path
    }

    // Getters \\

    //Gets the current selected file's encryption status
     public boolean IsEncrypted(){
        return EncryptedFiles.containsKey(SelectedFile.getName());
     }

    //Gets the current selected file
    public File GetCurrentSelectedFile(){
        return SelectedFile;
    }

    //Gets the current directory that the user is in
    public File GetCurrentSelectedDirectory(){
        return SelectedDirectory;
    }

    //Gets the current directory path that the user has taken
    public ArrayList<File> GetCurrentDirectoryPath(){
        return DirectoryPath;
    }

    //Gets the current selected file contents
    public ArrayList<String> GetCurrentFileData(){
        return FileData;
    }

    // Gets the changes that have been made to the file
    public int ChangesMade(){
        return GetNumberOfChanges();
    }

    //Gets all file directories in the current user account
    public ArrayList<File> GetAllFileDirectories(File Folder,ArrayList<File> Cache){
        //Checks if the folder is null
        if(Folder == null || Folder.listFiles() == null){
            ErrorHandle.log("File Directory Retrieval Rejected","Folder is null",1); // Logs the error
            return Cache; // Returns if the folder is null
        }

        //Checks if the cache contains the root directory already
        if(!Cache.contains(DataFolder) && Cache.size() == 0){
            Cache.add(DataFolder); // Adds the root directory to the cache
        }

        // Lists all the directories in the data folder and subdirectories and adds them to the cache
        for(File file : Folder.listFiles()){
            // Checks if the file is a directory
            if(file.isDirectory()){
                Cache.add(file); // Adds the file to the cache
                GetAllFileDirectories(file,Cache); // Recursively calls the method
            }
        }
        return Cache;
    }

    //Gets the current data directory file
    public File GetDataDirectory(){
        return DataFolder;
    }

    // Gets the file Data from within the selected file
    public ArrayList<String> GetFileData(){
        return FileHandle.Read(SelectedFile.getPath()); // Returns the file data from within the selected file
    }

    // Gets the files in the current selected directory and returns it as a LinkedHashMap
    public LinkedHashMap<File, HashMap<String,String>> ListDirectory(){
        // Cache HashMap
        LinkedHashMap<File,HashMap<String,String>> Cache = new LinkedHashMap<>();

        // Checks if the current selected directory is null
        if(SelectedDirectory == null){
            // logs the error
            ErrorHandle.log("Selected Directory is null","Cannot List Data files when directory is null",2); // Logs the error
            return null;
        }

        // Gets the files in the current selected directory
        File[] Files = SelectedDirectory.listFiles();

        // Checks whether the files in the current selected directory is null
        if(Files == null){
            // Logs the error
            ErrorHandle.log("Selected Directory is null","Unknown cause for Files being null",2); // Logs the error
            return null;
        }

        // Loops through the files and checks whether it is a file or another directory
        for(File File : Files){
            HashMap<String,String> Template = new HashMap<>(); // Stores the template that stores the metadata of a file

            Template.put("Creation Date",GetCreationDate(File)); // Gets the Creation Date of the file
            Template.put("Last Modified Date",GetLastModifiedDate(File)); // Gets the Last Modified Date of the file
            Template.put("File Size",GetFileSize(File)); // Gets the File Size of the file
            Template.put("Last Accessed",GetLastAccessedDate(File)); // Gets the Last Accessed Date of the file
            Template.put("Absolute Path",File.getAbsolutePath()); // Gets the Absolute Path of the file
            Template.put("Path",File.getPath()); // Gets the Path of the file
            Template.put("Name",File.getName()); // Gets the Name of the file

            //Checks if the file is encrypted
            if(EncryptedFiles.containsKey(File.getName())){
                Template.put("Cryptography","Encrypted"); // Sets the cryptography of the file to encrypted
            }
            else if(!EncryptedFiles.containsKey(File.getName())){
                Template.put("Cryptography","Decrypted"); // Sets the cryptography of the file to unencrypted
            }
            else{
                Template.put("Cryptography","Unknown"); // Sets the cryptography of the file to unknown
            }


            // Checks whether the file is a directory
            if(File.isDirectory()){
                Template.put("Type","Directory"); // Sets the type of the file to directory
                // Adds the file to the cache
                Cache.put(File,Template);
            }else{
                Template.put("Type","File"); // Sets the type of the file to file
                // Adds the file to the cache
                Cache.put(File,Template);
            }
        }

        // Returns the cache
        return Cache;
    }

    //Gets the names of all directories given an arraylist of files
    public ArrayList<String> GetDirectoryNames(ArrayList<File> Directories){
        ArrayList<String> DirectoryNames = new ArrayList<>();

        //Checks if the directories parameter is null
        if(Directories == null){
            return DirectoryNames;
        }

        for(File Directory : Directories){
            //Checks if the directory is the current directory and does not add it to the list
            if(Directory.equals(this.GetCurrentSelectedDirectory())){
                continue;
            }
            DirectoryNames.add(Directory.getName());
        }
        return DirectoryNames;
    }

    //Gets the paths of all directories given an arraylist of files
    public ArrayList<String> GetDirectoryPaths(ArrayList<File> Directories){
        ArrayList<String> DirectoryPaths = new ArrayList<>();

        //Checks if the directories parameter is null
        if(Directories == null){
            return DirectoryPaths;
        }

        for(File Directory : Directories){
            //Checks if the directory is the current directory and does not add it to the list
            if(Directory.equals(this.GetCurrentSelectedDirectory())){
                continue;
            }
            DirectoryPaths.add(Directory.getPath());
        }
        return DirectoryPaths;
    }

    public static void main(String[] args) {
        Console.initialize();
        // Logs in
        SystemStatus.LoggedIn = true;
        SystemStatus.CurrentUsername = "Guest";

        DataShard Data = new DataShard();
        Data.SetDirectory("Test");
        Data.SetFile("Test.txt");


        Data.SetEncryptionKey("Hello");
        Data.EncryptFile();



    }

    public JSONObject GetEncryptedFiles() {
        return EncryptedFiles;
    }
}
