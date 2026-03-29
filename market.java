import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class market
{
    //For storing shop data
    static void addShop(String name,String location,String zone,String phone,String time)
    {
        Map<String,String>shop=new HashMap<>();
        shop.put("name",name);
        shop.put("location",location);
        shop.put("phone",phone);
        shop.put("zone",zone);
        shop.put("time",time);
        SHOPS.add(shop);
    }
    //For storing vegetable data
    static void addVeg(String name,String price,String unit,String shop)
    {
        Map<String,String>veg=new HashMap<>();
        veg.put("name",name);
        veg.put("price",price);
        veg.put("unit",unit);
        veg.put("shop",shop);
        VEGETABLES.add(veg);

    }
    //For shop data
    static final List<Map<String, String>> SHOPS=new ArrayList<>();
    static
    {
        addShop("Papie Shop","Block A19","Zone A","Phone number: 084-323-5089","Open 8:00am-6:00pm");
        addShop("Aoey Shop","Block B08","Zone B","Phone number: 094-685-9598","Open 9:00am-7:00pm");
        addShop("Nok Shop","Block C12","Zone C","Phone number: 063-956-3287","Open 1:00am-11:00am");
        addShop("Aom Shop","Block D15","Zone D","Phone number: 092-636-1830","Open 12:30am-3:00pm");
        addShop("Mok Shop","Block E11","Zone E","Phone number: 065-292-2691","Open 11:30am-9:00pm");
        addShop("Aek Shop","Block F18","Zone F","Phone number: 063-593-5469","Open 6:00am-8:00pm");
    }
    //For vegetable data
    static final List<Map<String,String>> VEGETABLES=new ArrayList<>();
    static
    {
        addVeg("Mushroom","60","kg","Papie Shop");
        addVeg("Spinach","50","kg","Papie Shop");
        addVeg("Cabbage","40","kg","Papie Shop");
        addVeg("Broccoli","80","kg","Aoey Shop");
        addVeg("Kale","20","kg","Aoey Shop");
        addVeg("Bell Pepper","60","kg","Aoey Shop");
        addVeg("Shiitake mushrooms","95","kg","Nok Shop");
        addVeg("Chinese cabbage","50","kg","Nok Shop");
        addVeg("Cilantro","10","bunch","Nok Shop");
        addVeg("water spinach","15","bunch","Sak Shop");
        addVeg("Morning glory","20","bunch","Sak Shop");
        addVeg("Bok choy","30","kg","Sak Shop");
        addVeg("Carrot","40","kg","Mok Shop");
        addVeg("Eggplant","15","ear","Mok Shop");
        addVeg("Zucchini","20","kg","Mok Shop");
        addVeg("Tomato","35","kg","Aek Shop");
        addVeg("Corn","15","ear","Aek Shop");
        addVeg("Cauliflower","25","kg","Aek Shop");
    }
    //For convert list of map to json string "for sending to html"
    static String toJson(List<Map<String, String>> list) 
    {
        StringBuilder sb=new StringBuilder("[");
        //For use loop to iterate through each map in the list
        for (int i=0;i<list.size();i++)
        {
            sb.append("{");
            Map<String,String> m=list.get(i);
            int j=0;
            for (Map.Entry<String,String> e:m.entrySet())//For each entry in the map
            {
                sb.append("\"").append(e.getKey()).append("\":\"").append(e.getValue()).append("\"");
                if (++j<m.size()) sb.append(",");
            }
            sb.append("}");
            if (i+1 <list.size())sb.append(",");
        }
        return sb.append("]").toString();
    }
    //For get query parameter from url
    static String getParam(String query, String key) 
    {
        if (query==null) return "";
        for (String part : query.split("&")) 
        {
            String[] kv=part.split("=",2);
            if (kv.length==2 && kv[0].equals(key)) 
            {
                try { return URLDecoder.decode(kv[1],"UTF-8");}
                catch (Exception e){ return kv[1];}
            }
        }
        return "";
    }
    //For sending json response to html
    static void handle(HttpExchange ex, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
        //For allow html to use the data from java
        ex.getResponseHeaders().add("Access-Control-Allow-Origin","*");
        ex.sendResponseHeaders(200,bytes.length);
        try (OutputStream os=ex.getResponseBody())
        { os.write(bytes);}
    }
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        //For checking the shop name in the query and return to the html
        server.createContext("/shop", ex -> {
            String q = getParam(ex.getRequestURI().getRawQuery(), "name").toLowerCase();
            List<Map<String, String>> result = SHOPS.stream()
                .filter(s -> s.get("name").toLowerCase().contains(q))
                .collect(Collectors.toList());
            handle(ex, toJson(result));
        });
        //For checking the vegetable name in the query and return to the html
        server.createContext("/veg", ex -> {
            String q = getParam(ex.getRequestURI().getRawQuery(), "name").toLowerCase();
            List<Map<String, String>> result = VEGETABLES.stream()
                .filter(v -> v.get("name").toLowerCase().contains(q))
                .collect(Collectors.toList());
            handle(ex, toJson(result));
        });
        //For start the server
        server.setExecutor(null);
        server.start();
    }
}