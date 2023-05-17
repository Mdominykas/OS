package Utils;

import RealMachineComponents.Register;

public class RegisterContainer {
    public Register R1, R2, R3, FLAGS, IC, CS, DS, SI, PI;
    public RegisterContainer(Register R1, Register R2, Register R3, Register FLAGS, Register IC, Register CS, Register DS, Register SI, Register PI)
    {
        this.R1 = R1;
        this.R2 = R2;
        this.R3 = R3;
        this.FLAGS = FLAGS;
        this.IC = IC;
        this.CS = CS;
        this.DS = DS;
        this.SI = SI;
        this.PI = PI;
    }
}
