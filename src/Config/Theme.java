package Config;

import jdk.jfr.Description;

public class Theme{

    /**
     * Returns the Color theme of the system
     * @return Color.Blue
    */
    public static Color System_Color(){
        return Color.Blue; // This is the default color of the system
    }


    /**
     * Returns the theme of the system but bolded
     * @return Color.Bold_Blue
    */
    public static Color System_Bold(){
        return Color.Blue_Bold; // This is the default color of the system but bolded
    }


    /**
     * Returns the color of the menu in the system
     * @return  Color.Cyan_Bold_High_Intensity
     */
    public static Color Menu(){
        return Color.Cyan_Bold_High_Intensity; // This is the default color of the menu in the system
    }

    // Returns a System Header color

    /**
     * Returns a System Header color
     * @return Color.Blue_Underline
     */
    public static Color System_Header(){
        return Color.Blue_Underline;
    }

    /**
     * Returns a system description color
     * @return Color.Cyan_Bold
     */
    public static Color System_Description(){
        return Color.Cyan_Bold;
    }

    /**
     * Returns a separator color for the system
     * @return Color.Purple_Bold_High_Intensity
     */
    public static Color Separator(){
        return Color.Purple_Bold_High_Intensity;
    }

    /**
     * Returns a header color for the system
     * @return Color.Cyan_Underline
     */

    public static Color Header(){
        return Color.Cyan_Underline;
    }

    /**
     * Returns the color of the text in the system
     * @return Color.White
     */

    public static Color Text(){
        return Color.White; // This is the default color of the text in the system
    }

    /**
     * Returns the color of the warning text in the system
     * @return Color.Yellow
     */

    public static Color Warning(){
        return Color.Yellow; // This is the default color of the warning text in the system
    }


    /**
     * Returns the color of the error text in the system
     * @return Color.Red
     */
    public static Color Error(){
        return Color.Red; // This is the default color of the error text in the system
    }

    /**
     * Returns the color of the success text in the system
     * @return Color.Green
     */
    @Description("Green")
    public static Color Success(){
        return Color.Green; // This is the default color of the success text in the system
    }
}
