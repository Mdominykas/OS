public class InterruptHandler {
    Register SI, PI, TI;
    InterruptHandler(Register PI, Register SI, Register TI)
    {
        this.PI = PI;
        this.SI = SI;
        this.TI = TI;
    }
    boolean test()
    {
        return (SI.value() + PI.value() > 0) || (TI.value() == 0);
    }
}
