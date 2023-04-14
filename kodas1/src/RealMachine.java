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
    RealMachine() {
        R1 = new Register(6);
        R2 = new Register(6);
        R3 = new Register(6);
        IC = new Register(1);
        R1 = new Register(6);
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
    }

    public boolean load(String programName) {
        IC.value = 0x10;
        CS.value = 0x10;
        DS.value = 0x80;
        if(!pagingMechanism.createVirtualMachinePages())
            return false;
        this.vm = new VirtualMachine(R1, R2, R3, IC, CS, DS, this.interruptHandler, pagingMechanism);
        return true;
    }

    public void exec() {
        while (true) {
            vm.execute();
            if (SI.value == 1) {
                break;
            }
            MODE = true;
        }
        this.vm = null;
    }


}
