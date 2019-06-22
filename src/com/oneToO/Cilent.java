package com.oneToO;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.RandomAccessFile;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.channels.MulticastChannel;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.border.Border;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

public class Cilent extends JFrame {
	private MulticastSocket serve;
	private InetAddress dip;
	private int port = 10020;
	private String group = "224.119.81.9";
	JPanel p0, p1, p2, North;
	DefaultTreeModel root;
	private DefaultMutableTreeNode top = null;
	private DefaultMutableTreeNode node = null;
	JTree tree;
	JScrollPane scrollPane = null;
	JTextField pathName;
	JButton load;
	final static String REC_IP = "^[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}";
	Thread t = null;
	String path;

	public Cilent(String custom) throws IOException {
		super("客户端");
		this.setSize(650, 480);
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		InitCenter();
		InitNorth();
		this.setVisible(true);
	}

	/**
	 * 初始化North板块
	 */
	public void InitNorth() {
		North = new JPanel();
		North.setLayout(new FlowLayout());
		North.setBackground(Color.white);

		pathName = new JTextField("请指定服务器IP");
		pathName.setOpaque(false);
		pathName.setFont(new Font("黑体", 11, 13));
		textSet(pathName);
		pathName.addCaretListener(new CaretListener() {

			@Override
			public void caretUpdate(CaretEvent e) {
				if (pathName.getText().equals("请指定服务器IP"))
					pathName.setText("");
			}
		});

		load = new JButton("刷新");
		load.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				path = pathName.getText();
				if (path == null) {
					JOptionPane.showMessageDialog(Cilent.this, "服务器IP地址不能为空!");
					return;
				}
				if (!Pattern.matches(REC_IP, path)) {
					JOptionPane.showMessageDialog(Cilent.this, "服务器IP地址格式错误");
					return;
				}
				if (t != null && t.isAlive())
					return;
				t = new Thread() {
					public void run() {
						try {
							Socket s = new Socket();
							SocketAddress sd=new InetSocketAddress(path,10020);
							s.connect(sd,10000);
							ObjectInputStream in = new ObjectInputStream(s.getInputStream());
							Object ob = in.readObject();
							DefaultMutableTreeNode newNode = new DefaultMutableTreeNode("资源");
							DefaultMutableTreeNode temp = (DefaultMutableTreeNode) ob;
							root = new DefaultTreeModel(newNode);
							tree = new JTree(root);
							JMenuItem down=new JMenuItem("下载此文件");
							down.addActionListener(new ActionListener() {
								
								@Override
								public void actionPerformed(ActionEvent e) {
									// TODO Auto-generated method stub
									if(e.getSource()==down)
									{
										DefaultMutableTreeNode note=(DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
										if(note.getChildCount()!=0)
											return ;
										String filename=note.toString();
										while(note.getParent()!=null&&!note.getParent().toString().startsWith("供"))
										{
											note = (DefaultMutableTreeNode) note.getParent();
											filename = note.toString() + File.separatorChar + filename;
										}
			
										new Thread(new recThread(filename)).start();
									}
								}
							});
							JPopupMenu download=new JPopupMenu();
							download.add(down);
							tree.addMouseListener(new MouseAdapter() {
								public void mousePressed(MouseEvent e) {
									if(e.getButton()==1&&e.getClickCount()==2) {
										DefaultMutableTreeNode note=(DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
										if(note.getChildCount()!=0)
											return ;
										String filename=note.toString();
										while(note.getParent()!=null&&!note.getParent().toString().startsWith("供"))
										{
											note = (DefaultMutableTreeNode) note.getParent();
											filename = note.toString() + File.separatorChar + filename;
										}
			
										new Thread(new recThread(filename)).start();
									}
									else if(e.getButton()==3)
									{
										if(tree.getLastSelectedPathComponent()==null)
											return ;
										DefaultMutableTreeNode note=(DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
										if(note.getChildCount()!=0)
											return ;
										download.show(tree, e.getX(), e.getY());
									}
								};
							});
							root.insertNodeInto(temp, newNode, 0);
							if (scrollPane != null)
								p1.remove(scrollPane);
							scrollPane = new JScrollPane(tree);
							scrollPane.setPreferredSize(new Dimension(320, 400));
							p1.add(scrollPane, "Center");
							p1.revalidate();
							s.close();
						} catch (IOException e1) {
							JOptionPane.showMessageDialog(Cilent.this, "服务器未连接", "温馨提示", 1);
						} catch (ClassNotFoundException e) {
						}
					}
				};
				t.start();
			}
		});
		North.add(pathName);
		North.add(load);
		this.add(North, "North");
	}

	/**
	 * 初始化中心板块
	 */
	public void InitCenter() {
		p0 = new JPanel();
		p0.setLayout(new GridLayout(1, 2));
		p1 = new JPanel();
		p1.setBackground(Color.white);
		p2 = new JPanel();
		p2.setLayout(new GridLayout(50, 1));
		p2.setBorder(new TitledBorder("下载进度"));
		p2.setBackground(Color.white);
		JScrollPane s=new JScrollPane(p2);
		p0.add(p1);
		p0.add(s);
		this.add(p0, "Center");
	}

	/**
	 * 美化输入框
	 */
	public void textSet(JTextField field) {
		field.setBackground(Color.blue);
		field.setPreferredSize(new Dimension(130, 28));
		MatteBorder border = new MatteBorder(0, 0, 2, 0, Color.red);
		field.setBorder(border);
	}

