package simulator.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;

import org.json.JSONObject;

import simulator.control.Controller;
import simulator.launcher.Main;
import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.RegionInfo;

@SuppressWarnings("serial")
public class ChangeRegionsDialog extends JDialog implements EcoSysObserver {

	private DefaultComboBoxModel<String> _regionsModel;
	private DefaultComboBoxModel<String> _fromRowModel;
	private DefaultComboBoxModel<String> _toRowModel;
	private DefaultComboBoxModel<String> _fromColModel;
	private DefaultComboBoxModel<String> _toColModel;
	private DefaultTableModel _dataTableModel;
	private Controller _ctrl;
	private List<JSONObject> _regionsInfo;
	private String[] _headers = { "Key", "Value", "Description" };

	// TODO en caso de ser necesario, aÃ±adir los atributos aquÃ­â€¦
	ChangeRegionsDialog(Controller ctrl) {
		super((Frame) null, true);
		_ctrl = ctrl;
		initGUI();
		_ctrl.addObserver(this);
	}

	private void initGUI() {
		setTitle("Change Regions");
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		setContentPane(mainPanel);

		// TODO crea varios paneles para organizar los componentes visuales en el
		// dialogo, y aÃ±adelos al mainpanel. P.ej., uno para el texto de ayuda,
		// uno para la tabla, uno para los combobox, y uno para los botones.

		JPanel helpPanel = new JPanel();
		helpPanel.setLayout(new BoxLayout(helpPanel, BoxLayout.Y_AXIS));
		JPanel tablePanel = new JPanel();
		tablePanel.setLayout(new BoxLayout(tablePanel, BoxLayout.Y_AXIS));
		JPanel comboBoxPanel = new JPanel();
		comboBoxPanel.setLayout(new BoxLayout(comboBoxPanel, BoxLayout.X_AXIS));
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));
		
		// TODO crear el texto de ayuda que aparece en la parte superior del diÃ¡logo y
		// aÃ±adirlo al panel correspondiente diÃ¡logo (Ver el apartado Figuras)
		JTextArea helpText = new JTextArea(
				"Select a region type, the rows/cols interval, and provide values for the parameters "
						+ "in the Value column (default values are used for parameters with no value).");
		
		helpPanel.add(helpText, BorderLayout.CENTER);
		helpText.setEditable(false);
		helpText.setLineWrap(true);
		helpText.setWrapStyleWord(true);
		mainPanel.add(helpPanel);
		
		// _regionsInfo se usarÃ¡ para establecer la informaciÃ³n en la tabla
		_regionsInfo = Main._regions_factory.get_info();
		// _dataTableModel es un modelo de tabla que incluye todos los parÃ¡metros de
		// la region
		_dataTableModel = new DefaultTableModel() {
			@Override
			public boolean isCellEditable(int row, int column) {
				return column == 1;
			}

		};
		_dataTableModel.setColumnIdentifiers(_headers);
		// TODO crear un JTable que use _dataTableModel, y aÃ±adirlo al diÃ¡logo
		JTable table = new JTable(_dataTableModel);
		tablePanel.add(table);
		mainPanel.add(tablePanel, BorderLayout.CENTER);
		// _regionsModel es un modelo de combobox que incluye los tipos de regiones
		_regionsModel = new DefaultComboBoxModel<>();
		// TODO aÃ±adir la descripciÃ³n de todas las regiones a _regionsModel, para eso
		// usa la clave â€œdescâ€� o â€œtypeâ€� de los JSONObject en _regionsInfo,
		// ya que estos nos dan informaciÃ³n sobre lo que puede crear la factorÃ­a.
		for (JSONObject obj : _regionsInfo) {
			
			String type = obj.optString("type");
			String desc = obj.optString("desc");
			Vector<String> vec = new Vector<String>();
			vec.add(type);
			vec.add(desc);
			_dataTableModel.addRow(vec);
		}
		
		// TODO crear un combobox que use _regionsModel y aÃ±adirlo al diÃ¡logo.
		JComboBox<String> regionsComboBox = new JComboBox<>(_regionsModel);
		
		// TODO crear 4 modelos de combobox para _fromRowModel, _toRowModel,
		// _fromColModel y _toColModel.
		// TODO crear 4 combobox que usen estos modelos y aÃ±adirlos al diÃ¡logo.
		// TODO crear los botones OK y Cancel y aÃ±adirlos al diÃ¡logo.
		setPreferredSize(new Dimension(700, 400)); // puedes usar otro tamaÃ±o
		pack();
		setResizable(false);
		setVisible(false);
	}

	public void open(Frame parent) {
		setLocation(//
				parent.getLocation().x + parent.getWidth() / 2 - getWidth() / 2, //
				parent.getLocation().y + parent.getHeight() / 2 - getHeight() / 2);
		pack();
		setVisible(true);
	}
	// TODO el resto de mÃ©todos van aquÃ­â€¦

	@Override
	public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAdvance(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
		// TODO Auto-generated method stub

	}

}
