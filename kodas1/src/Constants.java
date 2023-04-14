public class Constants {
    public static int CF = 1;
    public static int ZF = 2;
    public static int SF = 4;
    public static int OF = 8;
    public static int realMachineLengthInBlocks = 256;
    public static int virtualMachineLengthInBlocks = 16;
    public static int WordLength = 6;
    public static int blockLengthInWords = 16;
    public static int realMachineLengthInWords = realMachineLengthInBlocks * blockLengthInWords;
    public static int virtualMachineLengthInWords = realMachineLengthInBlocks * blockLengthInWords;
    public static int numberOfSupervisorBLocks = 16;

}
