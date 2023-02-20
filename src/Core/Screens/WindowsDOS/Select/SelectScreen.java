package Core.Screens.WindowsDOS.Select;

import Config.Theme;
import Core.SystemStatus;
import Library.GraphicsHandler.Graphics;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;



public class SelectScreen extends Graphics {
    //Public Methods

    //Displays the main help screen
    @Override
    public void Help(){
        //Displays the help screen for the select screen
        DisplayTitle("Select Help");
        DisplayDescription("This screen allows you to select a file or directory");
        DisplaySeparator(true); //Displays an invisible separator to the user

        // Displays the help text
        DisplayText("Each file that is listed displays the information about the file, such as it's file size, date created, date modified, and last accessed");
        DisplayText("To select a file or directory enter the file or directory name or number that represents the file or directory");
        DisplayText("The system will automatically detect if you have selected a directory or a file");
        DisplayText(BackText);

        DisplaySeparator(true);
        DisplaySeparator();
        DisplayDescription("System Commands");
        DisplayText("Number - Selects the file or directory that is listed at the number");
        DisplayText("Name - Selects the file or directory that is listed at the name");
        DisplayText("Back - Returns to the previous screen");
        DisplaySeparator();

        DisplaySeparator(true);
        DisplaySeparator(true); //Displays an invisible separator to the user
        DisplaySystemText("Enter any key to return to continue");
        Scan.nextLine(); //Waits for the user to press enter
        clear(); //Clears the console
    }

    //Displays the main select screen
    @Override
    public void Display(){
        // Clears the screen
        clear();

        //Checks if the user has made changes to the previous file
        if(Shard.ChangesMade() != 0){
            DisplaySavePrompt();
        }

        //Starts the main loop
        while(!SystemStatus.InitiateShutdown){
            DisplayTitle("Select");
            DisplayDescription(HelpText);
            DisplaySeparator(true);

            //Gets all the files in the current directory selected
            LinkedHashMap<File, HashMap<String,String>> Files = Shard.ListDirectory();

            //Checks if the current directory is empty
            if(Files == null || (Files != null && Files.isEmpty())){
                DisplayText("The current directory is empty");
                DisplaySeparator(true);
                DisplaySystemText("Enter any key to return to the previous screen");
                String UserInput = Scan.nextLine(); // Waits for the user to press enter

                //Checks if the user requires help
                if(Verify(UserInput,"help")){
                    clear(); //Clears the console
                    Help(); //Displays the help screen
                    continue; //Continues the main loop
                }

                clear(); //Clears the console
                break; // Exits the main loop
            }

            ArrayList<String> DirectoryFiles = new ArrayList<>(); // Stores the data for the files in the current directory
            ArrayList<String> DirectoryDataNames = new ArrayList<>(); // Stores the names of the files in the current directory

            //Displays the files in the current directory
            for (File File : Files.keySet()) {
                //Gets the data for the current file
                HashMap<String,String> FileData = Files.get(File); // Stores the data for the current file
                String Type = FileData.get("Type"); // Stores the type of the current file
                String Size = FileData.get("File Size"); // Stores the size of the current file
                String DateCreated = FileData.get("Creation Date"); // Stores the date created of the current file
                String DateModified = FileData.get("Last Modified Date"); // Stores the date modified of the current file
                String LastAccessed = FileData.get("Last Accessed"); // Stores the last accessed of the current file
                String FileName = FileData.get("Name");
                String Encrypted = FileData.get("Cryptography");

                // puts in the names of the files for the user to select
                DirectoryDataNames.add(File.getName());

                //Formats the data for the current file into an array of strings
                String FormattedString = FormatFileMetadata(FileName,Type,Size,DateCreated,DateModified,LastAccessed,Encrypted);//FormatFileMetadata(FileName,Type,Size,DateCreated,DateModified,LastAccessed);//FileName + GetSpacing(File.getName(),SystemStatus.MaxFileLength) + " | File Type: " + Type + GetSpacing(Type,10) + " | File Size: " + Size + GetSpacing(Size,10) + " | Date Created: " + DateCreated + " | Date Modified: " + DateModified + " | Last Accessed: " + LastAccessed;
                DirectoryFiles.add(FormattedString);
            }

            //Displays the files in the current directory
            DisplayOptions(DirectoryFiles);

            // Gets the user input
            String UserInput = Scan.nextLine();

            //Checks if the user requires help
            if(Verify(UserInput,"help")){
                clear(); //Clears the console
                Help(); //Displays the help screen
                continue; //Continues the main loop
            }

            // Checks if the user wants to go back
            if(Verify(UserInput,"back")){
                clear(); //Clears the console
                break; //Exits the main loop
            }

            //Checks if the user has inputted the directory name or directory number to select
            if(VerifyNumber(UserInput,Files.size()) || Verify(UserInput,DirectoryDataNames) != null){
                //Gets the name of the file
                String FileName = null;

                //Checks whether the user entered a number or a file name
                if(VerifyNumber(UserInput,Files.size())){

                    // Checks if the current selected file number is outside the range of the files in the current directory
                    if(Integer.parseInt(UserInput) - 1 > Files.size()){
                        // Clears the screen
                        clear();
                        log("Please select from one of the options listed",Theme.Error());
                        continue; // Continues the loop
                    }

                    //Gets the file name from the number
                    FileName = DirectoryDataNames.get(Integer.parseInt(UserInput) - 1);
                }
                else if(Verify(UserInput,DirectoryDataNames) != null){
                    //Gets the file name from the user input
                    FileName = Verify(UserInput,DirectoryDataNames);
                }
                else if(Verify(UserInput,DirectoryDataNames) == null){
                    // Clears the screen
                    clear();
                    log("Please select from one of the options listed",Theme.Error());
                    continue; // Continues the loop
                }

                //Checks if the current selected file is a directory or file
                if(Shard.IsDirectory(FileName)){
                    // Sets the current directory to the selected directory
                    Shard.SetDirectory(FileName);
                }
                else if(Shard.IsFile(FileName)){
                    // Sets the current selected file to the selected file
                    Shard.SetFile(FileName);
                }
                else{
                    // Clears the screen
                    clear();
                    log(FileName + " could not be set as a directory or file!",Theme.Error());
                    log("Please try again",Theme.Error());

                    // Logs the error
                    ErrorHandle.log("Unable Set Directory|File","Unable to set file to " + FileName,1);

                    continue; // Continues the loop
                }

                // Clears the screen
                clear();

                // breaks the main loop
                break;
            }

            // user did not pick a valid option
            clear(); //Clears the console
            log("Please select from one of the options listed",Theme.Error());
        }
    }
}
