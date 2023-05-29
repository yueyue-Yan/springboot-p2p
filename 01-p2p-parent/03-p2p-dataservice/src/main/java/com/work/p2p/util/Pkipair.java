package com.work.p2p.util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;

/**
 * ClassName:Pkipair
 * Package:com.work.util
 * Description: 描述信息
 * @date:2023/4/29 13:57
 * @author:yueyue
 */
public class Pkipair {


    public String signMsg( String signMsg) {
        String base64 = "";
        try {

            KeyStore ks = KeyStore.getInstance("PKCS12");

            String file = Pkipair.class.getResource("10012140356.pfx").getPath().replaceAll("%20", " ");
            System.out.println(file);

            FileInputStream ksfis = new FileInputStream(file);

            BufferedInputStream ksbufin = new BufferedInputStream(ksfis);

            char[] keyPwd = "123456".toCharArray();
            //char[] keyPwd = "YaoJiaNiLOVE999Year".toCharArray();
            ks.load(ksbufin, keyPwd);

            PrivateKey priK = (PrivateKey) ks.getKey("test-alias", keyPwd);
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(priK);
            signature.update(signMsg.getBytes("utf-8"));
            //JDK从1.8升级到9.0.1后sun.misc.BASE64Decoder和sun.misc.BASE64Encoder不可用
            //java.util.Base64.Decoder和java.util.Base64.Encoder的JDK公共API，可代替sun.misc.BASE64Decoder和sun.misc.BASE64Encoder的JDK内部API
            //sun.misc.BASE64Encoder encoder = new sun.misc.BASE64Encoder();
            //base64 = encoder.encode(signature.sign());
            Base64.Encoder encoder = Base64.getEncoder();
            base64 = encoder.encodeToString(signature.sign());

        } catch(FileNotFoundException e){
            System.out.println("123");
        }catch (Exception ex) {
            ex.printStackTrace();
        }

        System.out.println("test = "+ base64);
        return base64;
    }
    public boolean enCodeByCer( String val, String msg) {
        boolean flag = false;
        try {

            String file = Pkipair.class.getResource("CFCA_sandbox.cer").toURI().getPath();//99bill[1].cert.rsa.20140803.cer
            System.out.println(file);                       //  99bill.cert.rsa.20140803.cer
            FileInputStream inStream = new FileInputStream(file);

            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509Certificate cert = (X509Certificate) cf.generateCertificate(inStream);

            PublicKey pk = cert.getPublicKey();

            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initVerify(pk);
            signature.update(val.getBytes());
            //JDK从1.8升级到9.0.1后sun.misc.BASE64Decoder和sun.misc.BASE64Encoder不可用
            //java.util.Base64.Decoder和java.util.Base64.Encoder的JDK公共API，可代替sun.misc.BASE64Decoder和sun.misc.BASE64Encoder的JDK内部API
            //sun.misc.BASE64Decoder decoder = new sun.misc.BASE64Decoder();
            //System.out.println(new String(decoder.decodeBuffer(msg)));
            //flag = signature.verify(decoder.decodeBuffer(msg));
            Base64.Decoder decoder = Base64.getDecoder();
            System.out.println(new String(decoder.decode(msg)));
            System.out.println(flag);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("no");
        }
        return flag;
    }

}

