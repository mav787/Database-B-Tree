import java.sql.*;
import edu.brandeis.cs127b.pa2.latex.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.TreeMap;
import java.util.Collection;
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
			Set<Part> parts = splitParts(arr[1]);
			Map<Supplier,Set<Part>> suppliers = getSupp(parts);
            Purchase p = new Purchase(purchaseNumber);
			for (Supplier supp : suppliers.keySet()){
				Suborder o = new Suborder(supp);
				p.add(o);
				for (Part part : suppliers.get(supp)){
                    o.add(part);
				}
			}
			doc.add(p);
		}	
		System.out.println(doc);
	}

	static Set<Part> splitParts(String ordersStrs){
		Set<Part> parts = new TreeSet<Part>();
		for (String orderStr : ordersStrs.split(",")){
			String[] orderStrArr = orderStr.split("x");
			parts.add(new Part(orderStrArr[1],Integer.parseInt(orderStrArr[0])));
		}
		return parts;
	}





    public static Map<Supplier,Set<Part>> getSupp(Set<Part> parts) throws SQLException{
        Map<Part,Part> quantityMap = new TreeMap<Part,Part>();
        for (Part p : parts){
            quantityMap.put(p,p);
        }
        Map<Supplier,Set<Part>> suppliers = new TreeMap<Supplier,Set<Part>>();
        String query = "SELECT A.ps_partkey, (SELECT ps_suppkey from partsupp AS B WHERE MIN(A.ps_supplycost) = b.ps_supplycost AND b.ps_partkey = A.ps_partkey LIMIT 1) AS cheapest_supplier, MIN(A.ps_supplycost) as cost FROM partsupp AS A WHERE ";
        for (Part part : parts){
            query = String.format("%s ps_partkey = %s OR ", query,part.getNumber());
        }
        query = String.format("%s FALSE GROUP BY ps_partkey", query);
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(query);
        while (rs.next()){
            Supplier supplier = new Supplier(rs.getString(2));
            if (suppliers.get(supplier) == null){
                suppliers.put(supplier, new TreeSet<Part>());
            }
            Part part = quantityMap.get(new Part(rs.getString(1), 0));
            part.setCost(Double.parseDouble(rs.getString(3)));
            suppliers.get(supplier).add(part);
        }
		return suppliers;
	}

	

}
