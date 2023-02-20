package Library.GraphicsHandler;

import Config.Theme;
import Core.Datastore;
import Core.SystemStatus;
import Library.DataHandler.DataShard;
import Library.DataHandler.Security;
import Library.EventHandler.ErrorHandler;
import java.util.ArrayList;
import java.util.Scanner;


public abstract class Graphics extends Console implements VerifyInput {
    // Public Variables
    public static final Security Security = new Security(); // Stores the security object for the user to encrypt and decrypt data
    public static final ErrorHandler ErrorHandle = new ErrorHandler(); // Stores the error handler object for the user to handle errors
    public static final Datastore Data = new Datastore(); // Stores the datastore object for the user to access data
    public static final DataShard Shard = new DataShard(); // Stores the data shard object for the user to access data
    public static final Scanner Scan = new Scanner(System.in); // Stores the scanner object for the user to input data
    public static final String HelpText = "Enter 'help' if you require any assistance"; // Stores the help text for the user
    public static final String BackText = "Enter 'back' to go back to the previous screen"; // Stores the back text for the user
    public static final String InvalidInputText = "Please select from one of the options listed below"; // Stores the invalid input text for the user
    public static final String ReturnHomeText = "Enter any key to return to the home screen";// Stores the return home text for the user
    // Abstract Methods
    public abstract void Help(); // Displays the help screen for the current screen

    public abstract void Display(); // Displays the current menu screen to the user.

    // Public Methods

    //Gets the space required for the message to be displayed on the screen evenly
    public String GetSpacing(String Message, int MaxSize){
        // Gets the spacing required for the message to be displayed on the screen
        int SpacingRequired = MaxSize - Message.length();
        String Spacing = "";
        // Spaces out the string by the required amount
        for(int i = 0; i < SpacingRequired; i++){
            Spacing += " ";
        }
        // Returns the spacing
        return Spacing;
    }

    // Displays the title color to the user
    public void DisplayTitle(String Text){
        Console.log(Text,Theme.System_Header()); // Displays the title to the user
    }

    // Displays the options to a user
    public void DisplayOptions(String[] Options){
        for(int i = 0; i < Options.length; i++){
            //Checks if the number is greater than 9 and reduces the space
            if((i + 1) > 9){
                Console.log((i+1) + "." + Options[i],Theme.Text()); // Displays the option to the user
                continue;  // Skips the rest of the loop
            }
            Console.log((i+1) + ". " + Options[i],Theme.Text()); // Displays the option to the user
        }
    }

    //Display options to the user but given an array list
    public void DisplayOptions(ArrayList<String> Options){
        for(int i = 0; i < Options.size(); i++) {
            //Checks if the number is greater than 9 and reduces the space
            if ((i + 1) > 9) {
                Console.log((i + 1) + "." + Options.get(i), Theme.Text()); // Displays the option to the user
                continue;  // Skips the rest of the loop
            }
            Console.log((i + 1) + ". " + Options.get(i), Theme.Text()); // Displays the option to the user
        }
    }

    // Displays the description to a user
    public void DisplayDescription(String Description){
        Console.log(Description,Theme.System_Description()); // Displays the description to the user
    }

    //Displays an invisible separator to the user
    public void DisplaySeparator(boolean Invisible){
        if(Invisible){
            Console.log(" ",Theme.Text()); // Displays the separator to the user
        } else {
            Console.log("--------------------------------------------------",Theme.Separator()); // Displays the separator to the user
        }
    }

    //Displays a text to the user
    public void DisplayText(String Text){
        Console.log(Text,Theme.Text()); // Displays the text to the user
    }

    // Displays the system text to the user
    public void DisplaySystemText(String Text){
        Console.log(Text,Theme.System_Color()); // Displays the system text to the user
    }

    // Displays a separator to the user
    public void DisplaySeparator(){
        Console.log("--------------------------------------------------",Theme.Separator()); // Displays the separator to the user
    }

