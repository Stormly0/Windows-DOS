package Core.Screens.Startup;
import Config.Theme;
import Core.Network;
import Core.SystemStatus;
import Library.GraphicsHandler.Graphics;


public class ResetPasswordScreen extends Graphics {

    //Private Instances
    private final Network Network = new Network(); // Used to handle the network of the system

    //Private Variables
    // Creates a debounce variable that prevents the user from spam email requests
    private static final long DebounceTime = 1; // Minutes
    private static long PastTime = System.currentTimeMillis(); // The time that the user last requested an email

    // Displays the help screen for the current screen
    @Override
    public void Help(){
        DisplayTitle("Reset Password Help");
        DisplayDescription("Having trouble resetting your password?");
        DisplaySeparator(true); // Displays an invisible separator to the user

        //Displays the help text
        DisplayText("To reset your password, please enter the email address that you want to send the reset code to! This code is used to confirm that you are the owner of the account.");
        DisplayText("The currently support email provides are: Gmail | Yahoo | Outlook | Hotmail");
        DisplayText("If you do not receive an email, please check your spam folder or enter 'resend' to resend the email");
        DisplayText(BackText);

        //Display the commands
        DisplaySeparator(true);
        DisplaySeparator();
        DisplayDescription("System Commands");
        DisplayText("Resend - Resends the reset code to the email address you have entered [Cooldown: " + DebounceTime + " minutes]");
        DisplayText("Back - Returns to the previous screen");
        DisplaySeparator();
        DisplaySeparator(true);

        DisplaySeparator(true); // Displays an invisible separator to the user
        DisplayDescription("Enter any key to return to the reset password screen");
        Scan.nextLine(); // Waits for the user to press enter
        clear(); // Clears the screen
    }


