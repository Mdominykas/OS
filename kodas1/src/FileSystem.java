import Constants.Constants;

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

    private int findTwoSubSequentWords(String firstWord, String secondWord)
    {
        for (int i = 0; i < Constants.externalMemoryLengthInWords; i++) {
            Character[] characters = externalMemory.getWord(i);
            if (Arrays.equals(characters, Conversion.stringToCharacterArray(firstWord))) {
                Character[] newCharacters = externalMemory.getWord(i + 1);
                if(Conversion.characterArrayToString(newCharacters).equals(secondWord))
                    return i + 1;
                i++;
            }
        }
        return -1;

    }

    int findFileStartWordNumber(String filename) {
        return findTwoSubSequentWords("$FILE$", filename);
    }

    int findProgramStartWordNumber(String programName) {
        return findTwoSubSequentWords("$PROG$", programName);
    }


}
