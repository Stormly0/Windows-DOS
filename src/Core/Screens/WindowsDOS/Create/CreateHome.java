package Core.Screens.WindowsDOS.Create;

import Config.Theme;
import Core.SystemStatus;
import Library.GraphicsHandler.Graphics;



public class CreateHome extends Graphics {
    //Private Instances
    private static final CreateDirectoryScreen CreateDirectoryScreen = new CreateDirectoryScreen(); // Used to create a directory
    private static final CreateFileScreen CreateFileScreen = new CreateFileScreen(); // Used to create a file

    //Private Variables
    private final static String[] Options = {
            "Create File",
            "Create Directory"
    };

    //Displays the help screen
    @Override
    public void Help(){
        //Displays the help screen for creating files or directories
        DisplayTitle("Create Help");
        DisplayDescription("This screen allows you to create files or directories");
        DisplaySeparator(true); // Displays an invisible separator to the user

        // Displays the help text
        DisplayText("To create a file or directory you enter a new file or directory name that doesn't already exist");
        DisplayText(BackText);
        DisplaySeparator(true);

        // Displays the system commands
        DisplaySeparator();
        DisplayDescription("System Commands");
        DisplayText("Create File - Creates a new file within the selected directory");
        DisplayText("Create Directory - Creates a new directory within the selected directory");
        DisplayText("Back - Returns to the previous screen");
        DisplaySeparator();

        DisplaySeparator(true);
        DisplaySeparator(true);
        DisplaySystemText(ReturnHomeText);
        Scan.nextLine(); // Waits for the user to press enter
        clear(); // Clears the console
    }


    //Displays the main screen
    @Override
    public void Display(){
        //Clears the screen
        clear();

        //Starts the main system loop
        while(!SystemStatus.InitiateShutdown){
            //Displays the title
            DisplayTitle("Create Menu");
            DisplayDescription(HelpText);
            DisplaySeparator(true); // Displays an invisible separator to the user

            log("Selected Directory: ",true,Theme.System_Bold());
            log(Shard.GetCurrentSelectedDirectory().getName(),Theme.Text());
            DisplaySeparator(true); // Displays an invisible separator to the user

            //Prompts the options to the user
            DisplayOptions(Options);

            // Gets the user input
            String UserInput = Scan.nextLine();

            // Checks if the user requires some help
            if(Verify(UserInput,"help")){
                clear(); // Clears the console
                // Displays the help screen to the user
                Help();
                continue;
            }

            // Checks if the user wants to go back to the previous screen
            if(Verify(UserInput,"back")){
                clear(); // Clears the console
                // Exits the loop
                break;
            }

            // Checks if the user wants to create a file
            if(Verify(UserInput,new String[]{"file","create file","1"}) != null){
                clear(); // Clears the console
                // Displays the create file screen
                CreateFileScreen.Display();
                break; // Exits the loop
            }

            // Checks if the user wants to create a directory
            if(Verify(UserInput,new String[]{"directory","create directory","2"}) != null){
                clear(); // Clears the console
                // Displays the create directory screen
                CreateDirectoryScreen.Display();
                break;
            }

            //User did not select any of the options
            clear();
            log(InvalidInputText, Theme.Error());
        }
    }
}
