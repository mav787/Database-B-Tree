import java.sql.*;
import edu.brandeis.cs127b.pa2.gnuplot.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.text.ParseException;
public class Part1 {
	static final String JDBC_DRIVER = "com.postgresql.jdbc.Driver";
	static final String DB_TYPE = "postgresql";
	static final String DB_DRIVER = "jdbc";
	static final String DB_NAME = System.getenv("PGDATABASE");
	static final String DB_HOST = System.getenv("PGHOST");
	static final String DB_URL = String.format("%s:%s://%s/%s",DB_DRIVER, DB_TYPE, DB_HOST, DB_NAME);
	static final String DB_USER = System.getenv("PGUSER");
	static final String DB_PASSWORD = System.getenv("PGPASSWORD");
	static Connection conn;


    static final String QUERY = "select r_name, extract('month' from l_shipdate) as month, extract('year' from l_shipdate) as year, SUM(l_extendedprice*(1-l_discount)*(1+l_tax)) AS cost from lineitem, supplier, nation, region where l_suppkey = s_suppkey and s_nationkey = n_nationkey and n_regionkey = r_regionkey GROUP BY r_name, extract('month' from l_shipdate), extract('year' from l_shipdate)";

	public static void main(String[] args) throws SQLException, ParseException{
		conn = DriverManager.getConnection(DB_URL,DB_USER,DB_PASSWORD);
        final String title = "Monthly TPC-H Order Revenue by region";
        final String xlabel = "Year";
        final String ylabel = "Order Revenue (Thousands)";
		TimeSeriesPlot plot = new TimeSeriesPlot(title, xlabel, ylabel);
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(QUERY);
		Map<String, DateLine> regions = new HashMap<String, DateLine>();
		while ( rs.next() ) {
			String region = rs.getString(1).trim();
			String date = String.format("%s-%s",rs.getString(3).trim(),rs.getString(2));
			String income = rs.getString(4).trim();
			if (!regions.containsKey(region)){
				regions.put(region, new DateLine(region));
			}
			DateFormat format = new SimpleDateFormat("yyyy-MM", Locale.ENGLISH);
			Date tempD = format.parse(date);
			long time = tempD.getTime();
			regions.get(region).add(new DatePoint(tempD,Double.parseDouble(income)/1000));
			
		}
		plot.addAll(regions.values());
		System.out.println(plot);
	}

}
