package diary;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JComboBox;

class DateEvent implements ActionListener {
	JComboBox year, month, date;
	Vector<String> days;
	java.util.Calendar cal;
	int i_year, i_mon, maxDate;

	DateEvent(JComboBox y, JComboBox m, Vector<String> da) {
		year = y;
		month = m;
		days = da;
	}

	public void actionPerformed(ActionEvent ae) {

		i_year = Integer.parseInt((String) year.getSelectedItem());
		i_mon = Integer.parseInt((String) month.getSelectedItem());
		cal = java.util.Calendar.getInstance();
		cal.set(i_year, i_mon - 1, 1);
		maxDate = cal.getActualMaximum(java.util.Calendar.DAY_OF_MONTH);
		int s = days.size();

		if (s > maxDate) {
			System.out.println("s =" + s + " max =" + maxDate);
			while (s > maxDate) {
				days.remove(s - 1);
				s = days.size();
			}
		} else if (s < maxDate) {
			System.out.println("s =" + s + " max =" + maxDate);
			while (s < maxDate) {
				String str = Integer.toString(s + 1);
				days.add(str);
				s = days.size();
			}
		} else {
			System.out.println("s =" + s + " max =" + maxDate);
		}

	}
} // 콤보박스 이벤트 끝
