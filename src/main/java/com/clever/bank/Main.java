package com.clever.bank;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import java.io.*;
import java.util.*;

public class Main {
    static List<User> users;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        users = Collections.synchronizedList(readUsersFromFile());
        InterestCalculator interestCalculator = new InterestCalculator();
        interestCalculator.start();
        while (true) {
            System.out.println("Menu:");
            System.out.println("1. Deposit Funds");
            System.out.println("2. Withdraw Funds");
            System.out.println("3. Make a Transfer");
            System.out.println("4. Exit");

            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    System.out.println("You selected: Deposit Funds");
                    System.out.print("Enter your username: ");
                    String username = scanner.next();
                    System.out.print("Enter your password: ");
                    String password = scanner.next();

                    User user = User.findUserByUsernameAndPassword(username, password);

                    if (user != null) {
                        System.out.println("Accounts for user: " + user.getUsername());
                        for (Account account : user.getAccounts()) {
                            System.out.println("Account Number: " + account.getAccountNumber());
                            System.out.println("Balance: " + account.getBalance());
                        }
                        System.out.print("Enter the account number you want to deposit to: ");
                        String accountNumber = scanner.next();
                        System.out.print("Enter the amount to deposit: ");
                        double amountToDeposit = scanner.nextDouble();

                        // Создаем транзакцию и выполняем ее
                        Transaction depositTransaction = new Transaction(
                                Account.findAccountByAccountNumber(accountNumber),
                                amountToDeposit,
                                TransactionType.Deposit
                        );
                        Thread depositThread = new Thread(depositTransaction);
                        depositThread.start();
                    } else {
                        System.out.println("Invalid username or password.");
                    }
                    saveUsersToFile(users);
                    break;

                case 2:
                    System.out.println("You selected: Withdraw Funds");
                    System.out.print("Enter your username: ");
                    username = scanner.next();
                    System.out.print("Enter your password: ");
                    password = scanner.next();

                    user = User.findUserByUsernameAndPassword(username, password);

                    if (user != null) {
                        System.out.println("Accounts for user: " + user.getUsername());
                        for (Account account : user.getAccounts()) {
                            System.out.println("Account Number: " + account.getAccountNumber());
                            System.out.println("Balance: " + account.getBalance());
                        }
                        System.out.print("Enter the account number you want to withdraw from: ");
                        String accountNumber = scanner.next();
                        System.out.print("Enter the amount to withdraw: ");
                        double amountToWithdraw = scanner.nextDouble();

                        // Создаем транзакцию и выполняем ее
                        Transaction withdrawTransaction = new Transaction(
                                Account.findAccountByAccountNumber(accountNumber),
                                amountToWithdraw,
                                TransactionType.Withdraw
                        );
                        Thread withdrawThread = new Thread(withdrawTransaction);
                        withdrawThread.start();
                    } else {
                        System.out.println("Invalid username or password.");
                    }
                    saveUsersToFile(users);
                    break;

                case 3:
                    System.out.println("You selected: Make a Transaction");
                    System.out.print("Enter your username: ");
                    username = scanner.next();
                    System.out.print("Enter your password: ");
                    password = scanner.next();

                    user = User.findUserByUsernameAndPassword(username, password);

                    if (user != null) {
                        System.out.println("Accounts for user: " + user.getUsername());
                        for (Account account : user.getAccounts()) {
                            System.out.println("Account Number: " + account.getAccountNumber());
                            System.out.println("Balance: " + account.getBalance());
                        }
                        System.out.print("Enter the source account number: ");
                        String sourceAccountNumber = scanner.next();
                        System.out.print("Enter the destination account number: ");
                        String destinationAccountNumber = scanner.next();
                        System.out.print("Enter the amount to transfer: ");
                        double amountToTransfer = scanner.nextDouble();

                        // Создаем транзакцию и выполняем ее
                        Account sourceAccount = Account.findAccountByAccountNumber(sourceAccountNumber);
                        Account destinationAccount = Account.findAccountByAccountNumber(destinationAccountNumber);
                        if (sourceAccount != null && destinationAccount != null) {
                            Transaction transferTransaction = new Transaction(
                                    sourceAccount,
                                    destinationAccount,
                                    amountToTransfer,
                                    TransactionType.Transfer
                            );
                            Thread transferThread = new Thread(transferTransaction);
                            transferThread.start();
                        } else {
                            System.out.println("One or both of the accounts do not exist.");
                        }
                    } else {
                        System.out.println("Invalid username or password.");
                    }
                    saveUsersToFile(users);
                    break;

                case 4:
                    System.out.println("Exiting the program.");
                    scanner.close();
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }

    }

    public static void saveUsersToFile(List<User> users) {
        try {
            FileWriter fileWriter = new FileWriter("users.yml");

            StringBuilder yamlData = new StringBuilder("users:\n"); // Добавляем "users:" в начало файла

            for (User user : users) {
                yamlData.append("- username: ").append(user.getUsername()).append("\n");
                yamlData.append("  password: ").append(user.getPassword()).append("\n");
                yamlData.append("  accounts:\n");

                for (Account account : user.getAccounts()) {
                    yamlData.append("  - accountNumber: ").append(account.getAccountNumber()).append("\n");
                    yamlData.append("    balance: ").append(account.getBalance()).append("\n");
                    yamlData.append("    bank:\n");
                    yamlData.append("      name: ").append(account.getBank().getName()).append("\n");
                }
            }

            fileWriter.write(yamlData.toString());
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }






    public static List<User> readUsersFromFile() {
        try {
            Yaml yaml = new Yaml();
            FileReader fileReader = new FileReader("users.yml");

            // Загружаем данные из файла как Map<String, Object>
            Map<String, Object> data = yaml.load(fileReader);

            // Преобразуем данные в список пользователей
            List<User> users = new ArrayList<>();
            List<Map<String, Object>> usersData = (List<Map<String, Object>>) data.get("users");

            for (Map<String, Object> userData : usersData) {
                User user = new User((String) userData.get("username"), (String) userData.get("password"));
                List<Map<String, Object>> accountsData = (List<Map<String, Object>>) userData.get("accounts");

                for (Map<String, Object> accountData : accountsData) {
                    String accountNumber = (String) accountData.get("accountNumber");
                    double balance = (double) accountData.get("balance");
                    Map<String, Object> bankData = (Map<String, Object>) accountData.get("bank");
                    String bankName = (String) bankData.get("name");

                    Bank bank = new Bank(bankName);
                    Account account = new Account(accountNumber, balance, bank);
                    user.addAccount(account);
                }

                users.add(user);
            }

            return users;
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList(); // Возвращаем пустой список при ошибке чтения
        }
    }
}
