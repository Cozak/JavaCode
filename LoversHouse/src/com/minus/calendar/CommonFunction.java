package com.minus.calendar;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.minus.lovershouse.util.AppManagerUtil;

public class CommonFunction {

	/**
	 * Calculate the days from beginDate to today.
	 * @param beginDate
	 * @return int
	 */
	public static int calculateDay(String beginDate){
		int difference = 0;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String nowDate = AppManagerUtil.getSimpleCurDate();
        try {
			Date now = df.parse(nowDate);
			Date begin = df.parse(beginDate);
			long diff = (now.getTime() - begin.getTime())/(1000*60*60*24);
			difference = (int)diff;
		} catch (ParseException e) {
			e.printStackTrace();
//			System.out.println("CalculateDate ERROR!!");
		}
		return difference;
	}

	/**
	 * Calculate the days from beginDate to endDate.
	 * @param beginDate
	 * @Param endDate
	 * @return int
	 */
	public static int calculateDay(String beginDate, String endDate){
		if (beginDate == null || endDate == null)
			return 0;
		int difference = 0;
        try {
    		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			Date end = df.parse(endDate);
			Date begin = df.parse(beginDate);
			long diff = (end.getTime() - begin.getTime())/(1000*60*60*24);
			difference = (int)diff;
			
		} catch (ParseException e) {
			e.printStackTrace();
//			System.out.println("CalculateDate ERROR!!");
		}
		return difference;
	}
	
	public static String standardizeDate(String date) {
		String[] date_parts = date.split("-");
		int year = Integer.valueOf(date_parts[0]);
		int month = Integer.valueOf(date_parts[1]);
		int day = Integer.valueOf(date_parts[2]);
		String monthStr = String.valueOf(month);
		if (month < 10)
			monthStr = "0" + monthStr;
		String dayStr = String.valueOf(day);
		if (day < 10)
			dayStr = "0" + dayStr;
		return year + "-" + monthStr + "-" + dayStr;
	}
	
	public static int compareTime(String left, String right) {
		long ret = 0;
		try {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
			Date leftTime = df.parse(left);
			Date rightTime = df.parse(right);
			ret = leftTime.getTime() - rightTime.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		if (ret < 0) return -1;
		else if (ret > 0) return 1;
		else return 0;
	}

	//计算中英文混合长度，一个中文字符=两个英文字符
	public static int calculateLengthWithByte(String str) {
        int length = 0;
        for (int i = 0; i < str.length(); i++) {
            char tmp = str.charAt(i);
            if (tmp > 0 && tmp < 127) {
            	length += 1;
            } else {
            	length += 2;
            }
        }
        return length;
    }
	
	public static String subStringWithByte(String str, int length) {
		if (str.length() == 0 || length == 0)
			return "";
		StringBuffer buf = new StringBuffer();
        for (int i = 0; i < str.length() && length > 0; i++) {
            char tmp = str.charAt(i);
            buf.append(tmp);
            if (tmp > 0 && tmp < 127) {
            	length -= 1;
            } else {
            	length -= 2;
            }
        }
        
        return buf.toString();
	}
	
	//向前截取字符串
	public static String subStr(String str, int subSLength) throws UnsupportedEncodingException{
        if (str == null) 
            return ""; 
        else{
            int tempSubLength = subSLength;//截取字节数
            String subStr = str.substring(0, str.length()<subSLength ? str.length() : subSLength);//截取的子串 
            int subStrByetsL = subStr.getBytes("GBK").length;//截取子串的字节长度
            //int subStrByetsL = subStr.getBytes().length;//截取子串的字节长度
            // 说明截取的字符串中包含有汉字 
            while (subStrByetsL > tempSubLength){ 
                int subSLengthTemp = --subSLength;
                subStr = str.substring(0, subSLengthTemp>str.length() ? str.length() : subSLengthTemp); 
                subStrByetsL = subStr.getBytes("GBK").length;
                //subStrByetsL = subStr.getBytes().length;
            } 
            return subStr;
        }
    }
	
	public static String byteSubstring(String s, int length) throws Exception {

        byte[] bytes = s.getBytes("Unicode");
        int n = 0; // 表示当前的字节数
        int i = 2; // 要截取的字节数，从第3个字节开始
        for (; i < bytes.length && n < length; i++)
        {
            // 奇数位置，如3、5、7等，为UCS2编码中两个字节的第二个字节
            if (i % 2 == 1)
            {
                n++; // 在UCS2第二个字节时n加1
            }
            else
            {
                // 当UCS2编码的第一个字节不等于0时，该UCS2字符为汉字，一个汉字算两个字节
                if (bytes[i] != 0)
                {
                    n++;
                }
            }
        }
        // 如果i为奇数时，处理成偶数
        if (i % 2 == 1)

        {
            // 该UCS2字符是汉字时，去掉这个截一半的汉字
            if (bytes[i - 1] != 0)
                i = i - 1;
            // 该UCS2字符是字母或数字，则保留该字符
            else
                i = i + 1;
        }

        return new String(bytes, 0, i, "Unicode");
    }
}

/*
 * 监听输入内容是否超出最大长度，并设置光标位置
 * */
class MaxLengthWatcher implements TextWatcher {
	public static final int MAX_CHINESE_CHARACTER_LENGTH = 30;
	
	private int mMaxLen = 0;
	private EditText mEditText = null;
	
	public MaxLengthWatcher(int maxLen, EditText editText) {
		this.mMaxLen = maxLen;
		this.mEditText = editText;
	}

	public void afterTextChanged(Editable arg0) {
	}

	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
	}

	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		int selectStart = mEditText.getSelectionStart();
		int selectEnd = mEditText.getSelectionEnd();
		Editable editable = mEditText.getText();
        mEditText.removeTextChangedListener(this); //先去掉监听器，否则会出现栈溢出
		if (CommonFunction.calculateLengthWithByte(editable.toString()) > this.mMaxLen) {
			editable.delete(selectStart - 1, selectEnd);
			mEditText.setTextKeepState(editable);
			//请读者注意这一行，保持光标原先的位置，而 mEditText.setText(s)会让光标跑到最前面，
            //就算是再加mEditText.setSelection(nSelStart) 也不起作用
		}
        mEditText.addTextChangedListener(this); //恢复监听器 
	}

}
