import javax.swing.text.AttributeSet;
import java.util.ArrayList;
import java.util.Arrays;

public class FileSystem {
    ExternalMemory externalMemory;
    ArrayList<Integer> freeFileNumbers;

    FileSystem(ExternalMemory externalMemory) {
        this.externalMemory = externalMemory;
        this.freeFileNumbers = new ArrayList<>();
        for (int i = 1; i <= 255; i++)
            this.freeFileNumbers.add(i);
    }

    int findFileStartWordNumber(String filename) {
        for (int i = 0; i < Constants.externalMemoryLengthInWords; i++) {
            Character[] characters = externalMemory.getWord(i);
            if (Arrays.equals(characters, Conversion.stringToCharacterArray("$FILE$"))) {
                Character[] newCharacters = externalMemory.getWord(i + 1);
                if (newCharacters.length == filename.length()) {
                    boolean match = true;
                    for (int j = 0; j < filename.length(); j++) {
                        if (filename.charAt(j) != newCharacters[j]) {
                            match = false;
                            break;
                        }
                    }
                    if(match)
                        return i + 1;
                }
                i++;
            }
        }
        return -1;
    }

    int findProgramStartWordNumber(String programName) {
        for (int i = 0; i < Constants.externalMemoryLengthInWords; i++) {
            Character[] characters = externalMemory.getWord(i);
            if (Arrays.equals(characters, Conversion.stringToCharacterArray("$PROG$"))) {
                Character[] newCharacters = externalMemory.getWord(i + 1);
                if (newCharacters.length == programName.length()) {
                    boolean match = true;
                    for (int j = 0; j < programName.length(); j++) {
                        if (programName.charAt(j) != newCharacters[j]) {
                            match = false;
                            break;
                        }
                    }
                    if(match)
                        return i + 1;
                }
                i++;
            }
        }
        return -1;
    }


}
