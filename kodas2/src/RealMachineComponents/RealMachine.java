package RealMachineComponents;

import Constants.*;
import OSException.*;
import Utils.Conversion;
import Utils.RegisterContainer;

import java.io.BufferedReader;
import java.io.IOException;

public class RealMachine {
    Register R1, R2, R3, IC, FLAGS, PTR, CS, DS;
    Register PI, SI, TI;
    MachineMemory machineMemory;
    VirtualMachine vm = null;
    boolean MODE;
    public InterruptHandler interruptHandler;
    public PagingMechanism pagingMechanism;
    ExternalMemory externalMemory;
    public ChannelMechanism channelMechanism;
    FileSystem fileSystem;
    public UserInput userInput;

    public RealMachine() {

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
        userInput = new UserInput();
        channelMechanism = new ChannelMechanism(machineMemory, externalMemory, userInput);
        fileSystem = new FileSystem(externalMemory);
    }

    public RegisterContainer containerOfRegisters() {
        return new RegisterContainer(R1, R2, R3, FLAGS, IC, CS, DS, SI, PI, PTR);
    }

    public void decreaseTimer() {
        TI.setValue(Math.max(TI.value() - 1, 0));
    }

    public void resetTimer() {
        TI.setValue(10);
    }

    public int TIValue() {
        return TI.value();
    }

