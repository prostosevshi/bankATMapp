package bankATM.service;

import bankATM.mapper.ATMCashMapper;
import bankATM.mapper.AccountMapper;
import bankATM.mapper.TransactionMapper;
import bankATM.entity.ATMCash;
import bankATM.entity.Account;
import bankATM.entity.DispenseOption;
import bankATM.entity.Transaction;
import bankATM.exception.ATMException;
import bankATM.util.MyBatisUtil;
import org.apache.ibatis.session.SqlSession;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class ATMService {

    public void withdraw(Long userId, String currency, int amount, DispenseOption option) {

        SqlSession session =
                MyBatisUtil.getSqlSessionFactory().openSession(false);

        try {

            AccountMapper accountMapper =
                    session.getMapper(AccountMapper.class);

            ATMCashMapper cashMapper =
                    session.getMapper(ATMCashMapper.class);

            TransactionMapper txMapper =
                    session.getMapper(TransactionMapper.class);

            Account account =
                    accountMapper.findByUserIdAndCurrency(
                            userId,
                            currency
                    );

            if (account == null) {
                throw new ATMException(
                        "Account not found"
                );
            }

            if (amount <= 0) {
                throw new ATMException(
                        "Amount must be greater than 0"
                );
            }

            BigDecimal withdrawAmount =
                    BigDecimal.valueOf(amount);

            if (account.getBalance()
                    .compareTo(withdrawAmount) < 0) {

                throw new ATMException(
                        "Not enough balance"
                );
            }

            List<ATMCash> cashList =
                    cashMapper.getCashByCurrency(currency);

            // проверяем выбранные купюры

            for (Map.Entry<Integer, Integer> entry :
                    option.getBanknotes().entrySet()) {

                Integer denomination =
                        entry.getKey();

                Integer quantity =
                        entry.getValue();

                ATMCash atmCash =
                        cashList.stream()
                                .filter(c ->
                                        c.getDenomination()
                                                == denomination)
                                .findFirst()
                                .orElseThrow();

                if (atmCash.getQuantity() < quantity) {

                    throw new ATMException(
                            "ATM cash changed"
                    );
                }
            }

            // уменьшаем купюры ATM

            for (Map.Entry<Integer, Integer> entry :
                    option.getBanknotes().entrySet()) {

                Integer denomination =
                        entry.getKey();

                Integer quantity =
                        entry.getValue();

                ATMCash atmCash =
                        cashList.stream()
                                .filter(c ->
                                        c.getDenomination()
                                                == denomination)
                                .findFirst()
                                .orElseThrow();

                cashMapper.decreaseCash(
                        atmCash.getId(),
                        quantity
                );
            }

            // списываем деньги со счёта

            accountMapper.withdraw(
                    account.getId(),
                    withdrawAmount
            );

            // история

            Transaction tx =
                    new Transaction();

            tx.setType("WITHDRAW");

            tx.setFromAccountId(
                    account.getId()
            );

            tx.setAmount(
                    withdrawAmount
            );

            tx.setFromCurrency(
                    currency
            );

            txMapper.insert(tx);

            session.commit();

        } catch (Exception e) {

            session.rollback();

            throw e;

        } finally {

            session.close();
        }
    }

    public void deposit(Long userId, String currency, int amount) {

        SqlSession session =
                MyBatisUtil.getSqlSessionFactory().openSession(false);

        try {

            AccountMapper accountMapper =
                    session.getMapper(AccountMapper.class);

            ATMCashMapper cashMapper =
                    session.getMapper(ATMCashMapper.class);

            // 1. найти аккаунт
            Account account =
                    accountMapper.findByUserIdAndCurrency(userId, currency);

            if (account == null) {
                throw new ATMException(
                        "Account not found"
                );
            }

            if (amount <= 0) {
                throw new ATMException(
                        "Amount must be greater than 0"
                );
            }

            // 2. раскладываем депозит по купюрам
            List<ATMCash> cashList =
                    cashMapper.getCashByCurrency(currency);

            int remaining = amount;

            for (ATMCash cash : cashList) {

                int denom = cash.getDenomination();

                int count = remaining / denom;

                if (count > 0) {

                    cashMapper.increaseCash(cash.getId(), count);
                    remaining -= count * denom;
                }
            }

            if (remaining != 0) {
                throw new ATMException(
                        "Cannot deposit this amount (invalid denominations)"
                );
            }

            // 3. увеличиваем баланс аккаунта
            accountMapper.deposit(account.getId(),
                    new java.math.BigDecimal(amount));


            // 4. record transaction
            TransactionMapper txMapper =
                    session.getMapper(TransactionMapper.class);

            Transaction tx = new Transaction();

            tx.setType("DEPOSIT");

            tx.setFromAccountId(null);
            tx.setToAccountId(account.getId());

            tx.setAmount(BigDecimal.valueOf(amount));

            tx.setFromCurrency(null);
            tx.setToCurrency(currency);

            tx.setExchangeRate(null);

            txMapper.insert(tx);


            session.commit();

        } catch (Exception e) {
            session.rollback();
            throw e;

        } finally {
            session.close();
        }
    }

    public List<Account> getBalance(Long userId) {

        SqlSession session =
                MyBatisUtil.getSqlSessionFactory().openSession();

        try {

            AccountMapper accountMapper =
                    session.getMapper(AccountMapper.class);

            return accountMapper.findByUserId(userId);

        } finally {
            session.close();
        }
    }
}
