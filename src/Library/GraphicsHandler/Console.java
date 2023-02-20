package Library.GraphicsHandler;

import Config.Color;
import java.util.Hashtable;


public class Console {
    // Private Variables 
    private final static Hashtable<String, String> Colors = new Hashtable<>();
    private static boolean Initialized = false; // Checks if the console has been initialized

    // Sets up the console
    public static void initialize(){
        Initialized = true;
        Colors.put("Black",BLACK);
        Colors.put("Red",RED);
        Colors.put("Green",GREEN);
        Colors.put("Yellow",YELLOW);
        Colors.put("Blue",BLUE);
        Colors.put("Purple",PURPLE);
        Colors.put("Cyan",CYAN);
        Colors.put("White",WHITE);
        Colors.put("Black_Bold",BLACK_BOLD);
        Colors.put("Red_Bold",RED_BOLD);
        Colors.put("Green_Bold",GREEN_BOLD);
        Colors.put("Yellow_Bold",YELLOW_BOLD);
        Colors.put("Blue_Bold",BLUE_BOLD);
        Colors.put("Purple_Bold",PURPLE_BOLD);
        Colors.put("Cyan_Bold",CYAN_BOLD);
        Colors.put("White_Bold",WHITE_BOLD);
        Colors.put("Black_Underline",BLACK_UNDERLINED);
        Colors.put("Red_Underline",RED_UNDERLINED);
        Colors.put("Green_Underline",GREEN_UNDERLINED);
        Colors.put("Yellow_Underline",YELLOW_UNDERLINED);
        Colors.put("Blue_Underline",BLUE_UNDERLINED);
        Colors.put("Purple_Underline",PURPLE_UNDERLINED);
        Colors.put("Cyan_Underline",CYAN_UNDERLINED);
        Colors.put("White_Underline",WHITE_UNDERLINED);
        Colors.put("Black_Background",BLACK_BACKGROUND);
        Colors.put("Red_Background",RED_BACKGROUND);
        Colors.put("Green_Background",GREEN_BACKGROUND);
        Colors.put("Yellow_Background",YELLOW_BACKGROUND);
        Colors.put("Blue_Background",BLUE_BACKGROUND);
        Colors.put("Purple_Background",PURPLE_BACKGROUND);
        Colors.put("Cyan_Background",CYAN_BACKGROUND);
        Colors.put("White_Background",WHITE_BACKGROUND);
        Colors.put("Black_High_Intensity",BLACK_BRIGHT);
        Colors.put("Red_High_Intensity",RED_BRIGHT);
        Colors.put("Green_High_Intensity",GREEN_BRIGHT);
        Colors.put("Yellow_High_Intensity",YELLOW_BRIGHT);
        Colors.put("Blue_High_Intensity",BLUE_BRIGHT);
        Colors.put("Purple_High_Intensity",PURPLE_BRIGHT);
        Colors.put("Cyan_High_Intensity",CYAN_BRIGHT);
        Colors.put("White_High_Intensity",WHITE_BRIGHT);
        Colors.put("Reset",RESET);        
    }

    // Outputs a normal message to the console 
    public static void log(Object Message){
        System.out.println(Message.toString());
    }

    // Outputs a normal message but in a single line fashion
    public static void log(int Message, boolean SingleLine){
        if (SingleLine){
            System.out.print(Message);
        } else {
            System.out.println(Message);
        }
    }

    // Clears the output 
    public static void clear(){
        System.out.print("\033[H\033[2J");  
        System.out.flush();  
    }

    // Outputs a warning message to the console
    public static void warn(Object Message){
        System.out.println(YELLOW + Message.toString() + RESET);
    }

