package Processes;

import Utils.Kernel;

public class Idle extends Process {
    Idle(Kernel kernel) {
        super(kernel);
    }

    public void run() {
        switch(state)
        {
            case 1:
                ;
                break;
            default:
                assert(false);
        }
    }
}
