package db.dataclas;

public class AccountAnsRequest {
    int  id;
    String  name;
    double balance;

    public AccountAnsRequest(int id, String name, double balance)  {
        this.id = id;
        this.name = name;
        this.balance = balance;
    }
}
