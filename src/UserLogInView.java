import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class UserLogInView extends JFrame {

    private JTextField userIDField;
    private JButton logInButton;

    public UserLogInView() {
        System.out.println("inside of log in view");

        setTitle("User Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        JLabel userIDLabel = new JLabel("Enter User ID:");
        userIDLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center the label horizontally
        userIDField = new JTextField();
        userIDField.setMaximumSize(new Dimension(Short.MAX_VALUE, 2 * userIDField.getFontMetrics(userIDField.getFont()).getHeight())); // used chat gpt to help
        //change the height of the log in input field

        logInButton = new JButton("Log In");
        logInButton.setAlignmentX(Component.CENTER_ALIGNMENT); // Center the button

        mainPanel.add(userIDLabel);

        mainPanel.add(userIDField);


        logInButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                int inputUserID = Integer.parseInt(userIDField.getText());
                boolean result = Application.getInstance().dataAdapter.UserLogIn(inputUserID);

                if (result) {
                    dispose();
                    System.out.println("user logged in. displaying home");
                    Application.getInstance().showHomePageFrame(inputUserID);
                }
                else {
                    JOptionPane.showMessageDialog(UserLogInView.this, "log in failed, try again");
                    userIDField.setText("");
                }
            }
        });
        mainPanel.add(logInButton);

        add(mainPanel);

        setLocationRelativeTo(null);
    }

}
