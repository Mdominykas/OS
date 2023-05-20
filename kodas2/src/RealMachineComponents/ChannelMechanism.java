package RealMachineComponents;

import Constants.Constants;
import Constants.*;


import java.util.ArrayList;

public class ChannelMechanism {
    public Register SB, SW, DB, DW, BC, ST, DT;
    private final MachineMemory machineMemory;
    private final ExternalMemory externalMemory;
    private final UserInput userInput;
    ChannelMechanism(MachineMemory machineMemory, ExternalMemory externalMemory, UserInput userInput) {
        SB = new Register(2);
        SW = new Register(2);
        DB = new Register(2);
        DW = new Register(2);
        BC = new Register(2);
        ST = new Register(1);
        DT = new Register(1);
        this.machineMemory = machineMemory;
        this.externalMemory = externalMemory;
        this.userInput = userInput;
    }

    public void exchange() {
        ArrayList<Character> temp = new ArrayList<>();
        int readBytes = 0;
        while (readBytes < BC.value()) {
            int byteNum = SB.value() * Constants.blockLengthInWords * Constants.WordLengthInBytes + SW.value() + readBytes;
            if ((ST.value() == STValues.SupervisorMemory) || (ST.value() == STValues.UserMemory)) { // galima, nes jos abi nuo pradžios numeruojasi
                temp.add(machineMemory.getByte(byteNum));
            } else if (ST.value() == STValues.ExternalMemory) {
//                optimizuoti
                temp.add(externalMemory.getByte(byteNum));
            }
            readBytes++;
        }

        if(ST.value() == STValues.Keyboard){
            boolean reachedEnd = userInput.readUntilEndOfLineButNotMoreThanN(BC.value(), temp);
            while(temp.size() < BC.value()){
                temp.add('$');
            }
        }
        int wroteBytes = 0;
        while (wroteBytes < BC.value()) {
            int byteNum = DB.value() * Constants.blockLengthInWords * Constants.WordLengthInBytes + DW.value() + wroteBytes;
            if ((DT.value() == DTValues.SupervisorMemory) || (DT.value() == DTValues.UserMemory)) {
                machineMemory.setByte(byteNum, temp.get(wroteBytes));
            } else if (DT.value() == DTValues.ExternalMemory){
                externalMemory.setByte(byteNum, temp.get(wroteBytes));
            }
            wroteBytes++;
        }
        if(DT.value() == DTValues.Screen){
            wroteBytes = 0;
            boolean hasSlash = false;
            while(wroteBytes < BC.value()){
                if(hasSlash){
                    if(temp.get(wroteBytes) == 'n')
                    {
                        System.out.print('\n');
                    }
                    else{
                        System.out.print('\\');
                    }
                    hasSlash = false;
                }
                else if(temp.get(wroteBytes) == '\\'){
                    hasSlash = true;
                }
                else {
                    System.out.print(temp.get(wroteBytes));
                }
                wroteBytes++;
            }
        }
    }

}
