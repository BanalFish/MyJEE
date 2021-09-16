package tmall.dao;

import tmall.bean.Product;
import tmall.bean.ProductImage;
import tmall.util.DBUtil;

import java.sql.*;
import java.util.List;

public class ProductImageDAO {
    public static final String type_single = "type_single";//单个图片
    public static final String type_detail = "type_detail";//详情图片

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
        String sql="select count(*) from ProductImage";
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
        String sql="update ProductImage set id=?, pid=?,type=?";
        try(PreparedStatement ps=c.prepareStatement(sql)) {
            ps.setInt(1,bean.getId());
            ps.setInt(2,bean.getProduct().getId());
            ps.setString(3,bean.getType());

            ps.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void delete(int id){
        try (Statement s = c.createStatement();) {

            String sql = "delete from ProductImage where id = " + id;

            s.execute(sql);

        } catch (SQLException e) {

            e.printStackTrace();
        }
    }

    public ProductImage get(int id){
        ProductImage productImage=new ProductImage();
        String sql="select * from ProductImage where id="+id;
        try(Statement s=c.createStatement()){
            ResultSet rs=s.executeQuery(sql);
            if(rs.next()){
                productImage.setId(id);
                productImage.setProduct(new ProductDAO().get(id));
                productImage.setType(rs.getString(3));

            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return productImage;
    }

    public List<ProductImage> list(Product p, String type){
        return list(p,type,0,Short.MAX_VALUE);
    }

    public List<ProductImage> list(Product p, String type, int start, int count){
        List<ProductImage> list=null;
        String sql="select * from ProductImage where pid=? and type=? order by pid limit ?,?";
        try(PreparedStatement ps=c.prepareStatement(sql)){
            ps.setInt(1,p.getId());
            ps.setString(2,type);
            ps.setInt(3,start);
            ps.setInt(4,count);

            ResultSet rs=ps.executeQuery();

            while(rs.next()){
                ProductImage pi=null;
                pi.setId(rs.getInt(1));
                pi.setProduct(new ProductDAO().get(rs.getInt("id")));
                pi.setType(rs.getString(3));
                list.add(pi);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return list;
    }

}
