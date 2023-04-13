public class MachineMemory {
    Character[] memory;

    MachineMemory() {
        this.memory = new Character[Constants.WordLength * Constants.MachineMemoryLengthInWords];
    }

    Character[] getWord(int num) {
        assert (num < this.memory.length);
        Character[] answer = new Character[Constants.WordLength];
        System.arraycopy(this.memory, Constants.WordLength * num, answer, 0, Constants.WordLength);
        return answer;
    }

    void setWord(int num, Character[] newWord) {
        assert(num < this.memory.length);
        System.arraycopy(newWord, 0, this.memory, Constants.WordLength * num, Constants.WordLength);
    }

}
