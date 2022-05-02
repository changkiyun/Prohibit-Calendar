package diary;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;

class cYear extends JPanel implements ActionListener {
	int tdyear, tdmonth, tddate, tdday;
	JLabel yearl;
	String yearpm, monthpm;
	Date today = new Date();
	JPanel top, center;

	int max_month[] = { 0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
	
	cYear() {
		JPanel ct = new JPanel();
		ct.setLayout(new BorderLayout());
		add(ct);
		tdyear = today.getYear() + 1900; // 현재 연도
		yearpm = Integer.toString(tdyear); // 문자로 바꿔주기
		tdmonth = today.getMonth() + 1; // 현재 달
		monthpm = Integer.toString(tdmonth); // 문자로 바꿔주기
		
		top = new JPanel();
		center = new JPanel();
		ct.add(top, BorderLayout.NORTH);
		ct.add(center, BorderLayout.CENTER);

		top.setLayout(new FlowLayout());
		JButton left = new JButton("◁");
		top.add(left);
		yearl = new JLabel();
		yearl.setText(yearpm);
		top.add(yearl);
		JButton right = new JButton("▷");
		top.add(right);
		JButton todaymove = new JButton("today");
		top.add(todaymove);

		left.addActionListener(this);
		todaymove.addActionListener(this);
		right.addActionListener(this);

		center.setLayout(new GridLayout(3, 4, 2, 2));
		JButton m1 = new JButton("January"); // 달력 첫 번째 줄
		center.add(m1);
		JButton m2 = new JButton("February");
		center.add(m2);
		JButton m3 = new JButton("March");
		center.add(m3);
		JButton m4 = new JButton("April");
		center.add(m4);
		JButton m5 = new JButton("May"); // 달력 두 번째 줄
		center.add(m5);
		JButton m6 = new JButton("June");
		center.add(m6);
		JButton m7 = new JButton("July");
		center.add(m7);
		JButton m8 = new JButton("August");
		center.add(m8);
		JButton m9 = new JButton("September"); // 달력 세 번째 줄
		center.add(m9);
		JButton m10 = new JButton("October");
		center.add(m10);
		JButton m11 = new JButton("November");
		center.add(m11);
		JButton m12 = new JButton("December");
		center.add(m12);

		m1.addActionListener(this);
		m2.addActionListener(this);
		m3.addActionListener(this);
		m4.addActionListener(this);
		m5.addActionListener(this);
		m6.addActionListener(this);
		m7.addActionListener(this);
		m8.addActionListener(this);
		m9.addActionListener(this);
		m10.addActionListener(this);
		m11.addActionListener(this);
		m12.addActionListener(this);

		setSize(1500, 1200);
		setLocation(0, 0);

	} // 생성자 끝

	public void actionPerformed(ActionEvent ae) {
		String s = ae.getActionCommand();
		DisplayMonth mc = new DisplayMonth();
		if (s == "◁") {
			tdyear -= 1;
			yearpm = Integer.toString(tdyear);
			yearl.setText(yearpm);
		} else if (s == "▷") {
			tdyear += 1;
			yearpm = Integer.toString(tdyear);
			yearl.setText(yearpm);
		} else if (s == "today") {
			tdyear = today.getYear() + 1900;
			yearpm = Integer.toString(tdyear);
			yearl.setText(yearpm);
		} else if (s == "January") {
			mc.clearTable();
			mc.makeMonth(max_month[1], tdyear, 1);
			mc.inputDate(tdyear, 1);
		} else if (s == "February") {
			mc.clearTable();
			mc.makeMonth(max_month[2], tdyear, 2);
			mc.inputDate(tdyear, 2);
		} else if (s == "March") {
			mc.clearTable();
			mc.makeMonth(max_month[3], tdyear, 3);
			mc.inputDate(tdyear, 3);
		} else if (s == "April") {
			mc.clearTable();
			mc.makeMonth(max_month[4], tdyear, 4);
			mc.inputDate(tdyear, 4);
		} else if (s == "May") {
			mc.clearTable();
			mc.makeMonth(max_month[5], tdyear, 5);
			mc.inputDate(tdyear, 5);
		} else if (s == "June") {
			mc.clearTable();
			mc.makeMonth(max_month[6], tdyear, 6);
			mc.inputDate(tdyear, 6);
		} else if (s == "July") {
			mc.clearTable();
			mc.makeMonth(max_month[7], tdyear, 7);
			mc.inputDate(tdyear, 7);
		} else if (s == "August") {
			mc.clearTable();
			mc.makeMonth(max_month[8], tdyear, 8);
			mc.inputDate(tdyear, 8);
		} else if (s == "September") {
			mc.clearTable();
			mc.makeMonth(max_month[9], tdyear, 9);
			mc.inputDate(tdyear, 9);
		} else if (s == "October") {
			mc.clearTable();
			mc.makeMonth(max_month[10], tdyear, 10);
			mc.inputDate(tdyear, 10);
		} else if (s == "November") {
			mc.clearTable();
			mc.makeMonth(max_month[11], tdyear, 11);
			mc.inputDate(tdyear, 11);
		} else {
			mc.clearTable();
			mc.makeMonth(max_month[12], tdyear, 12);
			mc.inputDate(tdyear, 12);
		} // if-else문 끝(임시)
	} // actionPerformed 끝
	
	
	//월간 화면에서 연도 받아오기
	 public void  get_y(int y) {			
	        yearpm = Integer.toString(y);
	        yearl.setText(yearpm);
	    } //get_y메소드 끝
}

