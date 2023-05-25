package Processes;

import Constants.Constants;
import OSException.ProgramNotFoundException;
import RealMachineComponents.UserInput;
import Resources.Resource;
import Resources.ResourceNames;
import Utils.Kernel;

import java.util.ArrayList;

public class ReadFromInterface extends Process {
    ArrayList<Character> command;
    UserInput userInput;
    boolean programNotFound;

    ReadFromInterface(Kernel kernel) {
        super(kernel);
        command = new ArrayList<>();
        userInput = kernel.realMachine.userInput;
    }

    public void run() {
        switch (state) {
            case 1:
                command.clear();
                kernel.waitResource(ResourceNames.FromUserInterface);
                state++;
                break;
            case 2:
                if (kernel.someoneWaitsResource(ResourceNames.UserInput)) {
                    state = 3;
                } else {
                    state = 5;
                }
                break;
            case 3:
                kernel.releaseResource(ResourceNames.UserInput);
                state = 4;
                break;
            case 4:
                kernel.waitResource(ResourceNames.UserInputReceived);
                state = 1;
                break;
            case 5:
                userInput.readUntilEndOfLineButNotMoreThanN(100, command);
                if (commandString().equals("shutdown")) {
                    state = 6;
                } else {
                    state = 7;
                }
                break;
            case 6:
                kernel.releaseResource(ResourceNames.MosEnd);
                state = 1;
                break;
            case 7:
                if (commandString().startsWith("load ")) {
                    state = 8;
                } else {
                    state = 15;
                }
                break;
            case 8:
                kernel.waitResource(ResourceNames.SupervisorMemory);
                state = 9;
                break;
            case 9:
                kernel.waitResource(ResourceNames.ExternalMemory);
                state = 10;
                break;
            case 10:
                kernel.waitResource(ResourceNames.ChannelMechanism);
                state = 11;
                break;
            case 11:
                String fileName = commandString().substring(5);
                try {
                    kernel.realMachine.copyProgramToSupervisorMemory(fileName);
                    programNotFound = false;
                } catch (ProgramNotFoundException ignored) {
                    programNotFound = true;
                }
                state = 12;
                break;
            case 12:
                kernel.releaseResource(ResourceNames.ChannelMechanism);
                state = 13;
                break;
            case 13:
                kernel.releaseResource(ResourceNames.ExternalMemory);
                state = 14;
                break;
            case 14:
                if(! programNotFound){
                    kernel.releaseResource(ResourceNames.TaskInSupervisorMemory);
                }
                else {
                    kernel.releaseResource(ResourceNames.SupervisorMemory);
                }
                state = 1;
                break;
            case 15:
                Resource lineInMemory = kernel.getResource(ResourceNames.LineInMemory);
                String line = "incorrect command";
                lineInMemory.addElement(line);
                kernel.releaseResource(ResourceNames.LineInMemory);
                state = 1;
                break;
        }
    }

    private String commandString() {
        StringBuilder sb = new StringBuilder();
        for (Character character : command)
            sb.append(character);
        return sb.toString();
    }
}
