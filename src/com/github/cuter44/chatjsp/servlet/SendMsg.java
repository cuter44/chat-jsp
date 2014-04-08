package com.github.cuter44.chatjsp.servlet;

/* io */
import java.io.IOException;
import java.io.PrintWriter;
import java.io.OutputStream;
/* net&servlet */
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.annotation.WebServlet;
/* galin-FX */
import com.github.cuter44.util.servlet.HttpUtil;
import com.github.cuter44.util.servlet.MissingParameterException;
/* json */
import com.alibaba.fastjson.*;

import java.util.Date;
import java.util.Arrays;

import com.github.cuter44.util.dump.Dump;
import com.github.cuter44.util.crypto.CryptoUtil;
import javax.crypto.SecretKey;

import com.github.cuter44.chatjsp.Constants;
import com.github.cuter44.chatjsp.dao.Message;
import com.github.cuter44.chatjsp.core.MessageQueue;

/**
 * ???DESCRIPTION
 * <br />
 * <pre style="font-size:12px">

   <strong>请求</strong>
   GET/POST /???URL

   <strong>参数</strong>

   <strong>响应</strong>
   application/json 对象:

   <strong>例外</strong>

   <strong>样例</strong>暂无
 * </pre>
 *
 */
@WebServlet(
    name="SendMessage",
    urlPatterns={"/send"}
)
public class SendMsg extends HttpServlet
{
    private static final String M = "m";
    private static final String D = "d";

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException
    {
        doPost(req, resp);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException
    {
        // Encoding Configuration
        // encode of req effects post method only
        req.setCharacterEncoding("utf-8");
        resp.setCharacterEncoding("utf-8");

        // Dequote if pend to use session
        HttpSession session = req.getSession();

        // Dequote if pend to write binary
        //resp.setContentType("???MIME");
        //OutputStream out = resp.getOutputStream();

        // Dequote if pend to write chars
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();

        // output buffer prepare
        JSONObject json = new JSONObject();

        try
        {
            System.out.println("收到的消息");
            Message msg = new Message();

            byte[] content = HttpUtil.getByteArrayParam(req, M);
            if (content ==  null)
                throw(new MissingParameterException(M));
            System.out.println("消息密文:"+content);

            // decrypt
            SecretKey key = (SecretKey)session.getAttribute(Constants.ATTR_AES_KEY);
            content = CryptoUtil.AESDecrypt(content, key);
            // unpad
            int i = content.length;
            while (content[i-1] == (byte)0)
                i--;
            msg.content = Arrays.copyOf(content, i);
            System.out.println("消息明文:"+new String(msg.content, "UTF-8"));

            msg.digest = HttpUtil.getByteArrayParam(req, D);
            if (msg.digest ==  null)
                throw(new MissingParameterException(D));
            System.out.println("收到的摘要:"+CryptoUtil.byteToHex(msg.digest));

            msg.t = new Date(System.currentTimeMillis());
            msg.from = (String)session.getAttribute(Constants.ATTR_CLIENT_ID);

            // decrypt and verify
            byte[] d = CryptoUtil.MD5Digest(msg.content);
            System.out.println("计算的摘要:"+CryptoUtil.byteToHex(d));
            if (!Arrays.equals(msg.digest, d))
                throw(new Exception("Message fingerprint mismatch."));

            // write to pseudo-persist
            MessageQueue.putMessage(msg);
        }
        catch (MissingParameterException ex)
        {
            ex.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        finally
        {
            out.println(json.toString());
        }
    }
}
