package com.csx;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.mysql.jdbc.Connection;
import com.oneToO.Cilent;

public class midWay extends JFrame{
	private Connection con;
	private JPanel Cp,Np;
	private String usr;
	private int power;
	private JButton send,serve,accept,down;
	public midWay(String usr,int power) {
		super("蓝狮");
		this.usr=usr;
		this.power=power;
		this.setSize(220, 110);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setResizable(false);
		
		InitNorth();
		InitCenter();
		
		this.setVisible(true);
	}
	
	public void InitNorth() {
		Np=new JPanel();
		Np.setBackground(Color.white);
		JLabel tip=new JLabel("请选择你要进行的工作:");
		tip.setForeground(Color.blue);
		Np.add(tip);
		this.add(Np,"North");
	}
	
	public void InitCenter() {
		Cp=new JPanel();
		Cp.setBackground(Color.white);
		Cp.setLayout(new FlowLayout());
		if(power==1)
		{
			send=new JButton("群发文件");
			serve=new JButton("资源服务");
			Cp.add(send);
			Cp.add(serve);
			send.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					new Thread() {
						public void run() {
							try {
								Serve csx = new Serve(usr);
								csx.load();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}.start();
					midWay.this.setVisible(false);
				}
			});
			serve.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					new Thread() {
						public void run() {
							try {
								com.oneToO.Serve csx=new com.oneToO.Serve();
							} catch (IOException e) {
							}
						}
					}.start();
					midWay.this.setVisible(false);
				}
			});
		}
		else if(power==0)
		{
			accept=new JButton("接收文件");
			down=new JButton("下载文件");
			Cp.add(accept);
			Cp.add(down);
			accept.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					new Thread() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							super.run();
							try {
								serveMain csx=new serveMain(10011, usr);
							} catch (UnknownHostException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}.start();
					midWay.this.setVisible(false);
				}
			});
			down.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					new Thread() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							super.run();
							try {
								Cilent csx=new Cilent(usr);
							} catch (IOException e) {
							}
						}
					}.start();
					midWay.this.setVisible(false);
				}
			});
		}
		this.add(Cp,"Center");
	}
	
	public static void main(String[] args) {
		new midWay("蓝狮",1);
	}
}
