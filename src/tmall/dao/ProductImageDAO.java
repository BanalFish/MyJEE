package tmall.dao;

import tmall.bean.Product;
import tmall.bean.ProductImage;
import tmall.util.DBUtil;

import java.sql.*;
import java.util.List;

public class ProductImageDAO {
    public static final String type_single = "type_single";
    public static final String type_detail = "type_detail";

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
        String sql="select * from ProductImage";
        try(Statement s=c.createStatement()){
            ResultSet rs=s.executeQuery(sql);
            while(rs.next()){
                total=total+1;
                //total = rs.getInt(1);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return total;
    }

    public void add(ProductImage bean){
        String sql = "insert into ProductImage values(null,?,?)";
        try (PreparedStatement ps = c.prepareStatement(sql);) {
            ps.setInt(1, bean.getProduct().getId());
            ps.setString(2, bean.getType());
            ps.execute();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                int id = rs.getInt(1);
                bean.setId(id);
            }
        } catch (SQLException e) {

            e.printStackTrace();
        }
    }

    public void update(ProductImage bean){
        try (Statement s = c.createStatement();) {

            String sql = "delete from ProductImage where id = " + id;

            s.execute(sql);

        } catch (SQLException e) {

            e.printStackTrace();
        }
    }

    public void delete(int id){

    }

    public ProductImage get(int id){

    }

    public List<ProductImage> list(Product p, String type){

    }

    public List<ProductImage> list(Product p, String type, int start, int count){

    }
}
