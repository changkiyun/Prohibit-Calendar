package diary;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

class DateOverException extends Exception {
} // ���ᳯ¥�� ���۳�¥���� ū ���

class EmptyException extends Exception {
} // ������ �Է��� ���� ���

class UpdateException extends Exception {
} // ������ ������ ������ ���� ���

class DeletException extends Exception {
} // ������ ���� ���� x

class Schedule_search extends JFrame implements ActionListener, ListSelectionListener {
//���� �߰�
	String year[] = { "2020", "2021", "2022", "2023" };
	String month[] = { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12" };
	Vector<String> date_s;
	Vector<String> date_e;
	java.util.Calendar cal;
	JTextField schedule_name, schedule_location, s_search; // ������, ��ġ, �˻�â
	JComboBox sYear, sMonth, sDate, eYear, eMonth, eDate;
	JTextArea memo;
	JScrollPane memoSP;
	String t_year, t_mon, t_date; // today
	int i_sYear, i_sMon, sMaxDate, i_eYear, i_eMon, eMaxDate; // �޺��ڽ��� ������ ��
	JButton s_insert, s_cancel, s_update, s_delete, w_cancel, s_e_btn; // �߰�, ���, ����, ����, �˻������(���), ���۳�¥�� ����
	JList s_list;
	Vector<String> search_rs = new Vector<>(); // �˻� ���
	JLabel sNo; //

// ������
	Schedule_search(String title) {
		cal = java.util.Calendar.getInstance();
		t_year = Integer.toString(cal.get(java.util.Calendar.YEAR));
		t_mon = Integer.toString(cal.get(java.util.Calendar.MONTH) + 1);
		t_date = Integer.toString(cal.get(java.util.Calendar.DATE));
		sMaxDate = cal.getActualMaximum(java.util.Calendar.DAY_OF_MONTH);

		date_s = new Vector<String>();
		for (int i = 1; i <= sMaxDate; i++) {
			String to;
			if (i < 10) {
				to = "0" + Integer.toString(i);
				date_s.add(to);
			} else {
				to = Integer.toString(i);
				date_s.add(to);
			}
		}

		date_e = new Vector<String>();
		for (int i = 1; i <= sMaxDate; i++) {
			String to;
			if (i < 10) {
				to = "0" + Integer.toString(i);
				date_e.add(to);
			} else {
				to = Integer.toString(i);
				date_e.add(to);
			}
		}
		//

		setTitle(title);
		Container ct = getContentPane();
		ct.setLayout(new BorderLayout());

		JPanel top = new JPanel();
		top.setLayout(new FlowLayout());

		s_search = new JTextField(15);
		w_cancel = new JButton("x");
		top.add(s_search);
		top.add(w_cancel);
		w_cancel.addActionListener(this);
		s_search.addActionListener(this);

		JPanel center = new JPanel();
		center.setLayout(new GridLayout(1, 2));

//  <<���� �˻� ��� (left)>>
		JPanel c_left = new JPanel();
		c_left.setPreferredSize(new Dimension(600, 300));

		s_list = new JList(search_rs);
		JScrollPane s_listSP = new JScrollPane(s_list);
		s_list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // ����Ʈ ���� ����
		c_left.add(new JLabel("�˻� ���"));
		c_left.add(s_listSP);
		s_list.setFixedCellWidth(290);
		s_list.setFixedCellHeight(45);
		s_list.addListSelectionListener(this);

//  >>���� �˻� ��� (left) ��<<

// <<���� ��ȭ�� (right)>>
		JPanel c_right = new JPanel();
		c_right.setLayout(new BorderLayout());
		JPanel c_r_center = new JPanel();
		c_r_center.setLayout(new GridLayout(2, 1));

		JPanel p0 = new JPanel();
		p0.setLayout(new GridLayout(5, 1));

		JPanel p1 = new JPanel();
		p1.setLayout(new FlowLayout(FlowLayout.LEFT));
		schedule_name = new JTextField(17);
		p1.add(new JLabel("���� �̸�    :"));
		p1.add(schedule_name); // ���� �̸�

		JPanel p2 = new JPanel();
		p2.setLayout(new FlowLayout(FlowLayout.LEFT));
		schedule_location = new JTextField(17);
		p2.add(new JLabel("��ġ            :"));
		p2.add(schedule_location); // ��ġ

		JPanel p3 = new JPanel();
		p3.setLayout(new FlowLayout(FlowLayout.LEFT));
		sYear = new JComboBox(year);
		sMonth = new JComboBox(month);
		sDate = new JComboBox(date_s);
		sYear.setSelectedItem(t_year);
		sMonth.setSelectedItem(t_mon);
		sDate.setSelectedItem(t_date);
		DateEvent sde = new DateEvent(sYear, sMonth, date_s);
		sYear.addActionListener(sde);
		sMonth.addActionListener(sde);
		p3.add(new JLabel("���۳�¥  "));
		p3.add(sYear);
		p3.add(new JLabel("��"));
		p3.add(sMonth);
		p3.add(new JLabel("��"));
		p3.add(sDate);
		p3.add(new JLabel("��")); // ���۳�¥

		JPanel p4 = new JPanel();
		p4.setLayout(new FlowLayout(FlowLayout.LEFT));
		eYear = new JComboBox(year);
		eMonth = new JComboBox(month);
		eDate = new JComboBox(date_e);
		eYear.setSelectedItem(t_year);
		eMonth.setSelectedItem(t_mon);
		eDate.setSelectedItem(t_date);
		DateEvent ede = new DateEvent(eYear, eMonth, date_e);
		eYear.addActionListener(ede);
		eMonth.addActionListener(ede);
		p4.add(new JLabel("���ᳯ¥  "));
		p4.add(eYear);
		p4.add(new JLabel("��"));
		p4.add(eMonth);
		p4.add(new JLabel("��"));
		p4.add(eDate);
		p4.add(new JLabel("��")); // ���ᳯ¥

		JPanel p5 = new JPanel();
		p5.setLayout(new FlowLayout(FlowLayout.RIGHT));
		s_e_btn = new JButton("���۳�¥�� ����");
		sNo = new JLabel("");
		s_e_btn.addActionListener(this);
		p5.add(s_e_btn);
		p5.add(sNo);
		sNo.setVisible(false); // ���۳�¥�� ����

		JPanel p0_1 = new JPanel();
		p0_1.setLayout(new BorderLayout());

		memo = new JTextArea(5, 15);
		memoSP = new JScrollPane(memo);

		p0.add(p1);
		p0.add(p2);
		p0.add(p3);
		p0.add(p4);
		p0.add(p5);
		p0_1.add(new JLabel("   ������"), BorderLayout.NORTH);
		p0_1.add(memoSP, BorderLayout.CENTER);
		c_r_center.add(p0);
		c_r_center.add(p0_1);
		c_right.add(c_r_center, BorderLayout.CENTER);

// >>���� ��ȭ�� (right)<<

		JPanel c_r_bottom = new JPanel();
		c_r_bottom.setLayout(new FlowLayout(FlowLayout.RIGHT));
		s_insert = new JButton("�߰�");
		s_update = new JButton("����");
		s_delete = new JButton("����");
		s_cancel = new JButton("���");

		s_insert.addActionListener(this);
		s_cancel.addActionListener(this);
		s_update.addActionListener(this);
		s_delete.addActionListener(this);

		c_r_bottom.add(s_insert);
		c_r_bottom.add(s_update);
		c_r_bottom.add(s_delete);
		c_r_bottom.add(s_cancel);
		c_right.add(c_r_bottom, BorderLayout.SOUTH);
		center.add(c_left);
		center.add(c_right);
		ct.add(top, BorderLayout.NORTH);
		ct.add(center, BorderLayout.CENTER);

	}
// ������ ��

	public void clear() {
		sNo.setText("");
		schedule_name.setText("");
		schedule_location.setText("");
		sYear.setSelectedItem(t_year);
		sMonth.setSelectedItem(t_mon);
		sDate.setSelectedItem(t_date);
		eYear.setSelectedItem(t_year);
		eMonth.setSelectedItem(t_mon);
		eDate.setSelectedItem(t_date);
		memo.setText("");

	}

// �̺�Ʈ 
	public void actionPerformed(ActionEvent ae) {
		String s = ae.getActionCommand();
		String i_start_year, i_start_month, i_start_date, i_end_year, i_end_month, i_end_date;
		String t_name = "", t_location = "", t_content = "";
		String strSql, search_name;

		Vector<String> s_vYear = new Vector<>();
		Vector<String> s_vMon = new Vector<>();
		Vector<String> s_vDate = new Vector<>();
		Vector<String> vNames = new Vector<>();
		Vector<String> e_vYear = new Vector<>();
		Vector<String> e_vMon = new Vector<>();
		Vector<String> e_vDate = new Vector<>();
		Vector<Long> s_convDate = new Vector<>();
		Vector<Long> e_convDate = new Vector<>();
		long diffDate;

		// DB����
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			System.err.println("JDBC-ODBC ����̹��� ���������� �ε���");
		} catch (ClassNotFoundException e) {
			System.err.println("����̹� �ε忡 �����߽��ϴ�.");
		}
		try {
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/smokedata?serverTimezone=UTC",
					"root", "dbsckdrl12");
			System.out.println("DB ���� �Ϸ�");
			Statement dbSt = con.createStatement();
			System.out.println("JDBC ����̹��� ���������� ����Ǿ����ϴ�.");

			if (ae.getSource() == s_search) { // s_search ���� �� ��� (�˻�)
				search_name = s_search.getText();

				System.out.println(search_name);

				strSql = "SELECT * FROM schedule WHERE schedule_name LIKE '%" + search_name + "%';";

				System.out.println(strSql);

				search_rs.clear();
				ResultSet r = dbSt.executeQuery(strSql);

				while (r.next()) {

					vNames.add(r.getString("schedule_name"));
					s_vYear.add(r.getString("schedule_start_year"));
					s_vMon.add(r.getString("schedule_start_month"));
					s_vDate.add(r.getString("schedule_start_date"));
					e_vYear.add(r.getString("schedule_end_year"));
					e_vMon.add(r.getString("schedule_end_month"));
					e_vDate.add(r.getString("schedule_end_date"));

					java.util.Calendar startCal = new GregorianCalendar(r.getInt("schedule_start_year"),
							r.getInt("schedule_start_month"), r.getInt("schedule_start_date"));
					java.util.Calendar endCal = new GregorianCalendar(r.getInt("schedule_end_year"),
							r.getInt("schedule_end_month"), r.getInt("schedule_end_date"));

					s_convDate.add(startCal.getTimeInMillis() / 1000);
					s_convDate.add(endCal.getTimeInMillis() / 1000);

				} // while ��

				System.out.println("s_convDate �� ����(�˻��� ������ �� : )" + s_convDate.size());

//  ����
				for (int i = s_convDate.size() - 1; i > 0; i--) {
					for (int j = 0; j < i; j++) {
						if (s_convDate.get(j) > s_convDate.get(j + 1)) {
							Collections.swap(s_convDate, j, j + 1);
							Collections.swap(e_convDate, j, j + 1);
							Collections.swap(vNames, j, j + 1);
							Collections.swap(s_vYear, j, j + 1);
							Collections.swap(s_vMon, j, j + 1);
							Collections.swap(s_vDate, j, j + 1);
							Collections.swap(e_vYear, j, j + 1);
							Collections.swap(e_vMon, j, j + 1);
							Collections.swap(e_vDate, j, j + 1);
						}
					}
				} // ���� ��

// 

				for (int l = 0; l < s_vDate.size(); l++) {
					String str = s_vYear.get(l) + "-" + s_vMon.get(l) + "-" + s_vDate.get(l) + "-" + vNames.get(l);
					search_rs.add(str);
				}

				System.out.println("�˻� ��� ���� ���� : " + search_rs.size());

				s_list.setListData(search_rs);

			} // �˻� �̺�Ʈ ��
			else {
// ��ư �̺�Ʈ
				if (s.equals("���")) {
					clear();
				} else if (s.equals("x")) {
					s_search.setText("");
				} else if (s.equals("���۳�¥�� ����")) {

					eYear.setSelectedItem((String) sYear.getSelectedItem());
					eMonth.setSelectedItem((String) sMonth.getSelectedItem());
					eDate.setSelectedItem((String) sDate.getSelectedItem());
				} else { // ��ư �߰�, ���� ����

					int i_no = 0;
					try {
						if (s.equals("�߰�")) {

							t_name = schedule_name.getText();
							t_location = schedule_location.getText();
							i_start_year = (String) sYear.getSelectedItem();
							i_start_month = (String) sMonth.getSelectedItem();
							i_start_date = (String) sDate.getSelectedItem();
							i_end_year = (String) eYear.getSelectedItem();
							i_end_month = (String) eMonth.getSelectedItem();
							i_end_date = (String) eDate.getSelectedItem();
							t_content = memo.getText();

							java.util.Calendar sCal = java.util.Calendar.getInstance();
							sCal.set(Integer.parseInt(i_start_year), Integer.parseInt(i_start_month),
									Integer.parseInt(i_start_date));
							java.util.Calendar eCal = java.util.Calendar.getInstance();
							eCal.set(Integer.parseInt(i_end_year), Integer.parseInt(i_end_month),
									Integer.parseInt(i_end_date));

							if (eCal.before(sCal))
								throw new DateOverException();
							else if (t_name.equals(""))
								throw new EmptyException();
							else
								System.out.println("�Է� ����");

							strSql = "INSERT INTO schedule (schedule_name, schedule_location, schedule_start_year, schedule_start_month, schedule_start_date, schedule_end_year, schedule_end_month, schedule_end_date, schedule_content) VALUES ('"
									+ t_name + "','" + t_location + "','" + i_start_year + "','" + i_start_month + "','"
									+ i_start_date + "','" + i_end_year + "','" + i_end_month + "','" + i_end_date
									+ "','" + t_content + "');";

							dbSt.executeUpdate(strSql);
							JOptionPane.showMessageDialog(this, "������ �߰��Ͽ����ϴ�.", "schedule",
									JOptionPane.INFORMATION_MESSAGE);
							System.out.println("������ ���� �Ϸ�");

						} // ���� �̺�Ʈ ��
						else if (s.equals("����")) {

							if (sNo.getText().equals(""))
								throw new UpdateException();

							i_no = Integer.parseInt(sNo.getText());
							t_name = schedule_name.getText();
							t_location = schedule_location.getText();
							i_start_year = (String) sYear.getSelectedItem();
							i_start_month = (String) sMonth.getSelectedItem();
							i_start_date = (String) sDate.getSelectedItem();
							i_end_year = (String) eYear.getSelectedItem();
							i_end_month = (String) eMonth.getSelectedItem();
							i_end_date = (String) eDate.getSelectedItem();
							t_content = memo.getText();

							java.util.Calendar sCal = java.util.Calendar.getInstance();
							sCal.set(Integer.parseInt(i_start_year), Integer.parseInt(i_start_month),
									Integer.parseInt(i_start_date));
							java.util.Calendar eCal = java.util.Calendar.getInstance();
							eCal.set(Integer.parseInt(i_end_year), Integer.parseInt(i_end_month),
									Integer.parseInt(i_end_date));

							if (eCal.before(sCal))
								throw new DateOverException();
							else if (t_name.equals(""))
								throw new EmptyException();
							else
								System.out.println("�Է� ����");

							System.out.println("i_no : " + i_no);

							strSql = "UPDATE schedule SET schedule_name='" + t_name + "',schedule_location='"
									+ t_location + "',schedule_start_year='" + i_start_year + "',schedule_start_month='"
									+ i_start_month + "',schedule_start_date='" + i_start_date + "',schedule_end_year='"
									+ i_end_year + "',schedule_end_month='" + i_end_month + "',schedule_end_date='"
									+ i_end_date + "',schedule_content='" + t_content + "' WHERE schedule_no=" + i_no
									+ ";";
							System.out.println(strSql);
							dbSt.executeUpdate(strSql);
							JOptionPane.showMessageDialog(this, "������ �����Ͽ����ϴ�.", "���� ����",
									JOptionPane.INFORMATION_MESSAGE);
							System.out.println("������ ���� �Ϸ�");
						} else if (s.equals("����")) {

							if (sNo.getText().equals(""))
								throw new DeletException();

							i_no = Integer.parseInt(sNo.getText());

							strSql = "DELETE FROM schedule WHERE schedule_no=" + i_no + ";";

							dbSt.executeUpdate(strSql);
							JOptionPane.showMessageDialog(this, "������ �����Ͽ����ϴ�.", "schedule",
									JOptionPane.INFORMATION_MESSAGE);

							clear();

						} else {
						}

//////////////////////////////////////////////////////////////////////////
						search_name = s_search.getText();
						search_rs.clear();

						strSql = "SELECT * FROM schedule WHERE schedule_name LIKE '%" + search_name + "%';";
						ResultSet r = dbSt.executeQuery(strSql);

						while (r.next()) {

							vNames.add(r.getString("schedule_name"));
							s_vYear.add(r.getString("schedule_start_year"));
							s_vMon.add(r.getString("schedule_start_month"));
							s_vDate.add(r.getString("schedule_start_date"));
							e_vYear.add(r.getString("schedule_end_year"));
							e_vMon.add(r.getString("schedule_end_month"));
							e_vDate.add(r.getString("schedule_end_date"));

							java.util.Calendar startCal = new GregorianCalendar(r.getInt("schedule_start_year"),
									r.getInt("schedule_start_month"), r.getInt("schedule_start_date"));
							java.util.Calendar endCal = new GregorianCalendar(r.getInt("schedule_end_year"),
									r.getInt("schedule_end_month"), r.getInt("schedule_end_date"));

							s_convDate.add(startCal.getTimeInMillis() / 1000);
							e_convDate.add(endCal.getTimeInMillis() / 1000);

						} // while ��

//  ����
						for (int i = s_convDate.size() - 1; i > 0; i--) {
							for (int j = 0; j < i; j++) {
								if (s_convDate.get(j) > s_convDate.get(j + 1)) {
									Collections.swap(s_convDate, j, j + 1);
									Collections.swap(e_convDate, j, j + 1);
									Collections.swap(vNames, j, j + 1);
									Collections.swap(s_vYear, j, j + 1);
									Collections.swap(s_vMon, j, j + 1);
									Collections.swap(s_vDate, j, j + 1);
									Collections.swap(e_vYear, j, j + 1);
									Collections.swap(e_vMon, j, j + 1);
									Collections.swap(e_vDate, j, j + 1);
								}
							}
						} // ���� ��

//

						for (int l = 0; l < s_vDate.size(); l++) {
							String str = s_vYear.get(l) + "-" + s_vMon.get(l) + "-" + s_vDate.get(l) + "-"
									+ vNames.get(l);
							search_rs.add(str);
						}
						s_list.setListData(search_rs);
////////////////////////////////////////////////////////////////////////////////////////////

					} catch (EmptyException e) {
						System.out.println("name ���� �Է�");
						JOptionPane.showMessageDialog(this, "�������� �Է����ּ���!", "error", JOptionPane.INFORMATION_MESSAGE);
					} catch (DateOverException e) {
						System.out.println("���ᳯ¥ > ���۳�¥");
						JOptionPane.showMessageDialog(this, "���ᳯ¥�� ���۳�¥���� ���� �� �����ϴ�. ���۳�¥ ���ķ� �������ּ���.", "error",
								JOptionPane.INFORMATION_MESSAGE);
					} catch (UpdateException e) {
						System.out.println("������ ���� ����X");
						JOptionPane.showMessageDialog(this, "������ ������ �������ּ���!", "error",
								JOptionPane.INFORMATION_MESSAGE);
					} catch (DeletException e) {
						System.out.println("������ ���� ����X");
						JOptionPane.showMessageDialog(this, "������ ������ �������ּ���!", "error",
								JOptionPane.INFORMATION_MESSAGE);
					}

				} // ��ư �̺�Ʈ ��

			} // �׼��̺�Ʈ ��

			dbSt.close();
			con.close();
		} catch (SQLException e) {
			System.out.println("SQLException : " + e.getMessage());
		}
	} // �׼Ǹ����� ��

	public void valueChanged(ListSelectionEvent se) { // ����Ʈ Ŭ�� �̺�Ʈ
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			System.err.println("JDBC-ODBC ����̹��� ���������� �ε���");
		} catch (ClassNotFoundException e) {
			System.err.println("����̹� �ε忡 �����߽��ϴ�.");
		}
		try {
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/smokedata?serverTimezone=UTC",
					"root", "dbsckdrl12");
			System.out.println("DB ���� �Ϸ�");
			Statement dbSt = con.createStatement();
			System.out.println("JDBC ����̹��� ���������� ����Ǿ����ϴ�.");

			String s, strSql;

			s = search_rs.get(s_list.getSelectedIndex());
			System.out.println(s);
			String arr[] = s.split("-"); // 0=�� 1=�� 2=�� 3=������

			strSql = "SELECT * FROM schedule WHERE schedule_name='" + arr[3] + "' and schedule_start_year=" + arr[0]
					+ " and schedule_start_month=" + arr[1] + " and schedule_start_date=" + arr[2] + ";";

			ResultSet r = dbSt.executeQuery(strSql);
			while (r.next()) {
				sNo.setText(r.getString("schedule_no"));
				schedule_name.setText(r.getString("schedule_name"));
				schedule_location.setText(r.getString("schedule_location"));
				sYear.setSelectedItem(r.getString("schedule_start_year"));
				sMonth.setSelectedItem(r.getString("schedule_start_month"));
				sDate.setSelectedItem(r.getString("schedule_start_date"));
				eYear.setSelectedItem(r.getString("schedule_end_year"));
				eMonth.setSelectedItem(r.getString("schedule_end_month"));
				eDate.setSelectedItem(r.getString("schedule_end_date"));
				memo.setText(r.getString("schedule_content"));
			}

			dbSt.close();
			con.close();
		} catch (SQLException e) {
			System.out.println("SQLException : " + e.getMessage());
		}
	}// ����Ʈ �̺�Ʈ ��

}// Ŭ���� ��

class Schedule_s1_test3 {
	public static void main(String args[]) {
		Schedule_search win = new Schedule_search("���� �߰�");
		win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		win.setSize(650, 500);
		win.setLocation(100, 200);
		win.show();
	}
}
