import java.util.Arrays;

public class VirtualMachine {
    private final PagingMechanism pagingMechanism;
    Register R1, R2, R3, IC, CS, DS;
    private final InterruptHandler interruptHandler;
    VirtualMachine(Register R1, Register R2, Register R3, Register IC, Register CS, Register DS, InterruptHandler interruptHandler, PagingMechanism pagingMechanism)
    {
        this.R1 = R1;
        this.R2 = R2;
        this.R3 = R3;
        this.IC = IC;
        this.CS = CS;
        this.DS = DS;
        this.interruptHandler = interruptHandler;
        this.pagingMechanism = pagingMechanism;
    }
    public void execute()
    {
//        execute 1 command
        while(true)
        {
            Character[] command = pagingMechanism.getWord(IC.value);
            System.out.println("command is:");
            System.out.println(Arrays.toString(command));
            /// perform command
            if(interruptHandler.test())
                break;
        }
    }
}
