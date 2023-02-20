package Core.Screens.WindowsDOS;

import Config.Theme;
import Core.Network;
import Core.Screens.WindowsDOS.Create.CreateHome;
import Core.Screens.WindowsDOS.Delete.DeleteHome;
import Core.Screens.WindowsDOS.Edit.EditHome;
import Core.Screens.WindowsDOS.Move.MoveFileScreen;
import Core.Screens.WindowsDOS.Return.ReturnDirectoryScreen;
import Core.Screens.WindowsDOS.Save.SaveScreen;
import Core.Screens.WindowsDOS.Select.SelectScreen;
import Core.Screens.WindowsDOS.View.ViewFileScreen;
import Core.SystemStatus;
import Library.GraphicsHandler.Graphics;
import java.io.File;
import java.util.ArrayList;


public class HomeScreen extends Graphics {
    //Private Instances
    private static final Network Network = new Network(); // Network of the system

    //Screens
    private static final SelectScreen SelectScreen = new SelectScreen(); // Used to select a file
    private static final CreateHome CreateHome = new CreateHome(); // Used to create a file or directory
    private static final DeleteHome DeleteHome = new DeleteHome(); // Used to delete a file or directory
    private static final EditHome EditHome = new EditHome(); // Used to edit a file
    private static final MoveFileScreen MoveFileScreen = new MoveFileScreen(); // Used to move a file
    private static final SaveScreen SaveScreen = new SaveScreen(); // Used to save a file
    private static final ViewFileScreen ViewFileScreen = new ViewFileScreen(); // Used to view a file
    private static final ReturnDirectoryScreen ReturnDirectoryScreen = new ReturnDirectoryScreen(); // Used to return to a previous directory

    //Private Variables

    // System Options for the current screen when the user has selected a file
    private static final String[] SystemOptions = {
            "Select",
            "Return",
            "Create",
            "Delete",
            "Edit",
            "View",
            "Move",
            "Save",
            "Log Out"
    };

    // System options for the current screen when the user has not selected a file
    private static final String[] SystemOptionsNoFile = {
            "Select",
            "Return",
            "Create",
            "Delete",
            "Log Out"
    };


    //Private Methods

    //Formats the directory path history into a displayable string
    private String FormatDirectoryHistory(ArrayList<File> DirectoryPathHistory){
        String FormattedDirectoryHistory = "C:/"; // Stores the formatted directory history
        for (int i = 0; i < DirectoryPathHistory.size(); i++) {
            File Directory = DirectoryPathHistory.get(i);
            //Checks if the current directory value is the last directory in the path
            if(i == DirectoryPathHistory.size() - 1){
                FormattedDirectoryHistory += Directory.getName(); // Adds the directory name to the formatted directory history
                continue;
            }

            FormattedDirectoryHistory += Directory.getName() + "/";
        }
        return FormattedDirectoryHistory;
    }

    // Displays the help screen for the current screen
    @Override
    public void Help(){
        // Displays the help screen to the user and explains to them how to log in to the system
        DisplayTitle("Home Help");
        DisplayDescription("Welcome to the home screen of Windows DOS");
        DisplaySeparator(true); // Displays an invisible separator to the user

        //Displays the help text
        DisplayText("The text in-between the separators is the current directory path that you are currently in");
        DisplayText("The selected file is the file that you have selected to edit");
        DisplayText("The selected directory is the directory that you have selected to view");
        DisplayText("The system options provided below are the options that you can use to manage your files and folders");

        DisplaySeparator(true);
        DisplaySeparator();

        //Commands
        DisplayDescription("System Commands");
        DisplayText("Select - Selects a file or directory");
        DisplayText("Return - Returns to a previous directory or select a directory to view");
        DisplayText("Create - Creates a file or directory");
        DisplayText("Delete - Deletes a file or directory");
        DisplayText("Edit - Edits the selected file");
        DisplayText("View - Views the selected file");
        DisplayText("Move - Moves the selected file");
        DisplayText("Save - Saves the selected file");
        DisplayText("Log Out - Logs out of the system");
        DisplaySeparator();

        DisplaySeparator(true);
        DisplaySeparator(true); // Displays an invisible separator to the user
        DisplaySystemText("Enter any key to return to the login screen");
        Scan.nextLine(); // Waits for the user to press enter
        clear(); // Clears the console
    }

