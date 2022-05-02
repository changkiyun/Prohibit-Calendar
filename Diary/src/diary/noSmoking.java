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

		diary = new JLabel("금연 다이어리");
		diary.setBounds(200, 60, 130, 30);
		pwl = new JLabel("PASSWORD            :");
		pwl.setBounds(120, 130, 130, 30);
		pwf = new JPasswordField(4);
		pwf.setBounds(250, 130, 130, 30);
		loginb = new JButton("로그인");
		loginb.setBounds(90, 220, 100, 30);
		findb = new JButton("PW찾기");
		findb.setBounds(200, 220, 100, 30);
		reb = new JButton("PW등록");
		reb.setBounds(310, 220, 100, 30);

		loginb.addActionListener(this); // 로그인 버튼에 이벤트 연결
		findb.addActionListener(this); // PW찾기 버튼에 이벤트 연결
		reb.addActionListener(this); // PW등록 버튼에 이벤트 연결

		ct.add(diary);
		ct.add(pwl);
		ct.add(pwf);
		ct.add(loginb);
		ct.add(findb);
		ct.add(reb);
	} // 생성자 끝

	public void actionPerformed(ActionEvent ae) {
		String s = ae.getActionCommand();
		if (s == "PW찾기") {
			pwFind pf = new pwFind("비밀번호 찾기");
			pf.setSize(500, 400);
			pf.setLocation(250, 250);
			pf.show();
			pf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		} else if (s == "PW등록") {
			pwRegister pr = new pwRegister("비밀번호 등록");
			pr.setSize(500, 400);
			pr.setLocation(250, 250);
			pr.show();
			pr.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		} else { // 로그인 버튼 클릭시
			try { // mysql jdbc Driver 연결
				Class.forName("com.mysql.cj.jdbc.Driver");
				System.err.println("JDBC-ODBC 드라이버를 정상적으로 로드함");
			} catch (ClassNotFoundException e) {
				System.err.println("드라이버 로드에 실패했습니다.");
			} // try-catch문 종료

			try {
				Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/smokedata?serverTimezone=UTC",
						"root", "dbsckdrl12");
				System.out.println("DB 연결 완료.");
				Statement dbPs = con.createStatement();
				System.out.println("JDBC 드라이버가 정상적으로 연결되었습니다.");

				String strSql;
				String t_password;
				t_password = pwf.getText();
				strSql = "SELECT*FROM person_info WHERE pwf = '" + t_password + "';";

				ResultSet result = dbPs.executeQuery(strSql);

				if (result.next()) {
					dispose();
					new Calendar();
				} else {
					MessageDialog md = new MessageDialog(this, "로그인 확인", true, "password가 틀렸습니다");
					md.setSize(250, 150);
					md.setLocation(350, 350);
					md.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
					md.show();
				} // if-else문 종료
				dbPs.close();
				con.close();
			} catch (SQLException e) {
				System.out.println("SQLException: " + e.getMessage());
			} // try-catch문 종료
		} // 로그인 else문 종료
	} // actionPerformend 메소드 끝

	public static void main(String args[]) {
		noSmoking win = new noSmoking("다이어리 비밀번호 입력");
		win.setSize(500, 400);
		win.setLocation(200, 200);
		win.setVisible(true);
		win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
} // noSmoking 클래스 끝

class pwFind extends JFrame implements ActionListener {
	JTextField namef, pwranswer;
	JLabel name, pwhintl, pw, ranswer;
	JComboBox pwhintc;
	String pwhint[] = { "어릴 적 살던 동네 이름?", "나의 보물 1호?", "가장 좋아했던 선생님 이름?", "내가 가장 존경하는 인물?", "우리집 애완동물 이름?" };

	pwFind(String title) {
		setTitle(title);
		Container ct = getContentPane();
		ct.setLayout(null);

		name = new JLabel("이름            :");
		name.setBounds(100, 80, 150, 30);
		namef = new JTextField(7);
		namef.setBounds(250, 80, 150, 30);
		pwhintl = new JLabel("PW 힌트                         :");
		pwhintl.setBounds(100, 120, 150, 30);
		pwhintc = new JComboBox(pwhint);
		pwhintc.setBounds(250, 120, 150, 30);
		ranswer = new JLabel("PW 힌트 정답                :");
		ranswer.setBounds(100, 160, 150, 30);
		pwranswer = new JTextField(12);
		pwranswer.setBounds(250, 160, 150, 30);
		JButton b1 = new JButton("확인");
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
	} // 생성자 끝

