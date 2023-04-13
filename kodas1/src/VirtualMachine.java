public class VirtualMachine {
    Register R1, R2, R3, IC, CS, DS;
    InterruptHandler interruptHandler;
    VirtualMachine(Register R1, Register R2, Register R3, Register IC, Register CS, Register DS, InterruptHandler interruptHandler)
    {
        this.R1 = R1;
        this.R2 = R2;
        this.R3 = R3;
        this.IC = IC;
        this.CS = CS;
        this.DS = DS;
        this.interruptHandler = interruptHandler;
    }
    public void execute()
    {
        while(true)
        {
            /// perform command
            if(interruptHandler.test())
                break;
        }
    }
}
