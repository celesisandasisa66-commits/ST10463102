package com.mycompany.consoleapplication;

import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.text.SimpleDateFormat;
import java.io.FileWriter;
import java.io.IOException;

public class ConsoleApplication {

    // ===================== MESSAGE CLASS =====================

    static class Message {

        String id;
        String hash;
        String recipient;
        String text;
        String status;
        String time;

        public Message(
                String id,
                String hash,
                String recipient,
                String text,
                String status,
                String time) {

            this.id = id;
            this.hash = hash;
            this.recipient = recipient;
            this.text = text;
            this.status = status;
            this.time = time;
        }
    }

    // ===================== STORAGE =====================

    static ArrayList<Message> messageList = new ArrayList<>();

    static int sentCount = 0;
    static int messageLimit = 0;

    static Scanner input = new Scanner(System.in);

    // ===================== CHECK USERNAME =====================

    public static boolean CheckUsername(String username) {

        return username.contains("_")
                && username.length() <= 5;
    }

    // ===================== CHECK PASSWORD =====================

    public static boolean CheckPassword(String password) {

        String regex =
                "^(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$";

        return Pattern.matches(regex, password);
    }

    // ===================== LOGIN =====================

    public static boolean login(
            String enteredUsername,
            String enteredPassword,
            String storedUsername,
            String storedPassword) {

        return enteredUsername.equals(storedUsername)
                && enteredPassword.equals(storedPassword);
    }

    // ===================== VALIDATE NUMBER =====================

    public static String validateNumber(String number) {

        if (number == null) {
            return "Invalid";
        }

        if (!number.startsWith("+27")) {
            return "Invalid: must start with +27";
        }

        if (number.length() != 12) {
            return "Invalid: must be 12 digits (+27XXXXXXXXX)";
        }

        String digits = number.substring(3);

        if (!digits.matches("\\d{9}")) {
            return "Invalid: only digits allowed after +27";
        }

        return "Valid";
    }

    // ===================== CREATE HASH =====================

    public static String createMessageHash(
            String id,
            int number,
            String message) {

        String[] words = message.trim().split(" ");

        String firstWord =
                (words.length > 0)
                ? words[0]
                : "MSG";

        String lastWord =
                (words.length > 1)
                ? words[words.length - 1]
                : firstWord;

        return (
                id.substring(0, 2)
                + ":"
                + number
                + ":"
                + firstWord
                + lastWord
        ).toUpperCase();
    }

    // ===================== SEND MESSAGE =====================

    public static void sendMessage() {

        if (sentCount >= messageLimit) {

            System.out.println(
                    "[WARNING] Message limit reached!");

            return;
        }

        System.out.print(
                "Enter Recipient (+27XXXXXXXXX): ");

        String recipient =
                input.nextLine();

        String validation =
                validateNumber(recipient);

        if (!validation.equals("Valid")) {

            System.out.println(
                    "[ERROR] " + validation);

            return;
        }

        System.out.print(
                "Enter Message (max 250 chars): ");

        String message =
                input.nextLine();

        if (message.length() > 250) {

            System.out.println(
                    "[ERROR] Message too long!");

            return;
        }

        String generatedId =
                String.format(
                        "%010d",
                        (long) (Math.random() * 10000000000L)
                );

        String generatedHash =
                createMessageHash(
                        generatedId,
                        sentCount,
                        message
                );

        String currentTime =
                new SimpleDateFormat("HH:mm:ss")
                        .format(new Date());

        Message newMessage =
                new Message(
                        generatedId,
                        generatedHash,
                        recipient,
                        message,
                        "SENT",
                        currentTime
                );

        messageList.add(newMessage);

        sentCount++;

        System.out.println("\n[SUCCESS] MESSAGE SENT");
        System.out.println("ID   : " + generatedId);
        System.out.println("HASH : " + generatedHash);
    }

    // ===================== SHOW MESSAGES =====================

    public static void showMessages() {
        System.out.println("\n==============================");
        System.out.println("        SHOW MESSAGES");
        System.out.println("==============================");
        System.out.println("Coming Soon");
        System.out.println("==============================\n");
    }

    // ===================== DISCARD MESSAGE =====================

