package com.mp.basekit.util;

import android.util.Patterns;

import com.mp.basekit.core.MPApplication;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2015/4/28.
 */
public class InputValidUtils {

    public static final String PSW_SYMBOL = "-/:;()$&@\".,?!'[]{}#%^*+=_|~<>";

    public static String formatPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.length() == 0) {
            return phoneNumber;
        }

        String phone = phoneNumber;
        if (phoneNumber.startsWith("+86")) {
            phone = phoneNumber.substring(3, phoneNumber.length());
        }

        StringBuffer strBufNumber = new StringBuffer(phone.length());
        for (int i = 0; i < phone.length(); i++) {
            char c = phone.charAt(i);
            if (c == ' ' || c == '-' || c == ')' || c == '(' || c == '_') {
                continue;
            }
            strBufNumber.append(c);
        }
        return strBufNumber.toString();
    }

    public static boolean isValidAccount(String phone) {
        return isValidEmail(phone);
    }

    public static boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isValidQQ(String qq) {
        Pattern p = Pattern.compile("^[0-9]{5,12}$");
        return p.matcher(qq).matches();
    }

    public static boolean hasChina(String name) {
        Pattern p = Pattern.compile("[\\u4e00-\\u9fa5]");
        return p.matcher(name).find();
    }

    public static boolean isChinaName(String name) {
        Pattern p = Pattern.compile("^[\\u4e00-\\u9fa5]{2,10}$");
        return p.matcher(name).matches();
    }

    /**
     * 身份证15位编码规则：dddddd yymmdd xx psw
     * dddddd：6位地区编码
     * yymmdd: 出生年(两位年)月日，如：910215
     * xx: 顺序编码，系统产生，无法确定
     * psw: 性别，奇数为男，偶数为女
     * <p>
     * 身份证18位编码规则：dddddd yyyymmdd xxx y
     * dddddd：6位地区编码
     * yyyymmdd: 出生年(四位年)月日，如：19910215
     * xxx：顺序编码，系统产生，无法确定，奇数为男，偶数为女
     * y: 校验码，该位数值可通过前17位计算获得
     * <p>
     * 前17位号码加权因子为 Wi = [ 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2 ]
     * 验证位 Y = [ 1, 0, 10, 9, 8, 7, 6, 5, 4, 3, 2 ]
     * 如果验证码恰好是10，为了保证身份证是十八位，那么第十八位将用X来代替
     * 校验位计算公式：Y_P = mod( ∑(Ai×Wi),11 )
     * i为身份证号码1...17 位; Y_P为校验码Y所在校验码数组位置
     */
    public static boolean isIDCardValid(String idCard) {
        boolean valid = false;
        Pattern p = Pattern.compile("(^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$)" +
                "|(^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])((\\d{4})|\\d{3}[Xx])$)");

        valid = p.matcher(formatPhoneNumber(idCard)).matches();

        if (valid) {
            if (idCard.length() == 18) {
                int[] idCardWi = new int[]{
                        7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
                int[] idCardY = new int[]{
                        1, 0, 10, 9, 8, 7, 6, 5, 4, 3, 2
                };
                int idCardWiSum = 0;
                for (int i = 0; i < 17; i++) {
                    idCardWiSum += Integer.parseInt(
                            idCard.substring(i, i + 1)) * idCardWi[i];
                }

                int idCardMod = idCardWiSum % 11;
                String idCardLast = idCard.substring(17);
                if (idCardMod == 2) {
                    if (idCardLast.equalsIgnoreCase("x")) {
                        valid = true;
                    } else {
                        valid = false;
                    }
                } else {
                    int last = Integer.parseInt(idCard.substring(17));
                    if (last == idCardY[idCardMod]) {
                        valid = true;
                    } else {
                        valid = false;
                    }
                }
            }
        }

        return valid;
    }


    public static boolean isKeyWordValid(String keyWord, int minLen, int maxLen) {
        Pattern p = Pattern.compile("^\\w{"
                + minLen + "," + maxLen + "}$");
        return p.matcher(formatPhoneNumber(keyWord)).matches();
    }

    public static boolean isNumber(String keyWord, int len) {
        Pattern p = Pattern.compile("^[0-9]{"
                + len + "}$");
        return p.matcher(formatPhoneNumber(keyWord)).matches();
    }

    public static boolean isNumberLimit(String keyWord, int min, int max) {
        Pattern p = Pattern.compile("^[0-9]{"
                + min + "," + max + "}$");
        return p.matcher(formatPhoneNumber(keyWord)).matches();
    }


    public static boolean isBankCardVaild(String cardId) {
        char bit = getBankCardCheckCode(cardId.substring(0, cardId.length() - 1));
        if (bit == 'N') {
            return false;
        }
        return cardId.charAt(cardId.length() - 1) == bit;
    }

    /**
     * 从不含校验位的银行卡卡号采用 Luhm 校验算法获得校验位
     *
     * @param nonCheckCodeCardId
     * @return
     */
    public static char getBankCardCheckCode(String nonCheckCodeCardId) {
        if (nonCheckCodeCardId == null || nonCheckCodeCardId.trim().length() == 0
                || !nonCheckCodeCardId.matches("\\d+")) {
            //如果传的不是数据返回N
            return 'N';
        }
        char[] chs = nonCheckCodeCardId.trim().toCharArray();
        int luhmSum = 0;
        for (int i = chs.length - 1, j = 0; i >= 0; i--, j++) {
            int k = chs[i] - '0';
            if (j % 2 == 0) {
                k *= 2;
                k = k / 10 + k % 10;
            }
            luhmSum += k;
        }
        return (luhmSum % 10 == 0) ? '0' : (char) ((10 - luhmSum % 10) + '0');
    }

    /**
     * 判别是否包含Emoji表情
     *
     * @param str
     * @return
     */
    public static boolean containsEmoji(String str) {
        int len = str.length();
        for (int i = 0; i < len; i++) {
            if (isEmojiCharacter(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    private static boolean isEmojiCharacter(char codePoint) {
        return !((codePoint == 0x0) ||
                (codePoint == 0x9) ||
                (codePoint == 0xA) ||
                (codePoint == 0xD) ||
                ((codePoint >= 0x20) && (codePoint <= 0xD7FF)) ||
                ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) ||
                ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF)));
    }

    /**
     * 验证str是否为正确的车牌号
     *
     * @param no
     * @return
     */
    public static boolean isPlateNo(String no) {
        if (no == null || no.equals("")) {
            return false;
        }
        Pattern p = Pattern.compile("(^[\\u4e00-\\u9fa5]{1}[a-z_A-Z]{1}[a-z_A-Z_0-9]{5}$)");
        return p.matcher(no).find();
    }

    /**
     * 正则表达式验证密码
     *
     * @param input
     * @return
     */
    public static boolean checkPayPsw(String input) {
        // 6位数字
        return input.matches("^\\d{6}$");
    }

    /**
     * 是否包含中文或中文符号
     *
     * @param str
     * @return
     */
    public static boolean isContainChinese(String str) {
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        char[] ch = str.toCharArray();
        for (char c : ch) {
            if (isChinese(c)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 根据Unicode编码完美的判断中文汉字和符号
     */
    private static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
            return true;
        }
        return false;
    }

    /**
     * 正则表达式验证登录密码
     *
     * @param input
     * @return
     */
    public static boolean isValidPwd(String input) {
        if (isContainChinese(input)) {
            return false;
        }
        if (!MPApplication.IS_PUBLISH_VERSION) {
            return true;
        }
        String regex = "^((?=.*\\d)(?=.*[a-zA-Z])).{8,20}$";
        return input.matches(regex);
    }

    public static boolean isValidAssetPwd(String input) {
        Pattern pNum = Pattern.compile("[^0-9]");
        Pattern pLetter = Pattern.compile("[^a-z]");
        Pattern pLetter2 = Pattern.compile("[^A-Z]");
        Matcher m = pNum.matcher(input);
        String num = m.replaceAll("");
        String letter = pLetter.matcher(input).replaceAll("");
        String letter2 = pLetter2.matcher(input).replaceAll("");
        int lengthNum = num.length();
        int lengthLetter = letter.length();
        int lengthLetter2 = letter2.length();
        return input.length() >= 8 && input.length() <= 16 && lengthNum > 0 && lengthLetter > 0 && lengthLetter2 > 0;
    }

    /**
     * 正则表达式验证密码
     *
     * @param input
     * @return
     */
    public static int checkPasswordLevel(String input) {
        if (!MPApplication.IS_PUBLISH_VERSION) {
            return input.length() > 10 ? 3 : 2;
        }

        Pattern pSymbol = Pattern.compile("[^-/:;()$&@\\\".,?!'\\\\[\\\\]{}#%^*+=_|~<>]");//在这里，编译 成一个正则。
        Pattern pNum = Pattern.compile("[^0-9]");
        Pattern pLetter = Pattern.compile("[^a-z]");
        Pattern pLetter2 = Pattern.compile("[^A-Z]");
        Matcher m = pNum.matcher(input);
        String num = m.replaceAll("");
        String symbol = pSymbol.matcher(input).replaceAll("");
        String letter = pLetter.matcher(input).replaceAll("");
        String letter2 = pLetter2.matcher(input).replaceAll("");
        int lengthNum = num.length();
        int lengthSym = symbol.length();
        int lengthLetter = letter.length();
        int lengthLetter2 = letter2.length();
        int level = 1;
        if ((lengthNum > 2 && lengthSym > 2 && lengthLetter > 2 && lengthLetter2 > 2)
                || lengthNum > 0 && lengthSym > 0 && lengthLetter > 0 && lengthLetter2 > 0 && input.length() > 12) {
            level = 4;
        } else if ((lengthNum > 0 && lengthSym > 0 && lengthLetter > 0 && lengthLetter2 > 0)
                || (lengthNum > 0 && lengthSym > 0 && lengthLetter > 0 && input.length() > 12)
                || (lengthNum > 0 && lengthSym > 0 && lengthLetter2 > 0 && input.length() > 12)
                || (lengthNum > 0 && lengthLetter > 0 && lengthLetter2 > 0 && input.length() > 12)
                || (lengthSym > 0 && lengthLetter > 0 && lengthLetter2 > 0 && input.length() > 12)) {
            level = 3;
        } else if ((lengthNum > 0 && lengthSym > 0 && lengthLetter > 0)
                || (lengthNum > 0 && lengthSym > 0 && lengthLetter2 > 0)
                || (lengthNum > 0 && lengthLetter > 0 && lengthLetter2 > 0)
                || (lengthSym > 0 && lengthLetter > 0 && lengthLetter2 > 0)
                || (lengthNum > 0 && lengthSym > 0 && input.length() > 12)
                || (lengthNum > 0 && lengthLetter > 0 && input.length() > 12)
                || (lengthNum > 0 && lengthLetter2 > 0 && input.length() > 12)
                || (lengthSym > 0 && lengthLetter > 0 && input.length() > 12)
                || (lengthSym > 0 && lengthLetter2 > 0 && input.length() > 12)
                || (lengthLetter > 0 && lengthLetter2 > 0 && input.length() > 12)) {
            level = 2;
        } else {
            level = 1;
        }
        return level;
    }

    /**
     * 名字有效性
     *
     * @param input
     * @return
     */
    public static boolean isValidName(String input) {
//        String regStr = "^(^[a-zA-Z\\/]*$|^[\\u4E00-\\u9FA5]*$)(?!\\D+$).{4,16}$";
//        String regStr = "(^[\\u4E00-\\u9FA0]|[a-z0-9A-Z]){4,16}$";

//        return input.matches(regStr);

        return !(input.length() < 1 || input.length() > 12);
    }

    public static boolean isConSpeCharacters(String string) {
        //不包含特殊字符
        return string.replaceAll("[\u4e00-\u9fa5]*[a-z]*[A-Z]*\\d*-*_*\\s*", "").length() != 0;
    }

    /**
     * 是否只有数字和字母
     *
     * @param str
     * @return
     */
    public static boolean isLetterDigit(String str) {
        String regex = "^[a-z0-9A-Z]+$";
        return str.matches(regex);
    }

    /**
     * 是否只有数字
     *
     * @param str
     * @return
     */
    public static boolean isDigit(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        return isNum.matches();
    }
}
