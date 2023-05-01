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

    private int findStartOfFile(String fileType, String fileName) {
        for (int i = 0; i + 2 < Constants.externalMemoryLengthInWords; i++) {
            Character[] characters = externalMemory.getWord(i);
            if (Arrays.equals(characters, Conversion.stringToCharacterArray(fileType))) {
                Character[] newCharacters = externalMemory.getWord(i + 1);
                if (Conversion.characterArrayToString(newCharacters).equals(fileName)) {
                    Character[] thirdCharacters = externalMemory.getWord(i + 2);
                    if (Conversion.characterArrayToString(thirdCharacters).equals("------")) {
                        return i + 3;
                    } else {
                        i += 2;
                    }
                } else {
                    i++;
                }
            }
        }
        return -1;

    }

    int findFileStartWordNumber(String filename) {
        return findStartOfFile("$FILE$", filename);
    }

    int findProgramStartWordNumber(String programName) {
        return findStartOfFile("$PROG$", programName);
    }


}
