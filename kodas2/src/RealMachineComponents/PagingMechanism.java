package RealMachineComponents;

import Constants.Constants;
import Utils.Conversion;

import java.util.Collections;
import java.util.LinkedList;

public class PagingMechanism {
    Register PTR;
    MachineMemory machineMemory;
    LinkedList<Integer> freePages;

    PagingMechanism(Register PTR, MachineMemory machineMemory) {
        this.PTR = PTR;
        this.machineMemory = machineMemory;
        freePages = new LinkedList<>();
        for (int pageNum = Constants.numberOfSupervisorBLocks; pageNum < Constants.realMachineLengthInBlocks; pageNum++)
            freePages.add(pageNum);
        Collections.shuffle(freePages);
    }

    public int getRealBlockNumber(int virtualBlockNumber){
        assert(virtualBlockNumber < Constants.virtualMachineLengthInBlocks);
        StringBuilder sb = new StringBuilder();
        for (char c : machineMemory.getWord(0x10 * PTR.value() + virtualBlockNumber)) {
            sb.append(c);
        }
        return Conversion.ConvertHexStringToInt(sb.toString());
    }

    public Character[] getWord(int num) {
        assert (num < Constants.virtualMachineLengthInWords);
        int blockNum = num / Constants.blockLengthInWords, wordNum = num % Constants.blockLengthInWords;
        int realBlock = getRealBlockNumber(blockNum);
        return machineMemory.getWord(0x10 * realBlock + wordNum);
    }

    public void setWord(int num, Character[] newWord) {
        assert (num < Constants.virtualMachineLengthInWords);
        int blockNum = num / Constants.blockLengthInWords, wordNum = num % Constants.blockLengthInWords;
        int realBlock = getRealBlockNumber(blockNum);
        machineMemory.setWord(0x10 * realBlock + wordNum, newWord);
    }

    boolean createVirtualMachinePages() {
        if (freePages.size() < Constants.virtualMachineLengthInBlocks + 1) {
            return false;
        }
        PTR.setValue(freePages.removeFirst());
        for(int i = 0; i < Constants.virtualMachineLengthInBlocks; i++)
        {
            machineMemory.writeNumber(PTR.value() * Constants.blockLengthInWords + i, freePages.removeFirst());
        }
        return true;
    }

    public void writeNumber(int location, int number){
        setWord(location, Conversion.convertToWordLengthCharacterArray(number));
    }

    public void freeVirtualMachinePages(){
        freePages.add(PTR.value());
        for(int i = 0; i < Constants.virtualMachineLengthInBlocks; i++)
        {
            int val = Conversion.ConvertHexStringToInt(machineMemory.getWord(PTR.value() + Constants.blockLengthInWords + i));
            freePages.add(val);
        }
    }
}
