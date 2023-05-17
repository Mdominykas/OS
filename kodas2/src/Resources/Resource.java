package Resources;

import Processes.Process;
import Utils.Kernel;

import java.util.ArrayList;
import java.util.List;

public class Resource {
    int fid;
    List<Object> elements;
    List<Process> waitingProcesses;
    List<Integer> waitingCount;
    public int name;
    private int availableElements; // galimai Resource tera binary semaphore

    public Resource(int name, Kernel kernel, List<Object> elements) {
        this.name = name;
        this.fid = kernel.getNewFid();
        this.elements = elements;
        waitingCount = new ArrayList<>();
        waitingProcesses = new ArrayList<>();
        availableElements = 0;
    }

    public boolean ask() {
        if (availableElements > 0) {
            availableElements--;
            return true;
        } else {
            return false;
        }
    }

    public Process release() {
        if (waitingProcesses.isEmpty()) {
            availableElements++;
            return null;
        }
        waitingCount.remove(0);
        return waitingProcesses.remove(0);
    }
}
