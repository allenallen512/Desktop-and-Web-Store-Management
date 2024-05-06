//import com.mysql.cj.x.protobuf.MysqlxPrepare;

import java.io.FileWriter;
import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.*;
import java.sql.Timestamp;

import redis.clients.jedis.Jedis;

public class RedisDataAdaptor implements DataAccess {

    private Connection connection = null;

    @Override
    public int connect() {
        try {
            Jedis jedis = new Jedis("redis://default:OibPKUzSUYhd5cwY0gvdRmdeCMtpldVZ@redis-10286.c325.us-east-1-4.ec2.redns.redis-cloud.com:10286");
        } catch (Exception e) {
            System.out.println("SQLite is not installed. System exits with error!");
            e.printStackTrace();
            System.exit(1);
        }
        return 0;
    }

    @Override
    public int disconnect() {
        try {
            connection.close();
        } catch (SQLException ex) {
            System.out.println("SQLite database cannot be closed. System exits with error!" + ex.getMessage());
        }
        return 0;
    }

//    ----------------------PRODUCT SECTION----------------------------

    public int getNewProductID() {
        try {
            Jedis jedis = new Jedis("redis://default:OibPKUzSUYhd5cwY0gvdRmdeCMtpldVZ@redis-10286.c325.us-east-1-4.ec2.redns.redis-cloud.com:10286");

            int maxProductID = 0;

            Set<String> keys = jedis.keys("product:*");

            for (String key : keys) {
                if (key.startsWith("product:")) {
                    Map<String, String> productDetails = jedis.hgetAll(key);
                    int productID = Integer.parseInt(productDetails.get("productID"));
                    maxProductID = Math.max(maxProductID, productID);
                }
            }
            int returnID = maxProductID + 1;
            jedis.close();

            return returnID;
        } catch (Exception e) {
            System.out.println("Error getting max productID: " + e.getMessage());
            return 0;
        }
    }

    @Override
    public boolean createProduct(Products product) {
        try {

            Jedis jedis = new Jedis("redis://default:OibPKUzSUYhd5cwY0gvdRmdeCMtpldVZ@redis-10286.c325.us-east-1-4.ec2.redns.redis-cloud.com:10286");

            Map<String, String> productDetails = new HashMap<>();
            productDetails.put("productID", String.valueOf(product.productID));
            productDetails.put("productName", product.productName);
            productDetails.put("productPrice", String.valueOf(product.productPrice));
            productDetails.put("productQuantity", String.valueOf(product.productQuantity));
            productDetails.put("productOwner", String.valueOf(product.productOwner));

            String key = "product:" + product.productID;
            jedis.hmset(key, productDetails);

            System.out.println("product created successfully");
            jedis.close();

            return true;
        } catch (Exception ex) {
            System.out.println("Error in creating product: " + ex.getMessage());
            return false;
        }
    }



    @Override
    public Products readProduct(int productID) {
        Jedis jedis = new Jedis("redis://default:OibPKUzSUYhd5cwY0gvdRmdeCMtpldVZ@redis-10286.c325.us-east-1-4.ec2.redns.redis-cloud.com:10286");
        try {
            String key = "product:" + productID;

            if (jedis.exists(key)) {
                String productName = jedis.hget(key, "productName");
                double productPrice = Double.parseDouble(jedis.hget(key, "productPrice"));
                int quantity = Integer.parseInt(jedis.hget(key, "productQuantity"));
                int owner = Integer.parseInt(jedis.hget(key, "productOwner"));
                jedis.close();

                return new Products(productID, productName, productPrice, quantity, owner);
            } else {
                System.out.println("Product ID: " + productID + " not found.");
                jedis.close();

                return null;
            }
        } catch (Exception e) {
            System.out.println("Error reading product: " + e.getMessage());
            return null;
        }
    }

    @Override
    public boolean updateProduct(Products product) {

//currently not updating the product key, only the product elements
        try (Jedis jedis = new Jedis("redis://default:OibPKUzSUYhd5cwY0gvdRmdeCMtpldVZ@redis-10286.c325.us-east-1-4.ec2.redns.redis-cloud.com:10286")) {
            String productKey = "product:" + product.productID;

            if (!jedis.exists(productKey)) {
                System.out.println("Product not found.");
                return false;
            }

            jedis.hset(productKey, "productName", product.productName);
            jedis.hset(productKey, "productPrice", String.valueOf(product.productPrice));
            jedis.hset(productKey, "productQuantity", String.valueOf(product.productQuantity));
            jedis.hset(productKey, "productOwner", String.valueOf(product.productOwner));
            jedis.close();

            return true;
        } catch (Exception e) {
            System.out.println("Error updating product in redis data adaptor: " + e.getMessage());


            return false;
        }
    }