    public void copyProgramToSupervisorMemory(String programName) throws OSException {
        int startInExternal = fileSystem.findFirstFileHeaderWord(programName); // 3 are subtracted for "------", programName, "$PROG$"
        if (startInExternal == -1) {
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
    }

    public boolean isProgramHeaderCorrect() {
        return Conversion.characterArrayToString(machineMemory.getWord(0)).equals("$PROG$") && Conversion.characterArrayToString(machineMemory.getWord(2)).equals("------");
    }

    public boolean checkForFins(int blockNum) {
        int firstWord = blockNum * Constants.blockLengthInWords;
        int lastWord = (blockNum + 1) * Constants.blockLengthInWords - 1;
        for (int i = firstWord; i <= lastWord; i++) {
            if (Conversion.characterArrayToString(machineMemory.getWord(i)).equals("$FINS$")) {
                return true;
            }
        }
        return false;
    }

    public void createPaging() {
        boolean created = pagingMechanism.createVirtualMachinePages();
        assert (created);
    }

    private void loadVirtualMachineFromSuperVisorMemory() {
        int virtualBlock = 0, virtualWord = 0;

        for (int wordNum = 0; wordNum < Constants.numberOfSupervisorBLocks * Constants.blockLengthInWords; wordNum++) {
            String curWord = Conversion.characterArrayToString(machineMemory.getWord(wordNum));
            if (curWord.startsWith("$$") && curWord.endsWith("$$$")) {
                virtualBlock = Conversion.convertHexCharacterToDigit(curWord.charAt(2));
                virtualWord = 0;
            } else if (curWord.equals(".CODES")) {
                virtualBlock = 1;
                virtualWord = 0;
            } else if (curWord.equals(".DATAS")) {
                virtualBlock = 8;
                virtualWord = 0;
            } else if (curWord.equals("$FINS$")) {
                break;
            } else {
                pagingMechanism.setWord(virtualBlock * Constants.blockLengthInWords + virtualWord, machineMemory.getWord(wordNum));
                virtualWord++;
                if (virtualWord == Constants.blockLengthInWords) {
                    virtualWord = 0;
                    virtualBlock++;
                }
            }
        }
    }

    public VirtualMachine createVirtualMachine() {
        loadVirtualMachineFromSuperVisorMemory();
        IC.setValue(0x10);
        CS.setValue(0x10);
        DS.setValue(0x80);
        R1.setValue(0);
        R2.setValue(0);
        R3.setValue(0);
        FLAGS.setValue(0);
        this.vm = new VirtualMachine(R1, R2, R3, FLAGS, IC, CS, DS, interruptHandler, pagingMechanism);
        return this.vm;
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
        if (ans < 0) ans = -1;
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
                System.out.println("show real memory {wordNum}\ndisplay channeling\nchangereg {name} {newVal}");
                System.out.println("setword real {num} {newVal}\nsetword virtual {num} {newVal}");
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

    public void outputNumber() {
        Character[] converted = Conversion.convertIntToDecCharacterArray(R1.value());
        int numberLen = converted.length;
        Character[] temp = machineMemory.getWord(0);
        machineMemory.setArr(0, converted);
        channelMechanism.SB.setValue(0);
        channelMechanism.SW.setValue(0);
        channelMechanism.ST.setValue(STValues.SupervisorMemory);
        channelMechanism.DT.setValue(DTValues.Screen);
        channelMechanism.BC.setValue(numberLen);
        channelMechanism.exchange();
        machineMemory.setWord(0, temp);
    }

    public void outputSymbols() {
        int symbolsToOutput = R3.value();
        int virtualAddress = R1.value();
        while (symbolsToOutput > 0) {
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

    public void inputLine() {
        if (userInput.bufferLength() == 0) {
            userInput.readUserLine();
        }
        int symbolsToGet = Math.min(R3.value(), userInput.bufferLength());
        int virtualAddress = R1.value();
        while (symbolsToGet > 0) {
            int virtualBlock = virtualAddress / (Constants.blockLengthInWords * Constants.WordLengthInBytes);
            int offset = virtualAddress % (Constants.blockLengthInWords * Constants.WordLengthInBytes);
            int length = Math.min(symbolsToGet, Constants.blockLengthInWords * Constants.WordLengthInBytes - offset);

            channelMechanism.ST.setValue(STValues.Keyboard);
            channelMechanism.DB.setValue(pagingMechanism.getRealBlockNumber(virtualBlock));
            channelMechanism.DW.setValue(offset);
            channelMechanism.DT.setValue(DTValues.UserMemory);
            channelMechanism.BC.setValue(length);
            channelMechanism.exchange();

            symbolsToGet -= length;
            virtualAddress += length;
        }
        if (symbolsToGet == 0) R3.setValue(0);
    }

    public void openFile() {
        Character[] name = pagingMechanism.getWord(R1.value());
        StringBuilder nameBuilder = new StringBuilder();
        for (char ch : name) {
            nameBuilder.append(ch);
        }
        String filename = nameBuilder.toString();

        FileHandler handler = fileSystem.openFile(filename);
        if (handler == null) {
            fileSystem.createFile(filename);
            handler = fileSystem.openFile(filename);
        }
        R2.setValue(handler.fileNumber);
    }

    public void closeFile() {
        int fileNumber = R2.value();
        if (fileSystem.closeFile(fileNumber)) {
            R1.setValue(0);
        } else {
            R1.setValue(fileNumber);
        }
    }

    public void writeFile() {
        int symbolsToOutput = R3.value();
        int virtualAddress = R1.value();
        FileHandler handler = fileSystem.fileHandlerByNumber(R2.value());
        while (symbolsToOutput > 0) {
            int virtualBlock = virtualAddress / (Constants.blockLengthInWords * Constants.WordLengthInBytes);
            int offset = virtualAddress % (Constants.blockLengthInWords * Constants.WordLengthInBytes);
            int length = Math.min(symbolsToOutput, Constants.blockLengthInWords * Constants.WordLengthInBytes - offset);

            int fileBlock = handler.wordPoz / Constants.blockLengthInWords;
            int fileOffset = (handler.wordPoz % Constants.blockLengthInWords) * Constants.WordLengthInBytes + handler.bytePoz;

            channelMechanism.SB.setValue(pagingMechanism.getRealBlockNumber(virtualBlock));
            channelMechanism.SW.setValue(offset);
            channelMechanism.ST.setValue(STValues.UserMemory);

            channelMechanism.DT.setValue(DTValues.ExternalMemory);
            channelMechanism.DB.setValue(fileBlock);
            channelMechanism.DW.setValue(fileOffset);
            channelMechanism.BC.setValue(length);

            int prevWord = handler.wordPoz;
            int prevByte = handler.bytePoz;
            handler.addBytes(length);
            int wordDiff = handler.wordPoz - prevWord;
            int startedOffset = (prevByte == 0) ? 0 : 1;
            for (int i = 0; i < wordDiff; i++) {
                externalMemory.setWordAndShift(prevWord + startedOffset, Conversion.stringToCharacterArray("      "));
            }
            channelMechanism.exchange();

            symbolsToOutput -= length;
            virtualAddress += length;
        }
    }

    public void readFile() {
        int fileNumber = R2.value();
        FileHandler handler = fileSystem.fileHandlerByNumber(fileNumber);

        int symbolsToRead = R3.value();
        int virtualAddress = R1.value();
        int fileEnd = fileSystem.findFileEndWord(handler.wordPoz);
        int bytesToFileEnd = (fileEnd - handler.wordPoz - 1) * Constants.WordLengthInBytes + (Constants.WordLengthInBytes - handler.bytePoz);
        if (symbolsToRead > bytesToFileEnd) {
            symbolsToRead = bytesToFileEnd;
            R3.setValue(bytesToFileEnd);
        }
        while (symbolsToRead > 0) {

            int sourceBytePoz = handler.wordPoz * Constants.WordLengthInBytes + handler.bytePoz;
            int sourceBlockPoz = sourceBytePoz / (Constants.blockLengthInWords * Constants.WordLengthInBytes);
            int sourceOffset = sourceBytePoz % (Constants.blockLengthInWords * Constants.WordLengthInBytes);

            int virtualBlock = virtualAddress / (Constants.blockLengthInWords * Constants.WordLengthInBytes);
            int offset = virtualAddress % (Constants.blockLengthInWords * Constants.WordLengthInBytes);
            int length = Math.min(symbolsToRead, Constants.blockLengthInWords * Constants.WordLengthInBytes - offset);

            channelMechanism.SB.setValue(sourceBlockPoz);
            channelMechanism.SW.setValue(sourceOffset);
            channelMechanism.ST.setValue(STValues.ExternalMemory);

            channelMechanism.DB.setValue(pagingMechanism.getRealBlockNumber(virtualBlock));
            channelMechanism.DW.setValue(offset);
            channelMechanism.DT.setValue(DTValues.UserMemory);

            channelMechanism.BC.setValue(length);
            channelMechanism.exchange();

            symbolsToRead -= length;
            virtualAddress += length;
            handler.addBytes(length);
        }
    }

    public void deleteFile() {
        int fileNumber = R2.value();
        FileHandler handler = fileSystem.fileHandlerByNumber(fileNumber);
        if (handler != null) {
            int fileStartWord = handler.fileStartPoz();
            int fileEndWord = fileSystem.findFileEndWord(fileStartWord);
            int lengthInWords = fileEndWord - fileStartWord + 1;
            while (lengthInWords > 0) {
                fileSystem.removeWordAndShift(fileStartWord);
                lengthInWords--;
            }
            fileSystem.closeFile(fileNumber);
        } else {
            R2.setValue(0);
        }
    }


    public void inputNumber() {
        R1.setValue(userInput.readNumber());
    }

    private int interruptHandling() {
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
                outputNumber();
            } else if (SI.value() == SIValues.OutputSymbols) {
                outputSymbols();
            } else if (SI.value() == SIValues.InputLine) {
                inputLine();
            } else if (SI.value() == SIValues.InputNumber) {
                inputNumber();
            } else if (SI.value() == SIValues.OpenFile) {
                openFile();
            } else if (SI.value() == SIValues.CloseFile) {
                closeFile();
            } else if (SI.value() == SIValues.WriteFile) {
                writeFile();
            } else if (SI.value() == SIValues.ReadFile) {
                readFile();
            } else if (SI.value() == SIValues.DeleteFile) {
                deleteFile();
            }
        }
        if (TI.value() == 0) {
            System.out.println("Timer interrupt happened");
            TI.setValue(10);
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

        MODE = false;
        while (true) {
            vm.execute();
            TI.setValue(TI.value() - 1);
            if (interruptHandler.test()) {
                MODE = true;
                if (interruptHandling() == 1) {
                    break;
                }
                MODE = false;
            }
        }
        pagingMechanism.freeVirtualMachinePages();
        this.vm = null;
    }

    public void setUserMode() {
        MODE = false;
    }
}
