package com.dzl.usercenter.constant;

/**
 * @author dzl
 * @date 2023/5/10 14:17
 */
public interface RedisConstant {

    String USER_RECOMMEND_KEY = "jingsaibang:user:recommend:";

    String USER_NOT_LOGIN_KEY = "jingsaibang:user:notlogin";

    String PRECACHE_JOB_KEY = "jingsaibang:precachejob:docache:lock";

    String JOIN_TEAM_KEY = "jingsaibang:join_team:lock";
}
