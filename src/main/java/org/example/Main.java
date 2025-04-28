package org.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;


public class Main {

    public static final char[] ALPHABET_EN = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O',
            'P', 'Q', 'R', 'S', 'T', 'U', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
            'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', ' ', '.', ',', '!', '?'};

    public static final char[] ALPHABET_RU = {'А', 'Б', 'В', 'Г', 'Д', 'Е', 'Ё', 'Ж', 'З', 'И', 'Й', 'К', 'Л', 'М', 'Н',
            'О', 'П', 'Р', 'С', 'Т', 'У', 'Ф', 'Х', 'Ц', 'Ч', 'Ш', 'Щ', 'Ъ', 'Ы', 'Ь', 'Э', 'Ю', 'Я', 'а', 'б', 'в', 'г',
            'д', 'е', 'ё', 'ж', 'з', 'и', 'й', 'к', 'л', 'м', 'н', 'о', 'п', 'р', 'с', 'т', 'у', 'ф', 'х', 'ц', 'ч', 'ш',
            'щ', 'ъ', 'ы', 'ь', 'э', 'ю', 'я', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ' ', '.', ',', '!', '?', '«', '»', '—', '-', '"', '\'', ':',};


    public static String[] mainMenu =
            {"*************************************",
                    "*",
                    "1. Encrypt file",
                    "2. Decrypt file",
                    "3. Brute force file",
                    "4. Exit",
                    "*",
                    "************************************"};

    public static final String BRUTE_PREFIX = "\\temp\\brute_temp.txt";

    public static final String ENCRYPT_PREFIX = "encrypted_%s";

    public static final String DECRYPT_PREFIX = "decrypted_%s.txt";

    public static CryptingService cryptingService;

    public static FileService fileService;

    public static void main(String[] args) throws IOException {
        cryptingService = new CryptingService(ALPHABET_RU, 0, true);//инициализируем первично
        fileService = new FileService(cryptingService);
        int command = 0;
        printMainMenu();
        while (notCommandNotInRange(command = getCommand())) {
            System.out.println("try again");
        }

        switch (command) {
            case 1:
                encryptSingleFile();
                break;
            case 2:
                decryptSingleFile();
                break;
            case 3:
                bruteForce();
                break;
            case 4:
            default:
                return;
        }

    }

    public static boolean notCommandNotInRange(int number) {
        return !(number < 1 || number > 4);
    }

    public static int getCommand() {
        try (Scanner scanner = new Scanner(System.in)) {
            return scanner.nextInt();
        }
    }

    public static void encryptSingleFile() {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Enter file to encrypt:");
            String pathStr = scanner.nextLine();
            Path pathFrom = Path.of(pathStr);
            if (Files.notExists(pathFrom)) {
                System.out.println("file not exists");
                return;
            }
            System.out.println("Enter encryption shift:");
            int shift = scanner.nextInt();
            Path pathTo = Path.of(pathFrom.getParent().toString(), String.format(ENCRYPT_PREFIX, pathFrom.getFileName()));
            cryptingService.setShift(shift);
            try {
                fileService.encryptFile(pathFrom, pathTo);
            } catch (FileServiceException e) {
                System.out.println("File service error encountered");
                throw new RuntimeException(e);
            }
        }
    }

    public static void decryptSingleFile() {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Enter file to decrypt:");
            String pathStr = scanner.nextLine();
            Path pathFrom = Path.of(pathStr);
            if (Files.notExists(pathFrom)) {
                System.out.println("file not exists");
                return;
            }
            System.out.println("Enter encryption shift:");
            int shift = scanner.nextInt();
            Path pathTo = Path.of(pathFrom.getParent().toString(), String.format(DECRYPT_PREFIX, pathFrom.getFileName()));
            cryptingService.setShift(shift);
            try {
                fileService.decryptFile(pathFrom, pathTo);
            } catch (FileServiceException e) {
                System.out.println("File service error encountered");
                throw new RuntimeException(e);
            }
        }
    }

    public static void bruteForce() {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Enter file to brute:");
            String bruteFilePathStr = scanner.nextLine();
            Path brutePath = Path.of(bruteFilePathStr);
            if (Files.notExists(brutePath)) {
                System.out.println("file not exists");
                return;
            }

            String referenceFilePathStr = scanner.nextLine();
            Path referencePath = Path.of(referenceFilePathStr);
            if (Files.notExists(referencePath)) {
                System.out.println("file not exists");
                return;
            }
            try {
                if (Files.isSameFile(brutePath, referencePath)) {
                    System.out.println("it is Same files!");
                    return;
                }
            } catch (IOException e) {
                System.out.println("Error during filecheck");
                return;
            }
            for (int i = 0; i < ALPHABET_RU.length; i++) {
                Path pathTo = Path.of(brutePath.getParent().toString(), BRUTE_PREFIX);
                try {
                    fileService.decryptFile(brutePath, pathTo);
                    if (fileService.compareFiles(brutePath, referencePath)) {
                        System.out.printf("Shift(key is %d) ", i);
                        Files.delete(brutePath);
                        return;
                    }
                } catch (FileServiceException e) {
                    System.out.println("File service error encountered");
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException();
                }
            }
        }
    }

    private static void printMainMenu() {
        for (int idx = 0; idx < mainMenu.length; idx++) {
            System.out.println(mainMenu[idx]);
        }
    }

}