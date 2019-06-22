package com.csx;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.MatteBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

public class findPasswd extends JFrame{
	JLabel tip,tip1	;
	JFrame frm1;
	JTextField account1,mailadd,yzm;
	String content="";
	JButton acquireYzm,submit;
	public static final String REGEX_EMAIL = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
	
	public findPasswd() {
		// TODO Auto-generated constructor stub
		JPanel p1;
		frm1=new JFrame("找回密码");
		frm1.setSize(450, 280);
		frm1.setLocationRelativeTo(null);
		frm1.setBackground(Color.white);
		frm1.setResizable(false);
		frm1.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		p1=new JPanel();
		p1.setLayout(null);
		p1.setOpaque(false);
		
		tip=new JLabel("温馨提示:信息及验证码填写正确才能够修改密码!");
		tip.setBounds(40, 10, 340, 25);
		tip.setForeground(Color.red);
		tip.setFont(new Font("宋体",0,15));
		tip1=new JLabel("");
		tip1.setBounds(330, 90, 100, 25);
		tip1.setFont(new Font("宋体", 1, 12));
		tip1.setForeground(Color.red);
		account1=new JTextField("账号");
		account1.setBounds(80, 50, 250, 25);
		account1.setOpaque(false);
		account1.addCaretListener(new CaretListener() {
			
			@Override
			public void caretUpdate(CaretEvent e) {
				// TODO Auto-generated method stub
				if(account1.getText().equals("账号"))
					account1.setText("");
			}
		});
		textSet(account1);
		mailadd=new JTextField("已绑定邮箱地址");
		mailadd.setBounds(80, 90, 250, 25);
		mailadd.setOpaque(false);
		mailadd.addCaretListener(new CaretListener() {
			
			@Override
			public void caretUpdate(CaretEvent e) {
				// TODO Auto-generated method stub
				String s1=mailadd.getText();
				if(s1.equals("已绑定邮箱地址"))
					mailadd.setText("");
				if(!Pattern.matches(REGEX_EMAIL, s1))
					tip1.setText("邮箱格式不正确");
				else
					tip1.setText("");
			}
		});
		textSet(mailadd);
		yzm=new JTextField("验证码");
		yzm.setBounds(80, 130, 170, 25);
		yzm.setOpaque(false);
		yzm.addCaretListener(new CaretListener() {
			
			@Override
			public void caretUpdate(CaretEvent e) {
				// TODO Auto-generated method stub
				if(yzm.getText().equals("验证码"))
					yzm.setText("");
			}
		});
		textSet(yzm);
		
		//获取验证码
		acquireYzm=new JButton("获取验证码");
		acquireYzm.setBounds(230, 130, 125, 25);
		acquireYzm.setForeground(Color.gray);
		acquireYzm.setBorderPainted(false);
		acquireYzm.setBackground(Color.white);
		acquireYzm.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				sendMail mail=new sendMail();
				mail.setUsr(account1.getText());
				new Thread() {
					public void run() {
						try {
							mail.send(mailadd.getText());
							content=mail.getYzm();
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}.start();
			}
		});
		
		//更改
		submit=new JButton("找回密码");
		submit.setBounds(135, 190, 125, 25);
		submit.setBackground(new Color(152, 92, 182));
		submit.setFont(new Font("楷体", 1, 15));
		submit.setForeground(Color.white);
		submit.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String s1=account1.getText();
				String s2=mailadd.getText();
				String s3=yzm.getText();
				//System.out.println(s3);
				//String sql="select account,passwd from Usr where account="+"'"+s1+"'";
				String sql="select * from Usr where account="+"'"+s1+"'"+" and email="+"'"+s2+"'";
				//System.out.println(sql1);
				//login.con
				PreparedStatement state=null;
				try {
					state=login.con.prepareStatement(sql);
					ResultSet resultSet=state.executeQuery(sql);
					System.out.println(resultSet);
					if(resultSet.next()==false)
					{
						JOptionPane.showMessageDialog(frm1, "账号或邮箱不匹配!");
						return ;
					}
					String passwd=resultSet.getString("passwd");
					//System.out.println(passwd);
					if(s3.equals(content))
					{
						JOptionPane.showMessageDialog(frm1, "你的密码为:"+passwd);
						content="dwuahd23u283932h4*&T%$R";
					}
					else
					{
						JOptionPane.showMessageDialog(frm1, "验证码错误!");
					}
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		p1.add(tip);
		p1.add(account1);
		p1.add(mailadd);
		p1.add(yzm);
		p1.add(acquireYzm);
		p1.add(submit);
		p1.add(tip1);
		
		frm1.setContentPane(p1);
		frm1.setVisible(true);
	}
	
	public void textSet(JTextField field) {  
        field.setBackground(Color.blue);  
        field.setPreferredSize(new Dimension(150, 28));  
        MatteBorder border = new MatteBorder(0, 0, 2, 0, Color.blue);  
        field.setBorder(border);  
    }
}
