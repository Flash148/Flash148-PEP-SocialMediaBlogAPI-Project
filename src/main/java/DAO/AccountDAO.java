package DAO;
import Model.Account;
import java.sql.*;


public class AccountDAO {
    // connection to the database
    private Connection connection;

    public AccountDAO() {
        this.connection = Util.ConnectionUtil.getConnection();
    }

    //handle user registration
    // 1. Return type changed to Account
    public Account getAccountByUsername(String username) { 
        String checkUsernameQuery = "SELECT * FROM account WHERE username = ?";
        PreparedStatement checkUsernameStmt = null;
        ResultSet rs = null;
        
        try {
            checkUsernameStmt = connection.prepareStatement(checkUsernameQuery);
            checkUsernameStmt.setString(1, username);
            rs = checkUsernameStmt.executeQuery();
            
            if (rs.next()) {
                // 2. Read from DB and pass directly into the Account constructor
                Account account = new Account(
                    rs.getInt("account_id"),
                    rs.getString("username"),
                    rs.getString("password")
                );
                return account; // Return the found account
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // close resources
            try {
                if (rs != null) rs.close();
                if (checkUsernameStmt != null) checkUsernameStmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        return null; // 3. Return null if no account matches the username
    }
    // insert new account into the database 
    public Account insertAccount(String username, String password) {
        String insertQuery = "INSERT INTO account (username, password) VALUES (?, ?)";
        PreparedStatement insertStmt = null;


        try {
            insertStmt = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
            insertStmt.setString(1, username);
            insertStmt.setString(2, password);
            insertStmt.executeUpdate();
            
            ResultSet pkeyResultSet = insertStmt.getGeneratedKeys();
            if (pkeyResultSet.next()) {
                int generatedId = pkeyResultSet.getInt(1);
                return new Account(generatedId, username, password);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (insertStmt != null) insertStmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
