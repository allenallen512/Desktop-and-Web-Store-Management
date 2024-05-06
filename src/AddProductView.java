import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;

public class AddProductView extends JFrame {

    public void setCurrentUser(int current) {
        this.currentUser = current;
    }

    private JTextField ProductsIDField;
    private JTextField ProductsNameField;
    private JTextField ProductsPriceField;
    private JTextField ProductsOwnerField;

    private JTextField ProductsQuantityField;
    private JButton addProducts;
    private JButton updateProducts;
    private JButton readButton;
    private JButton deleteButton;
    private int currentUser;

    private void displayProductsDetails(Products Products){


        JFrame details = new JFrame("Job Details");


        details.setSize(300,200);
        details.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        details.setLayout(new GridLayout(4,1));

        JLabel idLabel = new JLabel("ID: " + Products.productID);
        JLabel nameLabel = new JLabel("Product Name: " + Products.productName);
        JLabel priceLabel = new JLabel("Product Price: " +  Products.productPrice);
        JLabel supplierLabel = new JLabel("Product Owner: " + Products.productOwner);
        JLabel quantity = new JLabel("Product Quantity: " + Products.productQuantity);


        details.add(idLabel);
        details.add(nameLabel);
        details.add(priceLabel);
        details.add(supplierLabel);
        details.add(quantity);

        details.setVisible(true);
    }

    public AddProductView() {


        setTitle("Add Products Class");
        this.setSize(500, 300);

        this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));

        this.getContentPane().add(new JLabel ("Products View"));

