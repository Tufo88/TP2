package simulator.view;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTable;

import simulator.control.Controller;

public class MainWindow extends JFrame {
	private Controller _ctrl;
	
	

	public MainWindow(Controller ctrl) {
		super("[ECOSYSTEM SIMULATOR]");
		_ctrl = ctrl;
		initGUI();
	}

	private void initGUI() {
		JPanel mainPanel = new JPanel(new BorderLayout());
		setContentPane(mainPanel);
		mainPanel.add(new ControlPanel(_ctrl), BorderLayout.PAGE_START);
		mainPanel.add(new StatusBar(), BorderLayout.PAGE_END);
// Definici�n del panel de tablas (usa un BoxLayout vertical)
		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		mainPanel.add(contentPanel, BorderLayout.CENTER);
// TODO crear la tabla de especies y a�adirla a contentPanel.
// Usa setPreferredSize(new Dimension(500, 250)) para fijar su tama�o
		InfoTable speciesTable = new InfoTable("Species", new SpeciesTableModel(_ctrl)); 
		speciesTable.setPreferredSize(new Dimension(500, 250));
		contentPanel.add(speciesTable);
		
		JTable regionsTable;
		
// TODO crear la tabla de regiones.
// Usa setPreferredSize(new Dimension(500, 250)) para fijar su tama�o
// TODO llama a ViewUtils.quit(MainWindow.this) en el m�todo windowClosing
		//addWindowListener();
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		pack();
		setVisible(true);
		_ctrl.run(10, 0.03, false, null);
	}
}