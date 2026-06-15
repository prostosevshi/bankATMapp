package bankATM.view;

import bankATM.entity.Account;
import bankATM.entity.DispenseOption;
import bankATM.entity.Transaction;
import bankATM.exception.ATMException;
import bankATM.service.ATMService;
import bankATM.service.DispenseService;
import bankATM.service.TransactionService;
import bankATM.service.TransferService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

public class CLI {

    private static final ATMService atmService = new ATMService();
    private static final TransferService transferService = new TransferService();
    private static final TransactionService transactionService = new TransactionService();
    private static final DispenseService dispenseService = new DispenseService();

    private static void showBalance(Scanner scanner) {
        System.out.println("User id:");
        Long userId = Long.valueOf(scanner.next());

        List<Account> accounts =
                atmService.getBalance(userId);

        System.out.println();
        System.out.println("==Accounts of user " + userId + "==");
        for (Account account : accounts) {
            System.out.println(
                    account.getCurrency()
                            + ": "
                            + account.getBalance()
            );
        }
    }

    private static void deposit(Scanner scanner) {
        System.out.println("User id:");
        Long userId = Long.valueOf(scanner.next());

        System.out.println("Currency:");
        String currency =
                scanner.next().toUpperCase();

        System.out.println("Amount:");
        int amount = scanner.nextInt();

        atmService.deposit(
                userId,
                currency,
                amount
        );

        System.out.println("Deposit successful");
    }

    private static void withdraw(
            Scanner scanner) {

        System.out.println("User id:");
        Long userId =
                scanner.nextLong();

        System.out.println("Currency:");
        String currency =
                scanner.next()
                        .toUpperCase();

        System.out.println("Amount:");
        int amount =
                scanner.nextInt();

        List<DispenseOption> options =
                dispenseService.getWithdrawOptions(
                        currency,
                        amount
                );

        if (options.isEmpty()) {

            System.out.println(
                    "ATM cannot dispense this amount"
            );

            return;
        }

        for (int i = 0; i < options.size(); i++) {

            System.out.println(
                    "\nOption " + (i + 1)
            );

            options.get(i)
                    .getBanknotes()
                    .forEach((denom, qty) ->
                            System.out.println(
                                    denom +
                                            " x " +
                                            qty
                            ));
        }

        System.out.println(
                "\nChoose option:"
        );

        int choice =
                scanner.nextInt();

        DispenseOption selected =
                options.get(choice - 1);

        atmService.withdraw(
                userId,
                currency,
                amount,
                selected
        );

        System.out.println(
                "Withdraw successful"
        );
    }

    private static void transfer(Scanner scanner) {
        System.out.println("From account id:");
        Long fromId = scanner.nextLong();

        System.out.println("To account id:");
        Long toId = scanner.nextLong();

        System.out.println("Amount:");
        BigDecimal amount = scanner.nextBigDecimal();

        transferService.transfer(
                fromId,
                toId,
                amount
        );

        System.out.println("Transfer successful");
    }

    private static void showHistory(Scanner scanner) {
        System.out.println("Account id:");
        Long accountId = scanner.nextLong();

        List<Transaction> history =
                transactionService.getTransactionHistory(accountId);

        if (history.isEmpty()) {
            System.out.println("No transactions");
            return;
        }

        System.out.println();
        System.out.println("==Transactions for account " + accountId + "==");
        for (Transaction tx : history) {

            if ("DEPOSIT".equals(tx.getType())) {

                System.out.println(
                        tx.getCreatedAt()
                                + " | DEPOSIT | +"
                                + tx.getAmount()
                                + " "
                                + tx.getToCurrency()
                );

            } else if ("WITHDRAW".equals(tx.getType())) {

                System.out.println(
                        tx.getCreatedAt()
                                + " | WITHDRAW | -"
                                + tx.getAmount()
                                + " "
                                + tx.getFromCurrency()
                );

            } else {

                System.out.println(
                        tx.getCreatedAt()
                                + " | TRANSFER | "
                                + tx.getAmount()
                                + " "
                                + tx.getFromCurrency()
                                + " -> "
                                + tx.getToCurrency()
                );
            }
            /*System.out.println(
                    tx.getCreatedAt() + " | " +
                            tx.getType() + " | " +
                            tx.getAmount() + " | " +
                            tx.getFromAccountId() + " -> " +
                            tx.getToAccountId()
            );*/
        }
    }

    public static void runInterface() {

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {

            try {

                System.out.println("""
                        === ATM ===
                        1. Show balance
                        2. Deposit
                        3. Withdraw
                        4. Transfer
                        5. Transaction history
                        0. Exit
                        """);

                int choice = scanner.nextInt();

                switch (choice) {

                    case 1:
                        showBalance(scanner);
                        endOfOperationCLI();
                        break;

                    case 2:
                        deposit(scanner);
                        endOfOperationCLI();
                        break;

                    case 3:
                        withdraw(scanner);
                        endOfOperationCLI();
                        break;

                    case 4:
                        transfer(scanner);
                        endOfOperationCLI();
                        break;

                    case 5:
                        showHistory(scanner);
                        endOfOperationCLI();
                        break;

                    case 0:
                        running = false;
                        break;

                    default:
                        System.out.println("Invalid choice");
                        endOfOperationCLI();
                }
            } catch (ATMException e) {

                System.out.println();
                System.out.println(
                        "!!Error: " + e.getMessage() + "!!"
                );
                System.out.println();
            }
        }
    }

    public static void endOfOperationCLI() {
        System.out.println();
        System.out.println("==End of your operation==");
        System.out.println("==Returning to home screen==");
        System.out.println();
        System.out.println();
    }
}
