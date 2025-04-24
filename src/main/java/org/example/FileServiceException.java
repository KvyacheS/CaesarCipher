package org.example;

public class FileServiceException extends Exception {
    public FileServiceException(String message) {
        super(message);
    }

    public FileServiceException(Throwable ex) {
        super(ex);
    }
}
