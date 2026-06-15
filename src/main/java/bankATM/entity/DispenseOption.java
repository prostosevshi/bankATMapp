package bankATM.entity;

import java.util.Map;

public class DispenseOption {

    private Map<Integer, Integer> banknotes;

    public DispenseOption(Map<Integer, Integer> banknotes) {
        this.banknotes = banknotes;
    }

    public Map<Integer, Integer> getBanknotes() {
        return banknotes;
    }
}
