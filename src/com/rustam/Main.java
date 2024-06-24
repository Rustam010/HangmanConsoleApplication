package com.rustam;

// Учтены повторяющиеся буквы
// Учтен ввод только русских букв
// Учтено, если игрок вводит пустую строку
// Счетчик побед и поражений игрока
//


import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;

public class Main {
    public static int winCount = 0;
    public static int loseCount = 0;
    public static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws FileNotFoundException {

        startGame();
    }


    public static void startGame() {
        System.out.println("===================");
        System.out.println("  Игра \"Виселица\"");
        System.out.println("=================== \nНачать игру\n Да (1) | Нет (2)");
        StringBuilder playerChoise = new StringBuilder(scanner.nextLine());

        while (!(playerChoise.toString().equals("1") || playerChoise.toString().equals("2"))) {
            System.out.println("Введите цифру 1 - для начала игры, 2 - для выхода");
            playerChoise.setLength(0);
            playerChoise.append(scanner.nextLine());
        }

        if (playerChoise.toString().equals("1")) {
            String secretWord = createWord();
            ArrayList<Character> words = startGameLoop(secretWord);
        } else {
            System.out.println("До свидания!");
        }
    }

    public static String createWord() {  //создание секретного слова чтением из файла
        ArrayList<String> wordsList = new ArrayList<>();
        try (Scanner scanner2 = new Scanner(new File("src\\com\\rustam\\secretWords.txt"))) {
            while (scanner2.hasNext()) {
                wordsList.add(scanner2.nextLine());
            }
        } catch (FileNotFoundException e) {
            System.out.println("Файл не найден");
            throw new RuntimeException(e);
        }

        String secretWord = wordsList.get(new Random().nextInt(wordsList.size())); // секретное слово = случайное слово из списка wordsList

        ArrayList<Character> words2 = new ArrayList<>();
        for (int i = 0; i < secretWord.length(); i++) {
            words2.add('_');
        }
        printHangman(6);
        System.out.print("Слово состоит из " + secretWord.length() + " букв: ");
        System.out.println(words2);

        return secretWord;
    }

//    public static String createWord() {    // создание секретного слова сканером, если игрока два (НЕ ИСПОЛЬЗУЮ)
//        System.out.println("Игрок 1 загадывает слово:");
//        String secretWord = scanner.nextLine();
//        ArrayList<Character> words = new ArrayList<>();
//        for (int i = 0; i < secretWord.length(); i++) {
//            words.add('_');
//        }
//        System.out.print("Слово состоит из " + secretWord.length() + " букв: ");
//        System.out.println(words);
//        return secretWord;
//    }

    public static ArrayList<Character> startGameLoop(String secretWord) {
        int attemptCount = 6;

        HashSet<Character> usedWords = new HashSet<>(); // список для хранения использованных букв
        ArrayList<Character> words = new ArrayList<>(); // список, который заполняется прочерками, которые меняются на отгаданную букву
        for (int i = 0; i < secretWord.length(); i++) {
            words.add('_');
        }

        while (attemptCount > 0) {   // ОСНОВНОЙ ЦИКЛ ИГРЫ

            if (attemptCount == 5 || attemptCount == 6) {
                System.out.println("У вас осталось " + attemptCount + " попыток");
            } else if (attemptCount >= 2 && attemptCount <= 4) {
                System.out.println("У вас осталось " + attemptCount + " попытки");
            } else {
                System.out.println("У вас осталось " + attemptCount + " попытка");
            }


            System.out.println("\nВведите букву");

            StringBuilder attemptChar = new StringBuilder(scanner.nextLine());
            while (!checkWord(attemptChar.toString())) {
                attemptChar.setLength(0);
                attemptChar.append(scanner.nextLine());
            }

            System.out.println();
            for (int i = 0; i < secretWord.length(); i++) {
                String tempChar = Character.toString(secretWord.charAt(i));
                if (tempChar.equalsIgnoreCase(attemptChar.toString())) {                //проверка на угадывание
                    System.out.println("Вы угадали букву '" + attemptChar.charAt(0) + "'");
                    words.set(i, attemptChar.charAt(0));
                }
            }
            if (!(words.contains(attemptChar.charAt(0)))) {
                System.out.println("Упс! Буквы '" + attemptChar.charAt(0) + "' нет");
                attemptCount--;
            }
            if (usedWords.contains(attemptChar.charAt(0)) && !(words.contains(attemptChar.charAt(0)))) {
                attemptCount++;
            }
            if (usedWords.contains(attemptChar.charAt(0))) {
                System.out.println("Вы уже вводили букву:" + attemptChar.charAt(0) + "\nПопробуйте другую букву\n");
                continue;
            }
            printHangman(attemptCount);
            usedWords.add(attemptChar.charAt(0));
            System.out.println(words);
            System.out.println("Введенные ранее буквы: " + usedWords);

            if (!words.contains('_')) {   //выход из цикла если не осталось прочерков
                break;
            }
        }
        boolean result = checkGameResult(words, secretWord);
        return words;
    }

