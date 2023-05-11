import Constants.*;

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

    private Register arithmeticParseRegister(Character number) {
        if (number == '1') {
            return R1;
        } else if (number == '2') {
            return R2;
        } else if (number == '3') {
            return R3;
        } else {
            interruptHandler.setPI(PIValues.InvalidOperation);
            return R1;
        }
    }

    int parseSecondArithmeticOperand(String command) {
        int otherValue;
        if (command.charAt(3) == 'r')
            otherValue = arithmeticParseRegister(command.charAt(5)).value();
        else if (command.charAt(3) == 'c')
            otherValue = Conversion.ConvertHexStringToInt(command.substring(5, 6));
        else {
            interruptHandler.setPI(PIValues.InvalidOperation);
            return 0;
        }
        return otherValue;
    }

    void executeAdd(String command) {
        Register Rx = arithmeticParseRegister(command.charAt(4));
        int otherValue = parseSecondArithmeticOperand(command);
        Rx.add(otherValue, FLAGS);
    }

    void executeSub(String command) {
        Register Rx = arithmeticParseRegister(command.charAt(4));
        int otherValue = parseSecondArithmeticOperand(command);
        Rx.subtract(otherValue, FLAGS);
    }

    void executeMul(String command) {
        Register Rx = arithmeticParseRegister(command.charAt(4));
        int otherValue = parseSecondArithmeticOperand(command);
        Rx.multiply(otherValue, FLAGS);
    }

    void executeDiv(String command) {
        Register Rx = arithmeticParseRegister(command.charAt(4));
        int otherValue = parseSecondArithmeticOperand(command);
        Rx.divide(otherValue, FLAGS);
    }

    void executeCmp(String command) {
        Register Rx = arithmeticParseRegister(command.charAt(4));
        int otherValue = parseSecondArithmeticOperand(command);
        Rx.cmp(otherValue, FLAGS);
    }

    Register parseMvRegister(Character number) {
        if (number == '1') {
            return R1;
        } else if (number == '2') {
            return R2;
        } else if (number == '3') {
            return R3;
        } else if (number == '4') {
            return CS;
        } else if (number == '5') {
            return DS;
        } else {
            interruptHandler.setPI(PIValues.InvalidOperation);
            return R1;
        }
    }

    void executeMv(String command) {
        if (command.charAt(2) == 'r') {
            Register rx = parseMvRegister(command.charAt(4));
            Register ry = parseMvRegister(command.charAt(5));
            ry.setValue(rx.value());
        } else {
            int location;
            try {
                location = Conversion.ConvertHexStringToInt(command.substring(4, 6));
            } catch (IllegalArgumentException iae) {
                interruptHandler.setPI(PIValues.InvalidOperation);
                return;
            }
            Register r = parseMvRegister(command.charAt(3));
            if (command.charAt(2) == 'o') {
                pagingMechanism.writeNumber(location, r.value());
            } else if (command.charAt(2) == 'i') {
                r.setValue(Conversion.ConvertHexStringToInt(pagingMechanism.getWord(location)));
            } else if (command.charAt(2) == 'c') {
                int value;
                try {
                    value = Conversion.ConvertHexStringToInt(command.substring(4, 6));
                } catch (IllegalArgumentException iae) {
                    interruptHandler.setPI(PIValues.InvalidOperation);
                    return;
                }
                r.setValue((r.value() / 256) * 256 + value);
            } else {
                interruptHandler.setPI(PIValues.InvalidOperation);
            }
        }
    }

    public void execute() {
        Character[] command = pagingMechanism.getWord(IC.value());
//        System.out.println("IC value is: " + IC.value());
//        System.out.println("command is:" + Conversion.characterArrayToString(command));
        String commandString = Conversion.characterArrayToString(command);
        if (commandString.startsWith("J")) {
            executeJump(command);
        } else {
            if (commandString.startsWith("ADD")) {
                executeAdd(commandString);
            } else if (commandString.startsWith("SUB")) {
                executeSub(commandString);
            } else if (commandString.startsWith("MUL")) {
                executeMul(commandString);
            } else if (commandString.startsWith("DIV")) {
                executeDiv(commandString);
            } else if (commandString.startsWith("CMP")) {
                executeCmp(commandString);
            } else if (commandString.startsWith("MV")) {
                executeMv(commandString);
            } else if (commandString.equals("OUTNUM")) {
                interruptHandler.setSI(SIValues.OutputNumber);
            } else if (commandString.equals("OUTSIM")) {
                interruptHandler.setSI(SIValues.OutputSymbols);
            } else if (commandString.equals("INPLIN")) {
                interruptHandler.setSI(SIValues.InputLine);
            } else if (commandString.equals("INPNUM")) {
                interruptHandler.setSI(SIValues.InputNumber);
            } else if (commandString.startsWith("HALT")) {
                interruptHandler.setSI(SIValues.Halt);
            } else if (commandString.startsWith("OPENF")) {
                interruptHandler.setSI(SIValues.OpenFile);
            } else if (commandString.startsWith("CLOSEF")) {
                interruptHandler.setSI(SIValues.CloseFile);
            } else if (commandString.startsWith("WRITEF")) {
                interruptHandler.setSI(SIValues.WriteFile);
            } else if (commandString.startsWith("READF")) {
                interruptHandler.setSI(SIValues.ReadFile);
            } else if (commandString.startsWith("DELETF")) {
                interruptHandler.setSI(SIValues.DeleteFile);
            } else {
                interruptHandler.setPI(PIValues.InvalidOperation);
                IC.setValue(IC.value() + 1);
            }
            IC.setValue(IC.value() + 1);
        }
    }
}
