import Constants.Constants;

public class FileHandler {
    int fileNumber;
    int wordPoz;
    int bytePoz;
    boolean hasWritten;

    int bytesFromFileStart;
    FileHandler(int fileNumber, int wordPoz){
        this.fileNumber = fileNumber;
        this.wordPoz = wordPoz;
        this.bytePoz = 0;
        this.hasWritten = false;
        this.bytesFromFileStart = 18;
    }

    void addBytes(int byteCount){
        bytePoz += byteCount;
        wordPoz += (bytePoz / 6);
        bytePoz %= 6;

        bytesFromFileStart += byteCount;
    }

    public int fileStartPoz(){
        assert(bytePoz % Constants.WordLengthInBytes == bytesFromFileStart % Constants.WordLengthInBytes);
        return wordPoz - (bytesFromFileStart / Constants.WordLengthInBytes);
    }
}
