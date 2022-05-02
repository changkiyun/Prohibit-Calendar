package diary;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

class DateInputException extends Exception {
};

class DataEmptyException extends Exception {
};

public class Calendar extends JFrame {
	Container c;

	DisplayMonth dm = new DisplayMonth();
	cYear cm = new cYear();
	Todo_List tl = new Todo_List();

	// �ݿ� Ÿ�̸� ����
	DecimalFormat mf = new DecimalFormat("###,###,###");// �ݾ� ��� ����
	JLabel l1 = new JLabel("�ݿ� ������");
	JLabel l2 = new JLabel("�ݿ� �Ⱓ");
	JLabel l3 = new JLabel("�ǿ��� ���� ���");
	JLabel l4 = new JLabel("������ �ݾ�");
	JLabel l5 = new JLabel("�þ ����");// ���� �̸� ���̺�
	JCheckBox set_today; // ���ó�¥�� �ٲٱ� üũ�ڽ�
	ImageIcon line = new ImageIcon("images/Line.png");
	JLabel[] line_label = new JLabel[20];// ���� ���м� ���̺�
	JPanel smoke_data, smoke_data2; // �� ���� �Է� �� ��ü�Ǵ� �г�
	JButton prohibit_start_button; // �ʱ�ȭ�� �ݿ� ���۹�ư
	JPanel prohibit_info, smoke_info; // �޺��ڽ� ���� �� ����Ǵ� �г�
	JComboBox select_data;
	JLabel smoke_start_day, prohibit_period, cut_down_cigarret, reduce_money, increase_life; // �˾� ������� ���̺�
	JLabel smoke_start_day2, prohibit_period2, cut_down_cigarret2, reduce_money2, increase_life2; // ����ȭ�� ������� ���̺�
	DataInput SDI = new DataInput(); // ������ �Է�â ����
	Font font = new Font("���� ���", Font.BOLD, 20);
	Font font2 = new Font("���� ���", Font.PLAIN, 20);// ��Ʈ
	// �ݿ� Ÿ�̸� ����

	// Calendar �޼ҵ�
	public Calendar() {
		setTitle("�ݿ� ���̾");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);

		SDI.setVisible(false);

		c = getContentPane();
		c.setLayout(null);
		c.setBackground(Color.WHITE);
		prohibitTimerBeforeData(); // ���� �Է� �� ȭ�� �޼ҵ�
		smokeTimerAfterData(); // ���� �Է� �� ȭ�� �޼ҵ�
		prohibitDB(); // Calendarȭ�� ������ ������ ���� Ȯ�� �� ���� ��� �޼ҵ�

		// Todo����Ʈ
		JPanel todo_list = new JPanel();
		todo_list.setBounds(0, 360, 355, 355);
		todo_list.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		todo_list.add(tl);

		// ���� Ķ����
		JPanel calendar_main = new JPanel();
		calendar_main.setBounds(360, 0, 915, 715);
		calendar_main.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		calendar_main.setLayout(null);
		calendar_main.add(cm);
		calendar_main.add(dm);

		cm.setBounds(40, 40, 400, 150);
		dm.setBounds(10, 210, 470, 500);

		c.add(todo_list);
		c.add(calendar_main);
		c.add(todo_list);

