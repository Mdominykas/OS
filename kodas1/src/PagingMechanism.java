import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;

public class PagingMechanism {
    Register PTR;
    MachineMemory machineMemory;
    LinkedList<Integer> freePages;

    PagingMechanism(Register PTR, MachineMemory machineMemory) {
        this.PTR = PTR;
        this.machineMemory = machineMemory;
        freePages = new LinkedList<Integer>();
        for (int pageNum = Constants.numberOfSupervisorBLocks; pageNum < Constants.realMachineLengthInBlocks; pageNum++)
            freePages.add(pageNum);
        Collections.shuffle(freePages);
    }

    Character[] getWord(int num) {
        assert (num < Constants.virtualMachineLengthInWords);
        int blockNum = num / Constants.blockLengthInWords, wordNum = num % Constants.blockLengthInWords;
        int realBlock = Integer.parseInt(Arrays.toString(machineMemory.getWord(0x10 * PTR.value + blockNum)));
        return machineMemory.getWord(0x10 * realBlock + wordNum);
    }

    void setWord(int num, Character[] newWord) {
        assert (num < Constants.virtualMachineLengthInWords);
        int blockNum = num / Constants.blockLengthInWords, wordNum = num % Constants.blockLengthInWords;
        int realBlock = Integer.parseInt(Arrays.toString(machineMemory.getWord(0x10 * PTR.value + blockNum)));
        machineMemory.setWord(0x10 * realBlock + wordNum, newWord);
    }

    boolean createVirtualMachinePages() {
        if (freePages.size() < Constants.virtualMachineLengthInBlocks + 1) {
            return false;
        }
        PTR.value = freePages.removeFirst();
        for(int i = 0; i < Constants.virtualMachineLengthInBlocks; i++)
        {
            machineMemory.writeNumber(PTR.value * Constants.blockLengthInWords + i, freePages.removeFirst());
        }

        return true;
    }
}
