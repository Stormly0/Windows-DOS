package Library.DataHandler;

import Config.Theme;
import Library.EventHandler.ErrorHandler;
import Library.GraphicsHandler.Console;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;



public class DataHistory {
    // Stores all the changes made to the file 
    private final Hashtable<DataHistoryShard,String> Changes = new Hashtable<>(); // Stores the changes made to the file
    private final ErrorHandler ErrorHandle = new ErrorHandler(); // Handles all errors in the system

    // Private Instances
    private final Date Date = new Date(); // Creates a new date instance

    // Private Functions
    // Returns a formatted date&time String with the specified format [MM/dd/yyyy HH:mm:ss]
    private String FormatTime(Date Time){
        Format Format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss"); // Creates a new date format
        return Format.format(Time); // Returns the formatted date
    }

    // Returns a formatted filze size when given a file size in bytes 
    private String FormatFileSize(long Bytes){
        // Checks if the file size is less than 1KB 
        if(Bytes < 1024){
            return Bytes + "B"; // Returns the file size in bytes
        }

        // Checks if the file size is less than 1MB
        if(Bytes < 1048576){
            return (Bytes/1024) + "KB"; // Returns the file size in KB
        }

        // Checks if the file size is less than 1GB
        if(Bytes < 1073741824){
            return (Bytes/1048576) + "MB"; // Returns the file size in MB
        }

        // Checks if the file size is less than 1TB
        if(Bytes < 1099511627776L){
            return (Bytes/1073741824) + "GB"; // Returns the file size in GB
        } 

        // Otherwise just return the file size in bytes 
        return Bytes + "B";
    }

    //Checks if a history shard of the same data exists in the changes HashTable Array
    //Returns the number of duplicates are present in the changes HashTable Array
    private int CheckDuplicate(Hashtable<DataHistoryShard,String> Changes, String Data){
        int Count = 0; // Stores the number of duplicates are present in the changes HashTable Array
        for(DataHistoryShard HistoryShard : Changes.keySet()){ // Loops through the changes HashTable Array
            if(HistoryShard.GetData().equalsIgnoreCase(Data)){ // Checks if the data of the data shard object is the same as the data of the data shard object
                Count++; // Increments the count by 1
            }
        }
        return Count; // Returns the number of duplicates are present in the changes HashTable Array
    }

    //Loops through the changes hashTable array and finds a matching data change (First one it finds)
    private DataHistoryShard FindDataChange(String Data){
        for(DataHistoryShard HistoryShard : Changes.keySet()){ // Loops through the changes HashTable Array
            if(HistoryShard.GetData().equalsIgnoreCase(Data)){ // Checks if the data of the data shard object is the same as the data of the data shard object
                return HistoryShard; // Returns the data shard object
            }
        }
        return null; // Returns null if no data shard object is found
    }

    //Protected Methods 
    
    // --- FILE DATA CHANGES --- \\ 

    // Adds a change to the HashTable Array
    protected void AddChange(String Change){
        //Gets the number of duplicates of the same data in the changes HashTable Array
        int NumberOfDuplicates = CheckDuplicate(Changes,Change);
        DataHistoryShard MatchingHistoryShard = FindDataChange(Change); // Gets the data shard object of the data change

        //Creates a new DataHistoryShard object
        DataHistoryShard HistoryShard = new DataHistoryShard();

        // Sets the data of the DataHistoryShard object
        HistoryShard.SetData(Change);


        // Checks if the changes HashTable Array contains the change of Deleted Change
        // If you add a change of Deleted Change it will remove the change from the HashTable Array 
        if(MatchingHistoryShard != null && Changes.get(MatchingHistoryShard).equalsIgnoreCase("Delete")){
            Changes.remove(MatchingHistoryShard); // Removes the change from the HashTable Array
            return; // Returns the method and stops the rest of method from running
        }

        //Checks if the user added the same data multiple times and sets the data id of the data shard object
        if(NumberOfDuplicates != 0){
            HistoryShard.SetID(NumberOfDuplicates); // Sets the data id of the data shard object
        }

        Changes.put(HistoryShard,"Add"); // Adds the change to the HashTable Array
    }

    // Removes a change from the HashTable Array
    protected void RemoveChange(String Change){
        //Gets the Matching history data shard
        DataHistoryShard MatchingHistoryShard = FindDataChange(Change);

        //Creates a new DataHistoryShard object
        DataHistoryShard HistoryShard = new DataHistoryShard();
        HistoryShard.SetData(Change); // Sets the data of the DataHistoryShard object

        // Checks if the changes HashTable Array contains the change of Added Change 
        // If you remove a change of Added Change it will remove the change from the HashTable Array
        if(MatchingHistoryShard != null && Changes.get(MatchingHistoryShard).equalsIgnoreCase("Add")){
            Changes.remove(MatchingHistoryShard); // Removes the change from the HashTable Array
            return; // Returns the method and stops the rest of method from running
        }

        //Checks if the MatchingHistoryShard is null

        // User has chosen to delete data that has not been added to the file yet but there is already data in the file
        // This will add a change of Deleted Change to the HashTable Array
        if(MatchingHistoryShard == null){
            Changes.put(HistoryShard,"Delete"); // Adds the change to the HashTable Array
            return; // Returns the method and stops the rest of method from running
        }

    }

