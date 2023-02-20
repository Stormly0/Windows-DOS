package Core.Screens.WindowsDOS.Edit;

import Config.Theme;
import Core.SystemStatus;
import Library.GraphicsHandler.Graphics;
import java.util.ArrayList;



public class RemoveDataScreen extends Graphics {
    //Public Methods

    //Displays the help screen
    @Override
    public void Help(){
        DisplayTitle("Remove Data Help");
        DisplayDescription("This screen allows you to remove data from a file");
        DisplaySeparator(true); // Displays an invisible separator to the user

        // Displays the help text
        DisplayText("Enter the contents of the data that you would like to remove or the number index that represents the data that you would like to remove");
        DisplayText("If you would like to remove all the data from the file enter \"all\"");
        DisplayText(BackText);
        DisplaySeparator(true);

        // Displays Commands
        DisplaySeparator();
        DisplayDescription("System Commands");
        DisplayText("Number | Data - Removes the data that is represented by the number index or the data itself");
        DisplayText("Back - Goes back to the previous screen");
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
            DisplayTitle("Remove Data");
            DisplayDescription(HelpText);
            DisplaySeparator(true); // Displays an invisible separator to the user

            //Gets the file name
            String FileName = Shard.GetCurrentSelectedFile().getName();
            // Gets the data in the current file
            ArrayList<String> FileData = Shard.GetCurrentFileData();

            //Checks if there is any data inside the file
            if(FileData == null || (FileData != null && FileData.size() == 0)){
                DisplayText(FileName + " is empty");
                DisplaySeparator(true); // Displays an invisible separator to the user
                DisplaySystemText(ReturnHomeText);
                Scan.nextLine(); // Waits for the user to press enter
                clear(); // Clears the console
                break; // Breaks the loop
            }

            //Displays the system prompt
            DisplaySystemText("Enter the data that you would like to remove from " + FileName);

            // Displays the file data within the file to the console
            DisplayText("Data in " + FileName + ":");
            DisplaySeparator();
            DisplayOptions(FileData);
            DisplaySeparator();
            DisplaySeparator(true); // Displays an invisible separator to the user

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

            // Checks if the user wants to remove all the data from the file
            if(Verify(UserInput,"all")){
                // Loops through the file data and removes all of it
                for (String Data : FileData) {
                    Shard.Remove(Data);
                }

                clear();
                // Informs the user that all the data has been removed
                log("All the data has been removed from " + FileName, Theme.Success());
            }

            //Checks that the user input is valid whether it is a number or a string of the data
            if(VerifyNumber(UserInput,FileData.size()) || Verify(UserInput,FileData) != null){
                // Temp variable
                String TempDataName;

                //Checks whether the user input is a number or a data string
                if(VerifyNumber(UserInput,FileData.size())){
                    //Checks if the number is greater than the size of the file data
                    if(Integer.parseInt(UserInput) > FileData.size()){
                        clear();
                        //Informs the user that the number is invalid
                        log(InvalidInputText, Theme.Error());
                        continue; // Continues the loop
                    }

                    //Gets the data from the file data array list given the index of the number
                    TempDataName = FileData.get(Integer.parseInt(UserInput) - 1);
                }
                else if(Verify(UserInput,FileData) != null){
                    //Gets the data from the file data array list given the data string
                    TempDataName = Verify(UserInput,FileData);
                }
                else{
                    clear();
                    //Informs the user that the input is invalid
                    log(InvalidInputText, Theme.Error());
                    continue; // Continues the loop
                }

                // Checks if the TempFileName is null [Final Failsafe]
                if(TempDataName == null){
                    clear();
                    //Informs the user that the input is invalid
                    log(InvalidInputText, Theme.Error());
                    continue; // Continues the loop
                }

                clear();

                //Removes the data from the file
                Shard.Remove(TempDataName);

                clear();
                //Informs the user that the data has been removed
                log("\""+ TempDataName + "\" has been removed from " + FileName, Theme.Success());

                // Breaks out of the loop
                break;
            }

            // User input is invalid
            clear();
            //Informs the user that the input is invalid
            log(InvalidInputText, Theme.Error());
        }
    }
}
