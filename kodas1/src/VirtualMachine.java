import Constants.Constants;
import Constants.PIValues;

import java.util.Arrays;

public class VirtualMachine {
    private final PagingMechanism pagingMechanism;
    Register R1, R2, R3, FLAGS, IC, CS, DS;
    private final InterruptHandler interruptHandler;

    VirtualMachine(Register R1, Register R2, Register R3, Register FLAGS, Register IC, Register CS, Register DS, InterruptHandler interruptHandler, PagingMechanism pagingMechanism) {
        this.R1 = R1;
        this.R2 = R2;
        this.R3 = R3;
        this.FLAGS = FLAGS;
        this.IC = IC;
        this.CS = CS;
        this.DS = DS;
        this.interruptHandler = interruptHandler;
        this.pagingMechanism = pagingMechanism;
    }

    private void executeJump(Character[] command) {
        Character[] locationCom = new Character[2];
        locationCom[0] = command[4];
        locationCom[1] = command[5];
        int location = Conversion.ConvertHexStringToInt(locationCom);
        String commandString = Conversion.characterArrayToString(command);
        boolean incorrectCommand = false;
        boolean sf = (FLAGS.value() & Constants.SF) != 0, zf = (FLAGS.value() & Constants.ZF) != 0;
        boolean cf = (FLAGS.value() & Constants.CF) != 0, of = (FLAGS.value() & Constants.OF) != 0;
        if (commandString.startsWith("JMP")) {
            IC.setValue(location);
        } else if (commandString.startsWith("JE")) {
            if (zf) {
                IC.setValue(location);
            } else {
                IC.setValue(IC.value() + 1);
            }
        } else if (commandString.startsWith("JNE")) {
            if (!zf) {
                IC.setValue(location);
            } else {
                IC.setValue(IC.value() + 1);
            }
        } else if (commandString.startsWith("JL")) {
            if (sf != of) {
                IC.setValue(location);
            } else {
                IC.setValue(IC.value() + 1);
            }
        } else if (commandString.startsWith("JLE")) {
            if ((!zf) || (sf != of)) {
                IC.setValue(location);
            } else {
                IC.setValue(IC.value() + 1);
            }
        } else if (commandString.startsWith("JG")) {
            if ((!zf) || (sf == of)) {
                IC.setValue(location);
            } else {
                IC.setValue(IC.value() + 1);
            }
        } else if (commandString.startsWith("JGE")) {
            if (sf == of) {
                IC.setValue(location);
            } else {
                IC.setValue(IC.value() + 1);
            }
        } else if (commandString.startsWith("JB")) {
            if (cf) {
                IC.setValue(location);
            } else {
                IC.setValue(IC.value() + 1);
            }
        } else if (commandString.startsWith("JBE")) {
            if (cf || zf) {
                IC.setValue(location);
            } else {
                IC.setValue(IC.value() + 1);
            }
        } else if (commandString.startsWith("JA")) {
            if (!cf && !zf) {
                IC.setValue(location);
            } else {
                IC.setValue(IC.value() + 1);
            }
        } else if (commandString.startsWith("JAE")) {
            if (!cf) {
                IC.setValue(location);
            } else {
                IC.setValue(IC.value() + 1);
            }
        } else {
            interruptHandler.setPI(PIValues.InvalidOperation);
        }
    }

    public void execute() {
        Character[] command = pagingMechanism.getWord(IC.value());
        System.out.println("IC value is: " + IC.value());
        System.out.println("command is:" + Conversion.characterArrayToString(command));
        if (Conversion.characterArrayToString(command).startsWith("J")) {
            executeJump(command);
        } else {
            interruptHandler.setPI(PIValues.InvalidOperation);
            IC.setValue(IC.value() + 1);
        }
    }
}
