package tmall.dao;

import tmall.bean.Order;
import tmall.bean.OrderItem;
import tmall.bean.Product;
import tmall.bean.User;
import tmall.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderItemDAO {

    private Connection c;

    {
        try {
            c = DBUtil.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getTotal() {
        int total = 0;
        try (Statement s = c.createStatement();) {

            String sql = "select count(*) from OrderItem";

            ResultSet rs = s.executeQuery(sql);
            while (rs.next()) {
                total = rs.getInt(1);
            }
        } catch (SQLException e) {

            e.printStackTrace();
        }
        return total;
    }

    public void add(OrderItem bean){
        String sql="insert into orderItem values(null,?,?,?,?)";

        try(PreparedStatement ps=c.prepareStatement(sql)) {
            ps.setInt(1,bean.getProduct().getId());

            //订单项在创建的时候，是没有订单信息的
            if(null==bean.getOrder())
                ps.setInt(2,-1);
            else
                ps.setInt(2,bean.getOrder().getId());

            ps.setInt(3,bean.getUser().getId());
            ps.setInt(4,bean.getNumber());

            ps.execute();

            ResultSet rs=ps.getGeneratedKeys();
            if(rs.next()){
                bean.setId(rs.getInt(1));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void update(OrderItem bean){
        String sql = "update OrderItem set pid= ?, oid=?, uid=?,number=?  where id = ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, bean.getProduct().getId());
            if(null==bean.getOrder())
                ps.setInt(2, -1);
            else
                ps.setInt(2, bean.getOrder().getId());
            ps.setInt(3, bean.getUser().getId());
            ps.setInt(4, bean.getNumber());

            ps.setInt(5, bean.getId());
            ps.execute();

        } catch (SQLException e) {

            e.printStackTrace();
        }
    }

    public void delete(int id) {

        try (Statement s = c.createStatement();) {

            String sql = "delete from OrderItem where id = " + id;

            s.execute(sql);

        } catch (SQLException e) {

            e.printStackTrace();
        }
    }

    public OrderItem get(int id) {
        OrderItem bean = new OrderItem();

        try (Statement s = c.createStatement();) {

            String sql = "select * from OrderItem where id = " + id;

            ResultSet rs = s.executeQuery(sql);

            if (rs.next()) {
                int pid = rs.getInt("pid");
                int oid = rs.getInt("oid");
                int uid = rs.getInt("uid");
                int number = rs.getInt("number");
                Product product = new ProductDAO().get(pid);
                User user = new UserDAO().get(uid);

                bean.setProduct(product);
                bean.setUser(user);
                bean.setNumber(number);

                if(-1!=oid){//如果不存在订单
                    Order order= new OrderDAO().get(oid);
                    bean.setOrder(order);
                }

                bean.setId(id);
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }
        return bean;
    }

    //通过用户来查找
    public List<OrderItem> listByUser(int uid) {
        return listByUser(uid, 0, Short.MAX_VALUE);
    }
    public List<OrderItem> listByUser(int uid, int start, int count) {
        List<OrderItem> beans = new ArrayList<OrderItem>();

        String sql = "select * from OrderItem where uid = ? and oid=-1 order by id desc limit ?,? ";

        try (PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, uid);
            ps.setInt(2, start);
            ps.setInt(3, count);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                OrderItem bean = new OrderItem();
                int id = rs.getInt(1);

                int pid = rs.getInt("pid");
                int oid = rs.getInt("oid");
                int number = rs.getInt("number");

                Product product = new ProductDAO().get(pid);
                if(-1!=oid){
                    Order order= new OrderDAO().get(oid);
                    bean.setOrder(order);
                }
                User user = new UserDAO().get(uid);

                bean.setProduct(product);
                bean.setUser(user);
                bean.setNumber(number);
                bean.setId(id);
                beans.add(bean);
            }
        } catch (SQLException e) {

            e.printStackTrace();
        }
        return beans;
    }

    //查询某种订单下所有的订单项
    public List<OrderItem> listByOrder(int oid) {
        return listByOrder(oid, 0, Short.MAX_VALUE);
    }
    public List<OrderItem> listByOrder(int oid, int start, int count) {
        List<OrderItem> beans = new ArrayList<OrderItem>();

        String sql = "select * from OrderItem where oid = ? order by id desc limit ?,? ";

        try (PreparedStatement ps = c.prepareStatement(sql);) {

            ps.setInt(1, oid);
            ps.setInt(2, start);
            ps.setInt(3, count);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                OrderItem bean = new OrderItem();
                int id = rs.getInt(1);

                int pid = rs.getInt("pid");
                int uid = rs.getInt("uid");
                int number = rs.getInt("number");

                Product product = new ProductDAO().get(pid);
                if(-1!=oid){
                    Order order= new OrderDAO().get(oid);
                    bean.setOrder(order);
                }

                User user = new UserDAO().get(uid);
                bean.setProduct(product);

                bean.setUser(user);
                bean.setNumber(number);
                bean.setId(id);
                beans.add(bean);
            }
        } catch (SQLException e) {

            e.printStackTrace();
        }
        return beans;
    }

    //为订单设置订单项总计集合
    public void fill(List<Order> os){//订单集合
        for(Order o:os){//取出一个订单
            List<OrderItem> ois=listByOrder(o.getId());//订单项集合
            float total=0;
            int totalNumber=0;
            for(OrderItem oi:ois){//取出每一个订单产品，即在订单的情况下的product类
                total+=oi.getNumber()*oi.getProduct().getPromotePrice();//这项产品的总花费=数量*单价
                totalNumber+=oi.getNumber();//总数量
            }
            o.setTotal(total);//这个订单的总花费
            o.setOrderItems(ois);//订单项集合
            o.setTotalNumber(totalNumber);//总数量
        }
    }
    public void fill(Order o){
        List<OrderItem> ois=listByOrder(o.getId());
        float total=0;
        for(OrderItem oi:ois){
            total+=oi.getNumber()*oi.getProduct().getPromotePrice();
        }
        o.setTotal(total);
        o.setOrderItems(ois);
    }

    public List<OrderItem> listByProduct(int pid){
        return listByProduct(pid, 0, Short.MAX_VALUE);
    }
    public List<OrderItem> listByProduct(int pid, int start, int count){
        List<OrderItem> beans = new ArrayList<OrderItem>();

        String sql = "select * from OrderItem where pid = ? order by id desc limit ?,? ";

        try (PreparedStatement ps = c.prepareStatement(sql);) {

            ps.setInt(1, pid);
            ps.setInt(2, start);
            ps.setInt(3, count);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                OrderItem bean = new OrderItem();
                int id = rs.getInt(1);

                int uid = rs.getInt("uid");
                int oid = rs.getInt("oid");
                int number = rs.getInt("number");

                Product product = new ProductDAO().get(pid);
                if(-1!=oid){
                    Order order= new OrderDAO().get(oid);
                    bean.setOrder(order);
                }

                User user = new UserDAO().get(uid);
                bean.setProduct(product);

                bean.setUser(user);
                bean.setNumber(number);
                bean.setId(id);
                beans.add(bean);
            }
        } catch (SQLException e) {

            e.printStackTrace();
        }
        return beans;
    }

    public int getSaleCount(int pid){
        int total = 0;
        try (Statement s = c.createStatement();) {

            String sql = "select sum(number) from OrderItem where pid = " + pid;

            ResultSet rs = s.executeQuery(sql);
            while (rs.next()) {
                total = rs.getInt(1);
            }
        } catch (SQLException e) {

            e.printStackTrace();
        }
        return total;
    }
}
