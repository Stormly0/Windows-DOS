package Core.Screens.WindowsDOS.View;

import Core.SystemStatus;
import Library.GraphicsHandler.Graphics;
import java.util.ArrayList;

public class ViewFileScreen extends Graphics {
    //Public Methods

    //Displays the help screen
    @Override
    public void Help(){
        //Displays the header
        DisplayTitle("View File Help");
        DisplayDescription("This screen allows you to view the contents of a selected file");
        DisplaySeparator(true);

        //Displays the help text
        DisplayText("The contents of the selected file will be displayed as a list of lines with line numbers in the console");
        DisplayText("That set number that is indicated on the left represents the data that is stored in that specific line");
        DisplayText(BackText);

        //Displays the system text
        DisplaySeparator(true);
        DisplaySystemText(ReturnHomeText);
        Scan.nextLine();
        clear();
    }

    //Displays the main screen
    @Override
    public void Display(){
        clear();

        //Gets the file data
        ArrayList<String> FileData = Shard.GetCurrentFileData();

        // Starts the main loop
        while(!SystemStatus.InitiateShutdown){
            DisplayTitle("View File");
            DisplayDescription(HelpText);
            DisplaySeparator(true);

            //Displays the data in the current file as a list of lines
            DisplaySystemText("Data in " + Shard.GetCurrentSelectedFile().getName() + ":");
            DisplaySeparator();
            //Checks if there is data in the file
            if(FileData.size() == 0){
                DisplayText("There is no data in this file");
            }
            else {
                DisplayOptions(FileData); //Displays the data in the file
            }

            DisplaySeparator();

            // Waits for the user to press a key to continue
            DisplaySeparator(true);
            DisplaySystemText(ReturnHomeText);

            String UserInput = Scan.nextLine();

            //Checks if the user requires some help
            if(Verify(UserInput,"help")){
                clear();
                Help();
                continue;
            }
            clear();
            break; // Exits the loop
        }
    }
}
