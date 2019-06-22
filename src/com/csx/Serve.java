package com.csx;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.Buffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.concurrent.locks.LockSupport;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.border.TitledBorder;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;

public class Serve {
	ArrayList<Socket> soList;

	private DefaultMutableTreeNode top = null;
	private DefaultMutableTreeNode node = null;
	JList list;
	DefaultListModel<String> model;
	JTree tree;
	JFrame serveFrm;
	JPanel p0, p1, p2, stateP, pS;
	TreePath treePath;
	Socket socket;
	File file;
	JLabel regMan, hostL, link, stateT;
	JPopupMenu right, open;
	JToolBar state;
	JButton exact;
	FileInputStream fis;
	volatile int socNum;
	int length;
	byte[] bytes = new byte[10240];
	volatile int num = 0, currentNum = 0;
	volatile boolean canSend = true;
	sendThread t;
	Thread sendS; 

	class sendThread extends Thread {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			boolean n = true;
			currentNum = soList.size();
			ArrayList<Thread> sendList = new ArrayList<Thread>();
			do {
				synchronized (bytes) {
					try {
						length = fis.read(bytes, 0, bytes.length);
						if (sendList.size() != 0) {
							for (Thread a : sendList)
								LockSupport.unpark(a);
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if (length == -1)
					break;
				if (n) {
					for (Socket s : soList) {
						sendS = new Thread(new Task(s));
						sendS.start();
						sendList.add(sendS);
					}
					n = false;
				}
				LockSupport.park();
			} while (length != -1);
		}

	}

	public Serve(String hostN) throws UnknownHostException, IOException {
		serveFrm = new JFrame("服务端");
		serveFrm.setSize(650, 480);
		serveFrm.setResizable(false);
		serveFrm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
		p2.setBorder(new TitledBorder("已发送文件"));
		top = new DefaultMutableTreeNode("这台电脑");
		File[] roots = File.listRoots();
		for (int k = 0; k < roots.length; k++) {
			node = new DefaultMutableTreeNode(roots[k].toString().substring(0, 2));
			top.add(node);
			node.add(new DefaultMutableTreeNode("-"));
		}
		tree = new JTree(top);

		JScrollPane scrollPane = new JScrollPane(tree);
		scrollPane.setPreferredSize(new Dimension(320, 405));

		model = new DefaultListModel<String>();
		list = new JList<String>(model);

		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				super.mousePressed(e);
				if (e.getButton() == 1 && e.getClickCount() == 2 && list.getSelectedValue() != null) {
					String name = list.getSelectedValue().toString();
					// System.out.println(name);
					name = "cmd /C start \"\" " + "\"" + name + "\"";
					Process p;
					try {
						p = Runtime.getRuntime().exec(name);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
		});
//		list.setPreferredSize(new Dimension(310,376));
		JScrollPane scrollPane1 = new JScrollPane(list);
		scrollPane1.setPreferredSize(new Dimension(310, 376));
		p2.add(scrollPane1);

		// 右键打开文件
		JMenuItem openm = new JMenuItem("打开文件");
		openm.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (list.getSelectedValue() != null) {
					String name = list.getSelectedValue().toString();
					// System.out.println(name);
					name = "cmd /C start \"\" " + "\"" + name + "\"";
					System.out.println(name);
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
		open.add(openm);
		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				super.mousePressed(e);
				if (e.getButton() == 3 && list.getSelectedValue() != null)
					open.show(list, e.getX(), e.getY());
			}
		});