    // Outputs a message to the console with a custom color
    public static void log(Object Message, Color Color){
        // Checks if the console has been initialized 
        if(!Initialized){
            System.out.println("The console has not been initialized. Please call the initialize method before using the console colors");
            return;
        }

        switch (Color.toString()) {
            case "Black" -> System.out.println(BLACK + Message.toString() + RESET);
            case "Red" -> System.out.println(RED + Message.toString() + RESET);
            case "Green" -> System.out.println(GREEN + Message.toString() + RESET);
            case "Yellow" -> System.out.println(YELLOW + Message.toString() + RESET);
            case "Blue" -> System.out.println(BLUE + Message.toString() + RESET);
            case "Purple" -> System.out.println(PURPLE + Message.toString() + RESET);
            case "Cyan" -> System.out.println(CYAN + Message.toString() + RESET);
            case "White" -> System.out.println(WHITE + Message.toString() + RESET);
            case "Black_Bold" -> System.out.println(BLACK_BOLD + Message.toString() + RESET);
            case "Red_Bold" -> System.out.println(RED_BOLD + Message.toString() + RESET);
            case "Green_Bold" -> System.out.println(GREEN_BOLD + Message.toString() + RESET);
            case "Yellow_Bold" -> System.out.println(YELLOW_BOLD + Message.toString() + RESET);
            case "Blue_Bold" -> System.out.println(BLUE_BOLD + Message.toString() + RESET);
            case "Purple_Bold" -> System.out.println(PURPLE_BOLD + Message.toString() + RESET);
            case "Cyan_Bold" -> System.out.println(CYAN_BOLD + Message.toString() + RESET);
            case "White_Bold" -> System.out.println(WHITE_BOLD + Message.toString() + RESET);
            case "Black_Underline" -> System.out.println(BLACK_UNDERLINED + Message.toString() + RESET);
            case "Red_Underline" -> System.out.println(RED_UNDERLINED + Message.toString() + RESET);
            case "Green_Underline" -> System.out.println(GREEN_UNDERLINED + Message.toString() + RESET);
            case "Yellow_Underline" -> System.out.println(YELLOW_UNDERLINED + Message.toString() + RESET);
            case "Blue_Underline" -> System.out.println(BLUE_UNDERLINED + Message.toString() + RESET);
            case "Purple_Underline" -> System.out.println(PURPLE_UNDERLINED + Message.toString() + RESET);
            case "Cyan_Underline" -> System.out.println(CYAN_UNDERLINED + Message.toString() + RESET);
            case "White_Underline" -> System.out.println(WHITE_UNDERLINED + Message.toString() + RESET);
            case "Black_Background" -> System.out.println(BLACK_BACKGROUND + Message.toString() + RESET);
            case "Red_Background" -> System.out.println(RED_BACKGROUND + Message.toString() + RESET);
            case "Green_Background" -> System.out.println(GREEN_BACKGROUND + Message.toString() + RESET);
            case "Yellow_Background" -> System.out.println(YELLOW_BACKGROUND + Message.toString() + RESET);
            case "Blue_Background" -> System.out.println(BLUE_BACKGROUND + Message.toString() + RESET);
            case "Purple_Background" -> System.out.println(PURPLE_BACKGROUND + Message.toString() + RESET);
            case "Cyan_Background" -> System.out.println(CYAN_BACKGROUND + Message.toString() + RESET);
            case "White_Background" -> System.out.println(WHITE_BACKGROUND + Message.toString() + RESET);
            case "Black_High_Intensity" -> System.out.println(BLACK_BRIGHT + Message.toString() + RESET);
            case "Red_High_Intensity" -> System.out.println(RED_BRIGHT + Message.toString() + RESET);
            case "Green_High_Intensity" -> System.out.println(GREEN_BRIGHT + Message.toString() + RESET);
            case "Yellow_High_Intensity" -> System.out.println(YELLOW_BRIGHT + Message.toString() + RESET);
            case "Blue_High_Intensity" -> System.out.println(BLUE_BRIGHT + Message.toString() + RESET);
            case "Purple_High_Intensity" -> System.out.println(PURPLE_BRIGHT + Message.toString() + RESET);
            case "Cyan_High_Intensity" -> System.out.println(CYAN_BRIGHT + Message.toString() + RESET);
            case "White_High_Intensity" -> System.out.println(WHITE_BRIGHT + Message.toString() + RESET);
            case "Black_Bold_High_Intensity" -> System.out.println(BLACK_BOLD_BRIGHT + Message.toString() + RESET);
            case "Red_Bold_High_Intensity" -> System.out.println(RED_BOLD_BRIGHT + Message.toString() + RESET);
            case "Green_Bold_High_Intensity" -> System.out.println(GREEN_BOLD_BRIGHT + Message.toString() + RESET);
            case "Yellow_Bold_High_Intensity" -> System.out.println(YELLOW_BOLD_BRIGHT + Message.toString() + RESET);
            case "Blue_Bold_High_Intensity" -> System.out.println(BLUE_BOLD_BRIGHT + Message.toString() + RESET);
            case "Purple_Bold_High_Intensity" -> System.out.println(PURPLE_BOLD_BRIGHT + Message.toString() + RESET);
            case "Cyan_Bold_High_Intensity" -> System.out.println(CYAN_BOLD_BRIGHT + Message.toString() + RESET);
            case "White_Bold_High_Intensity" -> System.out.println(WHITE_BOLD_BRIGHT + Message.toString() + RESET);
            default -> warn("Error: Invalid input -> " + Color);
        }
    }

