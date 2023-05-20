package Processes;

import RealMachineComponents.UserInput;
import Resources.Resource;
import Resources.ResourceNames;
import Utils.Kernel;

import java.util.ArrayList;

public class ReadFromInterface extends Process {
    ArrayList<Character> command;
    UserInput userInput;

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
                    state = 4;
                }
                break;
            case 3:
                kernel.releaseResource(ResourceNames.UserInput);
                state = 1;
//                galimai cia reikia kazko laukti
                break;
            case 4:
                userInput.readUntilEndOfLineButNotMoreThanN(100, command);
                if (commandString().equals("shutdown")) {
                    state = 5;
                } else {
                    state = 6;
                }
                break;
            case 5:
                kernel.releaseResource(ResourceNames.MosEnd);
                state = 1;
                break;
            case 6:
                if (commandString().startsWith("load ")) {
                    state = 7;
                } else {
                    state = 10;
                }
                break;
            case 7:
                kernel.waitResource(ResourceNames.SupervisorMemory);
                state = 8;
                break;
            case 8:
                String fileName = commandString().substring(5);
                try
                {
                    kernel.realMachine.load(fileName);
                }
                catch(Exception ignored)
                {
                    assert(false); // not implemented
                }
                state = 9;
                break;
            case 9:
                kernel.releaseResource(ResourceNames.TaskInSupervisorMemory);
                state = 1;
                break;
            case 10:
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
