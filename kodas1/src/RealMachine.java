import Constants.*;
import OSException.*;

import java.io.BufferedReader;
import java.io.IOException;

public class RealMachine {
    Register R1, R2, R3, IC, FLAGS, PTR, CS, DS;
    Register PI, SI, TI;
    boolean MODE;
    MachineMemory machineMemory;
    VirtualMachine vm = null;
    InterruptHandler interruptHandler;
    PagingMechanism pagingMechanism;
    ExternalMemory externalMemory;
    ChannelMechanism channelMechanism;
    FileSystem fileSystem;

    RealMachine() {

        R1 = new Register(6);
        R2 = new Register(6);
        R3 = new Register(6);
        IC = new Register(2);
        R1 = new Register(6);
        CS = new Register(2);
        DS = new Register(2);
        FLAGS = new Register(1);
        PTR = new Register(2);
        PI = new Register(1);
        Register.PI = PI;
        SI = new Register(1);
        TI = new Register(1);
        machineMemory = new MachineMemory();
        interruptHandler = new InterruptHandler(PI, SI, TI);
        pagingMechanism = new PagingMechanism(PTR, machineMemory);
        externalMemory = new ExternalMemory();
        channelMechanism = new ChannelMechanism(machineMemory, externalMemory);
        fileSystem = new FileSystem(externalMemory);
    }

    private void loadVirtualMachineFromSuperVisorMemory(int numberOfBlocks, int wordsInLastBlock) {
        int virtualBlock = 0, virtualWord = 0;
        int lengthInSupervisor = (numberOfBlocks - 1) * Constants.blockLengthInWords + wordsInLastBlock;
        for (int i = 0; i < lengthInSupervisor; i++) {
            String curWord = Conversion.characterArrayToString(machineMemory.getWord(i));
            if (curWord.startsWith("$$$") && curWord.endsWith("$$")) {
                virtualBlock = Conversion.convertHexCharacterToDigit(curWord.charAt(3));
                virtualWord = 0;
            } else if (curWord.equals(".CODES")) {
                virtualBlock = 1;
                virtualWord = 0;
            } else if (curWord.equals(".DATAS")) {
                virtualBlock = 8;
                virtualWord = 0;
            } else {
                pagingMechanism.setWord(virtualBlock * Constants.blockLengthInWords + virtualWord, machineMemory.getWord(i));
                virtualWord++;
                if (virtualWord == 16) {
                    virtualWord = 0;
                    virtualBlock++;
                }
            }
        }
    }

    public void load(String programName) throws OSException {
        if (!pagingMechanism.createVirtualMachinePages()) {
            throw new NotEnoughFreePagesException("");
        }

        int startInExternal = fileSystem.findProgramStartWordNumber(programName);
        if (startInExternal == -1) {
            pagingMechanism.freeVirtualMachinePages();
            throw new ProgramNotFoundException("");
        }
        int fileStartBlock = startInExternal / Constants.blockLengthInWords;
        int fileStartByte = (startInExternal % Constants.blockLengthInWords) * Constants.WordLengthInBytes;
        int wroteBlocks = 0, wordsInLastBlock = -1;
        boolean hadFileEnding = false;
        for (int block = 0; block < 16; block++) {
            wroteBlocks++;
            channelMechanism.SB.setValue(fileStartBlock + block);
            channelMechanism.SW.setValue(fileStartByte);
            channelMechanism.ST.setValue(STValues.ExternalMemory);

            channelMechanism.DB.setValue(block);
            channelMechanism.DW.setValue(0);
            channelMechanism.DT.setValue(DTValues.SupervisorMemory);

            channelMechanism.BC.setValue(Constants.blockLengthInWords * Constants.WordLengthInBytes);
            channelMechanism.exchange();
            int endFile = -1;
            for (int i = 0; i < Constants.blockLengthInWords; i++) {
                if (Conversion.characterArrayToString(machineMemory.getWord(block * Constants.blockLengthInWords + i)).equals("$FINS$")) {
                    endFile = i;
                    break;
                }
            }
            if (endFile != -1) {
                hadFileEnding = true;
                wordsInLastBlock = endFile + 1;
                break;
            }
        }
        if (!hadFileEnding) {
            pagingMechanism.freeVirtualMachinePages();
            throw new IncorrectProgramSizeException("");
        }
        loadVirtualMachineFromSuperVisorMemory(wroteBlocks, wordsInLastBlock);
        IC.setValue(0x10);
        CS.setValue(0x10);
        DS.setValue(0x80);
        R1.setValue(0);
        R2.setValue(0);
        R3.setValue(0);
        FLAGS.setValue(0);
        this.vm = new VirtualMachine(R1, R2, R3, FLAGS, IC, CS, DS, interruptHandler, pagingMechanism);
    }

