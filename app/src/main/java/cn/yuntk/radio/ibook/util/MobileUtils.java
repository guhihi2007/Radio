/*
 * Copyright (C) 2013  WhiteCat 白猫 (www.thinkandroid.cn)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.yuntk.radio.ibook.util;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * **********************************************
 *
 * @author Gavin.lee
 * @description 判断手机号码
 * @date Jun 28, 2009 4:57:26 AM * @version 1.0
 * **********************************************
 */

public class MobileUtils {


    /* 移动号段：
        134 135 136 137 138 139 147 150 151 152 157 158 159 172 178 182 183 184 187 188
        联通号段：
        130 131 132 145 155 156 171 175 176 185 186
        电信号段：
        133 149 153 173 177 180 181 189
        虚拟运营商:
        170*/

    /**
     * 验证手机格式
     */
    public static boolean isMobile(String number) {
    /*
    移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
    联通：130、131、132、152、155、156、185、186
    电信：133、153、180、189、（1349卫通）
    总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
    */
        String num = "[1][34578]\\d{9}";//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(number)) {
            return false;
        } else {
            //matches():字符串是否在给定的正则表达式匹配
            return number.matches(num);
        }
    }

    /**
     * 中国移动拥有号码段为:139,138,137,136,135,134,159,158,157(3G),151,150,188(3G),187(3G
     * );13个号段 中国联通拥有号码段为:130,131,132,156(3G),186(3G),185(3G);6个号段
     * 中国电信拥有号码段为:133,153,189(3G),180(3G);4个号码段
     */
    private static String regMobileStr = "^1(([3][456789])|([5][01789])|([8][78]))[0-9]{8}$";
    private static String regMobile3GStr = "^((157)|(18[78]))[0-9]{8}$";
    private static String regUnicomStr = "^1(([3][012])|([5][6])|([8][56]))[0-9]{8}$";
    private static String regUnicom3GStr = "^((156)|(18[56]))[0-9]{8}$";
    private static String regTelecomStr = "^1(([3][3])|([5][3])|([8][09]))[0-9]{8}$";
    private static String regTelocom3GStr = "^(18[09])[0-9]{8}$";
    private static String regPhoneString = "(?:(\\(\\+?86\\))(0[0-9]{2,3}\\-?)?([2-9][0-9]{6,7})+(\\-[0-9]{1,4})?)|" + "(?:(86-?)?(0[0-9]{2,3}\\-?)?([2-9][0-9]{6,7})+(\\-[0-9]{1,4})?)";

    private String mobile = "";
    private int facilitatorType = 0;
    private boolean isLawful = false;
    private boolean is3G = false;

    public MobileUtils(String mobile) {
        this.setMobile(mobile);
    }

    public void setMobile(String mobile) {
        if (mobile == null) {
            return;
        }
        /** */
        /** 第一步判断中国移动 */
        if (mobile.matches(MobileUtils.regMobileStr)) {
            this.mobile = mobile;
            this.setFacilitatorType(0);
            this.setLawful(true);
            if (mobile.matches(MobileUtils.regMobile3GStr)) {
                this.setIs3G(true);
            }
        }
        /** */
        /** 第二步判断中国联通 */
        else if (mobile.matches(MobileUtils.regUnicomStr)) {
            this.mobile = mobile;
            this.setFacilitatorType(1);
            this.setLawful(true);
            if (mobile.matches(MobileUtils.regUnicom3GStr)) {
                this.setIs3G(true);
            }
        }
        /** */
        /** 第三步判断中国电脑 */
        else if (mobile.matches(MobileUtils.regTelecomStr)) {
            this.mobile = mobile;
            this.setFacilitatorType(2);
            this.setLawful(true);
            if (mobile.matches(MobileUtils.regTelocom3GStr)) {
                this.setIs3G(true);
            }
        }
        /** */
        /** 第四步判断座机 */
        if (mobile.matches(MobileUtils.regPhoneString)) {
            this.mobile = mobile;
            this.setFacilitatorType(0);
            this.setLawful(true);
            if (mobile.matches(MobileUtils.regMobile3GStr)) {
                this.setIs3G(true);
            }
        }
    }

    public String getMobile() {
        return mobile;
    }

    public int getFacilitatorType() {
        return facilitatorType;
    }

    public boolean isLawful() {
        return isLawful;
    }

    public boolean isIs3G() {
        return is3G;
    }

    private void setFacilitatorType(int facilitatorType) {
        this.facilitatorType = facilitatorType;
    }

    private void setLawful(boolean isLawful) {
        this.isLawful = isLawful;
    }

    private void setIs3G(boolean is3G) {
        this.is3G = is3G;
    }


    /**
     * 匹配手机号的规则：[3578]是手机号第二位可能出现的数字
     */
    public static final String REGEX_MOBILE = "^[1][3578][0-9]{9}$";

    /**
     * 校验手机号
     *
     * @param mobile
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isMobileNum(String mobile) {
        return Pattern.matches(REGEX_MOBILE, mobile);
    }


    /*1、手机号开头集合
    176，177，178,
            180，181，182,183,184,185，186，187,188。，189。
            145，147
            130，131，132，133，134,135,136,137, 138,139
            150,151, 152,153，155，156，157,158,159,


            2、正则表达式*/

    public static boolean isChinaPhoneLegal(String str)
            throws PatternSyntaxException {
        String regExp = "^((13[0-9])|(15[^4])|(18[0-9])|(17[0-8])|(147,145))\\d{8}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(str);
        return m.matches();
    }

}
