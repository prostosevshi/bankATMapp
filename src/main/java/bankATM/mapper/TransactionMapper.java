package bankATM.mapper;

import bankATM.entity.Transaction;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TransactionMapper {

    void insert(Transaction transaction);

    List<Transaction> findByAccountId(@Param("accountId") Long accountId);
}