	/**
	 * 发送文件信息并接收文件
	 * 
	 * @throws IOException
	 * @throws UnknownHostException
	 */
	public void receive(String fileName) throws UnknownHostException, IOException {
		long[] startPos, endPos;
		CountDownLatch latch = new CountDownLatch(4);
		startPos = new long[4];
		endPos = new long[4];
		Socket s = new Socket(path, 10022);
		DataOutputStream out = new DataOutputStream(s.getOutputStream());
		out.writeUTF(fileName);
		out.flush();
		String realName=fileName.substring(fileName.lastIndexOf('\\')+1);
		String tmpfileName = realName + "_temp";
		File dir = new File("D:" + File.separatorChar + "蓝狮下载文件");
		if (!dir.exists())
			dir.mkdir();
		File tmpfile = new File(dir.getAbsolutePath() + File.separatorChar + tmpfileName);
		DataInputStream in = new DataInputStream(s.getInputStream());
		long length = in.readLong();
		if(length!=0) {
			long threadLength = length / 4;
			File file = new File(dir.getAbsolutePath() + File.separatorChar + realName);
			if (file.exists() && file.length() == length)
			{
				JOptionPane.showMessageDialog(this,"该文件已存在,请不要重复下载!","温馨提示", 2);
			}
			else {
				ExecutorService exec = Executors.newCachedThreadPool();
				JProgressBar pro=new JProgressBar();
				pro.setMinimum(0);
				pro.setMaximum(100);
				pro.setValue(0);
				pro.setStringPainted(true);
				//pro.setPreferredSize(new Dimension(100, 15));
				JLabel filenm=new JLabel(file.getName());
				JPanel tmp=new JPanel();
				tmp.setBackground(Color.white);
				tmp.setLayout(new GridLayout(1,2));
				tmp.add(filenm);
				tmp.add(pro);
				p2.add(tmp);
				p2.revalidate();
				setBreakPos(tmpfile, startPos, endPos, length);
				for (int i = 0; i < 4; i++)
					exec.execute(new ThreadDownload(startPos[i], endPos[i], i, file, tmpfile, latch,fileName,pro));
				try {
					latch.await();
				} catch (InterruptedException e) {
				}
				exec.shutdown();
			}
			if(file.length()==length)
				if(tmpfile.exists())
				{
					tmpfile.delete();
					System.out.println("删除临时文件");
				}
		}
		else
			JOptionPane.showMessageDialog(this, "服务器不存在此文件");
		out.close();
		in.close();
		s.close();
	}

	/**
	 * 设置断点
	 * 
	 * @param tmpfile
	 * @param startPos
	 * @param endPos
	 * @param length
	 */
	public void setBreakPos(File tmpfile, long[] startPos, long[] endPos, long length) {
		RandomAccessFile tmpran = null;
		long threadLength = length / 4;
		try {
			if (tmpfile.exists()) {
				tmpran = new RandomAccessFile(tmpfile, "rw");
				for (int i = 0; i < 4; i++) {
					tmpran.seek(i * 8 + 8);
					startPos[i] = tmpran.readLong();

					tmpran.seek(8 * (i + 1000) + 16);
					endPos[i] = tmpran.readLong();
				}
			} else {
				tmpran = new RandomAccessFile(tmpfile, "rw");
				for (int i = 0; i < 4; i++) {
					startPos[i] = threadLength * i;
					if (i == 2)
						endPos[i] = length;
					else
						endPos[i] = threadLength * (i + 1) - 1;

					tmpran.seek(i * 8 + 8);
					tmpran.writeLong(startPos[i]);

					tmpran.seek(8 * (i + 1000) + 16);
					tmpran.writeLong(endPos[i]);
				}
			}
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		} finally {
			if (tmpran != null)
				try {
					tmpran.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}

	class ThreadDownload implements Runnable {
		private RandomAccessFile down = null;
		private RandomAccessFile tmp = null;
		private long startPos;
		private long endPos;
		private int id;
		private File tmpfile;
		private File file;
		private CountDownLatch latch;
		private String fileName;
		private JProgressBar pro;
		public ThreadDownload(long startPos, long endPos, int id, File file, File tmpfile, CountDownLatch latch,String fileName,JProgressBar pro) {
			this.startPos = startPos;
			this.endPos = endPos;
			this.id = id;
			this.tmpfile = tmpfile;
			this.file = file;
			this.latch = latch;
			this.fileName=fileName;
			this.pro=pro;
			try {
				down = new RandomAccessFile(file, "rw");
				tmp = new RandomAccessFile(tmpfile, "rw");
			} catch (FileNotFoundException e) {
			}
		}

		@Override
		public void run() {
			while (true) {
				Socket s = null;
				try {
					if (startPos < endPos) {
						s = new Socket(path, 10024);
						DataOutputStream out = new DataOutputStream(s.getOutputStream());
						out.writeUTF(fileName);
						out.flush();
						out.writeLong(startPos);
						out.flush();
						out.writeLong(endPos);
						out.flush();

						DataInputStream in = new DataInputStream(s.getInputStream());
						byte[] buf = new byte[10240];
						int length;
						down.seek(startPos);
						while ((length = in.read(buf)) != -1) {
							down.write(buf, 0, length);
							startPos = startPos + length;
							tmp.seek(8 * id + 8);
							tmp.writeLong(startPos);
						}
						System.out.println("该段已经传输完毕");
						latch.countDown();
						int i=pro.getValue();
						pro.setValue(i+25);
						out.close();
						in.close();
						down.close();
						tmp.close();
					}
					break;
				} catch (IOException e) {
				} finally {
					
					try {
						down.close();
						tmp.close();
					} catch (IOException e1) {
					}
					System.out.println(id+"线程传输完毕");
					if (s != null)
						try {
							s.close();
						} catch (IOException e) {
						}
				}
			}
		}
	}

	class recThread implements Runnable{
		String name;
		public recThread(String name) {
			this.name=name;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				receive(name);
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) throws IOException {
		new Cilent("csx");
	}
}
