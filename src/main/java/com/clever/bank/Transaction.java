package com.clever.bank;

import lombok.Data;
import org.yaml.snakeyaml.Yaml;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Data
public class Transaction implements Runnable {
    private static int transactionCounter = 0;
    private static final String CHECKS_DIRECTORY = "checks/";

    private int transactionNumber;
    private LocalDateTime transactionDateTime;
    private final Account sourceAccount;
    private final Account destinationAccount;
    private final TransactionType transactionType;
    private double amount;

    public Transaction(Account sourceAccount, Account destinationAccount, double amount, TransactionType transactionType) {
        this.transactionType = transactionType;
        this.transactionNumber = readLastTransactionNumber() + 1;
        this.transactionDateTime = LocalDateTime.now();
        this.sourceAccount = sourceAccount;
        this.destinationAccount = destinationAccount;
        this.amount = amount;
    }

    public Transaction(Account sourceAccount, double amount, TransactionType transactionType) {
        this.transactionType = transactionType;
        this.transactionNumber = ++transactionCounter;
        this.transactionDateTime = LocalDateTime.now();
        this.sourceAccount = sourceAccount;
        this.destinationAccount = null;
        this.amount = amount;
    }
    public static int readLastTransactionNumber() {
        int lastTransactionNumber = 0;

        try {
            Yaml yaml = new Yaml();
            FileReader fileReader = new FileReader("transactions.yml");

            // Load the YAML data from the file
            Map<String, List<Map<String, Object>>> data = yaml.load(fileReader);

            if (data != null && data.containsKey("transactions")) {
                List<Map<String, Object>> transactions = data.get("transactions");

                // Find the last transaction by iterating through the list
                for (Map<String, Object> transactionData : transactions) {
                    int transactionNumber = (int) transactionData.get("transactionNumber");
                    if (transactionNumber > lastTransactionNumber) {
                        lastTransactionNumber = transactionNumber;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return lastTransactionNumber;
    }

    public void executeTransaction() throws IOException {
        synchronized (sourceAccount) {
            if (transactionType == TransactionType.Transfer) {
                synchronized (destinationAccount) {
                    transfer();
                }
            } else if (transactionType == TransactionType.Deposit) {
                depositFunds();
            } else if (transactionType == TransactionType.Withdraw) {

                withdrawFunds();
            }
        }
        printTransactionDetails();

    }

    public void withdrawFunds() {

        if (sourceAccount.getBalance() >= amount) {
            sourceAccount.withdraw(amount);
            System.out.println("Withdraw successful");
        } else {
            System.out.println("Insufficient funds for the transaction");
        }
    }

    public void transfer() {
        Bank sourceBank = sourceAccount.getBank();
        Bank destinationBank = destinationAccount.getBank();

        if (sourceBank == destinationBank) {

            if (sourceAccount.getBalance() >= amount) {
                sourceAccount.withdraw(amount);
                destinationAccount.deposit(amount);
                printTransactionDetails();
            } else {
                System.out.println("Insufficient funds for the transaction");
            }
        } else {


            if (sourceAccount.getBalance() >= amount) {
                sourceAccount.withdraw(amount);
                destinationAccount.deposit(amount * 0.99);
                printTransactionDetails();
            } else {
                System.out.println("Insufficient funds for the transaction");
            }
        }

    }

    public void depositFunds() {

        Account account = Account.findAccountByAccountNumber(sourceAccount.getAccountNumber());

        if (account != null) {

            synchronized (account) {
                account.deposit(amount);
                System.out.println("Deposit successful");
            }
        } else {
            System.out.println("Account not found.");
        }
    }

    private void printTransactionDetails() {
        System.out.println("----------------------------");
        System.out.println("Transaction Number: " + transactionNumber);
        System.out.println("Date and Time: " + transactionDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        System.out.println("Transaction Type: " + transactionType);
        if (transactionType == TransactionType.Transfer) {
            System.out.println("Sender Bank: " + sourceAccount.getBank().getName());
            System.out.println("Receiver Bank: " + destinationAccount.getBank().getName());
            System.out.println("Receiver Account: " + destinationAccount.getAccountNumber());
            System.out.println("Commission: " + (amount * 0.01));
        }
        System.out.println("Sender Account: " + sourceAccount.getAccountNumber());
        System.out.println("Amount: " + amount);
        System.out.println("----------------------------");
        try {
            String checkFileName = CHECKS_DIRECTORY + "check_" + transactionNumber + ".txt";
            FileWriter checkFileWriter = new FileWriter(checkFileName);
            checkFileWriter.write(generateCheck());
            checkFileWriter.close();
            System.out.println("Check saved as: " + checkFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String generateCheck() {
        StringBuilder checkText = new StringBuilder();
        checkText.append("----------------------------\n");
        checkText.append("Transaction Number: ").append(transactionNumber).append("\n");
        checkText.append("Date and Time: ").append(transactionDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)).append("\n");
        checkText.append("Transaction Type: ").append(transactionType).append("\n");

        if (transactionType == TransactionType.Transfer) {
            checkText.append("Sender Bank: ").append(sourceAccount.getBank().getName()).append("\n");
            checkText.append("Receiver Bank: ").append(destinationAccount.getBank().getName()).append("\n");
            checkText.append("Receiver Account: ").append(destinationAccount.getAccountNumber()).append("\n");
            checkText.append("Commission: ").append(String.format(Locale.US, "%.2f", (amount * 0.01))).append("\n");
        }

        checkText.append("Sender Account: ").append(sourceAccount.getAccountNumber()).append("\n");
        checkText.append("Amount: ").append(String.format(Locale.US, "%.2f", amount)).append("\n");
        checkText.append("----------------------------\n");

        return checkText.toString();
    }

    @Override
    public void run() {
        try {
            executeTransaction();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



}


