package Core.Screens.WindowsDOS.Edit;

import Config.Theme;
import Core.SystemStatus;
import Library.GraphicsHandler.Graphics;



public class EditHome extends Graphics {
    //Private Instances
    private final static AddDataScreen AddData = new AddDataScreen();
    private final static RemoveDataScreen RemoveData = new RemoveDataScreen();
    private final static EncryptDataScreen EncryptData = new EncryptDataScreen();
    private final static DecryptDataScreen DecryptData = new DecryptDataScreen();

    //Private Variables
    private final static String[] Options = {
            "Add Data",
            "Remove Data",
            "Encrypt Data",
            "Decrypt Data"
    };
    //Public Methods

    //Displays the help screen
    @Override
    public void Help(){
        DisplayTitle("Edit Help");
        DisplayDescription("This screen allows you to edit a file");
        DisplaySeparator(true); // Displays an invisible separator to the user

        // Displays the help text
        DisplayText("This allows you to add or remove data from a selected file.");
        DisplayText(BackText);
        DisplaySeparator(true);

        //Displays the commands
        DisplaySeparator();
        DisplayDescription("System Commands");
        DisplayText("Add Data - Adds data to the selected file");
        DisplayText("Remove Data - Removes data from the selected file");
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
            DisplayTitle("Edit");
            DisplayDescription(HelpText);
            DisplaySeparator(true); // Displays an invisible separator to the user

            //Displays the selected file
            log("Selected File: ",true,Theme.System_Bold());
            log((Shard.GetCurrentSelectedFile() == null) ? "No File Selected" : Shard.GetCurrentSelectedFile().getName(),Theme.Text());
            DisplaySeparator(true); // Displays an invisible separator to the user

            //Displays the options
            DisplayOptions(Options); // Displays the options

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

            // Checks if the user wants to add data
            if(Verify(UserInput,new String[]{"add","add data","1"}) != null){
                clear();// Clears the console
                AddData.Display(); // Displays the add data screen
                continue; // Continues the loop
            }

            // Checks if the user wants to remove data
            if(Verify(UserInput,new String[]{"remove","remove data","2"}) != null){
                clear();// Clears the console
                RemoveData.Display(); // Displays the remove data screen
                continue; // Continues the loop
            }

            // Checks if the user wants to encrypt data
            if(Verify(UserInput,new String[]{"encrypt","encrypt data","3"}) != null){
                clear();// Clears the console
                EncryptData.Display(); // Displays the encrypt data screen
                continue; // Continues the loop
            }

            // Checks if the user wants to decrypt data
            if(Verify(UserInput,new String[]{"decrypt","decrypt data","4"}) != null){
                clear();// Clears the console
                DecryptData.Display(); // Displays the decrypt data screen
                continue; // Continues the loop
            }

            clear();
            //Informs the user that the option is invalid
            log(InvalidInputText, Theme.Error());
        }
    }
}
