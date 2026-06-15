package bankATM;

import bankATM.view.CLI;

public class Main {

    public static void main(String[] args) {

        CLI.runInterface();

        /*try (SqlSession session =
                     MyBatisUtil.getSqlSessionFactory().openSession()) {

            UserMapper mapper =
                    session.getMapper(UserMapper.class);

            User user = mapper.findById(1L);

            System.out.println(user.getName());
        }*/


        /*TransferService transfer = new TransferService();

        transfer.transfer(
                1L,
                2L,
                new BigDecimal("100")
        );*/


        /*ATMService atm = new ATMService();

        atm.deposit(1L, "USD", 320);
        atm.withdraw(1L,"USD", 270);*/
    }
}