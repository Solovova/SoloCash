import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException {
        FPostgres fPostgres = new FPostgres();
        fPostgres.connect();
        if (!fPostgres.isConnection()) {
            return;
        }

        //fPostgres.createTable();
        //fPostgres.createEmptyTable();
        //fPostgres.dropAllTable();


//        for (int i = 10; i < 100000; i++) {
//            fPostgres.insertAuthor(i,"Author " + i);
//        }

//        for (int i = 0; i < 10; i++) { //55s
//            for (int j = 0; j < 100; j++) {
//                int id = i*100+j+10;
//                fPostgres.insertAuthorAdd(id,"Author " + id);
//            }
//            fPostgres.insertAuthorPush();
//        }


        fPostgres.printAuthors();
        fPostgres.close();
    }
}
