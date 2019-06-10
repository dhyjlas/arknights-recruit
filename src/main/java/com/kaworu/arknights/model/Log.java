package com.kaworu.arknights.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Setter
@Getter
@Table(name = "sys_log")
public class Log {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id", columnDefinition="bigint COMMENT '日志ID'")
    private long id;

    @Column(name="ip", columnDefinition="varchar(128) COMMENT '来源IP'")
    private String ip;

    @Column(name="content", columnDefinition="varchar(255) COMMENT '内容'")
    private String content;

    @Column(name="result", columnDefinition="mediumtext COMMENT '结果'")
    private String result;

    @Column(name="date", columnDefinition="varchar(20) COMMENT '时间'")
    private String date;
}
