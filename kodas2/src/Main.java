import RealMachineComponents.RealMachine;
import Utils.Kernel;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello to this OS");
        RealMachine rm = new RealMachine();
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        Kernel kernel = new Kernel(rm);
        kernel.run();
//        assert(false);
//        try {
//            while (true) {
//                System.out.print(">");
//                String command = br.readLine();
////                br.
//                String[] parts = command.split("\\s+");
//                if ((parts.length == 1) && (Objects.equals(parts[0], "shutdown"))) {
//                    break;
//                } else if ((parts.length == 2) && (parts[0].equals("load"))) {
//                    try{
//                        rm.load(parts[1]);
//                        rm.exec();
//                    }
//                    catch(IncorrectProgramSizeException e){
//                        System.out.println("Program size was incorrect");
//                    }
//                    catch(NotEnoughFreePagesException e){
//                        System.out.println("Real machine didn't have enough pages");
//                    }
//                    catch(ProgramNotFoundException e){
//                        System.out.println("Couldn't find program file");
//                    }
//                    catch(OSException e){
//                        System.out.println("OS exception");
//                    }
//                }
//                else if ((parts.length == 2) && (parts[0].equals("debug"))){
//                    try{
//                        rm.load(parts[1]);
//                        rm.debug(br);
//                    }
//                    catch(IncorrectProgramSizeException e){
//                        System.out.println("Program size was incorrect");
//                    }
//                    catch(NotEnoughFreePagesException e){
//                        System.out.println("Real machine didn't have enough pages");
//                    }
//                    catch(ProgramNotFoundException e){
//                        System.out.println("Couldn't find program file");
//                    }
//                    catch(OSException e){
//                        System.out.println("OS exception");
//                    }
//                }
//                else {
//                    System.out.println("Unfamiliar command");
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}