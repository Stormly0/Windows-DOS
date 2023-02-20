package Library.GraphicsHandler;

// Purpose: To handle and verify all user input from the user. May be implemented on your own terms as long as it is able to verify user input.

import java.util.ArrayList;


public interface VerifyInput {
    boolean Verify(String RawInput, String Expected, boolean CaseSensitive); // Verifies the input
    boolean Verify(String RawInput, String Expected,String Expected1); // Verifies the input with two possible answers
    boolean Verify(String RawInput, String Expected); // Verifies the input with one possible answer 
    String Verify(String RawInput, ArrayList<String> Expected); // Verifies the input with multiple possible answers
    String Verify(String RawInput, String[] Expected); // Verifies the input with multiple possible answers
}
