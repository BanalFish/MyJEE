package tmall.dao;

import tmall.bean.Category;
import tmall.bean.Product;
import tmall.bean.ProductImage;
import tmall.util.DBUtil;
import tmall.util.DateUtil;

import java.util.ArrayList;
import java.util.Date;
import java.sql.*;
import java.util.List;

public class ProductDAO {

    private Connection c;

    {
        try {
            c = DBUtil.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getTotal(int cid) {

        int total = 0;
        try (Statement s = c.createStatement();) {

            String sql = "select count(*) from Product where cid = " + cid;

            ResultSet rs = s.executeQuery(sql);
            while (rs.next()) {
                total = rs.getInt(1);
            }
        } catch (SQLException e) {

            e.printStackTrace();
        }
        return total;
    }

    public void add(Product bean){
        String sql="insert into product values(null,?,?,?,?,?,?,?)";
        try(PreparedStatement ps=c.prepareStatement(sql)) {
            ps.setString(1, bean.getName());
            ps.setString(2,bean.getSubTitle());
            ps.setFloat(3,bean.getOrignalPrice());
            ps.setFloat(4,bean.getPromotePrice());
            ps.setInt(5,bean.getStock());
            ps.setInt(6,bean.getCategory().getId());
            ps.setTimestamp(7, DateUtil.d2t(bean.getCreateDate()));

            ps.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void delete(int id) {

        try (Statement s = c.createStatement();) {

            String sql = "delete from Product where id = " + id;

            s.execute(sql);

        } catch (SQLException e) {

            e.printStackTrace();
        }
    }

    public Product get(int id) {
        Product bean = new Product();

        try (Statement s = c.createStatement();) {

            String sql = "select * from Product where id = " + id;

            ResultSet rs = s.executeQuery(sql);

            if (rs.next()) {

                String name = rs.getString("name");
                String subTitle = rs.getString("subTitle");
                float orignalPrice = rs.getFloat("orignalPrice");
                float promotePrice = rs.getFloat("promotePrice");
                int stock = rs.getInt("stock");
                int cid = rs.getInt("cid");
                Date createDate = DateUtil.t2d( rs.getTimestamp("createDate"));

                bean.setName(name);
                bean.setSubTitle(subTitle);
                bean.setOrignalPrice(orignalPrice);
                bean.setPromotePrice(promotePrice);
                bean.setStock(stock);
                Category category = new CategoryDAO().get(cid);
                bean.setCategory(category);
                bean.setCreateDate(createDate);
                bean.setId(id);
                //下一句与h2j不一样
                bean.setFirstProductImage(bean.getFirstProductImage());
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }
        return bean;
    }


    public List<Product> list(int cid) {
        return list(cid,0, Short.MAX_VALUE);
    }

    public List<Product> list(int cid, int start, int count) {
        List<Product> beans = new ArrayList<Product>();
        Category category = new CategoryDAO().get(cid);
        String sql = "select * from Product where cid = ? order by id desc limit ?,? ";

        try (PreparedStatement ps = c.prepareStatement(sql);) {
            ps.setInt(1, cid);
            ps.setInt(2, start);
            ps.setInt(3, count);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Product bean = new Product();
                int id = rs.getInt(1);
                String name = rs.getString("name");
                String subTitle = rs.getString("subTitle");
                float orignalPrice = rs.getFloat("orignalPrice");
                float promotePrice = rs.getFloat("promotePrice");
                int stock = rs.getInt("stock");
                Date createDate = DateUtil.t2d( rs.getTimestamp("createDate"));

                bean.setName(name);
                bean.setSubTitle(subTitle);
                bean.setOrignalPrice(orignalPrice);
                bean.setPromotePrice(promotePrice);
                bean.setStock(stock);
                bean.setCreateDate(createDate);
                bean.setId(id);
                bean.setCategory(category);
                bean.setFirstProductImage(bean.getFirstProductImage());
                beans.add(bean);
            }
        } catch (SQLException e) {

            e.printStackTrace();
        }
        return beans;
    }

    //又一种排序
    public List<Product> list() {
        return list(0,Short.MAX_VALUE);
    }
    public List<Product> list(int start, int count) {
        List<Product> beans = new ArrayList<Product>();

        String sql = "select * from Product limit ?,? ";

        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql);) {

            ps.setInt(1, start);
            ps.setInt(2, count);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Product bean = new Product();
                int id = rs.getInt(1);
                int cid = rs.getInt("cid");
                String name = rs.getString("name");
                String subTitle = rs.getString("subTitle");
                float orignalPrice = rs.getFloat("orignalPrice");
                float promotePrice = rs.getFloat("promotePrice");
                int stock = rs.getInt("stock");
                Date createDate = DateUtil.t2d( rs.getTimestamp("createDate"));

                bean.setName(name);
                bean.setSubTitle(subTitle);
                bean.setOrignalPrice(orignalPrice);
                bean.setPromotePrice(promotePrice);
                bean.setStock(stock);
                bean.setCreateDate(createDate);
                bean.setId(id);

                Category category = new CategoryDAO().get(cid);
                bean.setCategory(category);
                beans.add(bean);
            }
        } catch (SQLException e) {

            e.printStackTrace();
        }
        return beans;
    }

    public void fill(List<Category> cs){
        for (Category c : cs)
            fill(c);
    }

    public void fill(Category c){
        List<Product> ps = this.list(c.getId());
        c.setProducts(ps);
    }

    public void fillByRow(List<Category> cs){
        int productNumberEachRow=8;
        for(Category c:cs){
            List<Product> products=c.getProducts();//这个类别下的所有产品
            List<List<Product>> productsByRow=new ArrayList<>();//这个类别下的每一页下的产品

            for(int i=0;i<products.size();i+=productNumberEachRow){
                int size=i+productNumberEachRow;
                size=size>products.size()?products.size():size;//如果不满足一页的数量
                List<Product> productsOfEachRow=products.subList(i,size);//子List分页展示
                productsByRow.add(productsOfEachRow);//把每一页的产品集合作为一个整体添加到productsByRow

            }
            c.setProductByRow(productsByRow);//添加到这个类别

            //这样，每个类别就能够分页展示，每页展示8个产品
        }
    }

    //一个产品有多个图片，但是只有一个主图片，把第一个图片设置为主图片
    public void setFirstProductImage(Product p){
        List<ProductImage> pis=new ProductImageDAO().list(p,ProductImageDAO.type_single);
        if(!pis.isEmpty())
            p.setFirstProductImage(pis.get(0));
    }

    //为产品设置销售和评价数量
    public void setSaleAndReviewNumber(Product p){
        int saleCount=new OrderItemDAO().getSaleCount(p.getId());
        p.setSaleCount(saleCount);

        int reviewCount=new ReviewDAO().getCount(p.getId());
        p.setReviewCount(reviewCount);
    }

    //为产品设置销售和评价数量
    public void setSaleAndReviewNumber(List<Product> products){
        for(Product p:products){
            setSaleAndReviewNumber(p);
        }
    }

    //根据关键字查询产品
    public List<Product> search(String keyword, int start, int count){
        List<Product> beans = new ArrayList<Product>();
        if(null==keyword||0==keyword.trim().length())
            return beans;

        String sql = "select * from Product where name like ? limit ?,? ";

        try (PreparedStatement ps = c.prepareStatement(sql);) {
            ps.setString(1, "%"+keyword.trim()+"%");
            ps.setInt(2, start);
            ps.setInt(3, count);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Product bean = new Product();
                int id = rs.getInt(1);
                int cid = rs.getInt("cid");
                String name = rs.getString("name");
                String subTitle = rs.getString("subTitle");
                float orignalPrice = rs.getFloat("orignalPrice");
                float promotePrice = rs.getFloat("promotePrice");
                int stock = rs.getInt("stock");
                Date createDate = DateUtil.t2d( rs.getTimestamp("createDate"));

                bean.setName(name);
                bean.setSubTitle(subTitle);
                bean.setOrignalPrice(orignalPrice);
                bean.setPromotePrice(promotePrice);
                bean.setStock(stock);
                bean.setCreateDate(createDate);
                bean.setId(id);

                Category category = new CategoryDAO().get(cid);
                bean.setCategory(category);
                setFirstProductImage(bean);
                beans.add(bean);
            }
        } catch (SQLException e) {

            e.printStackTrace();
        }
        return beans;
    }
}
