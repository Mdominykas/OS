package Utils;

import Processes.Process;
import Resources.Resource;
import Resources.ResourceNames;

public class Logging {
    public static void logCreatedProcess(Process process)
    {
        System.out.println("[LOG]: Created process: " + process.getClass());
    }
    public static void logDeletedProcess(Process process)
    {
        System.out.println("[LOG]: Deleted process: " + process.getClass());
    }
    public static void logProcessWaitsResource(Process process, Resource resource)
    {
        System.out.println("[LOG]: Process " + process.getClass() + " waits resource " + ResourceNames.nameToString(resource));
    }

    public static void logProcessReleaseResource(Process process, Resource resource)
    {
        System.out.println("[LOG]: Process " + process.getClass() + " releases resource " + ResourceNames.nameToString(resource));
    }
}
