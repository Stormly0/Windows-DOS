package Core;

import Core.Screens.Startup.StartupScreen;
import Core.Screens.WindowsDOS.HomeScreen;
import Library.EventHandler.ErrorHandler;
import Library.GraphicsHandler.Console;



// Handles the current user session in the system
public class Client {
    // Private Instances
    private static final StartupScreen Startup = new StartupScreen();// Startup Screen of the system
    private static final ErrorHandler ErrorHandle = new ErrorHandler(); // Error Handler of the system

    //Constructor
    public Client(){
        // Initializes the console
        Console.initialize();
    }

    // Public Methods

    // Starts up the client
    public void Start(){

        // Starts the main startup screen
        Startup.DisplayBoot();

//        try{
            //Starts a main system loop to handle all system events
            while(!SystemStatus.InitiateShutdown){
                // Displays the main startup screen to the user to allow them to login or register
                Startup.Display();

                //Creates a new home-screen object once the user has logged in
                // Home Screen of the windows DOS system
                HomeScreen home = new HomeScreen();

                //Displays the main home-screen to the user
                home.Display();
            }
//        }catch(Exception e){
//            // Handles any uncaught exceptions
//            ErrorHandle.log("Uncaught Exception","An uncaught exception has occurred in the system. Error: " + e.getMessage(),3); // Logs the error
//        }

    }
}
