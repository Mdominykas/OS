package Utils;

import Processes.Process;
import Processes.StartStop;
import RealMachineComponents.RealMachine;
import Resources.Resource;
import Resources.ResourceNames;

import java.util.ArrayList;
import java.util.List;

public class Kernel {
    public RealMachine realMachine;
    List<Process> readyProcesses, blockedProcesses;
    List<Process> readyStoppedProcesses, blockedStoppedProcesses;
    Process activeProcess;
    List<Resource> resources;

    int newFid;

    public Kernel(RealMachine realMachine) {
        this.realMachine = realMachine;
        readyProcesses = new ArrayList<>();
        blockedProcesses = new ArrayList<>();
        readyStoppedProcesses = new ArrayList<>();
        blockedStoppedProcesses = new ArrayList<>();
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
            if (timeLimit == 0) {
                runScheduler();
                timeLimit = timeReset;
            } else
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
            blockedProcesses.remove(process);
            readyStoppedProcesses.remove(process);
            blockedStoppedProcesses.remove(process);
        }
    }

    public void deleteProcess(int fid) {
        Process selected = null;
        for (Process process : readyProcesses) {
            if (process.getFId() == fid)
                selected = process;
        }
        for (Process process : blockedProcesses) {
            if (process.getFId() == fid)
                selected = process;
        }
        for (Process process : readyStoppedProcesses) {
            if (process.getFId() == fid)
                selected = process;
        }
        for (Process process : blockedStoppedProcesses) {
            if (process.getFId() == fid)
                selected = process;
        }

        if (activeProcess.getFId() == fid)
            selected = activeProcess;
        assert (selected != null);
        deleteProcess(selected);
    }

    public void stopProcess(Process process) {
        process.onStop();
        if (blockedProcesses.contains(process)) {
            blockedProcesses.remove(process);
            blockedStoppedProcesses.add(process);
        } else if (readyProcesses.contains(process)) {
            readyProcesses.remove(process);
            readyStoppedProcesses.add(process);
        } else {
            assert (false);
        }
    }

    public  void continueProcess(Process process) {
        process.onContinue();
        if (blockedStoppedProcesses.contains(process)) {
            blockedStoppedProcesses.remove(process);
            blockedProcesses.add(process);
        } else if (readyStoppedProcesses.contains(process)) {
            readyStoppedProcesses.remove(process);
            readyProcesses.add(process);
        } else {
            assert (false);
        }
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

    public void waitResource(int resourceName, int count) {
        Resource selectedResource = selectResource(resourceName);
        assert (selectedResource != null);
        Logging.logProcessWaitsResource(activeProcess, selectedResource);
        boolean hasEnough = selectedResource.waitResource(activeProcess, count);
        if (!hasEnough) {
            activeProcess.saveRegisters();
            blockedProcesses.add(activeProcess);
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
            if (blockedProcesses.contains(released)) {
                blockedProcesses.remove(released);
                readyProcesses.add(released);
            } else if (blockedStoppedProcesses.contains(released)) {
                blockedStoppedProcesses.remove(released);
                readyStoppedProcesses.add(released);
            } else {
                assert (false);
            }
        }
    }

    public void releaseResource(int resourceName, int count) {
        for (int i = 0; i < count; i++) {
            releaseResource(resourceName);
        }
    }

    public void releaseResourceFor(int resourceName, int targetFid)
    {
        Resource selectedResource = selectResource(resourceName);
        assert (selectedResource != null);
        Logging.logProcessReleaseResource(activeProcess, selectedResource);
        Process released = selectedResource.releaseFor(targetFid);
        if (released != null) {
            if (blockedProcesses.contains(released)) {
                blockedProcesses.remove(released);
                readyProcesses.add(released);
            } else if (blockedStoppedProcesses.contains(released)) {
                blockedStoppedProcesses.remove(released);
                readyStoppedProcesses.add(released);
            } else {
                assert (false);
            }
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
