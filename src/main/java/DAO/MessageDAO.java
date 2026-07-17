package DAO;

import Model.Message;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessageDAO {
    private Connection connection;

    public MessageDAO() {
        this.connection = Util.ConnectionUtil.getConnection();
    }

    public Message insertMessage(Message message) {
        String query = "INSERT INTO message (posted_by, message_text, time_posted_epoch) VALUES (?, ?, ?)";
        PreparedStatement stmt = null;

        try {
            stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, message.getPosted_by());
            stmt.setString(2, message.getMessage_text());
            stmt.setLong(3, message.getTime_posted_epoch());
            stmt.executeUpdate();

            ResultSet pkeyResultSet = stmt.getGeneratedKeys();
            if (pkeyResultSet.next()) {
                int generatedId = pkeyResultSet.getInt(1);
                return new Message(generatedId, message.getPosted_by(), message.getMessage_text(), message.getTime_posted_epoch());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public List<Message> getAllMessages() {
        String query = "SELECT * FROM message";
        List<Message> messages = new ArrayList<>();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = connection.prepareStatement(query);
            rs = stmt.executeQuery();
            while (rs.next()) {
                messages.add(mapRow(rs));
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

        return messages;
    }

    public Message getMessageById(int messageId) {
        String query = "SELECT * FROM message WHERE message_id = ?";
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = connection.prepareStatement(query);
            stmt.setInt(1, messageId);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
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

    public List<Message> getMessagesByAccountId(int accountId) {
        String query = "SELECT * FROM message WHERE posted_by = ?";
        List<Message> messages = new ArrayList<>();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = connection.prepareStatement(query);
            stmt.setInt(1, accountId);
            rs = stmt.executeQuery();
            while (rs.next()) {
                messages.add(mapRow(rs));
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

        return messages;
    }

    public Message deleteMessageById(int messageId) {
        Message existingMessage = getMessageById(messageId);
        if (existingMessage == null) {
            return null;
        }

        String query = "DELETE FROM message WHERE message_id = ?";
        PreparedStatement stmt = null;

        try {
            stmt = connection.prepareStatement(query);
            stmt.setInt(1, messageId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return existingMessage;
    }

    public Message updateMessageText(int messageId, String messageText) {
        String query = "UPDATE message SET message_text = ? WHERE message_id = ?";
        PreparedStatement stmt = null;

        try {
            stmt = connection.prepareStatement(query);
            stmt.setString(1, messageText);
            stmt.setInt(2, messageId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return getMessageById(messageId);
    }

    private Message mapRow(ResultSet rs) throws SQLException {
        return new Message(
            rs.getInt("message_id"),
            rs.getInt("posted_by"),
            rs.getString("message_text"),
            rs.getLong("time_posted_epoch")
        );
    }
}