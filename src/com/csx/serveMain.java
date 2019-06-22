package com.csx;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.math.RoundingMode;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.text.DecimalFormat;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;


public class serveMain extends JFrame {
	private DefaultMutableTreeNode top = null;
	private DefaultMutableTreeNode node = null;
	JList list;
	DefaultListModel<String> model;
	JTree tree;
	JFrame serveFrm;
	JPanel p0, p1, p2, stateP, pS;
	TreePath treePath;
	ObjectOutputStream objout;
	Socket socket;
	static ServerSocket serve;
	File file;
	JLabel regMan, hostL, link, stateT;
	JPopupMenu open;
	private static DecimalFormat df = null;
	JButton confirIp;
	JTextField ip;
	String IP;
	Thread t=null;
	public static final String REGEX_IP = "^[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}";

	public void setSersoc(ServerSocket serve1) {
		// serve=serve1;
	}

	static {
		df = new DecimalFormat("#0.0");
		df.setRoundingMode(RoundingMode.HALF_UP);
		df.setMinimumFractionDigits(1);
		df.setMaximumFractionDigits(1);
	}

	public serveMain(int port, String hostN) throws UnknownHostException, IOException {
		serveFrm = new JFrame("客户端");
		serveFrm.setSize(650, 480);
		serveFrm.setResizable(false);
		serveFrm.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		serveFrm.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				// TODO Auto-generated method stub
				super.windowClosing(e);
				if (JOptionPane.showConfirmDialog(serveFrm, "是否退出?", "确认", JOptionPane.YES_NO_CANCEL_OPTION) == 0) {
					if (socket != null && !socket.isClosed()) {
						try {
							socket.close();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					System.exit(0);
				}
			}
		});
		// serveFrm.setLayout(new GridLayout(1, 2));
		serveFrm.setLocationRelativeTo(null);
		p0 = new JPanel();
		p0.setLayout(new GridLayout(1, 2));
		p1 = new JPanel();
		p1.setBackground(Color.white);
		p2 = new JPanel();
		p2.setBackground(Color.white);
		p0.add(p1);
		p0.add(p2);
		p2.setBorder(new TitledBorder("已接收文件(文件保存在D:\\蓝狮文件传输)"));
		top = new DefaultMutableTreeNode("这台电脑");
		File[] roots = File.listRoots();
		for (int k = 0; k < roots.length; k++) {
			node = new DefaultMutableTreeNode(roots[k].toString().substring(0, 2));
			top.add(node);
			node.add(new DefaultMutableTreeNode("-"));
		}
		// DefaultTreeModel treeModel=new DefaultTreeModel(top);
		tree = new JTree(top);

		JScrollPane scrollPane = new JScrollPane(tree);
		scrollPane.setPreferredSize(new Dimension(320, 405));

		model = new DefaultListModel<String>();
		list = new JList<String>(model);
		// 双击打开此文件
		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				super.mousePressed(e);
				if (e.getButton() == 1 && e.getClickCount() == 2) {
					String name = list.getSelectedValue().toString();
					// System.out.println(name);
					name = "cmd /C start \"\" D:\\蓝狮文件传输\\" + "\"" + name + "\"";
					Process p;
					try {
						p = Runtime.getRuntime().exec(name);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
		});