//        JPanel main = new JPanel(new SpringLayout());
        JPanel main = new JPanel(new GridLayout(5,2,5,5));

        main.add(new JLabel("Product ID: "));
        ProductsIDField= new JTextField();
        main.add(ProductsIDField);

        main.add(new JLabel("Product Name: "));
        ProductsNameField = new JTextField();
        main.add(ProductsNameField);

        main.add(new JLabel("Product Price  "));
        ProductsPriceField = new JTextField();
        main.add(ProductsPriceField);

        main.add(new JLabel("Product Quantity  "));
        ProductsQuantityField = new JTextField();
        main.add(ProductsQuantityField);

        main.add(new JLabel("Product Owner ID"));
        ProductsOwnerField = new JTextField();
        main.add(ProductsOwnerField);



        addProducts = new JButton("Add Product for Sale");
        addProducts.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int inputProductsID = 999;
                String productsIDText = ProductsIDField.getText().trim();
                if (productsIDText.isEmpty()) {
                    inputProductsID = Application.getInstance().dataAdapter.getNewProductID();
                    System.out.println("the new ID is: " + inputProductsID);
                } else {
                    try {
                        inputProductsID = Integer.parseInt(productsIDText);
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(null, "Invalid Products ID! Please provide a valid ID!");

                    }
                }

                String inputProductName = ProductsNameField.getText().trim();
                if (inputProductName.length() == 0) {
                    JOptionPane.showMessageDialog(null, "enter a valid Products name");
                    return;
                }

                double inputPrice = 0;
                try {
                    inputPrice = Double.parseDouble(ProductsPriceField.getText());
                }
                catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "invalid price entered");
                    return;

                }

                int inputQuantity  = 0;
                try {
                    inputQuantity = Integer.parseInt(ProductsQuantityField.getText());
                }
                catch(NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "invalid quantity entered");
                    return;

                }

                int ownerID = currentUser;
                try {
                    ownerID = Integer.parseInt(ProductsOwnerField.getText());
                }         catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "nothing entered for owner, using current user");
                }

                Products Product = new Products(inputProductsID, inputProductName, inputPrice, inputQuantity, ownerID);

                boolean res = Application.getInstance().dataAdapter.createProduct(Product);
                if (!res) {
                    JOptionPane.showMessageDialog(null, "Products is NOT saved!");
                }
                else {
                    JOptionPane.showMessageDialog(null, "Products created!");
                }


            }
        });

        updateProducts = new JButton("Update Product");
        updateProducts.addActionListener(new ActionListener() {
             @Override
             public void actionPerformed(ActionEvent e) {
//                 product ID is the only field that has to be filled
                 int inputProductsID = 999;
                 try {
                     inputProductsID = Integer.parseInt(ProductsIDField.getText());
                 } catch (NumberFormatException ex) {
                     JOptionPane.showMessageDialog(null, "Invalid Products ID! Please provide a valid ID!");
                     return;
                 }

                 String inputProductName = ProductsNameField.getText().trim();

                 double inputPrice = -1; // Default value
                 if (!ProductsPriceField.getText().trim().isEmpty()) {
                     try {
                         inputPrice = Double.parseDouble(ProductsPriceField.getText());
                     } catch (NumberFormatException ex) {
                         JOptionPane.showMessageDialog(null, "Invalid price entered");

                     }
                 }
                 int inputQuantity = -1;

                 if (!ProductsQuantityField.getText().trim().isEmpty()) {
                     try {
                         inputQuantity = Integer.parseInt(ProductsQuantityField.getText());
                     }
                     catch(NumberFormatException ex) {
                         JOptionPane.showMessageDialog(null, "invalid update quantity entered");
                     }
                 }

                 int inputOwnerID = -1; // Default value
                 if (!ProductsOwnerField.getText().trim().isEmpty()) {
                     try {
                         inputOwnerID = Integer.parseInt(ProductsOwnerField.getText());
                     } catch (NumberFormatException ex) {
                         JOptionPane.showMessageDialog(null, "Invalid owner ID");

                     }
                 }

                 Products toBeUpdated = Application.getInstance().dataAdapter.readProduct(inputProductsID);

                 // use the current ID's value if the field is left blank
                 if (inputProductName.isEmpty()) {
                     inputProductName = toBeUpdated.productName;
                 }
                 if (inputPrice == -1) {
                     inputPrice = toBeUpdated.productPrice;
                 }
                 if (inputOwnerID== -1) {
                     inputOwnerID = toBeUpdated.productOwner;
                 }
                 if (inputQuantity == -1) {
                     inputQuantity = toBeUpdated.productQuantity;
                 }

                 Products product = new Products(inputProductsID, inputProductName, inputPrice, inputQuantity, inputOwnerID);

                 boolean res = Application.getInstance().dataAdapter.updateProduct(product);
                 if (!res) {
                     JOptionPane.showMessageDialog(null, "Products is NOT updated!");
                 } else {
                     JOptionPane.showMessageDialog(null, "Products updated!");
                 }

             }
         });

        readButton = new JButton("Read Products");
        readButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int id = 999;
                try {
                    id = Integer.parseInt(ProductsIDField.getText());
                }
                catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid Products ID! Please provide a valid ID!");
                    return;
                }

                Products result = Application.getInstance().dataAdapter.readProduct(id);
                if (result == null) {
                    JOptionPane.showMessageDialog(null, "Products with id: " + id + " was not found");
                }
                else {
                    displayProductsDetails(result);
                }

            }
        });

        deleteButton = new JButton("Delete Products");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int id = 999;
                try {
                    id = Integer.parseInt(ProductsIDField.getText());
                }
                catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid Products ID! Please provide a valid ID!");
                    return ;
                }

                Products result = Application.getInstance().dataAdapter.deleteProduct(id);
                if (result == null) {
                    JOptionPane.showMessageDialog(null, "Products with id: " + id + " was not found");
                }
                else {
                    JOptionPane.showMessageDialog(null, "Products Deleted");
                }


            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
//        SpringUtilities.makeCompactGrid(main,
//                3, 2, //rows, cols
//                6, 6,        //initX, initY
//                6, 6);       //xPad, yPad
        buttonPanel.add(addProducts);
        buttonPanel.add(updateProducts);
        buttonPanel.add(readButton);
        buttonPanel.add(deleteButton);

        add(main, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(null);


    }
}
