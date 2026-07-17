package Controller;
import io.javalin.Javalin;
import io.javalin.http.Context;
import DAO.AccountDAO;
import DAO.MessageDAO;
import Model.Account;
import Model.Message;
import Service.AccountService;
import Service.DuplicateUsernameException;
import Service.MessageService;
import Service.UnauthorizedLoginException;
/**
 * TODO: You will need to write your own endpoints and handlers for your controller. The endpoints you will need can be
 * found in readme.md as well as the test cases. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
public class SocialMediaController {
    /**
     * In order for the test cases to work, you will need to write the endpoints in the startAPI() method, as the test
     * suite must receive a Javalin object from this method.
     * @return a Javalin app object which defines the behavior of the Javalin controller.
     */
    
    private AccountService accountService;
    private MessageService messageService;
    
        public SocialMediaController() {
            this.accountService = new AccountService(null);
            this.messageService = new MessageService(new MessageDAO(), new AccountDAO());
    }

    public Javalin startAPI() {
        Javalin app = Javalin.create();
        app.post("/register", this::registerHandler);
        app.post("/login", this::loginHandler);
        app.post("/messages", this::createMessageHandler);
        app.get("/messages", this::getAllMessagesHandler);
        app.get("/messages/{message_id}", this::getMessageByIdHandler);
        app.delete("/messages/{message_id}", this::deleteMessageHandler);
        app.patch("/messages/{message_id}", this::updateMessageHandler);
        app.get("/accounts/{account_id}/messages", this::getMessagesByAccountHandler);

        return app;
    }

    /**
     * This is an example handler for an example endpoint.
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     */
    private void exampleHandler(Context context) {
        context.json("sample text");
    }

    private void registerHandler(Context context) {
        try {
            // read incoming JSON and convert to Account object
            Account account = context.bodyAsClass(Account.class);
            // Attempt registration via the service layer
            Account registeredAccount = accountService.register(account);
    
            if (registeredAccount != null) {
                context.json(registeredAccount); // Automatic serialization to JSON (200 OK)
            } else {
                context.status(400);
            }
        } catch (DuplicateUsernameException | IllegalArgumentException e) {
            context.status(400); // 400 Bad Request for validation failures
        }
    }
    
    private void loginHandler(Context context) {
        try {
            Account account = context.bodyAsClass(Account.class);
            Account loggedInAccount = accountService.login(account);
    
            // if login successful, return full account object
            if (loggedInAccount != null) {
                context.json(loggedInAccount); // 200 OK
            } else {
                context.status(401); // 401 Unauthorized
            }
        } catch (IllegalArgumentException | UnauthorizedLoginException e) {
            context.status(401); // 401 Unauthorized for invalid credentials
        }
    }

    private void createMessageHandler(Context context) {
        try {
            Message message = context.bodyAsClass(Message.class);
            Message createdMessage = messageService.createMessage(message);
            context.json(createdMessage);
        } catch (IllegalArgumentException e) {
            context.status(400);
        }
    }

    private void getAllMessagesHandler(Context context) {
        context.json(messageService.getAllMessages());
    }

    private void getMessageByIdHandler(Context context) {
        int messageId = Integer.parseInt(context.pathParam("message_id"));
        Message message = messageService.getMessageById(messageId);
        if (message != null) {
            context.json(message);
        }
    }

    private void deleteMessageHandler(Context context) {
        int messageId = Integer.parseInt(context.pathParam("message_id"));
        Message deletedMessage = messageService.deleteMessageById(messageId);
        if (deletedMessage != null) {
            context.json(deletedMessage);
        }
    }

    private void updateMessageHandler(Context context) {
        try {
            int messageId = Integer.parseInt(context.pathParam("message_id"));
            Message messageUpdate = context.bodyAsClass(Message.class);
            Message updatedMessage = messageService.updateMessageText(messageId, messageUpdate.getMessage_text());
            context.json(updatedMessage);
        } catch (IllegalArgumentException e) {
            context.status(400);
        }
    }

    private void getMessagesByAccountHandler(Context context) {
        int accountId = Integer.parseInt(context.pathParam("account_id"));
        context.json(messageService.getMessagesByAccountId(accountId));
    }

}