package db;

import java.sql.*;

public class DBPostgres {
    static private Connection connection = null;
    static private DBPostgres dbPostgres = null;
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/cashflow";
    private static final String USER = "cashflow";
    private static final String PASS = "vbwqu1pa";

    static public DBPostgres create() {
        if (dbPostgres == null) {
            dbPostgres = new DBPostgres();
            dbPostgres.connect();
        }
        return dbPostgres;
    }

    public void connect() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("PostgreSQL JDBC Driver is not found. Include it in your library path ");
            e.printStackTrace();
            return;
        }

        try {
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
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

    public void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void execute(String sqlQuery) {
        if (connection == null) {
            System.out.println("Connection is down!");
            return;
        }
        Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.execute(sqlQuery);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
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

    public ResultSet executeQuery(String sqlQuery){
        PreparedStatement pst = null;
        try {
            pst = connection.prepareStatement(sqlQuery);
            return pst.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getNextID(String table) {
        ResultSet rs = executeQuery(String.format("SELECT max(id) FROM %s;", table));
        try {
            if (rs.next()){
                return rs.getInt(1) + 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
