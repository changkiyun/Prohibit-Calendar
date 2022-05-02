package diary;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import java.util.*;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class WeekSchedule extends JFrame {
		
	Vector<String> columnName;
	Vector<Vector<String>> rowData;
	JTable table = null;
	DefaultTableModel model = null;
	JScrollPane tableSP;
	JLabel title = new JLabel();
	
	
	WeekSchedule() {
		Container c = getContentPane();
		c.setLayout(new BorderLayout());
		JPanel top = new JPanel();
		JPanel center = new JPanel();
		
		c.add(top,BorderLayout.NORTH);
		c.add(center,BorderLayout.CENTER);
		
		columnName = new Vector<String>();
		columnName.add("일정명"); columnName.add("위치"); columnName.add("시작일"); columnName.add("종료일"); columnName.add("내용");
		
		rowData = new Vector<Vector<String>>();
		model = new DefaultTableModel(rowData, columnName);
		table = new JTable(model);
		tableSP = new JScrollPane(table);
		
		top.setLayout(new FlowLayout());
		top.add(title);
		
		center.setLayout(new FlowLayout());
		center.add(tableSP);
				
		setTitle("주간 일정");
		setSize(500,550);
		setLocation(400,400);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setVisible(true);
		//printWeek(2021,12,8);
		
	}//생성자 끝
	
	void clearTable() {
		for(int i = 0; i<rowData.size();) rowData.remove(i);
	}//테이블 초기화 메소드
	
	void printWeek(int a, int b, int c) {
		
		String x; String y;
		if (b<10&&c>=10) {
			x = "0"+Integer.toString(b);
			y = Integer.toString(c);
		} else if (b>=10&&c<10) {
			x = Integer.toString(b);
			y = "0"+Integer.toString(c);
		} else if (b<10 && c<10) {
			x = "0"+Integer.toString(b);
			y = "0"+Integer.toString(c);
		} else {
			x = Integer.toString(b); 
			y = Integer.toString(c); //매개션수 b,c가 10보다 작은 경우 앞에 0을 붙여 문자열로 변환하는 조건문
		}
		java.util.Calendar c1 = java.util.Calendar.getInstance();
		String input = a+"-"+b+"-"+c;//  연도,달,일을 매개변수로부터 전달받아 하나의 문자열로 형성
		java.util.Date date = null;
		String tmp = a+""+x+""+y;
		
		try {
			date = new SimpleDateFormat("yyyy-MM-dd").parse(input);
		} catch(ParseException e) {}
		c1.setTime(date);
		int thisYear = c1.get(java.util.Calendar.YEAR);
		int thisMonth = c1.get(java.util.Calendar.MONTH)+1;
		int weekNum = c1.get(java.util.Calendar.WEEK_OF_MONTH);//전달받은 날짜로부터 몇년도 몇월 몇주차인지 표기
		title.setText(thisYear + "년 " + thisMonth + "월 " + weekNum + "주차");//기본 화면 생성 완료
		
		clearTable();
		table.updateUI();
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			System.err.println("JDBC 드라이버가 정상적으로 연결되었습니다.");
			
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/smokedata?serverTimezone=UTC","root","dbsckdrl12");
			System.out.println("DB 연결 완료");
			Statement dbSt = con.createStatement();
			
			for (int i=Integer.parseInt(tmp), j=i;i<=j+6;i++) {
				String stqSql = "SELECT * FROM schedule WHERE " + i +">= concat(schedule_start_year, schedule_start_month, schedule_start_date) and " + i + "<= concat(schedule_end_year, schedule_end_month, schedule_end_date);";//전달받은 날짜를 일요일로 기준삼아 토요일까지의 일정을 조회			
				ResultSet result = dbSt.executeQuery(stqSql);
			
			
				while(result.next()) {
					Vector<String> txt = new Vector<String>();
					txt.add(result.getString("schedule_name"));
					txt.add(result.getString("schedule_location"));
					txt.add(result.getString("schedule_start_year")+"-"+result.getString("schedule_start_month")+"-"+result.getString("schedule_start_date"));
					txt.add(result.getString("schedule_end_year")+"-"+result.getString("schedule_end_month")+"-"+result.getString("schedule_end_date"));//기간 컬럼이 분리되어 있기 때문에 문자열 하나로 합쳐 표기
					txt.add(result.getString("schedule_content"));
					rowData.add(txt); table.updateUI();
				} 
			}
			dbSt.close(); con.close();
		} catch(ClassNotFoundException e) { 
			System.err.println("드라이버 로드에 실패했습니다.");
		} catch(SQLException e) {
			System.out.println("SQLException:"+e.getMessage());//DB 조회 완료
		}
	}//특정 주 연동 및 데이터 출력 메소드 끝
	
	public static void main(String args[]) {
		WeekSchedule ws = new WeekSchedule();		
	}//메인 메소드 끝
	
}//클래스 끝