    /**
     * Formats a data file given its metadata
     * @param FileName String - Name of the file
     * @param Type String - Type of the file
     * @param Size String - Size of the file
     * @param DateCreated String - Date the file was created
     * @param DateModified String - Date the file was modified
     * @param LastAccessed String - Date the file was last accessed
     * @param Encrypted String - Whether the file is encrypted or not
     * return String - Formatted data file text
     */
    public String FormatFileMetadata(String FileName,String Type, String Size, String DateCreated, String DateModified, String LastAccessed,String Encrypted){
        String FormattedString = null;
        if(Encrypted.equalsIgnoreCase("Encrypted")){
            FormattedString = (Console.RED + "[LOCKED] " + Console.RESET) + GetSpacing("[LOCKED]",10) + FileName + GetSpacing(FileName, SystemStatus.MaxFileLength) + " | File Type: " + Type + GetSpacing(Type,10) + " | File Size: " + Size + GetSpacing(Size,10) + " | Date Created: " + DateCreated + " | Date Modified: " + DateModified + " | Last Accessed: " + LastAccessed;
        }
        else if(Encrypted.equalsIgnoreCase("Decrypted")){
            FormattedString = (Console.GREEN + "[UNLOCKED] " + Console.RESET) + GetSpacing("[UNLOCKED]",10) + FileName + GetSpacing(FileName, SystemStatus.MaxFileLength) + " | File Type: " + Type + GetSpacing(Type,10) + " | File Size: " + Size + GetSpacing(Size,10) + " | Date Created: " + DateCreated + " | Date Modified: " + DateModified + " | Last Accessed: " + LastAccessed;
        }
        return FormattedString;
    }

    //Displays the save prompt for the user to save the data
    // Displays the save prompt screen as the user has unsaved changes
    public void DisplaySavePrompt(){
        while(true){
            log("You have unsaved changes in " + Shard.GetCurrentSelectedFile().getName() + "!", Theme.Warning());
            log("Would you like to save your changes? [Y/N]", Theme.Text());

            //Gets the user input
            String UserInput = Scan.nextLine();

            //Checks if the user wants to save their changes
            if(Verify(UserInput, "y", "yes")){
                //Saves the changes to the file
                Shard.Save();
                clear();
                break;
            }
            else if(Verify(UserInput, "n", "no")){
                //Discards the changes to the file
                Shard.Discard();
                clear();
                break;
            }
            else{
                clear();
                //Displays an error message to the user
                log("Please enter either Y or N", Theme.Error());
            }
        }
    }

    // USER INPUT VERIFICATION METHODS

    @Override
    public String Verify(String Input, String[] Expected){
        // Loops through the expected array and checks if the input is matches any of the options and if it does return the option selected
        for (String Data : Expected) {
            if (Input.equalsIgnoreCase(Data)) {
                return Data;
            }
        }
        return null;
    }

    @Override
    public boolean Verify(String Input,String Expected, boolean CaseSensitive) {
        // Checks if the input is case-sensitive
        if(CaseSensitive){
            // Checks if the input is equal to the expected input
            return Input.equals(Expected);
        }
        else{
            // Checks if the input is equal to the expected input
            return Input.equalsIgnoreCase(Expected);
        }
    }

    @Override
    public boolean Verify(String Input, String Expected, String Alternative){
        // Checks if the input is equal to either the expected input or the alternative input
        return Input.equalsIgnoreCase(Expected) || Input.equalsIgnoreCase(Alternative);
    }

    @Override
    public boolean Verify(String Input, String Expected){
        // Checks if the input is equal to the expected input
        return Input.equalsIgnoreCase(Expected);
    }

    @Override
    public String Verify(String Input, ArrayList<String> Expected){
        // Checks if the input is equal to any of the expected inputs
        return Expected.contains(Input) ? Input : null;
    }

    // Verifies the user input depending on the options provided
    public boolean VerifyNumber(String Input, int Options){
        String Regex;

        // Checks if the number of options is greater than 10 or equal to 10
        if(Options > 9){
            // Generates regex that can match one and two-digit numbers
            int Ones = (((Options + 1))%10); // Gets the ones place
            int Tens = (((Options + 1)/10)%10); // Gets the tens place

            Regex = "^[1-" + Tens + "][0-" + Ones + "]$|^[1-9]"; // Generates the regular expression
        }
        else{
            //Checks if the number of options plus 1 is greater than 9 if so switch the regex to match one and two-digit numbers
            if((Options + 1) > 9){
                // Generates regex that can match one and two-digit numbers
                int Ones = (((Options + 1))%10); // Gets the ones place
                int Tens = (((Options + 1)/10)%10); // Gets the tens place

                Regex = "^[1-" + Tens + "][0-" + Ones + "]$|^[1-9]"; // Generates the regular expression
            }
            else{
                // Generates regex that can match one-digit numbers
                Regex = "^[1-" + (Options + 1) + "]$"; // Generates the regular expression
            }
        }

        // Checks to see if the input matches the regular expression
        return Input.matches(Regex);
    }

    // Gets the number that the user has inputted
    public int GetNumber(String Input, int Options){
        // Checks if the input is a number
        if(VerifyNumber(Input,Options)){
            // returns the number that the user has inputted
            return Integer.parseInt(Input);
        }
        else{
            // Returns -1 to indicate that the input does not match any of the cases
            return -1;
        }
    }
}