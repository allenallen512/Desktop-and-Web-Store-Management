import java.sql.Timestamp;
import java.util.List;

public interface DataAccess {
    public int connect();

    public int disconnect();
    public int getMaxProductId();

//    CRUD for job class

    public boolean createProduct(Products products);

    public Products readProduct(int productID);

    public boolean updateProduct(Products product);

    public Products deleteProduct(int productID);
    public boolean placeOrder(Orders order);


//    CRUD for employee models

    public boolean createCustomer(User customer);

    public int getNewProductID();

    public User readCustomer(int customerID);

    public int getNextCustomerID();

    public boolean updateCustomer(User customer);

    public User deleteCustomer(int customerID);


    public int getNextID();

    public int getNextOrderID();

    public List<String> displayAllProducts();

    public boolean UserLogIn(int userID);



}

