import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;

public class UserInput {
    Deque<Character> buffer;

    UserInput(){
        buffer = new ArrayDeque<Character>();
    }

    public Character[] getCharacters(int N){
        Character[] characters = new Character[N];
        int cur = 0;
        while(cur < N){
            if(!buffer.isEmpty()){
                characters[cur] = buffer.getFirst();
                buffer.removeFirst();
            }
            else {
                readUserLine();
            }
            cur++;
        }
        return characters;
    }

    public boolean readUntilEndOfLineButNotMoreThanN(int N, ArrayList<Character> result){
        int cur = 0;
        while(cur < N){
            if((!buffer.isEmpty()) && (buffer.getFirst() == '$'))
            {
                buffer.removeFirst();
                return true;
            }
            else if(!buffer.isEmpty()){
                result.add(buffer.getFirst());
                buffer.removeFirst();
            }
            else {
                readUserLine();
            }
            cur++;
        }
        return false;
    }

    public void readUserLine(){
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String line = null;
        try {
            line = br.readLine();
            for(int i = 0; i < line.length(); i++)
                buffer.add(line.charAt(i));
            buffer.add('$');
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int readNumber(){
        if(buffer.isEmpty()){
            readUserLine();
        }
        int ans = 0;
        while(!buffer.isEmpty()){
            if(buffer.getFirst() == '$'){
                buffer.removeFirst();
            }
            else if (('0' <= buffer.getFirst()) && (buffer.getFirst() <= '9')){
                ans *= 10;
                ans += buffer.getFirst() - '0';
                buffer.removeFirst();
            }
            else{
                break;
            }
        }
        return ans;
    }

    public int bufferLength(){
        return buffer.size();
    }
}
