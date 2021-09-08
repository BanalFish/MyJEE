package tmall.util;

import java.sql.Timestamp;
import java.util.Date;

public class DateUtil {//主要是用于java.util.Date类与java.sql.Timestamp 类的互相转换

    //实体类中日期类型的属性，使用的都是java.util.Date类
    //要想在MySQL中保存时间信息，必须使用datetime类型的字段
    //jdbc获取datetime需要Timestamp获取
    //否则只会保留日期信息，而丢失时间信息。

    public static Timestamp d2t(Date d) {
        if (null == d)
            return null;
        return new java.sql.Timestamp(d.getTime());
    }

    public static Date t2d(Timestamp t) {
        if (null == t)
            return null;
        return new java.util.Date(t.getTime());
    }
}
