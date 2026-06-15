package bankATM.service;

import bankATM.mapper.ATMCashMapper;
import bankATM.entity.ATMCash;
import bankATM.entity.DispenseOption;
import bankATM.util.MyBatisUtil;
import org.apache.ibatis.session.SqlSession;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DispenseService {

    public List<DispenseOption> getWithdrawOptions(
            String currency,
            int amount) {

        SqlSession session =
                MyBatisUtil.getSqlSessionFactory().openSession();

        try {

            ATMCashMapper cashMapper =
                    session.getMapper(ATMCashMapper.class);

            List<ATMCash> cashList =
                    cashMapper.getCashByCurrency(currency);

            List<DispenseOption> options =
                    new ArrayList<>();

            DispenseOption option1 =
                    buildOption(cashList, amount, 0);

            if (option1 != null) {
                options.add(option1);
            }

            DispenseOption option2 =
                    buildOption(cashList, amount, 1);

            if (option2 != null) {
                options.add(option2);
            }

            return options;

        } finally {
            session.close();
        }
    }

    private DispenseOption buildOption(List<ATMCash> cashList, int amount, int startIndex) {

        Map<Integer, Integer> result =
                new LinkedHashMap<>();

        int remaining = amount;

        for (int i = startIndex; i < cashList.size(); i++) {

            ATMCash cash =
                    cashList.get(i);

            int denomination =
                    cash.getDenomination();

            int available =
                    cash.getQuantity();

            int need =
                    remaining / denomination;

            int take =
                    Math.min(need, available);

            if (take > 0) {

                result.put(
                        denomination,
                        take
                );

                remaining -=
                        denomination * take;
            }
        }

        if (remaining != 0) {
            return null;
        }

        return new DispenseOption(result);
    }
}
