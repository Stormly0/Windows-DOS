package Library.EventHandler;

import Config.Color;
import Config.Sounds;
import Config.Theme;
import Core.SystemStatus;
import Library.GraphicsHandler.Console;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;



public class ErrorHandler {
    //Private Variables 

    private final static ThreadScheduler Scheduler = new ThreadScheduler(); // Used to schedule threads
    private final static SoundEngine SoundPlayer = new SoundEngine(); // Used to play sounds
    private final static ArrayList<String> ErrorLog = new ArrayList<>(); // Stores all the errors that have occurred in the system
    private final String MasterErrorLogPath = (SystemStatus.OperatingSystem.equalsIgnoreCase("windows") ? "src\\Core\\System\\MasterLog.txt" : "Core/System/MasterLog.txt");
    private String UserErrorLogPath = (SystemStatus.OperatingSystem.equalsIgnoreCase("windows")) ? "src\\Users\\" + SystemStatus.CurrentUsername + "\\System\\Log\\Log.txt" : "Users/" + SystemStatus.CurrentUsername + "/System/Log/Log.txt";


    //Private Methods 

    // Gets the spacing required for the message to be displayed on the screen properly 
    private String GetSpacing(String Message, int MaxSize){
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

    // Updates the user error log path if the system is running on different instances
    private boolean UpdatePath(){
        // Checks if the system has the user logged in 
        if(!SystemStatus.LoggedIn || SystemStatus.CurrentUsername == null){
            return false;
        } 

        // Updates the user error log path if the system is running on different instances
        String NewPath = (SystemStatus.OperatingSystem.equalsIgnoreCase("windows")) ? "src\\Users\\" + SystemStatus.CurrentUsername + "\\System\\Log\\Log.txt" : "Users/" + SystemStatus.CurrentUsername + "/System/Log/Log.txt";
        if(!NewPath.equalsIgnoreCase(UserErrorLogPath)){
            UserErrorLogPath = NewPath;
        }
        return true;
    }

    //Writes to a file with append set to true and given a string as the data
    private void Write(String Path, String Data, boolean Append) {
        // Writes the data to the file
        try {
            BufferedWriter Writer = new BufferedWriter(new FileWriter(Path, Append));

            // Writes the data to the file
            Writer.write(Data);
            Writer.newLine();

            // Closes the writer
            Writer.close();
        } catch (IOException e) {
            // Prints the error to the console
            Console.log("File Handler Error: " + e, Theme.Error());
        }
    }

    //Protected Methods

    // Displays the error screen for a major fault 
    protected void BugCheck(String StopCode, String Description){
        // Clears the screen 
        Console.clear(); 
        
        // Displays the error screen for a major fault
        // The stop code is the code that is displayed on the screen 
        final int ScreenLength = 150; 
        String FormatString = "";

        // Default Error Messages 
        final String DefaultTitle = "Windows has encountered a problem and needs to restart";
        final String FaultDescription = (Description != null) ? Description : "No description for the problem was provided";
        final String FaultCode = (StopCode != null) ? "FAULT: " + StopCode : "No fault code was provided";  

        // Logs the length of the error screen 
        for(int i = 0; i < ScreenLength; i++){
            FormatString += " ";
        }

        // Logs the top part 
        for(int i = 0; i < 2; i++){
            Console.log(FormatString,Color.Blue_Background);
        }

        Console.log(":(" + GetSpacing(":(",ScreenLength),Color.Blue_Background);
        Console.log(FormatString,Color.Blue_Background);
        Console.log("Windows has encountered a problem and needs to restart" + GetSpacing(DefaultTitle,ScreenLength),Color.Blue_Background);
        Console.log(FormatString,Color.Blue_Background);

        // Displays the error screen  
        
        Console.log(FormatString,Color.Blue_Background);
        Console.log(FaultCode + GetSpacing(FaultCode,ScreenLength),Color.Blue_Background); 
        Console.log(FaultDescription + GetSpacing(FaultDescription,ScreenLength),Color.Blue_Background);

        // Logs the bottom part
        for(int i = 0; i < 2; i++){
            Console.log(FormatString,Color.Blue_Background);
        }

        //Initiates a system shutdown
        SystemStatus.InitiateShutdown = true;


        //Waits a second before exiting the program
        Console.sleep(1000);


        // Exits the program
        System.exit(0);
    }
    
    //Public Methods

    // Returns the time stamp 
    public String GetTime(){
        // Gets the time
        Date Time = new Date(); 
        return Time.toString();
    }

    // Creates a log given the Stop Code, Description, and severity 
    public void log(String StopCode, String Description, int Severity){
        //Checks the current log mode
        if(!SystemStatus.LogMode.equalsIgnoreCase("All") && !SystemStatus.LogMode.equalsIgnoreCase("Error")){
            return;
        }

        // Gets the class name of where the error occurred
        String ClassName = Thread.currentThread().getStackTrace()[2].getClassName();

        // Gets the method name of where the error occurred
        String MethodName = Thread.currentThread().getStackTrace()[2].getMethodName();

        // Gets the line number of where the error occurred
        int LineNumber = Thread.currentThread().getStackTrace()[2].getLineNumber();

        // Severity 0: Informational
        // Severity 1: Warning
        // Severity 2: Fatal 
        String ErrorType = "";

        switch (Severity) {
            case 1 ->  ErrorType = "INFO";
            case 2 -> {
                ErrorType = "WARNING";
                Scheduler.Promise(() -> {
                    SoundPlayer.Play(Sounds.Error.toString());
                    return true;
                });
            }

            case 3 -> {
                ErrorType = "FATAL";
                Scheduler.Promise(() -> {
                    SoundPlayer.Play(Sounds.Crash.toString());
                    return true;
                });
            }
            default -> Console.log("Unknown Error Type");
        }
        
        // Formats the error message
        String DetailedErrorMessage = "Error Occurred in (" + ClassName + ") at line (" + LineNumber + ") in method (" + MethodName + ") with the following description: (" + Description + ")";

        //Formats the error message 
        String ErrorMessage = "[" + this.GetTime() + "][" + ErrorType + "][" + ((SystemStatus.CurrentUsername == null || !SystemStatus.LoggedIn) ? "User Not Logged In" : SystemStatus.CurrentUsername) + "] | Stop Code: " + StopCode + " | Description: " + Description + " | Severity: " + Severity + " | " + DetailedErrorMessage;

        // Adds the error to the log 
        ErrorLog.add(ErrorMessage); 

        // Logs the error into the master file log 
        Write(MasterErrorLogPath,ErrorMessage,true);

        // Checks if there is a specified user that is logged in 
        if(SystemStatus.LoggedIn && SystemStatus.CurrentUsername != null && UpdatePath()){
            //Logs the error in the user log as well  
            Write(UserErrorLogPath,ErrorMessage,true);
        }
        
        // Calls the bug check if the severity is fatal 
        if(Severity == 3){
            this.BugCheck(StopCode,Description);
        }
    }

    // Creates a log given the action, description
    public void action(String Task, String Description){
        // Checks the current log mode
        if(!SystemStatus.LogMode.equalsIgnoreCase("All") && !SystemStatus.LogMode.equalsIgnoreCase("Action")){
            return;
        }

        // Source | Gets where the action was called from

        // Gets the class name of where the error occurred
        String ClassName = Thread.currentThread().getStackTrace()[2].getClassName();

        // Gets the method name of where the error occurred
        String MethodName = Thread.currentThread().getStackTrace()[2].getMethodName();

        // Formats the action message 
        String ActionMessage = "[" + this.GetTime() + "][" + SystemStatus.CurrentUsername + "][ACTION] | Task: " + Task + " | Description: " + Description + " | Source: " + ClassName + " -> " + MethodName;

        // Adds the action to the log 
        //this.ActionLog.add(ActionMessage);

        // Writes the action to the master file log
        Write(MasterErrorLogPath,ActionMessage,true);

        // Checks if there is a specified user that is logged in 
        if(SystemStatus.LoggedIn && SystemStatus.CurrentUsername != null && UpdatePath()){
            //Logs the action in the user log as well  
            Write(UserErrorLogPath,ActionMessage,true);
        }
    }

    // Gets the Error Log 
    public ArrayList<String> GetErrorLog(){
        return ErrorLog;
    }
}
