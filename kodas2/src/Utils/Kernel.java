package Utils;

import Processes.Process;
import Processes.StartStop;
import RealMachineComponents.RealMachine;
import RealMachineComponents.UserInput;
import Resources.Resource;
import Resources.ResourceNames;

import java.util.ArrayList;
import java.util.List;

public class Kernel {
    public RealMachine realMachine;
    List<Process> readyProcesses, blockedProcess;
    Process activeProcess;
    List<Resource> resources;

    int newFid;

    public Kernel(RealMachine realMachine) {
        this.realMachine = realMachine;
        readyProcesses = new ArrayList<>();
        blockedProcess = new ArrayList<>();
        activeProcess = new StartStop(this);
        activeProcess.giveResourceReferences(realMachine.containerOfRegisters());
        resources = new ArrayList<>();
        newFid = 0;
    }

    public void run() {
        final int timeReset = 10;
        int timeLimit = timeReset;
        while (true) {
            if (activeProcess == null)
                break;
            activeProcess.run();

            if (realMachine.userInput.simulateReading()) {
                releaseResource(ResourceNames.FromUserInterface);
            }
            if(timeLimit == 0)
            {
                runScheduler();
                timeLimit = timeReset;
            }
            else
                timeLimit--;
        }
    }

    public void createProcess(Process parent, Process newProcess, int priority) {
        Logging.logCreatedProcess(newProcess);
        parent.childrenProcess.add(newProcess);
        newProcess.giveResourceReferences(realMachine.containerOfRegisters());
        newProcess.priority = priority;
        readyProcesses.add(newProcess);
    }

    public void deleteProcess(Process process) {
        for (Process child : process.childrenProcess) {
            deleteProcess(child);
        }
        for (Resource res : process.createdResources) {
            deleteResource(res);
        }
        Logging.logDeletedProcess(process);
        if (activeProcess == process) {
            activeProcess = null;
            runScheduler();
        } else {
            readyProcesses.remove(process);
            blockedProcess.remove(process);
        }
    }

    public void deleteProcess(int fid)
    {
        Process selected = null;
        for (Process process : readyProcesses)
        {
            if(process.getFId() == fid)
                selected = process;
        }
        for (Process process : blockedProcess)
        {
            if(process.getFId() == fid)
                selected = process;
        }
        if(activeProcess.getFId() == fid)
            selected = activeProcess;
        assert(selected != null);
        deleteProcess(selected);
    }

    private void runScheduler() {
        if (activeProcess != null) {
            activeProcess.saveRegisters();
            readyProcesses.add(activeProcess);
            activeProcess = null;
        }
        if (readyProcesses.size() == 0) {
            System.exit(0);
        }
        int highestPriorityNum = 0;
        for (int i = 0; i < readyProcesses.size(); i++) {
            if (readyProcesses.get(i).priority > readyProcesses.get(highestPriorityNum).priority) {
                highestPriorityNum = i;
            }
        }
        activeProcess = readyProcesses.get(highestPriorityNum);
        activeProcess.loadRegisters();
        readyProcesses.remove(activeProcess);
    }

    public void createResource(Process creator, int name, List<Object> elements) {
        Resource resource = new Resource(name, this, elements);
        resources.add(resource);
        creator.createdResources.add(resource);
    }

    public void deleteResource(Resource resource) {
//        galimai cia reikia is proceso pasalinti
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
        waitResource(resourceName, 1);
    }

    public void waitResource(int resourceName, int count)
    {
        Resource selectedResource = selectResource(resourceName);
        assert (selectedResource != null);
        Logging.logProcessWaitsResource(activeProcess, selectedResource);
        boolean hasEnough = selectedResource.waitResource(activeProcess, count);
        if (!hasEnough) {
            activeProcess.saveRegisters();
            blockedProcess.add(activeProcess);
            activeProcess = null;
            runScheduler();
        }
    }

    public void releaseResource(int resourceName) {
        Resource selectedResource = selectResource(resourceName);
        assert (selectedResource != null);
        Logging.logProcessReleaseResource(activeProcess, selectedResource);
        Process released = selectedResource.release();
        if (released != null) {
            assert (blockedProcess.contains(released));
            blockedProcess.remove(released);
            readyProcesses.add(released);
        }
    }



    public boolean someoneWaitsResource(int resourceName) {
        Resource resource = selectResource(resourceName);
        return resource.someOneWaits();
    }

    public Resource getResource(int resourceName) {
        return selectResource(resourceName);
    }


    public int getNewFid() {
        return newFid++;
    }
}
