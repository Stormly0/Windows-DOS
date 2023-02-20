package Core.Screens.WindowsDOS.Create;

import Config.Theme;
import Core.SystemStatus;
import Library.GraphicsHandler.Graphics;



public class CreateFileScreen extends Graphics {

    //Displays the help screen
    @Override
    public void Help(){
        //Displays the title
        DisplayTitle("Create File Help");
        DisplayDescription("This screen allows you to create a new file");
        DisplaySeparator(true);

        // Displays the help text
        DisplayText("Allows you to enter a name that you would like to create a file with");
        DisplayText("When creating the file you can only use letters. Numbers and special characters are not allowed");
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
        clear(); // Clears the start screen

        while(!SystemStatus.InitiateShutdown){
            //Displays the header
            DisplayTitle("Create File");
            DisplayDescription(HelpText);
            DisplaySeparator(true); // Displays an invisible separator to the user

            // Displays the prompt so that the user can create a new file in the current directory they have selected
            DisplayText("Please enter the name of the file you would like to create");

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

            // Checks if the user has entered a valid file name
            if(!Input.matches("[a-zA-Z]+")){
                clear();
                log("Please enter a file name that only contains letters", Theme.Error());
                continue;
            }

            //Checks the length of the file name
            if(Input.length() > SystemStatus.MaxFileLength){
                clear();
                log("Please enter a file name that is no longer than " + SystemStatus.MaxFileLength + " characters", Theme.Error());
                continue;
            }

            //Adds a .txt file extension to the file name
            Input = Input + ".txt";

            //Checks if the file already exists
            if(Shard.CheckFileExists(Input)){
                clear();
                log(Input + " already exists", Theme.Error());
                log("Please enter a different file name", Theme.Error());
                continue;
            }

            // Creates a new directory with the name the user entered
            boolean Status = Shard.CreateFile(Input);

            //Checks the status and informs the user on whether the file was created or not
            clear();
            if(Status){
                log(Input + " has been created successfully", Theme.Success());
            }
            else{
                log(Input + " could not be created", Theme.Error());
            }
            // breaks the loop
            break;
        }
    }
}