    private int parseDebugNumber(String num) {
        int ans = -1;
        try {
            if (num.endsWith("h")) {
                ans = Integer.parseInt(num.substring(0, num.length() - 1), 16);
            } else {
                ans = Integer.parseInt(num);
            }
        } catch (Exception ignored) {

        }
        if (ans < 0)
            ans = -1;
        return ans;
    }

    private int debugInteraction(BufferedReader br) {
        int continueFor = -1;
        try {
            System.out.println("enter debug command (help for explanation): ");
            String line = br.readLine();
            String[] parts = line.split("\\s+");
            if ((parts.length == 1) && (parts[0].equals("help"))) {
                System.out.println("list of commands: \nhelp\ncontinue\ncontinue {num}\nprint registers\nprint commands");
                System.out.println("print commands {num}\nshow virtual memory {wordNum}\nshow external memory {wordNum}");
                System.out.println("show real memory {wordNum}\ndisplay channeling\nchange register {name} {newVal}");
                System.out.println("changereg real {num} {newVal}\nsetword virtual {num} {newVal}");
                continueFor = 0;
            } else if (parts[0].equals("continue")) {
                if (parts.length == 1) {
                    continueFor = 1;
                } else if (parts.length == 2) {
                    continueFor = parseDebugNumber(parts[1]);
                }
            } else if (parts[0].equals("print")) {
                continueFor = 0;
                if (parts[1].equals("registers")) {
                    System.out.println("R1 = " + R1.toString() + ", R2 = " + R2.toString() + ", R3 = " + R3.toString());
                    System.out.println("IC = " + IC.toString() + ", DS = " + DS.toString() + ", CS = " + CS.toString());
                    System.out.println("FLAGS = " + FLAGS.toString() + ", PTR = " + PTR.toString());
                } else if ((parts[1].equals("commands")) && (parts.length == 2)) {
                    int startVal = Math.max(0, IC.value() - 2);
                    int endVal = Math.min(IC.value() + 2, IC.range() - 1);
                    for (int i = startVal; i <= endVal; i++) {
                        String hexNum = Conversion.characterArrayToString(Conversion.ConvertIntToHexCharacterArray(i));
                        System.out.println(hexNum + ": " + Conversion.characterArrayToString(pagingMechanism.getWord(i)));
                    }

                } else if ((parts[1].equals("commands")) && (parts.length == 3)) {
                    int num = parseDebugNumber(parts[2]);
                    final int halfOfDisplayLength = 4;
                    if (num != -1) {
                        int startVal = Math.max(0, num - halfOfDisplayLength);
                        int endVal = Math.min(num + halfOfDisplayLength, IC.range() - 1);
                        for (int i = startVal; i <= endVal; i++) {
                            String hexNum = Conversion.characterArrayToString(Conversion.ConvertIntToHexCharacterArray(i));
                            System.out.println(hexNum + ": " + Conversion.characterArrayToString(pagingMechanism.getWord(i)));
                        }
                    }
                } else {
                    continueFor = -1;
                }
            } else if ((parts.length == 4) && (parts[0].equals("show")) && (parts[2].equals("memory"))) {
                continueFor = 0;
                final int halfOfDisplayLength = 4;
                int num = parseDebugNumber(parts[3]);
                int startVal = Math.max(0, num - halfOfDisplayLength);
                if (num == -1) {
                    continueFor = -1;
                } else if (parts[1].equals("real")) {
                    int endVal = Math.min(Constants.realMachineLengthInWords - 1, num + halfOfDisplayLength);
                    for (int i = startVal; i <= endVal; i++) {
                        String hexNum = Conversion.characterArrayToString(Conversion.ConvertIntToHexCharacterArray(i));
                        System.out.println(hexNum + ": " + Conversion.characterArrayToString(machineMemory.getWord(i)));
                    }

                } else if (parts[1].equals("external")) {
                    int endVal = Math.min(Constants.externalMemoryLengthInWords - 1, num + halfOfDisplayLength);
                    for (int i = startVal; i <= endVal; i++) {
                        String hexNum = Conversion.characterArrayToString(Conversion.ConvertIntToHexCharacterArray(i));
                        System.out.println(hexNum + ": " + Conversion.characterArrayToString(externalMemory.getWord(i)));
                    }
                } else {
                    continueFor = -1;
                }
            } else if ((parts.length == 2) && (parts[0].equals("display")) && (parts[1].equals("channeling"))) {
                continueFor = 0;
                System.out.println("PTR =" + PTR.value());
                for (int i = PTR.value() * Constants.blockLengthInWords; i < (PTR.value() + 1) * Constants.blockLengthInWords; i++) {
                    String id = Conversion.characterArrayToString(Conversion.ConvertIntToHexCharacterArray(i - PTR.value() * Constants.blockLengthInWords));
                    System.out.println(id + " : " + Conversion.characterArrayToString(machineMemory.getWord(i)));
                }
            } else if ((parts.length == 3) && (parts[0].equals("changereg"))) {
                int newVal = parseDebugNumber(parts[2]);
                if (newVal != -1) {
                    continueFor = 0;
                    if (parts[1].equals("R1")) {
                        R1.setValue(newVal);
                    } else if (parts[1].equals("R2")) {
                        R2.setValue(newVal);
                    } else if (parts[1].equals("R3")) {
                        R3.setValue(newVal);
                    } else if (parts[1].equals("IC")) {
                        IC.setValue(newVal);
                    } else if (parts[1].equals("FLAGS")) {
                        FLAGS.setValue(newVal);
                    } else if (parts[1].equals("PTR")) {
                        PTR.setValue(newVal);
                    } else if (parts[1].equals("CS")) {
                        CS.setValue(newVal);
                    } else if (parts[1].equals("DS")) {
                        DS.setValue(newVal);
                    } else {
                        continueFor = -1;
                    }
                }
            } else if ((parts.length == 4) && (parts[0].equals("setword"))) {
                int address = parseDebugNumber(parts[2]);
                if ((address != -1) && (parts[3].length() == 6)) {
                    Character[] newVal = Conversion.stringToCharacterArray(parts[3]);
                    continueFor = 0;
                    if (parts[1].equals("real")) {
                        machineMemory.setWord(address, newVal);
                    } else if (parts[1].equals("virtual")) {
                        pagingMechanism.setWord(address, newVal);
                    } else {
                        continueFor = -1;
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return continueFor;
    }

    private int interruptHandling() {
        if (interruptHandler.test()) {
            if (PI.value() != PIValues.Nothing) {
                if (PI.value() == PIValues.InvalidOperation) {
                    System.out.println("Invalid operation");
                    return 1;
                }
            }
            if (SI.value() != SIValues.Nothing) {
//                System.out.println("SI interrupt happened" + SI.value());
                TI.setValue(Math.max(0, TI.value() - 4));
                if (SI.value() == SIValues.Halt) {
                    return 1;
                } else if (SI.value() == SIValues.OutputNumber) {
                    int numberLen = Conversion.ConvertIntToHexCharacterArray(R1.value()).length;
                    Character[] temp = machineMemory.getWord(0);
                    machineMemory.writeNumber(0, R1.value());
                    channelMechanism.SB.setValue(0);
                    channelMechanism.SW.setValue(Constants.WordLengthInBytes - numberLen);
                    channelMechanism.ST.setValue(STValues.SupervisorMemory);
                    channelMechanism.DT.setValue(DTValues.Screen);
                    channelMechanism.BC.setValue(numberLen);
                    channelMechanism.exchange();
                    machineMemory.setWord(0, temp);

                } else if (SI.value() == SIValues.OutputSymbols) {
                    int symbolsToOutput = R3.value();
                    int virtualAddress = R1.value();
                    while(symbolsToOutput > 0){
                        int virtualBlock = virtualAddress / (Constants.blockLengthInWords * Constants.WordLengthInBytes);
                        int offset = virtualAddress % (Constants.blockLengthInWords * Constants.WordLengthInBytes);
                        int length = Math.min(symbolsToOutput, Constants.blockLengthInWords * Constants.WordLengthInBytes - offset);

                        channelMechanism.SB.setValue(pagingMechanism.getRealBlockNumber(virtualBlock));
                        channelMechanism.SW.setValue(offset);
                        channelMechanism.ST.setValue(STValues.UserMemory);
                        channelMechanism.DT.setValue(DTValues.Screen);
                        channelMechanism.BC.setValue(length);
                        channelMechanism.exchange();

                        symbolsToOutput -= length;
                        virtualAddress += length;
                    }
                }
            }
            if (TI.value() == 0) {
                System.out.println("Timer interrupt happened");
                TI.setValue(10);
            }
        }
        interruptHandler.clearSIAndPI();
        return 0;
    }

    public void debug(BufferedReader br) {
        interruptHandler.clearSIAndPI();
        TI.setValue(10);

        int continueFor = 0;
        while (true) {
            while (continueFor <= 0) {
                continueFor = debugInteraction(br);
                if (continueFor == -1) {
                    System.out.println("Couldn't understand program");
                }
            }
            continueFor--;
            MODE = true;
            vm.execute();
            MODE = false;
            TI.setValue(TI.value() - 1);
            if (interruptHandling() == 1) {
                break;
            }
        }
        pagingMechanism.freeVirtualMachinePages();
        this.vm = null;
    }

    public void exec() {
        interruptHandler.clearSIAndPI();
        TI.setValue(10);

        while (true) {
            MODE = true;
            vm.execute();
            MODE = false;
            TI.setValue(TI.value() - 1);
            if (interruptHandling() == 1) {
                break;
            }
        }
        pagingMechanism.freeVirtualMachinePages();
        this.vm = null;
    }
}
