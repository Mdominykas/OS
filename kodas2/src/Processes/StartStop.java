package Processes;

import Constants.Constants;
import Resources.ResourceNames;
import Utils.Kernel;

import java.util.ArrayList;

public class StartStop extends Process {
    public StartStop(Kernel kernel) {
        super(kernel);
        this.priority = ProcessesPriority.StartStop;
    }

    private void createResources() {
        kernel.createResource(this, ResourceNames.ChannelMechanism, new ArrayList<>());
        kernel.releaseResource(ResourceNames.ChannelMechanism);
        kernel.createResource(this, ResourceNames.SupervisorMemory, new ArrayList<>());
        kernel.releaseResource(ResourceNames.SupervisorMemory);
        kernel.createResource(this, ResourceNames.UserMemory, new ArrayList<>());
        kernel.releaseResource(ResourceNames.UserMemory, Constants.realMachineLengthInBlocks - Constants.numberOfSupervisorBLocks);
        kernel.createResource(this, ResourceNames.ExternalMemory, new ArrayList<>());
        kernel.releaseResource(ResourceNames.ExternalMemory);

        kernel.createResource(this, ResourceNames.MosEnd, new ArrayList<>());
        kernel.createResource(this, ResourceNames.FromUserInterface, new ArrayList<>());
        kernel.createResource(this, ResourceNames.TaskInSupervisorMemory, new ArrayList<>());
        kernel.createResource(this, ResourceNames.TaskProgramInSupervisorMemory, new ArrayList<>());
        kernel.createResource(this, ResourceNames.LoaderPacket, new ArrayList<>());
        kernel.createResource(this, ResourceNames.FromLoader, new ArrayList<>());
        kernel.createResource(this, ResourceNames.LineInMemory, new ArrayList<>());
        kernel.createResource(this, ResourceNames.FromInterrupt, new ArrayList<>());
        kernel.createResource(this, ResourceNames.UserInput, new ArrayList<>());
        kernel.createResource(this, ResourceNames.UserInputReceived, new ArrayList<>());
        kernel.createResource(this, ResourceNames.Interrupt, new ArrayList<>());
        kernel.createResource(this, ResourceNames.WorkWithFiles, new ArrayList<>());
        kernel.createResource(this, ResourceNames.WorkWithFilesEnd, new ArrayList<>());
        kernel.createResource(this, ResourceNames.NonExistent, new ArrayList<>());
    }

    public void createProcesses() {
        kernel.createProcess(new ReadFromInterface(kernel), ProcessesPriority.ReadFromInterface);
        kernel.createProcess(new JCL(kernel), ProcessesPriority.JCL);
        kernel.createProcess(new MainProc(kernel), ProcessesPriority.MainProc);
        kernel.createProcess(new Interrupt(kernel), ProcessesPriority.Interrupt);
        kernel.createProcess(new PrintLine(kernel), ProcessesPriority.PrintLine);
        kernel.createProcess(new FileSystemProcess(kernel), ProcessesPriority.FileSystem);
        kernel.createProcess( new Idle(kernel), ProcessesPriority.Idle);
    }

    @Override
    public void run() {
        switch (state) {
            case 1:
                createResources();
                state++;
                break;
            case 2:
                createProcesses();
                state++;
                break;
            case 3:
                kernel.waitResource(ResourceNames.MosEnd);
                state++;
                break;
            case 4:
                kernel.deleteProcess(this);
                state++;
                break;
            default:
                assert (false);
                break;
        }

    }
}
