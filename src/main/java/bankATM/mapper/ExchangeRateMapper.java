package bankATM.mapper;

import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

public interface ExchangeRateMapper {

    BigDecimal getRate(@Param("from") String from,
                       @Param("to") String to);
}
