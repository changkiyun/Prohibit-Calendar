package diary;

import javax.swing.JFrame;

public class Main {
	public static void main(String args[]) {
        noSmoking win = new noSmoking("���̾ ��й�ȣ �Է�");
        win.setSize(500, 400);
        win.setLocation(200, 200);
        win.setVisible(true);
        win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
