package dao;//dao——>数据访问层，这里的类围绕着数据操作展开

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

//封装获取数据库连接的操作
public class DBUtil {
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/java_image_server?characterEncoding=utf8&useSSL=true";
    private static String USERNAME ="root";
    private static String PASSWORD ="hrmrhqd0127";

    //③加 volatile ，保持属性是内存可见的，使第一个线程进行操作后，其他线程可以及时看到更新
    private static volatile DataSource dataSource=null;

    //线程安全问题：①加锁  ②双重判断  ③加volatile

    public static DataSource getDataSource(){
        //通过这个方法创建 DataSource 的实例
        if(dataSource==null){//②双重判断（加锁操作是一种比较耗时、低效的操作，双重判断就是希望不要频繁的操作）
            synchronized (DBUtil.class){//①加锁
                if(dataSource==null){
                    dataSource=new MysqlDataSource();

                    MysqlDataSource tmpDataSource=(MysqlDataSource)dataSource;
                    tmpDataSource.setURL(URL);
                    tmpDataSource.setUser(USERNAME);
                    tmpDataSource.setPassword(PASSWORD);
                }
            }
        }
        return dataSource;
    }

    //获取数据库连接对象
    public static Connection getConnection() {
        try {
            return getDataSource().getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    //进行关闭操作（顺序很重要，先打开的后关闭）
    public static void close(Connection connection, PreparedStatement statement, ResultSet resultSet) {
        try {
            if(resultSet!=null){
                resultSet.close();
            }
            if(statement!=null){
                statement.close();
            }
            if(connection!=null){
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
