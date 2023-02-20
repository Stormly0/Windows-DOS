package Library.DataHandler;

import Config.Theme;
import Library.EventHandler.ErrorHandler;
import Library.GraphicsHandler.Console;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;


// NOTE: Do not use the error handling class in here as it will cause a circular dependency and the program will create an infinite loop due to the nature how the error handler is structured
public final class FileHandler {
    // Private Instances
    private final static ErrorHandler ErrorHandle = new ErrorHandler(); // Creates a new error handler instance

    //Private Variables
    private final static int FileWriteLoopDetection = 10; // Stores the number of times the write methods can be called within one second before the program will terminate itself
    private static int FileWriteCount = 0; // Stores the number of times the write methods have been called within one second
    private static long FileWritePastTime = System.currentTimeMillis(); // Stores the time the write methods were last called

    //Private Functions

    /**
     * logs a write event to prevent an infinite loop where the file writers run off until the program crashes
     */
    private void WriteEvent(){
        // Gets the current time and subtracts it by the time the write methods were last called
        long TimeDifference = System.currentTimeMillis() - FileWritePastTime;

        // Checks if the time difference is less than half a second
        // This checks to ensure that no file run off loop can occur in the system
        if(TimeDifference < 500){
            // Increments the file write count
            FileWriteCount++;

            // Checks if the file write count is greater than the loop detection
            if(FileWriteCount > FileWriteLoopDetection){
                // Logs the error
                ErrorHandle.log("File Write Error", "File write loop detected | Error: File write methods have been called " + FileWriteCount + " times within " + TimeDifference + " milliseconds", 3);
            }
        }
        else{
            // Resets the pastime
            FileWritePastTime = System.currentTimeMillis();

            // Resets the file write count
            FileWriteCount = 0;
        }
    }

    /**
     * Recursively deletes all files within a directory
     * @param File File Directory
     * @return boolean
     */
    private boolean ForceDeleteFiles(File File){
        // Checks to make sure the file is a directory
        File[] Files = File.listFiles();

        //Checks if the files is empty
        if(Files == null || Files.length == 0){
            // Logs that the directory is empty
            ErrorHandle.action("File Delete","No files found in " + File.getPath());
            return true;
        }

        //Loops through all the files
        for(File CurrentFile : Files){
            // Checks if the file is a directory or a file
            if(CurrentFile.isFile()){
                // Deletes the file
                boolean Status = CurrentFile.delete();

                // Checks if the file is deleted
                if(Status){
                    // Logs the action
                    ErrorHandle.action("File Delete","File deleted from  " + CurrentFile.getPath() + " successfully");
                }
                else{
                    // Logs the error
                    ErrorHandle.log("File Delete Error", "Unable to delete file | Path: " + CurrentFile.getPath() + " | Error: File was not deleted", 2);
                }
            }
            else if(CurrentFile.isDirectory()){
                // Recursively deletes all the files
                ForceDeleteFiles(CurrentFile);
            }
        }
        return true;
    }

    /**
     * Recursively deletes all empty directories within a directory
     * @param File File Directory
     * @return boolean
     */
    private boolean ForceDeleteDirectories(File File){
        // Checks to make sure the file is a directory
        File[] Files = File.listFiles();

        // Checks if the files is empty
        if(Files == null || Files.length == 0){
            // Deletes the directory since it is empty
            boolean Status = File.delete();

            if(Status){
                // Logs the action
                ErrorHandle.action("Directory Delete","Directory deleted from  " + File.getPath() + " successfully");
                return true;
            }
            else{
                // Logs the error
                ErrorHandle.log("Directory Delete Error", "Unable to delete directory | Path: " + File.getPath() + " | Error: Directory was not deleted", 2);
                return false;
            }
        }

        //Loops through all the files
        for(File CurrentFile : Files){
            // Checks if the file is a directory or a file
            if(CurrentFile.isDirectory()){
                ForceDeleteDirectories(CurrentFile);
            }
        }

        // Deletes the parent directory since it is considered "empty" since all the files have been deleted and all sub directories have been deleted
        return ForceDeleteDirectories(File);
    }