		list.setTransferHandler(new TransferHandler() {
			@Override
			public boolean importData(JComponent comp, Transferable t1) {
				// TODO Auto-generated method stub
				try {
					Object o = t1.getTransferData(DataFlavor.javaFileListFlavor);
					String filepath = o.toString();
					if (filepath.startsWith("[")) {
						filepath = filepath.substring(1);
					}
					if (filepath.endsWith("]"))
						filepath = filepath.substring(0, filepath.length() - 1);
					int index = filepath.indexOf(",");
					ArrayList<File> filelist = new ArrayList<File>();
					if (index != -1) {
						int begin = 0;
						while (index != -1) {
							// System.out.println(filepath.substring(begin, index));
							File file = new File(filepath.substring(begin, index));
							filelist.add(file);
							begin = index + 2;
							index = filepath.indexOf(",", index + 1);
						}
						File file = new File(filepath.substring(begin));
						filelist.add(file);
					}
					if (filelist.size() == 0) {
						model.addElement(filepath);
						file = new File(filepath);
						try {
							fis = new FileInputStream(file);
						} catch (FileNotFoundException e1) {
						}
						t = new sendThread();
						t.start();
					} else {
						for (File file1 : filelist) {
							// System.out.println(file1);
							file = file1;
							model.addElement(file.toString());
							try {
								fis = new FileInputStream(file);
							} catch (FileNotFoundException e1) {
							}
							t = new sendThread();
							t.start();
							while (t.isAlive()) {
							}
							Thread.sleep(100);
						}
						return true;
					}
				} catch (Exception e) {
				}
				return false;
			}

			@Override
			public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
				// TODO Auto-generated method stub
				for (int i = 0; i < transferFlavors.length; i++) {
					if (DataFlavor.javaFileListFlavor.equals(transferFlavors[i])) {
						return true;
					}
				}
				return false;
			}
		});

