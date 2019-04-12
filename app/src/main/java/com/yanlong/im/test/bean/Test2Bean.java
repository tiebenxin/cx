package com.yanlong.im.test.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class Test2Bean {
    @Unique
    private String name;
    @Id(autoincrement = true)
    private Long id;






    @Generated(hash = 1300498305)
    public Test2Bean(String name, Long id) {
        this.name = name;
        this.id = id;
    }

    @Generated(hash = 2141291037)
    public Test2Bean() {
    }






    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
