package Core.Screens.WindowsDOS.Delete;

import Config.Theme;
import Core.SystemStatus;
import Library.GraphicsHandler.Graphics;



public class DeleteDirectoryScreen extends Graphics {

    //Public Methods

    //Displays the help screen
    @Override
    public void Help(){
        DisplayTitle("Delete Directory Help");
        DisplayDescription("This screen allows you to delete a directory");
        DisplaySeparator(true); // Displays an invisible separator to the user

        // Displays the help text to the user
        DisplayText("The current selected directory is the directory that will be deleted");
        DisplayText("You cannot delete the root directory");
        DisplayText(BackText);
        DisplaySeparator(true);

        //Displays the commands
        DisplaySeparator();
        DisplayDescription("System Commands");
        DisplayText("Back - Returns to the previous screen");
        DisplaySeparator();

        DisplaySeparator(true);
        DisplaySeparator(true); // Displays an invisible separator to the user
        DisplayDescription("Enter any key to return to the to the home screen");
        Scan.nextLine();
        clear();
    }

    //Displays the main screen
    @Override
    public void Display(){
        clear();

        //Starts the main loop
        while(!SystemStatus.InitiateShutdown){
            DisplayTitle("Delete Directory Menu");
            DisplayDescription(HelpText);
            DisplaySeparator(true); // Displays an invisible separator to the user

            //Checks if the current directory is the root directory
            if(Shard.GetCurrentSelectedDirectory().getName().equalsIgnoreCase("Data") || Shard.GetCurrentSelectedDirectory().getPath().matches(".+\\\\Data")){
                log("You cannot delete the root directory", Theme.Error());
                DisplaySeparator(true); // Displays an invisible separator to the user
                DisplaySystemText(ReturnHomeText);
                String UserInput = Scan.nextLine(); // Waits for the user to press enter

                //Checks if the user requires some help
                if(Verify(UserInput,"help")){
                    clear(); // Clears the console
                    Help();// Displays the help screen
                    continue;
                }

                clear(); // Clears the console
                break;
            }

            //Warns the user that they are about to delete a directory
            DisplayText("You are about to delete the directory: " + Shard.GetCurrentSelectedDirectory().getName() + " | [Y/N]");
            log("Deleting the directory will delete all files and directories within the selected directory", Theme.Warning());

            //Gets the user input
            String UserInput = Scan.nextLine();

            // Checks if the user requires some help
            if(Verify(UserInput,"help")){
                clear(); // Clears the console
                Help();// Displays the help screen
                continue;
            }

            clear();
            // Checks if the user enters yes or no
            if(Verify(UserInput,"y","yes")){
                //Deletes the directory
                boolean Status = Shard.DeleteDirectory();

                //Checks if the directory was deleted successfully
                if(Status){
                    log("Directory deleted successfully", Theme.Success());
                }
                else{
                    log("Directory failed to delete", Theme.Error());
                }

                // Waits for the user to press enter
                DisplaySeparator(true); // Displays an invisible separator to the user
                DisplaySystemText("Enter any key to continue");
                Scan.nextLine();
                clear();
                break; // Breaks out of the loop
            }
            else if(Verify(UserInput,"n","no")){
                clear();
                break; // Breaks out of the loop
            }

            //User did not enter a valid input
            clear();
            log("Please enter either Y or N", Theme.Error());
        }
    }
}
