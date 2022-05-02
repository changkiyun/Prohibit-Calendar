package diary;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

class DateOverException extends Exception {
} // ���ᳯ¥�� ���۳�¥���� ū ���

class EmptyException extends Exception {
} // ������ �Է��� ���� ���

class Schedule_insert extends JFrame implements ActionListener {
	String year[] = { "2020", "2021", "2022", "2023" };
	String month[] = { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12" };
	Vector<String> date_s;
	Vector<String> date_e;
	java.util.Calendar cal;
	JTextField schedule_name, schedule_location;
	JComboBox sYear, sMonth, sDate, eYear, eMonth, eDate;
	JButton s_insert, s_cancel, s_e_btn;
	JTextArea memo;
	JScrollPane memoSP;
	JLabel sNo;
	String t_year, t_mon, t_date; // today
	int i_sYear, i_sMon, sMaxDate, i_eYear, i_eMon, eMaxDate; // �޺��ڽ��� ������ ��

	Schedule_insert(String title) {
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
		ct.setLayout(new BorderLayout(0, 20));

		JPanel top = new JPanel();
		top.setLayout(new GridLayout(2, 1));

		JPanel p0 = new JPanel();
		p0.setLayout(new GridLayout(5, 1));

		JPanel p1 = new JPanel();
		p1.setLayout(new FlowLayout(FlowLayout.LEFT));
		schedule_name = new JTextField(17);
		p1.add(new JLabel("���� �̸�    :"));
		p1.add(schedule_name);

		JPanel p2 = new JPanel();
		p2.setLayout(new FlowLayout(FlowLayout.LEFT));
		schedule_location = new JTextField(17);
		p2.add(new JLabel("��ġ            :"));
		p2.add(schedule_location);

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
		top.add(p0);
		top.add(p0_1);
		ct.add(top, BorderLayout.CENTER); // top ��

		JPanel bottom = new JPanel();
		s_insert = new JButton("�߰�");
		s_cancel = new JButton("���");
		s_insert.addActionListener(this);
		s_cancel.addActionListener(this);
		bottom.add(s_insert);
		bottom.add(s_cancel);
		ct.add(bottom, BorderLayout.SOUTH);

	}// schedule ������ ��

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

	public void actionPerformed(ActionEvent ae) {
		String s = ae.getActionCommand();
		String i_start_year, i_start_month, i_start_date, i_end_year, i_end_month, i_end_date;
		String t_name = "", t_location = "", t_content = "";

		if (s.equals("���")) {
			clear();
		} else if (s.equals("���۳�¥�� ����")) {
			eYear.setSelectedItem((String) sYear.getSelectedItem());
			eMonth.setSelectedItem((String) sMonth.getSelectedItem());
			eDate.setSelectedItem((String) sDate.getSelectedItem());
		} else if (s.equals("�߰�")) {
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

				String strSql;
				int i_no = 0;

				t_name = schedule_name.getText();
				t_location = schedule_location.getText();
				i_start_year = (String) sYear.getSelectedItem();
				i_start_month = (String) sMonth.getSelectedItem();
				i_start_date = (String) sDate.getSelectedItem();
				i_end_year = (String) eYear.getSelectedItem();
				i_end_month = (String) eMonth.getSelectedItem();
				i_end_date = (String) eDate.getSelectedItem();
				t_content = memo.getText();
				try {
//���۳�¥ ���ᳯ¥ ��
					java.util.Calendar sCal = java.util.Calendar.getInstance();
					sCal.set(Integer.parseInt(i_start_year), Integer.parseInt(i_start_month),
							Integer.parseInt(i_start_date));
					java.util.Calendar eCal = java.util.Calendar.getInstance();
					eCal.set(Integer.parseInt(i_end_year), Integer.parseInt(i_end_month), Integer.parseInt(i_end_date));

					if (eCal.before(sCal))
						throw new DateOverException();
					else if (t_name.equals(""))
						throw new EmptyException();
					else
						System.out.println("�Է� ����");

					strSql="INSERT INTO schedule (schedule_name, schedule_location, schedule_start_year, schedule_start_month, schedule_start_date, schedule_end_year, schedule_end_month, schedule_end_date, schedule_content) VALUES ('"+t_name+"','"+t_location+"','"+i_start_year+"','"+i_start_month+"','"+i_start_date+"','"+i_end_year+"','"+i_end_month+"','"+i_end_date+"','"+t_content+"');";

					dbSt.executeUpdate(strSql);
					JOptionPane.showMessageDialog(this, "������ �߰��Ͽ����ϴ�.", "schedule", JOptionPane.INFORMATION_MESSAGE);
					clear();
					System.out.println("������ ���� �Ϸ�");
				} catch (EmptyException e) {
					System.out.println("name ���� �Է�");
					JOptionPane.showMessageDialog(this, "�������� �Է����ּ���!", "error", JOptionPane.INFORMATION_MESSAGE);
				} catch (DateOverException e) {
					System.out.println("���ᳯ¥ > ���۳�¥");
					JOptionPane.showMessageDialog(this, "���ᳯ¥�� ���۳�¥���� ���� �� �����ϴ�. ���۳�¥ ���ķ� �������ּ���.", "error",
							JOptionPane.INFORMATION_MESSAGE);
				}

				dbSt.close();
				con.close();
			} catch (SQLException e) {
				System.out.println("SQLException : " + e.getMessage());
			}

		}
	}

}// schedule ��

