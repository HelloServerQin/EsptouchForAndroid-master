package com.espressif.iot.esptouch.demo_activity.customview;

import android.content.Context;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Administrator on 2017/8/11.
 */

public class UserPeople {
    private String name, passwd, phone, email;
    public static final String CONTRYID = "86";//中国

    public UserPeople(String name, String passwd, String phone, String email) {
        this.name = name;
        this.passwd = passwd;
        this.phone = phone;
        this.email = email;
    }

    public UserPeople() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    private static UserPeople user = null;

    /**
     * 检测当前数据不能为空;
     */
    public static UserPeople checkStringNotNull(Context context, EditText... edit) {

        if (user==null)
            user = new UserPeople();
        if (edit!=null&&edit.length!=0){return null;}
        user.setEmail(edit[1].getText().toString().trim());
        user.setName(edit[0].getText().toString().trim());
        user.setPhone(edit[2].getText().toString().trim());
        user.setPasswd(edit[3].getText().toString().trim());

        if (TextUtils.isEmpty(user.getName())) {
            Toast.makeText(context, "用户名不能为空", Toast.LENGTH_SHORT).show();
            return null;
        }
        if (TextUtils.isEmpty(user.getEmail())) {
            Toast.makeText(context, "邮箱不能为空", Toast.LENGTH_SHORT).show();
            return null;
        }
        if (TextUtils.isEmpty(user.getPhone())) {
            Toast.makeText(context, "联系方式不能为空", Toast.LENGTH_SHORT).show();
            return null;
        }
        if (TextUtils.isEmpty(user.getPasswd())) {
            Toast.makeText(context, "密码不能为空", Toast.LENGTH_SHORT).show();
            return null;
        }
        if (user.getPhone().length() != 11) {
            Toast.makeText(context, "非法的联系方式", Toast.LENGTH_SHORT).show();
            return null;
        }
        if (!user.getPhone().matches("^[1][0-9]{10}$")) {
            Toast.makeText(context, "非法的联系方式", Toast.LENGTH_SHORT).show();
            return null;
        }
        if (!user.getEmail().matches("^[A-Za-z0-9\\u4e00-\\u9fa5]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$")) {
            Toast.makeText(context, "非法的邮箱", Toast.LENGTH_SHORT).show();
            return null;
        }
        if (user.getPasswd().length() < 6) {
            Toast.makeText(context, "非法的密码设定", Toast.LENGTH_SHORT).show();
            return null;
        }
        return user;
    }
}
