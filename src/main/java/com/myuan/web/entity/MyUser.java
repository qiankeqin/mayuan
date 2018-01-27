package com.myuan.web.entity;

import javax.persistence.Column;
import javax.validation.constraints.Size;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import org.hibernate.validator.constraints.Email;

/*
 * @author liuwei
 * @date 2018/1/19 16:14
 *  用户类
 */
@Data
@Entity
public class MyUser extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 5615703065557959847L;

    //自增长主键
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    @Size(min = 3, max = 10, message = "用户名长度在3到10之间")
    private String name;

    private String sex;
    @Size(min = 6, max = 16, message = "密码长度在6到16之间")
    private String password;
    @Email
    @Column(unique = true)
    private String email;

    private String city;

    private String img;

    private Integer kiss;

    private String locked;

    private String description;

}
