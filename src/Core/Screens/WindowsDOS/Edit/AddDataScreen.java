package Core.Screens.WindowsDOS.Edit;

import Config.Theme;
import Core.SystemStatus;
import Library.GraphicsHandler.Graphics;



public class AddDataScreen extends Graphics {

    //Public Methods

    //Displays the help screen
    @Override
    public void Help(){
        DisplayTitle("Add Data Help");
        DisplayDescription("This screen allows you to add data to a file");
        DisplaySeparator(true); // Displays an invisible separator to the user

        //Displays the help text
        DisplayText("This allows you to enter data that you would like to add to a file.");
        DisplayText(BackText);
        DisplaySeparator(true);

        //Displays the commands
        DisplaySeparator();
        DisplayDescription("System Commands");
        DisplayText("Data - The data that you would like to add to the file");
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
        clear();

        //Starts the main loop
        while(!SystemStatus.InitiateShutdown){
            DisplayTitle("Add Data");
            DisplayDescription(HelpText);
            DisplaySeparator(true); // Displays an invisible separator to the user

            // Displays the input prompt
            DisplaySystemText("Enter the data that you would like to add to " + Shard.GetCurrentSelectedFile().getName());

            //Gets the user input
            String UserInput = Scan.nextLine();

            //Checks if the user requires help
            if(Verify(UserInput,"help")){
                clear();// Clears the console
                Help();// Displays the help screen
                continue; // Continues the loop
            }

            // Checks if the user wants to go back
            if(Verify(UserInput,"back")){
                clear();// Clears the console
                break; // Breaks the loop
            }

            //Checks if the user has entered an empty string
            if(UserInput == null || UserInput.isEmpty()){
                clear(); // Clears the console
                log("Empty Strings are not accepted as valid input", Theme.Error()); // Informs the user that they have entered an empty string
                continue; // Continues the loop
            }

            // Adds the data to the file
            Shard.Add(UserInput);

            clear(); // Clears the console

            //Informs the user that the data has been successfully added
            log("Successfully added " + "\"" + UserInput + "\"" + " to "  + Shard.GetCurrentSelectedFile().getName(), Theme.Success());

            // Breaks out of the loop
            break;
        }
    }
}
