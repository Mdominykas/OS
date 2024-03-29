import Constants.Constants;
import OSException.IncorrectProgramSizeException;
import OSException.NotEnoughFreePagesException;
import OSException.OSException;
import OSException.ProgramNotFoundException;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello to this OS");
        RealMachine rm = new RealMachine();
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
//
//        Scanner scanner = new Scanner(System.in);
//        int timeWaited = 0;
//        while(true)
//        {
//            try {
//                scanner.hasNextByte();
//                if (System.in.available() > 0) {
//                    System.out.println("available is:" + System.in.available());
////                    String input = scanner.nextLine();
//                    byte[] bytes = System.in.readNBytes(Constants.blockLengthInWords * Constants.WordLengthInBytes);
////                    Character chars =
////                    System.out.println("You entered: " + input);
////                    scanner.
//                    System.out.println("I waited " + timeWaited);
//                    break;
//                } else {
//                    timeWaited++;
////                    System.out.println("No input available");
//                }
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }

        try {
            while (true) {
                System.out.print(">");
                String command = br.readLine();
//                br.
                String[] parts = command.split("\\s+");
                if ((parts.length == 1) && (Objects.equals(parts[0], "shutdown"))) {
                    break;
                } else if ((parts.length == 2) && (parts[0].equals("load"))) {
                    try{
                        rm.load(parts[1]);
                        rm.exec();
                    }
                    catch(IncorrectProgramSizeException e){
                        System.out.println("Program size was incorrect");
                    }
                    catch(NotEnoughFreePagesException e){
                        System.out.println("Real machine didn't have enough pages");
                    }
                    catch(ProgramNotFoundException e){
                        System.out.println("Couldn't find program file");
                    }
                    catch(OSException e){
                        System.out.println("OS exception");
                    }
                }
                else if ((parts.length == 2) && (parts[0].equals("debug"))){
                    try{
                        rm.load(parts[1]);
                        rm.debug(br);
                    }
                    catch(IncorrectProgramSizeException e){
                        System.out.println("Program size was incorrect");
                    }
                    catch(NotEnoughFreePagesException e){
                        System.out.println("Real machine didn't have enough pages");
                    }
                    catch(ProgramNotFoundException e){
                        System.out.println("Couldn't find program file");
                    }
                    catch(OSException e){
                        System.out.println("OS exception");
                    }
                }
                else {
                    System.out.println("Unfamiliar command");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}