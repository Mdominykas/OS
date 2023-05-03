import Constants.Constants;

import java.util.Arrays;

public class MachineMemory {
    Character[] memory;

    MachineMemory() {
        this.memory = new Character[Constants.WordLengthInBytes * Constants.realMachineLengthInWords];
        Arrays.fill(memory, '0');
    }

    Character[] getWord(int num) {
        assert (num < this.memory.length);
        Character[] answer = new Character[Constants.WordLengthInBytes];
        System.arraycopy(this.memory, Constants.WordLengthInBytes * num, answer, 0, Constants.WordLengthInBytes);
        return answer;
    }

    void setWord(int num, Character[] newWord) {
        assert (num < this.memory.length);
        System.arraycopy(newWord, 0, this.memory, Constants.WordLengthInBytes * num, Constants.WordLengthInBytes);
    }

    void writeNumber(int wordPoz, int num) {
        setWord(wordPoz, Conversion.convertToWordLengthCharacterArray(num));
    }

    Character getByte(int byteNum) {
        return memory[byteNum];
    }

    void setByte(int byteNum, Character value) {
        memory[byteNum] = value;
    }

}
