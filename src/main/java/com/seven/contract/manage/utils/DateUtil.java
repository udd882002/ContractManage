package com.seven.contract.manage.utils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * @author seven
 * java8 计算两个日期之间的天数
 */
public interface DateUtil {
 
    /**
     * 计算当前日期与{@code endDate}的间隔天数
     *
     * @param endDate
     * @return 间隔天数
     */
     static long until(Date endDate){
         Instant instant = endDate.toInstant();
         ZoneId zoneId = ZoneId.systemDefault();

         // atZone()方法返回在指定时区从此Instant生成的ZonedDateTime。
         LocalDate localDate = instant.atZone(zoneId).toLocalDate();

         return LocalDate.now().until(localDate, ChronoUnit.DAYS);
    }
 
    /**
     * 计算日期{@code startDate}与{@code endDate}的间隔天数
     *
     * @param startDate
     * @param endDate
     * @return 间隔天数
     */
    static long until(Date startDate, Date endDate){

        ZoneId zoneId = ZoneId.systemDefault();

        Instant startInstant = startDate.toInstant();
        LocalDate startLocalDate = startInstant.atZone(zoneId).toLocalDate();

        Instant endInstant = endDate.toInstant();
        LocalDate endLocalDate = endInstant.atZone(zoneId).toLocalDate();

        return startLocalDate.until(endLocalDate, ChronoUnit.DAYS);
    }

}
