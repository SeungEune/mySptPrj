package biz.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.util.HtmlUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 문자열 유틸 클래스
 *
 * @author JHKIM
 */
public class StringUtil extends EgovStringUtil {

    /**
     * 조사 유형
     */
    @Getter
    @AllArgsConstructor
    public enum PositionType {
        /**
         * 은/는
         */
        TYPE1("은", "는"),
        /**
         * 이/가
         */
        TYPE2("이", "가"),
        /**
         * 을/를
         */
        TYPE3("을", "를");

        private final String firstValue;
        private final String secondValue;
    }

    /**
     * 입력한 값의 한글 조사를 붙여 리턴
     *
     * @param value        - 조사를 붙일 입력 값
     * @param positionType - 조사 유형
     * @return 입력한 값의 한글 조사가 조합된 문자열
     */
    public static String attachPosition(String value, PositionType positionType) {

        if (isEmpty(value) == true || value.length() < 1) {
            return EMPTY;
        }

        char lastWord = value.charAt(value.length() - 1);

        // 한글의 제일 처음과 끝의 범위밖일 경우는 오류
        if (lastWord < 0xAC00 || lastWord > 0xD7A3) {
            return value;
        }

        String addValue = (lastWord - 0xAC00) % 28 > 0 ? positionType.getFirstValue() : positionType.getSecondValue();

        return value + addValue;
    }

    /**
     * String이 비었거나("") 혹은 null 인지 검증한다.
     *
     * <pre>
     *  StringUtil.isEmpty(null)      = true
     *  StringUtil.isEmpty("")        = true
     *  StringUtil.isEmpty(" ")       = false
     *  StringUtil.isEmpty("bob")     = false
     *  StringUtil.isEmpty("  bob  ") = false
     * </pre>
     *
     * @param str - 체크 대상 스트링오브젝트이며 null을 허용함
     * @return <code>true</code> - 입력받은 String 이 빈 문자열 또는 null인 경우
     */
    public static boolean isEmpty(Object str) {
        return str == null || (str instanceof String) == false || str.toString().length() == 0;
    }

    /**
     * String이 비었거나("") 널이면, 대체문자열, 아니면 원본 문자열 리턴
     *
     * @param src - 체크 대상 스트링오브젝트이며 null을 허용함
     * @param alt - 대상 스트링오브젝트가 널이거나 값이 없을 경우 대체문자열
     * @return 입력받은 String 이 빈 문자열 또는 null인 경우, 대체문자열, 아니면 원본 문자열 리턴
     */
    public static String emptyConvert(Object src, String alt) {
        return isEmpty(src) ? alt : src.toString();
    }

