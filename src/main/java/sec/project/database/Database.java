package sec.project.database;

import org.h2.tools.RunScript;
import sec.project.Constants;

import java.io.FileReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
            Connection connection = DriverManager.getConnection(Constants.DB_ADDRESS, "sa", "");
            RunScript.execute(connection, new FileReader("sql/database-schema.sql"));
            RunScript.execute(connection, new FileReader("sql/database-import.sql"));
            ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM WebUsers");

            System.out.println("Initialized database with the following users:");
            while(resultSet.next()){
                String id = resultSet.getString("id");
                String name = resultSet.getString("name");
                System.out.println(id + "\t" + name + "\t");
            }
            resultSet.close();
            closeConnection();
        } catch (Exception e) {
            System.out.println("Exception occurred " + e);
        }
    }

    private boolean openConnection(){
        try {
            connection = DriverManager.getConnection(Constants.DB_ADDRESS, "sa", "");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void closeConnection(){
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void execute(String query){
        try{
            if(openConnection()){
                connection.createStatement().executeUpdate(query);
            }
            closeConnection();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public List<String> fetch(String query){
        List<String> results = new ArrayList<>();
        try{
            if(openConnection()){
                ResultSet resultSet = connection.createStatement().executeQuery(query);
                ResultSetMetaData metaData = resultSet.getMetaData();
                int columns = metaData.getColumnCount();
                while(resultSet.next()){
                    StringBuilder properties = new StringBuilder();
                    for(int i=1; i <= columns; i++){
                        System.out.println("Column : " + i);
                        if(!Constants.SENSITIVE_FIELD_IDS.contains(i)){
                            System.out.println("string: " + resultSet.getString(i));
                            properties.append("\t").append(resultSet.getString(i));
                        }
                    }
                    results.add(properties.toString());
                }
                return results;
            }
            closeConnection();
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