		soList = new ArrayList<Socket>();

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
								pathname = pathname + treePath.getPath()[i].toString() + File.separatorChar;
							}
							File file = new File(pathname.toString());
							File[] list = file.listFiles();
							if (list == null) {
								return;
							}
							for (int i = 0; i < list.length; i++) {
								String[] strings = list[i].getPath().split("////");
								String s1 = strings[strings.length - 1];
								DefaultMutableTreeNode node = new DefaultMutableTreeNode(
										s1.substring(s1.lastIndexOf(File.separatorChar) + 1));
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
							pathname = pathname + treePath.getPath()[i].toString() + File.separatorChar;
						}
						File file = new File(pathname.toString());
						File[] list = file.listFiles();
						if (list == null) {
							return;
						}
						for (int i = 0; i < list.length; i++) {
							String[] strings = list[i].getPath().split("////");
							String s1 = strings[strings.length - 1];
							DefaultMutableTreeNode node = new DefaultMutableTreeNode(
									s1.substring(s1.lastIndexOf(File.separatorChar) + 1));
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

		// 选择事件
		tree.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				super.mousePressed(e);
				if (e.getButton() == 1 && e.getClickCount() == 2) {
					DefaultMutableTreeNode note = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

					String name = note.toString();
					if (name.equals("-"))
						return;
					while (note.getParent() != null && !note.getParent().toString().equals("这台电脑")) {
						note = (DefaultMutableTreeNode) note.getParent();
						name = note.toString() + File.separatorChar + name;
					}
					File file1 = new File(name);
					if (file1.listFiles() != null) {
						return;
					} else
						file = file1;
					model.addElement(file.toString());
					// 文件输入流
					try {
						fis = new FileInputStream(file);
					} catch (FileNotFoundException e1) {
					}
					t = new sendThread();
					t.start();
				}
			}
		});

		JMenuItem send = new JMenuItem("发送此文件");
		// 发送文件
		send.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == send) {
					DefaultMutableTreeNode note = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

					String name = note.toString();
					while (note.getParent() != null && !note.getParent().toString().equals("这台电脑")) {
						note = (DefaultMutableTreeNode) note.getParent();
						name = note.toString() + File.separatorChar + name;
					}
					file = new File(name);
					if (file.listFiles() != null) {
						return;
					}
					model.addElement(file.toString());
					try {
						fis = new FileInputStream(file);
					} catch (FileNotFoundException e1) {
					}
					t = new sendThread();
					t.start();
				}
			}
		});
		right = new JPopupMenu();
		right.add(send);
		// 右键快捷菜单
		tree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				if (e.getButton() == 3) {
					if (tree.getLastSelectedPathComponent() == null)
						return;
					DefaultMutableTreeNode note = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
					String name = note.toString();
					while (note.getParent() != null && !note.getParent().toString().equals("这台电脑")) {
						note = (DefaultMutableTreeNode) note.getParent();
						name = note.toString() + File.separatorChar + name;
					}
					if ((new File(name)).listFiles() != null || name.equals("这台电脑"))
						return;
					if (note != null)
						right.show(tree, e.getX(), e.getY());
				}

			}
		});

		stateP = new JPanel();
		stateP.setLayout(new FlowLayout());
		stateP.setBackground(Color.white);
		regMan = new JLabel();
		hostL = new JLabel();
		link = new JLabel();
		link.setForeground(Color.red);
		stateP.add(regMan);
		stateP.add(hostL);
		stateP.add(link);
		hostL.setText("当前主机:" + InetAddress.getLocalHost().getHostAddress());
		regMan.setText("当前用户:" + hostN);

		// 传输状态栏
		stateT = new JLabel();
		// 显示当前有多少台设备连接
		new Thread() {
			public void run() {
				int num = 0;
				while (true) {
					ArrayList<Socket> delList = new ArrayList<Socket>();
					synchronized (soList) {
						for (Socket s : soList) {
							// 判断socket是否断开
							if (s.isClosed() || !s.isConnected()) {
								delList.add(s);
							}
						}
						soList.removeAll(delList);
					}
					link.setText("当前连接设备:" + soList.size() + "台");
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
					}
				}
			}
		}.start();
		exact = new JButton("查看设备详情");
		exact.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == exact) {
					JFrame ex = new JFrame("设备详情");
					ex.setSize(350, 300);
					ex.setResizable(false);
					ex.setLocationRelativeTo(null);

					JPanel p1 = new JPanel();
					p1.setBackground(Color.white);
					ex.setContentPane(p1);

					DefaultListModel<String> model = new DefaultListModel<String>();
					JList list = new JList<String>(model);
					JScrollPane s1 = new JScrollPane(list);
					s1.setPreferredSize(new Dimension(330, 253));
					ex.add(s1, "Center");

					if (model.getSize() != 0)
						model.removeAllElements();
					for (Socket s : soList) {
						model.addElement(s.toString());
					}

					ex.setVisible(true);
				}
			}
		});
		stateP.add(exact);
		pS = new JPanel();
		pS.setBackground(Color.white);
		pS.add(stateT);
		stateT.setText("欢迎使用蓝狮文件传输");
		p1.add(scrollPane, "Center");
		serveFrm.add(p0, "Center");
		serveFrm.add(stateP, "North");
		serveFrm.add(pS, "South");
		serveFrm.pack();
		serveFrm.setVisible(true);
	}

	public void load() throws IOException {
		ServerSocket serve = new ServerSocket(10011);
		while (true) {
			socket = serve.accept();
			soList.add(socket);
		}
	}

	class Task implements Runnable {
		Socket socket;

		public Task(Socket socket) {
			this.socket = socket;
		}

		@Override
		public void run() {
			try {
				sendFile(socket);
			} catch (IOException e) {
			}
		}
	}

	public void sendFile(Socket socket) throws IOException {
		DataOutputStream dos = null;
		long progress = 0;
		try {
			if (file.exists()) {
				dos = new DataOutputStream(socket.getOutputStream());
				// 文件名和长度
				dos.writeUTF(file.getName());
				dos.flush();
				dos.writeLong(file.length());
				dos.flush();
				stateT.setText("正在传输" + file.getName());
				while (progress != file.length() && length != -1) {
					progress = progress + length;

					synchronized (bytes) {
						dos.write(bytes, 0, length);
						dos.flush();
						stateT.setText(file.getName() + "传输进度:" + 100 * progress / file.length() + "%");
						num++;

						if (num == currentNum) {
							LockSupport.unpark(t);
							num = 0;
						}
					}
					LockSupport.park();
				}

				System.out.println(progress + "," + file.length());

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fis != null)
				fis.close();
			if (dos != null)
				dos.close();
			socket.close();
			synchronized (soList) {
				soList.remove(socket);
			}
		}
	}

	public static void main(String[] args) throws UnknownHostException, IOException {
		new Serve("csx").load();
	}
}