    @Override
    public Products deleteProduct(int productID) {

        try (Jedis jedis = new Jedis("redis://default:OibPKUzSUYhd5cwY0gvdRmdeCMtpldVZ@redis-10286.c325.us-east-1-4.ec2.redns.redis-cloud.com:10286")) {
            String productKey = "product:" + productID;

            if (!jedis.exists(productKey)) {
                System.out.println(" product with ID: " + productID + " does not exist in db");
                jedis.close();

                return null;
            }

            String productName = jedis.hget(productKey, "productName");
            double productPrice = Double.parseDouble(jedis.hget(productKey, "productPrice"));
            int quantity = Integer.parseInt(jedis.hget(productKey, "productQuantity"));
            int owner = Integer.parseInt(jedis.hget(productKey, "productOwner"));
            jedis.del(productKey);
            jedis.close();

            return new Products(productID, productName, productPrice, quantity, owner);
        } catch (Exception e) {
            System.out.println("Something went wrong in the delete product section: " + e.getMessage());
            return null;
        }
    }

    public List<String> displayAllProducts() {
        System.out.println("insid eof display all products");
        List<String> productList = new ArrayList<>();
        try (Jedis jedis = new Jedis("redis://default:OibPKUzSUYhd5cwY0gvdRmdeCMtpldVZ@redis-10286.c325.us-east-1-4.ec2.redns.redis-cloud.com:10286")) {
            int maxID = getMaxProductId();

            for (int i = 1; i <= maxID; i ++) {
                String productKey = "product:" + i;
                String productId = jedis.hget(productKey, "productID");
                String productName = jedis.hget(productKey, "productName");
                String productPrice = jedis.hget(productKey, "productPrice");
                String productQuantity = jedis.hget(productKey, "productQuantity");

                if (Integer.parseInt(productQuantity) <= 0) {
                    continue;
                }

                String productDetails = "Product ID: " + productId + ", Product Name: " + productName +
                        ", Product Price: " + productPrice + ", Product Quantity: " + productQuantity;

                productList.add(productDetails);
            }
        } catch (Exception e) {
            System.out.println("Error getting product details: " + e.getMessage());
        }
        return productList;

    }

    public int getMaxProductId() {
        try (Jedis jedis = new Jedis("redis://default:OibPKUzSUYhd5cwY0gvdRmdeCMtpldVZ@redis-10286.c325.us-east-1-4.ec2.redns.redis-cloud.com:10286")) {
            Set<String> productKeys = jedis.keys("product:*");
            int maxProductId = 0;
            if (productKeys.isEmpty()) {
                return 0;
            } else {
                for (String key : productKeys) {
                    String currentIdString = jedis.hget(key, "productID");
                    int productId = Integer.parseInt(currentIdString);
                    maxProductId = Math.max(maxProductId, productId);
                }
                return maxProductId;
            }
        } catch (Exception e) {
            System.out.println("Error getting max product ID: " + e.getMessage());
            return 0;
        }
    }

    public int getNextOrderID() {
        try (Jedis jedis = new Jedis("redis://default:OibPKUzSUYhd5cwY0gvdRmdeCMtpldVZ@redis-10286.c325.us-east-1-4.ec2.redns.redis-cloud.com:10286")) {
            Set<String> orderKeys = jedis.keys("order:*");

//  check if there are no orders first
            int maxOrderID = 0;
            if (orderKeys.isEmpty()) {
                return 1;
            } else {
                for (String key : orderKeys) {
                    String currentIDString = jedis.hget(key, "orderID");
                    if (currentIDString != null) {
                        int orderID = Integer.parseInt(currentIDString);
                        maxOrderID = Math.max(maxOrderID, orderID);
                    }
                }
                jedis.close();

                return maxOrderID + 1;
            }
        } catch (Exception e) {
            System.out.println("Error getting next order ID: " + e.getMessage());
            return 0;
        }

    }

    public double getProductPrice(int id) {
        try (Jedis jedis = new Jedis("redis://default:OibPKUzSUYhd5cwY0gvdRmdeCMtpldVZ@redis-10286.c325.us-east-1-4.ec2.redns.redis-cloud.com:10286")) {
            String productKey = "product:" + id;
            String priceString = jedis.hget(productKey, "productPrice");
            double price = Double.parseDouble(priceString);
            jedis.close();

            return price;

        } catch (Exception e) {
            System.out.println("error in getting product price" + e.getMessage());
            return 0;
        }

    }

    public boolean placeOrder(Orders order) {
        try (Jedis jedis = new Jedis("redis://default:OibPKUzSUYhd5cwY0gvdRmdeCMtpldVZ@redis-10286.c325.us-east-1-4.ec2.redns.redis-cloud.com:10286")) {

//            here I decrement the quantity of the current product
            String productKey = "product:" + order.productID;
            jedis.hincrBy(productKey, "productQuantity", -1);

            int orderID = getNextOrderID();
            String orderKey = "order:" + orderID;

            jedis.hset(orderKey, "orderID", String.valueOf(orderID));
            jedis.hset(orderKey, "orderDate", String.valueOf(order.OrderDate));
            jedis.hset(orderKey, "productID", String.valueOf(order.productID));
            jedis.hset(orderKey, "customerID", String.valueOf(order.customerID));
            jedis.hset(orderKey, "price", String.valueOf(getProductPrice(order.productID)));
            jedis.hset(orderKey, "creditCard", String.valueOf(order.creditCard));

            System.out.println("order placed");
            return true;
        } catch (Exception e) {
            System.out.println("Error inserting order: " + e.getMessage());
            return false;
        }
    }


