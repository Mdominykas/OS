public class RealMachine {
    Register R1, R2, R3, IC, FLAGS;
    Register PI, SI, TI;
    boolean isModeSupervisor;
    RealMachine() {
        R1 = new Register(6);
        R2 = new Register(6);
        R3 = new Register(6);
        IC = new Register(4);
        R1 = new Register(6);
        FLAGS = new Register(1);
        PI = new Register(1);
        SI = new Register(1);
        TI = new Register(1);
    }
}