    //Outputs a message that is a single line  with default color
    public static void log(Object Message,boolean SingleLine){
        if(SingleLine) {
            System.out.print(Message.toString());
        }
        else{
            System.out.println(Message.toString());
        }
    }

    // Outputs a message to the console with a single line and a custom color 
    public static void log(String Message, boolean SingleLine, Color Color){
        // Checks if the console has been initialized
        if(!Initialized){
            System.out.println("The console has not been initialized. Please call the initialize method before using the console colors");
            return;
        }

        if(SingleLine){
            System.out.print(Colors.get(Color.toString()) + Message + RESET);
        }
        else{
            System.out.println(Colors.get(Color.toString()) + Message + RESET);
        }
    }

    // Animates a message given the delay in ms
    public static void animate(String Message, long Delay){
        String[] MessageArray = Message.split("");
        for (String letter : MessageArray) {
            System.out.print(letter);
            try {
                Thread.sleep(Delay);
            } catch (Exception e) {
                warn("Error: " + e);
                break; 
            }
        }
        System.out.println();
    }

    // Animates the text in a single line and with a custom color 
    public static void animate(String Message, long Delay, boolean SingleLine, Color Color){

        // Checks if the console has been initialized 
        if(!Initialized){
            System.out.println("The console has not been initialized. Please call the initialize method before using the console colors");
            return;
        }

        String[] MessageArray = Message.split("");
        for (String letter : MessageArray) {
            System.out.print(Colors.get(Color.toString()) + letter + RESET);
            try {
                Thread.sleep(Delay);
            } catch (Exception e) {
                warn("Error: " + e);
                break; 
            }
        }

        if(!SingleLine){
            System.out.println();
        }
    }

    // Animates the text with a custom color 
    public static void animate(String Message, long Delay, Color Color){

        // Checks if the console has been initialized 
        if(!Initialized){
            System.out.println("The console has not been initialized. Please call the initialize method before using the console colors");
            return;
        }

        String[] MessageArray = Message.split("");
        for (String letter : MessageArray) {
            System.out.print(Colors.get(Color.toString()) + letter + RESET);
            try {
                Thread.sleep(Delay);
            } catch (Exception e) {
                warn("Error: " + e);
                break; 
            }
        }
        System.out.println();
    }

    // Yields the current thread for a given amount of time 
    public static void sleep(long Delay){
        try {
            Thread.sleep(Delay);
        } catch (Exception e) {
            warn("Error: " + e);
        }
    }

// ----------------------------------------------------------------------------------------------- \\

    // Different console colors; 

    // Reset
    public static final String RESET = "\033[0m";  // Text Reset

    // Regular Colors
    public static final String BLACK = "\033[0;30m";   // BLACK
    public static final String RED = "\033[0;31m";     // RED
    public static final String GREEN = "\033[0;32m";   // GREEN
    public static final String YELLOW = "\033[0;33m";  // YELLOW
    public static final String BLUE = "\033[0;34m";    // BLUE
    public static final String PURPLE = "\033[0;35m";  // PURPLE
    public static final String CYAN = "\033[0;36m";    // CYAN
    public static final String WHITE = "\033[0;37m";   // WHITE

