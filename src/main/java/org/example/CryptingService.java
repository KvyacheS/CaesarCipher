package org.example;

public class CryptingService {

    public CryptingService (char[] alphabet,int shift, boolean skipMissedAlphabet) {
        this.alphabet = alphabet;
        this.shift = shift % alphabet.length;//смысла нет
        this.skipMissedAlphabet = skipMissedAlphabet;
    }

    private static final char[] SYSCHARS = {'\n', '\r'};

    private final char[] alphabet;

    private int shift;

    private final boolean skipMissedAlphabet;

    public void setShift(int shift) {
        this.shift = shift;
    }

    public char[] encrypt(char[] inputChars, int effectiveLength) throws CryptingException {
        return crypt(inputChars, this.shift, effectiveLength);
    }

    public char[] encrypt(char[] inputChars) throws CryptingException {
        return crypt(inputChars, shift, inputChars.length);
    }

    public char[] decrypt(char[] inputChars, int effectiveLength) throws CryptingException {
        return crypt(inputChars, -shift, effectiveLength);
    }

    public char[] decrypt(char[] inputChars) throws CryptingException {
        return crypt(inputChars, -shift, inputChars.length);
    }

    private char[] crypt(char[] inputChars, int shift, int effectiveLength) throws CryptingException {
        char[] result = new char[effectiveLength];
        for (int idx = 0; idx < effectiveLength; idx++) {
            if (isSysChar(inputChars[idx])) {
                result[idx] = inputChars[idx];
                continue;
            }
            int charIdx = findAlphaBetPos(inputChars[idx], alphabet);
            if (charIdx == -1) {
                if (!skipMissedAlphabet) {
                    throw new CryptingException(String.format("Symbol %s not exist in alphabet", inputChars[idx]));
                }
                result[idx] = inputChars[idx];
                continue;
            }
            int newIdx = (alphabet.length + charIdx + shift) % alphabet.length;
            result[idx] = alphabet[newIdx];
        }
        return result;
    }

    public int findAlphaBetPos(char inputChar, char[] alphabet) {
        int pos = 0;
        while (pos < alphabet.length && alphabet[pos] != inputChar) {
            pos++;
        }
        return pos < alphabet.length ? pos : -1;
    }

    public boolean isSysChar(char ch) {
        boolean result = false;
        int idx = 0;
        while (!result && idx < SYSCHARS.length) {
            if (SYSCHARS[idx] == ch) {
                result = true;
            }
            idx++;
        }
        return result;
    }
}
