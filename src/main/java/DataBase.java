//STEP 1. Import required packages

import java.sql.*;

public class DataBase {
    private DBPostgres db = DBPostgres.create();
    private StringBuilder sqlQueryBuild = new StringBuilder();

    public void close() {
        db.close();
    }

    public void dropAllTable() {
        String sqlQuery = "DROP TABLE IF EXISTS books, authors, testing, images";
        db.execute(sqlQuery);
    }

    public void createEmptyTable() {
        String sqlQuery = "DROP TABLE IF EXISTS books, authors, testing, images;" +
                "CREATE TABLE IF NOT EXISTS authors (" +
                "id serial PRIMARY KEY," +
                "name VARCHAR(25)" +
                ");" +

                "CREATE TABLE IF NOT EXISTS books (" +
                "id serial PRIMARY KEY," +
                "author_id INT references authors(id), title VARCHAR(100)" +
                ");" +

                "CREATE TABLE IF NOT EXISTS testing(id INT);" +
                "CREATE TABLE IF NOT EXISTS images(id serial, data bytea);";

        db.execute(sqlQuery);
    }

    public void fillTestData() {
        String sqlQuery = "INSERT INTO authors(id, name) VALUES(1, 'Jack London');" +
                "INSERT INTO authors(id, name) VALUES(2, 'Honore de Balzac');" +
                "INSERT INTO authors(id, name) VALUES(4, 'Solo Vova'); " +
                "INSERT INTO authors(id, name) VALUES(3, 'Lion Feuchtwanger');" +
                "INSERT INTO authors(id, name) VALUES(5, 'Truman Capote');" +

                "INSERT INTO books(id, author_id, title) VALUES(1, 1, 'Call of the Wild');" +
                "INSERT INTO books(id, author_id, title) VALUES(2, 1, 'Martin Eden');" +
                "INSERT INTO books(id, author_id, title) VALUES(3, 2, 'Old Goriot');" +
                "INSERT INTO books(id, author_id, title) VALUES(4, 2, 'Cousin Bette');" +
                "INSERT INTO books(id, author_id, title) VALUES(5, 3, 'Jew Suess');" +
                "INSERT INTO books(id, author_id, title) VALUES(6, 4, 'Nana');" +
                "INSERT INTO books(id, author_id, title) VALUES(7, 4, 'The Belly of Paris');" +
                "INSERT INTO books(id, author_id, title) VALUES(8, 5, 'In Cold blood');" +
                "INSERT INTO books(id, author_id, title) VALUES(9, 5, 'Breakfast at Tiffany');";

        db.execute(sqlQuery);
    }

    public void insertAuthor(int id, String name) throws SQLException {
        String sqlQuery = String.format("INSERT INTO authors(id, name) VALUES(%d, \'%s\');", id, name);
        db.execute(sqlQuery);
    }

    public void insertAuthorAdd(int id, String name) throws SQLException {
        String sqlQuery = String.format("INSERT INTO authors(id, name) VALUES(%d, \'%s\');", id, name);
        sqlQueryBuild.append(sqlQuery);
    }

    public void insertAuthorPush() throws SQLException {
        db.execute(sqlQueryBuild.toString());
        sqlQueryBuild = new StringBuilder();
    }


    public void printAuthors() throws SQLException {
        ResultSet rs = db.executeQuery("SELECT * FROM authors");
        if (rs == null) return;
        while (rs.next()) {
            System.out.print(rs.getInt(1));
            System.out.print(": ");
            System.out.println(rs.getString(2));
        }
    }

    public boolean isConnection() {
        return db.isConnection();
    }
}
