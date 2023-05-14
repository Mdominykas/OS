import Constants.Constants;

import java.util.ArrayList;
import java.util.Arrays;

public class FileSystem {
    ExternalMemory externalMemory;
    ArrayList<Integer> freeFileNumbers;
    ArrayList<FileHandler> fileHandlers;

    FileSystem(ExternalMemory externalMemory) {
        this.externalMemory = externalMemory;
        this.freeFileNumbers = new ArrayList<>();
        for (int i = 1; i <= 255; i++)
            this.freeFileNumbers.add(i);
        this.fileHandlers = new ArrayList<>();
    }

    private int findFirstFileContentWord(String fileType, String fileName) {
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

    public int findFileEndWord(int fileContentWord){
        for(int i = fileContentWord; i < Constants.externalMemoryLengthInWords; i++){
            if(Conversion.characterArrayToString(externalMemory.getWord(i)).equals(Constants.fileEndWord)){
                return i;
            }
        }
        assert(false);
        return -1;
    }

    public void createFile(String fileName) {
        assert (fileName.length() == Constants.WordLengthInBytes);
        int startOfFile = -1;
        Character[] firsts = externalMemory.getWord(0);
        if (Conversion.characterArrayToString(firsts).equals(Constants.emptyMemoryWord)) {
            startOfFile = 0;
        } else {
            for (int i = 0; i + 1 < Constants.externalMemoryLengthInWords; i++) {
                Character[] word = externalMemory.getWord(i);
                Character[] nextWord = externalMemory.getWord(i + 1);
                if ((Conversion.characterArrayToString(word).equals(Constants.fileEndWord)) && (Conversion.characterArrayToString(nextWord).equals(Constants.emptyMemoryWord))) {
                    startOfFile = i + 1;
                }
            }
        }
        assert (startOfFile != -1);

        setWordAndShift(startOfFile, Conversion.stringToCharacterArray("$FILE$"));
        setWordAndShift(startOfFile + 1, Conversion.stringToCharacterArray(fileName));
        setWordAndShift(startOfFile + 2, Conversion.stringToCharacterArray("------"));
        setWordAndShift(startOfFile + 3, Conversion.stringToCharacterArray(Constants.fileEndWord));
    }

    public FileHandler openFile(String filename) {
        int firstFileWord = findFileStartWordNumber(filename);
        if (firstFileWord == -1)
            return null;

        if (freeFileNumbers.isEmpty()) {
            return null;
        }
        int num = freeFileNumbers.remove(0);
        FileHandler handler = new FileHandler(num, firstFileWord);
        fileHandlers.add(handler);
        return handler;
    }

    public FileHandler fileHandlerByNumber(int fileNumber){
        for (FileHandler fileHandler : fileHandlers) {
            if (fileHandler.fileNumber == fileNumber) {
                return fileHandler;
            }
        }
        return null;
    }

    public boolean closeFile(int fileNumber) {
        int index = -1;
        for (int i = 0; i < fileHandlers.size(); i++) {
            if (fileHandlers.get(i).fileNumber == fileNumber) {
                index = i;
            }
        }
        if (index != -1) {
            fileHandlers.remove(index);
            freeFileNumbers.add(fileNumber);
            return true;
        } else {
            return false;
        }
    }

    void setWordAndShift(int wordNum, Character[] word) {
        externalMemory.setWordAndShift(wordNum, word);
        for (FileHandler fh : fileHandlers) {
            if (fh.wordPoz > wordNum) {
                fh.wordPoz++;
            }
        }
    }

    void removeWordAndShift(int wordNum){
        externalMemory.removeWordAndShift(wordNum);
        for (FileHandler fh : fileHandlers) {
            if (fh.wordPoz > wordNum) {
                fh.wordPoz--;
            }
        }
    }

    int findFileStartWordNumber(String filename) {
        return findFirstFileContentWord("$FILE$", filename);
    }

    int findProgramStartWordNumber(String programName) {
        return findFirstFileContentWord("$PROG$", programName);
    }


}
