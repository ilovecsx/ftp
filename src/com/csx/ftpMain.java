package com.csx;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.swing.JOptionPane;

public class ftpMain {
	static Connection con;
	login lo;
	
	public static void linkDB() throws SQLException
	{
		con=DriverManager.getConnection("jdbc:mysql://139.9.4.238:3306/UserInfo", "root", "");
	}
	
	public static void main(String[] args) throws SQLException, IOException {
		
		JOptionPane.showMessageDialog(null, "正在初始化...时间稍长...请稍后");
		ftpMain.linkDB();
		login.setCon(con);
		login lo=new login();
	}
}