		open = new JPopupMenu();
		JMenuItem openm = new JMenuItem("打开文件");
		open.add(openm);
		openm.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (e.getSource() == openm) {
					String name = list.getSelectedValue().toString();
					name = "cmd /C start \"\" D:\\蓝狮文件传输\\" + "\"" + name + "\"";
					Process p;
					try {
						p = Runtime.getRuntime().exec(name);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});

		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				super.mousePressed(e);
				if (e.getButton() == 3 && list.getSelectedValue() != null)
					open.show(list, e.getX(), e.getY());
			}
		});
		// list.setPreferredSize(new Dimension(310,376));
		JScrollPane scrollPane1 = new JScrollPane(list);
		scrollPane1.setPreferredSize(new Dimension(310, 376));
		p2.add(scrollPane1);
		tree.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				if (e.getButton() == 1) {
					super.mousePressed(e);
					TreePath tp = tree.getPathForLocation(e.getX(), e.getY());
					String pathname = new String("");
					if (tp != null) {
						treePath = tp;
						int size = treePath.getPath().length;

						if (size > 0) {
							for (int i = 1; i < size; i++) {
								pathname = pathname + treePath.getPath()[i].toString() + '\\';
							}
							File file = new File(pathname.toString());
							File[] list = file.listFiles();
							if (list == null) {
								System.out.println("此为文件");
								return;
							}
							for (int i = 0; i < list.length; i++) {
								String[] strings = list[i].getPath().split("////");
								String s1 = strings[strings.length - 1];
								DefaultMutableTreeNode node = new DefaultMutableTreeNode(
										s1.substring(s1.lastIndexOf('\\') + 1));
								((DefaultMutableTreeNode) tp.getLastPathComponent()).add(node);
								if ((new File(strings[strings.length - 1])).listFiles() != null)
									node.add(new DefaultMutableTreeNode("-"));

							}
						}
					}
				}
			}
		});

		// 展开事件
		tree.addTreeWillExpandListener(new TreeWillExpandListener() {

			@Override
			public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
				// TODO Auto-generated method stub
				TreePath tp = event.getPath();
				String pathname = new String("");
				if (tp != null) {
					treePath = tp;
					int size = treePath.getPath().length;

					if (size > 0) {
						for (int i = 1; i < size; i++) {
							pathname = pathname + treePath.getPath()[i].toString() + '\\';
						}
						File file = new File(pathname.toString());
						File[] list = file.listFiles();
						if (list == null) {
							// System.out.println("此为文件");
							return;
						}
						for (int i = 0; i < list.length; i++) {
							String[] strings = list[i].getPath().split("////");
							String s1 = strings[strings.length - 1];
							DefaultMutableTreeNode node = new DefaultMutableTreeNode(
									s1.substring(s1.lastIndexOf('\\') + 1));
							((DefaultMutableTreeNode) tp.getLastPathComponent()).add(node);
							if ((new File(strings[strings.length - 1])).listFiles() != null)
								node.add(new DefaultMutableTreeNode("-"));

						}
					}
				}
			}

			@Override
			public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
				// TODO Auto-generated method stub

			}
		});

		confirIp=new JButton("确认");
		confirIp.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(e.getSource()==confirIp)
				{
					if(t!=null)
						if(t.isAlive())
							t.stop();
					if(ip.getText().equals(""))
						JOptionPane.showMessageDialog(serveFrm, "服务器主机ip不能为空");
					else if(!java.util.regex.Pattern.matches(REGEX_IP, ip.getText()))
					{
						JOptionPane.showMessageDialog(serveFrm, "IP地址格式不正确");
					}
					else {
						IP=ip.getText();
						t=new Thread() {
							public void run() {
								try {
									load();
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							
						};
						t.start();
					}
				}
			}
		});
		stateP = new JPanel();
		stateP.setLayout(new FlowLayout());
		stateP.setBackground(Color.white);
		regMan = new JLabel();
		hostL = new JLabel();
		link = new JLabel();
		ip=new JTextField("请填写服务器ip地址");
		ip.setOpaque(false);
		ip.setFont(new Font("黑体", 11, 13));
		ip.addCaretListener(new CaretListener() {
			
			@Override
			public void caretUpdate(CaretEvent e) {
				// TODO Auto-generated method stub
				if(ip.getText().equals("请填写服务器ip地址"))
					ip.setText("");
			}
		});
		textSet(ip);
		stateP.add(ip);
		stateP.add(confirIp);
		stateP.add(regMan);
		stateP.add(hostL);
		stateP.add(link);
		hostL.setText("当前主机:" + InetAddress.getLocalHost().getHostAddress());
		regMan.setText("当前用户:" + hostN);
		// 选择事件

		pS = new JPanel();
		stateT = new JLabel("欢迎使用蓝狮文件传输");
		pS.setBackground(Color.white);
		pS.add(stateT);

		p1.add(scrollPane, JPanel.LEFT_ALIGNMENT);
		link.setForeground(Color.red);
		link.setText("请指定服务器主机!");
		serveFrm.add(p0, "Center");
		serveFrm.add(stateP, "North");
		serveFrm.add(pS, "South");
		serveFrm.pack();
		serveFrm.setVisible(true);

	}

	public void load() throws InterruptedException {
		while (true) {
			if (socket == null || socket.isClosed() || !socket.isConnected()) {
				try {
					System.out.println("新的申请");
					Socket socket = new Socket();
					SocketAddress s=new InetSocketAddress(IP, 10011);
					socket.connect(s, 5000);
					if(socket==null)
						continue;
					this.socket = socket;
					new Thread(new Task(this.socket)).start();
					link.setForeground(Color.green);
					link.setText("服务器已连接成功!");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					link.setForeground(Color.red);
					link.setText("服务器未接受请求!");
					Thread.sleep(5000);
				}
			}
			// Thread.sleep(10000);
		}
	}

	class Task implements Runnable {

		private Socket socket;

		private DataInputStream dis;

		private FileOutputStream fos;

		public Task(Socket socket) {
			this.socket = socket;
		}

		@Override
		public void run() {
			long allle = 0;
			long fileLength = 0;
			try {
				dis = new DataInputStream(socket.getInputStream());
				// System.out.println(dis);
				// 文件名和长度
				String fileName = dis.readUTF();
				model.addElement(fileName);
				fileLength = dis.readLong();
				File directory = new File("D:\\蓝狮文件传输");
				if (!directory.exists()) {
					directory.mkdir();
				}
				File file = new File(directory.getAbsolutePath() + File.separatorChar + fileName);
				System.out.println(file.toString());
				fos = new FileOutputStream(file);

				// 开始接收文件
				byte[] bytes = new byte[10240];
				int length = 0;
				System.out.println(fileLength);
				while (allle != fileLength) {
					length = dis.read(bytes, 0, bytes.length);
					System.out.println(length);
					if (length == -1)
						break;
					allle = allle + length;
					System.out.println(allle);
					fos.write(bytes, 0, length);
					fos.flush();
					stateT.setText(file.getName() + " 接收进度:" + (100 * allle / fileLength) + "%");
				}
			} catch (Exception e) {
			} finally {
				try {
					if (fos != null)
						fos.close();
					if (dis != null)
						dis.close();
					socket.close();
					socket = null;
					
				} catch (Exception e) {
				}

			}
		}
	}

	private String getFormatFileSize(long length) {
		double size = ((double) length) / (1 << 30);
		if (size >= 1) {
			return df.format(size) + "GB";
		}
		size = ((double) length) / (1 << 20);
		if (size >= 1) {
			return df.format(size) + "MB";
		}
		size = ((double) length) / (1 << 10);
		if (size >= 1) {
			return df.format(size) + "KB";
		}
		return length + "B";
	}

	public void textSet(JTextField field) {  
        field.setBackground(Color.blue);  
        field.setPreferredSize(new Dimension(130, 28));  
        MatteBorder border = new MatteBorder(0, 0, 2, 0, Color.red);  
        field.setBorder(border);  
    }
	
	public static void main(String[] args) throws UnknownHostException, InterruptedException, IOException {
		new serveMain(10011, "csx");
	}
}
