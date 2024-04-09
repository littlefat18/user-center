package com.dzl.usercenter.service.impl;

import cn.hutool.core.lang.Pair;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzl.usercenter.common.ErrorCode;
import com.dzl.usercenter.common.ResultUtils;
import com.dzl.usercenter.constant.UserConstant;
import com.dzl.usercenter.exception.BusinessException;
import com.dzl.usercenter.mapper.UserMapper;
import com.dzl.usercenter.model.domain.User;
import com.dzl.usercenter.model.request.UpdateTagRequest;
import com.dzl.usercenter.model.request.UserQueryRequest;
import com.dzl.usercenter.service.UserService;
import com.dzl.usercenter.utils.AlgorithmUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.description.method.MethodDescription;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.dzl.usercenter.constant.RedisConstant.USER_NOT_LOGIN_KEY;
import static com.dzl.usercenter.constant.RedisConstant.USER_RECOMMEND_KEY;
import static com.dzl.usercenter.constant.UserConstant.USER_LOGIN_STATE;
import static com.dzl.usercenter.utils.StringUtils.stringJsonListToLongSet;

/**
 * @author genius
 * @description 针对表【user(用户表)】的数据库操作Service实现
 * @createDate 2022-04-24 09:43:11
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Resource
    private UserMapper userMapper;
    /**
     * 盐值 混淆密码
     */
    private static final String SALT = "yupi";

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword,String username) {
        // 1.校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword,username)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");

        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户名字太短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户密码太短");
        }
        if (username.length()>12){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"昵称过长");
        }

        // 账户不能包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号包含特殊字符");
        }
        // 密码和校验密码相同
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码校验密码不相等！");
        }

        // 账户不能重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        long count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户已存在");
        }

        // 昵称可重复吗？
