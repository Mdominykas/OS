package Processes;

import Constants.Constants;
import Resources.ResourceNames;
import Utils.Kernel;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class JobGovernor extends Process {
    VirtualMachineProcess virtualMachineProcess;
    JobGovernor(Kernel kernel) {
        super(kernel);
    }

    public void run() {
        switch(state)
        {
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

        }
    }
}