    // Displays the current menu screen to the user.
    @Override
    public void Display(){
        // Clears the screen
        clear();

        //Local Variables
        String ResetCode = null; // Stores the reset code that was sent to the user
        String LockedEmailAddress = null; // Stores the email address that the user has chosen
        boolean SentEmail = false; // Checks if the user has already sent an email

        // Starts the main loop
        MainLoop:
        while(true){
            DisplayTitle("Password Reset");
            DisplayDescription(HelpText);
            DisplaySeparator(true); // Displays an invisible separator to the user
            DisplayText("Please enter the email address that you want to send the reset code to");

            // Email Input
            String Email = Scan.nextLine();

            // Checks if the user requires some help
            if(Verify(Email,"help")){
                // Clears the screen
                clear();
                // Displays the help screen
                Help();
                continue; // Continues the loop
            }

            //Checks if the user wants to go back
            if(Verify(Email,"back")){
                clear(); // Clears the screen
                break; // Breaks out of the loop
            }

            // Checks to make sure that the email address is not blank
            if(Email.isEmpty()){
                clear();
                DisplaySystemText("Please enter an email address");
                continue;
            }

            // Checks to make sure that the email address is valid
            if(!Network.CheckEmail(Email)){
                clear();
                log("The email address you have entered is invalid, please try again",Theme.Error());
                continue;
            }

            //Clears the previous screen
            clear();

            // Starts a secondary loop
            while(true){
                DisplayTitle("Password Reset");
                DisplayDescription(HelpText);
                DisplaySeparator(true); // Displays an invisible separator to the user
                DisplayText("Please enter the reset code that was sent to your email address");
                DisplayText("If you did not receive an email, please enter 'resend' to resend the email");

                //Checks if the servers are no longer accepting requests due to the server no longer being online
                if(SystemStatus.ServerTerminated || (ResetCode != null && ResetCode.equalsIgnoreCase("Terminated"))){
                    DisplaySeparator(true);
                    DisplaySeparator();
                    log("Thank you for being apart of the Callisto Protocol.txt!",Theme.Error());
                    log("Unfortunately, the servers are no longer accepting password reset requests",Theme.Error());
                    log("Support for Windows DOS has been discontinued",Theme.Error());
                    log("Please contact the developer for more information",Theme.Error());
                    DisplaySeparator();
                    DisplaySeparator(true);
                    DisplaySystemText(ReturnHomeText);
                    Scan.nextLine(); // Waits for the user to press enter
                    clear(); // Clears the screen
                    break MainLoop; // Breaks out of the main loop
                }

                // Checks to debounce time to see if it is less than the current time
                // Checks if the time past is greater than the debounce time to ensure that the user cannot spam the email
                if(!SentEmail || (System.currentTimeMillis() - PastTime) > (DebounceTime * 1000 * 60)) {
                    // Updates the pastime
                    PastTime = System.currentTimeMillis();

                    // Updates the sent email variable
                    SentEmail = true;

                    //Sets the locked email address as it is the email address that the user has chosen
                    LockedEmailAddress = Email;

                    // Sends the reset code to the user specified email
                    ResetCode = Network.SendPasswordResetPrompt(Email); // Sends the reset code to the user

                    //Checks the reset code
                    if(ResetCode.equalsIgnoreCase("Terminated")){
                        continue; // Continues the loop
                    }
                }

                // Checks the user input code against the reset code
                String UserInput = Scan.nextLine();

                // Checks if the user requires some help
                if(Verify(UserInput,"help")){
                    // Clears the screen
                    clear();
                    // Displays the help screen
                    Help();
                    continue; // Continues the loop
                }

                // Checks if the user wants to resend the email || Checks to debounce time
                if(Verify(UserInput,"resend") && (System.currentTimeMillis() - PastTime) > (DebounceTime * 1000 * 60)){
                    // Clears the screen
                    clear();
                    // Updates the pastime
                    PastTime = System.currentTimeMillis();
                    // Displays the help screen
                    DisplaySystemText("Resending the email...");

                    //Sends the reset code to the user specified email
                    ResetCode = Network.SendPasswordResetPrompt(LockedEmailAddress); // Sends the reset code to the user

                    sleep(1000); // Waits for 1 second so the user can see the message
                    clear(); // Clears the screen
                    continue; // Continues the loop
                }
                else if(Verify(UserInput,"resend") && (System.currentTimeMillis() - PastTime) < (DebounceTime * 1000 * 60)){
                    // Clears the screen
                    clear();
                    // Informs the user that they have to wait before they can resend the email
                    DisplaySystemText("Sorry, you have to wait " + DebounceTime + " minute before you can resend another email");
                    continue; // Continues the loop
                }

                // Checks if the user input code matches the reset code
                if(Verify(UserInput,ResetCode)){
                    // Clears the screen
                    clear();
                    // Displays the help screen
                    log("Reset code accepted, please enter a new password",Theme.Success());
                    break; // Breaks the loop
                }
                else{
                    // Clears the screen
                    clear();
                    // Displays the help screen
                    log("The reset code you have entered is invalid, please try again",Theme.Error());
                }
            }

            // Starts a password reset loop
            while(true){
                // Prompts the user to enter a new password
                DisplayTitle("Password Reset");
                DisplayDescription(HelpText);
                DisplaySeparator(true); // Displays an invisible separator to the user
                DisplayText("Please enter a new password");

                // Password Input
                String Password = Scan.nextLine();

                // Checks if the user requires some help
                if(Verify(Password,"help")){
                    // Clears the screen
                    clear();
                    // Displays the help screen
                    Help();
                    continue; // Continues the loop
                }

                // Checks if the password is valid
                if(Password.isEmpty()){
                    // Clears the screen
                    clear();
                    // Informs the user that the password cannot be blank
                    log("The password cannot be blank", Theme.Error());
                    continue ; // Continues the loop
                }

                //Creates a new hash key for the password
                String HashKey = Security.GenerateHash(Password);

                //Resets the password
                Data.UpdateAccountPassword(SystemStatus.PasswordResetAccount,HashKey);

                //Clears the screen
                clear();

                // Breaks out of the loop
                break MainLoop;
            }
        }
    }
}
