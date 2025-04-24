package org.example;

import java.io.IOException;
import java.nio.file.Path;


public class Main {

    public static final char[] ALPHABET_EN = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O',
            'P', 'Q', 'R', 'S', 'T', 'U', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
            'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', ' ', '.', ',', '!', '?'};

    public static final char[] ALPHABET_RU = {'А', 'Б', 'В', 'Г', 'Д', 'Е', 'Ё', 'Ж', 'З', 'И', 'Й', 'К', 'Л', 'М', 'Н',
            'О', 'П', 'Р', 'С', 'Т', 'У', 'Ф', 'Х', 'Ц', 'Ч', 'Ш', 'Щ', 'Ъ', 'Ы', 'Ь', 'Э', 'Ю', 'Я', 'а', 'б', 'в', 'г',
            'д', 'е', 'ё', 'ж', 'з', 'и', 'й', 'к', 'л', 'м', 'н', 'о', 'п', 'р', 'с', 'т', 'у', 'ф', 'х', 'ц', 'ч', 'ш',
            'щ', 'ъ', 'ы', 'ь', 'э', 'ю', 'я', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ' ', '.', ',', '!', '?', '«', '»', '—', '-'};


    public static final int BUFF_SIZE_INP = 256;

    public static final int BUFF_SIZE_OUTPUT = BUFF_SIZE_INP * 2;//После перекодирования

    public static CryptingService cryptingService;

    public static FileService fileService;

    public static void main(String[] args) throws IOException {
        String inputStr = "?";
        char[] chars = inputStr.toCharArray();
        //

        cryptingService = new CryptingService(ALPHABET_RU, 1, true);
        String pathStr = "C:\\Users\\KvyacheS\\Desktop\\cipherTest";
        Path pathInp = Path.of(pathStr + "\\input.txt");
        Path pathOut = Path.of(pathStr + "\\output.txt");
        fileService = new FileService(cryptingService);
        try {
            fileService.encrypt(pathInp, pathOut);
        } catch (FileServiceException e) {
            System.out.println(e);
        }
    }
}