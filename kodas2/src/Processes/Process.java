package Processes;

import Resources.Resource;
import Utils.Kernel;

import java.util.ArrayList;
import java.util.List;

public abstract class Process {
    protected int fId;
    //savedRegisters
    public List<Resource> createdResources;
    protected int state;
    public int priority;
    public List<Process> childrenProcess;
    protected Kernel kernel;

    Process(Kernel kernel)
    {
        this.fId = kernel.getNewFid();
        this.kernel = kernel;
        this.createdResources = new ArrayList<>();
        this.childrenProcess = new ArrayList<>();
        state = 1;
    }

    abstract public void run();
}
