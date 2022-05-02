package diary;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;

import java.util.*;
import java.util.Vector;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.sql.*;

class DisplayMonth extends JPanel implements ActionListener, MouseListener {
   Vector<String> dayOfWeekFrame;
   Vector<Vector<String>> dateData;
   JTable table = null;
   JScrollPane tableSP;
   DefaultTableModel model = null;
   JButton backWard, forWard, year_month;

   JButton scAdd, scSearch;
   JButton toToday;

   // ��, �� , ���� �ִ� �ϼ� ����
   int max_month[] = { 0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
   int year_num, month_num, day_num;

   // ���� ��¥ ���� ��ü
   java.util.Calendar today;

   JPanel top,bottom;

   // ���̺� column�� ��ü
   TableColumn col;

   // DisplayMonth ������
   DisplayMonth() {

      setLayout(new BorderLayout());

      top = new JPanel();
      bottom = new JPanel();

      add(top, BorderLayout.NORTH);
      add(bottom, BorderLayout.CENTER);

      dayOfWeekFrame = new Vector<String>();
      dayOfWeekFrame.add("��");
      dayOfWeekFrame.add("��");
      dayOfWeekFrame.add("ȭ");
      dayOfWeekFrame.add("��");
      dayOfWeekFrame.add("��");
      dayOfWeekFrame.add("��");
      dayOfWeekFrame.add("��");

      dateData = new Vector<Vector<String>>();
      model = new DefaultTableModel(dateData, dayOfWeekFrame);
      table = new JTable(model);

      col = table.getColumnModel().getColumn(0);
      col.setCellRenderer(new MyRender(Color.red));

      tableSP = new JScrollPane(table);
      table.setRowHeight(67);

      // ���� ��¥ ���� ��, ��, �� ����
      today = java.util.Calendar.getInstance();
      year_num = today.get(java.util.Calendar.YEAR);
      month_num = today.get(java.util.Calendar.MONTH) + 1;
      day_num = today.get(java.util.Calendar.DATE);

      // ���� �޷��� �����ϴ� �޼ҵ� ȣ��
      makeMonth(max_month[month_num], year_num, month_num);

      // ��ư
      backWard = new JButton("-");
      forWard = new JButton("+");
      year_month = new JButton(year_num + "�� " + month_num + "��");
      toToday = new JButton("TODAY");
      scAdd = new JButton("�����߰�");
      scSearch = new JButton("�����˻�");

      backWard.addActionListener(this);
      forWard.addActionListener(this);
      year_month.addActionListener(this);
      toToday.addActionListener(this);
      table.addMouseListener(this);
      scAdd.addActionListener(this);
      scSearch.addActionListener(this);
      top.setLayout(new FlowLayout());
      top.add(scAdd);
      top.add(scSearch);
      top.add(backWard);
      top.add(year_month);
      top.add(forWard);
      top.add(toToday);

      bottom.setLayout(new FlowLayout());
      bottom.add(tableSP);

      setSize(1020, 600);
      setLocation(100, 200);
   }

   public void actionPerformed(ActionEvent ae) {
      // ���� �̵� ��ư �׼� �̺�Ʈ
      if (ae.getActionCommand().equals("-")) {
         if (month_num < 2) {
            year_num -= 1;
            month_num = 12;
         } else
            month_num -= 1;
         year_month.setText(year_num + "�� " + month_num + "��");

         clearTable();
         makeMonth(max_month[month_num], year_num, month_num);

      }
      // ���� �� �̵� ��ư �׼� �̺�Ʈ
      if (ae.getActionCommand().equals("+")) {
         if (month_num > 11) {
            year_num += 1;
            month_num = 1;
         } else
            month_num += 1;
         year_month.setText(year_num + "�� " + month_num + "��");

         clearTable();
         makeMonth(max_month[month_num], year_num, month_num);

      }

      // �� �� ��ư Ŭ���� ���� ȭ������ �̵� �̺�Ʈ
      if (ae.getActionCommand().equals(year_num + "�� " + month_num + "��")) {

         cYear cm = new cYear();
         
         cm.get_y(year_num);
      }

      // ���� ��¥ ȭ��(�ֺ� ȭ�� ����) �̵� �̺�Ʈ
      if (ae.getActionCommand().equals("TODAY")) {
         WeekSchedule ws = new WeekSchedule();
         	ws.printWeek(year_num, month_num, day_num);
      }

      if (ae.getActionCommand().equals("�����߰�")) {
    	  Schedule_insert win = new Schedule_insert("���� �߰�");
    	  win.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			win.setSize(300,430);
			win.setLocation(100, 200);
			win.show();
      }

      if (ae.getActionCommand().equals("�����˻�")) {
    	  Schedule_search win = new Schedule_search("���� �˻�");
    	  win.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			win.setSize(650,500);
			win.setLocation(100, 200);
			win.show();
      }
      table.updateUI();
   }

   public void mouseClicked(MouseEvent ae) {
      int row = 0;
      int count = 0, d = 0;
      row = table.getSelectedRow();
      String s = "";
      try {

         // Ŭ�� ��table�� ��(�ش� ��) �� ���� ù�� ���� ����
         for (int k = 0; k < 7;) {

            if (((String) model.getValueAt(row, k) != null)) {
               s = (String) model.getValueAt(row, k);
               break;
            } else if ((String) model.getValueAt(row, k) == null)
               k++;

         }

         char c[] = s.toCharArray(); // c[0], c[1]

         for (int i = 0; i < c.length; i++)
            if (Character.isDigit(c[i]))
               count++;
            else
               break;
         if (count == 1)
            d = Character.getNumericValue(c[0]);
         else if (count == 2)
            d = 10 * Character.getNumericValue(c[0]) + Character.getNumericValue(c[1]);

         // Ŭ���� �� �� �ֺ� ȭ�� ���

         WeekSchedule ws = new WeekSchedule();
         ws.printWeek(year_num, month_num, d);
      } catch (ArrayIndexOutOfBoundsException e) {
         JOptionPane.showMessageDialog(null, "�ְ� ȭ������ �̵��Ϸ��� \n �ٸ� �ָ� Ŭ�� �� �ּ���.", "�޼���", JOptionPane.ERROR_MESSAGE);

         // System.out.println("�ٸ� �ָ� Ŭ���� �ּ���!");
      }

   }

   public void mousePressed(MouseEvent ae) {
   }

   public void mouseReleased(MouseEvent ae) {
   }

   public void mouseEntered(MouseEvent ae) {
   }

   public void mouseExited(MouseEvent ae) {
   }

   // ��, ��, �� ������ �̿��Ͽ� �ش�� 1���� ���� ������ ���ϰ� �̸� ���� ���� �޷��� �����ϴ� �޼ҵ�
   public void makeMonth(int maxDay, int year_num, int month_num) {
      // ��,�� ������ �˰� ������ �ش���� 1���� ���� ������ �˱� ���� ��ü
      LocalDate date;
      DayOfWeek dayOfWeek;
      int first_dayOfWeek;
      int day = 1;

      // ��,�� ������ ���� �ش� �������� 1���� ���� ���� ���ϱ�
      date = LocalDate.of(year_num, month_num, 1);
      dayOfWeek = date.getDayOfWeek();
      first_dayOfWeek = dayOfWeek.getValue();
      first_dayOfWeek %= 7; // �Ͽ��� ���� 7�� ���� ���� �Ǿ� vectorȰ�� ���� ��ó��

      // 1~6��° �� vector ����
      Vector<String> firstWeek = new Vector<String>(7);
      Vector<String> secondWeek = new Vector<String>(7);
      Vector<String> thirdrWeek = new Vector<String>(7);
      Vector<String> fourthWeek = new Vector<String>(7);
      Vector<String> fifthWeek = new Vector<String>(7);
      Vector<String> sixthWeek = new Vector<String>(7);

      // 7�� 6�࿡ �� �� ������ ��¥ / ���� / �ݿ� ���� ���� ����.

      // ù��
      for (int i = 0; i < first_dayOfWeek; i++)
         firstWeek.add(null);
      for (int i = first_dayOfWeek; i < 7; i++)
         firstWeek.add(Integer.toString(day++) + checkSc(year_num, month_num, day - 1)
               + checkNoSmk(year_num, month_num, day - 1));

      // �ι�° ��
      for (int i = 0; i < 7; i++) {
         secondWeek.add(Integer.toString(day++) + '\n' + checkSc(year_num, month_num, day - 1)
               + checkNoSmk(year_num, month_num, day - 1));

      }

      // ����° ��
      for (int i = 0; i < 7; i++)
         thirdrWeek.add(Integer.toString(day++) + checkSc(year_num, month_num, day - 1)
               + checkNoSmk(year_num, month_num, day - 1));

      // �׹�° ��
      for (int i = 0; i < 7; i++)
         fourthWeek.add(Integer.toString(day++) + checkSc(year_num, month_num, day - 1)
               + checkNoSmk(year_num, month_num, day - 1));

      // �ټ���° ��
      int mid1 = maxDay - day + 1;
      if (mid1 > 7)
         for (int i = 0; i < 7; i++)
            fifthWeek.add(Integer.toString(day++) + checkSc(year_num, month_num, day - 1)
                  + checkNoSmk(year_num, month_num, day - 1));
      else {
         for (int i = 0; i < mid1; i++)
            fifthWeek.add(Integer.toString(day++) + checkSc(year_num, month_num, day - 1)
                  + checkNoSmk(year_num, month_num, day - 1));
         for (int i = mid1; i < 7; i++)
            fifthWeek.add(null);
      }
      // ������° ��
      int mid2 = maxDay - day + 1;
      if (mid2 > 0) {
         for (int i = 0; i < mid2; i++)
            sixthWeek.add(Integer.toString(day++) + checkSc(year_num, month_num, day - 1)
                  + checkNoSmk(year_num, month_num, day - 1));
         for (int i = mid2; i < 7; i++)
            sixthWeek.add(null);
      } else
         for (int i = 0; i < 7; i++)
            sixthWeek.add(null);

      dateData.add(firstWeek);
      dateData.add(secondWeek);
      dateData.add(thirdrWeek);
      dateData.add(fourthWeek);
      dateData.add(fifthWeek);
      dateData.add(sixthWeek);

   }

   // table Ŭ���� �޼ҵ�
   public void clearTable() {
      for (int i = 0; i < dateData.size();)
         dateData.remove(i);

   }

   // ������ ��ȸ �� ���ڿ� ǥ�� �޼ҵ�
   public String checkSc(int year_num, int month_num, int day) {
      String str = "";

      try {
         Class.forName("com.mysql.cj.jdbc.Driver");

         Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/smokedata?serverTimezone=UTC",
               "root", "dbsckdrl12");

         Statement dbSt1 = con.createStatement();
         Statement dbSt2 = con.createStatement();

         String strSql_st = "SELECT*FROM schedule WHERE schedule_start_year='" + year_num
               + "' and schedule_start_month='" + month_num + "' and schedule_start_date='" + day + "';";
         String strSql_ed = "SELECT*FROM schedule WHERE schedule_end_year='" + year_num
               + "' and schedule_end_month='" + month_num + "' and schedule_end_date='" + day + "';";

         ResultSet result_st = dbSt1.executeQuery(strSql_st);
         ResultSet result_ed = dbSt2.executeQuery(strSql_ed);

         if (result_st.next()) {
            str += "\n" + result_st.getString("schedule_name") + " ����";
         }
         if (result_ed.next()) {
            str += "\n" + result_ed.getString("schedule_name") + " ����";
         }

         dbSt1.close();
         dbSt2.close();
         con.close();
      } catch (ClassNotFoundException e) {
         System.err.println("����̹� �ε忡 �����߽��ϴ�.");

      } catch (SQLException e) {
         System.out.println("SQLException : " + e.getMessage());
      }
      return str;
   }

