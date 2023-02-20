package Core.Screens.WindowsDOS.Return;

import Config.Theme;
import Core.SystemStatus;
import Library.GraphicsHandler.Graphics;
import java.io.File;
import java.util.ArrayList;


public class ReturnDirectoryScreen extends Graphics {
    //Private Variables
    private final static String[] Options = {"Previous Directory","Select Directory"}; //Stores the options for the user to select from

    //Public Methods

    //Displays the help screen
    @Override
    public void Help(){
        //Displays the header
        DisplayTitle("Return Directory Help");
        DisplayDescription("This screen allows you to return to the previous directory");
        DisplaySeparator(true);

        // Displays the help text
        DisplayText("Returns to the previous directory that you selected");
        DisplayText("Allows the user to specify the directory that they would like to return to");
        DisplayText(BackText);
        DisplaySeparator(true);

        //Displays the commands
        DisplaySeparator();
        DisplayDescription("System Commands");
        DisplayText("Previous Directory - Returns to the previous directory");
        DisplayText("Select Directory - Allows the user to select the directory that they would like to return to");
        DisplayText("Back - Returns to the home screen");
        DisplaySeparator();

        // Displays the system text
        DisplaySeparator(true);
        DisplaySeparator(true);
        DisplaySystemText(ReturnHomeText);
        Scan.nextLine();
        clear();
    }

    //Displays the main screen
    @Override
    public void Display(){
        clear();

        // Starts the main loop
        while(!SystemStatus.InitiateShutdown){
            DisplayTitle("Return Directory");
            DisplayDescription(HelpText);
            DisplaySeparator(true);

            //Displays the options
            DisplayOptions(Options);

            //Gets the user input
            String UserInput = Scan.nextLine();

            //Checks if the user requires help
            if(Verify(UserInput,"help")){
                clear();
                Help();
                continue;
            }

            //Checks if the user wants to return to the home screen
            if(Verify(UserInput,"back")) {
                clear();
                break;
            }

            // Checks if the user wants to return to the previous directory
            if(Verify(UserInput,new String[]{"previous","previous directory","1"}) != null){

                //Checks if the current directory is the root directory
                if(Shard.IsRootDirectory()){
                    clear();
                    log("Cannot return to the previous directory as the current directory is the root directory",Theme.Error());
                    continue;
                }

                clear();
                Shard.ReturnDirectory(); // Returns to the previous directory

                // Displays the success message
                log("Returned to " + Shard.GetCurrentSelectedDirectory().getName() + " successfully",Theme.Success());
                break; // Breaks the loop so that the user returns to the home screen
            }

            // Checks if the user wants to return to a specific directory
            if(Verify(UserInput,new String[]{"select","select directory","2"}) != null) {
                clear();

                while(true){
                    // Lists the directories
                    DisplayTitle("Select Directory");
                    DisplayDescription(HelpText);
                    DisplaySeparator(true); // Displays an invisible separator to the user

                    //Displays the system prompt
                    DisplaySystemText("Enter the directory that you would like to return to");

                    ArrayList<File> Directories = Shard.GetAllFileDirectories(Shard.GetDataDirectory(),new ArrayList<>()); //Lists the directory files
                    ArrayList<String> DirectoryNames = Shard.GetDirectoryNames(Directories);// lists all the directories that the user can select from
                    ArrayList<String> DirectoryPaths = Shard.GetDirectoryPaths(Directories); // Lists all the directory paths

                    //Checks if there are no directories
                    if(Directories == null || (Directories != null && Directories.size() == 1)){

                        DisplaySeparator(true);
                        DisplaySeparator();
                        //Displays that there are no directories
                        DisplayText("There are no directories to select from");
                        DisplaySeparator();
                        DisplaySeparator(true);
                        DisplaySystemText(ReturnHomeText);
                        // Waits for the user to press enter
                        String Input = Scan.nextLine();

                        //Checks if the user requires help
                        if(Verify(Input,"help")){
                            clear();
                            Help();
                            continue;
                        }

                        // Clears the screen
                        clear();
                        // Breaks the loop
                        break;
                    }

                    DisplaySystemText("Available Directories:");
                    DisplaySeparator();
                    //Displays the options
                    for(int i = 0; i < DirectoryNames.size(); i++){
                        DisplayText((i + 1) + ". " + DirectoryNames.get(i) + GetSpacing(DirectoryNames.get(i),SystemStatus.MaxFileLength) + " | Path: " + DirectoryPaths.get(i));
                    }
                    DisplaySeparator();

                    // Gets the user input
                    String Input = Scan.nextLine();

                    //Checks if the user wants to go back
                    if(Verify(Input,"back")){
                        clear();
                        break;
                    }

                    //Checks if the user requires help
                    if(Verify(Input,"help")){
                        clear();
                        Help();
                        continue;
                    }

                    //Checks if the user has entered a valid input
                    if(VerifyNumber(Input,DirectoryNames.size()) || Verify(Input,DirectoryNames) != null){
                        // Gets the file path
                        String FilePath = null;
                        String FileName = null; // Stores the file name

                        //Checks whether the user entered a number or a file name
                        if (VerifyNumber(Input, DirectoryNames.size())) {

                            // Checks if the current selected file number is outside the range of the files in the current directory
                            if (Integer.parseInt(Input) - 1 > DirectoryNames.size()) {
                                // Clears the screen
                                clear();
                                log(InvalidInputText, Theme.Error());
                                continue; // Continues the loop
                            }
                            FileName = DirectoryNames.get(Integer.parseInt(Input) - 1);
                            FilePath = DirectoryPaths.get(Integer.parseInt(Input) - 1);
                        }
                        else if (Verify(Input, DirectoryNames) != null) {
                            FileName = Verify(Input, DirectoryNames);
                            FilePath = Verify(Input, DirectoryNames);
                        }
                        else {
                            // Clears the screen
                            clear();
                            log(InvalidInputText, Theme.Error());
                            continue; // Continues the loop
                        }

                        //Gets the file that the user has selected using the file name
                        File SelectedDirectory = null;

                        // Loops through the files and finds the file that the user has selected
                        for(File Directory : Directories){
                            if(Directory.getPath().equals(FilePath)){
                                SelectedDirectory = Directory;
                                break;
                            }
                        }

                        // Checks if the file is null
                        if(SelectedDirectory == null){
                            // Clears the screen
                            clear();
                            // Logs the error to the user
                            log(FileName + " is not found",Theme.Error());
                            // Logs the error
                            ErrorHandle.log("Return Directory Select",FileName + " is null and cannot be found!",2);

                            continue; // Continues the loop
                        }

                        // Selects the file
                        Shard.SetDirectory(SelectedDirectory);

                        clear(); // Clears the screen
                        //Informs the user that the directory they have returned to was successful
                        log("Selected directory \"" + Shard.GetCurrentSelectedDirectory().getName() + "\" successfully",Theme.Success());

                        // Breaks the loop
                        break;
                    }


                    // The user did not select a valid option
                    clear();
                    log(InvalidInputText,Theme.Error());
                }

                break;
            }

            // User has entered an invalid input
            clear();
            log(InvalidInputText,Theme.Error());
        }
    }
}
