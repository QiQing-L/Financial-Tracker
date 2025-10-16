package com.pluralsight;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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

    private static final String firstLine = String.format("%s%6s|%s%4s|%s|%s|%s","Date","","Time","", "Description", "Vendor", "Amount" );

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
     * Load transactions from FILE_NAME.
     * • If the file doesn’t exist, create an empty one so that future writes succeed.
     * • Each line looks like: date|time|description|vendor|amount
     */
        public static void loadTransactions(String fileName) {
            // TODO: create file if it does not exist, then read each line,
            //       parse the five fields, build a Transaction object,
            //       and add it to the transactions list.
            File file = new File(FILE_NAME);
            try{
                if(!file.exists()){
                    file.createNewFile();
                    System.out.println("File created: " +FILE_NAME);
                }else {
                    System.out.println("Found file " + FILE_NAME
                            + ", all your transactions will be added and saved in " +FILE_NAME);
                }

            }catch (Exception e){
                System.err.println("Error creating file." + e);
            }


            try{
                    BufferedReader reader = new BufferedReader(new FileReader(fileName));



                    String line;
                    while ((line =reader.readLine()) !=null ){
                        String[] parts = line.split("\\|");
                        LocalDate date = LocalDate.parse(parts[0],DATE_FMT);
                        LocalTime time = LocalTime.parse(parts[1],TIME_FMT);
                        String description = parts[2];
                        String vendor = parts[3];
                        double amount = Double.parseDouble(parts[4]);

                        transactions.add(new Transaction(date, time, description, vendor, amount));

                    }
                    reader.close();
               // this for each loop used for testing transactions arrayList.
                for(Transaction t: transactions){
                    System.out.println(t);
                }
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

        String date, time, description, vendor, amountS;
        date="";
        time ="";
        double amount = 0.0;
        LocalDate enterDate = LocalDate.now();
        LocalTime enterTime = LocalTime.now();

        try {
            boolean isDone = false;
            while(!isDone){
                System.out.println("Please enter information below to log your deposit: ");

                System.out.print("To log current date and time leave Date and Time flid empty and press enter." +
                    "\nDate (yyyy-MM-dd): ");
                date = scanner.nextLine().trim();
                System.out.print("Time (HH:mm:ss): ");
                time = scanner.nextLine().trim();

                if (date.equalsIgnoreCase("")){
                    enterDate = LocalDate.now();
                    date = enterDate.format(DATE_FMT);
                }else {enterDate = parseDate(date);
                    date = enterDate.format(DATE_FMT);
                }
                if (time.equalsIgnoreCase("")){
                    enterTime = LocalTime.now();
                    time = enterTime.format(TIME_FMT);
                }else{enterTime = parseTime(time);
                    time = enterTime.format(TIME_FMT);
                }

                System.out.print("Description: ");
                description = scanner.nextLine().trim();
                System.out.print("Vendor: ");
                vendor = scanner.nextLine().trim();
               try{
                    System.out.print("Amount: ");
                    amountS = scanner.nextLine();
                    amount = parseDouble(amountS);

               }catch(Exception e1){
                        System.out.println("Invalid entry. Please enter again with positive numbers.");
               }



            //LocalDateTime dateTime = LocalDateTime.of(date, time);

                if (amount > 0 ){
                    transactions.add(new Transaction(enterDate,enterTime, description, vendor, amount));

                    BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true));

                    String outPut = String.format("%s|%s|%s|%s|%.2f", date,time,description,vendor,amount);
                    writer.write(outPut);
                    writer.newLine();

                    System.out.println("Successfully added new deposit: \n" + outPut);
                    isDone = true;

                    writer.close();

                }
                else{
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
        // add BufferedWriter (, append  Ture ) to one file " transactions.csv"
        // prompt to get amount input, if (amount > 0) else re-enter.
        // Amount *-1 to get negative
        // Use parseDate and parseDouble.
        //  add try/ catch

        String date, time, description, vendor, amountS;
        double amount = 0.0;
        LocalDate enterDate;
        LocalTime enterTime;

        try {
            boolean isDone = false;
            while(!isDone){
                System.out.println("Please enter information below to log your payment: ");
                System.out.print("To log current date and time leave Date and Time flid empty and press enter." +
                        "\nDate (yyyy-MM-dd): ");
                date = scanner.nextLine().trim();

                System.out.print("Time (HH:mm:ss): ");
                time = scanner.nextLine().trim();

                if (date.equalsIgnoreCase("")){
                    enterDate = LocalDate.now();
                    date = enterDate.format(DATE_FMT);
                }else {enterDate = parseDate(date);
                    date = enterDate.format(DATE_FMT);}

                if (time.equalsIgnoreCase("")){
                    enterTime = LocalTime.now();
                    time = enterTime.format(TIME_FMT);
                }else{enterTime = parseTime(time);
                    time = enterTime.format(TIME_FMT);}

                System.out.print("Description: ");
                description = scanner.nextLine().trim();
                System.out.print("Vendor: ");
                vendor = scanner.nextLine().trim();

                try{
                    System.out.print("Amount: ");
                    amountS = scanner.nextLine();
                    amount = parseDouble(amountS);

                }catch(Exception e1){
                    System.out.println("Invalid entry. Please enter again with positive numbers.");
                }

                //LocalDateTime dateTime = LocalDateTime.of(date, time);

                if (amount > 0 ){

                    transactions.add(new Transaction(enterDate,enterTime, description, vendor, amount));

                    BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true));
                    amount *= -1;

                    String outPut = String.format("%s|%s|%s|%s|%.2f", date,time,description,vendor,amount);
                    writer.write(outPut);
                    writer.newLine();


                    System.out.println("Successfully added new payment: \n" + outPut);
                    isDone = true;
                    writer.close();

                }
                else{
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
        transactions.sort(Comparator
                .comparing(Transaction::getDate)
                .thenComparing(Transaction::getTime).reversed());
        //transactions.sort(Comparator.comparing(Transaction ::getDate));

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

            //can add sort here


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

        try{
            for(Transaction transaction: transactions){

                System.out.println(transaction);
            }
        } catch (Exception e) {
            System.err.println("Error displaying list. " + e );
        }

    }

    private static void displayDeposits() { /* TODO – only amount > 0  */
        System.out.println(firstLine);

        try{
            for(Transaction transaction: transactions){
                double amount = transaction.getAmount();
                if (amount>0){

                    System.out.println(transaction);
                }
            }
        }catch(Exception e){
            System.err.println("Error displaying list." + e);
        }


    }

    private static void displayPayments() { /* TODO – only amount < 0               */
        System.out.println(firstLine);

        try{
            for(Transaction transaction: transactions){
                double amount = transaction.getAmount();
                if (amount < 0){

                    System.out.println(transaction);
                }
            }
        }catch(Exception e){
            System.err.println("Error displaying list." + e);
        }

    }

    /* ------------------------------------------------------------------
       Reports menu
       ------------------------------------------------------------------ */
    private static void reportsMenu(Scanner scanner) {
        boolean running = true;
        LocalDate toDate = LocalDate.now();
        LocalDate startAfterDate, endBeforeDate;

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

                    startAfterDate = toDate.minusDays(toDate.getDayOfMonth());
                    endBeforeDate = toDate.plusDays(1);

                    //print dates for testing:
                    printDateForTest(startAfterDate,endBeforeDate);
                    System.out.println("Month To Date Report:");

                    filterTransactionsByDate(startAfterDate, endBeforeDate);

                }
                case "2" -> {/* TODO – previous month report
                plusMonths(n), minusMonths(n), */
                    startAfterDate = toDate.minusMonths(1).minusDays(toDate.getDayOfMonth());
                    endBeforeDate = toDate.minusDays(toDate.getDayOfMonth()-1);

                    //print dates for testing:
                    printDateForTest(startAfterDate,endBeforeDate);
                    System.out.println("Previous Month Report:");

                    filterTransactionsByDate(startAfterDate, endBeforeDate);
                }
                case "3" -> {/* TODO – year-to-date report
                 (1st of current year LocalDateTime.getYear to LocalDateTime.now)
                */
                    startAfterDate = toDate.minusDays(toDate.getDayOfYear());
                    endBeforeDate = toDate.plusDays(1);

                    //print dates for testing:
                    printDateForTest(startAfterDate,endBeforeDate);

                    System.out.println("Year To Date Report:");
                    filterTransactionsByDate(startAfterDate, endBeforeDate);

                }
                case "4" -> {/* TODO – previous year report
                plusYears(n), minusYears(n)? */
                    LocalDate previousYearDate = toDate.minusYears(1);
                    startAfterDate = previousYearDate.minusDays(previousYearDate.getDayOfYear());
                    endBeforeDate = toDate.minusDays(toDate.getDayOfYear()-1);


                    System.out.println("Previous Year Report:");
                    filterTransactionsByDate(startAfterDate, endBeforeDate);
                }
                case "5" -> {/* TODO – prompt for vendor then report */
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
        // TODO – iterate transactions, print those within the range
        try{
            LocalDate startDate = start.plusDays(1);
            LocalDate endDate = end.minusDays(1);
            System.out.println("start date: " + startDate);
            System.out.println("end date: " + endDate);

            System.out.println(firstLine);
            for(Transaction transaction: transactions){
                LocalDate date = transaction.getDate();
                if (date.isAfter(start) && date.isBefore(end)){

                    System.out.println(transaction);
                }
            }
        }catch(Exception e){
            System.err.println("Error displaying report." + e);
        }


    }

    private static void filterTransactionsByVendor(String vendor) {
        // TODO – iterate transactions, print those with matching vendor
        try{
            System.out.println(firstLine);
            boolean found = false;

            for(Transaction transaction: transactions){
            String theVendor = transaction.getVendor();

                if (theVendor.equalsIgnoreCase(vendor)){
                    System.out.println(transaction);
                    found = true;
                }

            }if(!found){
                System.out.println("Did not find any transaction under vendor: " + vendor);
            }


        }catch(Exception e){
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
        try{
            return LocalDate.parse(s, DATE_FMT);
        }catch(Exception e){
            return null;
        }

    }
    private static LocalTime parseTime(String s) {
        /* TODO – return LocalDate or null */
        try{
            return LocalTime.parse(s, TIME_FMT);
        }catch(Exception e){
            return null;
        }

    }
    private static Double parseDouble(String s) {
        try{
            //need to fix: do not need the round math
            return Math.round(Double.parseDouble(s) * 100.0) / 100.0;
        }
        catch(Exception e) {
            return null;
        }
    }

    /** this method is to print and validate dates logic used for testing */
    private static void printDateForTest(LocalDate startAfterDate,LocalDate endBeforeDate){

        LocalDate startDate = startAfterDate.plusDays(1);
        LocalDate endDate = endBeforeDate.minusDays(1);
        System.out.println("Starts after: "+startAfterDate );
        System.out.println("Ends before: "+ endBeforeDate );
        System.out.println("start date: " + startDate);
        System.out.println("end date: " + endDate);
    }
}