package diary;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class noSmoking extends JFrame implements ActionListener {

	JLabel diary, pwl;
	JPasswordField pwf;
	JButton loginb, findb, reb;

	noSmoking(String title) {
		setTitle(title);
		Container ct = getContentPane();
		ct.setLayout(null);

		diary = new JLabel("�ݿ� ���̾");
		diary.setBounds(200, 60, 130, 30);
		pwl = new JLabel("PASSWORD            :");
		pwl.setBounds(120, 130, 130, 30);
		pwf = new JPasswordField(4);
		pwf.setBounds(250, 130, 130, 30);
		loginb = new JButton("�α���");
		loginb.setBounds(90, 220, 100, 30);
		findb = new JButton("PWã��");
		findb.setBounds(200, 220, 100, 30);
		reb = new JButton("PW���");
		reb.setBounds(310, 220, 100, 30);

		loginb.addActionListener(this); // �α��� ��ư�� �̺�Ʈ ����
		findb.addActionListener(this); // PWã�� ��ư�� �̺�Ʈ ����
		reb.addActionListener(this); // PW��� ��ư�� �̺�Ʈ ����

		ct.add(diary);
		ct.add(pwl);
		ct.add(pwf);
		ct.add(loginb);
		ct.add(findb);
		ct.add(reb);
	} // ������ ��

	public void actionPerformed(ActionEvent ae) {
		String s = ae.getActionCommand();
		if (s == "PWã��") {
			pwFind pf = new pwFind("��й�ȣ ã��");
			pf.setSize(500, 400);
			pf.setLocation(250, 250);
			pf.show();
			pf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		} else if (s == "PW���") {
			pwRegister pr = new pwRegister("��й�ȣ ���");
			pr.setSize(500, 400);
			pr.setLocation(250, 250);
			pr.show();
			pr.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		} else { // �α��� ��ư Ŭ����
			try { // mysql jdbc Driver ����
				Class.forName("com.mysql.cj.jdbc.Driver");
				System.err.println("JDBC-ODBC ����̹��� ���������� �ε���");
			} catch (ClassNotFoundException e) {
				System.err.println("����̹� �ε忡 �����߽��ϴ�.");
			} // try-catch�� ����

			try {
				Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/smokedata?serverTimezone=UTC",
						"root", "dbsckdrl12");
				System.out.println("DB ���� �Ϸ�.");
				Statement dbPs = con.createStatement();
				System.out.println("JDBC ����̹��� ���������� ����Ǿ����ϴ�.");

				String strSql;
				String t_password;
				t_password = pwf.getText();
				strSql = "SELECT*FROM person_info WHERE pwf = '" + t_password + "';";

				ResultSet result = dbPs.executeQuery(strSql);

				if (result.next()) {
					dispose();
					new Calendar();
				} else {
					MessageDialog md = new MessageDialog(this, "�α��� Ȯ��", true, "password�� Ʋ�Ƚ��ϴ�");
					md.setSize(250, 150);
					md.setLocation(350, 350);
					md.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
					md.show();
				} // if-else�� ����
				dbPs.close();
				con.close();
			} catch (SQLException e) {
				System.out.println("SQLException: " + e.getMessage());
			} // try-catch�� ����
		} // �α��� else�� ����
	} // actionPerformend �޼ҵ� ��

	public static void main(String args[]) {
		noSmoking win = new noSmoking("���̾ ��й�ȣ �Է�");
		win.setSize(500, 400);
		win.setLocation(200, 200);
		win.setVisible(true);
		win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
} // noSmoking Ŭ���� ��

class pwFind extends JFrame implements ActionListener {
	JTextField namef, pwranswer;
	JLabel name, pwhintl, pw, ranswer;
	JComboBox pwhintc;
	String pwhint[] = { "� �� ��� ���� �̸�?", "���� ���� 1ȣ?", "���� �����ߴ� ������ �̸�?", "���� ���� �����ϴ� �ι�?", "�츮�� �ֿϵ��� �̸�?" };

	pwFind(String title) {
		setTitle(title);
		Container ct = getContentPane();
		ct.setLayout(null);

		name = new JLabel("�̸�            :");
		name.setBounds(100, 80, 150, 30);
		namef = new JTextField(7);
		namef.setBounds(250, 80, 150, 30);
		pwhintl = new JLabel("PW ��Ʈ                         :");
		pwhintl.setBounds(100, 120, 150, 30);
		pwhintc = new JComboBox(pwhint);
		pwhintc.setBounds(250, 120, 150, 30);
		ranswer = new JLabel("PW ��Ʈ ����                :");
		ranswer.setBounds(100, 160, 150, 30);
		pwranswer = new JTextField(12);
		pwranswer.setBounds(250, 160, 150, 30);
		JButton b1 = new JButton("Ȯ��");
		b1.setBounds(215, 210, 70, 30);
		pw = new JLabel();
		pw.setBounds(230, 260, 100, 30);

		b1.addActionListener(this);

		ct.add(name);
		ct.add(namef);
		ct.add(pwhintl);
		ct.add(pwhintc);
		ct.add(ranswer);
		ct.add(pwranswer);
		ct.add(b1);
		ct.add(pw);
	} // ������ ��

