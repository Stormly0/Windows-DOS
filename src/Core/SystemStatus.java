package Core;

import Library.EventHandler.SystemDetails;



//SYSTEM VARIABLES
// Do not change these variable names as they are critical to the system
public class SystemStatus {
    //Stores the current system operating system
    public static final String OperatingSystem = SystemDetails.GetOperatingSystem();
    //Stores if the system is currently logged in
    public static boolean LoggedIn = false;
    //Stores if the current logged-in user is an admin
    public static boolean IsAdmin = false;
    //Stores the current logged-in user's username
    public static String CurrentUsername = "";
    //Stores the current log Mode in the system to indicate what to log
    public static String LogMode = "All"; //All, Action, Errors, None
    //Indicates if the system is currently in debug mode
    public static boolean DebugMode = false;
    //Indicates which account the user wants to reset the password for
    public static String PasswordResetAccount = "";
    //Stores whether the system is in shutdown mode (Indicates whether the system is about to be shutdown and breaks all system loops)
    public static boolean InitiateShutdown = false;
    public static boolean ServerTerminated = false; // Indicates if the server has been terminated

    //CONFIG\\
    public static int MaxFileLength = 20; // The maximum length of a file name
}
