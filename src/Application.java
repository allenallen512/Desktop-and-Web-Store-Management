import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Application {
    public DataAccess dataAdapter = new RedisDataAdaptor();
    public AddOrderView orderView = new AddOrderView();
    public AddProductView productView = new AddProductView();

    public UserLogInView login = new UserLogInView();

    private static Application instance;   // Singleton pattern

    public static Application getInstance() {
        if (instance == null) {
            instance = new Application();
        }
        return instance;
    }


    //used online resources to see how I could make everythign appear in one window instead of seperate windows.

    public void showHomePageFrame(int currentUser) {

        System.out.println("Hello world!");
        Application application = Application.getInstance();
//        application.dataAdapter.connect();

//        application.employeeView.setVisible(true);

        JFrame homepageFrame = new JFrame("Homepage");
        homepageFrame.setSize(500, 300);
        homepageFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        homepageFrame.setLayout(new FlowLayout());

        // Button for Employee
        JButton employeeButton = new JButton("Click to Add Product for Sale");
        employeeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                application.productView.setCurrentUser(currentUser);
                application.productView.setVisible(true);

            }
        });
        homepageFrame.add(employeeButton);

        JButton orderButton = new JButton("Click to Order Product");
        orderButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                application.orderView.setCurrentUser(currentUser);
                application.orderView.setVisible(true);

            }
        });
        homepageFrame.add(orderButton);

        homepageFrame.setVisible(true);

        homepageFrame.setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        System.out.println("Hello world!");
        Application application = Application.getInstance();
        application.dataAdapter.connect();

        application.login.setVisible(true);
//        JFrame homepageFrame = new JFrame("Homepage");
//        homepageFrame.setSize(500, 300);
//        homepageFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        homepageFrame.setLayout(new FlowLayout());
//
//
//        homepageFrame.setVisible(true);

    }

}
