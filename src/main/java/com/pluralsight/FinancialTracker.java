package com.pluralsight;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;

import java.util.*;

public class FinancialTracker {

    /* ------------------------------------------------------------------
       Shared data and formatters
       ------------------------------------------------------------------ */

    private static final ArrayList<Transaction> transactions = new ArrayList<>();
    private static final String FILE_NAME = "transactions.csv";

    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private static final String TIME_PATTERN = "HH:mm:ss";
    private static final String DATETIME_PATTERN = DATE_PATTERN + " " + TIME_PATTERN;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern(DATE_PATTERN);
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern(TIME_PATTERN);
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern(DATETIME_PATTERN);

    private static final String firstLine = String.format("%-10s|%-8s|%-30s|%-20s|%s\n", "Date", "Time", "Description", "Vendor", "Amount");
    /* ------------------------------------------------------------------
        text colors
       ------------------------------------------------------------------ */
    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[93m";
    private static final String BLUE = "\u001B[34m";
    private static final String BLUE2 = "\u001B[94m";

    /* ------------------------------------------------------------------
       Main menu
       ------------------------------------------------------------------ */
    public static void main(String[] args) {
        loadTransactions(FILE_NAME);

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {

            System.out.println(YELLOW + "\nWelcome to Financial Tracker App\n" + RESET);
            System.out.println(GREEN + "=== Home Screen ====" + RESET);
            System.out.println(GREEN + "Choose an option:" + RESET);
            System.out.println(GREEN + "D) Add Deposit" + RESET);
            System.out.println(GREEN + "P) Make Payment (Debit)" + RESET);
            System.out.println(GREEN + "L) Ledger" + RESET);
            System.out.println(RED + "X) Exit" + RESET);

            String input = scanner.nextLine().trim();

            switch (input.toUpperCase()) {
                case "D" -> addDeposit(scanner);
                case "P" -> addPayment(scanner);
                case "L" -> ledgerMenu(scanner);
                case "X" -> running = false;
                default -> System.out.println("Invalid option");
            }
        }
        scanner.close();
    }

    /* ------------------------------------------------------------------
       File I/O
       ------------------------------------------------------------------ */

    /**
     * Create file if it does not exist, read file and added transactions to transactions list.
     *
     * @param fileName is set to "transactions.csv".
     */
    public static void loadTransactions(String fileName) {
        File file = new File(fileName);
        try {
            if (!file.exists()) {
                file.createNewFile();
                System.out.println("File created: " + fileName);
            } else {
                System.out.println("Found file " + fileName
                        + ", all your transactions will be added and saved in " + fileName + ".");
            }

        } catch (Exception e) {
            System.out.println(RED + "Error creating file." + e + RESET);
        }
        /// read file and added transaction to transactions list
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                LocalDate date = LocalDate.parse(parts[0], DATE_FMT);
                LocalTime time = LocalTime.parse(parts[1], TIME_FMT);
                String description = parts[2];
                String vendor = parts[3];
                double amount = Double.parseDouble(parts[4]);

                transactions.add(new Transaction(date, time, description, vendor, amount));
            }
            reader.close();

        } catch (Exception e) {
            System.out.println(RED + "Error, Unable to read file. " + fileName + e + RESET);
        }

    }

    /* ------------------------------------------------------------------
       Add new transactions
       ------------------------------------------------------------------ */

    /**
     * prompt user for deposit details, add and save deposit to list and to file.
     *
     * @param scanner Use for prompting user input for transaction details.
     */
    private static void addDeposit(Scanner scanner) {

        String date, time, description, vendor, amountS;
        double amount = 0.0;
        LocalDate enterDate;
        LocalTime enterTime;
        LocalDateTime dateTime;

        /// Prompt for deposit details.
        /// If user used invalid input/ format will trigger try/catch and have user re-enter.
        try {
            boolean isDone = false;
            while (!isDone) {
                System.out.println("Please enter information below to log your deposit: ");

                dateTime = promptUserDateTime(scanner);

                enterDate = dateTime.toLocalDate();
                enterTime = dateTime.toLocalTime();
                date = enterDate.format(DATE_FMT);
                time = enterTime.format(TIME_FMT);

                System.out.print("Description: ");
                description = scanner.nextLine().trim();
                System.out.print("Vendor: ");
                vendor = scanner.nextLine().trim();

                try {
                    System.out.print("Amount: ");
                    amountS = scanner.nextLine();
                    amount = parseDouble(amountS);

                } catch (Exception e1) {
                    System.out.println(RED + "Invalid entry. Please enter again with positive numbers." + RESET);
                }

                /// if entered amount is positive, add deposit to list and append to file.
                if (amount > 0) {
                    transactions.add(new Transaction(enterDate, enterTime, description, vendor, amount));

                    BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true));

                    String outPut = String.format("%s|%s|%s|%s|%.2f", date, time, description, vendor, amount);
                    writer.write(outPut);
                    writer.newLine();

                    System.out.println(GREEN + "Successfully added new deposit: \n" + outPut + RESET);
                    isDone = true;

                    writer.close();
                } else {
                    System.out.println(RED + "Invalid entry. Please enter again with positive numbers." + RESET);
                }

            }
        } catch (Exception ex) {
            System.out.println(RED + "There was a problem processing your file. \n" +
                    "This often happens if the date or time format is incorrect.\n " +
                    "Please review your entry and ensure it matches the correct format." + ex + RESET);
        }

    }

    /**
     * prompt user for debit details, add and save debit to list and to file.
     *
     * @param scanner Use for prompting user input for transaction details.
     */
    private static void addPayment(Scanner scanner) {

        String date, time, description, vendor, amountS;
        double amount = 0.0;
        LocalDate enterDate;
        LocalTime enterTime;
        LocalDateTime dateTime;

        /// Prompt for debit details.
        /// If user used invalid input/ format will trigger try/catch and have user re-enter.
        try {
            boolean isDone = false;
            while (!isDone) {
                System.out.println("Please enter information below to log your payment: ");

                dateTime = promptUserDateTime(scanner);

                enterDate = dateTime.toLocalDate();
                enterTime = dateTime.toLocalTime();
                date = enterDate.format(DATE_FMT);
                time = enterTime.format(TIME_FMT);


                System.out.print("Description: ");
                description = scanner.nextLine().trim();
                System.out.print("Vendor: ");
                vendor = scanner.nextLine().trim();

                try {
                    System.out.print("Amount: ");
                    amountS = scanner.nextLine();
                    amount = parseDouble(amountS);

                } catch (Exception e1) {
                    System.out.println(RED + "Invalid entry. Please enter again with positive numbers." + RESET);
                }

                /// if entered amount is positive, change amount to negative =, then add debit to list and append to file.
                if (amount > 0) {
                    amount *= -1;
                    transactions.add(new Transaction(enterDate, enterTime, description, vendor, amount));

                    BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true));

                    String outPut = String.format("%s|%s|%s|%s|%.2f", date, time, description, vendor, amount);
                    writer.write(outPut);
                    writer.newLine();

                    System.out.println(GREEN + "Successfully added new payment: \n" + outPut + RESET);

                    isDone = true;
                    writer.close();

                } else {
                    System.out.println(RED + "Invalid entry. Please enter again with positive numbers." + RESET);
                }

            }
        } catch (Exception ex) {
            System.out.println(RED + "There was a problem processing your file.\n" +
                    "This often happens if the date or time format is incorrect.\n" +
                    "Please review your entry and ensure it matches the correct format." + ex + RESET);
        }

    }

    /* ------------------------------------------------------------------
       Ledger menu
       ------------------------------------------------------------------ */
    private static void ledgerMenu(Scanner scanner) {
        /// Sort newest to oldest: date first, then time
        transactions.sort(Comparator.comparing(Transaction::getDate)
                .thenComparing(Transaction::getTime).reversed());

        boolean running = true;
        while (running) {
            System.out.println(BLUE + "== Ledger ==" + RESET);
            System.out.println(BLUE + "Choose an option:" + RESET);
            System.out.println(BLUE + "A) All" + RESET);
            System.out.println(BLUE + "D) Deposits" + RESET);
            System.out.println(BLUE + "P) Payments" + RESET);
            System.out.println(BLUE + "R) Reports" + RESET);
            System.out.println(GREEN + "H) Home" + RESET);

            String input = scanner.nextLine().trim();


            switch (input.toUpperCase()) {
                case "A" -> displayLedger();
                case "D" -> displayDeposits();
                case "P" -> displayPayments();
                case "R" -> reportsMenu(scanner);
                case "H" -> running = false;
                default -> System.out.println("Invalid option");
            }
        }
    }

    /* ------------------------------------------------------------------
       Display helpers: show data in neat columns
       ------------------------------------------------------------------ */
    private static void displayLedger() {
        System.out.println(BLUE2 + " All Transactions: " + RESET);
        System.out.println(firstLine);

        ///print all transactions in column format
        try {
            for (Transaction transaction : transactions) {
                System.out.println(transaction);
            }
        } catch (Exception e) {
            System.out.println(RED + "Error displaying list. " + e + RESET);
        }

    }

    private static void displayDeposits() {
        System.out.println(BLUE2 + " All Deposits: " + RESET);
        System.out.println(firstLine);

        ///print all deposits in column format
        try {
            for (Transaction transaction : transactions) {
                double amount = transaction.getAmount();
                if (amount > 0) {

                    System.out.println(transaction);
                }
            }
        } catch (Exception e) {
            System.out.println(RED + "Error displaying list." + e + RESET);
        }

    }

    private static void displayPayments() {
        System.out.println(BLUE2 + " All Payments: " + RESET);
        System.out.println(firstLine);

        ///print all payments in column format.
        try {
            for (Transaction transaction : transactions) {
                double amount = transaction.getAmount();
                if (amount < 0) {

                    System.out.println(transaction);
                }
            }
        } catch (Exception e) {
            System.out.println(RED + "Error displaying list." + e + RESET);
        }

    }

    /* ------------------------------------------------------------------
       Reports menu
       ------------------------------------------------------------------ */
    private static void reportsMenu(Scanner scanner) {
        boolean running = true;
        LocalDate currentDate = LocalDate.now();
        LocalDate startDate, endDate;

        while (running) {
            System.out.println(BLUE2 + " = Reports = " + RESET);
            System.out.println(BLUE2 + "Choose an option:" + RESET);
            System.out.println(BLUE2 + "1) Month To Date" + RESET);
            System.out.println(BLUE2 + "2) Previous Month" + RESET);
            System.out.println(BLUE2 + "3) Year To Date" + RESET);
            System.out.println(BLUE2 + "4) Previous Year" + RESET);
            System.out.println(BLUE2 + "5) Search by Vendor" + RESET);
            System.out.println(BLUE2 + "6) Custom Search" + RESET);
            System.out.println(GREEN + "0) Back" + RESET);

            String input = scanner.nextLine().trim();


            switch (input) {
                case "1" -> {
                    System.out.println("Month To Date Report:");
                    startDate = currentDate.withDayOfMonth(1);
                    endDate = currentDate;

                    filterTransactionsByDate(startDate, endDate);

                }
                case "2" -> {
                    System.out.println("Previous Month Report:");
                    LocalDate previousMonthDate = currentDate.minusMonths(1);

                    startDate = previousMonthDate.withDayOfMonth(1);
                    endDate = previousMonthDate.with(TemporalAdjusters.lastDayOfMonth());

                    filterTransactionsByDate(startDate, endDate);
                }
                case "3" -> {
                    System.out.println("Year To Date Report:");
                    startDate = currentDate.withDayOfYear(1);
                    endDate = currentDate;

                    filterTransactionsByDate(startDate, endDate);

                }
                case "4" -> {
                    System.out.println("Previous Year Report:");
                    LocalDate previousYearDate = currentDate.minusYears(1);

                    startDate = previousYearDate.withDayOfYear(1);
                    endDate = previousYearDate.with(TemporalAdjusters.lastDayOfYear());

                    filterTransactionsByDate(startDate, endDate);
                }
                case "5" -> {
                    System.out.print("Please enter the vendor: ");
                    input = scanner.nextLine().trim();
                    filterTransactionsByVendor(input);
                }
                case "6" -> customSearch(scanner);
                case "0" -> running = false;
                default -> System.out.println("Invalid option");
            }
        }
    }

    /* ------------------------------------------------------------------
       Reporting helpers
       ------------------------------------------------------------------ */

    /**
     * Filter Transactions by dates.
     *
     * @param start a predefined start date based on the current date.
     * @param end   a predefined start date based on the current date.
     */
    private static void filterTransactionsByDate(LocalDate start, LocalDate end) {

        try {
            System.out.println("start date: " + start);
            System.out.println("end date: " + end);

            System.out.println(firstLine);
            for (Transaction transaction : transactions) {
                LocalDate date = transaction.getDate();
                if ((date.isAfter(start) || date.isEqual(start)) && (date.isBefore(end) || date.isEqual(end))) {

                    System.out.println(transaction);
                }
            }
        } catch (Exception e) {
            System.out.println(RED + "Error displaying report." + e + RESET);
        }

    }

    /**
     * Iterate transactions, print transactions with matching vendor.
     *
     * @param vendor string input from user.
     */
    private static void filterTransactionsByVendor(String vendor) {
        try {
            System.out.println(firstLine);
            boolean found = false;

            for (Transaction transaction : transactions) {
                String theVendor = transaction.getVendor();

                if (theVendor.equalsIgnoreCase(vendor)) {
                    System.out.println(transaction);
                    found = true;
                }

            }
            if (!found) {
                System.out.println("Did not find any transaction under vendor: " + vendor);
            }

        } catch (Exception e) {
            System.out.println(RED + "Error displaying list." + e + RESET);
        }
    }

    private static void customSearch(Scanner scanner) {
        // TODO – prompt for any combination of date range, description,
        //        vendor, and exact amount, then display matches
        System.out.println(YELLOW + "This feature is still underdevelopment. " + RESET);
        System.out.println(YELLOW + "Returning to the Reports screen..." + RESET);

    }

    /* ------------------------------------------------------------------
       Utility parsers (you can reuse in many places)
       ------------------------------------------------------------------ */

    /**
     * pares LocalDate String
     *
     * @param s user date input in string
     * @return LocalDate or null
     */
    private static LocalDate parseDate(String s) {
        try {
            return LocalDate.parse(s, DATE_FMT);
        } catch (Exception e) {
            return null;
        }

    }

    /**
     * pares LocalTime String
     *
     * @param s user time input in string
     * @return LocalTime or null
     */
    private static LocalTime parseTime(String s) {
        try {
            return LocalTime.parse(s, TIME_FMT);
        } catch (Exception e) {
            return null;
        }

    }

    /**
     * pares one Date+Time String
     *
     * @param s one user date and time input in string
     * @return LocalDateTime or null
     */
    private static LocalDateTime parseDateTime(String s) {
        try {
            return LocalDateTime.parse(s, DATETIME_FMT);
        } catch (Exception e) {
            return null;
        }

    }

    /**
     * Parses a string to a Double, rounding to the nearest two decimal places.
     *
     * @param s The string input.
     * @return The rounded number, or null if parsing fails.
     */
    private static Double parseDouble(String s) {
        try {
            return Math.round(Double.parseDouble(s) * 100.0) / 100.0;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Prompt user for a date and time String input and return with a pares LocalDateTime.
     * Loops for re-enter if string input unable to parse into LocalDAteTime.
     *
     * @param scanner used for user input
     * @return current time or a pares LocalDateTime
     */
    private static LocalDateTime promptUserDateTime(Scanner scanner) {

        while (true) {
            System.out.print("To log the current date and time, leave Date and Time field empty and press enter." +
                    "\nDate and Time 'yyyy-MM-dd HH:mm:ss': ");
            String dateAndTime = scanner.nextLine().trim();

            if (dateAndTime.equalsIgnoreCase("")) {
                return LocalDateTime.now();

            }
            LocalDateTime parseReturnDT = parseDateTime(dateAndTime);
            if (parseReturnDT != null) {
                return parseReturnDT;
            } else {
                System.out.println(RED + "Invalid entry. " +
                        "Please review your Date and Time entry and ensure it matches the correct format." + RESET);
            }

        }

    }


}