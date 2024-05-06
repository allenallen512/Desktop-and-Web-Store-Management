public class  Products{
    public int productID;
    public String productName;
    public double productPrice;

    public int productQuantity;

    public int productOwner;

//    public int supplierID;
//    public String supplierName;

    public Products() {
        this.productID = 999;
        this.productName = "no name product";
        this.productPrice = 99.99;
        this.productQuantity = 0;
        this.productOwner = 999;
    }

    public Products(int id, String name, double price, int quantity, int owner) {
        this.productID = id;
        this.productName = name;
        this.productPrice = price;
        this.productQuantity = quantity;
        this.productOwner = owner;
    }
}
