package wss;

public class AccountAnsRequest {
    int  id;
    String  name;
    double balance;

    AccountAnsRequest(int id, String name, double balance)  {
        this.id = id;
        this.name = name;
        this.balance = balance;
    }
}
