package Library.DataHandler;


public final class DataHistoryShard {

    // Private Variables
    private int ID = 0; // Stores the ID of the data
    private String Data = ""; // Stores the data that the user has changed

    //Public Methods

    // SETTERS
    // Sets the data id of the data
    public void SetID(int ID){
        this.ID = ID;
    }

    // Sets the data of the data
    public void SetData(String Data){
        this.Data = Data;
    }

    // GETTERS
    // Gets the data id of the data
    public int GetID(){
        return ID;
    }

    // Gets the data of the data
    public String GetData(){
        return Data;
    }
}
