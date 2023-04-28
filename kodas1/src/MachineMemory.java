import Constants.Constants;

import java.util.Arrays;

public class MachineMemory {
    Character[] memory;

    MachineMemory() {
        this.memory = new Character[Constants.WordLength * Constants.realMachineLengthInWords];
        Arrays.fill(memory, '0');
    }

    Character[] getWord(int num) {
        assert (num < this.memory.length);
        Character[] answer = new Character[Constants.WordLength];
        System.arraycopy(this.memory, Constants.WordLength * num, answer, 0, Constants.WordLength);
        return answer;
    }

    void setWord(int num, Character[] newWord) {
        assert (num < this.memory.length);
        System.arraycopy(newWord, 0, this.memory, Constants.WordLength * num, Constants.WordLength);
    }

    void writeNumber(int wordPoz, int num) {
        int offset = 1;
//        possible error with num = 0
        while (num > 0) {
            this.memory[(wordPoz + 1) * Constants.WordLength - offset] = (char) Conversion.convertDigitToHexCharacter(num % 16);
            num /= 16;
            offset++;
        }
    }

    Character getByte(int byteNum) {
        return memory[byteNum];
    }

    void setByte(int byteNum, Character value) {
        memory[byteNum] = value;
    }

}
