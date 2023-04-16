import java.util.ArrayList;
import java.util.Collections;

public class Conversion {
    public static Character convertDigitToHexCharacter(int num)
    {
        assert(num < 16);
        if(num < 10)
            return (char) ('0' + num);
        else
            return (char) ('A' + num - 10);
    }

    public static int convertHexCharacterToDigit(char c)
    {
        if(('0' <= c) && (c <= '9'))
            return c - '0';
        else if(('A' <= c) && (c <= 'F'))
            return c - 'A';
        throw new IllegalArgumentException();
    }

    public static int ConvertHexStringToInt(String s)
    {
        int ans = 0;
        for(int i = 0; i < s.length(); i++)
        {
            ans *= 16;
            ans += convertHexCharacterToDigit(s.charAt(i));
        }
        return ans;
    }

    public static int ConvertHexStringToInt(Character [] s)
    {
        int ans = 0;
        for(int i = 0; i < s.length; i++)
        {
            ans *= 16;
            ans += convertHexCharacterToDigit(s[i]);
        }
        return ans;
    }

    public static Character[] ConvertIntToHexCharacterArray(int x)
    {
        ArrayList<Character> characters = new ArrayList<>();

        while(x != 0){
            characters.add(Conversion.convertDigitToHexCharacter(x % 16));
            x /= 16;
        }
        Collections.reverse(characters);
        if(characters.size() == 0)
            characters.add('0');

        return characters.toArray(new Character[0]);
    }



}
