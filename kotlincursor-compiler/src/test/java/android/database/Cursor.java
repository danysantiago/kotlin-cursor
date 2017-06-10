package android.database;

public interface Cursor {
    int getInt(int columnIndex);
    int getColumnIndexOrThrow(String columnName) throws IllegalArgumentException;
}
