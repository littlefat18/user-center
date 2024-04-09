package com.dzl.usercenter.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dzl.usercenter.mapper.UserMapper;
import com.dzl.usercenter.model.domain.User;
import com.dzl.usercenter.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.dzl.usercenter.constant.RedisConstant.PRECACHE_JOB_KEY;
import static com.dzl.usercenter.constant.RedisConstant.USER_RECOMMEND_KEY;

/**
 * @author dzl
 * @date 2023/5/10 14:58
 */
@Component
@Slf4j
public class PreCacheJob {

    @Resource
    private UserMapper userMapper;

    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    @Resource
    private RedissonClient redissonClient;

    // 重点用户
    private List<Long> mainUserList = Arrays.asList(1L);

    // 每天执行 缓存预热推荐用户
    @Scheduled(cron = "0,31 52 21 * * *")
    public void doCacheRecommendUser(){
        RLock lock = redissonClient.getLock(PRECACHE_JOB_KEY);
        try {
            if (lock.tryLock(0,-1,TimeUnit.MINUTES)){
                System.out.println("getLock:"+Thread.currentThread().getId());
                Thread.sleep(30000);
                for (Long userId : mainUserList){
                    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                    Page<User> userPage= userService.page(new Page<>(1, 20), queryWrapper);
//                    List<User> records = userPage.getRecords();
                    String redisKey = USER_RECOMMEND_KEY + userId;
//            userPage = (Page<User>) redisTemplate.opsForValue().get(redisKey);
                    // 写缓存
                    try {
                        redisTemplate.opsForValue().set(redisKey,userPage,30, TimeUnit.SECONDS);
                    }catch (Exception e){
                        log.error("redis set key error",e);
                    }
                }
            }
        } catch (InterruptedException e) {
            log.error("doCacheRecommendUser error",e);
        }finally {
            //只能释放自己的锁
            if (lock.isHeldByCurrentThread()){
                System.out.println("unLock:"+Thread.currentThread().getId());
                lock.unlock();
            }
        }
  }
}