    // Bold
    public static final String BLACK_BOLD = "\033[1;30m";  // BLACK
    public static final String RED_BOLD = "\033[1;31m";    // RED
    public static final String GREEN_BOLD = "\033[1;32m";  // GREEN
    public static final String YELLOW_BOLD = "\033[1;33m"; // YELLOW
    public static final String BLUE_BOLD = "\033[1;34m";   // BLUE
    public static final String PURPLE_BOLD = "\033[1;35m"; // PURPLE
    public static final String CYAN_BOLD = "\033[1;36m";   // CYAN
    public static final String WHITE_BOLD = "\033[1;37m";  // WHITE

    // Underline
    public static final String BLACK_UNDERLINED = "\033[4;30m";  // BLACK
    public static final String RED_UNDERLINED = "\033[4;31m";    // RED
    public static final String GREEN_UNDERLINED = "\033[4;32m";  // GREEN
    public static final String YELLOW_UNDERLINED = "\033[4;33m"; // YELLOW
    public static final String BLUE_UNDERLINED = "\033[4;34m";   // BLUE
    public static final String PURPLE_UNDERLINED = "\033[4;35m"; // PURPLE
    public static final String CYAN_UNDERLINED = "\033[4;36m";   // CYAN
    public static final String WHITE_UNDERLINED = "\033[4;37m";  // WHITE

    // Background
    public static final String BLACK_BACKGROUND = "\033[40m";  // BLACK
    public static final String RED_BACKGROUND = "\033[41m";    // RED
    public static final String GREEN_BACKGROUND = "\033[42m";  // GREEN
    public static final String YELLOW_BACKGROUND = "\033[43m"; // YELLOW
    public static final String BLUE_BACKGROUND = "\033[44m";   // BLUE
    public static final String PURPLE_BACKGROUND = "\033[45m"; // PURPLE
    public static final String CYAN_BACKGROUND = "\033[46m";   // CYAN
    public static final String WHITE_BACKGROUND = "\033[47m";  // WHITE

    // High Intensity
    public static final String BLACK_BRIGHT = "\033[0;90m";  // BLACK
    public static final String RED_BRIGHT = "\033[0;91m";    // RED
    public static final String GREEN_BRIGHT = "\033[0;92m";  // GREEN
    public static final String YELLOW_BRIGHT = "\033[0;93m"; // YELLOW
    public static final String BLUE_BRIGHT = "\033[0;94m";   // BLUE
    public static final String PURPLE_BRIGHT = "\033[0;95m"; // PURPLE
    public static final String CYAN_BRIGHT = "\033[0;96m";   // CYAN
    public static final String WHITE_BRIGHT = "\033[0;97m";  // WHITE

    // Bold High Intensity
    public static final String BLACK_BOLD_BRIGHT = "\033[1;90m"; // BLACK
    public static final String RED_BOLD_BRIGHT = "\033[1;91m";   // RED
    public static final String GREEN_BOLD_BRIGHT = "\033[1;92m"; // GREEN
    public static final String YELLOW_BOLD_BRIGHT = "\033[1;93m";// YELLOW
    public static final String BLUE_BOLD_BRIGHT = "\033[1;94m";  // BLUE
    public static final String PURPLE_BOLD_BRIGHT = "\033[1;95m";// PURPLE
    public static final String CYAN_BOLD_BRIGHT = "\033[1;96m";  // CYAN
    public static final String WHITE_BOLD_BRIGHT = "\033[1;97m"; // WHITE

     // Bold High Intensity Underline
     public static final String BLACK_BOLD_BRIGHT_UNDERLINED = "\033[1;90;4m"; // BLACK
     public static final String RED_BOLD_BRIGHT_UNDERLINED = "\033[1;91;4m";   // RED
     public static final String GREEN_BOLD_BRIGHT_UNDERLINED = "\033[1;92;4m"; // GREEN
     public static final String YELLOW_BOLD_BRIGHT_UNDERLINED = "\033[1;93;4m";// YELLOW
     public static final String BLUE_BOLD_BRIGHT_UNDERLINED = "\033[1;94;4m";  // BLUE
     public static final String PURPLE_BOLD_BRIGHT_UNDERLINED = "\033[1;95;4m";// PURPLE
     public static final String CYAN_BOLD_BRIGHT_UNDERLINED = "\033[1;96;4m";  // CYAN
     public static final String WHITE_BOLD_BRIGHT_UNDERLINED = "\033[1;97;4m"; // WHITE
}
