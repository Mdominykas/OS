import Constants.Constants;

import java.io.*;

public class ExternalMemory {
    final static String fileName = "hdd.txt";

    private void createFile() {
        File file = new File(fileName);
        if (!file.exists()) {
            try {
                boolean successfullyCreated = file.createNewFile();
                if (!successfullyCreated)
                    throw new RuntimeException();
                BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
                for (int i = 0; i < Constants.externalMemoryLengthInWords; i++) {
                    writer.write("000000");
                    writer.newLine();
                }
                writer.close(); // Close the writer
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    ExternalMemory() {
        createFile();
    }

    Character getByte(int num) {
        char ch = '\0';
        String line;
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            while(num >= Constants.WordLength)
            {
                br.readLine();
                num -= Constants.WordLength;
            }
            line = br.readLine();
            ch = line.charAt(num);
            br.close();

            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ch;
    }

    Character[] getWord(int num)
    {
        Character[] characters = new Character[Constants.WordLength];
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            while(num > 0)
            {
                br.readLine();
                num--;
            }
            String line = br.readLine();
            for(int i = 0; i < Constants.WordLength; i++)
            {
                characters[i] = line.charAt(i);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return characters;
    }


    void setByte(int num, Character value) {
        String line;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            while(num >= Constants.WordLength)
            {
                line = br.readLine();
                num -= Constants.WordLength;
                stringBuilder.append(line);
                stringBuilder.append(System.lineSeparator());
            }
            line = br.readLine();
            for(int i = 0; i < num; i++)
                stringBuilder.append(line.charAt(i));
            stringBuilder.append(value);
            for(int i = num + 1; i < Constants.WordLength; i++)
                stringBuilder.append(line.charAt(i));
            stringBuilder.append(System.lineSeparator());
            while((line = br.readLine()) != null)
            {
                stringBuilder.append(line);
                stringBuilder.append(System.lineSeparator());
            }
            br.close();

            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
            writer.write(stringBuilder.toString());
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
