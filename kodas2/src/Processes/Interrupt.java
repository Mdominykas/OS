package Processes;

import Resources.Resource;
import Resources.ResourceNames;
import Utils.Kernel;

public class Interrupt extends Process {
    int jobGovernorId;
    Interrupt(Kernel kernel) {
        super(kernel);
    }

    public void run() {
        switch(state)
        {
            case 1:
                kernel.waitResource(ResourceNames.Interrupt);
                state = 2;
                break;
            case 2:
                Resource interruptResource = kernel.getResource(ResourceNames.Interrupt);
                jobGovernorId = (int) interruptResource.removeElement();
                state = 3;
                break;
            case 3:
                kernel.releaseResourceFor(ResourceNames.FromInterrupt, jobGovernorId);
                state = 1;
                break;
            default:
                assert(false);
                break;
        }
    }
}
