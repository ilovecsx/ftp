package com.csx;

import java.net.InetAddress;
import java.util.Date;
import java.util.Properties;
import java.util.Random;

import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class sendMail {
	public static String myEmailAccount = "18166139880@163.com";
    public static String myEmailPassword = "csx19981211";
    public static String myEmailSMTPHost = "smtp.163.com";
    public static String usr=new String();
    public static String yzm="";
    
    public void setUsr(String s1)
    {
    	usr=s1;
    }
    
    public String getYzm()
    {
    	return yzm;
    }
    
    public static void send(String receive) throws Exception
    {
    	Properties props = new Properties();                    // 参数配置
        props.setProperty("mail.transport.protocol", "smtp");   // 使用的协议（JavaMail规范要求）
        props.setProperty("mail.smtp.host", myEmailSMTPHost);   // 发件人的邮箱的 SMTP 服务器地址
        props.setProperty("mail.smtp.auth", "true");
        Session session = Session.getInstance(props);
        session.setDebug(true);
        MimeMessage message = createMimeMessage(session, myEmailAccount, receive);
        Transport transport = session.getTransport();
        transport.connect(myEmailAccount, myEmailPassword);
        transport.sendMessage(message, message.getAllRecipients());
        transport.close();
    }
    
    public static MimeMessage createMimeMessage(Session session, String sendMail, String receiveMail) throws Exception {
        // 1. 创建一封邮件
        MimeMessage message = new MimeMessage(session);

        // 2. From: 发件人（昵称有广告嫌疑，避免被邮件服务器误认为是滥发广告以至返回失败，请修改昵称）
        message.setFrom(new InternetAddress(sendMail, "蓝狮文件传输", "UTF-8"));

        // 3. To: 收件人（可以增加多个收件人、抄送、密送）
        message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(receiveMail, usr, "UTF-8"));

        // 4. Subject: 邮件主题（标题有广告嫌疑，避免被邮件服务器误认为是滥发广告以至返回失败，请修改标题）
        message.setSubject("找回密码验证码", "UTF-8");

        yzm="";
        Random random=new Random();
        for(int i=0;i<6;i++)
        {
        	String charOrNum=random.nextInt(2)%2==0?"char":"int";
        	if("char".equalsIgnoreCase(charOrNum))
        	{
        		int temp=random.nextInt(2)%2==0?65:97;
        		yzm+=(char)(random.nextInt(26)+temp);
        	}
        	else if("int".equals(charOrNum))
        	{
        		yzm+=String.valueOf(random.nextInt(10));
        	}
        }
        // 5. Content: 邮件正文（可以使用html标签）（内容有广告嫌疑，避免被邮件服务器误认为是滥发广告以至返回失败，请修改发送内容）
        message.setContent("<p>尊敬的"+usr+"用户</p><p>&nbsp&nbsp&nbsp&nbsp你好,你的验证码为:"+yzm, "text/html;charset=UTF-8");

        // 6. 设置发件时间
        message.setSentDate(new Date());

        // 7. 保存设置
        message.saveChanges();

        return message;
    }
    
    public static void send1(String receive) throws Exception
    {
    	Properties props = new Properties();                    // 参数配置
        props.setProperty("mail.transport.protocol", "smtp");   // 使用的协议（JavaMail规范要求）
        props.setProperty("mail.smtp.host", myEmailSMTPHost);   // 发件人的邮箱的 SMTP 服务器地址
        props.setProperty("mail.smtp.auth", "true");
        Session session = Session.getInstance(props);
        session.setDebug(true);
        MimeMessage message = createMimeMessage1(session, myEmailAccount, receive);
        Transport transport = session.getTransport();
        transport.connect(myEmailAccount, myEmailPassword);
        transport.sendMessage(message, message.getAllRecipients());
        transport.close();
    }
    
    public static MimeMessage createMimeMessage1(Session session, String sendMail, String receiveMail) throws Exception {
        // 1. 创建一封邮件
        MimeMessage message = new MimeMessage(session);

        // 2. From: 发件人（昵称有广告嫌疑，避免被邮件服务器误认为是滥发广告以至返回失败，请修改昵称）
        message.setFrom(new InternetAddress(sendMail, "蓝狮文件传输", "UTF-8"));

        // 3. To: 收件人（可以增加多个收件人、抄送、密送）
        message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(receiveMail, usr, "UTF-8"));

        // 4. Subject: 邮件主题（标题有广告嫌疑，避免被邮件服务器误认为是滥发广告以至返回失败，请修改标题）
        message.setSubject("注册账号验证码", "UTF-8");

        yzm="";
        Random random=new Random();
        for(int i=0;i<6;i++)
        {
        	String charOrNum=random.nextInt(2)%2==0?"char":"int";
        	if("char".equalsIgnoreCase(charOrNum))
        	{
        		int temp=random.nextInt(2)%2==0?65:97;
        		yzm+=(char)(random.nextInt(26)+temp);
        	}
        	else if("int".equals(charOrNum))
        	{
        		yzm+=String.valueOf(random.nextInt(10));
        	}
        }
        // 5. Content: 邮件正文（可以使用html标签）（内容有广告嫌疑，避免被邮件服务器误认为是滥发广告以至返回失败，请修改发送内容）
        message.setContent("<p>尊敬的"+usr+"用户:</p><p>&nbsp&nbsp&nbsp&nbsp你好,你的验证码为:"+yzm, "text/html;charset=UTF-8");

        // 6. 设置发件时间
        message.setSentDate(new Date());

        // 7. 保存设置
        message.saveChanges();

        return message;
    }
    
}
