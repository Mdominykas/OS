import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello to this OS");
        RealMachine rm = new RealMachine();
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        try {
            while(true) {
                System.out.print(">");
                String command = br.readLine();
                String[] parts = command.split("\\s+");
                if((parts.length == 1) && (Objects.equals(parts[0], "shutdown"))){
                    break;
                }
                else if ((parts.length == 2) && (Objects.equals(parts[0], "load"))){
                    rm.load(parts[1]);
                    rm.exec();
                }
                else{
                    System.out.println("Unfamiliar command");
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}