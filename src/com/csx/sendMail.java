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
    	Properties props = new Properties();                    // ��������
        props.setProperty("mail.transport.protocol", "smtp");   // ʹ�õ�Э�飨JavaMail�淶Ҫ��
        props.setProperty("mail.smtp.host", myEmailSMTPHost);   // �����˵������ SMTP ��������ַ
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
        // 1. ����һ���ʼ�
        MimeMessage message = new MimeMessage(session);

        // 2. From: �����ˣ��ǳ��й�����ɣ����ⱻ�ʼ�����������Ϊ���ķ������������ʧ�ܣ����޸��ǳƣ�
        message.setFrom(new InternetAddress(sendMail, "��ʨ�ļ�����", "UTF-8"));

        // 3. To: �ռ��ˣ��������Ӷ���ռ��ˡ����͡����ͣ�
        message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(receiveMail, usr, "UTF-8"));

        // 4. Subject: �ʼ����⣨�����й�����ɣ����ⱻ�ʼ�����������Ϊ���ķ������������ʧ�ܣ����޸ı��⣩
        message.setSubject("�һ�������֤��", "UTF-8");

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
        // 5. Content: �ʼ����ģ�����ʹ��html��ǩ���������й�����ɣ����ⱻ�ʼ�����������Ϊ���ķ������������ʧ�ܣ����޸ķ������ݣ�
        message.setContent("<p>�𾴵�"+usr+"�û�</p><p>&nbsp&nbsp&nbsp&nbsp���,�����֤��Ϊ:"+yzm, "text/html;charset=UTF-8");

        // 6. ���÷���ʱ��
        message.setSentDate(new Date());

        // 7. ��������
        message.saveChanges();

        return message;
    }
    
    public static void send1(String receive) throws Exception
    {
    	Properties props = new Properties();                    // ��������
        props.setProperty("mail.transport.protocol", "smtp");   // ʹ�õ�Э�飨JavaMail�淶Ҫ��
        props.setProperty("mail.smtp.host", myEmailSMTPHost);   // �����˵������ SMTP ��������ַ
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
        // 1. ����һ���ʼ�
        MimeMessage message = new MimeMessage(session);

        // 2. From: �����ˣ��ǳ��й�����ɣ����ⱻ�ʼ�����������Ϊ���ķ������������ʧ�ܣ����޸��ǳƣ�
        message.setFrom(new InternetAddress(sendMail, "��ʨ�ļ�����", "UTF-8"));

        // 3. To: �ռ��ˣ��������Ӷ���ռ��ˡ����͡����ͣ�
        message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(receiveMail, usr, "UTF-8"));

        // 4. Subject: �ʼ����⣨�����й�����ɣ����ⱻ�ʼ�����������Ϊ���ķ������������ʧ�ܣ����޸ı��⣩
        message.setSubject("ע���˺���֤��", "UTF-8");

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
        // 5. Content: �ʼ����ģ�����ʹ��html��ǩ���������й�����ɣ����ⱻ�ʼ�����������Ϊ���ķ������������ʧ�ܣ����޸ķ������ݣ�
        message.setContent("<p>�𾴵�"+usr+"�û�:</p><p>&nbsp&nbsp&nbsp&nbsp���,�����֤��Ϊ:"+yzm, "text/html;charset=UTF-8");

        // 6. ���÷���ʱ��
        message.setSentDate(new Date());

        // 7. ��������
        message.saveChanges();

        return message;
    }
    
}
