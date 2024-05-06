import com.hp.gagawa.java.elements.*;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WebServer {

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8500), 0);
        HttpContext root = server.createContext("/");
//        root.setHandler(WebServer::handleRequest);

        HttpContext productSearch = server.createContext("/products/search");
        productSearch.setHandler(WebServer::handleRequestProductSearch);

        server.start();
    }

    private static void handleRequestProductSearch(HttpExchange exchange) throws IOException {

//        search url: http://localhost:8500/products/search?minprice=0&maxprice=100

        String uri = exchange.getRequestURI().toString();
        String[] inputParams = uri.substring(uri.indexOf('?') + 1).split("&");
//        this finds the substring after the question mark, then splits it so that we get the min price and the maxprice

        double minPrice = 0;
        double maxPrice = Double.MAX_VALUE;

//        had to look online for help. splits up the two parameters by the equal sign and gets the values for those.
        for (String bounds : inputParams) {
            String[] keyValue = bounds.split("=");
            if (keyValue.length == 2) {
                if (keyValue[0].equals("minprice")) {
                    minPrice = Double.parseDouble(keyValue[1]);
                } else if (keyValue[0].equals("maxprice")) {
                    maxPrice = Double.parseDouble(keyValue[1]);
                }
            }
        }

        Jedis jedis = new Jedis("redis://default:OibPKUzSUYhd5cwY0gvdRmdeCMtpldVZ@redis-10286.c325.us-east-1-4.ec2.redns.redis-cloud.com:10286");

        Set<String> productKeys = jedis.keys("product:*");
        Set<String> productIDforRange = new HashSet<>(); //get all hash with product: in it's key name

//        putting all the product keys which match the price reqs
        for (String key : productKeys) {
            String currentIDString = jedis.hget(key, "productID");
            String price = jedis.hget(key, "productPrice");

            if (Double.parseDouble(price) >= minPrice && Double.parseDouble(price) <= maxPrice) {
                productIDforRange.add(currentIDString);
            }

        }
        jedis.close();

        StringBuilder responseBuilder = new StringBuilder();
//        had a little help from online to build the HTML page in string format
        responseBuilder.append("<html><body>");
        responseBuilder.append("<p1>Products in Price Range</p1>");

        for (String productID : productIDforRange) {
            System.out.println("Product ID: " + productID);
        }

        for (String productID : productIDforRange) {
            String productDetails = jedis.hget("product:" + productID, "productName");
            productDetails += ", Price: " + jedis.hget("product:" + productID, "productPrice");
            productDetails += ", Quantity: " + jedis.hget("product:" + productID, "productQuantity");
            productDetails += ", Owner: " + jedis.hget("product:" + productID, "productOwner");

            responseBuilder.append("<p>").append(productDetails).append("</p>"); //puts it all in diff "paragraphs"
        }

        responseBuilder.append("</body></html>");//end of the html page
        String response = responseBuilder.toString();


//        ---------from the source document in webserver.zip
        exchange.sendResponseHeaders(200, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }


}
