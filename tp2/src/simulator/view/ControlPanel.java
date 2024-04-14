package simulator.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.net.URISyntaxException;

import icons.ICONS;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.filechooser.FileNameExtensionFilter;

import examples.EXAMPLES;
import simulator.control.Controller;
import simulator.misc.Utils;

public class ControlPanel extends JPanel {
	private Controller _ctrl;
	private ChangeRegionsDialog _changeRegionsDialog;
	private JToolBar _toolBar;
	private JFileChooser _fc;
	private boolean _stopped = true; // utilizado en los botones de run/stop
	
	private JButton _loadFileButton;
	private File _configFile;
	
	private JButton _mapViewerButton;
	
	
	private JButton _changeRegionsButton;

	
	private JButton _runButton;
	private JButton _stopButton;
	private JSpinner _stepsSpinner;
	private JTextField _deltaTimeText;
	
	
	private JButton _quitButton;
	
	
	private StatusBar _statusBar;
	
	public ControlPanel(Controller ctrl) {
	_ctrl = ctrl;
	initGUI();
	}
	
	private void initGUI() {
		setLayout(new BorderLayout());
		_toolBar = new JToolBar();
		add(_toolBar, BorderLayout.PAGE_START);
		// TODO crear los diferentes botones/atributos y a�adirlos a _toolaBar.
		// Todos ellos han de tener su correspondiente tooltip. Puedes utilizar
		// _toolaBar.addSeparator() para a�adir la l�nea de separaci�n vertical
		// entre las componentes que lo necesiten.
		
		//Load button
		_loadFileButton = new JButton();
		this._loadFileButton.setToolTipText("Load input file");
		this._loadFileButton.setIcon(new ImageIcon(ICONS.class.getResource("open.png")));
		_toolBar.add(_loadFileButton);
		
		//setup _fc
		_fc = new JFileChooser();
		_fc.setFileFilter(new FileNameExtensionFilter("JSON files", "json"));
//		EXAMPLES.class.getClassLoader();
//		try {
//			_fc.setCurrentDirectory(new File(EXAMPLES.class.getProtectionDomain().getCodeSource().getLocation().toURI()));
//		} catch (URISyntaxException e) {
//			e.printStackTrace();
//			 _fc.setCurrentDirectory(new File(System.getProperty("user.dir") + "/resources/examples"));
//		}
		_fc.setCurrentDirectory(new File(System.getProperty("user.dir") + "/resources/examples"));
		
		_configFile = null;
		
		_loadFileButton.addActionListener((e) -> {
			int result = _fc.showOpenDialog(ViewUtils.getWindow(this));
			if (result == JFileChooser.APPROVE_OPTION)
				_configFile = _fc.getSelectedFile();
		});
		
		
		
		_toolBar.add(Box.createGlue()); // this aligns the button to the right
		_toolBar.addSeparator();
		
		// Map viewer button
		
		this._mapViewerButton = new JButton();
		this._mapViewerButton.setToolTipText("Show Map");
		this._mapViewerButton.setIcon(new ImageIcon(ICONS.class.getResource("viewer.png")));
		_toolBar.add(this._mapViewerButton);
		
		
		//Change regions button
		
		this._changeRegionsButton = new JButton();
		this._changeRegionsButton.setToolTipText("Change regions");
		this._changeRegionsButton.setIcon(new ImageIcon(ICONS.class.getResource("regions.png")));
		_toolBar.add(this._changeRegionsButton);
		
		// TODO Inicializar _changeRegionsDialog con instancias del di�logo de cambio
		// de regiones
		
		
		
		_toolBar.add(Box.createGlue()); // this aligns the button to the right
		_toolBar.addSeparator();

		//run button
		_runButton = new JButton();
		this._runButton.setToolTipText("Run simulation");
		this._runButton.setIcon(new ImageIcon(ICONS.class.getResource("run.png")));
		_toolBar.add(_runButton);
		
		//stop button
		_stopButton = new JButton();
		this._stopButton.setToolTipText("Stop simulation");
		this._stopButton.setIcon(new ImageIcon(ICONS.class.getResource("stop.png")));
		_toolBar.add(_stopButton);
		_toolBar.addSeparator();
		
		JLabel stepsLabel = new JLabel("Steps: ");
		_toolBar.add(stepsLabel);
		
		(_stepsSpinner = new JSpinner()).setValue(10000);
		_toolBar.add(_stepsSpinner);
		
		_toolBar.addSeparator(new Dimension(4,4));
		
		JLabel deltaTimeLabel = new JLabel("Delta-time: ");
		_toolBar.add(deltaTimeLabel);
		this._deltaTimeText = new JTextField("0.03");
		_toolBar.add(this._deltaTimeText);
		
		_toolBar.add(Box.createGlue()); // this aligns the button to the right
		_toolBar.addSeparator();
		

		// Quit Button
		_quitButton = new JButton();
		_quitButton.setToolTipText("Quit");
		_quitButton.setIcon(new ImageIcon(ICONS.class.getResource("exit.png")));
		_quitButton.addActionListener((e) -> ViewUtils.quit(this));
		_toolBar.add(_quitButton);
		//
		
		
		}
		// TODO el resto de m�todos van aqu�
}
