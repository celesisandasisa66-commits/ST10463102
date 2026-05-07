package com.mycompany.consolesystem;

import java.util.*; // import utilities
import java.text.SimpleDateFormat; // import date format
import java.io.FileWriter; // import file writer
import java.io.IOException; // import IO error handling
import java.util.regex.Pattern; // import regex

public class ConsoleSystem { // main class (UPDATED NAME)

    // ---------------- STORAGE ----------------
    static ArrayList<String> ids = new ArrayList<>(); // store message ids
    static ArrayList<String> messages = new ArrayList<>(); // store messages
    static ArrayList<String> recipients = new ArrayList<>(); // store numbers
    static ArrayList<String> timestamps = new ArrayList<>(); // store time
    static ArrayList<String> hashes = new ArrayList<>(); // store hashes
    static ArrayList<String> statusList = new ArrayList<>(); // store status

    static int sentCount = 0; // count messages sent
    static int messageLimit = 0; // max messages allowed

    static Scanner input = new Scanner(System.in); // read input

    // ---------------- VALIDATION ----------------
    public static boolean checkUserName(String u) { // check username
        return u.contains("_") && u.length() <= 5; // must have _ and <=5
    }

    public static boolean checkPassword(String p) { // check password
        return Pattern.matches("^(?=.[A-Z])(?=.\\d)(?=.*[@#$%^&+=!]).{8,}$", p); // rules
    }

    public static boolean login(String u, String p, String su, String sp) { // login check
        return u.equals(su) && p.equals(sp); // compare input with saved
    }

    public static String validateNumber(String num) { // check phone number

        if (num == null) return "Invalid"; // null check

        if (!num.startsWith("+27")) // must start +27
            return "Invalid: must start with +27";

        if (num.length() != 12) // must be 12 chars
            return "Invalid: must be 12 digits (+27XXXXXXXXX)";

        if (!num.substring(3).matches("\\d{9}")) // rest must be digits
            return "Invalid: only digits allowed after +27";

        return "Valid"; // correct number
    }

    public static String createMessageHash(String id, int num, String msg) { // make hash

        String[] words = msg.trim().split(" "); // split message

        String first = words.length > 0 ? words[0] : "MSG"; // first word
        String last = words.length > 1 ? words[words.length - 1] : words[0]; // last word

        return (id.substring(0, 2) + ":" + num + ":" + first + last).toUpperCase(); // build hash
    }

    // ---------------- SAVE FILE ----------------
    public static void storeMessage() { // save messages

        try {
            FileWriter file = new FileWriter("messages.json"); // create file

            file.write("[\n"); // start json array

            for (int i = 0; i < messages.size(); i++) { // loop messages

                file.write("  {\n"); // start object
                file.write("    \"id\": \"" + ids.get(i) + "\",\n"); // write id
                file.write("    \"hash\": \"" + hashes.get(i) + "\",\n"); // write hash
                file.write("    \"recipient\": \"" + recipients.get(i) + "\",\n"); // write number
                file.write("    \"message\": \"" + messages.get(i) + "\",\n"); // write message
                file.write("    \"status\": \"" + statusList.get(i) + "\",\n"); // write status
                file.write("    \"time\": \"" + timestamps.get(i) + "\"\n"); // write time
                file.write("  }"); // end object

                if (i < messages.size() - 1) file.write(","); // add comma
                file.write("\n"); // new line
            }

            file.write("]"); // end json array
            file.close(); // close file

            System.out.println("\n[SYSTEM] File saved successfully!"); // success msg
            System.out.println("[SYSTEM] Total records saved: " + messages.size()); // count

        } catch (IOException e) { // catch error
            System.out.println("[ERROR] Failed to save file!"); // error msg
        }
    }

    // ---------------- SHOW MESSAGES ----------------
    public static void showMessages() { // display messages

        System.out.println("\n=============================="); // line
        System.out.println("      SHOW MESSAGES"); // title
        System.out.println("=============================="); // line

        System.out.println("[COMING SOON] This feature is under development."); // placeholder
        System.out.println("Please check back later."); // note

        System.out.println("==============================\n"); // end
    }