	public void actionPerformed(ActionEvent ae) {
		String s = ae.getActionCommand();
		String t_name = "", t_hint = "", t_answer = ""; // ȭ�鿡 �Էµ� �� ������ ����
		try { // mysql jdbc Driver ����
			Class.forName("com.mysql.cj.jdbc.Driver");
			System.err.println("JDBC ����̹��� ���������� �ε���");
		} catch (ClassNotFoundException e) {
			System.err.println("����̹� �ε忡 �����߽��ϴ�.");
		} // try-catch�� ����

		try {
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/smokedata?serverTimezone=UTC",
					"root", "dbsckdrl12");
			System.out.println("DB ���� �Ϸ�.");
			Statement dbPs = con.createStatement();
			System.out.println("JDBC ����̹��� ���������� ����Ǿ����ϴ�.");

			String strSql;
			t_name = namef.getText();
			t_hint = (String) pwhintc.getSelectedItem();
			t_answer = pwranswer.getText();

			strSql = "SELECT pwf FROM person_info WHERE namef = '" + t_name + "' and pwhintc = '" + t_hint
					+ "' and pwranswer = '" + t_answer + "';";
			ResultSet result = dbPs.executeQuery(strSql);

			while (result.next()) {
				pw.setText(result.getString("pwf"));
			} // while

			System.out.println("�˻� �Ϸ�");
			dbPs.close();
			con.close();
		} catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
		} // try-catch�� ����
	} // actionPerformend �޼ҵ� ��
} // pwFind Ŭ���� ��

class pwRegister extends JFrame implements ActionListener {

	JTextField namef, pwranswer;
	JPasswordField pwf, pwagainf;
	JComboBox pwhintc;
	String pwhint[] = { "� �� ��� ���� �̸�?", "���� ���� 1ȣ?", "���� �����ߴ� ������ �̸�?", "���� ���� �����ϴ� �ι�?", "�츮�� �ֿϵ��� �̸�?" };

	pwRegister(String title) {
		setTitle(title);
		Container ct = getContentPane();
		ct.setLayout(null);

		JLabel name = new JLabel("�̸�            :");
		name.setBounds(100, 50, 150, 30);
		namef = new JTextField(7);
		namef.setBounds(250, 50, 150, 30);
		JLabel pw = new JLabel("PASSWORD(4�ڸ�)      :");
		pw.setBounds(100, 90, 150, 30);
		pwf = new JPasswordField(4);
		pwf.setBounds(250, 90, 150, 30);
		JLabel pwagain = new JLabel("PW ��Ȯ��                     :");
		pwagain.setBounds(100, 130, 150, 30);
		pwagainf = new JPasswordField(4);
		pwagainf.setBounds(250, 130, 150, 30);
		JLabel pwhintl = new JLabel("PW ��Ʈ                         :");
		pwhintl.setBounds(100, 170, 150, 30);
		pwhintc = new JComboBox(pwhint);
		pwhintc.setBounds(250, 170, 150, 30);
		JLabel ranswer = new JLabel("PW ��Ʈ ����                :");
		ranswer.setBounds(100, 210, 150, 30);
		pwranswer = new JTextField(12);
		pwranswer.setBounds(250, 210, 150, 30);
		JButton b1 = new JButton("Ȯ��");
		b1.setBounds(215, 275, 70, 30);

		b1.addActionListener(this);

		ct.add(name);
		ct.add(namef);
		ct.add(pw);
		ct.add(pwf);
		ct.add(pwagain);
		ct.add(pwagainf);
		ct.add(pwhintl);
		ct.add(pwhintc);
		ct.add(ranswer);
		ct.add(pwranswer);
		ct.add(b1);
	} // ������ ��

	public void actionPerformed(ActionEvent ae) {
		String s = ae.getActionCommand();
		String t_name = "", t_pw = "", t_pwagain = "", t_hint = "", t_answer = "";

		if (s.equals("Ȯ��")) {
			try { // mysql jdbc Driver ����
				Class.forName("com.mysql.cj.jdbc.Driver");
				System.err.println("JDBC ����̹��� ���������� �ε���");
			} catch (ClassNotFoundException e) {
				System.err.println("����̹� �ε忡 �����߽��ϴ�.");
			} // try-catch�� ����

			try {
				Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/smokedata?serverTimezone=UTC",
						"root", "dbsckdrl12");
				System.out.println("DB ���� �Ϸ�.");
				Statement dbPs = con.createStatement();
				System.out.println("JDBC ����̹��� ���������� ����Ǿ����ϴ�.");

				t_name = namef.getText();
				t_pw = pwf.getText();
				t_pwagain = pwagainf.getText();
				t_hint = (String) pwhintc.getSelectedItem();
				t_answer = pwranswer.getText(); // ����ڰ� �Է��� �� �������� ��

				String strSql = "INSERT INTO person_info(namef, pwf, pwagainf, pwhintc, pwranswer) VALUES (" + "'"
						+ t_name + "','" + t_pw + "','" + t_pwagain + "','" + t_hint + "','" + t_answer + "');";
				dbPs.executeUpdate(strSql);
				System.out.println("������ ���� �Ϸ�");

				dbPs.close();
				con.close();
			} catch (SQLException e) {
				System.out.println("SQLException: " + e.getMessage());
			} // try-catch�� ����
			dispose();
		} // if �� ����
	} // actionPerformend �޼ҵ� ��
} // pwRegister Ŭ���� ��

class MessageDialog extends JDialog implements ActionListener {
	JButton b1;

	MessageDialog(JFrame parent, String title, boolean mode, String msg) {
		setTitle(title);
		Container ct = getContentPane();
		ct.setLayout(null);

		JLabel label = new JLabel(msg);
		label.setBounds(30, 10, 200, 30);
		b1 = new JButton("Ȯ��");
		b1.setBounds(90, 50, 60, 30);

		b1.addActionListener(this);

		ct.add(label);
		ct.add(b1);
	} // ������ ��

	public void actionPerformed(ActionEvent ae) {
		dispose();
	} // actionPerformed Ŭ���� ��
} // MessageDialog Ŭ���� ��