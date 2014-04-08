<%@ page contentType="text/html; charset=UTF-8" language="java" errorPage="" %>
<%@ page import="com.github.cuter44.util.crypto.CryptoUtil" %>
<%@ page import="java.security.KeyPair" %>
<%@ page import="java.security.interfaces.RSAPublicKey" %>
<%@ page import="java.security.interfaces.RSAPrivateKey" %>
<%@ page import="java.math.BigInteger" %>
<%@ page import="com.github.cuter44.chatjsp.Constants" %>
<%
  KeyPair keypair = CryptoUtil.generateRSAKey();
  RSAPublicKey pubkey = (RSAPublicKey)keypair.getPublic();
  RSAPrivateKey prvkey = (RSAPrivateKey)keypair.getPrivate();

  BigInteger m = pubkey.getModulus();
  BigInteger e = pubkey.getPublicExponent();
  BigInteger d = prvkey.getPrivateExponent();

  System.out.println("登录的用户");
  System.out.println("RSA公钥:"+m.toString()+","+e.toString());
  System.out.println("RSA私钥:"+m.toString()+","+d.toString());

  session.setAttribute(Constants.ATTR_SERVER_RSA_E, pubkey);
  session.setAttribute(Constants.ATTR_SERVER_RSA_D, prvkey);
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html class="whole-window">
<head>
    <title>CHAT</title>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8" />
    <link rel="stylesheet" type="text/css" href="./css/general.css" />
    <script type="text/javascript" src="./js/jquery-1.10.2.js"></script>
    <style type="text/css">
      div#wrapper
      {
        position: absolute;
        top:0px;
        left:120px;
        width:640px;
        bottom:0px;
      }
      div#board-container
      {
        position: relative;
        width:100%;
      }
      div#board-content
      {
        position:absolute;
        bottom:0px;
        width:100%;
      }
      div#input
      {
        width:100%;
      }
      textarea#sender-textarea
      {
        width:100%;
        height:96px;
        resize:none;
        margin:0px;
      }
      div.msg-frame
      {
        width:100%;
        font-size:14px;
        border-top:1px solid #a0a0a0;
        margin:4px 0px;
      }
      .msg-from
      {
        display:inline;
        color:#3377AA;
      }
      .msg-time
      {
        display:inline;
        color: #808080;
      }
      div.msg-content
      {
        padding-left:8px;
      }
    </style>
  </head>
  <body class="hasaha-background whole-window" style="font-family:sans-serif;">
    <div id="wrapper">

      <div id="board-container">
        <div id="board-content">
          <h1>ヽ(=^･ω･^=)丿 Hi~!</h1>
        </div>
      </div>
      
      <!-- seperator -->
      <div style="border-bottom:1px solid #a0a0a0; height:0px; margin:4px 0px;"></div>
      
      <div id="input">
        <!-- login -->
        <div id="login">
          <p>
          Fill in bolow to log in server.
          </p>
          <input id="id" class="to-validate" placeholder="Who are you?" type="text" size="24"></input>
          <button type="button" onclick="login();">Yoooo(=^ ・ω・^=)→</button>
        </div>
        <!-- working -->
        <div id="sender" style="display:none;">
          <div style="float:right; font:12px; color:#808080;">[logout]</div>
          <div style="margin:4px 0px;">
            <span id="idspan"></span> says:
          </div>
          <textarea id="sender-textarea" class="to-validate" placeholder="CTRL + ENTER || ALT + S -> send."></textarea>
        </div>
      </div>

    </div>
    <!-- crypto -->
    <script type="text/javascript" src="./js/TomWuCrypto/rsa.js"></script>
    <script type="text/javascript" src="./js/TomWuCrypto/jsbn.js"></script>
    <script type="text/javascript" src="./js/TomWuCrypto/prng4.js"></script>
    <script type="text/javascript" src="./js/TomWuCrypto/rng.js"></script>
    <script type="text/javascript" src="./js/CryptoJS/rollups/aes.js"></script>
    <script type="text/javascript" src="./js/CryptoJS/components/mode-ecb.js"></script>
    <script type="text/javascript" src="./js/CryptoJS/components/pad-zeropadding.js"></script>
    <script type="text/javascript" src="./js/CryptoJS/components/pad-nopadding.js"></script>
    <script type="text/javascript" src="./js/CryptoJS/rollups/md5.js"></script>

    <script>
      function generateAESKey()
      {
        var hexChars = ['0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'];
        var key = "";
        for (var i=0; i<32; i++)
          key = key + hexChars[Math.floor(Math.random()*16)];
        
        return(key);
      }

      function RSAEncrypt(plaintext)
      {
        var rsakey = new RSAKey();
        console.log("使用的RSA公钥:<%=m.toString(16)%>,<%=e.toString(16)%>");
        rsakey.setPublic("<%=m.toString(16)%>","<%=e.toString(16)%>");
        var ciphertext = rsakey.encrypt(plaintext);
        console.log("AES密钥密文:"+ciphertext);

        return(ciphertext);
      }
    </script>
    <!-- receive -->
    <script>
      function parseMessage(from, time, content)
      {
        $("#board-content").append(
          "<div class='msg-frame'>"+
            "<div class='msg-from'>"+
              from+
            "</div>&nbsp;&nbsp;"+
            "<div class='msg-time'>"+
              time.toLocaleTimeString()+
            "</div>"+
            "<div class='msg-content'>"+
              content+
            "</div>"+
          "</div>"
        );
      }

      function receive()
      {
        $.post(
          "./receive",
          {
            since: last.getTime()
          },
          function (data)
          {
            for (var i=0; i<data.length; i++)
            {
              console.log("收到的消息:");

              var c = data[i].content;
              console.log("密文:"+CryptoJS.enc.Hex.stringify(CryptoJS.enc.Base64.parse(c)));

              c = CryptoJS.AES.decrypt(
                c,
                AESKey,
                {mode: CryptoJS.mode.ECB, padding:CryptoJS.pad.ZeroPadding}
              );
              var content = CryptoJS.enc.Utf8.stringify(c);
              console.log("明文:"+content);

              var time = new Date();
              time.setTime(data[i].time);
              last = time;

              d = CryptoJS.MD5(c);
              console.log("收到的摘要:"+data[i].digest);
              console.log("计算的摘要:"+d.toString());
              if (d.toString() == data[i].digest)
                parseMessage(data[i].from, time, content);
            }
          }
        ).always(
          receive
        );
      }
    </script>
    <!-- send -->
    <script>
      function send()
      {
        var plaintext = $("#sender-textarea").val();
        if (plaintext == "")
        {
          $("#sender-textarea").addClass("invalid");
          return;
        }
        console.log("发送的消息");
        console.log("明文:"+plaintext);

        var encrypted = CryptoJS.AES
          .encrypt(
            CryptoJS.enc.Utf8.parse(plaintext),
            AESKey,
            {mode:CryptoJS.mode.ECB, padding: CryptoJS.pad.ZeroPadding}
          ).ciphertext.toString();
        console.log("密文:"+encrypted);

        var digested = CryptoJS.MD5(CryptoJS.enc.Utf8.parse(plaintext)).toString();
        console.log("摘要:"+digested);
        $.post(
          "./send",
          {
            m:encrypted,
            d:digested
          },
          function ()
          {
            $("#sender-textarea").val("");
          }
        );
      }
    </script>
    <!-- login -->
    <script>
      function login()
      {
        if ($("#id").val() == "")
        {
          $("#id").addClass("invalid");
          return;
        }

        console.log("登入");

        strAESKey = generateAESKey();
        console.log("AES密钥明文:"+strAESKey);

        $.post(
          "./register", 
          {
            id:$("#id").val(),
            key:RSAEncrypt(strAESKey)
          },
          function success()
          {
            AESKey = CryptoJS.enc.Hex.parse(strAESKey);
            $("#idspan").text($("#id").val());
            $("#login").slideUp();
            $("#sender").slideDown();
            receive();
          }
        );
      }
    </script>
    <!-- event -->
    <script>
      function fitBoard()
      {
        $("#board-container").height(
          $("#wrapper").height() - $("#input").height() - 120
        );
      }
      $(window).resize(fitBoard);

      function ifSendPressed(ev)
      {
        if ((ev.ctrlKey==true) && (ev.key=="Enter"))
        {
          send();
          return;
        }
        if ((ev.altKey==true) && (ev.key=="s"))
        {
          send();
          return;
        }
      }
      $("#sender-textarea").keypress(ifSendPressed);

      $(".to-validate").change(
        function()
        {
          $(this).removeClass("invalid");
        }
      );
    </script>
    <!-- bootstrap -->
    <script>
      var AESKey = null;
      var last = new Date();
      fitBoard();
    </script>
  </body>
</html>
