package Core;

import Config.Theme;
import Library.DataHandler.FileHandler;
import Library.EventHandler.ErrorHandler;
import Library.GraphicsHandler.Console;
import org.json.simple.JSONObject;
import java.io.File;
import java.util.LinkedHashMap;



// Handles all system data and manages user data such as the accounts
// THIS CLASS IS NOT THE SAME AS THE DATA SHARD CLASS!!
public final class Datastore {
    //Private Instances
    private static final FileHandler FileHandle = new FileHandler();// Used to handle file operations
    private static final ErrorHandler ErrorHandle = new ErrorHandler(); // Used to handle errors and action logs
    // Private Variables
    private static final File AccountFile = (SystemStatus.OperatingSystem.equalsIgnoreCase("Windows")) ? new File("src\\Core\\System\\Accounts.json") : new File("src/Core/System/Accounts.json");

    private static final LinkedHashMap<String,String> UserAccounts = new LinkedHashMap<>();  // Stores the user accounts of the system

    //Constructor
    public Datastore(){
        // Gets the current accounts from the system
        JSONObject TempVar_Accounts = FileHandle.ReadJSON(AccountFile); // Gets the current accounts from the system

        // Checks if TempVar_Accounts is null
        if(TempVar_Accounts != null){
            // Sets the accounts to the current accounts
            // Loops through the accounts and adds them to the user accounts hash map for type conversion
            for(Object Key : TempVar_Accounts.keySet()){
                UserAccounts.put(Key.toString(),TempVar_Accounts.get(Key).toString());
            }
        }
    }

    //Public Methods

    // Gets the system Accounts Data
    public JSONObject GetAccounts(){
        return new JSONObject(UserAccounts);
    }

    // Gets all account names in the system
    public String[] GetAccountNames(){
        // Creates a new array of the size of the user accounts
        String[] Accounts = new String[UserAccounts.size()];
        // Loops through the user accounts and adds them to the array
        int i = 0;
        for(String Account : UserAccounts.keySet()){
            Accounts[i] = Account;
            i++;
        }
        // Returns the array
        return Accounts;
    }

    //Checks if the user account exists
    public boolean AccountExists(String AccountName){
        return UserAccounts.containsKey(AccountName);
    }

