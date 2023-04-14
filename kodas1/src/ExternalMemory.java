import java.io.File;
import java.io.IOException;

public class ExternalMemory {
    final static String fileName = "hdd.txt";

    private void createFile()
    {
        File file = new File(fileName);
        if(!file.exists())
        {
            try {
                boolean successfullyCreated = file.createNewFile();
                if(!successfullyCreated)
                    throw new RuntimeException();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    ExternalMemory()
    {
        createFile();
    }
    Character getByte(int num)
    {
//        TODO: implementacija
        return 'c';
    }

    void setByte(int num, Character value)
    {
        throw new UnsupportedOperationException();
    }

}
