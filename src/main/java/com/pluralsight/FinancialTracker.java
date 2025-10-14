package com.pluralsight;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

/*
 * Capstone skeleton – personal finance tracker.
 * ------------------------------------------------
 * File format  (pipe-delimited)
 *     yyyy-MM-dd|HH:mm:ss|description|vendor|amount
 * A deposit has a positive amount;
 * a payment is stored as a negative amount.
 */
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

    /* ------------------------------------------------------------------
       Main menu
       ------------------------------------------------------------------ */
    public static void main(String[] args) {
        loadTransactions(FILE_NAME);

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("Welcome to TransactionApp");
            System.out.println("=Home Screen=");
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
     * Load transactions from FILE_NAME.
     * • If the file doesn’t exist, create an empty one so that future writes succeed.
     * • Each line looks like: date|time|description|vendor|amount
     */
        public static void loadTransactions(String fileName) {
            // TODO: create file if it does not exist, then read each line,
            //       parse the five fields, build a Transaction object,
            //       and add it to the transactions list.
            // add reader to one file " transactions.csv".
            //String formatting with "\\|": Each transaction should be saved as a single
            // line with the following format.
            //date|time|description|vendor|amount
            //2023-04-15|10:13:25|ergonomic keyboard|Amazon|-89.50
            //2023-04-15|11:15:00|Invoice 1001 paid|Joe|1500.00
            // add try/catch

            try{
                    BufferedReader reader = new BufferedReader(new FileReader(fileName));

                    String line;
                    while ((line =reader.readLine()) !=null ){
                        String[] parts = line.split("\\|");
                        LocalDate date = LocalDate.parse(parts[0]);
                        LocalTime time = LocalTime.parse(parts[1]);
                        String description = parts[2];
                        String vendor = parts[3];
                        double amount = Double.parseDouble(parts[4]);

                        transactions.add(new Transaction(date, time, description, vendor, amount));

                    }
//                    for(Transaction t: transactions){
//                        System.out.println(t.toString());
//                    }
                    reader.close();

            }catch (Exception e){
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
        // add BufferedWriter (, append  Ture ) to one file " transactions.csv"
        //  prompt to get amount input, if (amount > 0) else re-enter.
        //  Use parseDate and parseDouble.
        //  add try/ catch

        String date, time, description, vendor;
        double amount;

        try {
            System.out.println("Please enter information below to log your deposit: ");
            System.out.printf("To log current date and time leave Date and Time flid empty and press enter." +
                    "\nDate (yyyy-MM-dd): ");
            date = scanner.nextLine().trim();
            System.out.printf("Time (HH:mm:ss): ");
            time = scanner.nextLine().trim();
            System.out.printf("Description: ");
            description = scanner.nextLine().trim();
            System.out.printf("Vendor: ");
            vendor = scanner.nextLine().trim();

            //try{
            System.out.printf("Amount: ");
            amount = scanner.nextDouble();
//            }catch(Exception e1){
//                System.out.println("Invalid entry. Please enter again with positive numbers.");
//            }

            if (date.equalsIgnoreCase("")){
            LocalDate currentDate = LocalDate.now();
            date = currentDate.format(DATE_FMT);
            }
            if (time.equalsIgnoreCase("")){
            LocalTime currentTime = LocalTime.now();
            time = currentTime.format(DATE_FMT);
            }

            //LocalDateTime dateTime = LocalDateTime.of(date, time);

            boolean isDone = false;

            while(!isDone){
                if (amount > 0){

                    BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true));

                    String outPut = String.format("%s|%s|%s|%s|%.2f\n", date,time,description,vendor,amount);
                    writer.newLine();
                    writer.write(outPut);
                    System.out.println("Successfully added new deposit: \n" + outPut);
                    isDone = true;

                }else{
                    System.out.println("Invalid entry. Please enter again with positive numbers.");
                }

            }
        } catch (Exception ex) {
            System.err.println("Error. File was unable to read file. " + ex);
        }

    }

    /**
     * Same prompts as addDeposit.
     * Amount must be entered as a positive number,
     * then converted to a negative amount before storing.
     */
    private static void addPayment(Scanner scanner) {
        // add BufferedWriter (, append  Ture ) to one file " transactions.csv"
        // prompt to get amount input, if (amount > 0) else re-enter.
        // Amount *-1 to get negative
        // Use parseDate and parseDouble.
        //  add try/ catch

    }

    /* ------------------------------------------------------------------
       Ledger menu
       ------------------------------------------------------------------ */
    private static void ledgerMenu(Scanner scanner) {
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
    private static void displayLedger() { /* TODO – print all transactions in column format */ }

    private static void displayDeposits() { /* TODO – only amount > 0               */ }

    private static void displayPayments() { /* TODO – only amount < 0               */ }

    /* ------------------------------------------------------------------
       Reports menu
       ------------------------------------------------------------------ */
    private static void reportsMenu(Scanner scanner) {
        boolean running = true;
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
                case "1" -> {/* TODO – month-to-date report
                Date validation,
                (the 1st of current LocalDateTime.getMonth  to LocalDateTime.now )
                get the previous & next day date from a given date, plusDays(n), minusDays(n),
                can add ".isAfter" or ".is Before" to get comparison for if conditions.

                */
                }
                case "2" -> {/* TODO – previous month report
                plusMonths(n), minusMonths(n), */ }
                case "3" -> {/* TODO – year-to-date report
                 (1st of current year LocalDateTime.getYear to LocalDateTime.now)
                */ }
                case "4" -> {/* TODO – previous year report
                plusYears(n), minusYears(n)? */ }
                case "5" -> {/* TODO – prompt for vendor then report */ }
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
        // TODO – iterate transactions, print those within the range
    }

    private static void filterTransactionsByVendor(String vendor) {
        // TODO – iterate transactions, print those with matching vendor
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

        return null;
    }

    private static Double parseDouble(String s) {
        try{
            return Math.round(Double.parseDouble(s) * 100.0) / 100.0;
        }
        catch(Exception e) {
            return null;
        }
    }
}