    // ---------------- MAIN ----------------
    public static void main(String[] args) { // program start

        System.out.println("=== QUICKCHAT CONSOLE ==="); // title

        // ---------------- REGISTRATION ----------------
        System.out.print("Create Username: "); // ask username
        String su = input.nextLine(); // read username

        System.out.print("Create Password: "); // ask password
        String sp = input.nextLine(); // read password

        if (!checkUserName(su) || !checkPassword(sp)) { // validate
            System.out.println("Invalid registration!"); // fail msg
            return; // stop program
        }

        // ---------------- LOGIN ----------------
        boolean logged = false; // login flag

        while (!logged) { // loop until correct

            System.out.print("Login Username: "); // ask user
            String u = input.nextLine(); // read user

            System.out.print("Login Password: "); // ask pass
            String p = input.nextLine(); // read pass

            logged = login(u, p, su, sp); // check login

            if (!logged)
                System.out.println("Login failed!"); // wrong details
        }

        System.out.println("Welcome to QuickChat"); // success msg

        // ---------------- LIMIT ----------------
        System.out.print("How many messages do you want to send? "); // ask limit
        messageLimit = Integer.parseInt(input.nextLine()); // read limit

        // ---------------- MENU ----------------
        while (true) { // infinite loop

            System.out.println("\n========== MENU =========="); // menu
            System.out.println("1. Send Message"); // option 1
            System.out.println("2. Show Messages"); // option 2
            System.out.println("3. Discard Last Message"); // option 3
            System.out.println("4. Save Messages"); // option 4
            System.out.println("5. Quit"); // option 5
            System.out.println("=========================="); // end menu

            System.out.print("Choose option: "); // ask choice
            String choice = input.nextLine(); // read choice

            switch (choice) {

                case "1":

                    if (sentCount >= messageLimit) {
                        System.out.println("[WARNING] Message limit reached!");
                        break;
                    }

                    System.out.print("Enter Recipient (+27XXXXXXXXX): ");
                    String rec = input.nextLine();

                    String validation = validateNumber(rec);
                    if (!validation.equals("Valid")) {
                        System.out.println("[ERROR] " + validation);
                        break;
                    }

                    System.out.print("Enter Message (max 250 chars): ");
                    String msg = input.nextLine();

                    if (msg.length() > 250) {
                        System.out.println("[ERROR] Message too long!");
                        break;
                    }

                    String id = String.format("%010d",
                            (long) (Math.random() * 10000000000L));

                    String hash = createMessageHash(id, sentCount, msg);

                    ids.add(id);
                    hashes.add(hash);
                    recipients.add(rec);
                    messages.add(msg);
                    timestamps.add(new SimpleDateFormat("HH:mm:ss").format(new Date()));
                    statusList.add("SENT");

                    sentCount++;

                    System.out.println("\n[SUCCESS] MESSAGE SENT");
                    System.out.println("ID   : " + id);
                    System.out.println("HASH : " + hash);

                    break;

                case "2":
                    showMessages();
                    break;

                case "3":

                    if (messages.isEmpty()) {
                        System.out.println("[INFO] No messages to discard.");
                        break;
                    }

                    int last = messages.size() - 1;

                    System.out.println("[SYSTEM] Discarding last message...");
                    System.out.println("Removed: " + messages.get(last));

                    ids.remove(last);
                    hashes.remove(last);
                    recipients.remove(last);
                    messages.remove(last);
                    timestamps.remove(last);
                    statusList.remove(last);

                    sentCount--;

                    System.out.println("[SUCCESS] Message discarded.");
                    break;

                case "4":
                    storeMessage();
                    break;

                case "5":

                    System.out.print("Save before exit? (yes/no): ");
                    String save = input.nextLine();

                    if (save.equalsIgnoreCase("yes")) {
                        storeMessage();
                    }

                    System.out.println("Total messages sent: " + sentCount);
                    System.out.println("Goodbye!");
                    return;

                default:
                    System.out.println("[ERROR] Invalid option!");
            }
        }
    }
}
