package com.cxhello.sign.controller;

import com.cxhello.sign.entity.UserSignVo;
import com.cxhello.sign.exception.BusinessException;
import com.cxhello.sign.util.Result;
import com.cxhello.sign.util.ResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.BitFieldSubCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @author cxhello
 * @date 2021/7/29
 */
@RestController
public class UserSignController {

    public final static String USER_SIGN_IN = "userSign:%d:%d";

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 签到接口
     * @param id
     * @return
     */
    @GetMapping(value = "/sign/{id}")
    public Result sign(@PathVariable Long id) {
        String userSignInKey = String.format(USER_SIGN_IN, LocalDate.now().getYear(), id);
        long day = Long.parseLong(LocalDate.now().format(DateTimeFormatter.ofPattern("MMdd")));
        if (stringRedisTemplate.opsForValue().getBit(userSignInKey, day)) {
            throw new BusinessException("今日已签到");
        }
        stringRedisTemplate.opsForValue().setBit(userSignInKey, day, true);
        return ResultUtils.success();
    }

    @GetMapping(value = "/getSignInfo/{id}")
    public Result getSignInfo(@PathVariable Long id) {
        String userSignInKey = String.format(USER_SIGN_IN, LocalDate.now().getYear(), id);
        LocalDate localDate = LocalDate.now();
        // 当年累计签到天数
        Long totalDays = stringRedisTemplate.execute((RedisCallback<Long>) connection -> connection.bitCount(userSignInKey.getBytes()));
        // 当年连续签到天数
        int signCount = 0;
        List<Long> longList = stringRedisTemplate.opsForValue().bitField(userSignInKey, BitFieldSubCommands.create().get(BitFieldSubCommands.BitFieldType.signed(localDate.getDayOfMonth())).valueAt(localDate.getMonthValue() * 100 + 1));
        if (longList != null && longList.size() > 0) {
            //可能该用户这个月就没有签到过,需要判断一下,如果是空就给一个默认值0
            long v = longList.get(0) == null ? 0 : longList.get(0);
            for (int i = 0; i < localDate.getDayOfMonth(); i++) {
                // 如果是连续签到得到的long值右移一位再左移一位后与原始值不相等,代表低位为1,连续天数加一
                if (v >> 1 << 1 == v) {
                    break;
                }
                signCount += 1;
                v >>= 1;
            }
        }
        long day = Long.parseLong(LocalDate.now().format(DateTimeFormatter.ofPattern("MMdd")));
        // 今日是否签到
        int isSign = stringRedisTemplate.opsForValue().getBit(userSignInKey, day) ? 1 : 0;
        UserSignVo userSignVo = new UserSignVo();
        userSignVo.setTotalDays(totalDays);
        userSignVo.setSignCount(signCount);
        userSignVo.setIsSign(isSign);
        return ResultUtils.result(userSignVo);
    }

}