    //Public Functions
    // TODO: Make sure to specify the action log for these functions
    //Checks if a specified path provided is valid 
    public boolean CheckPath(String Path){
        //Checks if the path is empty
        if(Path.isEmpty() || Path.isBlank()){
            return false; //Returns false if the path is empty
        }

        //Checks if the path is valid 
        try{
            //Checks if the path is a valid path
            File TestingFile = new File(Path);

            //Returns false if the path is invalid
            return TestingFile.exists() && TestingFile.canWrite() && TestingFile.canRead(); //Returns true if the path is valid
        }
        catch(Exception e){
            //Prints the error to the console 
            Console.log(e,Theme.Error());
            return false; //Returns false if the path is invalid
        }
    }


    // JSON METHODS [SPECIAL METHODS]

    // Returns the default constructor for writing to a Json file
    public LinkedHashMap<String,Object> ConstructJSONTemplate(){
        // Creates a new LinkedHashMap
        // Returns the template
        return new LinkedHashMap<>();
    }

    // Writes to a JSON file
    public boolean WriteJSON(File File, JSONObject Data){
        // Writes the data to the file
        try{
            BufferedWriter BufferWriter = new BufferedWriter(new FileWriter(File));

            // Writes the data to the file
            BufferWriter.write(Data.toJSONString());

            // Closes the writer
            BufferWriter.close();

            // Logs the action
            ErrorHandle.action("File Write","File written to  " + File.getPath() + " successfully");

            // Returns true if the data was written to the file
            return true;
        }
        catch(IOException e){
            // Logs the error
            ErrorHandle.log("File Write Error", "Unable to write to file | Path: " + File.getPath() + " | Error: " + e.getMessage(), 3);

            return false;
        }
    }

    // Reads a JSON file
    public JSONObject ReadJSON(File File){
        JSONParser Parser = new JSONParser(); // Creates a new JSON parser
        // Reads the file
        try{
            // Creates a new buffered reader
            BufferedReader BufferReader = new BufferedReader(new FileReader(File));
            String Line = BufferReader.readLine(); // Reads the first line of the file

            //Checks if the file is empty
            if(Line == null){
                //Creates an empty JSON object
                JSONObject EmptyObject = new JSONObject();

                // Logs the action
                ErrorHandle.action("File Read","File read from  " + File.getPath() + " successfully");

                //Closes the buffered reader
                BufferReader.close();

                // Returns null if the file is empty
                return EmptyObject;
            }

            // Reads the file
            JSONObject FileData = (JSONObject) Parser.parse(Line);

            // Closes the reader
            BufferReader.close();

            // Logs the action
            ErrorHandle.action("File Read","File read from  " + File.getPath() + " successfully");

            // Returns the JSON object
            return FileData;
        }
        catch(IOException e){
            // Logs the error
            ErrorHandle.log("File Read Error", "Unable to read from file | Path: " + File.getPath() + " | Error: " + e.getMessage(), 2);

            // Prints the error to the console
            Console.log(e,Theme.Error());
            return null;
        }
        catch(ParseException e){
            // Logs the error
            ErrorHandle.log("File JSON Parse Error", "Unable to parse data from file | Path: " + File.getPath() + " | Error: " + e, 2);

            // Prints the error to the console
            Console.log(e,Theme.Error());
            return null;
        }
    }

    //Moves a file to another directory
    public boolean Move(File File,File Location){
        //Moves the file to the specified location
        try{
            //Moves the file to the specified location
            boolean Status = File.renameTo(new File(Location,File.getName()));

            //Checks if the file was moved
            if(!Status){
                // Logs the error
                ErrorHandle.log("File Move Error", "Unable to move file | Path: " + File.getPath() + " | Error: " + "File was not moved", 2);

                //Returns false if the file was not moved
                return false;
            }

            // Logs the action
            ErrorHandle.action("File Move","File moved from  " + File.getPath() + " to " + Location.getPath() + " successfully");

            //Returns true if the file was moved
            return true;
        }
        catch(Exception e){
            // Logs the error
            ErrorHandle.log("File Move Error", "Unable to move file | Path: " + File.getPath() + " | Error: " + e.getMessage(), 2);

            // Prints the error to the console
            Console.log(e,Theme.Error());
            return false;
        }
    }

    // DEFAULT METHODS

