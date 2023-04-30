import Constants.Constants;
import Constants.*;

import java.util.ArrayList;

public class ChannelMechanism {
    Register SB, SW, DB, DW, BC, ST, DT;
    MachineMemory machineMemory;
    ExternalMemory externalMemory;

    ChannelMechanism(MachineMemory machineMemory, ExternalMemory externalMemory) {
        SB = new Register(2);
        SW = new Register(2);
        DB = new Register(2);
        DW = new Register(2);
        BC = new Register(2);
        ST = new Register(1);
        DT = new Register(1);
        this.machineMemory = machineMemory;
        this.externalMemory = externalMemory;
    }

    void exchange() {
        ArrayList<Character> temp = new ArrayList<>();
        int readBytes = 0;
        while (readBytes < BC.value()) {
            int byteNum = SB.value() * Constants.blockLengthInWords * Constants.WordLengthInBytes + SW.value() + readBytes;
            if ((ST.value() == STValues.SupervisorMemory) || (ST.value() == STValues.UserMemory)) { // galimai jos abi nuo pradžios numeruojasi
                temp.add(machineMemory.getByte(byteNum));
            }
            else if (ST.value() == STValues.ExternalMemory){
//                optimizuoti
                temp.add(externalMemory.getByte(byteNum));
            }
            readBytes++;
        }
        int wroteBytes = 0;
        while(wroteBytes < BC.value())
        {
            int byteNum = DB.value() * Constants.blockLengthInWords * Constants.WordLengthInBytes + SW.value() + wroteBytes;
            if((DT.value() == 1) || (DT.value() == 2))
            {
                machineMemory.setByte(byteNum, temp.get(wroteBytes));
            }
            else if (DT.value() == 3)
                externalMemory.setByte(byteNum, temp.get(wroteBytes));
            wroteBytes++;
        }
    }

}
