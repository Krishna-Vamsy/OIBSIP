package oasis;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ATM {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}

class User {
    private String userId;
    private String pin;
    private double balance;

    public User(String userId, String pin, double balance) {
        this.userId = userId;
        this.pin = pin;
        this.balance = balance;
    }

    public String getUserId() {
        return userId;
    }

    public String getPin() {
        return pin;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}

class Database {
    private static final java.util.Map<String, User> users = new java.util.HashMap<>();

    static {
        // Sample users for testing
        users.put("user1", new User("user1", "1234", 1000.0));
        users.put("user2", new User("user2", "5678", 2000.0));
    }

    public static User getUser(String userId) {
        return users.get(userId);
    }
}

class LoginFrame extends JFrame {
    private JTextField userIdField;
    private JPasswordField pinField;

    public LoginFrame() {
        setTitle("ATM Login");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(3, 2));
        add(panel);

        JLabel userLabel = new JLabel("User ID:");
        panel.add(userLabel);

        userIdField = new JTextField(15);
        panel.add(userIdField);

        JLabel pinLabel = new JLabel("PIN:");
        panel.add(pinLabel);

        pinField = new JPasswordField(15);
        panel.add(pinField);

        panel.add(new JLabel());

        JButton loginButton = new JButton("Login");
        panel.add(loginButton);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userId = userIdField.getText();
                String pin = new String(pinField.getPassword());

                User user = Database.getUser(userId);

