package Core.Screens.WindowsDOS.Delete;

import Config.Theme;
import Core.SystemStatus;
import Library.GraphicsHandler.Graphics;



public class DeleteFileScreen extends Graphics {

    // Displays the help screen
    @Override
    public void Help(){
        DisplayTitle("Delete File Help");
        DisplayDescription("This screen allows you to delete a file");
        DisplaySeparator(true); // Displays an invisible separator to the user

        // Displays the help text
        DisplayText("The current selected file is the file that will be deleted");
        DisplayText(BackText);

        DisplaySeparator(true);

        //Displays the commands
        DisplaySeparator();
        DisplayDescription("System Commands");
        DisplayText("Back - Returns to the previous screen");
        DisplaySeparator();

        DisplaySeparator(true);

        DisplaySeparator(true); // Displays an invisible separator to the user
        DisplaySystemText(ReturnHomeText);
        Scan.nextLine();
        clear(); // Clears the console
    }

    // Displays the main screen
    @Override
    public void Display() {
        clear();

        // Starts the main loop
        while (!SystemStatus.InitiateShutdown) {
            DisplayTitle("Delete File Menu");
            DisplayDescription(HelpText);
            DisplaySeparator(true); // Displays an invisible separator to the user

            // Checks if the user has selected a file
            if (Shard.GetCurrentSelectedFile() == null) {
                log("Please select a file to delete", Theme.Error());
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

            // Warns the user that they are about to delete a file
            log("You are about to delete the file: " + Shard.GetCurrentSelectedFile().getName(),Theme.Warning());
            DisplayText("Are you sure you want to delete this file? [Y/N]");

            // Gets the user input
            String UserInput = Scan.nextLine();

            // Checks if the user requires some help
            if (Verify(UserInput, "help")) {
                clear(); // Clears the console
                Help();// Displays the help screen
                continue;
            }

            clear();
            // Checks if the user enters yes or no
            if (Verify(UserInput, "y", "yes")) {
                // Deletes the file
                boolean Status = Shard.DeleteFile();

                clear();
                // Checks if the file was deleted
                if (Status) {
                    log("The file was deleted successfully", Theme.Success());
                } else {
                    log("The file could not be deleted", Theme.Error());
                }

                DisplaySeparator(true); // Displays an invisible separator to the user
                DisplaySystemText(ReturnHomeText);
                Scan.nextLine(); // Waits for the user to press enter
                clear(); // Clears the console
                break;
            } else if (Verify(UserInput, "n", "no")) {
                clear(); // Clears the console
                break;
            }

            // Informs the user that they entered an invalid input
            clear();
            log("Please enter either Y or N", Theme.Error());
        }
    }
}
