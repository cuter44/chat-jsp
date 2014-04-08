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

  session.setAttribute(Constants.ATTR_SERVER_RSA_E, pubkey);
  session.setAttribute(Constants.ATTR_SERVER_RSA_D, prvkey);
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
 <head>
  <title>CHECK-IN</title>
  <meta http-equiv="content-type" content="text/html; charset=UTF-8" />
  <link rel="stylesheet" type="text/css" href="./css/general.css" />
  <script type="text/javascript" src="js/jquery-1.10.2.js"></script>
 </head>
 <body class="hasaha-background">
  <div class="left-single-column">
    <h1>ヽ(=^･ω･^=)丿 Hi~!</h1>
    <p>
    ...but sorry we cannot identify you, please fill in following to join.
    </p>
    <br />
    <form>
      <input id="id" placeholder="id* , display on messages." type="text" size="24" onfocus="$(this).removeClass('invalid')"></input>
      <br />
      <input id="key" placeholder="AES Key* , for safe transaction." type="text" size="24"></input><button type="button" onclick="generateAESKey();" onfocus="$(this).removeClass('invalid')">←Generate</button>
      <br>
      <button type="submit" onclick="go();">Yoooo(=^ ・ω・^=)→</button>
    </form>
  </div>
  <!-- import lib -->
  <script type="text/javascript" src="./js/TomWuCrypto/rsa.js"></script>
  <script type="text/javascript" src="./js/TomWuCrypto/jsbn.js"></script>
  <script type="text/javascript" src="./js/TomWuCrypto/prng4.js"></script>
  <script type="text/javascript" src="./js/TomWuCrypto/rng.js"></script>
  <!-- inline lib -->
  <script>
    function generateAESKey()
    {
      var hexChars = ['0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'];
      var key = "";
      for (var i=0; i<16; i++)
        key = key + hexChars[Math.floor(Math.random()*16)];

      $("#key").val(key);
    }

    function validateKey()
    {
      var hexChars = "0123456789abcdefABCDEF"
      var key = $("#key").val();

      if (key.length != 16)
        return(false);

      for (var i=0; i<16; i++)
      {
        if (hexChars.indexOf(key.charAt(i)) == -1)
          return(false);
      }

      return(true);
    }

    function encryptKey()
    {
      var rsakey = new RSAKey();
      rsakey.setPublic("<%=m.toString(16)%>","<%=e.toString(16)%>");
      var ciphertext = rsakey.encrypt($("#key").val());

      return(ciphertext);
    }

    function go()
    {
      if ($("#id").val() == "")
      {
        $("#id").addClass("invalid");
        return;
      }
      if (!validateKey())
      {
        $("#key").addClass("invalid");
        return;
      }

      $.post(
        "./register", 
        {
          id:$("#id").val(),
          key:encryptKey()
        },
        function success(a,b,c)
        {
          location.href = "./board.jsp";
        }
      );
    }
  </script>
  <script>
    generateAESKey();
  </script>
 </body>
</html>
