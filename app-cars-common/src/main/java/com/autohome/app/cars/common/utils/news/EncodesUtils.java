package com.autohome.app.cars.common.utils.news;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringEscapeUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EncodesUtils {
    private static final String DEFAULT_URL_ENCODING = "UTF-8";
    private static final char[] BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
    private static String zhPattern = "[\\u4e00-\\u9fa5]+";

    public EncodesUtils() {
    }

    public static String encodeHex(byte[] input) {
        return new String(Hex.encodeHex(input));
    }

    public static byte[] decodeHex(String input) {
        try {
            return Hex.decodeHex(input.toCharArray());
        } catch (Exception var2) {
            throw unchecked(var2);
        }
    }

    public static String encodeBase64(byte[] input) {
        return new String(Base64.encodeBase64(input));
    }

    public static String encodeBase64(String input) {
        try {
            return new String(Base64.encodeBase64(input.getBytes("UTF-8")));
        } catch (UnsupportedEncodingException var2) {
            return "";
        }
    }

    public static byte[] decodeBase64(String input) {
        return Base64.decodeBase64(input.getBytes());
    }

    public static String decodeBase64String(String input) {
        try {
            return new String(Base64.decodeBase64(input.getBytes()), "UTF-8");
        } catch (UnsupportedEncodingException var2) {
            return "";
        }
    }

    public static String encodeBase62(byte[] input) {
        char[] chars = new char[input.length];

        for(int i = 0; i < input.length; ++i) {
            chars[i] = BASE62[(input[i] & 255) % BASE62.length];
        }

        return new String(chars);
    }

    public static String escapeHtml(String html) {
        return StringEscapeUtils.escapeHtml4(html);
    }

    public static String unescapeHtml(String htmlEscaped) {
        return StringEscapeUtils.unescapeHtml4(htmlEscaped);
    }

    public static String escapeXml(String xml) {
        return StringEscapeUtils.escapeXml10(xml);
    }

    public static String unescapeXml(String xmlEscaped) {
        return StringEscapeUtils.unescapeXml(xmlEscaped);
    }

    public static String urlEncode(String part) {
        try {
            return URLEncoder.encode(part, "UTF-8");
        } catch (UnsupportedEncodingException var2) {
            throw unchecked(var2);
        }
    }

    public static String urlEncode(String str, String charset) throws UnsupportedEncodingException {
        Pattern p = Pattern.compile(zhPattern);
        Matcher m = p.matcher(str);
        StringBuffer b = new StringBuffer();

        while(m.find()) {
            m.appendReplacement(b, URLEncoder.encode(m.group(0), charset));
        }

        m.appendTail(b);
        return b.toString();
    }

    public static String urlDecode(String part) {
        try {
            return URLDecoder.decode(part, "UTF-8");
        } catch (UnsupportedEncodingException var2) {
            throw unchecked(var2);
        }
    }

    public static RuntimeException unchecked(Exception e) {
        return e instanceof RuntimeException ? (RuntimeException)e : new RuntimeException(e);
    }

    public static String strDeUnicode(String unicode) {
        if (unicode != null && !"".equals(unicode)) {
            StringBuilder sb = new StringBuilder();
            int pos = 0;
            int i;
            if (unicode.indexOf("\\u", pos) != -1) {
                while((i = unicode.indexOf("\\u", pos)) != -1) {
                    sb.append(unicode.substring(pos, i));
                    if (i + 5 < unicode.length()) {
                        pos = i + 6;
                        sb.append((char)Integer.parseInt(unicode.substring(i + 2, i + 6), 16));
                    }
                }
            } else {
                sb.append(unicode);
            }

            return sb.toString();
        } else {
            return null;
        }
    }

    public static String strEnUnicode(String string) {
        if (string != null && !"".equals(string)) {
            StringBuffer unicode = new StringBuffer();

            for(int i = 0; i < string.length(); ++i) {
                char c = string.charAt(i);
                unicode.append("\\u" + Integer.toHexString(c));
            }

            return unicode.toString();
        } else {
            return null;
        }
    }
}