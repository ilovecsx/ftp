package com.oneToO;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.border.MatteBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

public class Serve extends JFrame {
//	private MulticastSocket serve;
//	private InetAddress dip;
//	private int port = 10020;
//	private String group = "224.119.81.9";
	boolean oneServe = true;
	ArrayList<Socket> soList;
	JPanel p0, p1, p2, North;
	DefaultTreeModel root;
	private DefaultMutableTreeNode top = null;
	private DefaultMutableTreeNode node = null;
	JTree tree;
	TreePath treePath;
	File file = new File("E:" + File.separatorChar + "����С˵");
	JScrollPane scrollPane;
	JTextField pathName;
	JButton load;
	Thread t=null;

	public Serve() throws IOException {
		super("�����");
		this.setSize(650, 480);
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		InitCenter();
		InitNorth();
		this.setVisible(true);
	}

	/**
	 * ��ʼ��North���
	 * @throws UnknownHostException 
	 */
	public void InitNorth() throws UnknownHostException {
		North = new JPanel();
		North.setLayout(new FlowLayout());
		North.setBackground(Color.white);

		JLabel ip=new JLabel("������IP:"+InetAddress.getLocalHost().getHostAddress());
		ip.setForeground(Color.blue);
		ip.setFont(new Font("����", 11, 14));
		pathName = new JTextField("��ָ����Դ·��");
		pathName.setOpaque(false);
		pathName.setFont(new Font("����", 11, 13));
		textSet(pathName);
		pathName.addCaretListener(new CaretListener() {

			@Override
			public void caretUpdate(CaretEvent e) {
				// TODO Auto-generated method stub
				if (pathName.getText().equals("��ָ����Դ·��"))
					pathName.setText("");
			}
		});

		load = new JButton("ȷ��");
		load.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String path = pathName.getText();
				if (path == null) {
					JOptionPane.showMessageDialog(Serve.this, "��Դ·������Ϊ��!");
					return;
				}
				if (!path.endsWith("\\"))
					path = path + '\\';
				File file = new File(path);
				if (!file.exists()) {
					JOptionPane.showMessageDialog(Serve.this, "·������!");
					return;
				}
				System.out.println("���ﻹ��");
				if(t!=null&&t.isAlive())
					return ;
				System.out.println("����û��");
				t=new Thread(new Threadload(path));
				t.start();
				if (oneServe) {
					new Thread() {
						public void run() {
							try {
								accpet();
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}
					}.start();
					oneServe = false;
				}
			}
		});
		North.add(ip);
		North.add(pathName);
		North.add(load);
		this.add(North, "North");
	}

	class Threadload implements Runnable{
		String path;
		public Threadload(String path) {
			this.path=path;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			load(path);
		}
	}
	
	/**
	 * ��Ӧ��������
	 * 10020�˿ڽ������ӷ����ļ�Ŀ¼
	 * 10022�˿ڽ��տͻ���������ļ��������س���
	 * 10024�˿ڽ��տͻ���������ļ��Σ������ļ�
	 * @throws IOException
	 */
	public void accpet() throws IOException {
		new Thread() {
			public void run() {
				try {
					ServerSocket serve = new ServerSocket(10022);
					ExecutorService exec=Executors.newCachedThreadPool();
					while (true) {
						Socket s = serve.accept();
						exec.execute(new Thread(new ThreadName(s)));
					}
				} catch (IOException e) {
				}
			}
		}.start();
		new Thread() {
			public void run() {
				ServerSocket serve;
				ExecutorService exec=Executors.newCachedThreadPool();
				try {
					serve = new ServerSocket(10024);
					while (true) {
						Socket s = serve.accept();
						exec.execute(new Thread(new sendThread(s)));
					}
				} catch (IOException e) {
				}

			}
		}.start();
		ServerSocket serve = new ServerSocket(10020);
		ExecutorService exec=Executors.newCachedThreadPool();
		while (true) {
			Socket s = serve.accept();
			exec.execute(new Thread() {
				public void run() {
					try {
						sendTree(top, s);
					} catch (IOException e) {
					}
				}
			});
		}
	}

	/**
	 * ��ʼ�����İ��
	 */
	public void InitCenter() {
		p0 = new JPanel();
		//p0.setLayout(new GridLayout(1, 2));
		p1 = new JPanel();
		p1.setBackground(Color.white);
		//p2 = new JPanel();
		//p2.setBackground(Color.white);
		p0.add(p1,"Center");
		//p0.add(p2);
		top = new DefaultMutableTreeNode("��������Դ");
		root = new DefaultTreeModel(top);
		tree = new JTree(root);
		scrollPane = new JScrollPane(tree);
		scrollPane.setPreferredSize(new Dimension(635, 395));
		p1.add(scrollPane, "Center");
		this.add(p0, "Center");
	}

	/**
	 * ��ʼ����
	 */
	public void load(String path) {
		synchronized (top) {
			top.removeAllChildren();
			file = new File(path);
			File[] roots = file.listFiles();
			for (int i = 0; i < roots.length; i++) {
				DefaultMutableTreeNode R = new DefaultMutableTreeNode(roots[i]);
				top.add(R);
				getTree(roots[i], R);
			}
			root.reload();
		}
	}

	/**
	 * �ݹ������Դ�ļ���
	 * 
	 * @param file
	 * @param Node
	 */
	public void getTree(File file, DefaultMutableTreeNode Node) {
		File[] list = file.listFiles();
		if (list == null)
			return;
		for (int i = 0; i < list.length; i++) {
			String[] strings = list[i].getAbsolutePath().split("////");
			String pathname = strings[strings.length - 1];
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(
					pathname.substring(pathname.lastIndexOf(File.separatorChar) + 1));
			Node.add(node);
			if (list[i].isDirectory())
				getTree(list[i], node);
		}
	}

	/**
	 * ���������
	 */
	public void textSet(JTextField field) {
		field.setBackground(Color.blue);
		field.setPreferredSize(new Dimension(130, 28));
		MatteBorder border = new MatteBorder(0, 0, 2, 0, Color.red);
		field.setBorder(border);
	}

	/**
	 * ����tcp������
	 * 
	 * @param top
	 * @param socket
	 * @throws IOException
	 */
	public void sendTree(DefaultMutableTreeNode top, Socket socket) throws IOException {
		ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
		out.writeObject(top);
		out.flush();
		out.close();
		socket.close();
	}

	/**
	 * ���տͻ���������ļ��������س���
	 * @author csxwant
	 *
	 */
	class ThreadName implements Runnable {
		Socket s;

		public ThreadName(Socket s) {
			this.s = s;
		}

		public void run() {
			try {
				DataInputStream in = new DataInputStream(s.getInputStream());
				String path = in.readUTF();
				File file = new File(path);
				if (!file.exists()) {
					DataOutputStream out = new DataOutputStream(s.getOutputStream());
					out.writeLong(0);
					return;
				}
				DataOutputStream out = new DataOutputStream(s.getOutputStream());
				out.writeLong(file.length());
				in.close();
				out.close();
				s.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * ���Ϳͻ�����������ļ���
	 * @author csxwant
	 *
	 */
	class sendThread implements Runnable {
		Socket s;

		public sendThread(Socket s) {
			this.s = s;
		}

		@Override
		public void run() {
			try {
				DataInputStream in = new DataInputStream(s.getInputStream());
				String filepath = in.readUTF();
				long start = in.readLong();
				long end = in.readLong();
				File file = new File(filepath);
				RandomAccessFile ran = new RandomAccessFile(file, "r");
				long length = (end - start) + 1;
				ran.seek(start);
				long record = 0;
				byte[] buf = new byte[10240];
				DataOutputStream out = new DataOutputStream(s.getOutputStream());
				while (record != length) {
					int len = ran.read(buf, 0, buf.length);
					if (len == -1)
						break;
					if (s.isClosed() || !s.isConnected())
						break;
					out.write(buf, 0, len);
					out.flush();
					record += len;
				}
				in.close();
				out.close();
				s.close();
			} catch (IOException e) {
				try {
					s.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

		}
	}

	public static void main(String[] args) throws IOException {
		new Serve();
	}
}