    //-----------------------Customer----------------------------//

    public boolean UserLogIn(int userID) {
        try (Jedis jedis = new Jedis("redis://default:OibPKUzSUYhd5cwY0gvdRmdeCMtpldVZ@redis-10286.c325.us-east-1-4.ec2.redns.redis-cloud.com:10286")) {
            String userKey = "user:" + userID;

            if (jedis.exists(userKey)) {
                jedis.close();

                return true;
            } else {
                jedis.close();
                return false;
            }

        } catch (Exception e) {
            System.out.println("error loggin in: " + e.getMessage());
            return false;
        }
    }
//-------------many of these functions are no longer used or needed
    @Override
    public boolean createCustomer(User user) {
        try (Jedis jedis = new Jedis("redis://default:OibPKUzSUYhd5cwY0gvdRmdeCMtpldVZ@redis-10286.c325.us-east-1-4.ec2.redns.redis-cloud.com:10286")) {

            int userID = user.userID;
            String userName = user.userName;
            String userEmail = user.userEmail;
            jedis.hset("user:" + userID, "userID", String.valueOf(userID));
            jedis.hset("user:" + userID, "userName", userName);
            jedis.hset("user:" + userID, "userEmail", userEmail);

            System.out.println("User created");
            return true;
        } catch (Exception e) {
            System.out.println("Error creating user: " + e.getMessage());
            return false;
        }
    }


    @Override
    public User readCustomer(int userID) {
        try (Jedis jedis = new Jedis("redis://default:OibPKUzSUYhd5cwY0gvdRmdeCMtpldVZ@redis-10286.c325.us-east-1-4.ec2.redns.redis-cloud.com:10286")) {

            if (jedis.exists("user:" + userID)) {
                String name = jedis.hget("user:" + userID, "userName");
                String email = jedis.hget("user:" + userID, "userEmail");
                System.out.println("Customer found: " + name + ", " + email);
                return new User(userID, name, email);
            } else {
                System.out.println("user with id: " + userID + " not found");
                return null;
            }
        } catch (Exception e) {
            System.out.println("Error reading customer: " + e.getMessage());
            return null;
        }
    }

    //   ----------------this also needs to be updated still
    public int getNextCustomerID() {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT MAX(CustomerID) AS MaxID FROM customers");
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int maxPaymentID = resultSet.getInt("MaxID");
                return maxPaymentID + 1;
            }
            return 1;
        } catch (Exception e) {
            System.out.println("error getting max int" + e.getMessage());
            return 0;
        }
    }

    @Override
    public boolean updateCustomer(User user) {
        try (Jedis jedis = new Jedis("redis://default:OibPKUzSUYhd5cwY0gvdRmdeCMtpldVZ@redis-10286.c325.us-east-1-4.ec2.redns.redis-cloud.com:10286")) {

            String userKey = "user:" + user.userID;

            if (!jedis.exists(userKey)) {
                System.out.println("User with ID: " + user.userID + " does not exist");
                return false;
            }
            jedis.hset(userKey, "userName", user.userName);
            jedis.hset(userKey, "userEmail", user.userEmail);

            System.out.println("User updated successfully");
            return true;
        } catch (Exception e) {
            System.out.println("Error updating user in redis db adap: " + e.getMessage());
            return false;
        }
    }


    @Override
    public User deleteCustomer(int id) {
        try (Jedis jedis = new Jedis("redis://default:OibPKUzSUYhd5cwY0gvdRmdeCMtpldVZ@redis-10286.c325.us-east-1-4.ec2.redns.redis-cloud.com:10286")) {

            String userKey = "user:" + id;

            if (!jedis.exists(userKey)) {
                System.out.println(" user with ID: " + id + " cannot be found");
                return null;
            }
            String userName = jedis.hget(userKey, "userName");
            String userEmail = jedis.hget(userKey, "userEmail");

            jedis.del(userKey);
            return new User(id, userName, userEmail);
        } catch (Exception e) {
            System.out.println("There was an error in deleting a user: " + e.getMessage());
            return null;
        }
    }


    //----------------------------ORDER SECTION-----------------------------


    public int getNextID() {
        try {
            // Get the maximum payment ID from the orders table
            PreparedStatement statement = connection.prepareStatement("SELECT MAX(ID) AS MaxID FROM orders");
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int maxPaymentID = resultSet.getInt("MaxID");
                return maxPaymentID + 1;
            }
            return 1;
        } catch (Exception e) {
            System.out.println("error getting max int" + e.getMessage());
            return 0;
        }
    }
}


