package com.github.cuter44.util.servlet;

import java.nio.ByteBuffer;
/* util */
import java.util.List;
import java.util.ArrayList;
import java.util.StringTokenizer;
/* http */
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Cookie;

public class HttpUtil
{
    /**
     * 从 HTTP 请求中检出相应参数值的简便封装
     *
     * 没有该命名的参数时返回null
     * (!) 不能用于在 Session 中检出 Object, 会返回它们的 toString()
     * 优先顺序为 Http请求参数 > Session > Cookie
     * @param req Http请求
     * @param name 参数的名字
     * @return String 参数的值
     */
    public static String getParam(HttpServletRequest req, String name)
    {
        // Server-side Attribute
        Object ra = req.getAttribute(name);
        if (ra != null)
            return(ra.toString());

        // Http Parameter
        String value = null;
        if ((value = req.getParameter(name)) != null)
            return(value);

        // Session
        HttpSession s = req.getSession();
        Object sa = s.getAttribute(name);
        if (sa != null)
            return(sa.toString());

        // Cookies
        Cookie[] carr = req.getCookies();
        if (carr != null)
            for (int i=0; i<carr.length; i++)
                if (carr[i].getName().equals(name))
                    return(carr[i].getValue());

        return(null);
    }

  // WRAPPER
    /**
     * 同 getParam() 但是转换为 Integer 返回
     *
     * 对于无法转换的值返回null, 没有对应的值返回null
     * 同理因为有一次转换所以效率略低
     * @param req Http请求
     * @param name 参数的名字
     * @return Integer 参数的值
     */
    public static Integer getIntParam(HttpServletRequest req, String name)
    {
        try
        {
            String v = getParam(req, name);
            return(v==null?null:Integer.valueOf(v));
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return(null);
        }
    }

    /**
     * 同 getParam() 但是转换为 Double 返回
     *
     * 对于无法转换的值返回null, 没有对应的值返回null
     * 同理因为有一次转换所以效率略低
     * @param req Http请求
     * @param name 参数的名字
     * @return Double 参数的值
     */
    public static Double getDoubleParam(HttpServletRequest req, String name)
    {
        try
        {
            String v = getParam(req, name);
            return(v==null?null:Double.valueOf(v));
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return(null);
        }
    }

    /**
     * 同 getParam() 但是转换为 Byte 返回
     *
     * 对于无法转换的值返回null, 没有对应的值返回null
     * 同理因为有一次转换所以效率略低
     * @param req Http请求
     * @param name 参数的名字
     * @return Byte 参数的值
     */
    public static Byte getByteParam(HttpServletRequest req, String name)
    {
        try
        {
            String v = getParam(req, name);
            return(v==null?null:Byte.valueOf(v));
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return(null);
        }
    }

    public static Long getLongParam(HttpServletRequest req, String name)
    {
        try
        {
            String v = getParam(req, name);
            return(v==null?null:Long.valueOf(v));
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return(null);
        }
    }

    public static List<Long> getLongListParam(HttpServletRequest req, String name)
    {
        try
        {
            String v = getParam(req, name);
            if (v == null)
                return(null);

            StringTokenizer st = new StringTokenizer(v, ",");
            List<Long> l = new ArrayList<Long>(st.countTokens());

            while (st.hasMoreTokens())
                l.add(Long.valueOf(st.nextToken()));

            return(l);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return(null);
        }
    }

    public static byte[] getByteArrayParam(HttpServletRequest req, String name)
    {
        try
        {
            String v = getParam(req, name);
            if (v == null)
                return(null);

            int l = v.length() / 2;

            ByteBuffer buf = ByteBuffer.allocate(l);
            for (int i=0; i<v.length(); i+=2)
            {
                buf.put(
                    Integer.valueOf(
                        v.substring(i, i+2),
                        16
                    ).byteValue()
                );
            }
            return(buf.array());
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return(null);
        }
    }
}