    // Discards all changes that were made to the HashTable Array 
    protected void DiscardChanges(){
        Changes.clear(); // Clears the HashTable Array
    }

    // Returns the changes made to the file 
    protected Hashtable<DataHistoryShard,String> GetChanges(){
        return Changes; // Returns the changes made to the file 
    }

    // Returns the number of changes made to the file 
    protected int GetNumberOfChanges(){
        // Checks if the HashTable Array is empty 
        if(Changes.isEmpty()){
            return 0; // Returns 0 if the HashTable Array is empty 
        }
        return Changes.size(); // Returns the number of changes made to the file 
    }

    // --- FILE SYSTEM META DATA --- \\ 

    //Getters 
    // Gets the last modified date of the selected file 
    protected String GetLastModifiedDate(File File){
        try{
            BasicFileAttributes Attributes = Files.readAttributes(File.toPath(),BasicFileAttributes.class); // Gets the attributes of the file
            Date LastModifiedDate = new Date(Attributes.lastModifiedTime().toMillis()); // Gets the last modified date of the file
            return FormatTime(LastModifiedDate); // Returns the formatted date
        }
        catch(IOException e){
            // Logs the error
            Console.log("Failed to get last modified date of file " + File.getName(),Theme.Error());
            ErrorHandle.log("Failed to get last modified date of file " + File.getName(),e.getMessage(),3);
            return null; 
        }
    }

    // Gets the Last Accessed date of the selected file 
    protected String GetLastAccessedDate(File File){
        try{
            BasicFileAttributes Attributes = Files.readAttributes(File.toPath(),BasicFileAttributes.class); // Gets the attributes of the file
            Date LastAccessedDate = new Date(Attributes.lastAccessTime().toMillis()); // Gets the last accessed date of the file
            return FormatTime(LastAccessedDate); // Returns the formatted date
        }
        catch(IOException e){
            // Logs the error
            Console.log("Failed to get last accessed date of file " + File.getName(),Theme.Error());
            ErrorHandle.log("Failed to get last accessed date of file " + File.getName(),e.getMessage(),3);
            return null; 
        }
    }

    // Gets the creation date of the selected file 
    protected String GetCreationDate(File File){
        try{
            BasicFileAttributes Attributes = Files.readAttributes(File.toPath(),BasicFileAttributes.class); // Gets the attributes of the file
            Date CreationDate = new Date(Attributes.creationTime().toMillis()); // Gets the creation date of the file
            return FormatTime(CreationDate); // Returns the formatted date
        }
        catch(IOException e){
            // Logs the error
            Console.log("Failed to get creation date of file " + File.getName(),Theme.Error());
            ErrorHandle.log("Failed to get creation date of file " + File.getName(),e.getMessage(),3);
            return null; 
        }
    }

    // Gets the size of the selected file 
    protected String GetFileSize(File File){
        try{
            BasicFileAttributes Attributes = Files.readAttributes(File.toPath(),BasicFileAttributes.class); // Gets the attributes of the file
            long Size = Attributes.size(); // Gets the size of the file
            return FormatFileSize(Size); // Returns the formatted size
        }
        catch(IOException e){
            // Logs the error
            Console.log("Failed to get size of file " + File.getName(),Theme.Error());
            ErrorHandle.log("Failed to get size of file " + File.getName(),e.getMessage(),3);
            return null; 
        }
    }
    
    // Setters
    // Sets the last modified date of the selected file
    protected void SetLastModifiedDate(File File){
        try{
            FileTime CurrentTime = FileTime.fromMillis(this.Date.getTime()); // Gets the current time of the file 
            Files.setAttribute(File.toPath(),"lastModifiedTime",CurrentTime); // Sets the last modified date of the file
        }
        catch(IOException e){
            // Logs the error
            Console.log("Failed to set last modified date of file " + File.getName(),Theme.Error());
            ErrorHandle.log("Failed to set last modified date of file " + File.getName(),e.getMessage(),3);
        }
    }

    // Sets the last accessed date of the selected file
    protected void SetLastAccessedDate(File File){
        try{
            FileTime CurrentTime = FileTime.fromMillis(this.Date.getTime()); // Gets the current time of the file 
            Files.setAttribute(File.toPath(),"lastAccessTime",CurrentTime); // Sets the last accessed date of the file
        }
        catch(IOException e){
            // Logs the error
            Console.log("Failed to set last accessed date of file " + File.getName(),Theme.Error());
            ErrorHandle.log("Failed to set last accessed date of file " + File.getName(),e.getMessage(),3);
        }
    }

    // Sets the creation date of the selected file
    protected void SetCreationDate(File File){
        try{
            FileTime CurrentTime = FileTime.fromMillis(this.Date.getTime()); // Gets the current time of the file 
            Files.setAttribute(File.toPath(),"creationTime",CurrentTime); // Sets the creation date of the file
        }
        catch(IOException e){
            // Logs the error
            Console.log("Failed to set creation date of file " + File.getName(),Theme.Error());
            ErrorHandle.log("Failed to set creation date of file " + File.getName(),e.getMessage(),3);
        }
    }
    

}
