package Core.Screens.Startup;

import Config.Sounds;
import Config.Theme;
import Core.SystemStatus;
import Library.EventHandler.SoundEngine;
import Library.EventHandler.ThreadScheduler;
import Library.GraphicsHandler.Graphics;



//Purpose: This class is used to display the startup screen of the system
public class StartupScreen extends Graphics {
    //Private Instances
    private final SoundEngine SoundPlayer = new SoundEngine(); // Used to play sounds
    private final ThreadScheduler Scheduler = new ThreadScheduler(); // Used to schedule tasks
    // Private Variables
    private final String[] SystemOptions = {"Login","Register","Shutdown"};

    // SCREENS
    private final LoginScreen Login = new LoginScreen(); // Used to display the login screen
    private final RegisterScreen Register = new RegisterScreen(); // Used to display the register screen

    //Private Variables
    private static boolean FirstBoot = true; // Used to determine if this is the first boot of the system

    //Private Methods

    // Displays the main boot screen to the user
    private void DisplayBootScreen(){
        //Clears the console
        clear();

        String BootText = "___       ______       _________                           _______________________\n" +
                "__ |     / /__(_)____________  /________      _________    ___  __ \\_  __ \\_  ___/\n" +
                "__ | /| / /__  /__  __ \\  __  /_  __ \\_ | /| / /_  ___/    __  / / /  / / /____ \\ \n" +
                "__ |/ |/ / _  / _  / / / /_/ / / /_/ /_ |/ |/ /_(__  )     _  /_/ // /_/ /____/ / \n" +
                "____/|__/  /_/  /_/ /_/\\__,_/  \\____/____/|__/ /____/      /_____/ \\____/ /____/";




        String ProgressBar = "";
        double MaxLength = 50d;
        for(double i = 0; i <= MaxLength; i++){
            for(int x = 0; x <= (i/MaxLength); x++){
                ProgressBar += "█";
            }

            // Rounds to the nearest hundredth
            double Percentage = Math.round((i/MaxLength) * 10000d)/100d;


            if(Percentage >= 100){
                log("Boot Progress: " + Percentage + "% | [" + ProgressBar + "]\r",true);
            }
            else{
                log("Boot Progress: " + Percentage + "% | [" + ProgressBar + "]\r",true);
            }
            if(!SystemStatus.DebugMode) {
                sleep(50);
            }
        }


        sleep(1000);
        // Clears the console
        clear();

        if(SystemStatus.DebugMode) {
            log(BootText,Theme.System_Color());
        }
        else
        {
            // Prints out the System name one letter at a time
            animate(BootText,10,Theme.System_Color());

            //Plays the startup sound effect
            Scheduler.Promise(() -> {
                SoundPlayer.Play(Sounds.StartUp.toString());
                return true;
            }).thenAcceptAsync((result) -> {
                Scheduler.Terminate(); // Terminates the scheduler since this only runs once
            });
        }
        sleep(500);
    }

    //Public Methods

    // Displays the help screen for the current screen
    @Override
    public void Help(){
        // Displays the help screen to the user and explains to them how to log in to the system
        DisplayTitle("Help");
        DisplayDescription("Welcome to the login screen of Windows DOS");
        DisplaySeparator(true); // Displays an invisible separator to the user
        DisplayText("To log in to the system, please enter your username and password");
        DisplayText("If you don't have an account made, please create one by selecting the option to create an account");
        DisplayText("If you would like to return to a previous screen, please enter 'back' to return to the previous screen");
        DisplaySeparator(true); // Displays an invisible separator to the user
        DisplayDescription("Enter any key to return to the login screen");
        Scan.nextLine(); // Waits for the user to press enter
        clear(); // Clears the console
    }

    // Displays the current menu screen to the user.
    @Override
    public void Display(){

        // Checks if this is the first boot of the system
        if(FirstBoot){
            FirstBoot = false; // Sets the first boot to false
            // Displays the startup screen to the user
            log("Welcome to Windows DOS v1.0",Theme.System_Header());
            sleep(3000); // Waits for 3 seconds to allow the user to read the title
            clear(); // Clears the console
        }


        while(true){
            DisplayTitle("Windows DOS");
            DisplayDescription(HelpText);
            DisplaySeparator(true); // Displays an invisible separator to the user
            DisplayOptions(SystemOptions);

            String UserInput = Scan.nextLine();

            // Checks for the user input

            // User needs help
            if(UserInput.equalsIgnoreCase("help")){
                clear(); // Clears the console
                Help(); // Displays the help screen to the user
                continue; // Skips an iteration of the loop
            }

            //User wants to log in to the system
            if(Verify(UserInput,"1","Login")) {
                clear(); // Clears the console
                Login.Display(); // Displays the login screen to the user

                //Checks if the user is logged in or not
                if(SystemStatus.LoggedIn){
                    break;
                }
                continue; // Continues the loop
            }

            //User wants to register an account
            if(Verify(UserInput,"2","Register")){
                clear(); // Clears the console
                Register.Display(); // Displays the register screen to the user
                continue; // Continues the loop
            }

            //User wants to shut down the system
            if(Verify(UserInput,"3","Shutdown")){
                clear(); // Clears the console
                DisplaySystemText("Shutting down the system...");
                sleep(1000);
                SoundPlayer.Play(Sounds.Shutdown.toString()); // Plays the shutdown sound effect
                System.exit(0); // Exits the system
            }

            // Clears the console
            clear();
            // No inputs match which indicates that the user has entered an invalid input
            log("Please select an option listed below",Theme.Error());
        }
    }

    // Displays the boot screen
    public void DisplayBoot(){
        DisplayBootScreen();
    }
}
