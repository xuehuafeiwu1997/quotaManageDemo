package com.xmy.quotamanage;

import com.xmy.quotamanage.config.GlobalConfig;
import com.xmy.quotamanage.dao.QuotaDao;
import com.xmy.quotamanage.dao.UserDao;
import com.xmy.quotamanage.entity.Quota;
import com.xmy.quotamanage.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@EnableScheduling
class QuotaManageApplicationTests {

    SimpleDateFormat sdf = new SimpleDateFormat("");

    @Autowired
    QuotaDao quotaDao;

    @Autowired
    UserDao userDao;

    @Resource(name = "defaultThreadPool")
    ThreadPoolTaskExecutor executor;

    @Test
    void testMutiUserUpdateQuota() {

    }

    @Test
    void contextLoads() {

    }

    @Test
    void addQuota() {
        System.out.println("执行了增加额度的方法");
        System.out.println(quotaDao.getQuotaById(1));
    }

    @Test
    void getAllUsers() {
        List<User> users = new ArrayList<>();
        users = userDao.getAllUsers();
        System.out.println(users.size());
        System.out.println(users);
    }

    @Test
    void updateQuota() {
        System.out.println("更新额度");
        Quota quota = new Quota((long) 1,5000);
        quotaDao.updateQuota(quota);
    }

    //每隔5秒种执行一次任务
    @Test
    @Scheduled(cron = "*/5 * * * * ?")
    void scheduledTest() throws InterruptedException {
        System.out.println("springBoot的定时任务" + Thread.currentThread().getName());
        this.test1();
    }

    @Test
    void test1() throws InterruptedException {
        List<User> users = userDao.getAllUsers();
        for (User user : users) {
            new Thread(() -> {
                System.out.println("第"+user.getId()+"个用户增加额度"+Thread.currentThread().getName());
                Quota quota = quotaDao.getQuotaById(user.getQuotaId());
                synchronized (this) {
                    quota.setQcount(quota.getQcount() + 1000);
                }
                quotaDao.updateQuota(quota);
            }).start();
            Thread.sleep(1000);
            new Thread(()-> {
                System.out.println("第"+user.getId()+"个用户减少额度"+Thread.currentThread().getName());
                Quota quota = quotaDao.getQuotaById(user.getQuotaId());
                synchronized (this) {
                    quota.setQcount(quota.getQcount() - 500);
                }
                quotaDao.updateQuota(quota);
            }).start();
        }
    }
}
