package Service;
import DAO.AccountDAO;
import Model.Account;

public class AccountService {
        private AccountDAO accountDAO;

        public AccountService() {
            this.accountDAO = new AccountDAO();
        }

        public AccountService(AccountDAO accountDAO) {
            this.accountDAO = accountDAO;
        }

        public Account register(Account account) {
            //1. Validation check (Throws IllegalArgumentException for 400 errors)
            if (account.getUsername() == null || account.getUsername().isBlank()) {
                throw new IllegalArgumentException("Username cannot be blank");
            }
            if (account.getPassword() == null || account.getPassword().isBlank() || account.getPassword().length() < 4) {
                throw new IllegalArgumentException("Password must be at least 4 characters long");
            }
            //Duplicate check
            if (accountDAO.getAccountByUsername(account.getUsername()) != null) {
                throw new DuplicateUsernameException("Username alreadys exists");
            }

            return accountDAO.insertAccount(account.getUsername(), account.getPassword());
        }

         public Account login(Account account) {
            // Retrieve the account from the database
            Account existingAccount = accountDAO.getAccountByUsername(account.getUsername());

            // Check if the account exists and the password matches
            if (existingAccount != null && existingAccount.getPassword().equals(account.getPassword())) {
                return existingAccount; // Successful login
            } else {
                throw new UnauthorizedLoginException("Invalid username or password");
            }
        }


}
