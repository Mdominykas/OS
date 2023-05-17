package Utils;

import Processes.Process;
import Processes.StartStop;
import RealMachineComponents.RealMachine;
import Resources.Resource;

import java.util.ArrayList;
import java.util.List;

public class Kernel {
    RealMachine realMachine;
    List<Process> readyProcesses, blockedProcess;
    Process activeProcess;
    List<Resource> resources;

    int newFid;

    public Kernel(RealMachine realMachine) {
        this.realMachine = realMachine;
        readyProcesses = new ArrayList<>();
        blockedProcess = new ArrayList<>();
        activeProcess = new StartStop(this);
        resources = new ArrayList<>();
        newFid = 0;
    }

    public void run()
    {
        while(true)
        {
            if(activeProcess == null)
                break;
            activeProcess.run();
        }
    }

    public void createProcess(Process parent, Process newProcess) {
        parent.childrenProcess.add(newProcess);
        readyProcesses.add(newProcess);
    }

    public void deleteProcess(Process process) {
        for (Process child : process.childrenProcess) {
            deleteProcess(child);
        }
        for(Resource res : process.createdResources){
            deleteResource(res);
        }
//        here should be deletion of resources
        if (activeProcess == process) {
            activeProcess = null;
            runScheduler();
        } else {
            readyProcesses.remove(process);
            blockedProcess.remove(process);
        }
    }

    private void runScheduler() {
        if (activeProcess != null) {
            readyProcesses.add(activeProcess);
            activeProcess = null;
        }
        if(readyProcesses.size() == 0)
        {
            System.exit(0);
        }
        int highestPriorityNum = 0;
        for (int i = 0; i < readyProcesses.size(); i++) {
            if (readyProcesses.get(i).priority > readyProcesses.get(highestPriorityNum).priority) {
                highestPriorityNum = i;
            }
        }
        activeProcess = readyProcesses.get(highestPriorityNum);
        readyProcesses.remove(activeProcess);
    }

    public void createResource(Process creator, int name, List<Object> elements) {
        Resource resource = new Resource(name, this, elements);
        resources.add(resource);
        creator.createdResources.add(resource);
    }

    public void deleteResource(Resource resource)
    {
        resources.remove(resource);
    }

    private Resource selectResource(int resourceName) {
        Resource selectedResource = null;
        for (Resource resource : resources) {
            if (resource.name == resourceName) {
                selectedResource = resource;
            }
        }
        return selectedResource;
    }

    public void waitResource(int resourceName) {
        Resource selectedResource = selectResource(resourceName);
        assert (selectedResource != null);
        boolean hasEnough = selectedResource.ask();
        if (!hasEnough) {
            blockedProcess.add(activeProcess);
            activeProcess = null;
            runScheduler();
        }
    }

    public void releaseResource(int resourceName) {
        Resource selectedResource = selectResource(resourceName);
        assert (selectedResource != null);
        Process released = selectedResource.release();
        assert (blockedProcess.contains(released));
        blockedProcess.remove(released);
        readyProcesses.add(released);
    }

    public int getNewFid() {
        return newFid++;
    }
}
