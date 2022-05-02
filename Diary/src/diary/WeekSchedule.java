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
		columnName.add("������"); columnName.add("��ġ"); columnName.add("������"); columnName.add("������"); columnName.add("����");
		
		rowData = new Vector<Vector<String>>();
		model = new DefaultTableModel(rowData, columnName);
		table = new JTable(model);
		tableSP = new JScrollPane(table);
		
		top.setLayout(new FlowLayout());
		top.add(title);
		
		center.setLayout(new FlowLayout());
		center.add(tableSP);
				
		setTitle("�ְ� ����");
		setSize(500,550);
		setLocation(400,400);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setVisible(true);
		//printWeek(2021,12,8);
		
	}//������ ��
	
	void clearTable() {
		for(int i = 0; i<rowData.size();) rowData.remove(i);
	}//���̺� �ʱ�ȭ �޼ҵ�
	
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
			y = Integer.toString(c); //�Ű��Ǽ� b,c�� 10���� ���� ��� �տ� 0�� �ٿ� ���ڿ��� ��ȯ�ϴ� ���ǹ�
		}
		java.util.Calendar c1 = java.util.Calendar.getInstance();
		String input = a+"-"+b+"-"+c;//  ����,��,���� �Ű������κ��� ���޹޾� �ϳ��� ���ڿ��� ����
		java.util.Date date = null;
		String tmp = a+""+x+""+y;
		
		try {
			date = new SimpleDateFormat("yyyy-MM-dd").parse(input);
		} catch(ParseException e) {}
		c1.setTime(date);
		int thisYear = c1.get(java.util.Calendar.YEAR);
		int thisMonth = c1.get(java.util.Calendar.MONTH)+1;
		int weekNum = c1.get(java.util.Calendar.WEEK_OF_MONTH);//���޹��� ��¥�κ��� ��⵵ ��� ���������� ǥ��
		title.setText(thisYear + "�� " + thisMonth + "�� " + weekNum + "����");//�⺻ ȭ�� ���� �Ϸ�
		
		clearTable();
		table.updateUI();
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			System.err.println("JDBC ����̹��� ���������� ����Ǿ����ϴ�.");
			
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/smokedata?serverTimezone=UTC","root","dbsckdrl12");
			System.out.println("DB ���� �Ϸ�");
			Statement dbSt = con.createStatement();
			
			for (int i=Integer.parseInt(tmp), j=i;i<=j+6;i++) {
				String stqSql = "SELECT * FROM schedule WHERE " + i +">= concat(schedule_start_year, schedule_start_month, schedule_start_date) and " + i + "<= concat(schedule_end_year, schedule_end_month, schedule_end_date);";//���޹��� ��¥�� �Ͽ��Ϸ� ���ػ�� ����ϱ����� ������ ��ȸ			
				ResultSet result = dbSt.executeQuery(stqSql);
			
			
				while(result.next()) {
					Vector<String> txt = new Vector<String>();
					txt.add(result.getString("schedule_name"));
					txt.add(result.getString("schedule_location"));
					txt.add(result.getString("schedule_start_year")+"-"+result.getString("schedule_start_month")+"-"+result.getString("schedule_start_date"));
					txt.add(result.getString("schedule_end_year")+"-"+result.getString("schedule_end_month")+"-"+result.getString("schedule_end_date"));//�Ⱓ �÷��� �и��Ǿ� �ֱ� ������ ���ڿ� �ϳ��� ���� ǥ��
					txt.add(result.getString("schedule_content"));
					rowData.add(txt); table.updateUI();
				} 
			}
			dbSt.close(); con.close();
		} catch(ClassNotFoundException e) { 
			System.err.println("����̹� �ε忡 �����߽��ϴ�.");
		} catch(SQLException e) {
			System.out.println("SQLException:"+e.getMessage());//DB ��ȸ �Ϸ�
		}
	}//Ư�� �� ���� �� ������ ��� �޼ҵ� ��
	
	public static void main(String args[]) {
		WeekSchedule ws = new WeekSchedule();		
	}//���� �޼ҵ� ��
	
}//Ŭ���� ��
