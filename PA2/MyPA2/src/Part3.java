import java.sql.*;
import edu.brandeis.cs127b.pa2.latex.*;
import java.util.Map;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.Scanner;
import java.util.Set;
public class Part3 {
    static final String JDBC_DRIVER = "com.postgresql.jdbc.Driver";
    static final String DB_TYPE = "postgresql";
    static final String DB_DRIVER = "jdbc";
    static final String DB_NAME = System.getenv("PGDATABASE");
    static final String DB_HOST = System.getenv("PGHOST");
    static final String DB_URL = String.format("%s:%s://%s/%s",DB_DRIVER, DB_TYPE, DB_HOST, DB_NAME);
    static final String DB_USER = System.getenv("PGUSER");
    static final String DB_PASSWORD = System.getenv("PGPASSWORD");
    static Connection conn;

    public static void main(String[] args) throws SQLException{
        conn = DriverManager.getConnection(DB_URL,DB_USER,DB_PASSWORD);
        Scanner in = new Scanner(System.in);
        Document doc = new Document();
        while (in.hasNextLine()){
            String[] arr = in.nextLine().split(":");
            String purchaseNumber = arr[0];

            String[] partsStr = arr[1].split(",");

            // Parse and store part quantity and part number information
            int[] partQuantity = new int[partsStr.length];
            int[] partNumber = new int[partsStr.length];

            for(int i = 0; i < partsStr.length; i++){
                partQuantity[i] = Integer.parseInt(partsStr[i].split("x")[0]);
                partNumber[i] = Integer.parseInt(partsStr[i].split("x")[1]);
            }

            Map<Integer,Set<Part>> suppliers = new HashMap<Integer,Set<Part>>();	

            for(int i = 0; i < partsStr.length; i++){

            	// SQL query to find the supplier with lowest cost given a part number
                String QUERY = "SELECT PS1.ps_suppkey, PS1.ps_supplycost " 
                        + "FROM partsupp AS PS1 "
                        + "WHERE PS1.ps_partkey = " + partNumber[i] + " AND PS1.ps_availqty >= " + partQuantity[i] + " " 
                        + "AND PS1.ps_supplycost <= ALL (SELECT PS2.ps_supplycost "
                        + "FROM partsupp AS PS2 "
                        + "WHERE PS2.ps_partkey = " + partNumber[i] + " AND PS2.ps_availqty >= " + partQuantity[i] + ");";

                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(QUERY);

                // for each partnumber, find the supplier with cheapest price and enough quantity
                // and update the values in hashmap (construct the hashmap)
                while(rs.next()){
                    int suppkey = rs.getInt(1);
                    double supplycost = rs.getDouble(2);

                    Part part = new Part(String.valueOf(partNumber[i]), partQuantity[i]);
                    part.setCost(supplycost);

                    //Supplier supplier = new Supplier(String.valueOf(suppkey));
                    if(!suppliers.containsKey(suppkey)){
                        suppliers.put(suppkey, new TreeSet<Part>());
                    }
                    Set<Part> parts = suppliers.get(suppkey);
                    parts.add(part);
                }
            }


            Purchase p = new Purchase(purchaseNumber);
            for (Integer suppkey : suppliers.keySet()){
            	Supplier supp = new Supplier(String.valueOf(suppkey));
            	Suborder o = new Suborder(supp);
                p.add(o);
                for (Part part : suppliers.get(suppkey)){
                    o.add(part);
                }
            }
            doc.add(p);
        }
        System.out.println(doc);
    }
}
