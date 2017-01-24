package sec.project.database;

import org.h2.tools.RunScript;
import sec.project.Constants;

import java.io.FileReader;
import java.sql.*;

/**
 * Created by Jonatan on 24.1.2017.
 */
public class Database {

    private static Database instance;
    private Connection connection;

    // Forgive me this singleton pattern
    public static Database getInstance(){
        if(instance == null){
            instance = new Database();
        }
        return instance;
    }

    private Database(){
        try {
            openConnection();
            RunScript.execute(connection, new FileReader("sql/database-schema.sql"));
            RunScript.execute(connection, new FileReader("sql/database-import.sql"));
            closeConnection();
        } catch (Exception e) {
            System.out.println("Exception occurred " + e);
        }
    }

    public boolean openConnection(){
        try {
            connection = DriverManager.getConnection(Constants.DB_ADDRESS, "sa", "");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void closeConnection(){
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int insert(String query){
        try{
            PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.executeUpdate();
            ResultSet keys = statement.getGeneratedKeys();
            keys.next();
            int key = keys.getInt(1);
            keys.close();
            statement.close();
            return key;
        } catch (Exception e){
            e.printStackTrace();
        }
        return -1;
    }

    public int safeInsert(String name, String reason) throws SQLException {
        try {
            String query = "INSERT INTO Users (name, reason) VALUES (?, ?)";
            // Enable these options to mitigate vulnerability A4
            // String id = UUID.randomUUID().toString();
            // String query = "INSERT INTO WebUsers (id, name, reason) VALUES (?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            // statement.setString(1, id);
            // statement.setString(2, name);
            // statement.setString(3, reason);
            statement.setString(1, name);
            statement.setString(2, reason);
            statement.executeUpdate();

            ResultSet keys = statement.getGeneratedKeys();
            keys.next();
            int key = keys.getInt(1);
            keys.close();
            statement.close();
            return key;
        } catch(Exception e){
            e.printStackTrace();
        }
        return -1;
    }

    public ResultSet getUser(int id){
        try{
            String query = "SELECT * FROM Users WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ResultSet getUsers(){
        try{
            String query = "SELECT * FROM Users";
            return connection.createStatement().executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
