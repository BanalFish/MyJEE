package tmall.dao;

//ORM=Object Relationship Database Mapping
//
//对象和关系数据库的映射
//简单说，一个对象，对应数据库里的一条记录

import tmall.bean.Category;
import tmall.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

//CategoryDAO用来建立对于 Category 对象的ORM映射
//也就是与数据库的操作建立起来：前端->后端->DAO->数据库
public class CategoryDAO {

    private Connection c;

    {
        try {
            c = DBUtil.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //得到总数
    public int getTotal(){
        int total=0;
        try(Statement s=c.createStatement()){

            String sql="select count(*) from category";
            ResultSet resultSet=s.executeQuery(sql);

            while(resultSet.next()){
                total=resultSet.getInt(1);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return total;
    }

    //添加类别
    public void add(Category bean){
        String sql="insert into category values(null,?)";
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

    //更新类别信息
    public void update(Category bean){
        String sql="update category set name = ? where id =?";
        try(PreparedStatement ps=c.prepareStatement(sql)){
            ps.setString(1,bean.getName());
            ps.setInt(2,bean.getId());

            ps.execute();//执行
        }catch(SQLException throwables){
            throwables.printStackTrace();
        }
    }

    //删除
    public void delete(int id){
        String sql="delete from category where id ="+String.valueOf(id);
        try(PreparedStatement ps=c.prepareStatement(sql)){
            ps.execute();//执行
        }catch(SQLException throwables){
            throwables.printStackTrace();
        }
    }

    //通过ID得到一个类别信息
    public Category get(int id){
        String sql="select * from category where id = "+id;
        Category category=null;
        try(Statement s=c.createStatement()){
            ResultSet rs=s.executeQuery(sql);
            while(rs.next()){
                category.setId(rs.getInt(1));
                category.setName(rs.getString(2));
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return category;
    }

    public List<Category> list(){
        return list(0,Short.MAX_VALUE);
    }

    //通过ID得到一群类别信息
    public List<Category> list(int start, int count) {
        List<Category> beans=new ArrayList<>();
        String sql="select * from category order by id desc limit ?,?";
        try(PreparedStatement ps=c.prepareStatement(sql)){
            ps.setInt(1,start);
            ps.setInt(2,count);
            ResultSet rs=ps.executeQuery();

            while(rs.next()){
                Category category = new Category();
                category.setId(rs.getInt(1));
                category.setName(rs.getString(2));
                beans.add(category);
            }
        }catch(SQLException throwables){
            throwables.printStackTrace();
        }
        return beans;
    }
}
