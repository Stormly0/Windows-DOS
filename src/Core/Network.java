package Core;

import Config.Theme;
import Library.DataHandler.Security;
import Library.EventHandler.ErrorHandler;
import Library.EventHandler.ThreadScheduler;
import Library.GraphicsHandler.Console;
import org.json.simple.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.concurrent.CompletableFuture;



public class Network {
    //Private Instances
    private final ErrorHandler ErrorHandle = new ErrorHandler(); // Creates a new error handler instance
    private final ThreadScheduler Scheduler = new ThreadScheduler(); // Creates a new thread scheduler instance
    private final Security Cryptography = new Security(); // Creates a new security instance

    //Private Variables
    private final String ServerURL = "https://ClientToWebServer.stormly123.repl.co"; // The Address to the server
    private boolean Cooldown = false; // Checks if the server is on cool down

    //Private Methods

    //Creates a default data set to be sent to the server
    private LinkedHashMap<String, Object> CreateDefault() {
        LinkedHashMap<String, Object> Data = new LinkedHashMap<>(); // Creates a new linked hash map
        Data.put("Auth", "15561F5124B8D334BDF6E6D197966"); // Adds the authentication key to the data set
        Data.put("User", SystemStatus.CurrentUsername); // Adds the current username to the data set
        return Data; // Returns the data set
    }

    //Formats the data to be sent to the server 
    private JSONObject ParseJSON(LinkedHashMap<String, Object> Data) {
        return new JSONObject(Data); // Returns the JSON object
    }


