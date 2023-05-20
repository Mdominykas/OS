package Processes;

import Constants.*;

import RealMachineComponents.ChannelMechanism;
import Resources.LineInformation;
import Resources.Resource;
import Resources.ResourceNames;
import Utils.Kernel;

public class PrintLine extends Process {
    PrintLine(Kernel kernel) {
        super(kernel);
    }

    public void run() {
        switch(state)
        {
            case 1:
                kernel.waitResource(ResourceNames.LineInMemory);
                state++;
                break;
            case 2:
                kernel.waitResource(ResourceNames.ChannelMechanism);
                state++;
                break;
            case 3:
                Resource lineInMemory = kernel.getResource(ResourceNames.LineInMemory);
                Object object = lineInMemory.removeElement();
                if(object instanceof String)
                {
                    String line = (String) object;
                    System.out.println(line);
                }
                else if (object instanceof LineInformation)
                {
                    LineInformation lineInformation = (LineInformation) object;
                    ChannelMechanism channelMechanism = kernel.realMachine.channelMechanism;
                    channelMechanism.SB.setValue(lineInformation.blockNumber);
                    channelMechanism.SW.setValue(lineInformation.byteNum);
                    channelMechanism.BC.setValue(lineInformation.count);
                    if(lineInformation.blockNumber < Constants.numberOfSupervisorBLocks)
                        channelMechanism.ST.setValue(STValues.SupervisorMemory);
                    else
                        channelMechanism.ST.setValue(STValues.UserMemory);
                    channelMechanism.DT.setValue(DTValues.Screen);
                    channelMechanism.exchange();
                }
                else
                {
                    assert(false);
                }

                state++;
                break;
            case 4:
                kernel.releaseResource(ResourceNames.ChannelMechanism);
                state = 1;
                break;
            default:
                assert(false);
        }
    }
}
