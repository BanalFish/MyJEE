package tmall.dao;

import tmall.bean.Category;
import tmall.bean.User;
import tmall.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    private Connection c;

    {
        try {
            c = DBUtil.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getTotal(){
        int total=0;
        try(Statement s=c.createStatement()){

            String sql="select count(*) from user";
            ResultSet resultSet=s.executeQuery(sql);

            while(resultSet.next()){
                total=resultSet.getInt(1);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return total;
    }

    public void add(User bean){
        String sql="insert into user values(null,?)";
        try(PreparedStatement ps=c.prepareStatement(sql)){
            ps.setString(1, bean.getName());
            ps.execute();

            ResultSet rs=ps.getGeneratedKeys();
            if(rs.next()){
                int id=rs.getInt(1);
                bean.setId(id);
            }

        }catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void update(User bean){
        String sql="update user set name = ? where id =?";
        try(PreparedStatement ps=c.prepareStatement(sql)){
            ps.setString(1,bean.getName());
            ps.setInt(2,bean.getId());

            ps.execute();//执行
        }catch(SQLException throwables){
            throwables.printStackTrace();
        }
    }

    public void delete(int id){
        String sql="delete from user where id ="+String.valueOf(id);
        try(Statement s=c.createStatement()){
            s.execute(sql);//执行
        }catch(SQLException throwables){
            throwables.printStackTrace();
        }
    }

    public User get(int id){
        String sql="select * from user where id = "+id;
        User user=null;
        try(Statement s=c.createStatement()){
            ResultSet rs=s.executeQuery(sql);
            if(rs.next()){
                user.setId(id);
                user.setName(rs.getString("name"));
                user.setPassword(rs.getString("password"));
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return user;
    }

    private List<User> list(int start, short count) {
        List<User> beans=new ArrayList<>();
        String sql="select * from user order by id desc limit ?,?";
        try(PreparedStatement ps=c.prepareStatement(sql)){
            ps.setInt(1,start);
            ps.setShort(2,count);
            ResultSet rs=ps.executeQuery();

            while(rs.next()){
                User user = new User();
                user.setId(rs.getInt(1));
                user.setName(rs.getString(2));
                beans.add(user);
            }
        }catch(SQLException throwables){
            throwables.printStackTrace();
        }
        return beans;
    }

    public List<User> list() {
        return list(0,Short.MAX_VALUE);
    }

    public boolean isExist(String name){
        User user=get(name);
        return user!=null;

        /**
         * 下面注释的代码自己写的，暂且注释，后续验证准确性
         */
//        boolean res=false;
//        String str="select * from user where name ="+name;
//        try(Statement s=c.createStatement()){
//            ResultSet rs=s.executeQuery(str);
//            if(rs!=null){
//                res=true;
//            }
//        }catch(SQLException throwables){
//            throwables.printStackTrace();
//        }
//        return res;
    }

    public User get(String name){
        User user=null;
        String sql="select * from user where name = ?";
        try(PreparedStatement ps=c.prepareStatement(sql)){
            ps.setString(1,name);
            ResultSet rs=ps.executeQuery();

            if(rs.next()){
                user.setId(rs.getInt("id"));
                user.setName(rs.getString("name"));
                user.setPassword(rs.getString("password"));
            }


        }catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return user;

//        User user=null;
//        if(isExist(name)==true){
//            String str="select * from user where name ="+name;
//            try(Statement s=c.createStatement()){
//                ResultSet rs=s.executeQuery(str);
//                if(rs!=null){
//                    user.setId(rs.getInt(1));
//                    user.setName(rs.getString(2));
//                }
//            }catch(SQLException throwables){
//                throwables.printStackTrace();
//            }
//        }
//        return user;
    }

    public User get(String name, String password){

        User user=null;
        String sql="select * from user where name = ? and password = ?";
        try(PreparedStatement ps=c.prepareStatement(sql)){
            ps.setString(1,name);
            ps.setString(2,password);
            ResultSet rs=ps.executeQuery();

            if(rs.next()){
                user.setId(rs.getInt("id"));
                user.setName(rs.getString("name"));
                user.setPassword(rs.getString("password"));
            }


        }catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return user;



//        User user=null;
//        if(isExist(name)==true){
//            String str="select * from user where name ="+name;
//            try(Statement s=c.createStatement()){
//                ResultSet rs=s.executeQuery(str);
//                if(rs!=null){
//                    user.setId(rs.getInt(1));
//                    user.setName(rs.getString(2));
//                }
//            }catch(SQLException throwables){
//                throwables.printStackTrace();
//            }
//        }
//        return user;
    }
}
