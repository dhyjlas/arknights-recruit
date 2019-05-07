package com.kaworu.arknights.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Setter
@Getter
@Table(name = "sys_player")
public class Player implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id", columnDefinition="bigint COMMENT 'ID'")
    private long id;

    @Column(name="name", columnDefinition="varchar(256) COMMENT '名称'")
    private String name;

    @Column(name="occupation", columnDefinition="varchar(32) COMMENT '职业'")
    private String occupation;

    @Column(name="level", columnDefinition="varchar(32) COMMENT '星级'")
    private String level;

    @Column(name="sex", columnDefinition="varchar(32) COMMENT '性别'")
    private String sex;

    @Column(name="tag", columnDefinition="varchar(256) COMMENT '标签'")
    private String tag;

    @Column(name="access", columnDefinition="varchar(256) COMMENT '获取途径'")
    private String access;

    @Column(name="pic", columnDefinition="varchar(1024) COMMENT '头像图片地址'")
    private String pic;

    @Column(name="src", columnDefinition="varchar(1024) COMMENT '链接地址'")
    private String src;

    @Transient
    private List<String> tags;
}
