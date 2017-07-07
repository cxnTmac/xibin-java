package com.xibin.core.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class CodeGenerator {
	static public String getCodeByCurrentTimeAndRandomNum(String prefix){
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        Random random = new Random();
        int s = random.nextInt(999)%(999-0+1) + 0;
        String numStr = intToString(3,s);
		return prefix+sdf.format(date)+numStr;
	}
	static private String intToString(int length,int num){
		String result = ""+num;
		if((""+num).length() < length){
			for(int i = 0; i<length-(""+num).length();i++){
				result = '0'+ result;
			}
		}
		return result;
	}

}
