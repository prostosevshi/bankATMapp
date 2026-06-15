package bankATM.mapper;

import bankATM.entity.ATMCash;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ATMCashMapper {

    List<ATMCash> getCashByCurrency(@Param("currency") String currency);

    void decreaseCash(@Param("id") Long id,
                      @Param("quantity") int quantity);

    void increaseCash(@Param("id") Long id,
                      @Param("quantity") int quantity);
}
