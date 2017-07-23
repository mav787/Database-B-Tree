import java.sql.*;
import edu.brandeis.cs127b.pa2.graphviz.*;
public class Part2 {
	static final String JDBC_DRIVER = "com.postgresql.jdbc.Driver";
	static final String DB_TYPE = "postgresql";
	static final String DB_DRIVER = "jdbc";
	static final String DB_NAME = System.getenv("PGDATABASE");
	static final String DB_HOST = System.getenv("PGHOST");
	static final String DB_URL = String.format("%s:%s://%s/%s",DB_DRIVER, DB_TYPE, DB_HOST, DB_NAME);
	static final String DB_USER = System.getenv("PGUSER");
	static final String DB_PASSWORD = System.getenv("PGPASSWORD");

	static final String TRADE_RELATIONS = "SELECT customer_region.r_name AS customer_region_name, supplier_region.r_name AS supplier_region_name, SUM(l_extendedprice*(1-l_discount)*(1+l_tax)) AS trading_total FROM orders, lineitem, supplier, customer, nation as supplier_nation, nation AS customer_nation, region as supplier_region, region as customer_region WHERE o_orderkey = l_orderkey AND o_custkey = c_custkey AND l_suppkey = s_suppkey AND s_nationkey = supplier_nation.n_nationkey AND c_nationkey = customer_nation.n_nationkey AND supplier_nation.n_regionkey = supplier_region.r_regionkey AND customer_nation.n_regionkey = customer_region.r_regionkey GROUP BY customer_region.r_name, supplier_region.r_name ORDER BY trading_total DESC";
	public static void main(String[] args) throws SQLException{
		DirectedGraph g = new DirectedGraph();
		try {
			Connection conn = DriverManager.getConnection(DB_URL,DB_USER,DB_PASSWORD);
			Statement st = conn.createStatement();
     			ResultSet rs = st.executeQuery(TRADE_RELATIONS);
			String countryFrom;
			String countryTo;
			String trade;
			while ( rs.next() ) {
				countryFrom = rs.getString(1).trim();
				countryTo = rs.getString(2).trim();
				trade = rs.getString(3).trim();
				Node from = new Node(countryFrom);
				Node to = new Node(countryTo);
				DirectedEdge e = new DirectedEdge(from, to);
				e.addLabel(String.format("$%dM",Math.round(Double.parseDouble(trade))/1000000));
				g.add(e);
			}
			System.out.println(g);
		} catch (SQLException s) {
			throw s;
		}

		

	}

}
