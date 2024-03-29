import Constants.Constants;

import java.util.ArrayList;
import java.util.Arrays;
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
            return c - 'A' + 10;
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

    public static Character[] stringToCharacterArray(String s)
    {
        Character[] characters = new Character[s.length()];
        for(int i = 0; i < s.length(); i++)
            characters[i] = s.charAt(i);
        return characters;
    }

    public static String characterArrayToString(Character[] characters)
    {
        StringBuilder sb = new StringBuilder();
        for(Character ch : characters)
            sb.append(ch);
        return sb.toString();
    }

    public static Character[] convertToWordLengthCharacterArray(int number)
    {
        Character[] ans = new Character[Constants.WordLengthInBytes];
        Arrays.fill(ans, '0');
        Character[] converted = Conversion.ConvertIntToHexCharacterArray(number);
        for(int i = 1; i <= converted.length; i++){
            ans[Constants.WordLengthInBytes - i] = converted[converted.length - i];
        }
        return ans;
    }

    public static Character[] convertIntToDecCharacterArray(int number){
        ArrayList<Character> chars = new ArrayList<>();
        while(number != 0){
            chars.add((char) ('0' + (number % 10)));
            number /= 10;
        }
        if(chars.size() == 0)
            chars.add('0');
        Collections.reverse(chars);
        Character[] ans = new Character[chars.size()];
        for(int i = 0; i < chars.size(); i++)
            ans[i] = chars.get(i);
        return ans;
    }

}
