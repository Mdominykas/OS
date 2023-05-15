package Resources;

import Processes.Process;

import java.util.List;

public abstract class Resource {
    int fid;
    List<Integer> elements;
    List<Process> waitingProcesses;
    List<Integer> waitingCount;
}
