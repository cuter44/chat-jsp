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

import java.util.List;
import java.util.Date;
import java.util.Arrays;

import com.github.cuter44.util.dump.Dump;
import com.github.cuter44.util.crypto.CryptoUtil;
import javax.crypto.SecretKey;

import com.github.cuter44.chatjsp.Constants;
import com.github.cuter44.chatjsp.dao.Message;
import com.github.cuter44.chatjsp.core.MessageQueue;
import com.github.cuter44.chatjsp.core.ReceiverQueue;
import com.github.cuter44.chatjsp.core.ReceiverQueue.Token;

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
    name="ReceiveMessage",
    urlPatterns={"/receive"}
)
public class ReceiveMsg extends HttpServlet
{
    private static final String SINCE = "since";
    private static final String FROM = "from";
    private static final String TIME = "time";
    private static final String CONTENT = "content";
    private static final String DIGEST = "digest";

    private static JSONObject cryptoJsonize(Message m, SecretKey key)
    {
        JSONObject json = new JSONObject();

        json.put(FROM, m.from);
        json.put(TIME, m.t);

        // padding
        byte[] content = Arrays.copyOf(
            m.content,
            m.content.length%16==0?m.content.length:m.content.length+16-m.content.length%16
        );
        // encrypt
        content = CryptoUtil.AESEncrypt(
            content,
            key
        );
        json.put(CONTENT, CryptoUtil.base64Encode(content));

        json.put(DIGEST, CryptoUtil.byteToHex(m.digest));

        return(json);
    }

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
        JSONArray json = new JSONArray();

        try
        {
            Long _since = HttpUtil.getLongParam(req, SINCE);
            if (_since == null)
                throw(new MissingParameterException(SINCE));
            Date since = new Date(_since);

            List<Message> l = MessageQueue.getMessage(since);
            if (l.isEmpty())
            {
                Token t = ReceiverQueue.register(false);
                t.waitMessage();
                l = MessageQueue.getMessage(since);
            }

            for (int i=l.size()-1; i>=0; i--)
            {
                json.add(
                    cryptoJsonize(
                        l.get(i),
                        (SecretKey)session.getAttribute(Constants.ATTR_AES_KEY)
                    )
                );
            }
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
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }
        finally
        {
            out.println(json.toString());
        }
    }
}
