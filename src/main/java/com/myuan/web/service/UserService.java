package com.myuan.web.service;

import com.myuan.web.dao.UserDao;
import com.myuan.web.entity.MyResult;
import com.myuan.web.entity.MyRole;
import com.myuan.web.entity.MyUser;
import com.myuan.web.entity.MyZan;
import com.myuan.web.utils.IDUtil;
import com.myuan.web.utils.SaltPasswordUtil;
import com.myuan.web.utils.SendMail;
import com.myuan.web.utils.SwitchUtil;
import java.util.Date;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/*
 * @author liuwei
 * @date 2018/1/19 18:04
 * user业务层
 */
@Log4j
@Service
public class UserService {

    @Autowired
    private UserDao userDao;
    @Autowired
    private RoleService roleService;
    @Autowired
    private ZanService zanService;
    @Autowired
    private MailService mailService;
    @Autowired
    private SignService signService;

    /**
     * 用户名查用户
     */
    public MyUser getUserByName(String name) {
        return userDao.findMyUsersByName(name);
    }

    /**
     * 用户ID查用户
     */
    @Cacheable(value = "users", key = "'user_'+#id")
    public MyUser getUserById(Long id) {
        return userDao.findMyUsersById(id);
    }

    /**
     * 邮箱查用户
     */
    public MyUser getUserByEmail(String email) {
        return userDao.findMyUsersByEmail(email);
    }

    /**
     * 用户注册
     */
    @Transactional
    public MyResult saveUser(MyUser user) {
        try {
            if (getUserByName(user.getName()) != null) {
                return MyResult.error("用户名已被注册");
            }
            if (getUserByEmail(user.getEmail()) != null) {
                return MyResult.error("邮箱已被注册");
            }
            user.preInsert();
            user.setImg(IDUtil.randomer() + ".jpg");
            user.setCity("隐藏");
            user.setKiss(200);
            user.setLocked("0");
            userDao.save(user);
            //点赞表
            zanService.addUserZan(user.getId());
            //签到表
            signService.addSign(user.getId());
            log.info("用户：" + user.getName() + "注册成功");
            saveUserRole(user.getId());
            return MyResult.action("/user/login", "注册成功");
        } catch (Exception ex) {
            log.info("用户注册更新异常: " + ex.toString());
        }
        return MyResult.error("系统异常,请重试");
    }

    /**
     * 为新建用户分配 user角色
     */
    @Transactional
    public void saveUserRole(Long userId) {
        MyRole role = roleService.findRoleByType("user");
        roleService.saveUserRole(userId, role.getId());
    }

    /**
     * 更新用户密码
     */
    @Transactional
    @CacheEvict(value = "users", key = "'user_'+#id")
    public MyResult updateUserPass(Long id, String pass) {
        String newPass = SaltPasswordUtil.getNewPassword(pass);
        userDao.updateMyUserPass(id, newPass);
        return MyResult.ok("密码修改成功");
    }

    /**
     * 更新用户信息
     */
    @CacheEvict(value = "users", key = "'user_'+#id")
    public MyResult updateUserInfo(Long id, String name, String sex, String province, String city, String description) {
        try {
            MyUser user = getUserByName(name);
            if (user != null && !user.getId().equals(id)) {
                return MyResult.error("用户名已存在");
            }
            String addr = province + "/" + city;
            userDao.updateUserInfo(id, name, sex, addr, description, new Date());
            return MyResult.ok("信息修改成功");
        } catch (Exception ex) {
            ex.printStackTrace();
            log.info("用户信息修改异常");
        }
        return MyResult.error("系统异常，请重试");
    }

    /**
     * <liuwei> [2018/2/26 10:06] 增加飞吻
     */
    @CacheEvict(value = "users", key = "'user_'+#id")
    public void addUserKiss(Long id, Integer kiss) {
        userDao.addUserKiss(id, kiss);
    }

    /**
     * <liuwei> [2018/3/1 16:07] 重置密码
     */
    public MyResult resetPass(String email) {
        MyUser user = getUserByEmail(email);
        if (user == null) {
            return MyResult.error("邮箱不存在");
        }
        return mailService.sendSimpleMail(email, user.getName(), IDUtil.code());
    }
}
