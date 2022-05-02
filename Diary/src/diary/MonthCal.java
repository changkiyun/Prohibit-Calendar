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

   // 년, 월 , 월별 최대 일수 설정
   int max_month[] = { 0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
   int year_num, month_num, day_num;

   // 오늘 날짜 정보 객체
   java.util.Calendar today;

   JPanel top,bottom;

   // 테이블 column별 객체
   TableColumn col;

   // DisplayMonth 생성자
   DisplayMonth() {

      setLayout(new BorderLayout());

      top = new JPanel();
      bottom = new JPanel();

      add(top, BorderLayout.NORTH);
      add(bottom, BorderLayout.CENTER);

      dayOfWeekFrame = new Vector<String>();
      dayOfWeekFrame.add("일");
      dayOfWeekFrame.add("월");
      dayOfWeekFrame.add("화");
      dayOfWeekFrame.add("수");
      dayOfWeekFrame.add("목");
      dayOfWeekFrame.add("금");
      dayOfWeekFrame.add("토");

      dateData = new Vector<Vector<String>>();
      model = new DefaultTableModel(dateData, dayOfWeekFrame);
      table = new JTable(model);

      col = table.getColumnModel().getColumn(0);
      col.setCellRenderer(new MyRender(Color.red));

      tableSP = new JScrollPane(table);
      table.setRowHeight(67);

      // 오늘 날짜 기준 연, 월, 일 정보
      today = java.util.Calendar.getInstance();
      year_num = today.get(java.util.Calendar.YEAR);
      month_num = today.get(java.util.Calendar.MONTH) + 1;
      day_num = today.get(java.util.Calendar.DATE);

      // 월별 달력을 생성하는 메소드 호출
      makeMonth(max_month[month_num], year_num, month_num);

      // 버튼
      backWard = new JButton("-");
      forWard = new JButton("+");
      year_month = new JButton(year_num + "년 " + month_num + "월");
      toToday = new JButton("TODAY");
      scAdd = new JButton("일정추가");
      scSearch = new JButton("일정검색");

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
      // 전월 이동 버튼 액션 이벤트
      if (ae.getActionCommand().equals("-")) {
         if (month_num < 2) {
            year_num -= 1;
            month_num = 12;
         } else
            month_num -= 1;
         year_month.setText(year_num + "년 " + month_num + "월");

         clearTable();
         makeMonth(max_month[month_num], year_num, month_num);

      }
      // 다음 월 이동 버튼 액션 이벤트
      if (ae.getActionCommand().equals("+")) {
         if (month_num > 11) {
            year_num += 1;
            month_num = 1;
         } else
            month_num += 1;
         year_month.setText(year_num + "년 " + month_num + "월");

         clearTable();
         makeMonth(max_month[month_num], year_num, month_num);

      }

      // 연 월 버튼 클릭시 연별 화면으로 이동 이벤트
      if (ae.getActionCommand().equals(year_num + "년 " + month_num + "월")) {

         cYear cm = new cYear();
         
         cm.get_y(year_num);
      }

      // 오늘 날짜 화면(주별 화면 기준) 이동 이벤트
      if (ae.getActionCommand().equals("TODAY")) {
         WeekSchedule ws = new WeekSchedule();
         	ws.printWeek(year_num, month_num, day_num);
      }

      if (ae.getActionCommand().equals("일정추가")) {
    	  Schedule_insert win = new Schedule_insert("일정 추가");
    	  win.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			win.setSize(300,430);
			win.setLocation(100, 200);
			win.show();
      }

      if (ae.getActionCommand().equals("일정검색")) {
    	  Schedule_search win = new Schedule_search("일정 검색");
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

         // 클릭 한table의 행(해당 주) 의 제일 첫날 정보 추출
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

         // 클릭한 주 의 주별 화면 출력

         WeekSchedule ws = new WeekSchedule();
         ws.printWeek(year_num, month_num, d);
      } catch (ArrayIndexOutOfBoundsException e) {
         JOptionPane.showMessageDialog(null, "주간 화면으로 이동하려면 \n 다른 주를 클릭 해 주세요.", "메세지", JOptionPane.ERROR_MESSAGE);

         // System.out.println("다른 주를 클릭해 주세요!");
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

   // 연, 월, 일 정보를 이용하여 해당월 1일의 요일 정보를 구하고 이를 통해 월별 달력을 생성하는 메소드
   public void makeMonth(int maxDay, int year_num, int month_num) {
      // 년,월 정보를 알고 있을때 해당달의 1일의 요일 정보를 알기 위한 객체
      LocalDate date;
      DayOfWeek dayOfWeek;
      int first_dayOfWeek;
      int day = 1;

      // 연,월 정보를 통한 해당 연월기준 1일의 요일 정보 구하기
      date = LocalDate.of(year_num, month_num, 1);
      dayOfWeek = date.getDayOfWeek();
      first_dayOfWeek = dayOfWeek.getValue();
      first_dayOfWeek %= 7; // 일요일 기준 7로 값이 리턴 되어 vector활용 위해 값처리

      // 1~6번째 줄 vector 생성
      Vector<String> firstWeek = new Vector<String>(7);
      Vector<String> secondWeek = new Vector<String>(7);
      Vector<String> thirdrWeek = new Vector<String>(7);
      Vector<String> fourthWeek = new Vector<String>(7);
      Vector<String> fifthWeek = new Vector<String>(7);
      Vector<String> sixthWeek = new Vector<String>(7);

      // 7열 6행에 각 각 생성시 날짜 / 일정 / 금연 시작 일정 삽입.

      // 첫줄
      for (int i = 0; i < first_dayOfWeek; i++)
         firstWeek.add(null);
      for (int i = first_dayOfWeek; i < 7; i++)
         firstWeek.add(Integer.toString(day++) + checkSc(year_num, month_num, day - 1)
               + checkNoSmk(year_num, month_num, day - 1));

      // 두번째 줄
      for (int i = 0; i < 7; i++) {
         secondWeek.add(Integer.toString(day++) + '\n' + checkSc(year_num, month_num, day - 1)
               + checkNoSmk(year_num, month_num, day - 1));

      }

      // 세번째 줄
      for (int i = 0; i < 7; i++)
         thirdrWeek.add(Integer.toString(day++) + checkSc(year_num, month_num, day - 1)
               + checkNoSmk(year_num, month_num, day - 1));

      // 네번째 줄
      for (int i = 0; i < 7; i++)
         fourthWeek.add(Integer.toString(day++) + checkSc(year_num, month_num, day - 1)
               + checkNoSmk(year_num, month_num, day - 1));

      // 다섯번째 줄
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
      // 여섯번째 줄
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

   // table 클리어 메소드
   public void clearTable() {
      for (int i = 0; i < dateData.size();)
         dateData.remove(i);

   }

   // 일정일 조회 및 문자열 표시 메소드
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
            str += "\n" + result_st.getString("schedule_name") + " 시작";
         }
         if (result_ed.next()) {
            str += "\n" + result_ed.getString("schedule_name") + " 종료";
         }

         dbSt1.close();
         dbSt2.close();
         con.close();
      } catch (ClassNotFoundException e) {
         System.err.println("드라이버 로드에 실패했습니다.");

      } catch (SQLException e) {
         System.out.println("SQLException : " + e.getMessage());
      }
      return str;
   }

   // 금연시작일 조회및 문자열 표시 메소드
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
            str += '\n' + "금연 시작";
         }

         dbSt1.close();
         con.close();
      } catch (ClassNotFoundException e) {
         System.err.println("드라이버 로드에 실패했습니다.");

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

//일요일 날짜 색깔 설정 클래스
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