    // Writes to the file with append set to true
    public boolean Write(String Path, ArrayList<String> Data,boolean Append){
        WriteEvent();
        // Writes the data to the file
        try{
            BufferedWriter Writer = new BufferedWriter(new FileWriter(Path,Append));

            // Writes the data to the file 
            for (String Line : Data){
                Writer.write(Line);
                Writer.newLine();
            }

            // Closes the writer
            Writer.close();

            // Logs the action
            ErrorHandle.action("File Write","File written to  " + Path + " successfully");

            // Returns true if the data was written to the file
            return true;
        }
        catch(IOException e){
            // Logs the error
            ErrorHandle.log("File Write Error", "Unable to write to file | Path: " + Path + " | Error: " + e.getMessage(), 2);
            return false; 
        }
    }

    // Writes to the file with append set to false
    public boolean Write(String Path, ArrayList<String> Data){
        WriteEvent();
        // Writes the data to the file
        try{
            BufferedWriter Writer = new BufferedWriter(new FileWriter(Path));

            // Writes the data to the file 
            for (String Line : Data){
                Writer.write(Line);
                Writer.newLine();
            }

            // Closes the writer
            Writer.close();

            // Logs the action
            ErrorHandle.action("File Write","File written to  " + Path + " successfully");

            // Returns true if the data was written to the file
            return true;
        }
        catch(IOException e){
            // Logs the error
            ErrorHandle.log("File Write Error", "Unable to write to file | Path: " + Path + " | Error: " + e.getMessage(), 2);

            // Prints the error to the console 
            Console.log("File Handler Error: " + e,Theme.Error()); 
            return false; 
        }
    }

    // Writes to the file when given a string [USED FOR JSON FILE WRITING]
    public boolean Write(String Path,String Data){
        WriteEvent();
        try{
            BufferedWriter Writer = new BufferedWriter(new FileWriter(Path));

            Writer.write(Data);

            // Closes the writer
            Writer.close();

            // Logs the action
            ErrorHandle.action("File Write","File written to  " + Path + " successfully");

            // Returns true if the data was written to the file
            return true;
        }
        catch(IOException e){
            // Logs the error
            ErrorHandle.log("File Write Error", "Unable to write to file | Path: " + Path + " | Error: " + e.getMessage(), 2);

            // Prints the error to the console 
            Console.log(e,Theme.Error()); 
            return false; 
        }
    }

    // Writes to the file when given a file object 
    public boolean Write(File File, ArrayList<String> Data){
        WriteEvent();
        // Writes the data to the file
        try{
            BufferedWriter Writer = new BufferedWriter(new FileWriter(File));
            // Writes the data to the file 
            for (String Line : Data){
                Writer.write(Line); 
                Writer.newLine();   
            }

            // Closes the writer
            Writer.close();

            // Logs the action
            ErrorHandle.action("File Write","File written to  " + File.getPath() + " successfully");

            // Returns true if the data was written to the file
            return true;
        }
        catch(IOException e){
            // Logs the error
            ErrorHandle.log("File Write Error", "Unable to write to file | Path: " + File.getPath() + " | Error: " + e.getMessage(), 2);

            // Prints the error to the console 
            Console.log(e,Theme.Error()); 
            return false; 
        }
    }

    // Writes as a new file
    public boolean WriteAs(String Filename,File File, ArrayList<String> Data){
        WriteEvent();
        // Creates a new file object
        File NewFile = new File(File.getParentFile() + "/" + Filename);
        boolean Status = Write(NewFile,Data);

        // Checks the status and logs the action
        if(Status){
            ErrorHandle.action("File Write As","File written to  " + NewFile.getPath() + " successfully");
        }
        else{
            ErrorHandle.log("File Write As Error", "Unable to write to file | Path: " + NewFile.getPath(), 2);
        }
        return Status;
    }

