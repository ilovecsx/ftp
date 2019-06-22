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
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.MatteBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

public class register extends JFrame {
	JFrame frm2;
	JTextField account, mailAdd, yzm;
	JPasswordField passwd, confirPw;
	JButton registe, acquireYzm;
	JLabel tip1, tip2, tip3;
	JCheckBox view;
	String content = "";
	int flag = 0, flag1 = 0;
	public static final String REGEX_EMAIL = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";

	public register() {
		// TODO Auto-generated constructor stub
		JPanel p1;

		frm2 = new JFrame("用户注册");
		frm2.setSize(480, 330);
		frm2.setLocationRelativeTo(null);
		frm2.setBackground(Color.white);
		frm2.setResizable(false);
		frm2.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		p1 = new JPanel();
		p1.setLayout(null);
		p1.setOpaque(false);

		account = new JTextField("填写账号");
		account.setBounds(80, 20, 250, 25);
		account.setOpaque(false);
		account.addCaretListener(new CaretListener() {

			@Override
			public void caretUpdate(CaretEvent e) {
				// TODO Auto-generated method stub
				if (account.getText().equals("填写账号"))
					account.setText("");
				String s1 = account.getText();
				String sql = "select * from Usr where account='" + s1 + "'";
				// System.out.println(sql);
				PreparedStatement state = null;
				try {
					state = ftpMain.con.prepareStatement(sql);
					ResultSet resultSet = state.executeQuery(sql);
					if (resultSet.next() == true) {
						tip3.setForeground(Color.red);
						tip3.setText("该用户名已被注册!");
						flag1 = 0;
						return;
					} else if (resultSet.next() == false) {
						tip3.setForeground(Color.green);
						tip3.setText("该用户名可使用");
						flag1 = 1;
					}
					if (s1.equals(""))
						tip3.setText("");
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		textSet(account);
		tip3 = new JLabel("");
		tip3.setBounds(330, 20, 130, 25);
		tip3.setFont(new Font("宋体", 1, 12));
		passwd = new JPasswordField("密码");
		passwd.setBounds(80, 60, 250, 25);
		passwd.setOpaque(false);
		passwd.setEchoChar((char) 0);
		passwd.addCaretListener(new CaretListener() {

			@Override
			public void caretUpdate(CaretEvent e) {
				// TODO Auto-generated method stub
				// passwd.setText("");
				if (new String(passwd.getPassword()).equals("密码"))
					passwd.setText("");
				if (passwd.getEchoChar() != '*' && !view.isSelected())
					passwd.setEchoChar('*');
			}
		});
		textSet(passwd);
		view = new JCheckBox("显示密码");
		view.setBounds(340, 60, 150, 25);
		view.setOpaque(false);
		view.setBorderPainted(false);
		view.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (view.isSelected()) {
					passwd.setEchoChar((char) 0);
					confirPw.setEchoChar((char) 0);
				} else if (!view.isSelected()) {
					passwd.setEchoChar('*');
					confirPw.setEchoChar('*');
				}
			}
		});
		confirPw = new JPasswordField("确认密码");
		confirPw.setBounds(80, 100, 250, 25);
		confirPw.setOpaque(false);
		confirPw.setEchoChar((char) 0);
		confirPw.addCaretListener(new CaretListener() {

			@Override
			public void caretUpdate(CaretEvent e) {
				// TODO Auto-generated method stub
				// confirPw.setText("");
				if (new String(confirPw.getPassword()).equals("确认密码"))
					confirPw.setText("");
				if (confirPw.getEchoChar() != '*' && !view.isSelected())
					confirPw.setEchoChar('*');
				String s1 = new String(passwd.getPassword());
				String s2 = new String(confirPw.getPassword());
				if (s1.equals(s2) == false)
					tip1.setText("两次密码输入不一致");
				else if (s1.equals(s2)) {
					tip1.setText("");
				}
			}
		});
		textSet(confirPw);
		mailAdd = new JTextField("邮箱地址");
		mailAdd.setBounds(80, 140, 250, 25);
		mailAdd.setOpaque(false);
		tip2 = new JLabel("");
		tip2.setBounds(330, 140, 100, 25);
		tip2.setFont(new Font("宋体", 1, 12));
		tip2.setForeground(Color.red);
		mailAdd.addCaretListener(new CaretListener() {

			@Override
			public void caretUpdate(CaretEvent e) {
				// TODO Auto-generated method stub
				if (mailAdd.getText().equals("邮箱地址"))
					mailAdd.setText("");
				String s1 = mailAdd.getText();
				if (!Pattern.matches(REGEX_EMAIL, s1)) {
					tip2.setForeground(Color.red);
					tip2.setText("邮箱格式不正确");
					flag = 0;
				} else {
					tip2.setText("");
					flag = 1;
				}
				String sql = "select * from Usr where email='" + s1 + "'";
				// System.out.println(sql);
				PreparedStatement state = null;
				try {
					state = ftpMain.con.prepareStatement(sql);
					ResultSet resultSet = state.executeQuery(sql);
					if (resultSet.next() == true) {
						tip2.setForeground(Color.red);
						tip2.setText("该邮箱已被绑定!");
						flag = 0;
						return;
					} else if (resultSet.next() == false && !s1.equals("") && tip2.getText().equals("")) {
						tip2.setForeground(Color.green);
						tip2.setText("该邮箱可使用");
						flag = 1;
					}
					if (s1.equals(""))
						tip2.setText("");
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		textSet(mailAdd);
		yzm = new JTextField("验证码");
		yzm.setBounds(80, 180, 170, 25);
		yzm.setOpaque(false);
		yzm.addCaretListener(new CaretListener() {

			@Override
			public void caretUpdate(CaretEvent e) {
				// TODO Auto-generated method stub
				if (yzm.getText().equals("验证码"))
					yzm.setText("");
			}
		});
		textSet(yzm);

		acquireYzm = new JButton("获取验证码");
		acquireYzm.setBounds(250, 180, 100, 25);
		acquireYzm.setForeground(Color.gray);
		acquireYzm.setBorderPainted(false);
		acquireYzm.setBackground(Color.white);
		acquireYzm.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (flag == 0) {
					JOptionPane.showMessageDialog(frm2, "邮箱地址不正确,请检查后再输入!");
					return;
				}
				sendMail mail = new sendMail();
				mail.setUsr(account.getText());

				new Thread() {
					public void run() {
						try {
							mail.send1(mailAdd.getText());
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						content = mail.getYzm();
					}
				}.start();

			}
		});

		registe = new JButton("注册");
		registe.setBounds(80, 230, 250, 25);
		registe.setBackground(new Color(152, 92, 182));
		registe.setFont(new Font("楷体", 1, 15));
		registe.setForeground(Color.white);
		registe.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String s1 = account.getText();
				String s2 = new String(passwd.getPassword());
				String s3 = mailAdd.getText();
				String s4 = yzm.getText();
				if (s1.equals("") || s2.equals("") || flag == 0 || !s4.equals(content) || flag1 == 0) {
					JOptionPane.showMessageDialog(frm2, "信息填写不正确,请重新检查后再注册!");
					content = "dwaudhauwydg217e%%6";
					return;
				}
				String sql = "insert into Usr values('" + s1 + "','" + s2 + "','" + s3 + "','" + '0' + "')";
				System.out.println(sql);
				PreparedStatement state = null;
				try {
					state = ftpMain.con.prepareStatement(sql);
					int resultSet = state.executeUpdate(sql);
					if (resultSet == 1) {
						JOptionPane.showMessageDialog(frm2, "注册成功!");
						content = "dwaudhauwydg217e%%6";
						return;
					}
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});

		tip1 = new JLabel("");
		tip1.setBounds(330, 100, 170, 25);
		tip1.setOpaque(false);
		tip1.setForeground(Color.red);
		tip1.setBackground(Color.white);

		p1.add(account);
		p1.add(passwd);
		p1.add(confirPw);
		p1.add(mailAdd);
		p1.add(yzm);
		p1.add(acquireYzm);
		p1.add(registe);
		p1.add(tip1);
		p1.add(view);
		p1.add(tip2);
		p1.add(tip3);
		frm2.setContentPane(p1);
		frm2.setVisible(true);
	}

	public void textSet(JTextField field) {
		field.setBackground(Color.blue);
		field.setPreferredSize(new Dimension(150, 28));
		MatteBorder border = new MatteBorder(0, 0, 2, 0, Color.blue);
		field.setBorder(border);
	}
}
