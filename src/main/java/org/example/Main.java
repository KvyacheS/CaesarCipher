package org.example;

public class Main {

    public static final char[] alphabet = {'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S'
    ,'T','U','W','X','Y','Z','a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w',
     'x','y','z','0','1','2','3','4','5','6','7','8','9',' ','.',',','!','?'};




    public static void main(String[] args) {
        //String inputStr = "Hello, World!";
        String inputStr = "?";
        char[] chars = inputStr.toCharArray();
        int  shift = 3;

        System.out.println(decrypt(encrypt(chars,shift,alphabet),shift,alphabet));
    }

    public static char[] encrypt(char[] inputChars,int shift, char[] alphabet) {
        char[] result = new char[inputChars.length];
        for(int idx= 0; idx <inputChars.length; idx++) {
            int charIdx = findAlphaBetPos(inputChars[idx],alphabet);
            int newIdx = charIdx + shift < alphabet.length ? charIdx + shift : (charIdx + shift ) - alphabet.length;
            result[idx] = alphabet[newIdx];
        }
        return result;
    }


    public static int findAlphaBetPos(char inputChar, char[] alphabet) {
        int pos = 0;
        while (pos < alphabet.length &&  alphabet[pos] != inputChar) {
            pos++;
        }
        return pos < alphabet.length ? pos : -1;
    }

    public static char[] decrypt(char[] inputChars,int shift, char[] alphabet) {
        char[] result = new char[inputChars.length];
        for(int idx= 0; idx <inputChars.length; idx++) {
            int charIdx = findAlphaBetPos(inputChars[idx],alphabet);
            int newIdx = charIdx - shift >=  0 ? charIdx - shift : alphabet.length  + (charIdx -shift);
            result[idx] = alphabet[newIdx];
        }
        return result;
    }

}