		setSize(865, 755);
		setVisible(true);
	}// ������ ����

	// ���� �Է� �� ȭ�� �޼ҵ�
	public void prohibitTimerBeforeData() {
		// �� ������ ��� �г�
		smoke_data = new JPanel();
		smoke_data.setBounds(0, 0, 355, 355);
		smoke_data.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		smoke_data.setLayout(null);

		// �ʱ�ȭ�� �ؽ�Ʈ
		JLabel smoke_text = new JLabel(
				"<html><body style='text-align:center;'>������� ��������<br /> �Է��ϰ� �ݿ��� �����ϼ���.</body></html>");
		smoke_text.setFont(font);
		smoke_text.setBounds(40, 95, 300, 100);

		// �ʱ�ȭ�� �ݿ����۹�ư
		prohibit_start_button = new JButton("�ݿ� ����");
		prohibit_start_button.setFont(font);
		prohibit_start_button.setBounds(100, 220, 150, 40);
		prohibit_start_button.addActionListener(new prohibitTimerBeforeDataListener());

		// �г�, �����̳ʿ� �߰�
		smoke_data.add(smoke_text);
		smoke_data.add(prohibit_start_button);
		c.add(smoke_data);
	} // prohibitTimerBeforeData

	// �ݿ� ���� ��ư Ŭ���� ���� �Է� �˾�â ����
	class prohibitTimerBeforeDataListener implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			if (ae.getActionCommand() == "�ݿ� ����") {
				SDI.setTitle("�� ���� �Է�");
				SDI.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				SDI.setSize(350, 450);
				SDI.setVisible(true);

			}
		}
	} // prohibitTimerBeforeDataListener

	// ���� �Է� �� ȭ�� �޼ҵ�
	public void smokeTimerAfterData() {
		smoke_data2 = new JPanel();
		smoke_data2.setBounds(0, 0, 355, 355);
		smoke_data2.setLayout(null);
		smoke_data2.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		smoke_data2.setVisible(false);// �������� setVisible(false)

		// ������ ���� �޺��ڽ�, ���й�ư (BorderLayout NORTH)
		JPanel top = new JPanel();
		top.setLayout(null);
		top.setBounds(3, 3, 345, 50);

		String[] combobox_smoke_data = { "�ݿ�������", "��������" };
		select_data = new JComboBox(combobox_smoke_data);
		select_data.addActionListener(new smokeTimerAfterDataListener());

		JButton fail_button = new JButton("����");
		fail_button.addActionListener(new ProhibitFailListener());

		select_data.setBounds(120, 10, 100, 30);
		fail_button.setBounds(250, 10, 80, 30);

		// �ݿ�, �� ���� ���̺� �����ؼ� �� �гο��� ���
		prohibit_info = new JPanel();
		prohibit_info.setLayout(null);
		prohibit_info.setBounds(5, 45, 345, 305);

		// �� ���� ��� ���̺�
		smoke_start_day2 = new JLabel();
		prohibit_period2 = new JLabel();
		cut_down_cigarret2 = new JLabel();
		reduce_money2 = new JLabel();
		increase_life2 = new JLabel();

		// ���̺� ��Ʈ ����
		smoke_start_day2.setFont(font2);
		prohibit_period2.setFont(font2);
		cut_down_cigarret2.setFont(font2);
		reduce_money2.setFont(font2);
		increase_life2.setFont(font2);

		// �� ���� ��� �гο� ���̺� �߰�
		prohibit_info.add(smoke_start_day2);
		prohibit_info.add(prohibit_period2);
		prohibit_info.add(cut_down_cigarret2);
		prohibit_info.add(reduce_money2);
		prohibit_info.add(increase_life2);

		// ���� �̸� ���
		prohibit_info.add(l1);
		prohibit_info.add(l2);
		prohibit_info.add(l3);
		prohibit_info.add(l4);
		prohibit_info.add(l5);

		for (int i = 0; i <= 5; i++) {
			line_label[i] = new JLabel(line);
			prohibit_info.add(line_label[i]);
		}

		// ������� ���̺� ��ġ����
		smoke_start_day2.setBounds(100, 15, 300, 30);
		prohibit_period2.setBounds(100, 75, 300, 30);
		cut_down_cigarret2.setBounds(100, 135, 300, 30);
		reduce_money2.setBounds(100, 195, 300, 30);
		increase_life2.setBounds(100, 255, 300, 30);

		// ���� �̸� ���̺�
		l1.setBounds(20, 0, 300, 30);
		l2.setBounds(20, 60, 300, 30);
		l3.setBounds(20, 120, 300, 30);
		l4.setBounds(20, 180, 300, 30);
		l5.setBounds(20, 240, 300, 30);

		// ���м�
		line_label[0].setBounds(15, 55, 320, 2);
		line_label[1].setBounds(15, 115, 320, 2);
		line_label[2].setBounds(15, 175, 320, 2);
		line_label[3].setBounds(15, 235, 320, 2);
		line_label[4].setBounds(15, 295, 320, 2);

		top.add(select_data);
		top.add(fail_button);
		smoke_data2.add(top);
		smoke_data2.add(prohibit_info);
		c.add(smoke_data2);

	}// smokeTimerAfterData

	// // ���� �Է� �� ȭ�� ������
	class smokeTimerAfterDataListener implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			int selected = select_data.getSelectedIndex();

			// �ݿ����� ���ý� ������
			if (selected == 0) {
				try {
					Class.forName("com.mysql.cj.jdbc.Driver");
					System.err.println("JDBC-ODBC ����̹��� ���������� �ε���");
				} catch (ClassNotFoundException e) {
					System.err.println("����̹� �ε忡 �����߽��ϴ�.");
				} // JDBC ����̹� �ε�

				try {
					Connection con = DriverManager.getConnection(
							"jdbc:mysql://localhost:3306/smokedata?serverTimezone=UTC", "root", "dbsckdrl12");
					System.out.println("DB ���� �Ϸ�.");
					Statement dbSt = con.createStatement();
					System.out.println("JDBC ����̹��� ���������� ����Ǿ����ϴ�.");

					String strSQL;
					strSQL = "SELECT * FROM smoke_data; ";

					ResultSet result = dbSt.executeQuery(strSQL);

					if (result.next()) {
						// DB�� ���������� �о����
						int sy = result.getInt("smoke_year");
						int sm = result.getInt("smoke_month");
						int sd = result.getInt("smoke_day");
						int sq = result.getInt("smoke_quantity");
						int cp = result.getInt("cigarette_price");

						// �о�� sy, sm, sd ������� LocalDate�� ��¥ ����
						LocalDate startDate = LocalDate.of(sy, sm, sd);
						LocalDate endDate = LocalDate.now();

						// Period�� ��ü period�� ��¥ ���� ����
						Period period = startDate.until(endDate);
						int yeart = period.getYears();
						int montht = period.getMonths();
						int dayt = period.getDays();

						int prohibit_period_between = (int) ChronoUnit.DAYS.between(startDate, endDate); // ��¥ ���� ���

						int cdct = (sq * 20) * prohibit_period_between; // �ǿ��� ���� ��� ����
						int rm = cp * prohibit_period_between; // ������ �ݾ�
						int il = ((sq * 20) * prohibit_period_between) * 15; // �þ ����

						int il_year = il / 525600;
						int il_month = il % 525600 / 43200;
						int il_day = il % 525600 % 43200 / 1440;

						smoke_start_day2.setText(result.getString("smoke_year") + "�� " + result.getString("smoke_month")
								+ "�� " + result.getString("smoke_day") + "��");
						prohibit_period2.setText(yeart + "�� " + montht + "���� " + dayt + "��");
						cut_down_cigarret2.setText(mf.format(cdct) + "����");
						reduce_money2.setText(mf.format(rm) + "��");
						increase_life2.setText(il_year + "�� " + il_month + "�� " + il_day + "��");

						// �����̸� ���̺� ����
						l1.setText("�ݿ� ������");
						l2.setText("�ݿ� �Ⱓ");
						l3.setText("�ǿ��� ���� ���");
						l4.setText("������ �ݾ�");
						l5.setText("�þ ����");
					}
					dbSt.close();
					con.close();
				} catch (SQLException e) {
					System.out.println("SQLException : " + e.getMessage());
				}
			}

			// ������ ���ý� ������
			else if (selected == 1) {
				try {
					Class.forName("com.mysql.cj.jdbc.Driver");
					System.err.println("JDBC-ODBC ����̹��� ���������� �ε���");
				} catch (ClassNotFoundException e) {
					System.err.println("����̹� �ε忡 �����߽��ϴ�.");
				} // JDBC ����̹� �ε�

				try {
					Connection con = DriverManager.getConnection(
							"jdbc:mysql://localhost:3306/smokedata?serverTimezone=UTC", "root", "dbsckdrl12");
					System.out.println("DB ���� �Ϸ�.");
					Statement dbSt = con.createStatement();
					System.out.println("JDBC ����̹��� ���������� ����Ǿ����ϴ�.");

					String strSQL;
					strSQL = "SELECT * FROM smoke_data; ";

					ResultSet result = dbSt.executeQuery(strSQL);

					if (result.next()) {
						// DB�� ���������� �о����
						int sy = result.getInt("smoke_year");
						int sm = result.getInt("smoke_month");
						int sd = result.getInt("smoke_day");
						int py = result.getInt("prohibit_year");
						int pm = result.getInt("prohibit_month");
						int pd = result.getInt("prohibit_day");
						int sq = result.getInt("smoke_quantity");
						int cp = result.getInt("cigarette_price");

						// �о�� sy, sm, sd ������� LocalDate�� ��¥ ����
						LocalDate startDate = LocalDate.of(py, pm, pd);
						LocalDate endDate = LocalDate.of(sy, sm, sd);

						// Period�� ��ü period�� ��¥ ���� ����
						Period period = startDate.until(endDate);
						int yeart = period.getYears();
						int montht = period.getMonths();
						int dayt = period.getDays();

						int prohibit_period_between = (int) ChronoUnit.DAYS.between(startDate, endDate); // ��¥ ���� ���

						int cdct = (sq * 20) * prohibit_period_between; // �ǿ� ��� ����
						int rm = cp * prohibit_period_between; // ����� �ݾ�
						int il = ((sq * 20) * prohibit_period_between) * 15; // �پ�� ����

						// ���� ��, ��, �� ��ȯ
						int il_year = il / 525600;
						int il_month = il % 525600 / 43200;
						int il_day = il % 525600 % 43200 / 1440;

						// ������� ���̺� ����
						smoke_start_day2.setText(result.getString("prohibit_year") + "�� "
								+ result.getString("prohibit_month") + "�� " + result.getString("prohibit_day") + "��");
						prohibit_period2.setText(yeart + "�� " + montht + "���� " + dayt + "��");
						cut_down_cigarret2.setText(mf.format(cdct) + "����");
						reduce_money2.setText(mf.format(rm) + "��");
						increase_life2.setText(il_year + "�� " + il_month + "�� " + il_day + "��");

						// �����̸� ���̺� ����
						l1.setText("�� ������");
						l2.setText("�� �Ⱓ");
						l3.setText("�ǿ� ���");
						l4.setText("����� �ݾ�");
						l5.setText("�پ�� ����");
					}
					dbSt.close();
					con.close();
				} catch (SQLException e) {
					System.out.println("SQLException : " + e.getMessage());
				}
			} // else if
		}// actionPerformed
	}// smokeTimerAfterDataListener

	// �ݿ� ���� �� �˾�
	public class ProhibitFail extends JFrame {
		JLabel top_text, bottom_text;
		JButton OK;
		Font font2 = new Font("���� ���", Font.PLAIN, 20);

		public ProhibitFail() {
			setTitle("�ݿ�����");
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

			Container c = getContentPane();
			c.setLayout(new BorderLayout());

			JPanel top = new JPanel();
			JLabel info = new JLabel("������� �޼��� �ݿ� �����Դϴ�.");
			top.setLayout(new FlowLayout(FlowLayout.CENTER, 40, 20));
			top.add(info);
			info.setFont(font2);

			// center �г�
			JPanel center = new JPanel();
			center.setLayout(null);

			JLabel jl1 = new JLabel("�ݿ� ������");
			JLabel jl2 = new JLabel("�ݿ� �Ⱓ");
			JLabel jl3 = new JLabel("�ǿ��� ���� ���");
			JLabel jl4 = new JLabel("������ �ݾ�");
			JLabel jl5 = new JLabel("�þ ����");

			// �� ���� ��� ���̺� Calendar Class ��������
			smoke_start_day = new JLabel();
			prohibit_period = new JLabel();
			cut_down_cigarret = new JLabel();
			reduce_money = new JLabel();
			increase_life = new JLabel();

			// ������� ���̺� ��ġ����
			smoke_start_day.setBounds(120, 15, 300, 30);
			prohibit_period.setBounds(120, 75, 300, 30);
			cut_down_cigarret.setBounds(120, 135, 300, 30);
			reduce_money.setBounds(120, 195, 300, 30);
			increase_life.setBounds(120, 255, 300, 30);

			// �� ���� ��� �гο� ���̺� �߰�
			center.add(smoke_start_day);
			center.add(prohibit_period);
			center.add(cut_down_cigarret);
			center.add(reduce_money);
			center.add(increase_life);

			// ���� �̸� ���
			center.add(jl1);
			center.add(jl2);
			center.add(jl3);
			center.add(jl4);
			center.add(jl5);

			jl1.setBounds(40, 0, 300, 30);
			jl2.setBounds(40, 60, 300, 30);
			jl3.setBounds(40, 120, 300, 30);
			jl4.setBounds(40, 180, 300, 30);
			jl5.setBounds(40, 240, 300, 30);

			for (int i = 10; i < 20; i++) {
				line_label[i] = new JLabel(line);
				center.add(line_label[i]);
			}

			line_label[10].setBounds(35, 55, 320, 2);
			line_label[11].setBounds(35, 115, 320, 2);
			line_label[12].setBounds(35, 175, 320, 2);
			line_label[13].setBounds(35, 235, 320, 2);
			line_label[14].setBounds(35, 295, 320, 2);

			// ���̺� ��Ʈ ����
			smoke_start_day.setFont(font2);
			prohibit_period.setFont(font2);
			cut_down_cigarret.setFont(font2);
			reduce_money.setFont(font2);
			increase_life.setFont(font2);

			// ���� �г�
			OK = new JButton("Ȯ��");
			
			//Ȯ�� ��ư Ŭ�� �� �˾� â ����
			OK.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					setVisible(false);
				}
			});
			JLabel bottom_text = new JLabel(
					"<html><body style='text-align:center;'>�������� �ݿ����� ��ư�� ������<br /> �ٽ� �ݿ��� �����ϼ���.</body></html>");

			center.add(bottom_text);
			center.add(OK);

			bottom_text.setBounds(100, 270, 300, 100);
			OK.setBounds(140, 350, 90, 30);

			c.add(top, BorderLayout.NORTH);
			c.add(center);
			setSize(400, 500);
			setVisible(true);
		}
	}// �ݿ����� �˾� Ŭ���� ����

	// �ݿ� ���н� �˾�
	// ������---------------------------------------------------------------------
	public class ProhibitFailListener implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			String s = ae.getActionCommand();
			if (s == "����") {
				new ProhibitFail();
				smoke_data.setVisible(true);
				smoke_data2.setVisible(false);
				try {
					Class.forName("com.mysql.cj.jdbc.Driver");
					System.err.println("JDBC-ODBC ����̹��� ���������� �ε���");
				} catch (ClassNotFoundException e) {
					System.err.println("����̹� �ε忡 �����߽��ϴ�.");
				} // JDBC ����̹� �ε�

				try {
					Connection con = DriverManager.getConnection(
							"jdbc:mysql://localhost:3306/smokedata?serverTimezone=UTC", "root", "dbsckdrl12");
					System.out.println("DB ���� �Ϸ�.");
					Statement dbSt = con.createStatement();
					System.out.println("JDBC ����̹��� ���������� ����Ǿ����ϴ�.");

					String strSQL;
					strSQL = "SELECT * FROM smoke_data; ";
					ResultSet result = dbSt.executeQuery(strSQL);

					if (result.next()) {
						// DB�� ���������� �о����
						int sy = result.getInt("smoke_year");
						int sm = result.getInt("smoke_month");
						int sd = result.getInt("smoke_day");
						int sq = result.getInt("smoke_quantity");
						int cp = result.getInt("cigarette_price");

						// �о�� sy, sm, sd ������� LocalDate�� ��¥ ����
						LocalDate startDate = LocalDate.of(sy, sm, sd);
						LocalDate endDate = LocalDate.now();

						// Period�� ��ü period�� ��¥ ���� ����
						Period period = startDate.until(endDate);
						int yeart = period.getYears();
						int montht = period.getMonths();
						int dayt = period.getDays();

						int prohibit_period_between = (int) ChronoUnit.DAYS.between(startDate, endDate); // ��¥ ���� ���

						int cdct = (sq * 20) * prohibit_period_between; // �ǿ��� ���� ��� ����
						int rm = cp * prohibit_period_between; // ������ �ݾ�
						int il = ((sq * 20) * prohibit_period_between) * 15; // �þ ����

						int il_year = il / 525600;
						int il_month = il % 525600 / 43200;
						int il_day = il % 525600 % 43200 / 1440;

						smoke_start_day.setText(result.getString("smoke_year") + "�� " + result.getString("smoke_month")
								+ "�� " + result.getString("smoke_day") + "��");
						prohibit_period.setText(yeart + "�� " + montht + "���� " + dayt + "��");
						cut_down_cigarret.setText(mf.format(cdct) + "����");
						reduce_money.setText(mf.format(rm) + "��");
						increase_life.setText(il_year + "�� " + il_month + "�� " + il_day + "��");

						strSQL = "delete from smoke_data; ";
						dbSt.executeUpdate(strSQL);

					}

					dbSt.close();
					con.close();
				} catch (SQLException e) {
					System.out.println("SQLException : " + e.getMessage());
				}

			} // if�� ����
		}// actionPerformed
	}

	// ������ �Է�ȭ�� Ŭ���� ---------------------------------------------
	public class DataInput extends JFrame {
		String[] price = { "4000", "4500", "4800", "5000", "8000" };
		String[] year = { "2000", "2001", "2002", "2003", "2004", "2005", "2006", "2007", "2008", "2009", "2010",
				"2011", "2012", "2013", "2014", "2015", "2016", "2017", "2018", "2019", "2020", "2021" };
		String[] month = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12" };
		String[] day = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17",
				"18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31" };

		JTextField smoke_quantity = new JTextField(10);
		JTextField smoke_period = new JTextField(10);
		JComboBox cigarette_price = new JComboBox(price);
		JComboBox smoke_year = new JComboBox(year);
		JComboBox smoke_month = new JComboBox(month);
		JComboBox smoke_day = new JComboBox(day);
		JComboBox prohibit_year = new JComboBox(year);
		JComboBox prohibit_month = new JComboBox(month);
		JComboBox prohibit_day = new JComboBox(day);
		JButton b1 = new JButton("Ȯ��");
		JButton b2 = new JButton("���");

		// ������
		DataInput() {
			Container c = getContentPane();
			c.setLayout(new BorderLayout(0, 30));

			JPanel top = new JPanel();
			top.setLayout(new GridLayout(5, 1, 0, 15));

			// ���� ���� �Է�
			JPanel p1 = new JPanel();
			p1.setLayout(new FlowLayout(FlowLayout.LEFT));
			p1.add(new JLabel("���� ����          :  "));
			p1.add(smoke_quantity);
			p1.add(new JLabel("��"));

			// �� �޼� �Է�
			JPanel p2 = new JPanel();
			p2.setLayout(new FlowLayout(FlowLayout.LEFT));
			p2.add(new JLabel("�� �޼�              :  "));
			p2.add(smoke_period);
			p2.add(new JLabel("��"));

			// ��� ���� �Է�
			JPanel p3 = new JPanel();
			p3.setLayout(new FlowLayout(FlowLayout.LEFT));
			p3.add(new JLabel("��� ����              :  "));
			p3.add(cigarette_price);
			cigarette_price.setSelectedItem("4500");
			p3.add(new JLabel("��"));

			// �� ������ �Է�
			JPanel p4 = new JPanel();
			p4.setLayout(new FlowLayout(FlowLayout.LEFT));
			p4.add(new JLabel("�� ������          :  "));
			// ����
			p4.add(prohibit_year);
			p4.add(new JLabel("��"));
			// ��
			p4.add(prohibit_month);
			p4.add(new JLabel("��"));
			// ��
			p4.add(prohibit_day);
			p4.add(new JLabel("��"));

			// �ݿ� ������ �Է�
			JPanel p5 = new JPanel();
			p5.setLayout(new FlowLayout(FlowLayout.LEFT));
			p5.add(new JLabel("�ݿ� ������          :  "));
			p5.add(smoke_year);
			p5.add(new JLabel("��"));
			p5.add(smoke_month);
			p5.add(new JLabel("��"));
			p5.add(smoke_day);
			p5.add(new JLabel("��"));
			p5.add(new JLabel("                                 "));
			set_today = new JCheckBox("���÷� ����");
			p5.add(set_today);
			set_today.addItemListener(new DataInputListenerToday());

			// �����Է¶�
			top.add(p1);
			top.add(p2);
			top.add(p3);
			top.add(p4);
			top.add(p5);
			c.add(top, BorderLayout.CENTER);

			// Ȯ��, ��ҹ�ư
			JPanel bottom = new JPanel();
			bottom.add(b1);
			b1.addActionListener(new DataInputListener());
			bottom.add(b2);
			b2.addActionListener(new DataInputListener());
			c.add(bottom, BorderLayout.SOUTH);
		}// ������ ����

		public class DataInputListenerToday implements ItemListener {
			LocalDate today = LocalDate.now();

			// Period�� ��ü period�� ��¥ ���� ����
			int year = today.getYear();
			int month = today.getMonthValue();
			int day = today.getDayOfMonth();

			// ���� ��¥�� ����
			public void itemStateChanged(ItemEvent ie) {
				if (ie.getStateChange() == ItemEvent.SELECTED) {
					smoke_year.setSelectedItem((Object) Integer.toString(year));
					smoke_month.setSelectedItem((Object) Integer.toString(month));
					smoke_day.setSelectedItem((Object) Integer.toString(day));
				}
			}// itemStateChanged
		}

		// ������ �Է�ȭ�� Ȯ��,��� �׼�
		public class DataInputListener implements ActionListener {
			String t_smoke_quantity, t_smoke_period, t_cigarette_price, t_smoke_year, t_smoke_month, t_smoke_day,
					t_prohibit_year, t_prohibit_month, t_prohibit_day;

			public void actionPerformed(ActionEvent ae) {
				String s = ae.getActionCommand();

				int sye = Integer.parseInt((String) smoke_year.getSelectedItem());
				int sme = Integer.parseInt((String) smoke_month.getSelectedItem());
				int sde = Integer.parseInt((String) smoke_day.getSelectedItem());
				int pye = Integer.parseInt((String) prohibit_year.getSelectedItem());
				int pme = Integer.parseInt((String) prohibit_month.getSelectedItem());
				int pde = Integer.parseInt((String) prohibit_day.getSelectedItem());
				String sqe = smoke_quantity.getText();
				String spe = smoke_period.getText();
				
				LocalDate startDatee = LocalDate.of(pye, pme, pde);
				LocalDate endDatee = LocalDate.of(sye, sme, sde);

				Boolean date = startDatee.isAfter(endDatee);
				try {
					if (s == "Ȯ��") {
						if (date)
							throw new DateInputException();
						try {
							Class.forName("com.mysql.cj.jdbc.Driver");
							System.err.println("JDBC ����̹��� ���������� �ε���");
						} catch (ClassNotFoundException e) {
							System.err.println("����̹� �ε忡 �����߽��ϴ�.");
						} // catch
						if (smoke_period.getText().equals("") || smoke_quantity.getText().equals(""))
							throw new DataEmptyException();
						try {
							Connection con = DriverManager.getConnection(
									"jdbc:mysql://localhost:3306/smokedata?serverTimezone=UTC", "root", "dbsckdrl12");
							System.out.println("DB ���� �Ϸ�");
							Statement dbSt = con.createStatement();
							System.out.println("JDBC ����̹��� ���������� ����Ǿ����ϴ�.");

							t_smoke_quantity = smoke_quantity.getText();
							t_smoke_period = smoke_period.getText();
							t_cigarette_price = (String) cigarette_price.getSelectedItem();
							t_smoke_year = (String) smoke_year.getSelectedItem();
							t_smoke_month = (String) smoke_month.getSelectedItem();
							t_smoke_day = (String) smoke_day.getSelectedItem();
							t_prohibit_year = (String) prohibit_year.getSelectedItem();
							t_prohibit_month = (String) prohibit_month.getSelectedItem();
							t_prohibit_day = (String) prohibit_day.getSelectedItem();

							String strSql = "INSERT INTO smoke_data (smoke_quantity, smoke_period, cigarette_price, smoke_year,smoke_month, smoke_day, prohibit_year, prohibit_month, prohibit_day) VALUES ("
									+ "'" + t_smoke_quantity + "', '" + t_smoke_period + "', '" + t_cigarette_price
									+ "', '" + t_smoke_year + "', '" + t_smoke_month + "', '" + t_smoke_day + "','"
									+ t_prohibit_year + "', '" + t_prohibit_month + "', '" + t_prohibit_day + "');";
							dbSt.executeUpdate(strSql);
							System.out.println("������ ���� �Ϸ�");

							// Ȯ�ι�ư Ŭ�� �� �ݿ����� ���ȭ�� ������ ���
							strSql = "SELECT * FROM smoke_data; ";

							ResultSet result = dbSt.executeQuery(strSql);

							if (result.next()) {
								// DB�� ���������� �о����
								int sy = result.getInt("smoke_year");
								int sm = result.getInt("smoke_month");
								int sd = result.getInt("smoke_day");
								int sq = result.getInt("smoke_quantity");
								int cp = result.getInt("cigarette_price");

								// �о�� sy, sm, sd ������� LocalDate�� ��¥ ����
								LocalDate startDate = LocalDate.of(sy, sm, sd);
								LocalDate endDate = LocalDate.now();

								// Period�� ��ü period�� ��¥ ���� ����
								Period period = startDate.until(endDate);
								int yeart = period.getYears();
								int montht = period.getMonths();
								int dayt = period.getDays();

								int prohibit_period_between = (int) ChronoUnit.DAYS.between(startDate, endDate); // ��¥���� ���

								int cdct = (sq * 20) * prohibit_period_between; // �ǿ��� ���� ��� ����
								int rm = cp * prohibit_period_between; // ������ �ݾ�
								int il = ((sq * 20) * prohibit_period_between) * 15; // �þ ����

								int il_year = il / 525600;
								int il_month = il % 525600 / 43200;
								int il_day = il % 525600 % 43200 / 1440;

								smoke_start_day2.setText(result.getString("smoke_year") + "�� "
										+ result.getString("smoke_month") + "�� " + result.getString("smoke_day") + "��");
								prohibit_period2.setText(yeart + "�� " + montht + "���� " + dayt + "��");
								cut_down_cigarret2.setText(mf.format(cdct) + "����");
								reduce_money2.setText(mf.format(rm) + "��");
								increase_life2.setText(il_year + "�� " + il_month + "�� " + il_day + "��");
							}

							dbSt.close();
							con.close();

							smoke_data.setVisible(false);
							smoke_data2.setVisible(true);
							SDI.setVisible(false);

						} catch (SQLException e) {
							System.out.println(" SQLException: " + e.getMessage());
						} // catch
					}

					else if (s == "���") {
						SDI.setVisible(false);
					} // else if
				} catch (DateInputException e) {
					DateSelectErr die = new DateSelectErr(SDI, "���ÿ���", false, "�� ���� ��¥�� �ݿ� ���� ��¥ �����̾���մϴ�.");
					die.setVisible(true);
				} catch (DataEmptyException e) {
					DateSelectErr dee = new DateSelectErr(SDI, "�Է¿���", false, "������ ��� �Է����ּ���");
					dee.setVisible(true);
				}
			}// actionPerformed

		}// InputSmokeData Ŭ���� ����

	}// Smoke_Data_Input Ŭ���� ����

	// ùȭ�� ���� ����
	public void prohibitDB() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			System.err.println("JDBC-ODBC ����̹��� ���������� �ε���");
		} catch (ClassNotFoundException e) {
			System.err.println("����̹� �ε忡 �����߽��ϴ�.");
		} // JDBC ����̹� �ε�

		try {
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/smokedata?serverTimezone=UTC",
					"root", "dbsckdrl12");
			System.out.println("DB ���� �Ϸ�.");
			Statement dbSt = con.createStatement();
			System.out.println("JDBC ����̹��� ���������� ����Ǿ����ϴ�.");

			String strSQL;
			strSQL = "SELECT * FROM smoke_data; ";

			ResultSet result = dbSt.executeQuery(strSQL);

			if (result.next()) {
				// DB�� ���������� �о����
				int sy = result.getInt("smoke_year");
				int sm = result.getInt("smoke_month");
				int sd = result.getInt("smoke_day");
				int sq = result.getInt("smoke_quantity");
				int cp = result.getInt("cigarette_price");

				// �о�� sy, sm, sd ������� LocalDate�� ��¥ ����
				LocalDate startDate = LocalDate.of(sy, sm, sd);
				LocalDate endDate = LocalDate.now();

				// Period�� ��ü period�� ��¥ ���� ����
				Period period = startDate.until(endDate);
				int yeart = period.getYears();
				int montht = period.getMonths();
				int dayt = period.getDays();

				int prohibit_period_between = (int) ChronoUnit.DAYS.between(startDate, endDate); // ��¥ ���� ���

				int cdct = (sq * 20) * prohibit_period_between; // �ǿ��� ���� ��� ����
				int rm = cp * prohibit_period_between; // ������ �ݾ�
				int il = ((sq * 20) * prohibit_period_between) * 15; // �þ ����

				int il_year = il / 525600;
				int il_month = il % 525600 / 43200;
				int il_day = il % 525600 % 43200 / 1440;

				smoke_start_day2.setText(result.getString("smoke_year") + "�� " + result.getString("smoke_month") + "�� "
						+ result.getString("smoke_day") + "��");
				prohibit_period2.setText(yeart + "�� " + montht + "���� " + dayt + "��");
				cut_down_cigarret2.setText(mf.format(cdct) + "����");
				reduce_money2.setText(mf.format(rm) + "��");
				increase_life2.setText(il_year + "�� " + il_month + "�� " + il_day + "��");

				smoke_data.setVisible(false);
				smoke_data2.setVisible(true);

			}
			dbSt.close();
			con.close();
		} catch (SQLException e) {
			System.out.println("SQLException : " + e.getMessage());
		}
	}

}// Calendar class ����

class DateSelectErr extends JDialog implements ActionListener {
	JButton OK;

	DateSelectErr(JFrame frame, String title, boolean mode, String msg) {
		super(frame, title, mode);
		JPanel msgp = new JPanel();
		JLabel msgl = new JLabel(msg);
		msgp.add(msgl);
		add(msgp, BorderLayout.CENTER);

		JPanel bp = new JPanel();
		OK = new JButton("Ȯ��");
		OK.addActionListener(this);
		bp.add(OK);
		add(bp, BorderLayout.SOUTH);
		pack();
	}// DateSelectErr

	public void actionPerformed(ActionEvent ae) {
		dispose();
	} // actionPerformed
} // DateSelectErr
