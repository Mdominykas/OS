package Processes;

import Constants.Constants;
import Resources.Resource;
import Resources.ResourceNames;
import Utils.Kernel;

public class JCL extends Process {
    int curBlock = 0;
    final int firstBlock = 0;

    JCL(Kernel kernel) {
        super(kernel);
    }

    public void run() {
        switch (state) {
            case 1:
                kernel.waitResource(ResourceNames.TaskInSupervisorMemory);
                state = 2;
                break;
            case 2:
                if (kernel.realMachine.isProgramHeaderCorrect()) {
                    state = 5;
                    curBlock = firstBlock;
                } else {
                    state = 3;
                }
                break;
            case 3:
                Resource lineInMemory = kernel.getResource(ResourceNames.LineInMemory);
                String lineWrongHeader = "incorrect header of program";
                lineInMemory.addElement(lineWrongHeader);
                kernel.releaseResource(ResourceNames.LineInMemory);
                state = 4;
                break;
            case 4:
                kernel.releaseResource(ResourceNames.SupervisorMemory);
                state = 1;
                break;
            case 5:
                if (kernel.realMachine.checkForFins(curBlock)) {
                    state = 6;
                } else {
                    state = 7;
                }
                break;
            case 6:
                Resource resource = kernel.getResource(ResourceNames.TaskProgramInSupervisorMemory);
                resource.addElement(0);
                kernel.releaseResource(ResourceNames.TaskProgramInSupervisorMemory);
                state = 1;
                break;
            case 7:
                if (curBlock + 1 == Constants.numberOfSupervisorBLocks) {
                    state = 8;
                } else {
                    state = 10;
                }
                break;
            case 8:
                Resource lim = kernel.getResource(ResourceNames.LineInMemory);
                String lineNoFins = "Program doesn't have $FINS block";
                lim.addElement(lineNoFins);
                kernel.releaseResource(ResourceNames.LineInMemory);
                state = 9;
                break;
            case 9:
                kernel.releaseResource(ResourceNames.SupervisorMemory);
                state = 1;
                break;
            case 10:
                curBlock++;
                state = 5;
                break;
        }
    }
}
