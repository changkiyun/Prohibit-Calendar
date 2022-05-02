package diary;

import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class AddTodo extends JFrame implements ActionListener {
	Todo_List feedback;//상위 클래스 Todo_List명을 공유
	JLabel tfTitle;
	JTextField tf = new JTextField();
	JButton add, cancel;
	
	AddTodo(Todo_List tl) {
		feedback = tl;
		Container c = getContentPane();
		c.setLayout(new BorderLayout());
		
		tfTitle = new JLabel("TODO 리스트 내용");
		c.add(tfTitle, BorderLayout.NORTH);
		
		tf = new JTextField(255);
		c.add(tf, BorderLayout.CENTER);
		
		JPanel south = new JPanel();
		south.setLayout(new FlowLayout());
		add = new JButton("추가");
		cancel = new JButton("취소");
		south.add(add); south.add(cancel);
		c.add(south, BorderLayout.SOUTH);
		
		add.addActionListener(this);
		cancel.addActionListener(this);
		
		setTitle("TODO 리스트 추가");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setSize(300,430);
		setLocation(100,200);
		
	}
	
	public void clear() {
		 tf.setText("");
		}
	
	public void actionPerformed(ActionEvent ae) {
		String s = ae.getActionCommand(); 
		
		if(s.equals("취소")) { 
			clear(); //취소버튼 누를 시 TextField 공백으로 초기화
			} else if (s.equals("추가")) {
				//clearTable();
				//table.updateUI();
				try {
					Class.forName("com.mysql.cj.jdbc.Driver");
					System.err.println("JDBC 드라이버가 정상적으로 연결되었습니다.");					
					Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/smokedata?serverTimezone=UTC","root","dbsckdrl12");
					System.out.println("DB 연결 완료");
					Statement dbSt = con.createStatement();
					
					String value;
					String strSql;
					value = tf.getText();
					
					strSql="INSERT INTO TODO_List (TODO_content) VALUES ('"+value+"');";//TextField의 정보를 db에 추가
					dbSt.executeUpdate(strSql);
					
					dbSt.close(); con.close();
					 
				} catch(ClassNotFoundException e) { 
					System.err.println("드라이버 로드에 실패했습니다.");
				} catch(SQLException e) {
					System.out.println("SQLException:"+e.getMessage());//DB 조회 완료
				} finally {
					feedback.refresh();//일정 추가 후 리스트에 표기되는 정보 갱신 메소드 호출
				}
				
			}
	}
	
	public static void main(String args[]) {
		
		

		
	}
}