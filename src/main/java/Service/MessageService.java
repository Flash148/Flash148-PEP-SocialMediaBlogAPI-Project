package Service;

import DAO.AccountDAO;
import DAO.MessageDAO;
import Model.Message;

import java.util.List;

public class MessageService {
    private MessageDAO messageDAO;
    private AccountDAO accountDAO;

    public MessageService(MessageDAO messageDAO, AccountDAO accountDAO) {
        this.messageDAO = messageDAO;
        this.accountDAO = accountDAO;
    }

    public Message createMessage(Message message) {
        validateMessageText(message.getMessage_text());
        if (accountDAO.getAccountById(message.getPosted_by()) == null) {
            throw new IllegalArgumentException("posted_by must reference an existing account");
        }

        return messageDAO.insertMessage(message);
    }

    public List<Message> getAllMessages() {
        return messageDAO.getAllMessages();
    }

    public Message getMessageById(int messageId) {
        return messageDAO.getMessageById(messageId);
    }

    public List<Message> getMessagesByAccountId(int accountId) {
        return messageDAO.getMessagesByAccountId(accountId);
    }

    public Message deleteMessageById(int messageId) {
        return messageDAO.deleteMessageById(messageId);
    }

    public Message updateMessageText(int messageId, String messageText) {
        validateMessageText(messageText);
        if (messageDAO.getMessageById(messageId) == null) {
            throw new IllegalArgumentException("message_id does not exist");
        }

        return messageDAO.updateMessageText(messageId, messageText);
    }

    private void validateMessageText(String messageText) {
        if (messageText == null || messageText.isBlank()) {
            throw new IllegalArgumentException("message_text cannot be blank");
        }
        if (messageText.length() > 255) {
            throw new IllegalArgumentException("message_text cannot be over 255 characters");
        }
    }
}
