package Processes;

import Constants.Constants;
import Resources.ResourceNames;
import Utils.Kernel;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class StartStop extends Process {
    public StartStop(Kernel kernel) {
        super(kernel);
    }

    private void createResources() {
        kernel.createResource(this, ResourceNames.ChannelMechanism, new ArrayList<>());
        List<Object> supervisorMemoryPages = new LinkedList<>();
        for (int pageNum = 0; pageNum < Constants.numberOfSupervisorBLocks; pageNum++)
            supervisorMemoryPages.add(pageNum);
        kernel.createResource(this, ResourceNames.SupervisorMemory, supervisorMemoryPages);
        List<Object> userMemoryPages = new LinkedList<>();
        for (int pageNum = Constants.numberOfSupervisorBLocks; pageNum < Constants.realMachineLengthInBlocks; pageNum++)
            userMemoryPages.add(pageNum);
        kernel.createResource(this, ResourceNames.UserMemory, userMemoryPages);
        kernel.createResource(this, ResourceNames.ExternalMemory, new ArrayList<>());
        kernel.createResource(this, ResourceNames.MosEnd, new ArrayList<>());
        kernel.createResource(this, ResourceNames.FromUserInterface, new ArrayList<>());
        kernel.createResource(this, ResourceNames.TaskInSupervisorMemory, new ArrayList<>());
        kernel.createResource(this, ResourceNames.TaskProgramInSupervisorMemory, new ArrayList<>());
        kernel.createResource(this, ResourceNames.LoaderPacket, new ArrayList<>());
        kernel.createResource(this, ResourceNames.FromLoader, new ArrayList<>());
        kernel.createResource(this, ResourceNames.LineInMemory, new ArrayList<>());
        kernel.createResource(this, ResourceNames.FromInterrupt, new ArrayList<>());
        kernel.createResource(this, ResourceNames.UserInput, new ArrayList<>());
        kernel.createResource(this, ResourceNames.Interrupt, new ArrayList<>());
        kernel.createResource(this, ResourceNames.WorkWithFiles, new ArrayList<>());
        kernel.createResource(this, ResourceNames.WorkWithFilesEnd, new ArrayList<>());
        kernel.createResource(this, ResourceNames.NonExistent, new ArrayList<>());
    }

    public void createProcesses() {
        kernel.createProcess(this, new ReadFromInterface(kernel));
        kernel.createProcess(this, new JCL(kernel));
        kernel.createProcess(this, new MainProc(kernel));
        kernel.createProcess(this, new Loader(kernel));
        kernel.createProcess(this, new Interrupt(kernel));
        kernel.createProcess(this, new PrintLine(kernel));
        kernel.createProcess(this, new FileSystem(kernel));
        kernel.createProcess(this, new Idle(kernel));
    }

    @Override
    public void run() {
        switch (state) {
            case 1:
                System.out.println("kursiu resursus");
                createResources();
                state++;
                break;
            case 2:
                System.out.println("kursiu procesus");
                createProcesses();
                state++;
                break;
            case 3:
                System.out.println("lauksiu resurso");
                kernel.waitResource(ResourceNames.MosEnd);
                state++;
                break;
            case 4:
                System.out.println("baigiu");
                kernel.deleteProcess(this);
                state++;
                break;
            default:
                assert (false);
                break;
        }

    }
}
