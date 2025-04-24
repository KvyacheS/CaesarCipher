package org.example;

import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumSet;
import java.util.Set;

import static java.nio.file.StandardOpenOption.*;


public class Main {

    public static final char[] ALPHABET_EN = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O',
            'P', 'Q', 'R', 'S', 'T', 'U', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
            'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', ' ', '.', ',', '!', '?'};

    public static final char[] ALPHABET_RU = {'А', 'Б', 'В', 'Г', 'Д', 'Е', 'Ё', 'Ж', 'З', 'И', 'Й', 'К', 'Л', 'М', 'Н',
            'О', 'П', 'Р', 'С', 'Т', 'У', 'Ф', 'Х', 'Ц', 'Ч', 'Ш', 'Щ', 'Ъ', 'Ы', 'Ь', 'Э', 'Ю', 'Я', 'а', 'б', 'в', 'г',
            'д', 'е', 'ё', 'ж', 'з', 'и', 'й', 'к', 'л', 'м', 'н', 'о', 'п', 'р', 'с', 'т', 'у', 'ф', 'х', 'ц', 'ч', 'ш',
            'щ', 'ъ', 'ы', 'ь', 'э', 'ю', 'я', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ' ', '.', ',', '!', '?', '«', '»', '—', '-'};


    public static final char[] SYSCHARS = {'\n', '\r'};

    public static final int BUFF_SIZE_INP = 256;

    public static final int BUFF_SIZE_OUTPUT = BUFF_SIZE_INP * 2;//После перекодирования

    public static CryptingService cryptingService;


    public static void main(String[] args) throws IOException {
        String inputStr = "?";
        char[] chars = inputStr.toCharArray();
        //

        cryptingService = new CryptingService(ALPHABET_RU,1,true);
        String pathStr = "C:\\Users\\KvyacheS\\Desktop\\cipherTest";
        Path pathInp = Path.of(pathStr + "//input.txt");
        Path pathOut = Path.of(pathStr + "//output.txt");

        CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder();
        try (ReadableByteChannel rbc = Files.newByteChannel(pathInp, Set.of(READ)); WritableByteChannel wbc = Files.newByteChannel(pathOut, EnumSet.of(WRITE, TRUNCATE_EXISTING, CREATE));) {
            ByteBuffer readBuffer = ByteBuffer.allocate(256);
            CharBuffer charBuffer = CharBuffer.allocate(256);
            while (rbc.read(readBuffer) > -1) {
                readBuffer.flip();
                charBuffer.clear();

                decoder.decode(readBuffer, charBuffer, false);
                charBuffer.flip();
                if (readBuffer.hasRemaining()) {
                    readBuffer.compact();
                } else {
                    readBuffer.clear();
                }
                char[] encrypted = cryptingService.encrypt(charBuffer.array(),charBuffer.length());
                ByteBuffer writeBuffer = ByteBuffer.allocate(512);
                writeBuffer.put(Charset.defaultCharset().encode(CharBuffer.wrap(encrypted)));
                writeBuffer.flip();
                wbc.write(writeBuffer);
            }
        } catch (IOException | BufferOverflowException | CryptingException e) {
            System.out.println(e);
        }
    }
}