package Processes;

import Resources.Resource;
import Utils.Kernel;

import java.util.List;

public abstract class Process {
    protected int fId;
    //savedRegisters
    protected List<Resource> createdResources;
    protected int state;
    public int priority;
    protected Process parentProcess;
    protected List<Process> childrenProcess;

    Process(Kernel kernel)
    {
        this.fId = kernel.getNewFid();
        state = 1;
    }

    abstract public void run();
}
