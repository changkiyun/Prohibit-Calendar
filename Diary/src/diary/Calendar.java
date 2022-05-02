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

	// 금연 타이머 변수
	DecimalFormat mf = new DecimalFormat("###,###,###");// 금액 출력 포멧
	JLabel l1 = new JLabel("금연 시작일");
	JLabel l2 = new JLabel("금연 기간");
	JLabel l3 = new JLabel("피우지 않은 담배");
	JLabel l4 = new JLabel("절약한 금액");
	JLabel l5 = new JLabel("늘어난 수명");// 정보 이름 레이블
	JCheckBox set_today; // 오늘날짜로 바꾸기 체크박스
	ImageIcon line = new ImageIcon("images/Line.png");
	JLabel[] line_label = new JLabel[20];// 정보 구분선 레이블
	JPanel smoke_data, smoke_data2; // 흡연 정보 입력 후 교체되는 패널
	JButton prohibit_start_button; // 초기화면 금연 시작버튼
	JPanel prohibit_info, smoke_info; // 콤보박스 선택 시 변경되는 패널
	JComboBox select_data;
	JLabel smoke_start_day, prohibit_period, cut_down_cigarret, reduce_money, increase_life; // 팝업 정보출력 레이블
	JLabel smoke_start_day2, prohibit_period2, cut_down_cigarret2, reduce_money2, increase_life2; // 메인화면 정보출력 레이블
	DataInput SDI = new DataInput(); // 데이터 입력창 종료
	Font font = new Font("맑은 고딕", Font.BOLD, 20);
	Font font2 = new Font("맑은 고딕", Font.PLAIN, 20);// 폰트
	// 금연 타이머 변수

	// Calendar 메소드
	public Calendar() {
		setTitle("금연 다이어리");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);

		SDI.setVisible(false);

		c = getContentPane();
		c.setLayout(null);
		c.setBackground(Color.WHITE);
		prohibitTimerBeforeData(); // 정보 입력 전 화면 메소드
		smokeTimerAfterData(); // 정보 입력 후 화면 메소드
		prohibitDB(); // Calendar화면 생성시 데이터 유무 확인 후 정보 출력 메소드

		// Todo리스트
		JPanel todo_list = new JPanel();
		todo_list.setBounds(0, 360, 355, 355);
		todo_list.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		todo_list.add(tl);

		// 메인 캘린더
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
	}// 생성자 종료

	// 정보 입력 전 화면 메소드
	public void prohibitTimerBeforeData() {
		// 흡연 데이터 출력 패널
		smoke_data = new JPanel();
		smoke_data.setBounds(0, 0, 355, 355);
		smoke_data.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		smoke_data.setLayout(null);

		// 초기화면 텍스트
		JLabel smoke_text = new JLabel(
				"<html><body style='text-align:center;'>사용자의 흡연정보를<br /> 입력하고 금연을 시작하세요.</body></html>");
		smoke_text.setFont(font);
		smoke_text.setBounds(40, 95, 300, 100);

		// 초기화면 금연시작버튼
		prohibit_start_button = new JButton("금연 시작");
		prohibit_start_button.setFont(font);
		prohibit_start_button.setBounds(100, 220, 150, 40);
		prohibit_start_button.addActionListener(new prohibitTimerBeforeDataListener());

		// 패널, 컨테이너에 추가
		smoke_data.add(smoke_text);
		smoke_data.add(prohibit_start_button);
		c.add(smoke_data);
	} // prohibitTimerBeforeData

	// 금연 시작 버튼 클릭시 정보 입력 팝업창 생성
	class prohibitTimerBeforeDataListener implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			if (ae.getActionCommand() == "금연 시작") {
				SDI.setTitle("흡연 정보 입력");
				SDI.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				SDI.setSize(350, 450);
				SDI.setVisible(true);

			}
		}
	} // prohibitTimerBeforeDataListener

	// 정보 입력 후 화면 메소드
	public void smokeTimerAfterData() {
		smoke_data2 = new JPanel();
		smoke_data2.setBounds(0, 0, 355, 355);
		smoke_data2.setLayout(null);
		smoke_data2.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		smoke_data2.setVisible(false);// 오류방지 setVisible(false)

		// 데이터 선택 콤보박스, 실패버튼 (BorderLayout NORTH)
		JPanel top = new JPanel();
		top.setLayout(null);
		top.setBounds(3, 3, 345, 50);

		String[] combobox_smoke_data = { "금연데이터", "흡연데이터" };
		select_data = new JComboBox(combobox_smoke_data);
		select_data.addActionListener(new smokeTimerAfterDataListener());

		JButton fail_button = new JButton("실패");
		fail_button.addActionListener(new ProhibitFailListener());

		select_data.setBounds(120, 10, 100, 30);
		fail_button.setBounds(250, 10, 80, 30);

		// 금연, 흡연 정보 레이블 공유해서 한 패널에서 출력
		prohibit_info = new JPanel();
		prohibit_info.setLayout(null);
		prohibit_info.setBounds(5, 45, 345, 305);

		// 흡연 정보 출력 레이블
		smoke_start_day2 = new JLabel();
		prohibit_period2 = new JLabel();
		cut_down_cigarret2 = new JLabel();
		reduce_money2 = new JLabel();
		increase_life2 = new JLabel();

		// 레이블 폰트 설정
		smoke_start_day2.setFont(font2);
		prohibit_period2.setFont(font2);
		cut_down_cigarret2.setFont(font2);
		reduce_money2.setFont(font2);
		increase_life2.setFont(font2);

		// 흡연 정보 출력 패널에 레이블 추가
		prohibit_info.add(smoke_start_day2);
		prohibit_info.add(prohibit_period2);
		prohibit_info.add(cut_down_cigarret2);
		prohibit_info.add(reduce_money2);
		prohibit_info.add(increase_life2);

		// 정보 이름 출력
		prohibit_info.add(l1);
		prohibit_info.add(l2);
		prohibit_info.add(l3);
		prohibit_info.add(l4);
		prohibit_info.add(l5);

		for (int i = 0; i <= 5; i++) {
			line_label[i] = new JLabel(line);
			prohibit_info.add(line_label[i]);
		}

		// 정보출력 레이블 위치지정
		smoke_start_day2.setBounds(100, 15, 300, 30);
		prohibit_period2.setBounds(100, 75, 300, 30);
		cut_down_cigarret2.setBounds(100, 135, 300, 30);
		reduce_money2.setBounds(100, 195, 300, 30);
		increase_life2.setBounds(100, 255, 300, 30);

		// 정보 이름 레이블
		l1.setBounds(20, 0, 300, 30);
		l2.setBounds(20, 60, 300, 30);
		l3.setBounds(20, 120, 300, 30);
		l4.setBounds(20, 180, 300, 30);
		l5.setBounds(20, 240, 300, 30);

		// 구분선
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

	// // 정보 입력 후 화면 리스너
	class smokeTimerAfterDataListener implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			int selected = select_data.getSelectedIndex();

			// 금연정보 선택시 데이터
			if (selected == 0) {
				try {
					Class.forName("com.mysql.cj.jdbc.Driver");
					System.err.println("JDBC-ODBC 드라이버를 정상적으로 로드함");
				} catch (ClassNotFoundException e) {
					System.err.println("드라이버 로드에 실패했습니다.");
				} // JDBC 드라이버 로드

				try {
					Connection con = DriverManager.getConnection(
							"jdbc:mysql://localhost:3306/smokedata?serverTimezone=UTC", "root", "dbsckdrl12");
					System.out.println("DB 연결 완료.");
					Statement dbSt = con.createStatement();
					System.out.println("JDBC 드라이버가 정상적으로 연결되었습니다.");

					String strSQL;
					strSQL = "SELECT * FROM smoke_data; ";

					ResultSet result = dbSt.executeQuery(strSQL);

					if (result.next()) {
						// DB값 정수형으로 읽어오기
						int sy = result.getInt("smoke_year");
						int sm = result.getInt("smoke_month");
						int sd = result.getInt("smoke_day");
						int sq = result.getInt("smoke_quantity");
						int cp = result.getInt("cigarette_price");

						// 읽어온 sy, sm, sd 기반으로 LocalDate형 날짜 생성
						LocalDate startDate = LocalDate.of(sy, sm, sd);
						LocalDate endDate = LocalDate.now();

						// Period형 객체 period에 날짜 차이 저장
						Period period = startDate.until(endDate);
						int yeart = period.getYears();
						int montht = period.getMonths();
						int dayt = period.getDays();

						int prohibit_period_between = (int) ChronoUnit.DAYS.between(startDate, endDate); // 날짜 차이 계산

						int cdct = (sq * 20) * prohibit_period_between; // 피우지 않은 담배 개수
						int rm = cp * prohibit_period_between; // 절약한 금액
						int il = ((sq * 20) * prohibit_period_between) * 15; // 늘어난 수명

						int il_year = il / 525600;
						int il_month = il % 525600 / 43200;
						int il_day = il % 525600 % 43200 / 1440;

						smoke_start_day2.setText(result.getString("smoke_year") + "년 " + result.getString("smoke_month")
								+ "월 " + result.getString("smoke_day") + "일");
						prohibit_period2.setText(yeart + "년 " + montht + "개월 " + dayt + "일");
						cut_down_cigarret2.setText(mf.format(cdct) + "개비");
						reduce_money2.setText(mf.format(rm) + "원");
						increase_life2.setText(il_year + "년 " + il_month + "달 " + il_day + "분");

						// 정보이름 레이블 변경
						l1.setText("금연 시작일");
						l2.setText("금연 기간");
						l3.setText("피우지 않은 담배");
						l4.setText("절약한 금액");
						l5.setText("늘어난 수명");
					}
					dbSt.close();
					con.close();
				} catch (SQLException e) {
					System.out.println("SQLException : " + e.getMessage());
				}
			}

			// 흡연정보 선택시 데이터
			else if (selected == 1) {
				try {
					Class.forName("com.mysql.cj.jdbc.Driver");
					System.err.println("JDBC-ODBC 드라이버를 정상적으로 로드함");
				} catch (ClassNotFoundException e) {
					System.err.println("드라이버 로드에 실패했습니다.");
				} // JDBC 드라이버 로드

				try {
					Connection con = DriverManager.getConnection(
							"jdbc:mysql://localhost:3306/smokedata?serverTimezone=UTC", "root", "dbsckdrl12");
					System.out.println("DB 연결 완료.");
					Statement dbSt = con.createStatement();
					System.out.println("JDBC 드라이버가 정상적으로 연결되었습니다.");

					String strSQL;
					strSQL = "SELECT * FROM smoke_data; ";

					ResultSet result = dbSt.executeQuery(strSQL);

					if (result.next()) {
						// DB값 정수형으로 읽어오기
						int sy = result.getInt("smoke_year");
						int sm = result.getInt("smoke_month");
						int sd = result.getInt("smoke_day");
						int py = result.getInt("prohibit_year");
						int pm = result.getInt("prohibit_month");
						int pd = result.getInt("prohibit_day");
						int sq = result.getInt("smoke_quantity");
						int cp = result.getInt("cigarette_price");

						// 읽어온 sy, sm, sd 기반으로 LocalDate형 날짜 생성
						LocalDate startDate = LocalDate.of(py, pm, pd);
						LocalDate endDate = LocalDate.of(sy, sm, sd);

						// Period형 객체 period에 날짜 차이 저장
						Period period = startDate.until(endDate);
						int yeart = period.getYears();
						int montht = period.getMonths();
						int dayt = period.getDays();

						int prohibit_period_between = (int) ChronoUnit.DAYS.between(startDate, endDate); // 날짜 차이 계산

						int cdct = (sq * 20) * prohibit_period_between; // 피운 담배 개수
						int rm = cp * prohibit_period_between; // 사용한 금액
						int il = ((sq * 20) * prohibit_period_between) * 15; // 줄어든 수명

						// 수명 연, 월, 일 변환
						int il_year = il / 525600;
						int il_month = il % 525600 / 43200;
						int il_day = il % 525600 % 43200 / 1440;

						// 정보출력 레이블 변경
						smoke_start_day2.setText(result.getString("prohibit_year") + "년 "
								+ result.getString("prohibit_month") + "월 " + result.getString("prohibit_day") + "일");
						prohibit_period2.setText(yeart + "년 " + montht + "개월 " + dayt + "일");
						cut_down_cigarret2.setText(mf.format(cdct) + "개비");
						reduce_money2.setText(mf.format(rm) + "원");
						increase_life2.setText(il_year + "년 " + il_month + "달 " + il_day + "분");

						// 정보이름 레이블 변경
						l1.setText("흡연 시작일");
						l2.setText("흡연 기간");
						l3.setText("피운 담배");
						l4.setText("사용한 금액");
						l5.setText("줄어든 수명");
					}
					dbSt.close();
					con.close();
				} catch (SQLException e) {
					System.out.println("SQLException : " + e.getMessage());
				}
			} // else if
		}// actionPerformed
	}// smokeTimerAfterDataListener

	// 금연 실패 시 팝업
	public class ProhibitFail extends JFrame {
		JLabel top_text, bottom_text;
		JButton OK;
		Font font2 = new Font("맑은 고딕", Font.PLAIN, 20);

		public ProhibitFail() {
			setTitle("금연실패");
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

			Container c = getContentPane();
			c.setLayout(new BorderLayout());

			JPanel top = new JPanel();
			JLabel info = new JLabel("현재까지 달성한 금연 정보입니다.");
			top.setLayout(new FlowLayout(FlowLayout.CENTER, 40, 20));
			top.add(info);
			info.setFont(font2);

			// center 패널
			JPanel center = new JPanel();
			center.setLayout(null);

			JLabel jl1 = new JLabel("금연 시작일");
			JLabel jl2 = new JLabel("금연 기간");
			JLabel jl3 = new JLabel("피우지 않은 담배");
			JLabel jl4 = new JLabel("절약한 금액");
			JLabel jl5 = new JLabel("늘어난 수명");

			// 흡연 정보 출력 레이블 Calendar Class 전역변수
			smoke_start_day = new JLabel();
			prohibit_period = new JLabel();
			cut_down_cigarret = new JLabel();
			reduce_money = new JLabel();
			increase_life = new JLabel();

			// 정보출력 레이블 위치지정
			smoke_start_day.setBounds(120, 15, 300, 30);
			prohibit_period.setBounds(120, 75, 300, 30);
			cut_down_cigarret.setBounds(120, 135, 300, 30);
			reduce_money.setBounds(120, 195, 300, 30);
			increase_life.setBounds(120, 255, 300, 30);

			// 흡연 정보 출력 패널에 레이블 추가
			center.add(smoke_start_day);
			center.add(prohibit_period);
			center.add(cut_down_cigarret);
			center.add(reduce_money);
			center.add(increase_life);

			// 정보 이름 출력
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

			// 레이블 폰트 설정
			smoke_start_day.setFont(font2);
			prohibit_period.setFont(font2);
			cut_down_cigarret.setFont(font2);
			reduce_money.setFont(font2);
			increase_life.setFont(font2);

			// 바텀 패널
			OK = new JButton("확인");
			
			//확인 버튼 클릭 시 팝업 창 종료
			OK.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					setVisible(false);
				}
			});
			JLabel bottom_text = new JLabel(
					"<html><body style='text-align:center;'>언제든지 금연시작 버튼을 눌러서<br /> 다시 금연을 시작하세요.</body></html>");

			center.add(bottom_text);
			center.add(OK);

			bottom_text.setBounds(100, 270, 300, 100);
			OK.setBounds(140, 350, 90, 30);

			c.add(top, BorderLayout.NORTH);
			c.add(center);
			setSize(400, 500);
			setVisible(true);
		}
	}// 금연실패 팝업 클래스 종료

	// 금연 실패시 팝업
	// 리스너---------------------------------------------------------------------
	public class ProhibitFailListener implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			String s = ae.getActionCommand();
			if (s == "실패") {
				new ProhibitFail();
				smoke_data.setVisible(true);
				smoke_data2.setVisible(false);
				try {
					Class.forName("com.mysql.cj.jdbc.Driver");
					System.err.println("JDBC-ODBC 드라이버를 정상적으로 로드함");
				} catch (ClassNotFoundException e) {
					System.err.println("드라이버 로드에 실패했습니다.");
				} // JDBC 드라이버 로드

				try {
					Connection con = DriverManager.getConnection(
							"jdbc:mysql://localhost:3306/smokedata?serverTimezone=UTC", "root", "dbsckdrl12");
					System.out.println("DB 연결 완료.");
					Statement dbSt = con.createStatement();
					System.out.println("JDBC 드라이버가 정상적으로 연결되었습니다.");

					String strSQL;
					strSQL = "SELECT * FROM smoke_data; ";
					ResultSet result = dbSt.executeQuery(strSQL);

					if (result.next()) {
						// DB값 정수형으로 읽어오기
						int sy = result.getInt("smoke_year");
						int sm = result.getInt("smoke_month");
						int sd = result.getInt("smoke_day");
						int sq = result.getInt("smoke_quantity");
						int cp = result.getInt("cigarette_price");

						// 읽어온 sy, sm, sd 기반으로 LocalDate형 날짜 생성
						LocalDate startDate = LocalDate.of(sy, sm, sd);
						LocalDate endDate = LocalDate.now();

						// Period형 객체 period에 날짜 차이 저장
						Period period = startDate.until(endDate);
						int yeart = period.getYears();
						int montht = period.getMonths();
						int dayt = period.getDays();

						int prohibit_period_between = (int) ChronoUnit.DAYS.between(startDate, endDate); // 날짜 차이 계산

						int cdct = (sq * 20) * prohibit_period_between; // 피우지 않은 담배 개수
						int rm = cp * prohibit_period_between; // 절약한 금액
						int il = ((sq * 20) * prohibit_period_between) * 15; // 늘어난 수명

						int il_year = il / 525600;
						int il_month = il % 525600 / 43200;
						int il_day = il % 525600 % 43200 / 1440;

						smoke_start_day.setText(result.getString("smoke_year") + "년 " + result.getString("smoke_month")
								+ "월 " + result.getString("smoke_day") + "일");
						prohibit_period.setText(yeart + "년 " + montht + "개월 " + dayt + "일");
						cut_down_cigarret.setText(mf.format(cdct) + "개비");
						reduce_money.setText(mf.format(rm) + "원");
						increase_life.setText(il_year + "년 " + il_month + "달 " + il_day + "분");

						strSQL = "delete from smoke_data; ";
						dbSt.executeUpdate(strSQL);

					}

					dbSt.close();
					con.close();
				} catch (SQLException e) {
					System.out.println("SQLException : " + e.getMessage());
				}

			} // if문 종료
		}// actionPerformed
	}

	// 흡연정보 입력화면 클래스 ---------------------------------------------
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
		JButton b1 = new JButton("확인");
		JButton b2 = new JButton("취소");

		// 생성자
		DataInput() {
			Container c = getContentPane();
			c.setLayout(new BorderLayout(0, 30));

			JPanel top = new JPanel();
			top.setLayout(new GridLayout(5, 1, 0, 15));

			// 일일 흡연량 입력
			JPanel p1 = new JPanel();
			p1.setLayout(new FlowLayout(FlowLayout.LEFT));
			p1.add(new JLabel("일일 흡연량          :  "));
			p1.add(smoke_quantity);
			p1.add(new JLabel("갑"));

			// 흡연 햇수 입력
			JPanel p2 = new JPanel();
			p2.setLayout(new FlowLayout(FlowLayout.LEFT));
			p2.add(new JLabel("흡연 햇수              :  "));
			p2.add(smoke_period);
			p2.add(new JLabel("년"));

			// 담배 가격 입력
			JPanel p3 = new JPanel();
			p3.setLayout(new FlowLayout(FlowLayout.LEFT));
			p3.add(new JLabel("담배 가격              :  "));
			p3.add(cigarette_price);
			cigarette_price.setSelectedItem("4500");
			p3.add(new JLabel("원"));

			// 흡연 시작일 입력
			JPanel p4 = new JPanel();
			p4.setLayout(new FlowLayout(FlowLayout.LEFT));
			p4.add(new JLabel("흡연 시작일          :  "));
			// 연도
			p4.add(prohibit_year);
			p4.add(new JLabel("년"));
			// 달
			p4.add(prohibit_month);
			p4.add(new JLabel("월"));
			// 일
			p4.add(prohibit_day);
			p4.add(new JLabel("일"));

			// 금연 시작일 입력
			JPanel p5 = new JPanel();
			p5.setLayout(new FlowLayout(FlowLayout.LEFT));
			p5.add(new JLabel("금연 시작일          :  "));
			p5.add(smoke_year);
			p5.add(new JLabel("년"));
			p5.add(smoke_month);
			p5.add(new JLabel("월"));
			p5.add(smoke_day);
			p5.add(new JLabel("일"));
			p5.add(new JLabel("                                 "));
			set_today = new JCheckBox("오늘로 변경");
			p5.add(set_today);
			set_today.addItemListener(new DataInputListenerToday());

			// 정보입력란
			top.add(p1);
			top.add(p2);
			top.add(p3);
			top.add(p4);
			top.add(p5);
			c.add(top, BorderLayout.CENTER);

			// 확인, 취소버튼
			JPanel bottom = new JPanel();
			bottom.add(b1);
			b1.addActionListener(new DataInputListener());
			bottom.add(b2);
			b2.addActionListener(new DataInputListener());
			c.add(bottom, BorderLayout.SOUTH);
		}// 생성자 종료

		public class DataInputListenerToday implements ItemListener {
			LocalDate today = LocalDate.now();

			// Period형 객체 period에 날짜 차이 저장
			int year = today.getYear();
			int month = today.getMonthValue();
			int day = today.getDayOfMonth();

			// 오늘 날짜로 변경
			public void itemStateChanged(ItemEvent ie) {
				if (ie.getStateChange() == ItemEvent.SELECTED) {
					smoke_year.setSelectedItem((Object) Integer.toString(year));
					smoke_month.setSelectedItem((Object) Integer.toString(month));
					smoke_day.setSelectedItem((Object) Integer.toString(day));
				}
			}// itemStateChanged
		}

		// 흡연정보 입력화면 확인,취소 액션
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
					if (s == "확인") {
						if (date)
							throw new DateInputException();
						try {
							Class.forName("com.mysql.cj.jdbc.Driver");
							System.err.println("JDBC 드라이버를 정상적으로 로드함");
						} catch (ClassNotFoundException e) {
							System.err.println("드라이버 로드에 실패했습니다.");
						} // catch
						if (smoke_period.getText().equals("") || smoke_quantity.getText().equals(""))
							throw new DataEmptyException();
						try {
							Connection con = DriverManager.getConnection(
									"jdbc:mysql://localhost:3306/smokedata?serverTimezone=UTC", "root", "dbsckdrl12");
							System.out.println("DB 연결 완료");
							Statement dbSt = con.createStatement();
							System.out.println("JDBC 드라이버가 정상적으로 연결되었습니다.");

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
							System.out.println("데이터 삽입 완료");

							// 확인버튼 클릭 시 금연정보 출력화면 데이터 출력
							strSql = "SELECT * FROM smoke_data; ";

							ResultSet result = dbSt.executeQuery(strSql);

							if (result.next()) {
								// DB값 정수형으로 읽어오기
								int sy = result.getInt("smoke_year");
								int sm = result.getInt("smoke_month");
								int sd = result.getInt("smoke_day");
								int sq = result.getInt("smoke_quantity");
								int cp = result.getInt("cigarette_price");

								// 읽어온 sy, sm, sd 기반으로 LocalDate형 날짜 생성
								LocalDate startDate = LocalDate.of(sy, sm, sd);
								LocalDate endDate = LocalDate.now();

								// Period형 객체 period에 날짜 차이 저장
								Period period = startDate.until(endDate);
								int yeart = period.getYears();
								int montht = period.getMonths();
								int dayt = period.getDays();

								int prohibit_period_between = (int) ChronoUnit.DAYS.between(startDate, endDate); // 날짜차이 계산

								int cdct = (sq * 20) * prohibit_period_between; // 피우지 않은 담배 개수
								int rm = cp * prohibit_period_between; // 절약한 금액
								int il = ((sq * 20) * prohibit_period_between) * 15; // 늘어난 수명

								int il_year = il / 525600;
								int il_month = il % 525600 / 43200;
								int il_day = il % 525600 % 43200 / 1440;

								smoke_start_day2.setText(result.getString("smoke_year") + "년 "
										+ result.getString("smoke_month") + "월 " + result.getString("smoke_day") + "일");
								prohibit_period2.setText(yeart + "년 " + montht + "개월 " + dayt + "일");
								cut_down_cigarret2.setText(mf.format(cdct) + "개비");
								reduce_money2.setText(mf.format(rm) + "원");
								increase_life2.setText(il_year + "년 " + il_month + "달 " + il_day + "분");
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

					else if (s == "취소") {
						SDI.setVisible(false);
					} // else if
				} catch (DateInputException e) {
					DateSelectErr die = new DateSelectErr(SDI, "선택오류", false, "흡연 시작 날짜는 금연 시작 날짜 이전이어야합니다.");
					die.setVisible(true);
				} catch (DataEmptyException e) {
					DateSelectErr dee = new DateSelectErr(SDI, "입력오류", false, "정보를 모두 입력해주세요");
					dee.setVisible(true);
				}
			}// actionPerformed

		}// InputSmokeData 클래스 종료

	}// Smoke_Data_Input 클래스 종료

	// 첫화면 선택 변수
	public void prohibitDB() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			System.err.println("JDBC-ODBC 드라이버를 정상적으로 로드함");
		} catch (ClassNotFoundException e) {
			System.err.println("드라이버 로드에 실패했습니다.");
		} // JDBC 드라이버 로드

		try {
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/smokedata?serverTimezone=UTC",
					"root", "dbsckdrl12");
			System.out.println("DB 연결 완료.");
			Statement dbSt = con.createStatement();
			System.out.println("JDBC 드라이버가 정상적으로 연결되었습니다.");

			String strSQL;
			strSQL = "SELECT * FROM smoke_data; ";

			ResultSet result = dbSt.executeQuery(strSQL);

			if (result.next()) {
				// DB값 정수형으로 읽어오기
				int sy = result.getInt("smoke_year");
				int sm = result.getInt("smoke_month");
				int sd = result.getInt("smoke_day");
				int sq = result.getInt("smoke_quantity");
				int cp = result.getInt("cigarette_price");

				// 읽어온 sy, sm, sd 기반으로 LocalDate형 날짜 생성
				LocalDate startDate = LocalDate.of(sy, sm, sd);
				LocalDate endDate = LocalDate.now();

				// Period형 객체 period에 날짜 차이 저장
				Period period = startDate.until(endDate);
				int yeart = period.getYears();
				int montht = period.getMonths();
				int dayt = period.getDays();

				int prohibit_period_between = (int) ChronoUnit.DAYS.between(startDate, endDate); // 날짜 차이 계산

				int cdct = (sq * 20) * prohibit_period_between; // 피우지 않은 담배 개수
				int rm = cp * prohibit_period_between; // 절약한 금액
				int il = ((sq * 20) * prohibit_period_between) * 15; // 늘어난 수명

				int il_year = il / 525600;
				int il_month = il % 525600 / 43200;
				int il_day = il % 525600 % 43200 / 1440;

				smoke_start_day2.setText(result.getString("smoke_year") + "년 " + result.getString("smoke_month") + "월 "
						+ result.getString("smoke_day") + "일");
				prohibit_period2.setText(yeart + "년 " + montht + "개월 " + dayt + "일");
				cut_down_cigarret2.setText(mf.format(cdct) + "개비");
				reduce_money2.setText(mf.format(rm) + "원");
				increase_life2.setText(il_year + "년 " + il_month + "달 " + il_day + "분");

				smoke_data.setVisible(false);
				smoke_data2.setVisible(true);

			}
			dbSt.close();
			con.close();
		} catch (SQLException e) {
			System.out.println("SQLException : " + e.getMessage());
		}
	}

}// Calendar class 종료

class DateSelectErr extends JDialog implements ActionListener {
	JButton OK;

	DateSelectErr(JFrame frame, String title, boolean mode, String msg) {
		super(frame, title, mode);
		JPanel msgp = new JPanel();
		JLabel msgl = new JLabel(msg);
		msgp.add(msgl);
		add(msgp, BorderLayout.CENTER);

		JPanel bp = new JPanel();
		OK = new JButton("확인");
		OK.addActionListener(this);
		bp.add(OK);
		add(bp, BorderLayout.SOUTH);
		pack();
	}// DateSelectErr

	public void actionPerformed(ActionEvent ae) {
		dispose();
	} // actionPerformed
} // DateSelectErr
