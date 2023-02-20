package Core.Screens.WindowsDOS.Save;

import Config.Theme;
import Core.SystemStatus;
import Library.GraphicsHandler.Graphics;



public class SaveScreen extends Graphics {
    //Private Variables
    private final static String[] Options = {"Save","Save as","Discard"}; //Stores the options for the user to select from

    //Public Methods

    //Displays the help screen
    @Override
    public void Help(){
        //Displays the title
        DisplayTitle("Save Help");
        DisplayDescription("This screen allows you to save any changes made to the current selected file onto the disk");
        DisplaySeparator(true); //Displays an invisible separator to the user

        //Displays the help text
        DisplayText("You can save your changes to the selected file or discard your changes made to the selected file");
        DisplayText(BackText);

        //Displays the commands
        DisplaySeparator();
        DisplayDescription("System Commands");
        DisplayText("Save - Saves the changes made to the selected file");
        DisplayText("Save as - Saves the changes made to the selected file as a new file");
        DisplayText("Discard - Discards the changes made to the selected file");
        DisplayText("Back - Returns to the home screen");
        DisplaySeparator();

        //Displays the system text
        DisplaySeparator(true);
        DisplaySeparator(true); //Displays an invisible separator to the user
        DisplaySystemText(ReturnHomeText);
        Scan.nextLine(); //Waits for the user to press enter
        clear(); //Clears the console
    }

    //Displays the main screen
    @Override
    public void Display(){
        clear();

        //Starts the main loop
        while(!SystemStatus.InitiateShutdown){
            //Displays the title
            DisplayTitle("Save");
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
            if(Verify(UserInput,"back")){
                clear();
                break;
            }

            //Checks if the user wants to save the file
            if(Verify(UserInput,"save","1")){
                clear();
                // Saves the file
                boolean Status = Shard.Save();

                if(Status){
                    //Informs the user that their changes have been saved
                    log("Changes saved to " + Shard.GetCurrentSelectedFile().getName(),Theme.Success());
                }
                else {
                    //Informs the user that their changes have not been saved
                    log("Did not save " + Shard.GetCurrentSelectedFile().getName() + " no changes made",Theme.Error());
                }
                break;
            }

            //Checks if the user wants to save as a new file
            if(Verify(UserInput,"save as","2")){
                clear();

                //Prompts the user to enter a file name for the new file
                while(true){
                    DisplayTitle("Save as");
                    DisplaySystemText("Enter the name of the new file that you would like to save as");
                    DisplaySeparator(true);

                    //Gets the user input
                    String NewFileName = Scan.nextLine();

                    //Checks if the user wants to go back
                    if(Verify(NewFileName,"back")){
                        clear();
                        break;
                    }

                    // Checks for invalid characters
                    if(!NewFileName.matches("[a-zA-Z]+")){
                        clear();
                        log("Please enter a file name that only contains letters",Theme.Error());
                        continue;
                    }

                    //Adds a file extension of .txt to the file name
                    NewFileName += ".txt";

                    //Checks if the user has entered a file name that is too long
                    if(NewFileName.length() > SystemStatus.MaxFileLength){
                        clear();
                        log("Please enter a file name that is less than " + SystemStatus.MaxFileLength + " characters",Theme.Error());
                        continue;
                    }

                    // Checks if the file name already exists
                    if (Shard.CheckFileExists(NewFileName)) {
                        clear();
                        log("A file with the name " + NewFileName + " already exists",Theme.Error());
                        continue;
                    }

                    // Saves the file as a new file
                    boolean Status = Shard.SaveAs(NewFileName);

                    clear();
                    //Checks if the file was saved successfully
                    if(Status){
                        //Informs the user that their changes have been saved
                        log("Changes saved as " + NewFileName,Theme.Success());
                    }
                    else {
                        //Informs the user that their changes have not been saved
                        log("Unable to save as " + NewFileName,Theme.Error());
                    }
                    // Breaks the loop
                    break;
                }
                break;
            }

            //Checks if the user wants to discard the changes in the selected file
            if(Verify(UserInput,"discard","3")){
                clear();
                //Discards the changes in the selected file
                Shard.Discard();
                //Informs the user that their changes have been discarded
                log("Changes discarded in " + Shard.GetCurrentSelectedFile().getName(),Theme.Success());
                break;
            }

            clear();
            // User did not enter a valid option
            log(InvalidInputText,Theme.Error());
        }
    }
}
