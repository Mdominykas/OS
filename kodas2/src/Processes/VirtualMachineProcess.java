package Processes;

import RealMachineComponents.VirtualMachine;
import Resources.Resource;
import Resources.ResourceNames;
import Utils.Kernel;

public class VirtualMachineProcess extends Process {
    int jobGovernorId;
    VirtualMachine vm;

    VirtualMachineProcess(Kernel kernel, int jobGovernorId, VirtualMachine vm) {
        super(kernel);
        this.jobGovernorId = jobGovernorId;
        this.vm = vm;
    }

    public void run() {
        switch (state) {
            case 1:
                kernel.realMachine.setUserMode();
                kernel.realMachine.interruptHandler.clearSIAndPI();
                state = 2;
                break;
            case 2:
                while(true)
                {
                    vm.execute();
                    kernel.realMachine.decreaseTimer();
                    if (kernel.realMachine.interruptHandler.test()) {
                        if (kernel.realMachine.interruptHandler.testSIAndPI()) {
                            state = 3;
                            break;
                        } else {
                            return;
                        }
                    }
                }
                break;
            case 3:
                Resource interrupt = kernel.getResource(ResourceNames.Interrupt);
                interrupt.addElement(jobGovernorId);
                kernel.releaseResource(ResourceNames.Interrupt);
                state = 4;
                break;
        }
    }

    @Override
    public void onContinue() {
        super.onContinue();
        state = 1;
    }

    public int getSIValue(){
        return savedRegisters.SI;
    }

    public int getJobGovernorId()
    {
        return jobGovernorId;
    }

}
