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

public class FileService {

    public FileService(CryptingService cryptingService) {
        this.cryptingService = cryptingService;
    }

    public CryptingService cryptingService;

    private final int BUFF_SIZE_INP = 256;

    private final int BUFF_SIZE_OUTPUT = BUFF_SIZE_INP * 2;

    public void encryptFile(Path input, Path output) throws FileServiceException {
        cryptFile(input, output, CryptOperation.ENCRYPT);
    }

    public void decryptFile(Path input, Path output) throws FileServiceException {
        cryptFile(input, output, CryptOperation.DECRYPT);
    }

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
                ByteBuffer writeBuffer = ByteBuffer.allocate(BUFF_SIZE_OUTPUT);
                writeBuffer.put(Charset.defaultCharset().encode(CharBuffer.wrap(crypted)));
                writeBuffer.flip();
                wbc.write(writeBuffer);
            }
        } catch (IOException | BufferOverflowException | CryptingException e) {
            throw new FileServiceException(e);
        }
    }

}