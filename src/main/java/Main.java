import db.DBMain;
import test.TestData;

public class Main {
    public static void main(String[] args) {
        DBMain dbMain = new DBMain();
        if (!dbMain.isConnection()) {
            return;
        }

        TestData.fillTestData(dbMain);

        dbMain.close();
    }
}
