package bankATM.mapper;

import bankATM.entity.Account;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

public interface AccountMapper {

    BigDecimal getBalance(@Param("id") Long id);

    void withdraw(@Param("id") Long id,
                  @Param("amount") BigDecimal amount);

    void deposit(@Param("id") Long id,
                 @Param("amount") BigDecimal amount);

    Account findByUserIdAndCurrency(@Param("userId") Long userId,
                                    @Param("currency") String currency);

    Account findById(@Param("id") Long id);

    List<Account> findByUserId(@Param("userId") Long userId);
}
