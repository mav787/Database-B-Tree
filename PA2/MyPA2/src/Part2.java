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

    // query to retrieve data    
    static final String QUERY = "SELECT RC.r_name, RS.r_name, SUM(l_extendedprice * (1 - l_discount) * (1 + l_tax)) "
                                + "FROM supplier, customer, orders, lineitem, nation AS NS, nation AS NC, region AS RS, region AS RC "
                                + "WHERE l_orderkey = o_orderkey AND o_custkey = c_custkey AND "
                                + "l_suppkey = s_suppkey AND s_nationkey = NS.n_nationkey AND c_nationkey = NC.n_nationkey "
                                + "AND NS.n_regionkey = RS.r_regionkey AND NC.n_regionkey = RC.r_regionkey "
                                + "GROUP BY RC.r_name, RS.r_name;";
    
    public static void main(String[] args) throws SQLException{
        DirectedGraph g = new DirectedGraph();
        try {
            Connection conn = DriverManager.getConnection(DB_URL,DB_USER,DB_PASSWORD);
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(QUERY);
            String fromLabel;
            String toLabel;
            String weight;
            while ( rs.next() ) {
                fromLabel = rs.getString(1).trim();
                toLabel = rs.getString(2).trim();
                // make some management to the String "weight" to match the formatting
                weight = "$" + rs.getInt(3)/1000000 + "M";
                Node from = new Node(fromLabel);
                Node to = new Node(toLabel);
                DirectedEdge e = new DirectedEdge(from, to);
                e.addLabel(weight);
                g.add(e);
            }
            System.out.println(g);
        } catch (SQLException s) {
            throw s;
        }
    }
}
