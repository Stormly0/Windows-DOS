package Core.Screens.Startup;

import Config.Theme;
import Core.SystemStatus;
import Library.GraphicsHandler.Graphics;


public class LoginScreen extends Graphics {
    // Private Instances
    // Screens
    private final ResetPasswordScreen ResetPassword = new ResetPasswordScreen(); // Used to display the reset password screen
    // Public Methods

    // Displays the help screen for the current screen
    @Override
    public void Help(){
        DisplayTitle("Login Help");
        DisplayDescription("Having trouble logging into the system?");
        DisplaySeparator(true); // Displays an invisible separator to the user

        //Displays the help text
        DisplayText("To log in to the system, please enter your username and password for your account that you have selected");
        DisplayText("if you have forgotten your password, please enter the command \"reset\" to reset your password");
        DisplayText(BackText);

        //Displays the commands
        DisplaySeparator(true);
        DisplaySeparator();
        DisplayDescription("System Commands");
        DisplayText("Reset - Resets the password for the account you have selected");
        DisplayText("Back - Returns to the previous screen");
        DisplaySeparator();
        DisplaySeparator(true);

        DisplaySeparator(true); // Displays an invisible separator to the user
        DisplayDescription("Enter any key to return to the login screen");
        Scan.nextLine(); // Waits for the user to press enter
        clear(); // Clears the screen
    }

    // Displays the current menu screen to the user.
    @Override
    public void Display(){
        // Clears the screen
        clear();
        MainLoop:
        while(!SystemStatus.InitiateShutdown){
            //Formats the screen
            DisplayTitle("Login");
            DisplayDescription(HelpText);
            DisplayText("Please select the account you would like to use");
            DisplaySeparator(true); // Displays an invisible separator to the user

            // Displays the registered accounts to the user
            String[] Accounts = Data.GetAccountNames();

            //Checks if there are any accounts currently registered
            if(Accounts.length == 0){
                // Displays an error message to the user
                DisplaySystemText("There are no accounts currently registered to the system");
                DisplaySeparator(true); // Displays an invisible separator to the user
                DisplaySystemText("Enter any key to return to the previous screen");
                Scan.nextLine(); // Waits for the user to press enter
                clear(); // Clears the screen
                break;
            }

            // Loops through the accounts and displays them to the user
            for(int i = 0; i < Accounts.length; i++){
                DisplayText((i+1) + ". " + Accounts[i]);
            }

            // Gets the user input
            String UserInput = Scan.nextLine();

            //Checks if the user requires some help
            if(Verify(UserInput,"help")){
                // Clears the screen
                clear();
                // Displays the help screen to the user
                Help();

                // Skips an iteration of the loop
                continue;
            }

            // Checks if the user wants to go back to the previous screen
            if(Verify(UserInput,"back")){
                // Clears the screen
                clear();
                // Returns to the previous screen
                break;
            }

            // Checks if the user input is valid
            if(VerifyNumber(UserInput,Accounts.length) || Verify(UserInput,Accounts) != null){
                // Gets the account name
                String AccountName = null;

                // Checks whether the user has entered a number or a string
                if(VerifyNumber(UserInput,Accounts.length)){

                    //Checks if the user has entered a number within the range of the account size
                    if(Integer.parseInt(UserInput) - 1 > Accounts.length){
                        // Clears the screen
                        clear();
                        // Otherwise the user  input is invalid
                        log("The account you selected does not exist", Theme.Error());
                        log("Please select an account listed below", Theme.Error());
                        continue;
                    }

                    AccountName = Accounts[GetNumber(UserInput,Accounts.length) - 1];
                } else if(Verify(UserInput,Accounts) != null){
                    AccountName = Verify(UserInput,Accounts);
                }
                else if(Verify(UserInput,Accounts) == null){
                    // Clears the screen
                    clear();
                    // Otherwise the user  input is invalid
                    log("The account you selected does not exist", Theme.Error());
                    log("Please select an account listed below", Theme.Error());
                    continue;
                }

                //Sets the current system user to the account name
                SystemStatus.CurrentUsername = AccountName;

                // Gets the account password
                String AccountPassword = (String) Data.GetAccounts().get(AccountName);

                /// STARTS THE MAIN PASSWORD LOOP

                //Clears the screen
                clear();
                while(true){
                    DisplayTitle("Login"); // Displays the title to the user
                    DisplayDescription(HelpText);
                    DisplaySeparator(true);
                    // Gets the user input for the password
                    DisplaySystemText("Please enter the password for " + AccountName + " account");

                    UserInput = Scan.nextLine();

                    // Checks if the user requires some help
                    if(Verify(UserInput,"help")){
                        // Clears the screen
                        clear();
                        // Displays the help screen to the user
                        Help();
                        // Skips an iteration of the loop
                        continue;
                    }

                    // Checks if the user wants to go back
                    if(Verify(UserInput,"back")){
                        // Clears the screen
                        clear();
                        // Returns to the previous screen
                        break MainLoop;
                    }

                    // Checks if the user has entered the reset command
                    if(Verify(UserInput,"reset")){
                        // Clears the screen
                        clear();

                        // Sets the account that the user wants to reset the password for
                        SystemStatus.PasswordResetAccount = AccountName;

                        // Starts the reset password screen
                        ResetPassword.Display();

                        //Updates the account password
                        AccountPassword = (String) Data.GetAccounts().get(AccountName);

                        // Sets the Password reset account to null
                        SystemStatus.PasswordResetAccount = null;

                        // Skips an iteration of the loop
                        continue;
                    }

                    //Creates the hash for the user input
                    UserInput = Security.GenerateHash(UserInput);

                    // Checks if the password is correct
                    if(Verify(UserInput,AccountPassword,true)){
                        // Sets the current user and initializes the system
                        SystemStatus.LoggedIn = true;
                        SystemStatus.CurrentUsername = AccountName;

                        // Checks if the user is an admin
                        if(AccountName.equals("Admin")){
                            SystemStatus.IsAdmin = true;
                        }

                        // Logs the action
                        ErrorHandle.action("Login","User " + AccountName + " has logged in to the system");

                        //Clears the screen
                        clear();

                        // Informs the user that they have logged in
                        log("Logged in as " + AccountName, Theme.Success());

                        // Initializes the datashard class
                        Shard.Initialize();

                        //Sets a random encryption shift for the encryption class
                        // Everytime the user logs in the encryption shift will be different which will make decryption a bit more difficult but considering it is a simple encryption it is not that hard to decrypt

                        sleep(1000);
                        // breaks the loop
                        break MainLoop;
                    }
                    else{
                        // Clears the screen
                        clear();

                        // Informs the user that the password is incorrect
                        log("The password you entered is incorrect", Theme.Error());
                    }
                }
            }

            // Clears the console
            clear();

            // Otherwise the user  input is invalid
            log("The account you selected does not exist", Theme.Error());
            log("Please select an account listed below", Theme.Error());
        }
    }
}
