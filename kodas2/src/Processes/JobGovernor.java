package Processes;

import Constants.*;
import RealMachineComponents.UserInput;
import Resources.ResourceNames;
import Utils.Kernel;

public class JobGovernor extends Process {
    VirtualMachineProcess virtualMachineProcess;

    JobGovernor(Kernel kernel) {
        super(kernel);
    }

    public void run() {
        switch (state) {
            case 1:
                kernel.waitResource(ResourceNames.UserMemory, Constants.virtualMachineLengthInBlocks + 1);
                state = 2;
                break;
            case 2:
                kernel.realMachine.createPaging();
                state = 3;
                break;
            case 3:
                virtualMachineProcess = new VirtualMachineProcess(kernel, fId, kernel.realMachine.createVirtualMachine());
                kernel.createProcess(this, virtualMachineProcess, ProcessesPriority.VirtualMachine);
                state = 4;
                break;
            case 4:
                kernel.releaseResource(ResourceNames.SupervisorMemory);
                state = 5;
                break;
            case 5:
                kernel.waitResource(ResourceNames.FromInterrupt);
                state = 6;
                break;
            case 6:
                kernel.stopProcess(virtualMachineProcess);
                state = 7;
                break;
            case 7:
                if ((virtualMachineProcess.savedRegisters.PI > 0) || (virtualMachineProcess.savedRegisters.SI == SIValues.Halt)) {
                    state = 19;
                } else {
                    state = 8;
                }
                break;
            case 8:
                if (SIValues.isWorkWithFiles(virtualMachineProcess.savedRegisters.SI)) {
                    state = 9;
                } else {
                    state = 11;
                }
                break;
            case 9:
                kernel.releaseResource(ResourceNames.WorkWithFiles);
                state = 10;
                break;
            case 10:
                kernel.waitResource(ResourceNames.WorkWithFilesEnd);
                state = 18;
                break;
            case 11:
                if(SIValues.isInput(virtualMachineProcess.savedRegisters.SI)){
                    state = 12;
                }
                else{
                    state = 17;
                }
                break;
            case 12:
                kernel.waitResource(ResourceNames.UserInput);
                state = 13;
                break;
            case 13:
                kernel.releaseResource(ResourceNames.UserInputReceived);
                state = 14;
                break;
            case 14:
                kernel.waitResource(ResourceNames.ChannelMechanism);
                state = 15;
                break;
        }
    }
}
