package Processes;

import RealMachineComponents.Register;
import Resources.Resource;
import Resources.SavedRegisters;
import Utils.Kernel;
import Utils.RegisterContainer;

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
    RegisterContainer registerContainer;
    SavedRegisters savedRegisters;
    Process(Kernel kernel)
    {
        this.fId = kernel.getNewFid();
        this.kernel = kernel;
        this.createdResources = new ArrayList<>();
        this.childrenProcess = new ArrayList<>();
        this.savedRegisters = new SavedRegisters();
        state = 1;
    }

    abstract public void run();
    public void giveResourceReferences(RegisterContainer registerContainer)
    {
        this.registerContainer = registerContainer;
    }
    public void saveRegisters()
    {
        this.savedRegisters.save(registerContainer);
    }
    public void loadRegisters()
    {
        this.savedRegisters.load(registerContainer);
    }

    public int getFId()
    {
        return fId;
    }
}