    public static boolean checkWord(String attemptChar) { //проверка введенной буквы
        HashSet<String> russianChars = new HashSet<>();
        try (Scanner scanner2 = new Scanner(new File("src\\com\\rustam\\russianChars.txt"))) {
            while (scanner2.hasNext()) {
                russianChars.add(scanner2.nextLine());
            }
        } catch (FileNotFoundException e) {
            System.out.println("Файл не найден");
            throw new RuntimeException(e);
        }

        if (attemptChar.isEmpty()) {
            System.out.println("Вы не ввели букву");
            return false;
        } else if (attemptChar.length() > 1) {
            System.out.println("Введите только одну букву");
            return false;
        } else if (!russianChars.contains(Character.toString(attemptChar.charAt(0)))) {
            System.out.println("Вводите только буквы русского алфавита");
            return false;
        }
        return true;
    }

    public static boolean checkGameResult(ArrayList<Character> words, String secretWord) {
        if (words.contains('_')) { // если в слове остались прочерки, то проигрышь
            printHangman(0);
            System.out.println("\nВЫ ПРОИГРАЛИ! Это было слово: " + secretWord);
            loseCount++;
            System.out.println("Количество побед = " + winCount);
            System.out.println("Количество проигрышей = " + loseCount);
            System.out.println("Хотите сыграть еще?");
            System.out.println();

            startGame();
            return false;
        } else {
            System.out.println("\nУРА! ВЫ ПОБЕДИЛИ! Вы отгадали слово: " + secretWord);
            winCount++;
            System.out.println("Количество побед = " + winCount);
            System.out.println("Количество проигрышей = " + loseCount);
            System.out.println("Хотите сыграть еще?");
            System.out.println();

            startGame();
            return true;
        }
    }

    public static void printHangman(int attemptCount) {  // реализация рисунка виселицы в консоле
        if (attemptCount == 6) {
            System.out.println(" _________");
            for (int i = 0; i < 1; i++) {
                System.out.println("|        |");
            }
            for (int i = 0; i < 5; i++) {
                System.out.println("|    ");
            }
            System.out.println("==========");
        } else if (attemptCount == 5) {
            System.out.println(" _________");
            for (int i = 0; i < 1; i++) {
                System.out.println("|        |");
            }
            System.out.println("|        0");
            for (int i = 0; i < 4; i++) {
                System.out.println("|    ");
            }
            System.out.println("==========");
        } else if (attemptCount == 4) {
            System.out.println(" _________");
            for (int i = 0; i < 1; i++) {
                System.out.println("|        |");
            }
            System.out.println("|        0");
            System.out.println("|        |");

            for (int i = 0; i < 3; i++) {
                System.out.println("|    ");
            }
            System.out.println("==========");
        } else if (attemptCount == 3) {
            System.out.println(" _________");
            for (int i = 0; i < 1; i++) {
                System.out.println("|        |");
            }
            System.out.println("|        0");
            System.out.println("|       /|");

            for (int i = 0; i < 3; i++) {
                System.out.println("|    ");
            }
            System.out.println("==========");
        } else if (attemptCount == 2) {
            System.out.println(" _________");
            for (int i = 0; i < 1; i++) {
                System.out.println("|        |");
            }
            System.out.println("|        0");
            System.out.println("|       /|\\");

            for (int i = 0; i < 3; i++) {
                System.out.println("|    ");
            }
            System.out.println("==========");
        } else if (attemptCount == 1) {
            System.out.println(" _________");
            for (int i = 0; i < 1; i++) {
                System.out.println("|        |");
            }
            System.out.println("|        0");
            System.out.println("|       /|\\");
            System.out.println("|       /");

            for (int i = 0; i < 2; i++) {
                System.out.println("|    ");
            }
            System.out.println("==========");
        } else if (attemptCount == 0) {
            System.out.println(" _________");
            for (int i = 0; i < 1; i++) {
                System.out.println("|        |");
            }
            System.out.println("|        0");
            System.out.println("|       /|\\");
            System.out.println("|       / \\");

            for (int i = 0; i < 2; i++) {
                System.out.println("|    ");
            }
            System.out.println("==========");
        }
    } // реализация рисунка виселицы в консоле

    String getStringFromWords(ArrayList<Character> list) { // возвращение слова из списка букв
        StringBuilder builder = new StringBuilder();
        for (Character ch : list) {
            builder.append(ch);
        }
        return builder.toString();
    }
}


