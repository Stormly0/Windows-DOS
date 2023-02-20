package Core.Screens.WindowsDOS.Delete;

import Config.Theme;
import Core.SystemStatus;
import Library.GraphicsHandler.Graphics;



public class DeleteHome extends Graphics {
    //Private Instances
    private final static DeleteFileScreen DeleteFileScreen = new DeleteFileScreen(); // Used to delete a file
    private final static DeleteDirectoryScreen DeleteDirectoryScreen = new DeleteDirectoryScreen(); // Used to delete a directory
    private final static DeleteSelectScreen DeleteSelectScreen = new DeleteSelectScreen(); // Used to delete a file you choose

    //Private Variables
    private final static String[] Options = {
            "Delete File",
            "Delete Directory",
            "Delete Select"
    };

    //Public methods

    //Displays the help screen
    @Override
    public void Help(){
        DisplayTitle("Delete Help");
        DisplayDescription("This screen allows you to delete a file or directory");
        DisplaySeparator(true); // Displays an invisible separator to the user

        // Displays the help text
        DisplayText("To delete a file or directory you enter the name of the file or directory you would like to delete");
        DisplayText(BackText);
        DisplaySeparator(true);

        //Displays the commands
        DisplaySeparator();
        DisplayDescription("System Commands");
        DisplayText("Delete File - Deletes the current selected file");
        DisplayText("Delete Directory - Deletes the current selected directory");
        DisplayText("Delete Select - Deletes the current selected file you choose in the selected directory");
        DisplayText("Back - Returns to the previous screen");
        DisplaySeparator();

        DisplaySeparator(true);
        DisplaySeparator(true); // Displays an invisible separator to the user
        DisplayDescription(ReturnHomeText);
        Scan.nextLine();
        clear(); // Clears the console
    }

    //Displays the main screen
    @Override
    public void Display(){
        clear();

        //Starts the main loop
        while(!SystemStatus.InitiateShutdown){
            DisplayTitle("Delete Menu");
            DisplayDescription(HelpText);
            DisplaySeparator(true); // Displays an invisible separator to the user

            //Displays the current selected file and directory
            log("Selected Directory: ",true,Theme.System_Bold());
            log(Shard.GetCurrentSelectedDirectory().getName(),Theme.Text());
            log("Selected File: ",true,Theme.System_Bold());
            log((Shard.GetCurrentSelectedFile() == null) ? "No File Selected" : Shard.GetCurrentSelectedFile().getName(),Theme.Text());
            DisplaySeparator(true); // Displays an invisible separator to the user

            //Displays the options
            DisplayOptions(Options);

            // Gets the user input
            String UserInput = Scan.nextLine();

            // Checks if the user requires some help
            if(Verify(UserInput,"help")){
                clear(); // Clears the console
                Help();// Displays the help screen
                continue;
            }

            // Checks if the user wants to go back
            if(Verify(UserInput,"back")){
                clear();  // Clears the console
                break; // Breaks the loop
            }

            // Checks if the user wants to delete a file
            if(Verify(UserInput,new String[]{"file","delete file","1"}) != null){
                clear(); // Clears the console
                DeleteFileScreen.Display(); // Displays the delete file screen
                break; // Breaks the loop and returns to the home screen
            }

            // Checks if the user wants to delete a directory
            if(Verify(UserInput,new String[]{"directory","delete directory","2"}) != null){
                clear(); // Clears the console
                DeleteDirectoryScreen.Display(); // Displays the delete directory screen
                break; // Breaks the loop and returns to the home screen
            }

            // Checks if the user wants to delete a file in a directory using select mode
            if(Verify(UserInput,new String[]{"select","delete select","3"}) != null){
                clear(); // Clears the console
                DeleteSelectScreen.Display(); // Displays the delete file screen
                break; // Breaks the loop and returns to the home screen
            }

            // User did not select an valid option
            clear();
            log(InvalidInputText, Theme.Error());
        }
    }
}
