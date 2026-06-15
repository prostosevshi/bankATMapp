package bankATM.service;

import bankATM.mapper.TransactionMapper;
import bankATM.entity.Transaction;
import bankATM.util.MyBatisUtil;
import org.apache.ibatis.session.SqlSession;

import java.util.List;

public class TransactionService {

    public List<Transaction> getTransactionHistory(Long accountId) {

        SqlSession session =
                MyBatisUtil.getSqlSessionFactory().openSession();

        try {

            TransactionMapper mapper =
                    session.getMapper(TransactionMapper.class);

            return mapper.findByAccountId(accountId);

        } finally {
            session.close();
        }
    }
}
