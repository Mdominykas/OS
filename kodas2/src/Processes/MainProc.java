package Processes;

import Resources.Resource;
import Resources.ResourceNames;
import Utils.Kernel;

public class MainProc extends Process {
    int fid;
    MainProc(Kernel kernel) {
        super(kernel);
    }

    public void run() {
        switch (state) {
            case 1:
                kernel.waitResource(ResourceNames.TaskProgramInSupervisorMemory);
                state = 2;
                break;
            case 2:
                Resource resource = kernel.getResource(ResourceNames.TaskProgramInSupervisorMemory);
                fid = (int) resource.removeElement();
                if (fid == 0) {
                    state = 3;
                } else {
                    state = 4;
                }
                break;
            case 3:
                kernel.createProcess(this, new JobGovernor(kernel), ProcessesPriority.JobGovernor);
                state = 1;
                break;
            case 4:
                kernel.deleteProcess(fid);
                state = 1;
                break;
        }
    }
}
