package Core.Screens.WindowsDOS.Create;

import Config.Theme;
import Core.SystemStatus;
import Library.GraphicsHandler.Graphics;



public class CreateDirectoryScreen extends Graphics {

    //Displays the help screen
    @Override
    public void Help(){
        //Displays the title
        DisplayTitle("Create Directory Help");
        DisplayDescription("This screen allows you to create a new directory");
        DisplaySeparator(true);

        // Displays the help text
        DisplayText("Allows you to enter a name that you would like to create a directory with");
        DisplayText("When creating the directory you can only use letters. Numbers and special characters are not allowed");
        DisplayText(BackText);

        DisplaySeparator(true);

        //Displays the commands
        DisplaySeparator();
        DisplayDescription("System Commands");
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

        //Starts the main system loop
        while(!SystemStatus.InitiateShutdown){
            DisplayTitle("Create Directory");
            DisplayDescription(HelpText);
            DisplaySeparator(true); // Displays an invisible separator to the user

            // Displays the prompt so that the user can create a new directory in the current directory they have selected
            DisplayText("Please enter the name of the directory you would like to create");

            //Gets the user input
            String Input = Scan.nextLine();

            //Checks if the user requires help
            if(Verify(Input,"help")){
                clear(); // Clears the console
                Help();// Displays the help screen to the user
                continue;
            }

            //Checks if the user wants to go back to the previous screen
            if(Verify(Input,"back")){
                clear(); // Clears the console
                break;
            }

            // Checks if the user has entered a valid directory name
            if(!Input.matches("[a-zA-Z]+")){
                clear();
                log("Please enter a directory name that only contains letters", Theme.Error());
                continue;
            }

            //Checks the file length
            if(Input.length() > SystemStatus.MaxFileLength){
                clear();
                log("Please enter a file name that is no longer than " + SystemStatus.MaxFileLength + " characters", Theme.Error());
                continue;
            }

            //Checks if the directory already exists
            if(Shard.CheckDirectoryExists(Input)){
                clear();
                log("The directory " + Input + " already exists", Theme.Error());
                log("Please enter a different directory name", Theme.Error());
                continue;
            }

            //Creates the directory
            boolean Status = Shard.CreateDirectory(Input);

            //Checks if the directory was created successfully
            clear();

            if(Status){
                log("The directory " + Input + " was created successfully", Theme.Success());
            }
            else{
                log("The directory " + Input + " could not be created", Theme.Error());
            }
            // Breaks out of the loop
            break;
        }
    }
}
