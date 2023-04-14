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
    }

    void exchange() {
        ArrayList<Character> temp = new ArrayList<>();
        int readBytes = 0;
        while (readBytes < BC.value) {
            int byteNum = SB.value * Constants.blockLengthInWords * Constants.WordLength + SW.value + readBytes;
            if ((ST.value == 1) || (ST.value == 2)) {
                temp.add(machineMemory.getByte(byteNum));
            }
            else if (ST.value == 3){
//                optimizuoti
                temp.add(externalMemory.getByte(byteNum));
            }
            readBytes++;
        }
        int wroteBytes = 0;
        while(wroteBytes < BC.value)
        {
            int byteNum = DB.value * Constants.blockLengthInWords * Constants.WordLength + SW.value + wroteBytes;
            if((DT.value == 1) || (DT.value == 2))
            {
                machineMemory.setByte(byteNum, temp.get(byteNum));
            }
            else if (DT.value == 3)
                externalMemory.setByte(byteNum, temp.get(byteNum));
            wroteBytes++;
        }
    }

}
