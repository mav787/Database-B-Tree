import java.sql.*;
import java.util.*;
import edu.brandeis.cs127b.pa2.gnuplot.*;
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

    // SQL query for retrieving data 
    static final String QUERY= "SELECT EXTRACT(YEAR from l_shipdate) AS YEAR, EXTRACT(MONTH from l_shipdate) AS MONTH, SUM(l_extendedprice *(1 + l_tax) * (1 - l_discount)), n_regionkey "  
                                  + "FROM lineitem, supplier, nation "
                                  + "WHERE (l_suppkey = s_suppkey AND s_nationkey = n_nationkey) "
                                  + "GROUP BY YEAR, MONTH, n_regionkey;";
    

    public static void main(String[] args) throws SQLException {

        conn = DriverManager.getConnection(DB_URL,DB_USER,DB_PASSWORD);

        // Customize graph properties 
        final String title = "Monthly TPC-H Order Sales Total by region";
        final String xlabel = "Year";
        final String ylabel = "Order Total(Thousands)";

        TimeSeriesPlot plot = new TimeSeriesPlot(title, xlabel, ylabel);

        // Statement objects to hold statements;
        Statement st = conn.createStatement();
        
        // ResultSet objects to store retrieved data
        ResultSet rs = st.executeQuery(QUERY);
        
        // DateLine objects to display visualization
        DateLine africa = new DateLine("AFRICA");
        DateLine america = new DateLine("AMERICA");
        DateLine asia = new DateLine("ASIA");
        DateLine europe = new DateLine("EUROPE");
        DateLine middleeast = new DateLine("MIDDLE EAST");

        // Render visualization 
        while ( rs.next() ) {
                Calendar calendar = new GregorianCalendar(rs.getInt(1), rs.getInt(2), 1);  
                java.util.Date date = calendar.getTime();
                switch (rs.getInt(4)){
                    case 0: 
                        africa.add(new DatePoint(date, rs.getDouble(3) / 1000));
                        break;
                    case 1:
                        america.add(new DatePoint(date, rs.getDouble(3) / 1000));
                        break;
                    case 2:
                        asia.add(new DatePoint(date, rs.getDouble(3) / 1000));
                        break;
                    case 3:
                        europe.add(new DatePoint(date, rs.getDouble(3) / 1000));
                        break;
                    case 4:
                        middleeast.add(new DatePoint(date, rs.getDouble(3) / 1000));
                        break;
                    default:
                        break;
                }
                
        }
        plot.add(africa);
        plot.add(america);
        plot.add(asia);
        plot.add(europe);
        plot.add(middleeast);

        System.out.println(plot);
    }

}
