package db;

import java.sql.*;

public class DBPostgres {
    static private Connection connection = null;
    static private DBPostgres dbPostgres = null;
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/cashflow";
    private static final String USER = "cashflow";
    private static final String PASS = "vbwqu1pa";
    private PreparedStatement pst;
    private StringBuilder packetSQLQuery = new StringBuilder();

    static DBPostgres create() {
        if (dbPostgres == null) {
            dbPostgres = new DBPostgres();
            dbPostgres.connect();
        }
        return dbPostgres;
    }

    private void connect() {
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

    boolean isConnection() {
        return connection != null;
    }

    void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
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

    int getNextID(String table) {
        ResultSet rs = executeQuery(String.format("SELECT max(id) FROM %s;", table));
        try {
            if (rs.next()) {
                return rs.getInt(1) + 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    boolean isTable(String name) {
        return executeQueryIsRows(String.format("SELECT TABLE_NAME FROM cashflow.INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = \'%s\';", name));
    }

    int getIDFromTableByFiler(String table, String fieldName, String name) {
        ResultSet rs = dbPostgres.executeQuery(String.format("SELECT id FROM %s WHERE %s='%s';", table, fieldName, name));
        try {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    boolean isIDInTable(String table, int id) {
        return dbPostgres.executeQueryIsRows(String.format("SELECT id FROM %s WHERE id=%d;", table, id));
    }

    boolean isStrFieldInTableByFilter(String table, String field, String value) {
        return dbPostgres.executeQueryIsRows(String.format("SELECT %s FROM %s WHERE %s='%s';", field, table, field, value));
    }

    public ResultSet getRowByIDFromTable(String table, int id, String row) {
        return dbPostgres.executeQuery(String.format("SELECT %s FROM %s WHERE id =%d;", row, table, id));
    }


    // base functions
    public ResultSet executeQuery(String sqlQuery) {
        try {
            if (pst != null) pst.close();
            pst = connection.prepareStatement(sqlQuery);
            return pst.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean executeQueryIsRows(String sqlQuery) {
        try {
            return executeQuery(sqlQuery).next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    void executeSimple(String sqlQuery) throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute(sqlQuery);
        statement.close();
    }

    void setPacketSQLQueryAdd(String sqlQuery) {
        packetSQLQuery.append(sqlQuery);
    }

    void setPacketSQLQueryExecute() throws SQLException {
        executeSimple(packetSQLQuery.toString());
        packetSQLQuery = new StringBuilder();
    }
}
