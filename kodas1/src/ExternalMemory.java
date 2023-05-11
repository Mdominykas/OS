import Constants.Constants;

import java.io.*;

public class ExternalMemory {
    final static String fileName = "hdd.txt";

    ExternalMemory() {
        createHddFile();
        fixFileSystem();
    }

    private void createHddFile() {
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

    private void fixFileSystem() {
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));

            String[] lines = new String[Constants.externalMemoryLengthInWords];
            for (int i = 0; i < Constants.externalMemoryLengthInWords; i++) {
                lines[i] = br.readLine();
                if(lines[i] == null){
                    lines[i] = "000000";
                }
            }
            br.close();

            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
            for (int i = 0; i < Constants.externalMemoryLengthInWords; i++) {
                if (lines[i].length() == Constants.WordLengthInBytes) {
                    writer.write(lines[i]);
                } else if (lines[i].length() > Constants.WordLengthInBytes) {
                    System.out.println("too long line");
                    writer.write(lines[i].substring(0, Constants.WordLengthInBytes));
                } else {
                    System.out.println("too short line");
                    writer.write(lines[i]);
                    for (int j = lines[i].length(); j < Constants.WordLengthInBytes; j++) {
                        writer.write("0");
                    }
                }
                writer.newLine();

            }

            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    Character getByte(int num) {
        char ch = '\0';
        String line;
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            while (num >= Constants.WordLengthInBytes) {
                br.readLine();
                num -= Constants.WordLengthInBytes;
            }
            line = br.readLine();
            ch = line.charAt(num);
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ch;
    }

    Character[] getWord(int num) {
        Character[] characters = new Character[Constants.WordLengthInBytes];
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            while (num > 0) {
                br.readLine();
                num--;
            }
            String line = br.readLine();
            assert (line.length() == Constants.WordLengthInBytes);
            for (int i = 0; i < line.length(); i++) {
                characters[i] = line.charAt(i);
            }
            for (int i = line.length(); i < Constants.WordLengthInBytes; i++) {
                characters[i] = ' ';
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return characters;
    }

    String[] readAllLines() {
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            String[] lines = new String[Constants.externalMemoryLengthInWords];

            for (int i = 0; i < Constants.externalMemoryLengthInWords; i++) {
                lines[i] = br.readLine();
                assert (lines[i] != null);
            }
            br.close();
            return lines;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    void setWordAndShift(int poz, Character[] word) {
        assert((poz >=0 ) &&(poz < Constants.externalMemoryLengthInWords));
        assert(word.length == Constants.WordLengthInBytes);
        try {
            String[] lines = readAllLines();

            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
            for (int i = 0; i < Constants.externalMemoryLengthInWords; i++) {
                if (i < poz) {
                    writer.write(lines[i]);
                } else if (i == poz) {
                    writer.write(Conversion.characterArrayToString(word));
                } else {
                    writer.write(lines[i - 1]);
                }
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    void removeWordAndShift(int poz) {
        assert (poz < Constants.externalMemoryLengthInWords);
        try {
            String[] lines = readAllLines();

            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
            for (int i = 0; i < Constants.externalMemoryLengthInWords; i++) {
                if (i < poz) {
                    writer.write(lines[i]);
                } else if (i == poz) {
                    continue;
                } else {
                    writer.write(lines[i - 1]);
                }
                writer.newLine();
            }
            writer.write("000000");
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    void setByte(int num, Character value) {
        String line;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            while (num >= Constants.WordLengthInBytes) {
                line = br.readLine();
                num -= Constants.WordLengthInBytes;
                stringBuilder.append(line);
                stringBuilder.append(System.lineSeparator());
            }
            line = br.readLine();
            for (int i = 0; i < num; i++)
                stringBuilder.append(line.charAt(i));
            stringBuilder.append(value);
            for (int i = num + 1; i < Constants.WordLengthInBytes; i++)
                stringBuilder.append(line.charAt(i));
            stringBuilder.append(System.lineSeparator());
            while ((line = br.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append(System.lineSeparator());
            }
            br.close();

            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
//            seems totally wrong, so for now I will use assert
            assert (false);
            writer.write(stringBuilder.toString());
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void setWord(int num, Character[] word) {
        assert (false); // not implemented
    }


}
