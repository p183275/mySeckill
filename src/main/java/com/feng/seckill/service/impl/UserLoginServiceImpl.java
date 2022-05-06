package com.feng.seckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.feng.seckill.component.SmsComponent;
import com.feng.seckill.entitys.constant.*;
import com.feng.seckill.entitys.po.UserPO;
import com.feng.seckill.entitys.vo.UserInfoAndAccountVO;
import com.feng.seckill.entitys.vo.UserLoginVO;
import com.feng.seckill.entitys.vo.UserRegisterVO;
import com.feng.seckill.entitys.vo.UserVO;
import com.feng.seckill.exception.entity.SQLDuplicateException;
import com.feng.seckill.mapper.UserInfoMapper;
import com.feng.seckill.mapper.UserLoginMapper;
import com.feng.seckill.security.Md5PassEncoder;
import com.feng.seckill.service.UserLoginService;
import com.feng.seckill.util.JWTUtils;
import com.feng.seckill.util.RegisterUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author : pcf
 * @date : 2022/1/15 16:26
 */
@Slf4j
@Service
public class UserLoginServiceImpl extends ServiceImpl<UserLoginMapper, UserPO> implements UserLoginService {

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private Md5PassEncoder passwordEncoder;
    @Autowired
    private UserLoginMapper userLoginMapper;
    @Autowired
    private UserInfoMapper userInfoMapper;
    @Autowired
    private SmsComponent smsComponent;


    /**
     * 用户注册
     * @param vo 用户信息封装类
     */
    @Override
    public void registerUser(UserRegisterVO vo) {

        // 拿到电话号码
        String phoneNumber = vo.getPhoneNumber();
        // 判断电话号码是否为空
        if (phoneNumber.isEmpty())
            throw new RuntimeException(ExceptionConstant.PHONE_NUMBER_NOT_NULL_EXCEPTION);
        // 判断电话号码是否格式错误 phoneNumber
        if (phoneNumber.length() != 11 || !"1".equals(phoneNumber.substring(0, 1)))
            throw new RuntimeException(ExceptionConstant.PHONE_NUMBER_NOT_ILLEGAL_EXCEPTION);

        // 对比验证码
        String s = redisTemplate.opsForValue().get(phoneNumber + LoginConstant.CHECK_CODE);
        // 验证码格式 时间戳_验证码  用_分隔
        String checkCodeFromRedis = s != null ? s.split("_")[1] : null;
        String checkCodeFromUser = vo.getCheckCode();

        // 判断两处验证码是否相等
        if (checkCodeFromRedis == null || checkCodeFromUser == null)
            throw new RuntimeException(ExceptionConstant.CHECK_CODE_ERROR_EXCEPTION);
        if (!checkCodeFromRedis.equals(checkCodeFromUser))
            throw new RuntimeException(ExceptionConstant.CHECK_CODE_ERROR_EXCEPTION);

        // 创建用户
        UserPO userPO = new UserPO();

        // 复制属性
        BeanUtils.copyProperties(vo, userPO);

        // 拿到当前时间
        Date date = new Date();

        // 密码加密
        String encodePassword = passwordEncoder.encode(vo.getPassword() + vo.getLoginAccount());
        userPO.setPassword(encodePassword);

        // 设置属性
        userPO.setRoleId(RoleConstant.RoleEnum.DEFAULT_ROLE.getCode());
        userPO.setCreateDate(date);
        userPO.setRoleName(RoleConstant.RoleEnum.DEFAULT_ROLE.getMsg());
        // 显示
        userPO.setShowStatus(MyBatisConstant.LogicDelete.NOT_DELETE.getCode());
        // 保存
        try {
            userLoginMapper.insert(userPO);
        }catch (DuplicateKeyException e){
            throw new SQLDuplicateException("账号已存在");
        }
    }