    public static void discardLastMessage() {

        if (messageList.isEmpty()) {

            System.out.println(
                    "[INFO] No messages to discard.");

            return;
        }

        int lastIndex =
                messageList.size() - 1;

        Message removedMessage =
                messageList.remove(lastIndex);

        sentCount--;

        System.out.println(
                "[SYSTEM] Discarding last message...");

        System.out.println(
                "Removed: "
                + removedMessage.text);

        System.out.println(
                "[SUCCESS] Message discarded.");
    }

    // ===================== SAVE FILE =====================

    public static void storeMessage() {

        try {

            FileWriter writer =
                    new FileWriter("messages.json");

            writer.write("[\n");

            for (int i = 0; i < messageList.size(); i++) {

                Message msg =
                        messageList.get(i);

                writer.write("  {\n");

                writer.write(
                        "    \"id\": \"" + msg.id + "\",\n");

                writer.write(
                        "    \"hash\": \"" + msg.hash + "\",\n");

                writer.write(
                        "    \"recipient\": \"" + msg.recipient + "\",\n");

                writer.write(
                        "    \"message\": \"" + msg.text + "\",\n");

                writer.write(
                        "    \"status\": \"" + msg.status + "\",\n");

                writer.write(
                        "    \"time\": \"" + msg.time + "\"\n");

                writer.write("  }");

                if (i < messageList.size() - 1) {
                    writer.write(",");
                }

                writer.write("\n");
            }

            writer.write("]");

            writer.close();

            System.out.println(
                    "\n[SYSTEM] File saved successfully!");

            System.out.println(
                    "[SYSTEM] Total records saved: "
                    + messageList.size());

        } catch (IOException e) {

            System.out.println(
                    "[ERROR] Failed to save file!");
        }
    }

    // ===================== DISPLAY MENU =====================

    public static void displayMenu() {

        System.out.println("\n========== MENU ==========");
        System.out.println("1. Send Message");
        System.out.println("2. Show Messages");
        System.out.println("3. Discard Last Message");
        System.out.println("4. Save Messages");
        System.out.println("5. Quit");
        System.out.println("==========================");
    }

    // ===================== MAIN METHOD =====================

    public static void main(String[] args) {

        System.out.println("=== QUICKCHAT CONSOLE ===");

        // ---------- REGISTRATION ----------

        System.out.print("Create Username: ");

        String savedUsername =
                input.nextLine();

        System.out.print("Create Password: ");

        String savedPassword =
                input.nextLine();

        if (!CheckUsername(savedUsername)
                || !CheckPassword(savedPassword)) {

            System.out.println(
                    "Invalid registration!");

            return;
        }

        // ---------- LOGIN ----------

        boolean loggedIn = false;

        while (!loggedIn) {

            System.out.print("Login Username: ");

            String username =
                    input.nextLine();

            System.out.print("Login Password: ");

            String password =
                    input.nextLine();

            loggedIn =
                    login(
                            username,
                            password,
                            savedUsername,
                            savedPassword
                    );

            if (!loggedIn) {

                System.out.println(
                        "Login failed!");
            }
        }

        System.out.println(
                "Welcome to QuickChat");

        // ---------- MESSAGE LIMIT ----------

        System.out.print(
                "How many messages do you want to send? ");

        messageLimit =
                Integer.parseInt(input.nextLine());

        // ---------- MAIN LOOP ----------

        boolean running = true;

        while (running) {

            displayMenu();

            System.out.print("Choose option: ");

            String option =
                    input.nextLine();

            switch (option) {

                case "1":

                    sendMessage();

                    break;

                case "2":

                    showMessages();

                    break;

                case "3":

                    discardLastMessage();

                    break;

                case "4":

                    storeMessage();

                    break;

                case "5":

                    System.out.print(
                            "Save before exit? (yes/no): ");

                    String saveChoice =
                            input.nextLine();

                    if (saveChoice.equalsIgnoreCase("yes")) {

                        storeMessage();
                    }

                    System.out.println(
                            "Total messages sent: "
                            + sentCount);

                    System.out.println("Goodbye!");

                    running = false;

                    break;

                default:

                    System.out.println(
                            "[ERROR] Invalid option!");
            }
        }
    }
}
