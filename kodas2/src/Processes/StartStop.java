package Processes;

import Utils.Kernel;

public class StartStop extends Process{
    StartStop(Kernel kernel)
    {
        super(kernel);
    }
    
    private void createResources()
    {

    }

    @Override
    public void run() {
        switch (state) {
            case 1:
                
                state = 2;
                break;
            case 2:
                // Code to be executed when state is 2
                break;
            case 3:
                // Code to be executed when state is 3
                break;
            case 4:
                // Code to be executed when state is 4
                break;
            case 5:

                break;
            default:
                assert(false);
                break;
        }

    }
}
