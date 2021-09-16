package tmall.dao;

import tmall.bean.Category;
import tmall.bean.Property;
import tmall.util.DBUtil;

import java.sql.*;
import java.util.List;

public class PropertyDAO {

    private Connection c;

    {
        try {
            c = DBUtil.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getTotal(int cid){
        int total=0;
        String sql="select count(*) from property";

        try(Statement s=c.createStatement()){

            ResultSet rs=s.executeQuery(sql);
            while(rs.next()){
                total=total+1;//如果有结果就+1
                //total = rs.getInt(1);
                //重启数据库就不会出现断层现象
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return total;
    }

    public void add(Property bean){
        String sql="insert into property values(null,?,?)";
        try(PreparedStatement ps=c.prepareStatement(sql)){
            ps.setInt(1,bean.getId());
            ps.setString(2,bean.getName());

            ps.execute();

            ResultSet rs=ps.getGeneratedKeys();
            while(rs.next()){
                int id=rs.getInt("id");
                bean.setId(id);
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void update(Property bean){
        String sql="update property set cid = ? ,name = ? where id = ?";
        try(PreparedStatement ps=c.prepareStatement(sql)){
            ps.setInt(1,bean.getCategory().getId());
            ps.setString(2,bean.getName());
            ps.setInt(3,bean.getId());

            ps.execute();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void delete(int id){
        String sql="delete from property where id ="+id;
        try(Statement s=c.createStatement()){
            s.executeQuery(sql);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public Property get(int id){
        Property property=null;
        String sql="select * from property where id = "+id;
        try(Statement s=c.createStatement()){
            ResultSet rs = s.executeQuery(sql);
            if(rs.next()){
                property.setId(rs.getInt("id"));
                property.setName(rs.getString("name"));

                CategoryDAO categoryDAO=new CategoryDAO();
                Integer cid=rs.getInt("cid");
                Category category=categoryDAO.get(cid);

                property.setCategory(category);

            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return property;
    }

    public List<Property> list(int cid){

        return list(cid,0,Short.MAX_VALUE);

    }

    public List<Property> list(int cid, int start, int count){
        List<Property> list=null;
        String sql="select * from category where cid = ? order by id limit ? , ?";
        try(PreparedStatement ps=c.prepareStatement(sql)){
            ps.setInt(1,cid);
            ps.setInt(2,start);
            ps.setInt(3,count);

            ResultSet rs=ps.executeQuery();

            while (rs.next()){
                Property p=null;
                p.setId(rs.getInt("id"));
                p.setName(rs.getString("name"));

                CategoryDAO categoryDAO=new CategoryDAO();
                Integer cId=rs.getInt("cid");
                Category category=categoryDAO.get(cId);

                p.setCategory(category);

                list.add(p);
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }


        return list;

    }



}
