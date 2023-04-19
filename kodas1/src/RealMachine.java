import java.io.File;

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

    public boolean load(String programName) {
        IC.setValue(0x10);
        CS.setValue(0x10);
        DS.setValue(0x80);
        if(!pagingMechanism.createVirtualMachinePages())
            return false;

        int startInExternal = fileSystem.findProgramStartWordNumber(programName);
        int fileStartBlock = startInExternal / Constants.blockLengthInWords;
        int fileStartByte = (startInExternal % Constants.blockLengthInWords) * Constants.WordLength;
        for(int block = 0; block < 16; block++){
            channelMechanism.SB.setValue(fileStartBlock + block);
            channelMechanism.SW.setValue(fileStartByte);
            channelMechanism.ST.setValue(3);

            channelMechanism.DB.setValue(block);
            channelMechanism.DW.setValue(0); // TODO sudeti REGISTRU reiksmes i klases
            channelMechanism.DT.setValue(2);

            channelMechanism.exchange();
            int endFile = -1;
            for(int i = 0; i < Constants.blockLengthInWords; i++)
            {
                if(Conversion.characterArrayToString(machineMemory.getWord(block * Constants.blockLengthInWords + i)).equals("$FINS$")){
                    endFile = i;
                    break;
                }
            }
            if(endFile != -1){
                for(int i = endFile; i < Constants.blockLengthInWords; i++){
// TODO uznulinti likusius blokus
                }

                break;
            }
        }
        this.vm = new VirtualMachine(R1, R2, R3, IC, CS, DS, this.interruptHandler, pagingMechanism);

        return true;
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
