package com.xiaofeng.GoSports.Dao;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.xiaofeng.GoSports.Model.User;

import java.util.ArrayList;

public class DBOpenHelper extends SQLiteOpenHelper {
    /**
     * 声明一个AndroidSDK自带的数据库变量db
     */
    private SQLiteDatabase db;

    /**
     * 写一个这个类的构造函数，参数为上下文context，所谓上下文就是这个类所在包的路径
     * 指明上下文，数据库名，工厂默认空值，版本号默认从1开始
     * super(context,"db_test",null,1);
     * 把数据库设置成可写入状态，除非内存已满，那时候会自动设置为只读模式
     * 不过，以现如今的内存容量，估计一辈子也见不到几次内存占满的状态
     * db = getReadableDatabase();
     */
    public DBOpenHelper(Context context) {
        super(context, "db_test", null, 1);
        db = getReadableDatabase();
    }

    // 创建数据库
    static final String CREATE_SQL[] = {
            "CREATE TABLE IF NOT EXISTS user(" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name varchar," +
                    "password varchar," +
                    "email varchar," +
                    "phonenum varchar)",
            "INSERT INTO user VALUES (1,'admin',123,'admin@163.com',17858407426)",
            "INSERT INTO user VALUES (2,'mxf',123,'zhangsan@163.com',13968083789)",
            "INSERT INTO user VALUES (3,'maxiaofeng',123,'lisi@163.com',13968083456)",
            "INSERT INTO user VALUES (4,'admin','admin','wangwu@163.com',1561565454)",
            "CREATE TABLE IF NOT EXISTS pushUpTable(" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "beginTime varchar," +
                    "endTime varchar," +
                    "energyCost varchar," +
                    "pushUpCount varchar," +
                    "costTime varchar)",
            "CREATE TABLE IF NOT EXISTS skippingTable(" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "beginTime varchar," +
                    "endTime varchar," +
                    "energyCost varchar," +
                    "jumpCount varchar," +
                    "costTime varchar)"

    };

    /**
     * 创建数据库
     *
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // 创建数据库
        Log.i("sqlite_____", "create Database");

        // 执行 SQL 语句
        for (int i = 0; i < CREATE_SQL.length; i++) {
            db.execSQL(CREATE_SQL[i]);
        }

        // 完成数据库的创建
        Log.i("sqlite_____", "Finished Database");
    }

    //版本适应
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS user");
        onCreate(db);
    }

    /**
     * 接下来写自定义的增删改查方法
     * 这些方法，写在这里归写在这里，以后不一定都用
     * add()
     * delete()
     * update()
     * getAllData()
     */
    public void addUser(String name, String password, String email, String phonenum) {
        db.execSQL("INSERT INTO user (name,password,email,phonenum) VALUES(?,?,?,?)", new Object[]{name, password, email, phonenum});
    }

    public void delete(String name, String password) {
        db.execSQL("DELETE FROM user WHERE name = AND password =" + name + password);
    }

    public void update(String password) {
        db.execSQL("UPDATE user SET password = ?", new Object[]{password});
    }

    /**
     * 增加俯卧撑锻炼记录
     *
     * @param beginTime
     * @param endTime
     * @param energyCost
     * @param pushUpCount
     * @param costTime
     */
    public void addPushUpRecord(String beginTime, String endTime,
                                String energyCost, String pushUpCount, String costTime) {
        db.execSQL("INSERT INTO pushUpTable (beginTime,endTime,energyCost,pushUpCount,costTime) VALUES(?,?,?,?,?)",
                new Object[]{beginTime, endTime, energyCost, pushUpCount, costTime});
    }

    /**
     * 将一次跳绳的运动数据记录加入到数据库中
     *
     * @param beginTime
     * @param endTime
     * @param energyCost
     * @param jumpCount
     * @param costTime
     */
    public void addSkippingRecord(String beginTime, String endTime,
                                  String energyCost, String jumpCount, String costTime) {
        db.execSQL("INSERT INTO pushUpTable (beginTime,endTime,energyCost,pushUpCount,costTime) VALUES(?,?,?,?,?)",
                new Object[]{beginTime, endTime, energyCost, jumpCount, costTime});
    }

    /**
     * 前三个没啥说的，都是一套的看懂一个其他的都能懂了
     * 下面重点说一下查询表user全部内容的方法
     * 我们查询出来的内容，需要有个容器存放，以供使用，
     * 所以定义了一个ArrayList类的list
     * 有了容器，接下来就该从表中查询数据了，
     * 这里使用游标Cursor，这就是数据库的功底了，
     * 在Android中我就不细说了，因为我数据库功底也不是很厚，
     * 但我知道，如果需要用Cursor的话，第一个参数："表名"，中间5个：null，
     * 最后是查询出来内容的排序方式："name DESC"
     * 游标定义好了，接下来写一个while循环，让游标从表头游到表尾
     * 在游的过程中把游出来的数据存放到list容器中
     */
    public ArrayList<User> getAllData() {
        ArrayList<User> list = new ArrayList<User>();
        @SuppressLint("Recycle") Cursor cursor = db.query("user", null, null, null, null, null, "name DESC");
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String email = cursor.getString(cursor.getColumnIndex("email"));
            String phonenum = cursor.getString(cursor.getColumnIndex("phonenum"));
            String password = cursor.getString(cursor.getColumnIndex("password"));

            list.add(new User(name, password, email, phonenum));
        }
        return list;
    }

    public Boolean findUserIfExisted(String username, String password) {
        /**
         * 查询用户信息,放回值是一个游标(结果集,遍历结果集)
         */
        Cursor cursor = db.query("user", new String[]{"name", "password"}, "name=?",
                new String[]{username}, null, null, null, "0,1");
        // 游标移动进行校验
        if (cursor.moveToNext()) {
            // 从数据库获取密码进行校验
            String dbPassword = cursor.getString(cursor.getColumnIndex("password"));
            // 关闭游标
            cursor.close();
            if (password.equals(dbPassword)) return true;
        }
        return false;
    }
}


