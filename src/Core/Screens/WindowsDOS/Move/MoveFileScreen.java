package Core.Screens.WindowsDOS.Move;

import Config.Theme;
import Core.SystemStatus;
import Library.GraphicsHandler.Graphics;
import java.io.File;
import java.util.ArrayList;



public class MoveFileScreen extends Graphics {
    //Public Methods

    //Displays the help screen
    @Override
    public void Help(){
        DisplayTitle("Move File Help");
        DisplayDescription("Moves a file from one location to another");
        DisplaySeparator(true);

        //Displays the help text
        DisplayText("Moves a selected file to a different directory");
        DisplayText(BackText);

        // Displays the commands
        DisplaySeparator();
        DisplayDescription("System Commands");
        DisplayText("Number | Name - Selects the file that you would like to move");
        DisplayText("Back - Returns to the home screen");
        DisplaySeparator();

        DisplaySeparator(true);
        DisplaySeparator(true);
        DisplaySystemText(ReturnHomeText);
        Scan.nextLine();
        clear();
    }

    //Displays the main screen
    @Override
    public void Display() {

        //Checks if the user has selected a file
        if(Shard.GetCurrentSelectedFile() == null){
            clear(); // Clears the console
            log("You have not selected a file to move", Theme.Error()); // Displays an error message to the user
        }

        //Checks if the user has unsaved changes
        if(Shard.ChangesMade() != 0){
            DisplaySavePrompt();
        }

        //Lists all directories in the current selected directory
        ArrayList<File> Directories = Shard.GetAllFileDirectories(Shard.GetDataDirectory(), new ArrayList<>());
        ArrayList<String> DirectoryPaths = Shard.GetDirectoryPaths(Directories); // Adds all directory paths to the array list
        ArrayList<String> DirectoryNames = Shard.GetDirectoryNames(Directories); // Adds all directory names to the array list

        // Clears the console
        clear();

        //Starts the main loop
        while (!SystemStatus.InitiateShutdown) {
            DisplayTitle("Move File");
            DisplayDescription(HelpText);
            DisplaySeparator(true);

            //Checks if the current directory is empty
            if(Directories == null || (Directories != null && Directories.size() == 0)){
                DisplayText("There are no directories for you to move the selected file to");
                DisplaySeparator();
                DisplaySeparator(true);
                DisplaySystemText(ReturnHomeText);
                Scan.nextLine();
                clear();
                break;
            }

            //Displays the current selected file
            log("Selected File: ",true,Theme.System_Bold());
            log(Shard.GetCurrentSelectedFile().getName(),Theme.Text());
            log("Selected Directory: ",true,Theme.System_Bold());
            log(Shard.GetCurrentSelectedDirectory().getName(),Theme.Text());
            DisplaySeparator(true);

            log("Available Directories: ",true,Theme.System_Color());
            log(DirectoryNames.size(),Theme.Text());
            DisplaySeparator();

            //Displays the options
            for(int i = 0; i < DirectoryNames.size(); i++){
                DisplayText((i + 1) + ". " + DirectoryNames.get(i) + GetSpacing(DirectoryNames.get(i),SystemStatus.MaxFileLength) + " | Path: " + DirectoryPaths.get(i));
            }
            DisplaySeparator();

            //Gets the user input
            String UserInput = Scan.nextLine();

            //Checks if the user requires some help
            if(Verify(UserInput,"help")){
                clear();
                Help();
                continue;
            }

            //Checks if the user wants to go back
            if(Verify(UserInput,"back")){
                clear();
                break;
            }

            //Checks the user input
            if (VerifyNumber(UserInput, DirectoryNames.size()) || Verify(UserInput, DirectoryNames) != null) {
                String SelectedDirectory;

                // We add one because we initially add the root directory as part of
                //Checks if the user input is a number and parses it into the selected directory
                if (VerifyNumber(UserInput, DirectoryNames.size())) {
                    //Checks if the user input number is greater than the number of directories
                    if (Integer.parseInt(UserInput) > DirectoryNames.size()) {
                        clear();
                        log(InvalidInputText, Theme.Error()); // Displays an error message to the user
                        continue;
                    }

                    SelectedDirectory = DirectoryNames.get(Integer.parseInt(UserInput) - 1);
                } else if (Verify(UserInput, DirectoryNames) != null) {
                    SelectedDirectory = Verify(UserInput, DirectoryNames);
                } else {
                    clear();
                    log(InvalidInputText, Theme.Error()); // Displays an error message to the user
                    continue;
                }

                //Gets the selected directory and moves the file
                File MoveToDirectory = new File(DirectoryPaths.get(DirectoryNames.indexOf(SelectedDirectory)));

                //Gets the current selected file
                File SelectedFile = Shard.GetCurrentSelectedFile();

                // Moves the file to the selected directory
                boolean Status = Shard.MoveFile(MoveToDirectory);

                clear();
                // Checks if the file was moved successfully
                if(Status){
                    log(SelectedFile.getName() + " moved successfully",Theme.Success());
                }
                else{
                    log("Unable to move " + SelectedFile.getName(),Theme.Error());
                }

                // Breaks the loop
                break;
            }

            clear();
            // User has entered an invalid input
            log(InvalidInputText, Theme.Error());
        }
    }
}
