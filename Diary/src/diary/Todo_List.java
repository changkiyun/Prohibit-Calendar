package diary;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;
import java.sql.*;

import javax.swing.event.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

public class Todo_List extends JPanel implements ActionListener, MouseListener {
	Vector<String> columnName;
	Vector<Vector<String>> rowData;
	DefaultTableModel model = null;
	JTable table = null;
	JScrollPane tableSP;
	
		
	Todo_List() {
		JLabel title = new JLabel("Todo List");
		JButton addTodo = new JButton("추가");
		title.setHorizontalAlignment(JLabel.CENTER);
		//addTodo.setHorizontalAlignment(JLabel.RIGHT);
		
		setLayout(new BorderLayout());
		
		JPanel north = new JPanel();
		north.setLayout(new BorderLayout());
		add(north, BorderLayout.NORTH);
		north.add(title,BorderLayout.NORTH); north.add(addTodo,BorderLayout.CENTER);
		
		columnName = new Vector<String>();
		columnName.add("내용");
		rowData = new Vector<Vector<String>>();
		model = new DefaultTableModel(rowData, columnName);
		table = new JTable(model);
		tableSP = new JScrollPane(table);
		add(tableSP,BorderLayout.CENTER);
		
		
		table.addMouseListener(this);
		addTodo.addActionListener(this);
		listUp();//일정 불러오기
	}

	
	public void actionPerformed(ActionEvent ae) {
	
		
		new AddTodo(this);//일정 추가 버튼 클릭시 일정 추가 창 열기
	}
	
	public void mouseClicked(MouseEvent e) { //일정 클릭시 해당 컬럼의 내용을 db에서 삭제하고 리스트를 갱신함
		 int row = table.getSelectedRow();
		 int col = table.getSelectedColumn();
		 Object data = table.getValueAt(row, col);
		 String value = String.valueOf(data);
		 
		 try {
				Class.forName("com.mysql.cj.jdbc.Driver");
				System.err.println("JDBC 드라이버가 정상적으로 연결되었습니다.");					
				Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/smokedata?serverTimezone=UTC","root","dbsckdrl12");
				System.out.println("DB 연결 완료");
				Statement dbSt = con.createStatement();
				
				
				String strSql;
				
				 
				
					strSql="DELETE FROM TODO_List WHERE TODO_content LIKE ('"+value+"');";
				dbSt.executeUpdate(strSql);
				
				dbSt.close(); con.close();
				
			} catch(ClassNotFoundException ae) { 
				System.err.println("드라이버 로드에 실패했습니다.");
			} catch(SQLException ae) {
				System.out.println("SQLException:"+ae.getMessage());//DB 조회 완료
			}
		 
		 refresh(); //일정 삭제 후 새로고침 메소드 호출
		 
	}

		 
	 
	 public void mousePressed(MouseEvent e) {
	 
	  
	 }

	 
	 public void mouseReleased(MouseEvent e) {
	  
	  
	 }

	 
	 public void mouseEntered(MouseEvent e) {
	  
	  
	 }

	
	 public void mouseExited(MouseEvent e) {
	  
	  
	 }

		 
	
	void clearTable() {
		for(int i = 0; i<rowData.size();) rowData.remove(i);
	}
	void refresh() { //일정이 추가되거나 삭제될 경우 호출되는 새로고침 메소드
		clearTable();
		table.updateUI();
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			System.err.println("JDBC 드라이버가 정상적으로 연결되었습니다.");
			
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/smokedata?serverTimezone=UTC","root","dbsckdrl12");
			System.out.println("DB 연결 완료");
			Statement dbSt = con.createStatement();
			
			
				String stqSql = "SELECT * FROM TODO_List";//	
				ResultSet result = dbSt.executeQuery(stqSql);
			
			
				while(result.next()) {
					Vector<String> txt = new Vector<String>();
					txt.add(result.getString("TODO_content"));
					rowData.add(txt); table.updateUI();
				} 
			
			dbSt.close(); con.close();
		} catch(ClassNotFoundException e) { 
			System.err.println("드라이버 로드에 실패했습니다.");
		} catch(SQLException e) {
			System.out.println("SQLException:"+e.getMessage());//DB 조회 완료
		}
		
	}//새로고침 메소드 끝
	
	void listUp() { //일정 불러오기 메소드
		clearTable();
		table.updateUI();
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			System.err.println("JDBC 드라이버가 정상적으로 연결되었습니다.");
			
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/smokedata?serverTimezone=UTC","root","dbsckdrl12");
			System.out.println("DB 연결 완료");
			Statement dbSt = con.createStatement();
			
			String stqSql = "SELECT * FROM TODO_List"; 
			ResultSet result = dbSt.executeQuery(stqSql);
			
			while(result.next()) {
				Vector<String> txt = new Vector<String>();
				
				txt.add(result.getString("TODO_content"));
				rowData.add(txt); table.updateUI();
			} 
			
		dbSt.close(); con.close();
		} catch(ClassNotFoundException e) { 
			System.err.println("드라이버 로드에 실패했습니다.");
		} catch(SQLException e) {
			System.out.println("SQLException:"+e.getMessage());//DB 조회 완료
		}
	}//일정 불러오기 메소드 끝
	
	public static void main(String args[]) {
		Todo_List tl = new Todo_List();
	}
}



