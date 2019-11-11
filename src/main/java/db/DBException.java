package db;

public class DBException extends Exception {
    DBException(String errorMessage) {
        super(errorMessage);
    }
}