    /**
     * 입력한 패스워드와 비교할 패스워드 일치 여부
     *
     * @param password        입력한 패스워드
     * @param comparePassword 비교할 패스워드
     * @param id              아이디(암호화 키값)
     * @return 입력한 패스워드와 비교할 패스워드 일치 여부
     */
    public static boolean checkEncryptPassword(String password, String comparePassword, String id) {

        try {

            String encPassword = encryptPassword(password, id);

            return comparePassword.equals(encPassword);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 입력한 패스워드를 암호화 (SHA-256 알고리즘 적용)
     *
     * @param password        입력한 패스워드
     * @param id              아이디(암호화 키값)
     * @return 입력한 패스워드와 비교할 패스워드 일치 여부
     */
    public static String encryptPassword(String password, String id) {
        try {
            return EgovFileScrty.encryptPassword(password, id);
        } catch (Exception e) {
            return null;
        }
    }

    public final static List<String> BLACK_LIST = Arrays.asList(
            "javascript", "script", "iframe", "form", "document", "vbscript", "applet",
            "embed", "object", "frame", "grameset", "layer", "bgsound",
            "alert", "prompt", "confirm", "onblur", "onchange", "onclick", "ondblclick", "enerror",
            "onfocus", "onload", "onmouse", "expression", "meta", "xml",
            "onreset", "onmove", "onstop", "eval", "cookie", "onstart",
            "onresize", "onmousewheel", "ondataavailable",
            "onafterprint", "onafterupdate", "onmousedown",
            "onbeforeactivate", "onbeforecopy", "ondatasetchanged",
            "onbeforedeactivate", "onbeforeeditfocus", "onbeforepaste",
            "onbeforeprint", "onbeforeunload", "onbeforeupdate",
            "onpropertychange", "ondatasetcomplete", "oncellchange",
            "onlayoutcomplete", "onmousemove", "oncontextmenu",
            "oncontrolselect", "onreadystatechange", "onselectionchange",
            "onrowsinserted", "onactivae", "oncopy", "oncut", "onbeforecut", "ondeactivate",
            "ondrag", "ondragend", "ondragenter", "ondragleave", "ondragover", "ondragstart",
            "ondrop", "onerror", "onerrorupdate", "onfilterchange", "onfinish", "onresizestart",
            "onunload", "onselectstart", "onfocusin", "onfocusout", "onhelp", "onkeydown",
            "onkeypress", "onkeyup", "onrowsdelete", "onlosecapture", "onbounce", "onmouseenter",
            "onmouseleave", "onbefore", "onmouseout", "onmouseover", "onmouseup", "onresizeend",
            "onabort", "onmoveend", "onmovestart", "onrowenter", "onsubmit");

    public final static String STR_EMPTY = "";
    public final static String STR_DOT = ".";
    public final static String STR_COMMA = ",";

    /**
     * 문자열이 null 또는 빈 문자열인지 여부를 반환
     *
     * @author 김언중
     * @since  2023-07-10
     * @param  str Null 또는 빈 문자열 여부를 체크할 변수
     * @return boolean
     */
    public static boolean chkNull(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * 문자열이 "null"인 경우 빈 문자열로 변환하여 반환
     *
     * @author 김언중
     * @since  2023-07-10
     * @param  str Null 또는 빈 문자열 여부를 체크할 변수
     * @return String
     */
    public static String convNullStr(String str) {
        return chkNull(str) ? STR_EMPTY : str;
    }

    /**
     * 입력받은 객체(obj)가 "Null"인 경우 빈 문자열을, 그 외의 경우 문자열로 변환하여 반환
     *
     * @author 김언중
     * @since  2023-07-10
     * @param  obj 문자열로 변환할 객체
     * @return String
     */
    public static String toStr(Object obj) {
        return obj == null ? STR_EMPTY : obj.toString();
    }

    /**
     * 문자열이 null 또는 빈 문자열인 경우 0, 그 외의 경우 정수로 변환하여 반환
     *
     * @author 김언중
     * @since  2023-07-10
     * @param  str 정수로 변환할 문자열
     * @return int
     */
    public static int toInt(String str) {
        return chkNull(str) ? 0 : Integer.parseInt(str);
    }

    /**
     * 문자열이 null 또는 빈 문자열인 경우 0.0, 그 외의 경우 실수로 변환하여 반환
     *
     * @author 김언중
     * @since  2023-07-10
     * @param str 실수로 변환할 문자열
     * @return double
     */
    public static double toDbl(String str) {
        return chkNull(str) ? 0.0 : Double.parseDouble(str);
    }

    /**
     * 문자열이 null 또는 빈 문자열인 경우 0.0, 그 외의 경우 Bool 값으로 변환하여 반환
     *
     * @author 김언중
     * @since  2023-07-10
     * @param  str Bool 값으로 변환할 문자열
     * @return boolean
     */
    public static boolean toBool(String str) {
        return !chkNull(str) && Boolean.parseBoolean(str);
    }

    /**
     * 전달받은 문자열(str)을 UTF-8 형식으로 인코딩하여 반환
     *
     * @author 김언중
     * @since  2023-07-10
     * @param  str UTF-8 형식으로 인코딩할 문자열
     * @return String
     */
    public static String encode(String str) {
        return URLEncoder.encode(convNullStr(str));
    }

    /**
     * 전달받은 문자열(str)을 전달받은 형식(charSet)으로 인코딩하여 반환
     * 전달받은 형식(charSet)이 지원하지 않는 인코딩 형식인 경우,
     * encode(String str) 메소드를 호출하여 그 결과를 반환
     *
     * @author 김언중
     * @since  2023-07-10
     * @param  str 인코딩할 문자열
     * @param  charSet 인코딩 형식
     * @return String
     */
    public static String encode(String str, String charSet) {
        try {
            return URLEncoder.encode(convNullStr(str), charSet);
        } catch (UnsupportedEncodingException e) {
            return encode(str);
        }
    }

    /**
     * 전달받은 문자열(str)을 UTF-8 형식으로 디코딩하여 반환
     *
     * @author 김언중
     * @since  2023-07-10
     * @param  str UTF-8 형식으로 디코딩할 문자열
     * @return String
     */
    public static String decode(String str) {
        return URLDecoder.decode(convNullStr(str));
    }

    /**
     * 전달받은 문자열(str)을 전달받은 형식(charSet)으로 디코딩하여 반환
     * 전달받은 형식(charSet)이 지원하지 않는 디코딩 형식인 경우,
     * decode(String str) 메소드를 호출하여 그 결과를 반환
     *
     * @author 김언중
     * @since  2023-07-10
     * @param  str 디코딩할 문자열
     * @param  charSet 디코딩 형식
     * @return String
     */
    public static String decode(String str, String charSet) {
        try {
            return URLDecoder.decode(convNullStr(str), charSet);
        } catch (UnsupportedEncodingException e) {
            return decode(str);
        }
    }

    public static String formatTelNo(String telNo, String separator) {
        StringBuilder stringBuilder = new StringBuilder();
        separator = chkNull(separator) ? "" : separator;
        stringBuilder.append(telNo, 0, 3).append(separator);
        int middle = (telNo.length() == 11) ? 7 : 6;
        stringBuilder.append(telNo, 3, middle).append(separator).append(telNo.substring(middle));
        return stringBuilder.toString();
    }

    public static String formatDate(String strDate, String separator) {
        return formatDate(strDate, separator, separator, "");
    }

    public static String formatDate(String strDate, String sepY, String sepM, String sepD) {
        StringBuilder stringBuilder = new StringBuilder();
        sepY = chkNull(sepY) ? "" : sepY;
        sepM = chkNull(sepM) ? "" : sepM;
        sepD = chkNull(sepD) ? "" : sepD;
        stringBuilder.append(strDate, 0, 4).append(sepY)
                .append(strDate, 4, 6).append(sepM)
                .append(strDate.substring(6)).append(sepD);
        return stringBuilder.toString();
    }

    public static boolean chkLen(String str, int length) {
        return chkNull(str) || (str.length() <= length);
    }

    public static String htmlEscape(String str) {
        return chkNull(str) ? null : HtmlUtils.htmlEscape(str);
    }

    public static String htmlUnescape(String str) {
        return chkNull(str) ? null : HtmlUtils.htmlUnescape(str);
    }

    public static String toValidFolderNm(String str) {
        String regex = "[^[a-zA-Z0-9가-힣]+$]";  // 알파벳 대소문자 및 숫자만 허용
        String folderNm = str.replaceAll(regex, "_");
        folderNm = folderNm.replaceAll("_+", "_");
        folderNm = folderNm.replaceAll("^_|_$", "");
        return folderNm;
    }

    public static String delXss(String str) {
        if (!chkNull(str)) {
            for (String target : BLACK_LIST) {
                String strPattern = "(?i)" + Pattern.quote(target); // (?i) makes it case insensitive, Pattern.quote handles special regex characters
                Pattern pattern = Pattern.compile(strPattern);
                Matcher matcher = pattern.matcher(str);
                str = matcher.replaceAll(STR_EMPTY);
            }
        }

        return str;
    }

    public static String convHexToRgba(String hex) {
        hex = hex.replace("#", "");

        if (hex.length() == 3 || hex.length() == 4) {
            hex = hex.chars()
                    .mapToObj(c -> String.valueOf((char)c) + (char)c)
                    .reduce((a, b) -> a + b)
                    .get();
        }

        if (hex.length() == 6 || hex.length() == 8) {
            int r = Integer.parseInt(hex.substring(0, 2), 16);
            int g = Integer.parseInt(hex.substring(2, 4), 16);
            int b = Integer.parseInt(hex.substring(4, 6), 16);
            float a = 1.0f;

            if (hex.length() == 8) {
                a = Integer.parseInt(hex.substring(6, 8), 16) / 255.0f;
            }

            return String.format("rgba(%d, %d, %d, %.2f)", r, g, b, a);
        }

        return STR_EMPTY;
    }
}
