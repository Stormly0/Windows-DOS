package Core.Screens.Startup;

import Config.Theme;
import Library.GraphicsHandler.Graphics;



public class RegisterScreen extends Graphics {
    // Displays the help screen for the current screen
    @Override
    public void Help(){
        DisplayTitle("Register Help");
        DisplayDescription("Having trouble registering an account?");
        DisplaySeparator(true); // Displays an invisible separator to the user

        //Displays the help text
        DisplayText("To register an account, please enter your username and password for your account that you want to create!");
        DisplayText(BackText);
        DisplaySeparator(true);

        //Displays commands
        DisplaySeparator();
        DisplayDescription("System Commands");
        DisplayText("Back - Returns to the previous screen");
        DisplaySeparator();
        DisplaySeparator(true);

        DisplaySeparator(true); // Displays an invisible separator to the user
        DisplaySystemText("Enter any key to return to the register screen");
        Scan.nextLine(); // Waits for the user to press enter
        clear(); // Clears the screen
    }

    // Displays the current menu screen to the user.
    @Override
    public void Display(){
        // Clears the screen
        clear();
        MainLoop:
        while(true){
            // Displays the startup screen to the user
            DisplayTitle("Register");
            DisplayDescription(HelpText);
            DisplaySeparator(true); // Displays an invisible separator to the user

            // Username Input
            log("Username: ",true,Theme.Text());
            String Username = Scan.nextLine();

            //Checks if the user requires some help
            if(Verify(Username,"help")){
                // Clears the screen
                clear();
                // Displays the help screen
                Help();
                continue; // Continues the loop
            }

            // Checks if the user wants to go back to the previous screen
            if(Verify(Username,"back")){
                clear(); // Clears the screen
                break; // Breaks out of the loop
            }

            // Checks to make sure that the username is not already taken
            if(Data.AccountExists(Username)){
                clear();
                DisplaySystemText("The username you have entered is already taken, please try again");
                continue;
            }

            //Checks to make sure if the username is not blank
            if(Username.isEmpty()){
                clear();
                log("Please enter a username",Theme.Error());
                continue;
            }

            // Checks to make sure that the username entered is not invalid
            if(!Username.matches("[a-zA-Z]+")){
                clear();
                log("Your username can only contain letters! No special characters or spaces are allowed!",Theme.Error());
                continue;
            }

            //Checks to make sure that the username is not longer than 12 characters long
            if(Username.length() > 12){
                clear();
                log("Please enter a username that is no longer than 12 characters",Theme.Error());
                continue;
            }

            // Password Input

            while(true){
                clear();
                DisplayTitle("Register"); // Displays the title of the screen
                DisplayDescription(HelpText); // Displays the help text to the user
                DisplaySeparator(true);// Displays an invisible separator to the user
                log("Username: " + Username,Theme.Text()); // Displays the username to the user
                log("Password: ",true,Theme.Text()); // Displays the password to the user
                String Password = Scan.nextLine();

                //Checks if the user requires some help
                if(Verify(Password,"help")){
                    // Clears the screen
                    clear();
                    // Displays the help screen
                    Help();
                    continue; // Continues the loop
                }

                // Checks if the user wants to go back to the previous screen
                if(Verify(Password,"back")){
                    clear(); // Clears the screen
                    break MainLoop; // Breaks out of the loop
                }

                // Checks to make sure that the password is not blank
                if(Password.isEmpty()){
                    clear();
                    log("Please enter a password",Theme.Error());
                    continue;
                }

                // Checks to make sure that the password entered is no longer than 40 characters
                if(Password.length() > 40){
                    log("Please enter a password that is no longer than 40 characters", Theme.Error());
                    continue;
                }

                //Clears the screen
                clear();

                // Creates a new account with the username and password
                boolean Status = Data.CreateNewUserDirectory(Username); // Creates a new directory for the user

                //Creates a new Hash key for the current selected password
                String HashKeyString = Security.GenerateHash(Password);

                Data.AddAccount(Username,HashKeyString); // Adds the account to the system

                //Checks the status of the account creation
                if(Status){
                    log(Username + " has been successfully registered!",Theme.Success());
                }
                else{
                    log("An error occurred while trying to register " + Username,Theme.Error());
                }

                DisplaySeparator(true); // Displays an invisible separator to the user
                DisplaySystemText("Enter any key to return to the startup screen");
                Scan.nextLine(); // Waits for the user to press enter
                clear(); // Clears the screen
                break MainLoop; // Breaks out of the loop
            }
        }
    }
}
