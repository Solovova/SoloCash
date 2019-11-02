import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException {
        DataBase DataBase = new DataBase();
        if (!DataBase.isConnection()) {
            return;
        }

        DataBase.createEmptyTable();
        //DataBase.dropAllTable();

        for (int i = 0; i < 1000; i++) { //55s
            for (int j = 0; j < 100; j++) {
                int id = i*100+j;
                DataBase.insertAuthorAdd(id,"Author " + id);
            }
            DataBase.insertAuthorPush();
        }
        DataBase.printAuthors();
        DataBase.close();
    }
}