    /**
     * 发送短信拿到验证码
     * @param phoneNumber 电话号码
     */
    @Override
    public void getCheckCode(String phoneNumber) {

        // 判断电话号码是否为空
        if (phoneNumber == null)
            throw new RuntimeException(ExceptionConstant.PHONE_NUMBER_NOT_NULL_EXCEPTION);

        // 判断电话号码是否格式错误
        if (phoneNumber.length() != 11 || !"1".equals(phoneNumber.substring(0, 1)))
            throw new RuntimeException(ExceptionConstant.PHONE_NUMBER_NOT_ILLEGAL_EXCEPTION);

        // 除去
        phoneNumber = phoneNumber.substring(0, 11);

        // 获取 redis 操作对象
        ValueOperations<String, String> operations = redisTemplate.opsForValue();

        // TODO 防止 60s 内多次发送验证码
        // 拿到 redis 中的对象
        String redisMsg = operations.get(phoneNumber + LoginConstant.CHECK_CODE);
        // 拿到当前时间戳
        long curTime = Calendar.getInstance().getTimeInMillis();
        if (redisMsg != null){
            // 拿到当前时间戳
            String[] split = redisMsg.split("_");
            // 判断时间是否超出 60 s
            long oriTime = Long.parseLong(split[0]);
            // 如果超出则抛出异常
            if (curTime - oriTime <= 60000)
                throw new RuntimeException(ExceptionConstant.CHECK_CODE_MANY_TIME);
        }

        // 生成验证码
        String checkCode = RegisterUtil.getCheckCode();

        // TODO 调用短信接口
        smsComponent.sendSmsCode(phoneNumber, checkCode, LoginConstant.EXPIRE_TIME.toString());

        // 存入redis，并设置三分钟过期
        operations.set(phoneNumber + LoginConstant.CHECK_CODE, curTime + "_" + checkCode,
                LoginConstant.EXPIRE_TIME, TimeUnit.MINUTES);
    }

    /**
     * 管理员登录
     * @param loginVO 登录信息封装类欸
     * @return token
     */
    @Override
    public Map<String, String> adminLogin(UserLoginVO loginVO) {

        // 拿出账号，并判断是否为空
        String loginAccount = loginVO.getLoginAccount();
        if (loginAccount == null || loginAccount.length() <= 0)
            throw new RuntimeException(ExceptionConstant.LOGIN_ACCOUNT_NOT_NULL_EXCEPTION);

        // 查库
        QueryWrapper<UserPO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("login_account", loginAccount);
        queryWrapper.eq("role_id", RoleConstant.RoleEnum.ADMIN_ROLE.getCode());
        UserPO userPO = userLoginMapper.selectOne(queryWrapper);

        // 如果为空，则抛出账号密码异常
        if (userPO == null)
            throw new RuntimeException(ExceptionConstant.LOGIN_ACCOUNT_OR_PASSWORD_FILED_EXCEPTION);

        // 对比密码
        boolean matches = passwordEncoder.matches(loginVO.getPassword() + userPO.getLoginAccount(),
                userPO.getPassword());
        if (!matches)
            throw new RuntimeException(ExceptionConstant.LOGIN_ACCOUNT_OR_PASSWORD_FILED_EXCEPTION);

        // 生成token，设置过期时间为1天
        Map<String, String> map = new HashMap<>();
        map.put("loginAccount", userPO.getLoginAccount());
        map.put("userName", userPO.getName());
        map.put("userId", String.valueOf(userPO.getUserId()));
        map.put("roleId", String.valueOf(userPO.getRoleId()));
        String token = JWTUtils.getToken(map, TokenConstant.TOKEN_EXPIRE_TIME);

        // 将token放入redis，设置一天过期
        redisTemplate.opsForValue().set(userPO.getLoginAccount() + TokenConstant.TOKEN, token,
                Duration.ofDays(TokenConstant.DurationTime.ONE_DAY.getCode()));

        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", token);

        return tokenMap;
    }

    /**
     * 拿到当前登录用户的所有信息
     * @param request 请求
     * @return 用户信息
     */
    @Override
    public UserInfoAndAccountVO getLoginUserInfo(HttpServletRequest request) {

        // 拿到用户主键
        String token = request.getHeader("token");
        String userId = JWTUtils.verify(token).getClaim("userId").asString();

        return userInfoMapper.getUserInfoAndAccountVO(Long.parseLong(userId));
    }
}
