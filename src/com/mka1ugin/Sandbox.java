package com.mka1ugin;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.alibaba.fastjson.JSONArray;
import com.dobot.api.ErrorInfoBean;
import com.dobot.api.ErrorInfoHelper;

public class Sandbox {
    public static void main(String[] args) {
        String s1 = "0,{5},RobotMode();";
        String s2 = "-10000,{},RobotState();";

        parseResult(s1);
        parseResult(s2);
    }

    public static boolean parseResult(String strResult) {
        ErrorInfoHelper eih = new ErrorInfoHelper();

        int iBegPos = strResult.indexOf('{');
        if (iBegPos < 0) {
            return false;
        }
        int iEndPos = strResult.lastIndexOf('}', iBegPos + 1);
        //int iEndPos = strResult.lastindexOf('}',
                //iBegPos + 1);
        if (iEndPos < 0) {
            return false;
        }
        boolean bOk = strResult.startsWith("0,");
        if (iEndPos - iBegPos <= 1) {
            return bOk;
        }
        strResult = strResult.substring(iBegPos + 1, iEndPos);
        if (strResult == null || strResult.isEmpty()) {
            return bOk;
        }
        StringBuilder sb = new StringBuilder();
        strResult = '[' + strResult + ']';
        JSONArray jsonArray = JSONArray.parseArray(strResult);

        for (int i = 0; i < jsonArray.size(); i++) {

           JSONArray array = ((JSONArray) jsonArray.get(i)).toJavaObject(JSONArray.class);
            for (int j = 0; j < array.size(); j++) {
                ErrorInfoBean bean = new ErrorInfoBean();
                if (i == 0) {
                    bean = eih.findController((Integer) array.get(j));
                } else {
                    bean = eih.findServo((Integer) array.get(j));
                }
                if (null != bean) {
                    sb.append("ID:" + bean.getId() + "\r\n");
                    sb.append("Type:" + bean.getType() + "\r\n");
                    sb.append("Level:" + bean.getLevel() + "\r\n");
                    sb.append("Solution:" + bean.getEn().solution + "\r\n");
                }
            }
        }

        if (sb.length() > 0) {
            SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");// 设置日期格式
            String strTime = "Time Stamp:" + df.format(new Date());// new Date()为获取当前系统时间
            System.out.println(strTime + "\r\n" + sb);
        }
        return bOk;
    }
}