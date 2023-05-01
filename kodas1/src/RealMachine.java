import Constants.*;
import OSException.*;

public class RealMachine {
    Register R1, R2, R3, IC, FLAGS, PTR, CS, DS;
    Register PI, SI, TI;
    boolean MODE;
    MachineMemory machineMemory;
    VirtualMachine vm = null;
    InterruptHandler interruptHandler;
    PagingMechanism pagingMechanism;
    ExternalMemory externalMemory;
    ChannelMechanism channelMechanism;
    FileSystem fileSystem;

    RealMachine() {
        R1 = new Register(6);
        R2 = new Register(6);
        R3 = new Register(6);
        IC = new Register(2);
        R1 = new Register(6);
        CS = new Register(2);
        DS = new Register(2);
        FLAGS = new Register(1);
        PTR = new Register(2);
        PI = new Register(1);
        SI = new Register(1);
        TI = new Register(1);
        machineMemory = new MachineMemory();
        interruptHandler = new InterruptHandler(PI, SI, TI);
        pagingMechanism = new PagingMechanism(PTR, machineMemory);
        externalMemory = new ExternalMemory();
        channelMechanism = new ChannelMechanism(machineMemory, externalMemory);
        fileSystem = new FileSystem(externalMemory);
    }

    private void loadVirtualMachineFromSuperVisorMemory(int numberOfBlocks, int wordsInLastBlock) {
        int virtualBlock = 0, virtualWord = 0;
        int lengthInSupervisor = numberOfBlocks * Constants.blockLengthInWords + wordsInLastBlock;
        for (int i = 0; i < lengthInSupervisor; i++) {
            String curWord = Conversion.characterArrayToString(machineMemory.getWord(i));
            if (curWord.startsWith("$$$") && curWord.endsWith("$$")) {
                virtualBlock = Conversion.convertHexCharacterToDigit(curWord.charAt(3));
                virtualWord = 0;
            } else if (curWord.equals(".CODES")) {
                virtualBlock = 1;
                virtualWord = 0;
            } else if (curWord.equals(".DATAS")) {
                virtualBlock = 8;
                virtualWord = 0;
            }
            else{
                pagingMechanism.setWord(virtualBlock * Constants.blockLengthInWords + virtualWord, machineMemory.getWord(i));
                virtualWord++;
                if(virtualWord == 16){
                    virtualWord = 0;
                    virtualBlock++;
                }
            }
        }
    }

    public void load(String programName) throws OSException {
        IC.setValue(0x10);
        CS.setValue(0x10);
        DS.setValue(0x80);
        if (!pagingMechanism.createVirtualMachinePages()) {
            throw new NotEnoughFreePagesException("");
        }

        int startInExternal = fileSystem.findProgramStartWordNumber(programName);
        if (startInExternal == -1) {
            throw new ProgramNotFoundException("");
        }
        int fileStartBlock = startInExternal / Constants.blockLengthInWords;
        int fileStartByte = (startInExternal % Constants.blockLengthInWords) * Constants.WordLengthInBytes;
        int wroteBlocks = 0, wordsInLastBlock = -1;
        boolean hadFileEnding = false;
        for (int block = 0; block < 16; block++) {
            wroteBlocks++;
            channelMechanism.SB.setValue(fileStartBlock + block);
            channelMechanism.SW.setValue(fileStartByte);
            channelMechanism.ST.setValue(STValues.ExternalMemory);

            channelMechanism.DB.setValue(block);
            channelMechanism.DW.setValue(0);
            channelMechanism.DT.setValue(DTValues.SupervisorMemory);

            channelMechanism.BC.setValue(Constants.blockLengthInWords * Constants.WordLengthInBytes);
            channelMechanism.exchange();
            int endFile = -1;
            for (int i = 0; i < Constants.blockLengthInWords; i++) {
                if (Conversion.characterArrayToString(machineMemory.getWord(block * Constants.blockLengthInWords + i)).equals("$FINS$")) {
                    endFile = i;
                    break;
                }
            }
            if (endFile != -1) {
                hadFileEnding = true;
                wordsInLastBlock = endFile + 1;
                break;
            }
        }
        if (!hadFileEnding) {
            throw new IncorrectProgramSizeException("");
        }
        loadVirtualMachineFromSuperVisorMemory(wroteBlocks, wordsInLastBlock);
        this.vm = new VirtualMachine(R1, R2, R3, IC, CS, DS, interruptHandler, pagingMechanism);
    }

    public void exec() {
        while (true) {
            vm.execute();
            if (SI.value() == 1) {
                break;
            }
            MODE = true;
        }
        this.vm = null;
    }


}
