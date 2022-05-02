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
	Todo_List feedback;//���� Ŭ���� Todo_List���� ����
	JLabel tfTitle;
	JTextField tf = new JTextField();
	JButton add, cancel;
	
	AddTodo(Todo_List tl) {
		feedback = tl;
		Container c = getContentPane();
		c.setLayout(new BorderLayout());
		
		tfTitle = new JLabel("TODO ����Ʈ ����");
		c.add(tfTitle, BorderLayout.NORTH);
		
		tf = new JTextField(255);
		c.add(tf, BorderLayout.CENTER);
		
		JPanel south = new JPanel();
		south.setLayout(new FlowLayout());
		add = new JButton("�߰�");
		cancel = new JButton("���");
		south.add(add); south.add(cancel);
		c.add(south, BorderLayout.SOUTH);
		
		add.addActionListener(this);
		cancel.addActionListener(this);
		
		setTitle("TODO ����Ʈ �߰�");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setSize(300,430);
		setLocation(100,200);
		
	}
	
	public void clear() {
		 tf.setText("");
		}
	
	public void actionPerformed(ActionEvent ae) {
		String s = ae.getActionCommand(); 
		
		if(s.equals("���")) { 
			clear(); //��ҹ�ư ���� �� TextField �������� �ʱ�ȭ
			} else if (s.equals("�߰�")) {
				//clearTable();
				//table.updateUI();
				try {
					Class.forName("com.mysql.cj.jdbc.Driver");
					System.err.println("JDBC ����̹��� ���������� ����Ǿ����ϴ�.");					
					Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/smokedata?serverTimezone=UTC","root","dbsckdrl12");
					System.out.println("DB ���� �Ϸ�");
					Statement dbSt = con.createStatement();
					
					String value;
					String strSql;
					value = tf.getText();
					
					strSql="INSERT INTO TODO_List (TODO_content) VALUES ('"+value+"');";//TextField�� ������ db�� �߰�
					dbSt.executeUpdate(strSql);
					
					dbSt.close(); con.close();
					 
				} catch(ClassNotFoundException e) { 
					System.err.println("����̹� �ε忡 �����߽��ϴ�.");
				} catch(SQLException e) {
					System.out.println("SQLException:"+e.getMessage());//DB ��ȸ �Ϸ�
				} finally {
					feedback.refresh();//���� �߰� �� ����Ʈ�� ǥ��Ǵ� ���� ���� �޼ҵ� ȣ��
				}
				
			}
	}
	
	public static void main(String args[]) {
		
		

		
	}
}