	public void actionPerformed(ActionEvent ae) {
		String s = ae.getActionCommand();
		String t_name = "", t_hint = "", t_answer = ""; // 화면에 입력된 값 얻어오는 변수
		try { // mysql jdbc Driver 연결
			Class.forName("com.mysql.cj.jdbc.Driver");
			System.err.println("JDBC 드라이버를 정상적으로 로드함");
		} catch (ClassNotFoundException e) {
			System.err.println("드라이버 로드에 실패했습니다.");
		} // try-catch문 종료

		try {
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/smokedata?serverTimezone=UTC",
					"root", "dbsckdrl12");
			System.out.println("DB 연결 완료.");
			Statement dbPs = con.createStatement();
			System.out.println("JDBC 드라이버가 정상적으로 연결되었습니다.");

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

			System.out.println("검색 완료");
			dbPs.close();
			con.close();
		} catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
		} // try-catch문 종료
	} // actionPerformend 메소드 끝
} // pwFind 클래스 끝

class pwRegister extends JFrame implements ActionListener {

	JTextField namef, pwranswer;
	JPasswordField pwf, pwagainf;
	JComboBox pwhintc;
	String pwhint[] = { "어릴 적 살던 동네 이름?", "나의 보물 1호?", "가장 좋아했던 선생님 이름?", "내가 가장 존경하는 인물?", "우리집 애완동물 이름?" };

	pwRegister(String title) {
		setTitle(title);
		Container ct = getContentPane();
		ct.setLayout(null);

		JLabel name = new JLabel("이름            :");
		name.setBounds(100, 50, 150, 30);
		namef = new JTextField(7);
		namef.setBounds(250, 50, 150, 30);
		JLabel pw = new JLabel("PASSWORD(4자리)      :");
		pw.setBounds(100, 90, 150, 30);
		pwf = new JPasswordField(4);
		pwf.setBounds(250, 90, 150, 30);
		JLabel pwagain = new JLabel("PW 재확인                     :");
		pwagain.setBounds(100, 130, 150, 30);
		pwagainf = new JPasswordField(4);
		pwagainf.setBounds(250, 130, 150, 30);
		JLabel pwhintl = new JLabel("PW 힌트                         :");
		pwhintl.setBounds(100, 170, 150, 30);
		pwhintc = new JComboBox(pwhint);
		pwhintc.setBounds(250, 170, 150, 30);
		JLabel ranswer = new JLabel("PW 힌트 정답                :");
		ranswer.setBounds(100, 210, 150, 30);
		pwranswer = new JTextField(12);
		pwranswer.setBounds(250, 210, 150, 30);
		JButton b1 = new JButton("확인");
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
	} // 생성자 끝

	public void actionPerformed(ActionEvent ae) {
		String s = ae.getActionCommand();
		String t_name = "", t_pw = "", t_pwagain = "", t_hint = "", t_answer = "";

		if (s.equals("확인")) {
			try { // mysql jdbc Driver 연결
				Class.forName("com.mysql.cj.jdbc.Driver");
				System.err.println("JDBC 드라이버를 정상적으로 로드함");
			} catch (ClassNotFoundException e) {
				System.err.println("드라이버 로드에 실패했습니다.");
			} // try-catch문 종료

			try {
				Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/smokedata?serverTimezone=UTC",
						"root", "dbsckdrl12");
				System.out.println("DB 연결 완료.");
				Statement dbPs = con.createStatement();
				System.out.println("JDBC 드라이버가 정상적으로 연결되었습니다.");

				t_name = namef.getText();
				t_pw = pwf.getText();
				t_pwagain = pwagainf.getText();
				t_hint = (String) pwhintc.getSelectedItem();
				t_answer = pwranswer.getText(); // 사용자가 입력한 값 가져오기 끝

				String strSql = "INSERT INTO person_info(namef, pwf, pwagainf, pwhintc, pwranswer) VALUES (" + "'"
						+ t_name + "','" + t_pw + "','" + t_pwagain + "','" + t_hint + "','" + t_answer + "');";
				dbPs.executeUpdate(strSql);
				System.out.println("데이터 삽입 완료");

				dbPs.close();
				con.close();
			} catch (SQLException e) {
				System.out.println("SQLException: " + e.getMessage());
			} // try-catch문 종료
			dispose();
		} // if 문 종료
	} // actionPerformend 메소드 끝
} // pwRegister 클래스 끝

class MessageDialog extends JDialog implements ActionListener {
	JButton b1;

	MessageDialog(JFrame parent, String title, boolean mode, String msg) {
		setTitle(title);
		Container ct = getContentPane();
		ct.setLayout(null);

		JLabel label = new JLabel(msg);
		label.setBounds(30, 10, 200, 30);
		b1 = new JButton("확인");
		b1.setBounds(90, 50, 60, 30);

		b1.addActionListener(this);

		ct.add(label);
		ct.add(b1);
	} // 생성자 끝

	public void actionPerformed(ActionEvent ae) {
		dispose();
	} // actionPerformed 클래스 끝
} // MessageDialog 클래스 끝