   // �ݿ������� ��ȸ�� ���ڿ� ǥ�� �޼ҵ�
   public String checkNoSmk(int year_num, int month_num, int day) {
      String str = "";

      try {
         Class.forName("com.mysql.cj.jdbc.Driver");

         Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/smokedata?serverTimezone=UTC",
               "root", "dbsckdrl12");

         Statement dbSt1 = con.createStatement();

         String strSql_st = "SELECT*FROM smoke_data WHERE smoke_year='" + year_num
               + "' and smoke_month='" + month_num + "' and smoke_day='" + day + "';";

         ResultSet result_st = dbSt1.executeQuery(strSql_st);

         if (result_st.next()) {
            str += '\n' + "�ݿ� ����";
         }

         dbSt1.close();
         con.close();
      } catch (ClassNotFoundException e) {
         System.err.println("����̹� �ε忡 �����߽��ϴ�.");

      } catch (SQLException e) {
         System.out.println("SQLException : " + e.getMessage());
      }
      return str;
   }
   
   void inputDate(int year, int month) {
	   year_num = year;
	   month_num = month;
   }
}

//�Ͽ��� ��¥ ���� ���� Ŭ����
class MyRender extends DefaultTableCellRenderer {
   Color red;

   MyRender(Color red) {
      super();
      this.red = red;
   }

   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
         int row, int column) {
      Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
      cell.setForeground(red);
      return cell;
   }
}

class MonthCal {
   public static void main(String args[]) {
      DisplayMonth dm = new DisplayMonth();
      
   }
}