package Library.EventHandler;


public final class SystemDetails {
    // Gets the current operating system of the java runtime environment 
    public static String GetOperatingSystem(){
        return System.getProperties().getProperty("os.name").split(" ")[0];
    }

    // Gets the current version of the java runtime environment
    public static String GetJavaVersion(){
        return System.getProperties().getProperty("java.version");
    }

    // Gets all the system properties of the java runtime environment
    public static String GetSystemProperties(){
        return System.getProperties().toString();
    }
}
