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

import com.github.cuter44.util.dump.Dump;
import com.github.cuter44.util.crypto.CryptoUtil;
import java.security.interfaces.RSAPrivateKey;
import javax.crypto.spec.SecretKeySpec;

import com.github.cuter44.chatjsp.Constants;

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
    name="Register",
    urlPatterns={"/register"}
)
public class Register extends HttpServlet
{
    private static final String ID = "id";
    private static final String KEY = "key";

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
            System.out.println("登录的用户");

            String id = HttpUtil.getParam(req, ID);
            if (id ==  null)
                throw(new MissingParameterException(ID));
            System.out.println("用户名:"+id);

            byte[] key = HttpUtil.getByteArrayParam(req, KEY);
            if (key ==  null)
                throw(new MissingParameterException(KEY));
            System.out.println("AES密钥密文:"+CryptoUtil.byteToHex(key));

            // get plaintext aes key
            key = CryptoUtil.hexToBytes(
                new String(
                    CryptoUtil.RSADecrypt(
                        key,
                        (RSAPrivateKey)session.getAttribute(Constants.ATTR_SERVER_RSA_D)
                    )
                )
            );

            SecretKeySpec spec = new SecretKeySpec(key, "AES");

            System.out.println(spec.getAlgorithm());
            System.out.println(spec.getFormat());
            System.out.println("AES密钥明文:"+CryptoUtil.byteToHex(spec.getEncoded()));

            session.setAttribute(Constants.ATTR_CLIENT_ID, id);
            session.setAttribute(Constants.ATTR_AES_KEY, spec);
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
