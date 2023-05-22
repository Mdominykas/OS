package Resources;

import Processes.Process;
import Utils.Kernel;

import java.util.ArrayList;
import java.util.List;

public class Resource {
    protected int fid;
    protected List<Object> elements;
    protected List<Process> waitingProcesses;
    protected List<Integer> waitingCount;
    protected List<Integer> releasedBeforeWait;
    public int name;
    protected int availableElements; // galimai Resource tera binary semaphore

    public Resource(int name, Kernel kernel, List<Object> elements) {
        this.name = name;
        this.fid = kernel.getNewFid();
        this.elements = elements;
        waitingCount = new ArrayList<>();
        waitingProcesses = new ArrayList<>();
        releasedBeforeWait = new ArrayList<>();
        availableElements = 0;
    }

    private boolean ask() {
        return ask(1);
    }

    private boolean ask(int count) {
        if (availableElements >= count) {
            availableElements -= count;
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

    public Process releaseFor(int fid) {
        int id = -1;
        for (int i = 0; i < waitingProcesses.size(); i++) {
            if (waitingProcesses.get(i).getFId() == fid) {
                id = i;
            }
        }
        if (id == -1) {
            releasedBeforeWait.add(fid);
            return null;
        } else {
            waitingCount.remove(id);
            return waitingProcesses.remove(id);
        }
    }

    public boolean waitResource(Process process) {
        return waitResource(process, 1);
    }

    public boolean waitResource(Process process, int count) {
        if (releasedBeforeWait.contains(process.getFId())) {
            int id = -1;
            for (int i = 0; i < releasedBeforeWait.size(); i++) {
                if (releasedBeforeWait.get(i) == process.getFId()) {
                    id = i;
                }
            }
            assert (id != -1);
            releasedBeforeWait.remove(id);
            return true;
        }

        boolean ret = ask(count);
        if (!ret) {
            waitingProcesses.add(process);
            waitingCount.add(count);
        }
        return ret;
    }

    public boolean someOneWaits() {
        return waitingProcesses.size() > 0;
    }

    public void addElement(Object object) {
        this.elements.add(object);
    }

    public Object removeElement() {
        return this.elements.remove(0);
    }
}
