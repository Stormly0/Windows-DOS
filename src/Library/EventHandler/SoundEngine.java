package Library.EventHandler;

import Core.SystemStatus;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;
import java.util.HashMap;


public class SoundEngine {
    // Private instances
    private final Library.EventHandler.ErrorHandler ErrorHandle = new ErrorHandler(); // Handles all the errors in the system

    private final HashMap<String,File> Sounds = new HashMap<>(); // Stores all the sounds in the system

    // Constructor
    public SoundEngine(){
        // Sets the sound files that are needed
        Sounds.put("StartUp",new File(Format("Windows Unlock")));
        Sounds.put("Shutdown",new File(Format("Windows Exclamation")));
        Sounds.put("Crash",new File(Format("Windows Critical Stop")));
        Sounds.put("Error",new File(Format("Windows Error")));
    }

    //Private Methods

    //Formats the sound path to the correct format
    private String Format(String Name){
        //Checks operating system
        if(SystemStatus.OperatingSystem.equalsIgnoreCase("Windows")){
            return "src\\Library\\Sounds\\" + Name + ".wav";
        }
        else{
            return "src/Library/Sounds/" + Name + ".wav";
        }
    }

    /**
     * <p>Plays the sound when given a specific sound file object</p><br>
     * <i>Recommended to use a new thread to play the sound to prevent yielding main thread execution</i>
     * @param SoundFile The sound file object to play
     */
    private void Play(File SoundFile){
        try{
            // Creates a new audio input stream
            AudioInputStream AudioStream = AudioSystem.getAudioInputStream(SoundFile.getAbsoluteFile());

            // Creates a new clip
            Clip Clip = AudioSystem.getClip();

            // Opens the audio stream
            Clip.open(AudioStream);

            // Plays the sound
            Clip.start();

            // Waits for the sound to start playing
            while (!Clip.isRunning()){
                Thread.sleep(10); // Sleeps the thread for 10 milliseconds, so it doesn't loop for no reason
            }

            // Waits for the sound to finish playing
            while (Clip.isRunning()){
                Thread.sleep(10); // Sleeps the thread for 10 milliseconds, so it doesn't loop for no reason
            }

            // Closes the clip
            Clip.close();

            // Logs the action
            ErrorHandle.action("Sound Played Successfully", "Path: " + SoundFile.getAbsolutePath());

        }
        catch(Exception e){
            // Logs the error
            ErrorHandle.log("Sound Failed to play", "The sound: " + SoundFile.getAbsolutePath() + " failed to play", 2);
        }
    }


    // Public Methods

    /**
     * <p>Plays the sound when given a specific sound name</p><br>
     * <i>Recommended to use a new thread to play the sound to prevent yielding main thread execution</i>
     * @param SoundName The sound name to play
     */
    public void Play(String SoundName){
        // Checks if the sound exists
        if(Sounds.containsKey(SoundName)){
            // Plays the sound
            Play(Sounds.get(SoundName));
        }
        else{
            // Logs the error
            ErrorHandle.log("Sound Failed to play", "The sound: " + SoundName + " does not exist", 2);
        }
    }

}
