package Core.Screens.WindowsDOS.Edit;

import Config.Theme;
import Core.SystemStatus;
import Library.GraphicsHandler.Graphics;

import java.io.File;

public class EncryptDataScreen extends Graphics {

    //Public Methods

    //Displays the Help screen
    @Override
    public void Help(){

    }

    //Displays the main screen
    @Override
    public void Display(){
        clear();

        // Starts the main loop
        while(!SystemStatus.InitiateShutdown){
            //Displays the header
            DisplayTitle("Encryption");
            DisplayDescription(HelpText);
            DisplaySeparator(true); // Displays an invisible separator to the user

            File CurrentFile = Shard.GetCurrentSelectedFile();

            log("Selected File: ",true, Theme.System_Bold());
            log(CurrentFile.getName(),Theme.Text());
            DisplaySeparator(true);

            //Checks if the current file is empty
            if(Shard.GetCurrentFileData().size() == 0){
                DisplaySystemText("This file is empty");
                DisplaySeparator(true);
                DisplaySystemText(ReturnHomeText);
                Scan.nextLine(); // Waits for the user to press enter
                clear(); // Clears the console
                break;
            }

            //Checks if the file is already encrypted
            if(Shard.IsEncrypted()){
                DisplaySystemText("This file is already encrypted");
                DisplaySeparator(true);
                DisplaySystemText(ReturnHomeText);
                Scan.nextLine(); // Waits for the user to press enter
                clear(); // Clears the console
                break;
            }

            //Displays the input prompt
            DisplaySystemText("Please enter the encryption password that you would like to use for " + CurrentFile.getName());

            //Gets the user input
            String UserInput = Scan.nextLine();

            //Checks if the user requires help
            if(Verify(UserInput,"help")){
                clear(); // Clears the console
                Help(); // Displays the help screen
                continue; // Continues the loop
            }

            //Checks if the user wants to return to the previous screen
            if(Verify(UserInput,"back")){
                clear(); // Clears the console
                break; // Returns to the previous screen
            }


            //Checks if the password is empty
            if(UserInput.isEmpty()){
                log("Please enter a password",Theme.Error());
                clear();
                continue;
            }

            //Checks to make sure if the user has entered a valid password
            if(!UserInput.matches("^[a-zA-Z0-9]+$")){
                log("Please enter a password that only contains letters and numbers",Theme.Error());
                clear();
                continue;
            }

            //Sets the encryption key
            Shard.SetEncryptionKey(UserInput);

            //Encrypts the file
            boolean Status = Shard.EncryptFile();

            //Checks if the file was encrypted successfully
            clear();
            if(Status) {
                log("The file was encrypted successfully", Theme.Success());
            }
            else{
                log("The file could not be encrypted",Theme.Error());
            }
            break;
        }
    }
}
