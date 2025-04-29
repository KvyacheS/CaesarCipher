package org.example;

public class CryptingService {

    public CryptingService(char[] alphabet, int shift, boolean skipMissedAlphabet) {
        this.alphabet = alphabet;
        this.shift = shift % alphabet.length;//Сдвиг не может быть больше  или равен длинне алфавита- результат будет аналогичен
        this.skipMissedAlphabet = skipMissedAlphabet;
    }

    private static final char[] SYSCHARS = {'\n', '\r', '\t', '\b', '\f'};

    private final char[] alphabet;

    private int shift;

    private final boolean skipMissedAlphabet;

    public void setShift(int shift) {
        this.shift = shift % alphabet.length;
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

    /**
     * Шифрующюший-Дешифрующий метод
     * @param inputChars Массив символов, которые  необходимо зашифровать
     * @param shift Сдвиг(ключ) на который  нужно преобразовать переданный массив
     * @param effectiveLength (фактический размер символов, которые нужно шифровать)
     * @return зашифрованный/расшифрованный массив символов  inputChars
     * @throws CryptingException если не выключен пропуск не найденных в алфавите символов, кидает исключение
     * если символа нет в алфавите
     */
    //TODO: Попробовать сделать что-нибудь, чтобы не было необходимости вычислять эффиктивную длинну массива
    private char[] crypt(char[] inputChars, int shift, int effectiveLength) throws CryptingException {
        char[] result = new char[effectiveLength];
        for (int idx = 0; idx < effectiveLength; idx++) {
            if (isSysChar(inputChars[idx])) {
                result[idx] = inputChars[idx];
                continue;
            }
            int charIdx = findAlphaBetPos(inputChars[idx], alphabet);
            if (charIdx == -1) {
                //В случае, если включено жесткое соотвествие алфавиту
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

    /**
     * Поиск символа в алфавите
     * @param inputChar искомый символ
     * @param alphabet алфавит, в котором ведется поиск
     * @return позицю в алфавите или  -1, если символа там нет
     */
    public int findAlphaBetPos(char inputChar, char[] alphabet) {
        int pos = 0;
        while (pos < alphabet.length && alphabet[pos] != inputChar) {
            pos++;
        }
        return pos < alphabet.length ? pos : -1;
    }

    /**
     * Проверка на то, что символ относится к управляющей последовательности
     * @param ch переданный на проверку символ
     * @return true, если символ является управляющим
     */
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