                if (user != null && user.getPin().equals(pin)) {
                    dispose();
                    new ATMFrame(user).setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(LoginFrame.this, "Invalid User ID or PIN", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
}

class ATMFrame extends JFrame {
    private User user;

    public ATMFrame(User user) {
        this.user = user;
        setTitle("ATM - Welcome, " + user.getUserId());
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTabbedPane tabbedPane = new JTabbedPane();
        add(tabbedPane);

        TransactionsHistoryPanel transactionsHistoryPanel = new TransactionsHistoryPanel();
        WithdrawPanel withdrawPanel = new WithdrawPanel(user, transactionsHistoryPanel);
        DepositPanel depositPanel = new DepositPanel(user, transactionsHistoryPanel);
        TransferPanel transferPanel = new TransferPanel(user, transactionsHistoryPanel);
        QuitPanel quitPanel = new QuitPanel();

        tabbedPane.addTab("Transactions History", transactionsHistoryPanel);
        tabbedPane.addTab("Withdraw", withdrawPanel);
        tabbedPane.addTab("Deposit", depositPanel);
        tabbedPane.addTab("Transfer", transferPanel);
        tabbedPane.addTab("Quit", quitPanel);
    }
}

class TransactionsHistoryPanel extends JPanel {
    private JTextArea historyTextArea;

    public TransactionsHistoryPanel() {
        setLayout(new BorderLayout());
        historyTextArea = new JTextArea(10, 30);
        historyTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(historyTextArea);
        add(scrollPane, BorderLayout.CENTER);
        updateHistory();
    }

    public void updateHistory() {
        java.util.List<String> transactionHistory = getTransactionHistory();
        StringBuilder historyText = new StringBuilder();
        for (String transaction : transactionHistory) {
            historyText.append(transaction).append("\n");
        }
        historyTextArea.setText(historyText.toString());
    }

    private java.util.List<String> getTransactionHistory() {
        java.util.List<String> transactionHistory = new java.util.ArrayList<>();
        // Simulated transaction history, replace with actual data retrieval
        transactionHistory.add("Withdraw $50");
        transactionHistory.add("Deposit $100");
        transactionHistory.add("Transfer to user2 $30");
        return transactionHistory;
    }
}

class WithdrawPanel extends JPanel {
    private User user;
    private JTextField amountField;
    private TransactionsHistoryPanel transactionsHistoryPanel;

    public WithdrawPanel(User user, TransactionsHistoryPanel transactionsHistoryPanel) {
        this.user = user;
        this.transactionsHistoryPanel = transactionsHistoryPanel;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        JLabel titleLabel = new JLabel("Withdraw Money");
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);
        add(titleLabel);

        add(Box.createVerticalStrut(10));

        JLabel amountLabel = new JLabel("Enter Amount:");
        amountLabel.setAlignmentX(CENTER_ALIGNMENT);
        add(amountLabel);

        amountField = new JTextField(10);
        amountField.setAlignmentX(CENTER_ALIGNMENT);
        add(amountField);

        add(Box.createVerticalStrut(10));

        JButton withdrawButton = new JButton("Withdraw");
        withdrawButton.setAlignmentX(CENTER_ALIGNMENT);
        add(withdrawButton);

        add(Box.createVerticalStrut(10));

        withdrawButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                withdrawAmount();
            }
        });
    }

    private void withdrawAmount() {
        try {
            double amount = Double.parseDouble(amountField.getText());
            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Invalid amount. Please enter a positive value.", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (amount > user.getBalance()) {
                JOptionPane.showMessageDialog(this, "Insufficient funds. Cannot withdraw more than the current balance.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                // Perform the withdrawal (update balance and transaction history)
                user.setBalance(user.getBalance() - amount);
                transactionsHistoryPanel.updateHistory(); // Update the transactions history panel
                JOptionPane.showMessageDialog(this, "Withdrawal successful. Remaining balance: $" + user.getBalance(), "Success", JOptionPane.INFORMATION_MESSAGE);
                amountField.setText(""); // Clear the amount field after successful withdrawal
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid amount. Please enter a valid numeric value.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

class DepositPanel extends JPanel {
    private User user;
    private JTextField amountField;
    private TransactionsHistoryPanel transactionsHistoryPanel;

    public DepositPanel(User user, TransactionsHistoryPanel transactionsHistoryPanel) {
        this.user = user;
        this.transactionsHistoryPanel = transactionsHistoryPanel;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        JLabel titleLabel = new JLabel("Deposit Money");
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);
        add(titleLabel);

        add(Box.createVerticalStrut(10));

        JLabel amountLabel = new JLabel("Enter Amount:");
        amountLabel.setAlignmentX(CENTER_ALIGNMENT);
        add(amountLabel);

        amountField = new JTextField(10);
        amountField.setAlignmentX(CENTER_ALIGNMENT);
        add(amountField);

        add(Box.createVerticalStrut(10));

        JButton depositButton = new JButton("Deposit");
        depositButton.setAlignmentX(CENTER_ALIGNMENT);
        add(depositButton);

        add(Box.createVerticalStrut(10));

        depositButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                depositAmount();
            }
        });
    }

    private void depositAmount() {
        try {
            double amount = Double.parseDouble(amountField.getText());
            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Invalid amount. Please enter a positive value.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                // Perform the deposit (update balance and transaction history)
                user.setBalance(user.getBalance() + amount);
                transactionsHistoryPanel.updateHistory(); // Update the transactions history panel
                JOptionPane.showMessageDialog(this, "Deposit successful. New balance: $" + user.getBalance(), "Success", JOptionPane.INFORMATION_MESSAGE);
                amountField.setText(""); // Clear the amount field after successful deposit
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid amount. Please enter a valid numeric value.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

class TransferPanel extends JPanel {
    private User user;
    private JTextField recipientField;
    private JTextField amountField;
    private TransactionsHistoryPanel transactionsHistoryPanel;

    public TransferPanel(User user, TransactionsHistoryPanel transactionsHistoryPanel) {
        this.user = user;
        this.transactionsHistoryPanel = transactionsHistoryPanel;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        JLabel titleLabel = new JLabel("Transfer Money");
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);
        add(titleLabel);

        add(Box.createVerticalStrut(10));

        JLabel recipientLabel = new JLabel("Recipient User ID:");
        recipientLabel.setAlignmentX(CENTER_ALIGNMENT);
        add(recipientLabel);

        recipientField = new JTextField(10);
        recipientField.setAlignmentX(CENTER_ALIGNMENT);
        add(recipientField);

        add(Box.createVerticalStrut(10));

        JLabel amountLabel = new JLabel("Enter Amount:");
        amountLabel.setAlignmentX(CENTER_ALIGNMENT);
        add(amountLabel);

        amountField = new JTextField(10);
        amountField.setAlignmentX(CENTER_ALIGNMENT);
        add(amountField);

        add(Box.createVerticalStrut(10));

        JButton transferButton = new JButton("Transfer");
        transferButton.setAlignmentX(CENTER_ALIGNMENT);
        add(transferButton);

        add(Box.createVerticalStrut(10));

        transferButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                transferAmount();
            }
        });
    }

    private void transferAmount() {
        try {
            String recipientUserId = recipientField.getText();
            double amount = Double.parseDouble(amountField.getText());

            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Invalid amount. Please enter a positive value.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                User recipient = Database.getUser(recipientUserId);
                if (recipient == null) {
                    JOptionPane.showMessageDialog(this, "Recipient user not found.", "Error", JOptionPane.ERROR_MESSAGE);
                } else if (user.getBalance() < amount) {
                    JOptionPane.showMessageDialog(this, "Insufficient funds. Cannot transfer more than the current balance.", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    // Perform the transfer (update balances and transaction history)
                    user.setBalance(user.getBalance() - amount);
                    recipient.setBalance(recipient.getBalance() + amount);
                    transactionsHistoryPanel.updateHistory(); // Update the transactions history panel
                    JOptionPane.showMessageDialog(this, "Transfer successful. Remaining balance: $" + user.getBalance(), "Success", JOptionPane.INFORMATION_MESSAGE);
                    recipientField.setText(""); // Clear recipient field after successful transfer
                    amountField.setText(""); // Clear amount field after successful transfer
                }
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid amount. Please enter a valid numeric value.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

class QuitPanel extends JPanel {
    public QuitPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        JLabel titleLabel = new JLabel("Quit Operations");
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);
        add(titleLabel);

        add(Box.createVerticalStrut(10));

        JButton quitButton = new JButton("Quit");
        quitButton.setAlignmentX(CENTER_ALIGNMENT);
        add(quitButton);

        add(Box.createVerticalStrut(10));

        quitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                quitOperation();
            }
        });
    }

    private void quitOperation() {
        int option = JOptionPane.showConfirmDialog(this, "Are you sure you want to quit?", "Quit", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            System.exit(0); // Terminate the application
        }
    }
}
