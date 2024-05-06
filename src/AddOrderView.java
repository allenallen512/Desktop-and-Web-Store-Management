import com.mysql.cj.x.protobuf.MysqlxCrud;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.Appinfo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Timestamp;
import java.util.*;
import java.util.List;
import java.sql.Date;

public class AddOrderView extends JFrame {

    public void setCurrentUser(int current) {
        this.currentUser = current;
    }

    private JTextField EntryID;
    private JButton viewProducts;
    private JTextField creditCardField;

    private JTextField expirationDateField;
    private JTextField cvcCodeField;
    private JTextField orderProductField;
    private JButton addOrder;

    private int currentUser;

    private void displayProductDetails(List<String> productList) {
        JFrame details = new JFrame("Product Details");

        details.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        details.setLayout(new GridLayout(productList.size() + 1, 1)); // Increase the grid size

        JLabel productListLabel = new JLabel("Available Products:");
        details.add(productListLabel);

        for (String product : productList) {
            JLabel productLabel = new JLabel(product);
            details.add(productLabel);
        }

        details.pack();
        details.setVisible(true);
    }

    public AddOrderView() {

//        List<String> allProducts = Application.getInstance().dataAdapter.displayAllProducts();
//        System.out.println(allProducts);

        setTitle("Order Product");
        this.setSize(500, 300);

        this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));

        this.getContentPane().add(new JLabel("Order View"));

//        -----------------here the main is made
        JPanel mainPanel = new JPanel(new GridLayout(3, 1, 5, 5));
//seperate panels for each section. just for UI needs.

        JPanel productIDPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        productIDPanel.add(new JLabel("Product ID:"));
        orderProductField = new JTextField(100); // Adjust the width as needed
        productIDPanel.add(orderProductField);
        mainPanel.add(productIDPanel);

        JPanel creditCardPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        creditCardPanel.add(new JLabel("Credit Card (no spaces): "));
        creditCardField = new JTextField(100);
        creditCardPanel.add(creditCardField);
        mainPanel.add(creditCardPanel);

        JPanel paymentInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel paymentInfoPanel2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        paymentInfoPanel.add(new JLabel("Expiration Date (MM/YY): "));
        expirationDateField = new JTextField(20);
        paymentInfoPanel.add(expirationDateField);

        paymentInfoPanel2.add(new JLabel("CVC Code: "));
        cvcCodeField = new JTextField(10);
        paymentInfoPanel2.add(cvcCodeField);
        mainPanel.add(paymentInfoPanel2);
        mainPanel.add(paymentInfoPanel);

        viewProducts = new JButton("View all products");
        viewProducts.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<String> products = Application.getInstance().dataAdapter.displayAllProducts();
                if (products == null) {
                    JOptionPane.showMessageDialog(null, "no products to show");
                }
                else {
                    displayProductDetails(products);
                }
            }
        });
        mainPanel.add(viewProducts);

        addOrder = new JButton("Buy Product");
        addOrder.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int ProductID = 999;
                String inputProductID = orderProductField.getText().trim();
                if (inputProductID.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please enter a product ID.");
                    return;
                }
                try {
                    ProductID = Integer.parseInt(inputProductID);
                    System.out.println("the input product ID: \n" + ProductID);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid Product ID! Please provide a valid ID.");
                    return;
                }

                String creditCard;
                String cardString = creditCardField.getText().trim();
                String cardCVC = cvcCodeField.getText().trim();
                String expDate = expirationDateField.getText().trim();
                if (cardString.isEmpty() || cardCVC.isEmpty() || expDate.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "enter valid credit card info to place order");
                    return;
                }
                else {
                    creditCard = cardString + "-" + cardCVC + "-" + expDate;
                }

                int OrderID = Application.getInstance().dataAdapter.getNextOrderID();
                Timestamp orderDate = new Timestamp(System.currentTimeMillis());
                int orderCustomer = currentUser;

                Orders newOrder = new Orders(OrderID, orderDate, orderCustomer, ProductID, creditCard);
                boolean res = Application.getInstance().dataAdapter.placeOrder(newOrder);
                if (!res) {
                    JOptionPane.showMessageDialog(null, "Order is NOT saved!");
                } else {
                    JOptionPane.showMessageDialog(null, "Order created!");
                    orderProductField.setText("");
                }
            }
        });
        mainPanel.add(addOrder);

        add(mainPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
    }
}


