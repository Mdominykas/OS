package Resources;

public class ResourceNames {
    public static final int ChannelMechanism = 1;
    public static final int SupervisorMemory = 2;
    public static final int UserMemory = 3;
    public static final int ExternalMemory = 4;
    public static final int MosEnd = 5;
    public static final int FromUserInterface = 6;
    public static final int TaskInSupervisorMemory = 7;
    public static final int TaskProgramInSupervisorMemory = 8;
    public static final int LoaderPacket = 9;
    public static final int FromLoader = 10;
    public static final int LineInMemory = 11;
    public static final int FromInterrupt = 12;
    public static final int UserInput = 13;
    public static final int UserInputReceived = 14;
    public static final int Interrupt = 15;
    public static final int WorkWithFiles = 16;
    public static final int WorkWithFilesEnd = 17;
    public static final int NonExistent = 18;

    public static String nameToString(Resource resource)
    {
        switch (resource.name) {
            case ChannelMechanism:
                return "ChannelMechanism";
            case SupervisorMemory:
                return "SupervisorMemory";
            case UserMemory:
                return "UserMemory";
            case ExternalMemory:
                return "ExternalMemory";
            case MosEnd:
                return "MosEnd";
            case FromUserInterface:
                return "FromUserInterface";
            case TaskInSupervisorMemory:
                return "TaskInSupervisorMemory";
            case TaskProgramInSupervisorMemory:
                return "TaskProgramInSupervisorMemory";
            case LoaderPacket:
                return "LoaderPacket";
            case FromLoader:
                return "FromLoader";
            case LineInMemory:
                return "LineInMemory";
            case FromInterrupt:
                return "FromInterrupt";
            case UserInput:
                return "UserInput";
            case Interrupt:
                return "Interrupt";
            case WorkWithFiles:
                return "WorkWithFiles";
            case WorkWithFilesEnd:
                return "WorkWithFilesEnd";
            case NonExistent:
                return "NonExistent";
            case UserInputReceived:
                return "UserInputReceived";
            default:
                assert(false);
                return "UnknownResource";
        }
    }

}