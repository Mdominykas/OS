import Processes.Process;
import Resources.Resource;

import java.util.ArrayList;
import java.util.List;

public class Kernel {
    RealMachine realMachine;
    List<Process> readyProcesses, blockedProcess;
    Process activeProcess;
    List<Resource> resources;

    public Kernel(RealMachine realMachine) {
        this.realMachine = realMachine;
        readyProcesses = new ArrayList<>();
        blockedProcess = new ArrayList<>();
        activeProcess = null;
        resources = new ArrayList<>();
    }

    public void createProcess(Process newProcess) {
        readyProcesses.add(newProcess);
    }

    public void deleteProcess(Process process) {
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
        assert (readyProcesses.size() > 0);
        int highestPriorityNum = 0;
        for (int i = 0; i < readyProcesses.size(); i++) {
            if (readyProcesses.get(i).priority > readyProcesses.get(highestPriorityNum).priority) {
                highestPriorityNum = i;
            }
        }
        activeProcess = readyProcesses.get(highestPriorityNum);
        readyProcesses.remove(activeProcess);
    }
}
