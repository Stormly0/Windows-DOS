package Core.Screens.WindowsDOS.Edit;

import Config.Theme;
import Core.SystemStatus;
import Library.GraphicsHandler.Graphics;
import org.json.simple.JSONObject;

import java.io.File;
import java.util.LinkedHashMap;

public class DecryptDataScreen extends Graphics {

    //Public Methods

    //Displays the help screen
    @Override
    public void Help(){
        DisplayTitle("Decrypt Data Help");
        DisplayDescription("This screen allows you to decrypt data from a file");
        DisplaySeparator(true); // Displays an invisible separator to the user

        //TODO: Remember to make a better help screen here
        //Displays the help text
        DisplayText("This allows you to decrypt data from a file.");
        DisplayText(BackText);
        DisplaySeparator(true);

        //Displays the commands
        DisplaySeparator();
        DisplayDescription("System Commands");
        DisplayText("Password - The password that you would like to use to decrypt the file");
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
    public void Display() {
        clear();

        //Starts the main loop
        while (!SystemStatus.InitiateShutdown) {
            DisplayTitle("Decrypt Data");
            DisplayDescription(HelpText);
            DisplaySeparator(true); // Displays an invisible separator to the user

            //Gets the current file
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
            if(!Shard.IsEncrypted()){
                DisplaySystemText("This file is not encrypted");
                DisplaySeparator(true);
                DisplaySystemText(ReturnHomeText);
                Scan.nextLine(); // Waits for the user to press enter
                clear(); // Clears the console
                break;
            }

//TODO: REMEMBER TO SET THE ENCRYPTION KEY WHEN STARTING UP THE PROGRAM AND TRYING TO DECRYPT A FILE AFTER
            //Displays the input prompt
            DisplaySystemText("Enter the password that you used to encrypt " + Shard.GetCurrentSelectedFile().getName());

            //Gets the user input
            String UserInput = Scan.nextLine();

            //Checks if the user requires help
            if (Verify(UserInput, "help")) {
                clear();// Clears the console
                Help();// Displays the help screen
                continue; // Continues the loop
            }

            //Checks if the user wants to return to the previous screen
            if (Verify(UserInput, "back")) {
                clear(); // Clears the console
                break; // Breaks the loop
            }

            //Checks if the password is empty
            if(UserInput.isEmpty()){
                log("Please enter a password", Theme.Error());
                clear();
                continue;
            }

            //TODO: Check if the current password is correct

            //Gets the encryption key
            JSONObject EncryptedFiles = Shard.GetEncryptedFiles();

            //Gets the current file selected
            JSONObject CurrentEncryptedFile = (JSONObject) EncryptedFiles.get(Shard.GetCurrentSelectedFile().getName());

            // Separates the path and the password
            String Path = CurrentEncryptedFile.get("Path").toString();
            String Password = CurrentEncryptedFile.get("Key").toString();

            String HashKey = Security.GenerateHash(Password);

            if(!Verify(HashKey,Password,true)){
                log("The password is incorrect",Theme.Error());
                continue;
            }

            //Sets the encryption key
            Shard.SetEncryptionKey(UserInput);

            //Decrypts the file
            clear();
            if (Shard.DecryptFile(UserInput)) {
                DisplaySystemText("The file has been decrypted");
            } else {
                DisplaySystemText("The file could not be decrypted");
            }
            break; // Breaks the loop
        }
    }
}
