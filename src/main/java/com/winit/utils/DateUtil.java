package com.winit.utils;

import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

	/**
	 * 获取当前日期前一天时间
	 * @param now
	 * @param sdf
	 * @return
	 */
	public static Date getNowDateBeforOneDay(Date now,SimpleDateFormat  sdf){
		Date dBefore = new Date();
		Calendar calendar = Calendar.getInstance(); //得到日历
		calendar.setTime(now);//把当前时间赋给日历
		calendar.add(Calendar.DAY_OF_MONTH, -1);  //设置为前一天
		dBefore = calendar.getTime();   //得到前一天的时间

		String defaultStartDate = sdf.format(dBefore);    //格式化前一天
		Date date = null ;
		try {
			date = sdf.parse(defaultStartDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}
	
	/**
	 * 获取当天时间
	 * @param sdf
	 * @return
	 */
	public static Date getNowDate(SimpleDateFormat  sdf){
		
		String defaultDate = sdf.format(new Date());
		Date date = null ;
		try {
			date = sdf.parse(defaultDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * 获取当天时间戳
	 * @return
	 */
	public static int getSecondTimestamp(Date date){
		if (null == date) {
			return 0;
		}
		String timestamp = String.valueOf(date.getTime());
		int length = timestamp.length();
		if (length > 3) {
			return Integer.valueOf(timestamp.substring(0,length-3));
		} else {
			return 0;
		}
	}


	/**
	 * 获取当天时间
	 * @param day
	 * @return
	 */
	public static Date getDateByAddDay(int  day){
		try{
			Format f = new SimpleDateFormat("yyyy-MM-dd");
			Date today = new Date();
			Calendar c = Calendar.getInstance();
			c.setTime(today);
			c.add(Calendar.DAY_OF_MONTH, day);// 今天+1天
			Date tomorrow = c.getTime();
			return tomorrow;

		}catch (Exception ex){
			return new  Date();
		}
	}
}