    // Makes a post request to the specified server
    public boolean Post(JSONObject Data) {
        Cooldown = true; // Sets the cool-down to true
        //Creates a new URL 
        try {
            final URL Target = new URL(ServerURL);

            //Sets up a http connection to a php server
            HttpURLConnection ConnectionOut = (HttpURLConnection) Target.openConnection();
            ConnectionOut.setRequestMethod("POST");
            ConnectionOut.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            ConnectionOut.setDoOutput(true);
            ConnectionOut.setDoInput(true);
            ConnectionOut.setReadTimeout(15000);

            // Formats the data to be sent to the server 
            String DataOut = Data.toString();

            // Writes the data and streams it to the target server 
            OutputStreamWriter DataStreamOut = new OutputStreamWriter(ConnectionOut.getOutputStream(), StandardCharsets.UTF_8);
            DataStreamOut.write(DataOut);
            DataStreamOut.flush();

            //Gets the response from the server
            int ResponseCode = ConnectionOut.getResponseCode();


            // Gets the response data 
            String ResponseData = ConnectionOut.getResponseMessage();

            //Checks the response code to see if the connection was terminated because the servers are no longer running
            if(ResponseCode == 503){
                //Logs the error
                ErrorHandle.log("SERVER_TERMINATED", "The server is no longer operational! Thank you for supporting the Callisto Protocol.txt! [RESPONSE]", 2);
                SystemStatus.ServerTerminated = true; // Sets the server terminated to true
                Cooldown = false; // Sets the cool down to false

                // Closes the connection
                ConnectionOut.disconnect();
                DataStreamOut.close();

                return false;
            }

            //Checks the response code 
            if (ResponseCode != 200) {
                //Logs the error 
                ErrorHandle.log("POST_RES_FAIL", "Connection in to " + ServerURL + " was unsuccessful! (" + ResponseCode + "|" + ResponseData + ") [RESPONSE]", 2);

                Cooldown = false; // Sets the cool down to false

                // Closes the connection
                ConnectionOut.disconnect();
                DataStreamOut.close();

                return false;
            }


            // Logs the action
            ErrorHandle.action("POST_REQ_SUCCESS", "Connection out to " + ServerURL + " was successful! (" + ResponseCode + "|" + ResponseData + ") [REQUEST]");

            // Reads the response Data
            BufferedReader DataStreamIn = new BufferedReader(new InputStreamReader(ConnectionOut.getInputStream()));

            // Waits for the response data 
            String InputLine = DataStreamIn.readLine();

            //Closes the connection
            DataStreamIn.close();
            DataStreamOut.close();
            ConnectionOut.disconnect();

            // Sets the cool down to false
            Cooldown = false;

            // Checks the response data 
            if (InputLine != null && InputLine.equalsIgnoreCase("200")) {
                ErrorHandle.action("POST_RES_SUCCESS", "Connection in to " + ServerURL + " was successful! (" + ResponseCode + "|" + ResponseData + ") [RESPONSE]");
                return true;
            } else if (InputLine != null && (InputLine.equalsIgnoreCase("400") || InputLine.equalsIgnoreCase("404"))) {
                ErrorHandle.log("POST_RES_FAIL", "Connection in to " + ServerURL + " was unsuccessful! (" + ResponseCode + "|" + ResponseData + ") [RESPONSE]", 2);
                return false;
            } else if (InputLine != null && InputLine.equalsIgnoreCase("503")) {
                ErrorHandle.log("SERVER_TERMINATED", "The server is no longer operational! Thank you for supporting the Callisto Protocol.txt! [RESPONSE]", 2);
                SystemStatus.ServerTerminated = true; // Sets the server terminated to true
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            Cooldown = false; // Sets the cooldown to false

            if (SystemStatus.DebugMode) {
                System.out.println("Error: " + e);
            }

            // Logs the Error 
            ErrorHandle.log("POST_REQ_NO_RES", "Post request received no response (SERVER COLD START) | Description: " + e + " | [NO-RESPONSE]", 2);

            // Returns false
            return false;
        }
    }

    //Public Methods

    //Checks whether a provided email address is valid
    public boolean CheckEmail(String Email) {
        return Email.matches("^[A-Za-z0-9+_.-]+@(.+)$"); // Returns the result of the check
    }

    /**
     * Sends a request for a password reset
     * @param Email User's email to send the password reset token to
     * @return Password Reset Token
     */
    public String SendPasswordResetPrompt(String Email) {
        //Checks if the server is terminated
        if(SystemStatus.ServerTerminated){
            // Error Log
            ErrorHandle.log("PASS_RESET_FAIL", "Post request to " + ServerURL + " was unsuccessful! (Server is terminated) [NO-REQUEST]", 2);
            return "Terminated";
        }

        // Checks if the server is on cooldown 
        if (Cooldown) {
            // Error Log
            ErrorHandle.log("PASS_RESET_FAIL", "Post request to " + ServerURL + " was unsuccessful! (Server is on cooldown) [NO-REQUEST]", 2);
            Console.log("Slow down! You are sending too many requests!", Theme.Error());
            return null;
        }

        //Checks if the email is valid 
        // Regex 
        if (!CheckEmail(Email)) {
            Console.log("Invalid Email!", Theme.Error());
            // Logs the error
            ErrorHandle.log("PASS_RESET_FAIL", "Post request to " + ServerURL + " was unsuccessful! (Invalid Email) [NO-REQUEST]", 2);
            return null;
        }

        // Creates the required Data needed to send to the server
        LinkedHashMap<String, Object> Data = this.CreateDefault(); // Creates a new data set
        String SecurityCode = Cryptography.GenerateSecurityCode(); // Generates a security code

        // Adds the required data to the data set
        Data.put("Email", Email); // Adds the email to the data set
        Data.put("PassResetKey", SecurityCode);
        Data.put("Action", "PasswordReset"); // Adds the action to the data set


        // Sends the data to the server by creating a new promise
        CompletableFuture<Boolean> Promise = Scheduler.Promise(() -> this.Post(this.ParseJSON(Data)));


        // NOTE: Remember to use the thenAcceptAsync() method to prevent a deadlock within the process that involves the awaitTermination() method.
        // This happens because the awaitTermination() method is blocking the thread it was executing on and the execution thread is waiting for the promise to be completed.
        // Basically the thread is waiting for itself to complete, and it will never complete because it is waiting for itself to complete.
        // Waits for the promise to be completed
        Promise.thenAcceptAsync((Result) -> {
            //Checks if the global debug variable is set to true | if so then it will display additional debug information
            if (SystemStatus.DebugMode) {
                Console.log("Status: " + Result);

                // Handle reject or resolve
                if (Result) {
                    Console.log("Promise resolved | Connection Successful", Theme.Success());
                } else {
                    Console.log("Promise rejected | Connection Failed", Theme.Error());
                }
            }

            // checks the result
            if (Result) {
                ErrorHandle.action("PASS_RESET_REQUEST", "Password reset was successfully sent to " + Email + " | Security Code: " + SecurityCode);
            } else {
                ErrorHandle.log("PASS_RESET_FAIL", "Post request to " + ServerURL + " was unsuccessful!", 2);
            }

        });

        // returns the security code
        return SecurityCode;
    }

    /**
     * Sends a server termination request
     *
     */
    public void TerminateServer() {
        //Creates the action request
        LinkedHashMap<String, Object> Data = this.CreateDefault(); // Creates a new data set
        Data.put("Pass", "AC4372BZF"); // Adds the password to the data set
        Data.put("Action", "Terminate"); // Adds the action to the data set

        // Sends the data to the server by creating a new promise
        CompletableFuture<Boolean> Promise = Scheduler.Promise(() -> this.Post(this.ParseJSON(Data)));

        // Waits for the promise to be completed
        Promise.thenAcceptAsync((Result) -> {
            //Checks if the global debug variable is set to true | if so then it will display additional debug information
            if (SystemStatus.DebugMode) {
                Console.log("Status: " + Result);

                // Handle reject or resolve
                if (Result) {
                    Console.log("Promise resolved | Connection Successful", Theme.Success());
                } else {
                    Console.log("Promise rejected | Connection Failed", Theme.Error());
                }
            }

            // checks the result
            if (Result) {
                ErrorHandle.action("SERVER_TERMINATED", "The server was terminated successfully!");
                SystemStatus.ServerTerminated = true; // Sets the server terminated to true
            } else {
                ErrorHandle.log("SERVER_TERMINATED_FAIL", "The server was not terminated successfully!", 2);
            }
        });

    }

}
