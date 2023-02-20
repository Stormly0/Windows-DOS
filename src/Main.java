import Core.Client;

import java.io.File;


public class Main {
    // Private Instances
    private final static Client SystemClient = new Client(); // Handles the current user session in the system
    public static void main(String[] args) {
        SystemClient.Start(); // Starts up the client
 
//        File file = new File("src\\Users\\Guest\\Data\\Test\\Test.txt");
//        System.out.println(file.getParentFile()); // Prints the parent directory of the file
//        String FileName = file.getParentFile().getPath();
//        System.out.println(FileName.matches("^src\\\\Users\\\\Guest\\\\Data\\\\.+$"));

    }
}
