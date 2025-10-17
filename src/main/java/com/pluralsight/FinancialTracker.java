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

    private static final String firstLine = String.format("%-10s|%-8s|%20s|%20s|%s", "Date", "Time", "Description", "Vendor", "Amount");
/* ------------------------------------------------------------------
    text colors
    ------------------------------------------------------------------ */
    private static final String RESET  = "\u001B[0m";
    private static final String RED    = "\u001B[31m";
    private static final String GREEN  = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE   = "\u001B[34m";

    /* ------------------------------------------------------------------
       Main menu
       ------------------------------------------------------------------ */
    public static void main(String[] args) {
        loadTransactions(FILE_NAME);

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {

            System.out.println("Welcome to TransactionApp");
            System.out.println("=== Home Screen ====");
            System.out.println("Choose an option:");
            System.out.println("D) Add Deposit");
            System.out.println("P) Make Payment (Debit)");
            System.out.println("L) Ledger");
            System.out.println("X) Exit");

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
     * @param fileName
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
            System.err.println("Error creating file." + e);
        }

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
            System.err.println("Error, Unable to read file. " + fileName + e);
        }

    }

    /* ------------------------------------------------------------------
       Add new transactions
       ------------------------------------------------------------------ */

    /**
     * Prompt for ONE date+time string in the format
     * "yyyy-MM-dd HH:mm:ss", plus description, vendor, amount.
     * Validate that the amount entered is positive.
     * Store the amount as-is (positive) and append to the file.
     */
    private static void addDeposit(Scanner scanner) {

        String date, time, description, vendor, amountS, dateAndTime;
        double amount = 0.0;
        LocalDate enterDate;
        LocalTime enterTime;
        LocalDateTime dateTime;

        try {
            boolean isDone = false;
            while (!isDone) {
                System.out.println("Please enter information below to log your deposit: ");

                System.out.print("To log the current date and time, leave Date and Time field empty and press enter." +
                        "\nDate and Time 'yyyy-MM-dd HH:mm:ss': ");
                dateAndTime = scanner.nextLine().trim();
                if (dateAndTime.equalsIgnoreCase("")) {
                    dateTime = LocalDateTime.now();
                    enterDate = dateTime.toLocalDate();
                    enterTime = dateTime.toLocalTime();
                    date = enterDate.format(DATE_FMT);
                    time = enterTime.format(TIME_FMT);
                } else {
                    dateTime = LocalDateTime.parse(dateAndTime,DATETIME_FMT);
                    enterDate = dateTime.toLocalDate();
                    enterTime = dateTime.toLocalTime();
                    date = enterDate.format(DATE_FMT);
                    time = enterTime.format(TIME_FMT);

                }

                System.out.print("Description: ");
                description = scanner.nextLine().trim();
                System.out.print("Vendor: ");
                vendor = scanner.nextLine().trim();
                try {
                    System.out.print("Amount: ");
                    amountS = scanner.nextLine();
                    amount = parseDouble(amountS);

                } catch (Exception e1) {
                    System.out.println("Invalid entry. Please enter again with positive numbers.");
                }


                //To do: LocalDateTime dateTime = LocalDateTime.of(date, time);

                if (amount > 0) {
                    transactions.add(new Transaction(enterDate, enterTime, description, vendor, amount));

                    BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true));

                    String outPut = String.format("%s|%s|%s|%s|%.2f", date, time, description, vendor, amount);
                    writer.write(outPut);
                    writer.newLine();

                    System.out.println("Successfully added new deposit: \n" + outPut);
                    isDone = true;

                    writer.close();
                } else {
                    System.out.println("Invalid entry. Please enter again with positive numbers.");
                }

            }
        } catch (Exception ex) {
            System.err.println("There was a problem processing your file. \n" +
                    "This often happens if the date or time format is incorrect.\n " +
                    "Please review your entry and ensure it matches the correct format." + ex);
        }

    }

    /**
     * Same prompts as addDeposit.
     * Amount must be entered as a positive number,
     * then converted to a negative amount before storing.
     */
    private static void addPayment(Scanner scanner) {

        String date, time, description, vendor, amountS, dateAndTime;
        double amount = 0.0;
        LocalDate enterDate;
        LocalTime enterTime;
        LocalDateTime dateTime;

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
                    System.out.println("Invalid entry. Please enter again with positive numbers.");
                }


                if (amount > 0) {
                    amount *= -1;
                    transactions.add(new Transaction(enterDate, enterTime, description, vendor, amount));

                    BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true));

                    String outPut = String.format("%s|%s|%s|%s|%.2f", date, time, description, vendor, amount);
                    writer.write(outPut);
                    writer.newLine();


                    System.out.println("Successfully added new payment: \n" + outPut);
                    isDone = true;
                    writer.close();

                } else {
                    System.out.println("Invalid entry. Please enter again with positive numbers.");
                }

            }
        } catch (Exception ex) {
            System.err.println("There was a problem processing your file.\n" +
                    "This often happens if the date or time format is incorrect.\n" +
                    "Please review your entry and ensure it matches the correct format." + ex);
        }

    }

    /* ------------------------------------------------------------------
       Ledger menu
       ------------------------------------------------------------------ */
    private static void ledgerMenu(Scanner scanner) {
        // Sort newest to oldest: date first, then time
        transactions.sort(Comparator.comparing(Transaction::getDate)
                .thenComparing(Transaction::getTime).reversed());

        boolean running = true;
        while (running) {
            System.out.println("Ledger");
            System.out.println("Choose an option:");
            System.out.println("A) All");
            System.out.println("D) Deposits");
            System.out.println("P) Payments");
            System.out.println("R) Reports");
            System.out.println("H) Home");

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
    private static void displayLedger() { /* TODO – print all transactions in column format */
        System.out.println(firstLine);

        try {
            for (Transaction transaction : transactions) {

                System.out.println(transaction);
            }
        } catch (Exception e) {
            System.err.println("Error displaying list. " + e);
        }

    }

    private static void displayDeposits() { /* TODO – only amount > 0  */
        System.out.println(firstLine);

        try {
            for (Transaction transaction : transactions) {
                double amount = transaction.getAmount();
                if (amount > 0) {

                    System.out.println(transaction);
                }
            }
        } catch (Exception e) {
            System.err.println("Error displaying list." + e);
        }

    }

    private static void displayPayments() { /* TODO – only amount < 0               */
        System.out.println(firstLine);

        try {
            for (Transaction transaction : transactions) {
                double amount = transaction.getAmount();
                if (amount < 0) {

                    System.out.println(transaction);
                }
            }
        } catch (Exception e) {
            System.err.println("Error displaying list." + e);
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
            System.out.println("Reports");
            System.out.println("Choose an option:");
            System.out.println("1) Month To Date");
            System.out.println("2) Previous Month");
            System.out.println("3) Year To Date");
            System.out.println("4) Previous Year");
            System.out.println("5) Search by Vendor");
            System.out.println("6) Custom Search");
            System.out.println("0) Back");

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
    private static void filterTransactionsByDate(LocalDate start, LocalDate end) {

        try {
//
            System.out.println("start date: " + start);
            System.out.println("end date: " + end);

            System.out.println(firstLine);
            for (Transaction transaction : transactions) {
                LocalDate date = transaction.getDate();
                if ((date.isAfter(start)|| date.isEqual(start)) && (date.isBefore(end)|| date.isEqual(end))) {

                    System.out.println(transaction);
                }
            }
        } catch (Exception e) {
            System.err.println("Error displaying report." + e);
        }

    }

    private static void filterTransactionsByVendor(String vendor) {
        // TODO – iterate transactions, print those with matching vendor
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
            System.err.println("Error displaying list." + e);
        }
    }

    private static void customSearch(Scanner scanner) {
        // TODO – prompt for any combination of date range, description,
        //        vendor, and exact amount, then display matches

    }

    /* ------------------------------------------------------------------
       Utility parsers (you can reuse in many places)
       ------------------------------------------------------------------ */
    private static LocalDate parseDate(String s) {
        /* TODO – return LocalDate or null */
        try {
            return LocalDate.parse(s, DATE_FMT);
        } catch (Exception e) {
            return null;
        }

    }

    private static LocalTime parseTime(String s) {
        /* TODO – return LocalDate or null */
        try {
            return LocalTime.parse(s, TIME_FMT);
        } catch (Exception e) {
            return null;
        }

    }

    private static LocalDateTime parseDateTime(String s) {
        /* TODO – return LocalDate or null */
        try {
            return LocalDateTime.parse(s, DATETIME_FMT);
        } catch (Exception e) {
            return null;
        }

    }

    /**
     *Parses a string to a Double, rounding to the nearest two decimal places.
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

    private static LocalDateTime promptUserDateTime (Scanner scanner) {

        while (true) {
            System.out.print("To log the current date and time, leave Date and Time field empty and press enter." +
                    "\nDate and Time 'yyyy-MM-dd HH:mm:ss': ");
            String dateAndTime = scanner.nextLine().trim();

            if (dateAndTime.equalsIgnoreCase("")) {
                return LocalDateTime.now();

            }
            LocalDateTime parseReturnDT = parseDateTime(dateAndTime);
            if (parseReturnDT != null ){
                return parseReturnDT;
            }else {
                System.out.println(RED + "Invalid entry. " +
                        "Please review your Date and Time entry and ensure it matches the correct format."+ RESET);
            }

        }

    }


}