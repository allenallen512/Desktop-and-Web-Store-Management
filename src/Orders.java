import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Date;


public class Orders {

    public int OrderID;
    public Timestamp OrderDate;
    public int customerID;
    public int productID;

    public String creditCard;



    public Orders() {
        this.OrderID = 999;
        this.OrderDate = null;
        this.customerID = 999;
        this.productID = 999;
        this.creditCard = "x";
    }

    public Orders(int order, Timestamp time,int customer, int product, String credit){
        this.OrderID = order;
        this.OrderDate = time;
        this.customerID = customer;
        this.productID = product;
        this.creditCard = credit;
    }

}
