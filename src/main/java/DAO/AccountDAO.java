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
    public Account getAccountByUsername(String username) { 
        String checkUsernameQuery = "SELECT * FROM account WHERE username = ?";
        PreparedStatement checkUsernameStmt = null;
        ResultSet rs = null;
        
        try {
            checkUsernameStmt = connection.prepareStatement(checkUsernameQuery);
            checkUsernameStmt.setString(1, username);
            rs = checkUsernameStmt.executeQuery();
            
            if (rs.next()) {
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
        
        return null;
    }
    // look up an account by its id, used to validate posted_by on messages
    public Account getAccountById(int accountId) {
        String query = "SELECT * FROM account WHERE account_id = ?";
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = connection.prepareStatement(query);
            stmt.setInt(1, accountId);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return new Account(
                    rs.getInt("account_id"),
                    rs.getString("username"),
                    rs.getString("password")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return null;
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
