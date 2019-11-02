//STEP 1. Import required packages

import java.sql.*;

public class FPostgres {
    private Connection connection = null;

    //  Database credentials
    static final String DB_URL = "jdbc:postgresql://localhost:5433/CashFlow";
    static final String USER = "cashflow";
    static final String PASS = "vbwqu1pa";

    private StringBuilder insertToTable = new StringBuilder();

    public void connect() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("PostgreSQL JDBC Driver is not found. Include it in your library path ");
            e.printStackTrace();
            return;
        }

        try {
            connection = DriverManager
                    .getConnection(DB_URL, USER, PASS);

        } catch (SQLException e) {
            System.out.println("Connection Failed");
            e.printStackTrace();
            return;
        }

        if (connection != null) {
            System.out.println("You successfully connected to database now");
        } else {
            System.out.println("Failed to make connection to database");
        }
    }

    public boolean isConnection() {
        return connection != null;
    }

    public void close() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    public void createTable() throws SQLException {
        Statement statement = null;
        String createTableSQL = "DROP TABLE IF EXISTS books, authors, testing, images;" +
                "CREATE TABLE IF NOT EXISTS authors (" +
                "id serial PRIMARY KEY," +
                "name VARCHAR(25)" +
                ");" +

                "CREATE TABLE IF NOT EXISTS books (" +
                "id serial PRIMARY KEY," +
                "author_id INT references authors(id), title VARCHAR(100)" +
                ");" +

                "CREATE TABLE IF NOT EXISTS testing(id INT);" +
                "CREATE TABLE IF NOT EXISTS images(id serial, data bytea);" +

                "INSERT INTO authors(id, name) VALUES(1, 'Jack London');" +
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


        try {
            statement = connection.createStatement();
            statement.execute(createTableSQL);
            System.out.println("Table \"dbuser\" is created!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }

    public void getInfo() throws SQLException {
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery("SELECT VERSION()");
        if (rs.next()) {
            System.out.println(rs.getString(1));
        }
    }

    public void insertAuthor(int id, String name) throws SQLException {
        Statement statement = null;
        String insertToTableLocal = String.format("INSERT INTO authors(id, name) VALUES(%d, \'%s\');",id,name);

        //System.out.println(insertToTable);

        try {
            statement = connection.createStatement();
            statement.execute(insertToTableLocal);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }

    public void insertAuthorAdd(int id, String name) throws SQLException {
        String insertToTableRow = String.format("INSERT INTO authors(id, name) VALUES(%d, \'%s\');",id,name);
        insertToTable.append(insertToTableRow);
        //System.out.println(insertToTable);
    }

    public void insertAuthorPush() throws SQLException {
        Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.execute(insertToTable.toString());
            insertToTable = new StringBuilder();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }



    public void printAuthors() throws SQLException {
        PreparedStatement pst = connection.prepareStatement("SELECT * FROM authors");
        ResultSet rs = pst.executeQuery();

        while (rs.next()) {
            System.out.print(rs.getInt(1));
            System.out.print(": ");
            System.out.println(rs.getString(2));
        }
    }
}
