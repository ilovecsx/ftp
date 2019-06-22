package com.csx;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.MatteBorder;

public class login extends JFrame {
	JFrame frm;
	JComboBox<String> account;
	DefaultComboBoxModel<String> acc;
	JPasswordField passwd;
	JCheckBox[] remerber;
	JButton refind, loginIn, register;
	String[] reAcc0, reAcc1, rePass;
	int num0 = 0, num1 = 0, num2;
	static Connection con;
	public ResultSet resultSet;
	Serve csx;
	serveMain c;

	/*
	 * public static void linkDB() throws SQLException {
	 * con=DriverManager.getConnection("jdbc:mysql://139.9.4.238:3306/UserInfo",
	 * "root", ""); }
	 */

	public static void setCon(Connection con1) {
		con = con1;
	}

	public login() throws SQLException, IOException {
		// TODO Auto-generated constructor stub
		reAcc0 = new String[100];
		reAcc1 = new String[100];
		rePass = new String[100];
		JPanel p1;
		frm = new JFrame("用户登录");
		// frm.setLayout(null);
		frm.setSize(450, 300);
		frm.setLocationRelativeTo(null);
		frm.setBackground(Color.white);
		frm.setResizable(false);
		frm.setDefaultCloseOperation(EXIT_ON_CLOSE);

		// 账号与密码
		p1 = new JPanel();
		p1.setLayout(null);
		p1.setOpaque(false);
		acc = new DefaultComboBoxModel<String>();
		account = new JComboBox<String>(acc);
		account.setEditable(true);
		passwd = new JPasswordField();
		account.setBounds(95, 30, 250, 25);
		passwd.setBounds(95, 90, 250, 25);
		account.setOpaque(false);
		File f1 = new File("D:\\蓝狮文件传输" + File.separatorChar + "acc.dat");
		File f2 = new File("D:\\蓝狮文件传输" + File.separatorChar + "accpas.dat");

		if (!f2.exists())
			f2.createNewFile();

		BufferedReader in;

		in = new BufferedReader(new FileReader(f2));
		String ac1 = new String("");
		while ((ac1 = in.readLine()) != null) {
			String[] a = ac1.split(",");
			//System.out.println(a[0]+a[1]);
			acc.addElement(a[0]);
			rePass[num0] = a[1];
			num0++;
		}
		in.close();

		new Thread() {
			public void run() {
				boolean go = true;
				if (!f1.exists())
					try {
						f1.createNewFile();
					} catch (IOException e) {
					}
				BufferedReader in;
				try {
					in = new BufferedReader(new FileReader(f1));
					String ac1 = new String("");
					while ((ac1 = in.readLine()) != null) {
						go = true;
						for (int i = 0; i < acc.getSize(); i++) {
							if (ac1.equals(acc.getElementAt(i))) {
								go=false;
								break;
							}
						}
						if (go) 
							acc.addElement(ac1);
					}
					in.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();

		// 记住账号监听
		account.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				// TODO Auto-generated method stub
				int no = account.getSelectedIndex();
				if (no == -1)
					return;
				passwd.setText(rePass[no]);
			}
		});

		passwd.setOpaque(false);
		textSet(account);
		textSet(passwd);
		p1.add(account);
		p1.add(passwd);

		remerber = new JCheckBox[2];
		// 记住
		remerber[0] = new JCheckBox("记住账号");
		remerber[0].setBounds(95, 135, 80, 25);
		remerber[0].setBackground(Color.white);
		remerber[0].setForeground(Color.gray);
		remerber[0].addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == remerber[0] && remerber[0].isSelected()) {
					String s1 = (String) account.getSelectedItem();
					if (s1 == null)
						return;
					BufferedReader in;
					try {
						in = new BufferedReader(new FileReader(f1));
						String ac1 = new String("");
						while ((ac1 = in.readLine()) != null) {
							if (ac1.equals(s1)) {
								in.close();
								return;
							}
						}
					} catch (IOException e3) {
					}
					BufferedWriter out = null;
					try {
						out = new BufferedWriter(new FileWriter(f1, true));
					} catch (IOException e2) {
					}
					try {
						if(f1.length()==0) {
							out.write(s1);
							out.close();
						}
						else {
							out.newLine();
							out.write(s1);
							out.close();
						}
					} catch (IOException e1) {
					}
				}

			}
		});
		p1.add(remerber[0]);
		remerber[1] = new JCheckBox("记住密码");
		remerber[1].setBounds(185, 135, 80, 25);
		remerber[1].setBackground(Color.white);
		remerber[1].setForeground(Color.gray);
		remerber[1].addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == remerber[1] && remerber[1].isSelected()) {
					String s1 = (String) account.getSelectedItem();
					String s2 = new String(passwd.getPassword());
					if (s1 == null || s2.equals(""))
						return;
					BufferedReader in;
					try {
						in = new BufferedReader(new FileReader(f2));
						String ac1 = new String("");
						while ((ac1 = in.readLine()) != null) {
							String a[] = ac1.split(",");
							if (a[0].equals(s1)) {
								in.close();
								return;
							}
						}
					} catch (IOException e3) {
					}
					BufferedWriter out = null;
					try {
						out = new BufferedWriter(new FileWriter(f2, true));
					} catch (IOException e2) {
					}
					try {
						if(f2.length()==0) {
							out.write(s1 + "," + s2);
							out.close();
						}
						else {
							out.newLine();
							out.write(s1+","+s2);
							out.close();
						}
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

				}
			}
		});
		p1.add(remerber[1]);

		// 找回密码
		refind = new JButton("忘记密码");
		refind.setBounds(265, 136, 90, 23);
		refind.setForeground(Color.gray);
		refind.setBorderPainted(false);
		refind.setBackground(Color.white);
		refind.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				new findPasswd();
			}
		});
		p1.add(refind);

		// 登录按钮
		loginIn = new JButton("登录");
		loginIn.setBounds(95, 190, 250, 40);
		loginIn.setBackground(new Color(152, 92, 182));
		loginIn.setFont(new Font("楷体", 1, 25));
		loginIn.setForeground(Color.white);
		loginIn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (e.getSource() == loginIn) {
					String s1 = (String) account.getSelectedItem();
					// System.out.println(s1);
					if (s1 == null) {
						JOptionPane.showMessageDialog(frm, "账号不能为空!");
						return;
					}
					String s2 = new String(passwd.getPassword());
					if (s2.equals("")) {
						JOptionPane.showMessageDialog(frm, "密码不能为空!");
						return;
					}
					String s3 = new String("dawdwag@##4%%#^&*");
					String sql = "select account,passwd,isCtrl from Usr where account=" + "'" + s1 + "'";
					try {
						PreparedStatement state = null;
						state = con.prepareStatement(sql);
						try {
							resultSet = state.executeQuery(sql);
							if (resultSet.next() == false) {
								JOptionPane.showMessageDialog(frm, "账户不存在!请重新检查后输入");
								return;
							}
							final String acc = resultSet.getString("account");
							s3 = resultSet.getString("passwd");
							int power = resultSet.getInt("isCtrl");
							if (s3.equals(s2)) {
								new Thread() {
									public void run() {
										midWay csx=new midWay(acc, power);
									};
								}.start();
								frm.setVisible(false);
							} else
								JOptionPane.showMessageDialog(frm, "密码错误", "错误", 0);
						} catch (SQLException e2) {
							JOptionPane.showMessageDialog(frm, "账户不存在!");
						}
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					// System.out.println(resultSet);
				}
			}
		});
		p1.add(loginIn);

		// 注册
		register = new JButton("注册账号");
		register.setBounds(0, 220, 90, 23);
		register.setForeground(Color.gray);
		register.setBorderPainted(false);
		register.setBackground(Color.white);
		register.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				new register();
			}
		});
		p1.add(register);

		// 按回车登录
		passwd.addKeyListener(key_listener);
		loginIn.addKeyListener(key_listener);

		frm.setContentPane(p1);
		frm.setVisible(true);
	}

	public void textSet(JTextField field) {
		field.setBackground(Color.blue);
		field.setPreferredSize(new Dimension(150, 28));
		MatteBorder border = new MatteBorder(0, 0, 2, 0, Color.blue);
		field.setBorder(border);
	}

	public void textSet(JComboBox<String> field) {
		field.setBackground(new Color(192, 192, 192));
		field.setPreferredSize(new Dimension(150, 28));
		field.setFont(getFont());
		/*
		 * MatteBorder border = new MatteBorder(0, 0, 2, 0, new Color(192, 192, 192));
		 */
		field.setBorder(null);
	}

	KeyListener key_listener = new KeyListener() {

		@Override
		public void keyTyped(KeyEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void keyReleased(KeyEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void keyPressed(KeyEvent e) {
			// TODO Auto-generated method stub
			if (e.getKeyChar() == KeyEvent.VK_ENTER) {
				String s1 = (String) account.getSelectedItem();
				// System.out.println(s1);
				if (s1 == null) {
					JOptionPane.showMessageDialog(frm, "账号不能为空!");
					return;
				}
				String s2 = new String(passwd.getPassword());
				if (s2.equals("")) {
					JOptionPane.showMessageDialog(frm, "密码不能为空!");
					return;
				}
				String s3 = new String("dawdwag@##4%%#^&*");
				String sql = "select account,passwd,isCtrl from Usr where account=" + "'" + s1 + "'";
				// System.out.println(sql);
				try {
					PreparedStatement state = null;
					state = con.prepareStatement(sql);
					try {
						resultSet = state.executeQuery(sql);
						if (resultSet.next() == false) {
							JOptionPane.showMessageDialog(frm, "账户不存在!请重新检查后输入");
							return;
						}
						final String acc = resultSet.getString("account");
						s3 = resultSet.getString("passwd");
						int power = resultSet.getInt("isCtrl");
						if (s3.equals(s2)) {
							new Thread() {
								public void run() {
									midWay csx=new midWay(acc, power);
								};
							}.start();
							frm.setVisible(false);
						} else
							JOptionPane.showMessageDialog(frm, "密码错误", "错误", 0);
					} catch (SQLException e2) {
						JOptionPane.showMessageDialog(frm, "账户不存在!");
					}
				} catch (SQLException e1) {
				}
			}
		}
	};
}
