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
		JButton addTodo = new JButton("�߰�");
		title.setHorizontalAlignment(JLabel.CENTER);
		//addTodo.setHorizontalAlignment(JLabel.RIGHT);
		
		setLayout(new BorderLayout());
		
		JPanel north = new JPanel();
		north.setLayout(new BorderLayout());
		add(north, BorderLayout.NORTH);
		north.add(title,BorderLayout.NORTH); north.add(addTodo,BorderLayout.CENTER);
		
		columnName = new Vector<String>();
		columnName.add("����");
		rowData = new Vector<Vector<String>>();
		model = new DefaultTableModel(rowData, columnName);
		table = new JTable(model);
		tableSP = new JScrollPane(table);
		add(tableSP,BorderLayout.CENTER);
		
		
		table.addMouseListener(this);
		addTodo.addActionListener(this);
		listUp();//���� �ҷ�����
	}

	
	public void actionPerformed(ActionEvent ae) {
	
		
		new AddTodo(this);//���� �߰� ��ư Ŭ���� ���� �߰� â ����
	}
	
	public void mouseClicked(MouseEvent e) { //���� Ŭ���� �ش� �÷��� ������ db���� �����ϰ� ����Ʈ�� ������
		 int row = table.getSelectedRow();
		 int col = table.getSelectedColumn();
		 Object data = table.getValueAt(row, col);
		 String value = String.valueOf(data);
		 
		 try {
				Class.forName("com.mysql.cj.jdbc.Driver");
				System.err.println("JDBC ����̹��� ���������� ����Ǿ����ϴ�.");					
				Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/smokedata?serverTimezone=UTC","root","dbsckdrl12");
				System.out.println("DB ���� �Ϸ�");
				Statement dbSt = con.createStatement();
				
				
				String strSql;
				
				 
				
					strSql="DELETE FROM TODO_List WHERE TODO_content LIKE ('"+value+"');";
				dbSt.executeUpdate(strSql);
				
				dbSt.close(); con.close();
				
			} catch(ClassNotFoundException ae) { 
				System.err.println("����̹� �ε忡 �����߽��ϴ�.");
			} catch(SQLException ae) {
				System.out.println("SQLException:"+ae.getMessage());//DB ��ȸ �Ϸ�
			}
		 
		 refresh(); //���� ���� �� ���ΰ�ħ �޼ҵ� ȣ��
		 
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
	void refresh() { //������ �߰��ǰų� ������ ��� ȣ��Ǵ� ���ΰ�ħ �޼ҵ�
		clearTable();
		table.updateUI();
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			System.err.println("JDBC ����̹��� ���������� ����Ǿ����ϴ�.");
			
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/smokedata?serverTimezone=UTC","root","dbsckdrl12");
			System.out.println("DB ���� �Ϸ�");
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
			System.err.println("����̹� �ε忡 �����߽��ϴ�.");
		} catch(SQLException e) {
			System.out.println("SQLException:"+e.getMessage());//DB ��ȸ �Ϸ�
		}
		
	}//���ΰ�ħ �޼ҵ� ��
	
	void listUp() { //���� �ҷ����� �޼ҵ�
		clearTable();
		table.updateUI();
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			System.err.println("JDBC ����̹��� ���������� ����Ǿ����ϴ�.");
			
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/smokedata?serverTimezone=UTC","root","dbsckdrl12");
			System.out.println("DB ���� �Ϸ�");
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
			System.err.println("����̹� �ε忡 �����߽��ϴ�.");
		} catch(SQLException e) {
			System.out.println("SQLException:"+e.getMessage());//DB ��ȸ �Ϸ�
		}
	}//���� �ҷ����� �޼ҵ� ��
	
	public static void main(String args[]) {
		Todo_List tl = new Todo_List();
	}
}



