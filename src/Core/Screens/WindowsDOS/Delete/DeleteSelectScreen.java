package Core.Screens.WindowsDOS.Delete;

import Config.Theme;
import Core.SystemStatus;
import Library.GraphicsHandler.Graphics;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

// NEW OPTION :)
public class DeleteSelectScreen extends Graphics {

    //Public Methods

    //Displays the help screen
    public void Help(){
        DisplayTitle("Delete Help");
        DisplayDescription("This screen allows you to delete a file or directory");
        DisplaySeparator(true); // Displays an invisible separator to the user

        // Displays the help text
        DisplayText("This menu allows you to select any file within the current selected directory to delete");
        DisplayText(BackText);
        DisplaySeparator(true);

        //Displays the commands
        DisplaySeparator();
        DisplayDescription("System Commands");
        DisplayText("Back - Returns to the previous screen");
        DisplaySeparator();

        DisplaySeparator(true);
        DisplaySeparator(true); // Displays an invisible separator to the user
        DisplayDescription(ReturnHomeText);
        Scan.nextLine();
        clear(); // Clears the console
    }


    //Displays the main screen
    public void Display(){
        //Clears the screen
        clear();

        //Displays the main screen
        while(!SystemStatus.InitiateShutdown){
            //Displays the header
            DisplayTitle("Delete Select File Screen");
            DisplayDescription(HelpText);
            DisplaySeparator(true);

            //Gets all the selected files within the current directory
            LinkedHashMap<File, HashMap<String,String>> Data = Shard.ListDirectory(); // Gets all the files within the current directory
            ArrayList<String> FileNames = new ArrayList<>(); // Creates a new array list to store all the file names
            ArrayList<String> FilePaths = new ArrayList<>(); // Creates a new array list to store all the file paths

            //Checks if there are no files within the current directory
            if(Data == null || (Data != null && Data.isEmpty())){
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


            //Puts all the files into a string array
            for(File fileData : Data.keySet()){
                FileNames.add(fileData.getName()); // Adds the file name to the array list
                FilePaths.add(fileData.getPath()); // Adds the file path to the array list
            }

            //Displays the files to the user
            for(int i = 0; i < FileNames.size(); i++){
                DisplayText((i + 1) + ". " + FileNames.get(i) + GetSpacing(FileNames.get(i), SystemStatus.MaxFileLength) + " | Path:" + FilePaths.get(i));
            }


            //Gets the user input indicate which file they want to delete
            String Input = Scan.nextLine();

            //Checks if the user requires help
            if(Verify(Input,"help")){
                clear(); //Clears the console
                Help(); //Displays the help screen
                continue; //Continues the main loop
            }

            //Checks if the user wants to go back
            if(Input.equals("back")){
                //Returns to the previous screen
                break;
            }

            //Checks if the user has selected a name or a number
            if(VerifyNumber(Input,FileNames.size()) || Verify(Input,FileNames) != null){
                File SelectedFile = null; // Creates a new file object to store the selected file

                //Checks if the user has selected a number
                if(VerifyNumber(Input,FileNames.size())) {

                    //Checks if the number is within the range of the file names
                    if(Integer.parseInt(Input) > FileNames.size()){
                        //Displays the error message
                        clear(); // Clears the screen
                        log(InvalidInputText, Theme.Error());
                        continue;
                    }

                    //Gets the file name from the number
                    SelectedFile = new File(FilePaths.get(Integer.parseInt(Input) - 1));
                }
                else if(Verify(Input,FileNames) != null){
                    //Gets the file name from the name
                    SelectedFile = new File(FilePaths.get(FileNames.indexOf(Verify(Input,FileNames))));
                }

                //Checks if the selected file is null
                //User has entered an invalid input
                if(SelectedFile == null){
                    //Displays the error message
                    clear(); // Clears the screen
                    log(InvalidInputText, Theme.Error());
                    continue;
                }

                //Verifies with the user if they want to delete the selected file
                while(true){
                    //Displays the header
                    DisplayTitle("Delete File");
                    DisplayDescription(HelpText);
                    DisplaySeparator(true);

                    //Warns the user if they really want to delete the file
                    log("Are you sure you want to delete the file " + SelectedFile.getName() + "? [Y|N]", Theme.Warning());

                    //Gets the user input
                    Input = Scan.nextLine();

                    //Checks if the user requires help
                    if(Verify(Input,"help")){
                        clear(); //Clears the console
                        Help(); //Displays the help screen
                        continue; //Continues the main loop
                    }

                    //Checks the user input
                    if(Verify(Input,"y","yes")){
                        //Deletes the file
                        boolean Status = Shard.DeleteFile(SelectedFile);

                        //Checks the status
                        clear(); // Clears the screen
                        if(Status){
                            //Displays the success message
                            log("Successfully deleted the file " + SelectedFile.getName(), Theme.Success());
                        }
                        else{
                            //Displays the error message
                            log("Failed to delete the file " + SelectedFile.getName(), Theme.Error());
                        }
                        break;
                    }
                    else if(Verify(Input,"n","no")){
                        // Returns to the previous screen
                        break;
                    }

                    //User has entered invalid input
                    clear();
                    log("Please enter either Y or N", Theme.Error());
                }

                //Returns to the previous screen
                break;
            }


            //User has entered invalid input
            clear();
            log(InvalidInputText, Theme.Error());
        }
    }
}
