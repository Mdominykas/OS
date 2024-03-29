package RealMachineComponents;

import Constants.PIValues;
import Constants.SIValues;

public class InterruptHandler {
    Register SI, PI, TI;
    InterruptHandler(Register PI, Register SI, Register TI)
    {
        this.PI = PI;
        this.SI = SI;
        this.TI = TI;
    }
    public boolean test()
    {
        return (SI.value() + PI.value() > 0) || (TI.value() == 0);
    }

    public boolean testSIAndPI()
    {
        return (SI.value() + PI.value() > 0);
    }

    public void setSI(int value){
        SI.setValue(value);
    }

    public void setPI(int value){
        PI.setValue(value);
    }

    public void clearSIAndPI(){
        PI.setValue(PIValues.Nothing);
        SI.setValue(SIValues.Nothing);
    }

    public void handle()
    {

    }

}
