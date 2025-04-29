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

    public static Scanner scanner = new Scanner(System.in);

    public static String[] mainMenu =
            {"*************************************",
                    "*",
                    "1. Encrypt file",
                    "2. Decrypt file",
                    "3. Brute force file",
                    "4. Exit",
                    "*",
                    "************************************"};

    public static final String BRUTE_PREFIX = "brute_temp.txt";

    public static final String ENCRYPT_PREFIX = "encrypted_%s";

    public static final String DECRYPT_PREFIX = "decrypted_%s.txt";

    public static CryptingService cryptingService;

    public static FileService fileService;

    public static void main(String[] args) throws IOException {
        try {
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
        } finally {
            scanner.close();
        }
    }

    public static boolean notCommandNotInRange(int number) {
        return number < 1 || number > 4;
    }

    public static int getCommand() {
        return scanner.nextInt();
    }

    public static void encryptSingleFile() {
        System.out.println("Enter file to encrypt:");
        Path filePathToEncrypt = getFilePath();
        if (filePathToEncrypt == null) {
            return;
        }
        System.out.println("Enter encryption shift:");
        int shift = scanner.nextInt();
        Path pathTo = Path.of(filePathToEncrypt.getParent().toString(), String.format(ENCRYPT_PREFIX, filePathToEncrypt.getFileName()));
        cryptingService.setShift(shift);
        try {
            fileService.encryptFile(filePathToEncrypt, pathTo);
        } catch (FileServiceException e) {
            System.out.println("File service error encountered");
            throw new RuntimeException(e);
        }
    }

    public static void decryptSingleFile() {
        System.out.println("Enter file to decrypt:");
        Path filePathToDecrypt = getFilePath();
        if (filePathToDecrypt == null) {
            return;
        }
        System.out.println("Enter encryption shift:");
        int shift = scanner.nextInt();
        Path pathTo = Path.of(filePathToDecrypt.getParent().toString(), String.format(DECRYPT_PREFIX, filePathToDecrypt.getFileName()));
        cryptingService.setShift(shift);
        try {
            fileService.decryptFile(filePathToDecrypt, pathTo);
        } catch (FileServiceException e) {
            System.out.println("File service error encountered");
            throw new RuntimeException(e);
        }
    }

    /**
     * Метод  подбора ключа(сдвига) для зашифрованного файла
     */
    public static void bruteForce() {
        System.out.println("Enter file to brute:");
        Path brutePath = getFilePath();
        if (brutePath == null) {
            return;
        }
        System.out.println("Enter file path to reference:");
        Path referencePath = getFilePath();
        if (referencePath == null) {
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
        int idx = -1;//На случай если передадут незашифрованный файл
        boolean shiftFound = false;
        while (idx < ALPHABET_RU.length && !shiftFound) {
            idx++;
            cryptingService.setShift(idx);
            Path pathTo = Path.of(brutePath.getParent().toString(), BRUTE_PREFIX);
            try {
                fileService.decryptFile(brutePath, pathTo);
                if (fileService.compareFiles(pathTo, referencePath)) {
                    shiftFound = true;
                } else {
                    Files.delete(pathTo);
                }
            } catch (FileServiceException e) {
                System.out.println("File service error encountered");
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException();
            }
        }
        if (shiftFound) {
            System.out.printf("Shift(key is %d) ", idx);
        } else {

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

    /**
     * Валидированный метод чтения пути к файлу из терминала
     * @return Путь к файлу, после чтения из консоли или null, если валидации не пройдены
     */
    private static Path getFilePath() {
        String pathStr;
        //После чтения числа может определять остаток содержимого в терминале как пустую
        // строку и считывать ее, также нужно убедиться что пользователй в принципе что-то ввел
        do {
            pathStr = scanner.nextLine();
        } while (pathStr.isEmpty());
        Path path = Path.of(pathStr);
        if (Files.notExists(path)) {
            System.out.println("file not exists");
            return null;
        }
        if (!Files.isRegularFile(path)) {
            System.out.println("not a file");
            return null;
        }
        return path;
    }

    /**
     * Метод вывод
     */
    private static void printMainMenu() {
        for (String menu : mainMenu) {
            System.out.println(menu);
        }
    }

}