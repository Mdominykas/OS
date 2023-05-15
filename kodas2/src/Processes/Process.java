package Processes;

import Resources.Resource;

import java.util.List;

public abstract class Process {
    int FId;
    //savedRegisters
    List<Resource> createdResources;
    int state;
    public int priority;
    Process parentProcess;
    List<Process> childrenProcess;
}
