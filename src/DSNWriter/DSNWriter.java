package DSNWriter;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.prefs.Preferences;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
//import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class DSNWriter extends JFrame {
	private static final long serialVersionUID = 1L;
	Preferences pref = null;

	private String _DroneType = "F1";
	private String _SalesArea = "CN";
	private String _Interface = "Uart";

	/* 串口连接 */
	private JPanel ComPanel = new JPanel();
	private JComboBox<String> srSelect = new JComboBox<String>();
	private JComboBox<String> srBaudSet = new JComboBox<String>();
	private final String[] srBaudRate = {"9600", "57600", "115200", "230400"};
	private JButton OpenPortBtn = new JButton("连接");
	/* 主面板 */
	private JPanel MainPanel = new JPanel();

	private JPanel SNPanel = new JPanel();
	private JTextField DSN_txt = new JTextField(25);
	private JPanel InfoPan = new JPanel();
	private JLabel InfoLab = new JLabel();
	private JPanel BtnPanel = new JPanel();
	private JButton WriteBtn = new JButton("写入");
	/* 菜单栏 */
	JMenuBar MenuBar = new JMenuBar();
	JMenu setMenu = new JMenu("设置(s)");
	JMenu ItemInterface = new JMenu("接口(i)");
	JMenu ItemDroneType = new JMenu("机型(t)");
	JMenu ItemSalesArea = new JMenu("区域(a)");
	JCheckBoxMenuItem ItemUart = null;
	JCheckBoxMenuItem ItemWifi = null;
	ButtonGroup Interface_bg = new ButtonGroup();

	JCheckBoxMenuItem ItemDroneF1 = null;
	JCheckBoxMenuItem ItemDroneF2 = null;
	ButtonGroup DroneType_bg = new ButtonGroup();

	JCheckBoxMenuItem ItemAreaCN = null;
	JCheckBoxMenuItem ItemAreaEU = null;
	JCheckBoxMenuItem ItemAreaAA = null;
	ButtonGroup Area_bg = new ButtonGroup();

	public DSNWriter() {
		pref = Preferences.userRoot().node(this.getClass().getName());
		_DroneType = pref.get("_dsn_DroneType", "");
		if(_DroneType.equals("")) _DroneType = "F1";
		_SalesArea = pref.get("_dsn_SalesArea", "");
		if(_SalesArea.equals("")) _SalesArea = "CN";
		_Interface = pref.get("_dsn_Interface", "");
		if(_Interface.equals("")) _Interface = "Uart";

		ItemUart = new JCheckBoxMenuItem("串口", _Interface.equals("Uart"));
		ItemWifi = new JCheckBoxMenuItem("Wifi", _Interface.equals("Wifi"));
		ItemDroneF1 = new JCheckBoxMenuItem("F1", _DroneType.equals("F1"));
		ItemDroneF2 = new JCheckBoxMenuItem("F2", _DroneType.equals("F2"));
		ItemAreaCN = new JCheckBoxMenuItem("中国", _SalesArea.equals("CN"));
		ItemAreaEU = new JCheckBoxMenuItem("欧洲", _SalesArea.equals("EU"));
		ItemAreaAA = new JCheckBoxMenuItem("北美", _SalesArea.equals("AA"));

		ItemDroneF1.addActionListener(dtl); ItemDroneF2.addActionListener(dtl);
		ItemAreaCN.addActionListener(sal); ItemAreaEU.addActionListener(sal); ItemAreaAA.addActionListener(sal);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				setTitle("F1/2序列号写入工具  V0.1.0");
				setSize(600, 280);
				setResizable(false);
				setLocationRelativeTo(null);
				setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				setIconImage(getToolkit().getImage(DSNWriter.class.getResource("gitkitty.png")));
				setVisible(true);

				setJMenuBar(MenuBar);
				MenuBar.add(setMenu);
				setMenu.setMnemonic('s');
				setMenu.setFont(new Font("宋体", Font.PLAIN, 14));
				ItemInterface.setMnemonic('i');
				ItemInterface.setFont(new Font("宋体", Font.PLAIN, 14));
				setMenu.add(ItemInterface);
				setMenu.addSeparator();
				ItemDroneType.setMnemonic('t');
				ItemDroneType.setFont(new Font("宋体", Font.PLAIN, 14));
				setMenu.add(ItemDroneType);
				setMenu.addSeparator();
				ItemSalesArea.setMnemonic('a');
				ItemSalesArea.setFont(new Font("宋体", Font.PLAIN, 14));
				setMenu.add(ItemSalesArea);
				ItemUart.setFont(new Font("宋体", Font.PLAIN, 14));
				Interface_bg.add(ItemUart);
				ItemInterface.add(ItemUart);
				ItemWifi.setFont(new Font("宋体", Font.PLAIN, 14));
				Interface_bg.add(ItemWifi);
				ItemInterface.add(ItemWifi);
				ItemDroneF1.setFont(new Font("宋体", Font.PLAIN, 14));
				DroneType_bg.add(ItemDroneF1);
				ItemDroneType.add(ItemDroneF1);
				ItemDroneF2.setFont(new Font("宋体", Font.PLAIN, 14));
				DroneType_bg.add(ItemDroneF2);
				ItemDroneType.add(ItemDroneF2);

				ItemAreaCN.setFont(new Font("宋体", Font.PLAIN, 14));
				Area_bg.add(ItemAreaCN);
				ItemSalesArea.add(ItemAreaCN);
				ItemAreaEU.setFont(new Font("宋体", Font.PLAIN, 14));
				Area_bg.add(ItemAreaEU);
				ItemSalesArea.add(ItemAreaEU);
				ItemAreaAA.setFont(new Font("宋体", Font.PLAIN, 14));
				Area_bg.add(ItemAreaAA);
				ItemSalesArea.add(ItemAreaAA);

				ComPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
				ComPanel.setBackground(new Color(233, 80, 80, 160));
				if(_Interface.equals("Uart")) {
					srSelect.setPreferredSize(new Dimension(90, 30));
					srSelect.setFont(srSelect.getFont().deriveFont(Font.BOLD, 14));
					srSelect.setToolTipText("select com port");

					srBaudSet.setPreferredSize(new Dimension(90, 30));
					srBaudSet.setMaximumRowCount(5);
					srBaudSet.setEditable(false);
					for(String s : srBaudRate)
						srBaudSet.addItem(s);
					srBaudSet.setSelectedIndex(2);//default: 115200
					srBaudSet.setFont(srBaudSet.getFont().deriveFont(Font.BOLD, 14));
					srBaudSet.setToolTipText("set baudrate");

					OpenPortBtn.setPreferredSize(new Dimension(90, 30));
					OpenPortBtn.setFont(new Font("宋体", Font.BOLD, 18));
//					OpenPortBtn.addActionListener(sbl);
					OpenPortBtn.setToolTipText("open com port");

					ComPanel.add(srSelect);
					ComPanel.add(srBaudSet);
					ComPanel.add(OpenPortBtn);
				} else {
					
				}
				add(ComPanel, BorderLayout.NORTH);
				MainPanel.setLayout(new GridLayout(3, 1));

				DSN_txt.setEditable(false);
				DSN_txt.setText("PX1707000000FxCN");
				DSN_txt.setHorizontalAlignment(JTextField.CENTER);
				DSN_txt.setFont(new Font("Courier New", Font.BOLD, 32));
				SNPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 10));
				SNPanel.add(DSN_txt); MainPanel.add(SNPanel);
				InfoLab.setText("机型: " + _DroneType + " - 区域: " + GetAreaName(_SalesArea));
				InfoLab.setFont(new Font("楷体", Font.BOLD, 30));
				InfoPan.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 10));
				InfoPan.add(InfoLab); MainPanel.add(InfoPan);
				WriteBtn.setEnabled(false);
				WriteBtn.setFont(new Font("黑体", Font.BOLD, 30));
				WriteBtn.setPreferredSize(new Dimension(280, 46));
				BtnPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
				BtnPanel.add(WriteBtn); MainPanel.add(BtnPanel);
				add(MainPanel, BorderLayout.CENTER);
			}
		});
	}

	private ActionListener dtl = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if(ItemDroneF1.isSelected()) _DroneType = "F1";
			if(ItemDroneF2.isSelected()) _DroneType = "F2";
			InfoLab.setText("机型: " + _DroneType + " - 区域: " + GetAreaName(_SalesArea));
			pref.put("_dsn_DroneType", _DroneType);
		}
	};
	private ActionListener sal = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if(ItemAreaCN.isSelected()) _SalesArea = "CN";
			if(ItemAreaEU.isSelected()) _SalesArea = "EU";
			if(ItemAreaAA.isSelected()) _SalesArea = "AA";
			InfoLab.setText("机型: " + _DroneType + " - 区域: " + GetAreaName(_SalesArea));
			pref.put("_dsn_SalesArea", _SalesArea);
		}
	};

	private static String GetAreaName(String n) {
		String Name = null;
		if(n.equals("CN")) Name = "中国";
		if(n.equals("EU")) Name = "欧洲";
		if(n.equals("AA")) Name = "北美";
		return Name;
	}

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		new DSNWriter();
	}
}
