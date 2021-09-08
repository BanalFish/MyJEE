package tmall.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {
    static String loginName = "root";
    static String password = "ma117861";

    static {
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");

        }catch(ClassNotFoundException e){
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        String url="jdbc:mysql://localhost:3306/tmall?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone = GMT&&rewriteBatchedStatements=true";
        return DriverManager.getConnection(url,loginName,password);

    }
    public static void main(String[] args) throws SQLException {
        System.out.println(getConnection());

    }

}