    // Displays the current menu screen to the user.
    @Override
    public void Display(){
        // Clears the screen
        clear();

        //Starts the main loop
        while(!SystemStatus.InitiateShutdown){
            DisplayTitle("Home");
            DisplayDescription(HelpText);
            DisplaySeparator(true); // Displays an invisible separator to the user
            DisplaySeparator(); // Displays a separator to the user

            File CurrentFile = Shard.GetCurrentSelectedFile(); // Stores the current selected file
            File CurrentDirectory = Shard.GetCurrentSelectedDirectory(); // Stores the current selected directory
            String DirectoryPathHistory = FormatDirectoryHistory(Shard.GetCurrentDirectoryPath()); // Stores the current directory path


            //Displays the current directory path history
            DisplaySystemText(DirectoryPathHistory);

            DisplaySeparator(); // Displays a separator to the user
            DisplaySeparator(true); // Displays an invisible separator to the user

            //Displays the current selected directory
            log("Selected Directory: ",true,Theme.System_Bold());
            log(CurrentDirectory.getName(),Theme.Text());

            // Checks if the user has selected a file
            if(CurrentFile != null){

                //Displays the current selected file name
                log("Selected File: ",true,Theme.System_Bold());
                log(CurrentFile.getName(),Theme.Text());

                //Displays a separator
                DisplaySeparator(true);

                //Displays the system options for the current screen
                DisplayOptions(SystemOptions);
            }
            else{
                //Displays the current selected file name
                log("Selected File: ",true,Theme.System_Bold());
                log("No File Selected",Theme.Text());

                //Displays a separator
                DisplaySeparator(true);

                //Displays the system options for the current screen
                DisplayOptions(SystemOptionsNoFile);
            }

            //// ---- USER INPUT SELECTION ---- \\\\

            // Gets the user input
            String UserInput = Scan.nextLine();

            //Checks if the user requires some help
            if(Verify(UserInput,"help")){
                // Clears the screen
                clear();
                // Displays the help screen
                Help();
                continue; // Continues the loop
            }

            //Checks if the user wants to select
            if (Verify(UserInput, "select", "1")) {
                // Clears the screen
                clear();
                // Displays the select file screen
                SelectScreen.Display();
                continue; // Continues the loop
            }

            //Checks if the user wants to return to a previous directory
            if(Verify(UserInput,"return","2")){
                // Clears the screen
                clear();
                // Returns to the previous directory
                ReturnDirectoryScreen.Display();
                continue; // Continues the loop
            }

            //Checks if the user wants to create
            if(Verify(UserInput,"create","3")){
                // Clears the screen
                clear();

                // Displays the create file screen
                CreateHome.Display();

                continue; // Continues the loop
            }

            //Checks if the user wants to delete
            if(Verify(UserInput,"delete","4")){
                // Clears the screen
                clear();
                // Displays the delete file screen
                DeleteHome.Display();
                continue; // Continues the loop
            }

            //Checks if the user wants to edit
            if(Shard.GetCurrentSelectedFile() != null && Verify(UserInput,"edit","5")){
                // Clears the screen
                clear();
                // Displays the edit file screen
                EditHome.Display();
                continue; // Continues the loop
            }

            //Checks if the user wants to view
            if(Shard.GetCurrentSelectedFile() != null && Verify(UserInput,"view","6")){
                // Clears the screen
                clear();
                ViewFileScreen.Display(); // Displays the view file screen
                continue; // Continues the loop
            }

            //Checks if the user wants to move a file
            if(Shard.GetCurrentSelectedFile() != null && Verify(UserInput,"move","7")){
                // Clears the screen
                clear();
                // Displays the move file screen
                MoveFileScreen.Display();
                continue; // Continues the loop
            }

            // Checks to see if the user wants to save their changes
            if(Shard.GetCurrentSelectedFile() != null && Verify(UserInput,"save","8")){
                // Clears the screen
                clear();
                // Saves the changes to the file
                SaveScreen.Display();
                continue; // Continues the loop
            }

            //Checks if the current user is an admin
            if(SystemStatus.IsAdmin && Verify(UserInput,"Terminate")){
                // Clears the screen
                clear();

                // Terminates the server
                Network.TerminateServer();

                //Checks if the server has been terminated
                for(int i = 0; i < 10; i++){
                    if(SystemStatus.ServerTerminated){
                        break;
                    }
                    else{
                        sleep(2000); // Waits for 1 second
                    }
                }

                //Checks if the server has been terminated
                if(SystemStatus.ServerTerminated){
                    log("Server has been terminated",Theme.Success());
                }
                else{
                    log("Server has not been terminated",Theme.Error());
                }

                //Continues the loop
                continue;
            }

            // Checks if the user wants to log out of the system
            if(Verify(UserInput, new String[]{"log out","logout",Integer.toString(SystemOptions.length),Integer.toString(SystemOptionsNoFile.length)}) != null){
                // Clears the screen
                clear();

                //Checks if the user has unsaved changes
                if(Shard.ChangesMade() != 0){
                    // Starts a main loop
                    while(true){
                        log("You have unsaved changes in " + Shard.GetCurrentSelectedFile().getName() + " !",Theme.Warning());
                        log("Would you like to save your changes? (Y/N)",Theme.Text());

                        //Gets the user input
                        UserInput = Scan.nextLine();

                        //Checks if the user wants to save their changes
                        if(Verify(UserInput,"y","yes")){
                            //Saves the changes to the file
                            Shard.Save();
                            clear();
                            break;
                        }
                        else if(Verify(UserInput,"n","no")){
                            //Discards the changes to the file
                            Shard.Discard();
                            clear();
                            break;
                        }
                        else{
                            clear();
                            //Displays an error message to the user
                            log("Please enter either Y or N",Theme.Error());
                        }
                    }
                }
                //Informs the user that they are logging out
                DisplaySystemText("Logging out...");

                // Logs the user out of the system
                SystemStatus.LoggedIn = false;
                SystemStatus.CurrentUsername = null; // Sets the current username to null
                SystemStatus.IsAdmin = false;

                //Logs the action
                ErrorHandle.action("Log out","User has logged out of the system");

                //Uninitialises the datashard
                Shard.Uninitialize();

                // Waits for 1 second
                sleep(1000);
                clear(); // Clears the screen
                break; // Breaks the loop
            }

            clear();
            // User has entered an invalid input
            log("Please select from the options provided", Theme.Error());
        }
    }
}