//        queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("username", username);
//        count = userMapper.selectCount(queryWrapper);
//        if (count > 0) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR,"昵称");
//        }

        // 2.加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 3.插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setCreateTime(new Date());
        user.setUsername(username);
        user.setTags("[]");
        user.setUserIds("[]");
        boolean saveResult = this.save(user);
        if (!saveResult) {
            return -1;
        }
        return user.getId();
    }

    /**
     * 根据标签搜索用户(内存过滤)
     *
     * @param tagNameList 用户要拥有的标签
     * @return
     */
    @Override
    public List<User> searchUsersByTags(List<String> tagNameList) {
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 1.先查询所有用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        // 2.在内存中判断是否包含要求的标签
        List<User> userList = userMapper.selectList(queryWrapper);
        Gson gson = new Gson();
        return userList.stream().filter(user -> {
            String tagsStr = user.getTags();
            Set<String> temTagNameSet = gson.fromJson(tagsStr, new TypeToken<Set<String>>() {}.getType());
            temTagNameSet = Optional.ofNullable(temTagNameSet).orElse(new HashSet<>());
            for (String tagName:tagNameList) {
                if (!temTagNameSet.contains(tagName)){
                    return false;
                }
            }
            return true;
        }).map(this::getSafetyUser).collect(Collectors.toList());
    }

    /**
     * 根据标签搜索用户(SQL 查询版)
     *
     * @param tagNameList 用户要拥有的标签
     * @return
     */
    @Deprecated
    private List<User> searchUsersBySQL(List<String> tagNameList) {
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        // 拼接 and 查询
        // like '%Java%' and like '%Python%'
        for (String tagName : tagNameList) {
            queryWrapper = queryWrapper.like("tags",tagName);
        }
        List<User> userList = userMapper.selectList(queryWrapper);
        return userList.stream().map(this::getSafetyUser).collect(Collectors.toList());
    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {

        // 1.校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号密码为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号过短");
        }
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码过短");
        }

        // 账户不能包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号有特殊字符");
        }
        // 2.加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 3.查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        //用户不存在
        if (user == null) {
            log.info("user login failed,userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户不存在");
        }
        // 4.用户脱敏
        User safetyUser = getSafetyUser(user);
        // 5.记录用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, safetyUser);
        System.out.println(request.getSession().getId());

        return safetyUser;
    }

    /**
     * 用户脱敏方法
     * @param originUser
     * @return
     */
    @Override
    public User getSafetyUser(User originUser){
        // 4.用户脱敏
        if (originUser==null){
            return null;
        }
        User safetyUser = new User();
        safetyUser.setId(originUser.getId());
        safetyUser.setUsername(originUser.getUsername());
        safetyUser.setUserAccount(originUser.getUserAccount());
        safetyUser.setAvatarUrl(originUser.getAvatarUrl());
        safetyUser.setGender(originUser.getGender());
        safetyUser.setPhone(originUser.getPhone());
        safetyUser.setProfile(originUser.getProfile());
        safetyUser.setEmail(originUser.getEmail());
        safetyUser.setUserStatus(originUser.getUserStatus());
        safetyUser.setCreateTime(originUser.getCreateTime());
        safetyUser.setUserRole(originUser.getUserRole());
        safetyUser.setPlanetCode(originUser.getPlanetCode());
        safetyUser.setTags(originUser.getTags());
        safetyUser.setUserIds(originUser.getUserIds());
        return safetyUser;

    }

    @Override
    public int userLogout(HttpServletRequest request) {
        //移除用户登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }

    @Override
    public int updateUser(User user, User loginUser) {
        long userId = user.getId();
        if (userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 补充校验，如果用户没有传任何要更新的值，就直接报错，不用执行 update 语句
        // 如果是管理员，允许更新任意用户
        // 如果不是管理员，只允许更新当前（自己的）信息
        if (!isAdmin(loginUser) && userId != loginUser.getId()) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        User oldUser = userMapper.selectById(userId);
        if (oldUser == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        return userMapper.updateById(user);

    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        if (userObj == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        return (User) userObj;
    }

    @Override
    public String redisFormat(Long key) {
        return String.format("jingsaibang:user:search:%s", key);
    }

    /**
     * 流处理
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateTagById(UpdateTagRequest updateTag, User currentUser) {
        long id = updateTag.getId();
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "该用户不存在");
        }
        Set<String> newTags = updateTag.getTagList();
        if (newTags.size() > 12) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "最多设置12个标签");
        }
        if (!isAdmin(currentUser) && id != currentUser.getId()) {
            throw new BusinessException(ErrorCode.NO_AUTH, "无权限");
        }
        User user = userMapper.selectById(id);
        Gson gson = new Gson();
        Set<String> oldTags = gson.fromJson(user.getTags(), new TypeToken<Set<String>>() {
        }.getType());
        Set<String> oldTagsCapitalize = toCapitalize(oldTags);
        Set<String> newTagsCapitalize = toCapitalize(newTags);

        // 添加 newTagsCapitalize 中 oldTagsCapitalize 中不存在的元素
        oldTagsCapitalize.addAll(newTagsCapitalize.stream().filter(tag -> !oldTagsCapitalize.contains(tag)).collect(Collectors.toSet()));
        // 移除 oldTagsCapitalize 中 newTagsCapitalize 中不存在的元素
        oldTagsCapitalize.removeAll(oldTagsCapitalize.stream().filter(tag -> !newTagsCapitalize.contains(tag)).collect(Collectors.toSet()));
        String tagsJson = gson.toJson(oldTagsCapitalize);
        user.setTags(tagsJson);
        return userMapper.updateById(user);
    }
    /**
     * String类型集合首字母大写
     *
     * @param oldSet 原集合
     * @return 首字母大写的集合
     */
    private Set<String> toCapitalize(Set<String> oldSet) {
        return oldSet.stream().map(StringUtils::capitalize).collect(Collectors.toSet());
    }
    @Override
    public Page<User> recommendUsers(long pageSize, long pageNum, HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        // 未登录
        if (userObj == null) {
            String notLoginKey = USER_NOT_LOGIN_KEY;
            Page<User> userPage = (Page<User>) redisTemplate.opsForValue().get(notLoginKey);
            if (userPage != null){
                return userPage;
            }
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            userPage= page(new Page<>(pageNum, pageSize), queryWrapper);
            // 写缓存
            try {
                redisTemplate.opsForValue().set(notLoginKey,userPage,30, TimeUnit.SECONDS);
            }catch (Exception e){
                log.error("redis set key error",e);
            }
            return userPage;
        }
        User loginUser = (User) userObj;
//        User loginUser = getLoginUser(request);
        long userId = loginUser.getId();
        // 如果有缓存,直接读缓存
        String redisKey = USER_RECOMMEND_KEY + userId;
        Page<User> userPage = (Page<User>) redisTemplate.opsForValue().get(redisKey);
        if (userPage!= null){
            return userPage;
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        userPage= page(new Page<>(pageNum, pageSize), queryWrapper);
        // 写缓存
        try {
            redisTemplate.opsForValue().set(redisKey,userPage,30, TimeUnit.SECONDS);
        }catch (Exception e){
            log.error("redis set key error",e);
        }
//        List<User> userList = userService.list(queryWrapper);
//        List<User> list = userList.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
        return userPage;
    }

    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    @Override
    public boolean isAdmin(HttpServletRequest request) {
        // 仅管理员可查询
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        return user != null && user.getUserRole() == UserConstant.ADMIN_ROLE;
    }

    /**
     * 是否为管理员
     *
     * @param loginUser
     * @return
     */
    @Override
    public boolean isAdmin(User loginUser) {
        return loginUser != null && loginUser.getUserRole() == UserConstant.ADMIN_ROLE;
    }

    @Override
    public List<User> matchUsers(long num, User loginUser) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "tags");
        queryWrapper.isNotNull("tags");
        List<User> userList = this.list(queryWrapper);
        String tags = loginUser.getTags();
        Gson gson = new Gson();
        List<String> tagList = gson.fromJson(tags, new TypeToken<List<String>>() {
        }.getType());
        // 用户列表的下标 => 相似度
        List<Pair<User, Long>> list = new ArrayList<>();
        // 依次计算所有用户和当前用户的相似度
        for (int i = 0; i < userList.size(); i++) {
            User user = userList.get(i);
            String userTags = user.getTags();
            // 无标签或者为当前用户自己
            if (StringUtils.isBlank(userTags) || user.getId() == loginUser.getId()) {
                continue;
            }
            List<String> userTagList = gson.fromJson(userTags, new TypeToken<List<String>>() {
            }.getType());
            // 计算分数
            long distance = AlgorithmUtils.minDistance(tagList, userTagList);
            list.add(new Pair<>(user, distance));
        }
        // 按编辑距离由小到大排序
        List<Pair<User, Long>> topUserPairList = list.stream()
                .sorted((a, b) -> (int) (a.getValue() - b.getValue()))
                .limit(num)
                .collect(Collectors.toList());
        // 原本顺序的 userId 列表
        List<Long> userIdList = topUserPairList.stream().map(pair -> pair.getKey().getId()).collect(Collectors.toList());
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.in("id", userIdList);
        // 1, 3, 2
        // User1、User2、User3
        // 1 => User1, 2 => User2, 3 => User3
        Map<Long, List<User>> userIdUserListMap = this.list(userQueryWrapper)
                .stream()
                .map(user -> getSafetyUser(user))
                .collect(Collectors.groupingBy(User::getId));


        List<User> finalUserList = new ArrayList<>();
        for (Long userId : userIdList) {
            finalUserList.add(userIdUserListMap.get(userId).get(0));
        }
        return finalUserList;

    }

    @Override
    public List<User> searchFriend(UserQueryRequest userQueryRequest, User currentUser) {
        String searchText = userQueryRequest.getSearchText();
        User user = this.getById(currentUser.getId());
        Set<Long> friendsId = stringJsonListToLongSet(user.getUserIds());
        List<User> users = new ArrayList<>();
        Collections.shuffle(users);
        friendsId.forEach(id -> {
            User u = this.getById(id);
            if (u.getUsername().contains(searchText)) {
                users.add(u);
            }
        });
        return users;
    }

    @Override
    public boolean deleteFriend(User currentUser, Long id) {
        User loginUser = this.getById(currentUser.getId());
        User friendUser = this.getById(id);
        Set<Long> friendsId = stringJsonListToLongSet(loginUser.getUserIds());
        Set<Long> fid = stringJsonListToLongSet(friendUser.getUserIds());
        friendsId.remove(id);
        fid.remove(loginUser.getId());
        String friends = new Gson().toJson(friendsId);
        String fids = new Gson().toJson(fid);
        loginUser.setUserIds(friends);
        friendUser.setUserIds(fids);
        return this.updateById(loginUser) && this.updateById(friendUser);
    }

    @Override
    public List<User> getFriendsById(User currentUser) {
        User loginUser = this.getById(currentUser.getId());
        Set<Long> friendsId = stringJsonListToLongSet(loginUser.getUserIds());
        return friendsId.stream().map(user -> this.getSafetyUser(this.getById(user))).collect(Collectors.toList());
    }

    @Override
    public Boolean isFriend(Integer id , User currentUser) {
        //String friendsId = currentUser.getUserIds();
        long currentUserId = currentUser.getId();
        User nowUser = getById(currentUserId);
        String friendsId = nowUser.getUserIds();
        System.out.println("===================="+friendsId);
        List<Integer> friendsIds = JSONUtil.toList(friendsId, Integer.class);
        System.out.println("===================="+friendsIds);
        if (friendsIds == null){
            return false;
        }
        return friendsIds.contains(id);
    }


}