    // Creates a new empty file
    public boolean Create(String Name, File SelectedDirectory){
        // Creates a new file object
        File NewFile = new File(SelectedDirectory,Name);

        // Checks if the file exists
        if(NewFile.exists()){
            // Logs the error
            ErrorHandle.log("File Create Error", "File already exists | Path: " + NewFile.getPath(), 2);

            // Returns false
            return false;
        }
        else{
            // Creates the file
            try{
                boolean Status = NewFile.createNewFile();

                // Checks the status and logs the action
                if(Status){
                    ErrorHandle.action("File Create","File created at  " + NewFile.getPath() + " successfully");
                }
                else{
                    ErrorHandle.log("File Create Error", "Unable to create file | Path: " + NewFile.getPath(), 2);
                }

                // Returns true
                return true;
            }
            catch(IOException e){
                // Logs the error
                ErrorHandle.log("File Create Error", "Unable to create file | Path: " + NewFile.getPath() + " | Error: " + e.getMessage(), 2);

                // Prints the error to the console
                Console.log(e,Theme.Error());
                return false;
            }
        }
    }

    //Deletes a file when given the file
    public boolean Delete(File File){
        //Deletes the file
        try{
            //Checks if the file is a directory or a file
            if(File.isDirectory()){

                // Gets the list of files inside the directory
                File[] Files = File.listFiles();

                // Checks if the directory is empty
                if(Files != null && Files.length == 0){
                    // Deletes the directory
                    boolean Status = File.delete();

                    // Checks if the directory was deleted
                    if(!Status){
                        // Logs the error
                        ErrorHandle.log("File Delete Error", "Unable to delete file | Path: " + File.getPath() + " | Error: File was not deleted", 2);

                        // Returns false if the file was not deleted
                        return false;
                    }

                    // Logs the action
                    ErrorHandle.action("File Delete","File deleted from  " + File.getPath() + " successfully");

                    //Returns true if the file was deleted
                    return true;
                }

                // Deletes all the files in the directories
                boolean Status = ForceDeleteFiles(File);

                // Deletes all empty directories
                boolean Status1 = ForceDeleteDirectories(File);

                return Status && Status1;
            }

            //Deletes the file
            boolean Status = File.delete();

            // Checks if the file was deleted
            if(!Status){
                // Logs the error
                ErrorHandle.log("File Delete Error", "Unable to delete file | Path: " + File.getPath() + " | Error: File was not deleted", 2);

                // Returns false if the file was not deleted
                return false;
            }

            // Logs the action
            ErrorHandle.action("File Delete","File deleted from  " + File.getPath() + " successfully");

            //Returns true if the file was deleted
            return true;
        }
        catch(Exception e){
            // Logs the error
            ErrorHandle.log("File Delete Error", "Unable to delete file | Path: " + File.getPath() + " | Error: " + e.getMessage(), 2);

            // Prints the error to the console
            Console.log(e,Theme.Error());
            return false;
        }
    }

    // Reads the data from the file with the given path
    public ArrayList<String> Read(String Path){
        // Reads the data from the file 
        try{
            BufferedReader Reader = new BufferedReader(new FileReader(Path)); 
            ArrayList<String> Data = new ArrayList<>();

            // Reads the data from the file 
            String Line = Reader.readLine(); 

            while(Line != null){
                Data.add(Line); 
                Line = Reader.readLine(); 
            }

            // Closes the reader
            Reader.close();

            // Logs the action
            ErrorHandle.action("File Read","File read from  " + Path + " successfully");

            // Returns the data from the file
            return Data;
        }
        catch(IOException e){
            // Logs the error
            ErrorHandle.log("File Read Error", "Unable to read from file | Path: " + Path + " | Error: " + e.getMessage(), 2);

            // Prints the error to the console 
            Console.log(e,Theme.Error()); 
            return null; 
        }
    }

    // Reads the data from the file when given the File object 
    public ArrayList<String> Read(File File){
        // Reads the data from the file 
        try{
            BufferedReader Reader = new BufferedReader(new FileReader(File)); 
            ArrayList<String> Data = new ArrayList<>();

            // Reads the data from the file 
            String Line = Reader.readLine(); 

            while(Line != null){
                Data.add(Line); 
                Line = Reader.readLine(); 
            }

            // Closes the reader
            Reader.close();

            // Logs the action
            ErrorHandle.action("File Read","File read from  " + File.getPath() + " successfully");

            // Returns the data from the file
            return Data;
        }
        catch(IOException e){
            // Logs the error
            ErrorHandle.log("File Read Error", "Unable to read from file | Path: " + File.getPath() + " | Error: " + e.getMessage(), 2);

            // Prints the error to the console 
            Console.log(e,Theme.Error()); 
            return null; 
        }
    }

}
