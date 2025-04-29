package org.example;

import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
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

public class FileService {

    public FileService(CryptingService cryptingService) {
        this.cryptingService = cryptingService;
    }

    public CryptingService cryptingService;

    private final int BUFF_SIZE_INP = 256;

    public void encryptFile(Path input, Path output) throws FileServiceException {
        cryptFile(input, output, CryptOperation.ENCRYPT);
    }

    public void decryptFile(Path input, Path output) throws FileServiceException {
        cryptFile(input, output, CryptOperation.DECRYPT);
    }

    /**
     * Метод шифрования файла
     * @param input путь до исходного файла
     * @param output путь до выходного файла(
     * @param operation типа операции {ENCRYPT,DECRYPT}
     * @throws FileServiceException
     */
    private void cryptFile(Path input, Path output, CryptOperation operation) throws FileServiceException {
        CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder();
        try (ReadableByteChannel rbc = Files.newByteChannel(input, Set.of(READ)); WritableByteChannel wbc = Files.newByteChannel(output, EnumSet.of(WRITE, TRUNCATE_EXISTING, CREATE));) {
            ByteBuffer readBuffer = ByteBuffer.allocate(BUFF_SIZE_INP);
            CharBuffer charBuffer = CharBuffer.allocate(BUFF_SIZE_INP);
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
                char[] crypted;

                switch (operation) {
                    case DECRYPT:
                        crypted = cryptingService.encrypt(charBuffer.array(), charBuffer.length());
                        break;
                    case ENCRYPT:
                        crypted = cryptingService.decrypt(charBuffer.array(), charBuffer.length());
                        break;
                    default:
                        throw new FileServiceException("UNSUPPORTED METHOD");
                }
                //Поскольку в UTF-8 количество байт выделенных на один закодированный символ может различаться
                // делаем запас 4 (т.е) если было например 256 символов, которые весили 1 байт, преобразовались к
                //256 символам размером 4 байта
                ByteBuffer writeBuffer = ByteBuffer.allocate(BUFF_SIZE_INP * 4);
                writeBuffer.put(Charset.defaultCharset().encode(CharBuffer.wrap(crypted)));
                writeBuffer.flip();
                wbc.write(writeBuffer);
            }
        } catch (IOException | BufferOverflowException | CryptingException e) {
            throw new FileServiceException(e);
        }
    }

    /**
     * Метод сравнения файлов по содержимому
     *
     * @param first  Путь к первому файлу
     * @param second путь ко веторому файлу
     * @return true, если файлы равны и false если не равны
     */
    public boolean compareFiles(Path first, Path second) {
        if (first == second) {
            return true;
        }
        try (FileChannel fileChannelFirst = FileChannel.open(first);
             FileChannel fileChannelSecond = FileChannel.open(second)) {
            if (fileChannelFirst.size() != fileChannelSecond.size()) {
                return false;
            }
            ByteBuffer readBufferFirst = ByteBuffer.allocate(BUFF_SIZE_INP);
            ByteBuffer readBufferSecond = ByteBuffer.allocate(BUFF_SIZE_INP);
            while (fileChannelFirst.read(readBufferFirst) > -1 && fileChannelSecond.read(readBufferSecond) > -1) {
                readBufferFirst.flip();
                readBufferSecond.flip();
                if (!readBufferFirst.equals(readBufferSecond)) {
                    return false;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException();
        }
        return true;
    }
}