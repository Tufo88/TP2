package extra.jdialog.ex2;

import java.lang.reflect.InvocationTargetException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

@SuppressWarnings("serial")
public class MainWindow extends JFrame {

	JSONBuilderDialog _dialog;

	public MainWindow() {
		super("Custom Dialog Example");
		initGUI();
	}

	private void initGUI() {

		JPanel mainPanel = new JPanel();
		this.setContentPane(mainPanel);

		mainPanel.add(new JLabel("Click "));
		JButton here = new JButton("HERE");
		here.addActionListener((e) -> build_json());
		mainPanel.add(here);
		mainPanel.add(new JLabel(" to build your json"));

		_dialog = new JSONBuilderDialog();

		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.pack();
		this.setVisible(true);
	}

	protected void build_json() {

		int status = _dialog.open(this);

		if (status == 0) {
			System.out.println("Canceled");
		} else {
			System.out.println("Here is your JSON:");
			System.out.println();
			System.out.println(_dialog.getJSON());
		}
	}

	public static void main(String[] args) throws InvocationTargetException, InterruptedException {
		SwingUtilities.invokeAndWait(() -> new MainWindow());
	}
}