    /**
     * <p>Creates a new user directory which will be used to separate the user data into their own directory</p>
     * <b>Handles the error</b>
     * @param Name Name of the parent file directory
     * @return Status
     */
    public boolean CreateNewUserDirectory(String Name){
        String CurrentPath = (SystemStatus.OperatingSystem.equalsIgnoreCase("windows")) ? "src\\Users\\" + Name : "Users/" + Name;  // Gets the current path of the user directory

        // Checks if the user directory already exists to prevent overwriting of data
        if(FileHandle.CheckPath(CurrentPath)){
            Console.log("User directory already exists", Theme.Error());
            ErrorHandle.log("User Directory already exists","Directory not created!",2); // Logs the error
            return false; // Returns false if the user directory already exists
        }

        //Creates a new user directory folder according to the user's name and the operating system that the environment is running on
        File ParentFolder = (SystemStatus.OperatingSystem.equalsIgnoreCase("windows")) ? new File("src\\Users\\" + Name) : new File("Users/" + Name);
        boolean ParentFolderStatus = ParentFolder.mkdir(); // Creates the parent folder [Directory]

        // Checks the status of the parent folder on whether it was created
        if(!ParentFolderStatus){
            // Logs it in the error log
            ErrorHandle.log("Parent Directory Creation Failed","Parent directory is unable to be created",3); // Logs the error
            return false;
        }

        // Creates sub folders

        // Creates a Data folder that will store all the data of the user
        File DataFolder = new File(ParentFolder,"Data");
        boolean DataFolderStatus = DataFolder.mkdir(); // Creates the data folder [Directory]

        // Creates a System Folder that stores all the user log/actions and settings
        File SystemFolder = new File(ParentFolder,"System");
        boolean SystemFolderStatus = SystemFolder.mkdir(); // Creates the system folder [Directory]

        //Creates the system log folder
        File SystemLogFolder = new File(SystemFolder,"Log");
        boolean LogFolderStatus = SystemLogFolder.mkdir(); // Creates the system log folder [Directory]

        //Creates the system settings folder
        File SystemSettingsFolder = new File(SystemFolder,"Settings");
        boolean SettingsFolderStatus = SystemSettingsFolder.mkdir(); // Creates the system settings folder [Directory]

        // Checks the status of all the other file directories
        if(!DataFolderStatus || !SystemFolderStatus || !LogFolderStatus || !SettingsFolderStatus){
            // Logs it in the error log
            ErrorHandle.log("Sub Directory Creation Failed","Sub directory is unable to be created",3); // Logs the error
            return false;
        }

        //Creates a config file that stores all the user's settings
        File ConfigFile = new File(SystemSettingsFolder,"Config.json");

        // Creates a log txt file that stores all user actions and errors
        File LogFile = new File(SystemLogFolder,"Log.txt");

        // Creates an encrypted files that stores all the encrypted files for the user
        File EncryptedFiles = new File(SystemLogFolder,"EncryptedFiles.json");

        // Creates the files
        try{
            boolean ConfigFileStatus = ConfigFile.createNewFile();
            boolean LogFileStatus = LogFile.createNewFile();
            boolean EncryptedFileStatus = EncryptedFiles.createNewFile();

            // Checks the status of all files being created in the directory
            if(!ConfigFileStatus || !LogFileStatus || !EncryptedFileStatus){
                // Logs it in the error log
                ErrorHandle.log("File Creation Failed","File is unable to be created",3); // Logs the error
                return false;
            }


            // Writes the default settings for the config file in a JSON format
            LinkedHashMap<String,Object> DefaultSettings = new LinkedHashMap<>();
            DefaultSettings.put("Name",Name);
            DefaultSettings.put("Text_Color","W");
            JSONObject JSONParent = new JSONObject(DefaultSettings);

            FileHandle.Write(ConfigFile.getPath(),JSONParent.toString()); // Writes the default settings into the config file

            // Logs the action that the user has done
            ErrorHandle.action("Created Directory","New Directory " + Name + " has been created");
            return true;
        }
        catch(Exception e){
            // Logs the error
            Console.log("Failed to create new user directory for" + Name);
            ErrorHandle.log("Directory Creation Error",e.getMessage(),3);
            return false;
        }
    }

    //Adds a new user account in the system json file
    public void AddAccount(String Username, String Password){
        UserAccounts.put(Username,Password); // Adds the new account to the user accounts hash map

        // Creates a new JSON object that stores all the user accounts
        JSONObject JSONParent = new JSONObject(UserAccounts);

        // Writes the new user accounts to the system json file
        boolean Status = FileHandle.WriteJSON(AccountFile,JSONParent);

        // Checks the status
        if(Status){
            // Logs the action that the user has done
            ErrorHandle.action("Created Account","New Account " + Username + " has been created");
        }
        else{
            // Logs the error
            ErrorHandle.log("Account Creation Error","Unable to create new account for " + Username,3);
        }
    }

    //Updates an account password in the system as the user wants to reset their password
    public void UpdateAccountPassword(String Username,String Password){
        UserAccounts.replace(Username,Password); // Updates the password of the user account

        // Creates a new JSON object that stores all the user accounts
        JSONObject JSONParent = new JSONObject(UserAccounts);

        // Writes the new user accounts to the system json file
        boolean Status = FileHandle.WriteJSON(AccountFile,JSONParent);

        // Checks the status
        if(Status){
            // Logs the action that the user has done
            ErrorHandle.action("Updated Account Password","Account " + Username + " has been updated");
        }
        else{
            // Logs the error
            ErrorHandle.log("Account Password Update Error","Unable to update account password for " + Username,3);
        }
    }
}
