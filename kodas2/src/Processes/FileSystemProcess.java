package Processes;

import Constants.SIValues;
import Resources.ResourceNames;
import Utils.Kernel;

public class FileSystemProcess extends Process {
    VirtualMachineProcess virtualMachineProcess;

    FileSystemProcess(Kernel kernel) {
        super(kernel);
    }

    public void run() {
        switch (state) {
            case 1:
                kernel.waitResource(ResourceNames.WorkWithFiles);
                state = 2;
                break;
            case 2:
                Object obj = kernel.getResource(ResourceNames.WorkWithFiles).removeElement();
                assert (obj instanceof VirtualMachineProcess);
                virtualMachineProcess = (VirtualMachineProcess) obj;
                kernel.waitResource(ResourceNames.ExternalMemory);
                state = 3;
                break;
            case 3:
                kernel.waitResource(ResourceNames.ChannelMechanism);
                state = 4;
                break;
            case 4:
                if (virtualMachineProcess.getSIValue() == SIValues.OpenFile) {
                    state = 5;
                } else {
                    state = 6;
                }
                break;
            case 5:
                saveRegisters();
                virtualMachineProcess.loadRegisters();
                kernel.realMachine.openFile();
                virtualMachineProcess.saveRegisters();
                loadRegisters();
                state = 13;
                break;
            case 6:
                if (virtualMachineProcess.getSIValue() == SIValues.CloseFile) {
                    state = 7;
                } else {
                    state = 8;
                }
                break;
            case 7:
                saveRegisters();
                virtualMachineProcess.loadRegisters();
                kernel.realMachine.closeFile();
                virtualMachineProcess.saveRegisters();
                loadRegisters();
                state = 13;
                break;
            case 8:
                if (virtualMachineProcess.getSIValue() == SIValues.DeleteFile) {
                    state = 9;
                } else {
                    state = 10;
                }
                break;
            case 9:
                saveRegisters();
                virtualMachineProcess.loadRegisters();
                kernel.realMachine.deleteFile();
                virtualMachineProcess.saveRegisters();
                loadRegisters();
                state = 13;
                break;
            case 10:
                if (virtualMachineProcess.getSIValue() == SIValues.WriteFile) {
                    state = 11;
                } else {
                    state = 12;
                }
                break;
            case 11:
                saveRegisters();
                virtualMachineProcess.loadRegisters();
                kernel.realMachine.writeFile();
                virtualMachineProcess.saveRegisters();
                loadRegisters();
                state = 13;
                break;
            case 12:
                saveRegisters();
                virtualMachineProcess.loadRegisters();
                kernel.realMachine.readFile();
                virtualMachineProcess.saveRegisters();
                loadRegisters();
                state = 13;
                break;
            case 13:
                kernel.releaseResource(ResourceNames.ChannelMechanism);
                state = 14;
                break;
            case 14:
                kernel.releaseResource(ResourceNames.ExternalMemory);
                state = 15;
                break;
            case 15:
                kernel.releaseResourceFor(ResourceNames.WorkWithFilesEnd, virtualMachineProcess.getJobGovernorId());
                state = 1;
                break;

        }
    }
}
