package tmall.bean;

public class User {
    private String password;
    private String name;
    private int id;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAnonymousName() {

        char cs[];

        if (name == null)
            return null;
        if (name.length() == 1)
            return "*";
        else if (name.length() == 2)//aa->a*a
            return name.substring(0, 1) + "*"+name.substring(name.length()-1);
        else {
            cs = name.toCharArray();

            for (int i = 1; i < name.length() - 1; i++) {//aaaa->a***a
                cs[i] = '*';
            }
        }
        return cs.toString();
    }
}
