package com.feng.seckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.feng.seckill.entitys.constant.RedisConstant;
import com.feng.seckill.entitys.constant.UserInfoConstant;
import com.feng.seckill.entitys.po.AccountPO;
import com.feng.seckill.entitys.po.BreakRulePO;
import com.feng.seckill.entitys.po.SeckillResultPO;
import com.feng.seckill.entitys.po.UserPO;
import com.feng.seckill.entitys.vo.HelpPage;
import com.feng.seckill.mapper.AccountMapper;
import com.feng.seckill.mapper.BreakRuleMapper;
import com.feng.seckill.mapper.SeckillResultMapper;
import com.feng.seckill.mapper.UserInfoMapper;
import com.feng.seckill.security.Md5PassEncoder;
import com.feng.seckill.service.PersonalService;
import com.feng.seckill.util.JWTUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.xml.crypto.Data;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author : pcf
 * @date : 2022/2/13 21:37
 */
@Service
public class PersonalServiceImpl implements PersonalService {

    @Autowired
    private BreakRuleMapper breakRuleMapper;
    @Autowired
    private SeckillResultMapper seckillResultMapper;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private AccountMapper accountMapper;
    @Autowired
    private UserInfoMapper userInfoMapper;
    @Autowired
    private Md5PassEncoder passEncoder;

    /**
     * 清除黑名单
     */
    @Override
    public void reflashBlackNames() {
        redisTemplate.delete(RedisConstant.SET_BLACK_TABLE);
        SetOperations<String, String> opsForSet = redisTemplate.opsForSet();
        opsForSet.add(RedisConstant.SET_BLACK_TABLE, "0");
    }

    /**
     * 获得个人破坏规则的记录
     * @param request 请求
     * @return 破坏规则的记录
     */
    @Override
    public List<BreakRulePO> getPersonalBreakRules(HttpServletRequest request) {
        // 拿到 token
        String token = request.getHeader("token");
        // 从 token 中拿到用户id
        String userId = JWTUtils.verify(token).getClaim("userId").asString();

        // 创建包装类
        QueryWrapper<BreakRulePO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", Long.parseLong(userId));
        // 按时间顺序
        queryWrapper.orderByDesc("begin_time");

        return breakRuleMapper.selectList(queryWrapper);
    }

    /**
     * 获得个人成功秒杀的产品记录
     * @param request 请求
     * @param helpPage 分页数据
     * @return 记录
     */
    @Override
    public IPage<SeckillResultPO> getPersonalProduct(HttpServletRequest request, HelpPage helpPage){

        // 拿到 token
        String token = request.getHeader("token");
        // 从 token 中拿到用户id
        String userId = JWTUtils.verify(token).getClaim("userId").asString();

        // 创建包装类
        QueryWrapper<SeckillResultPO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", Long.parseLong(userId));
        // 按时间顺序
        queryWrapper.orderByDesc("create_date");

        Page<SeckillResultPO> page = new Page<>();
        // 复制属性
        BeanUtils.copyProperties(helpPage, page);

        return seckillResultMapper.selectPage(page, queryWrapper);
    }

    @Override
    public void createUser(Integer number) {

        System.out.println("开始生成账户");

        for (int i = 1; i <= number; i++){
            UserPO userPO = new UserPO();
            createUserDetail(userPO, i);
            userInfoMapper.insert(userPO);

            AccountPO accountPO = new AccountPO();
            createAccount(accountPO, i);
            accountPO.setUserId(userPO.getUserId());
            accountMapper.insert(accountPO);

            userInfoMapper.updateAccId(accountPO.getAccountId(), userPO.getUserId());
        }
        System.out.println("生成账户完成");
    }

    private UserPO createUserDetail(UserPO userPO, int i){
        // 用户名和账号
        String s = i + "";

        userPO.setName("user" + s);
        userPO.setLoginAccount(s + "");

        userPO.setGender((i % 2) + "");

        userPO.setPassword(passEncoder.encode(UserInfoConstant.DEFAULT_PWD + s));

        userPO.setAge(20);

        // 设置普通用户权限
        userPO.setRoleId(2L);
        userPO.setRoleName("个人客户");

        // 工作状态
        userPO.setWorkStatus("1");

        // 设置创建日期
        Date date = new Date();
        userPO.setCreateDate(date);

        return userPO;
    }

    // 生成账户
    private AccountPO createAccount(AccountPO accountPO, int i){
        accountPO.setAccountNum(i + "");
        accountPO.setBalance(UserInfoConstant.DEFAULT_MONEY);
        accountPO.setCreateDate(new Date());
        accountPO.setPayPassword(UserInfoConstant.DEFAULT_PWD);
        return accountPO;
    }
}
