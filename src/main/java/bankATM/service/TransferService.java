package bankATM.service;

import bankATM.mapper.AccountMapper;
import bankATM.mapper.ExchangeRateMapper;
import bankATM.mapper.TransactionMapper;
import bankATM.entity.Account;
import bankATM.entity.Transaction;
import bankATM.exception.ATMException;
import bankATM.util.MyBatisUtil;
import org.apache.ibatis.session.SqlSession;

import java.math.BigDecimal;

public class TransferService {

    public void transfer(Long fromId,
                         Long toId,
                         BigDecimal amount) {

        SqlSession session =
                MyBatisUtil.getSqlSessionFactory().openSession(false);

        try {

            AccountMapper accountMapper =
                    session.getMapper(AccountMapper.class);

            ExchangeRateMapper rateMapper =
                    session.getMapper(ExchangeRateMapper.class);

            TransactionMapper txMapper =
                    session.getMapper(TransactionMapper.class);

            Account from = accountMapper.findById(fromId);
            Account to = accountMapper.findById(toId);

            if (from == null || to == null) {
                throw new ATMException(
                        "Account not found"
                );
            }

            if (from.getId().equals(to.getId())) {
                throw new ATMException(
                        "Cannot transfer to the same account"
                );
            }

            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new ATMException(
                        "Amount must be positive"
                );
            }

            // 1. get rate
            BigDecimal rate;

            if (from.getCurrency().equals(to.getCurrency())) {
                rate = BigDecimal.ONE;
            } else {
                rate = rateMapper.getRate(
                        from.getCurrency(),
                        to.getCurrency()
                );

                if (rate == null) {
                    throw new RuntimeException("No exchange rate");
                }
            }

            // 2. conversion
            BigDecimal convertedAmount =
                    amount.multiply(rate);

            // 3. check balance
            BigDecimal balance = accountMapper.getBalance(fromId);

            if (balance.compareTo(amount) < 0) {
                throw new RuntimeException("Not enough money");
            }

            // 4. withdraw + deposit
            accountMapper.withdraw(fromId, amount);
            accountMapper.deposit(toId, convertedAmount);

            // 5. logging
            Transaction tx = new Transaction();

            tx.setType("TRANSFER");

            tx.setFromAccountId(from.getId());
            tx.setToAccountId(to.getId());

            tx.setAmount(amount);

            tx.setFromCurrency(from.getCurrency());
            tx.setToCurrency(to.getCurrency());
            tx.setExchangeRate(rate);

            txMapper.insert(tx);

            // 6. commit
            session.commit();

        } catch (Exception e) {
            session.rollback();
            throw e;

        } finally {
            session.close();
        }
    }
}
