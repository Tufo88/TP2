package simulator.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.util.List;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
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
	
	private int _maxCols;
	private int _maxRows;
	// TODO en caso de ser necesario, aÃ±adir los atributos aquÃ­â€¦
	ChangeRegionsDialog(Controller ctrl) {
		super((Frame) null, true);
		_ctrl = ctrl;
		_ctrl.addObserver(this);
		initGUI();
	}

	private void initGUI() {
		setTitle("Change Regions");
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		setContentPane(mainPanel);


		JPanel helpPanel = new JPanel();
		helpPanel.setLayout(new BoxLayout(helpPanel, BoxLayout.Y_AXIS));
		JPanel tablePanel = new JPanel();
		tablePanel.setLayout(new BoxLayout(tablePanel, BoxLayout.Y_AXIS));
		JPanel comboBoxPanel = new JPanel();
		comboBoxPanel.setLayout(new BoxLayout(comboBoxPanel, BoxLayout.X_AXIS));
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));

		JTextArea helpText = new JTextArea(
				"Select a region type, the rows/cols interval, and provide values for the parameters "
						+ "in the Value column (default values are used for parameters with no value).");

		helpPanel.add(helpText, BorderLayout.CENTER);
		helpText.setEditable(false);
		helpText.setLineWrap(true);
		helpText.setWrapStyleWord(true);
		mainPanel.add(helpPanel, BorderLayout.CENTER);

	
		_regionsInfo = Main._regions_factory.get_info();
		
		_dataTableModel = new DefaultTableModel() {
			@Override
			public boolean isCellEditable(int row, int column) {
				return column == 1;
			}

		};
		_dataTableModel.setColumnIdentifiers(_headers);
		
		JTable table = new JTable(_dataTableModel);
		tablePanel.add(new JScrollPane(table));
		mainPanel.add(tablePanel, BorderLayout.CENTER);
		
		_regionsModel = new DefaultComboBoxModel<>();
		
		for (JSONObject obj : _regionsInfo) {	
			String type = obj.optString("type");
			if(type != null) _regionsModel.addElement(type);
		}
		
		JComboBox<String> regionsComboBox = new JComboBox<>(_regionsModel);

		_fromRowModel = new DefaultComboBoxModel<>();
		_toRowModel = new DefaultComboBoxModel<>();
		
		_fromColModel = new DefaultComboBoxModel<>();
		_toColModel = new DefaultComboBoxModel<>();

		JLabel regionTypeLabel = new JLabel("Region type:");
		
		comboBoxPanel.add(regionTypeLabel, BorderLayout.CENTER);
		comboBoxPanel.add(regionsComboBox, BorderLayout.CENTER);
		
		regionsComboBox.addActionListener(e-> {
			for (JSONObject obj : _regionsInfo) {
				
				String type = obj.optString("type");
				if(((String)regionsComboBox.getSelectedItem()).equals(type)) {
					JSONObject data = obj.optJSONObject("data");
					_dataTableModel.setRowCount(0);
					
					if(data != null) {
						
						for(String k: data.keySet()) {
							Vector<String> v = new Vector<>();
							v.add(k);
							v.add("");
							v.add(data.getString(k));
							_dataTableModel.addRow(v);
						}
						
						
						
					}
				}
				
			}
		});
		// TODO crear 4 combobox que usen estos modelos y aÃ±adirlos al diÃ¡logo.
		JLabel fromToRowLabel = new JLabel("Row from/to:");
		JComboBox<String> fromRowBox = new JComboBox<>(_fromRowModel);
		JComboBox<String> toRowBox = new JComboBox<>(_toRowModel);
		
		for(int i = 0; i < _maxRows; i++) {
			fromRowBox.addItem(Integer.toString(i));
			toRowBox.addItem(Integer.toString(i));
		}

		JLabel fromToColLabel = new JLabel("Column from/to:");
		JComboBox<String> fromColBox = new JComboBox<>(_fromColModel);
		JComboBox<String> toColBox = new JComboBox<>(_toColModel);
		
		for(int i = 0; i < _maxCols; i++) {
			fromColBox.addItem(Integer.toString(i));
			toColBox.addItem(Integer.toString(i));
		}
		
		comboBoxPanel.add(fromToRowLabel, BorderLayout.CENTER);

		comboBoxPanel.add(fromRowBox, BorderLayout.CENTER);
		comboBoxPanel.add(toRowBox, BorderLayout.CENTER);
		
		comboBoxPanel.add(fromToColLabel, BorderLayout.CENTER);
		comboBoxPanel.add(fromColBox, BorderLayout.CENTER);
		comboBoxPanel.add(toColBox, BorderLayout.CENTER);
		
		mainPanel.add(comboBoxPanel, BorderLayout.CENTER);

		JButton cancel = new JButton("Cancel");
		JButton ok = new JButton("OK");
		buttonsPanel.add(cancel);
		buttonsPanel.add(ok);
		
		mainPanel.add(buttonsPanel, BorderLayout.CENTER);
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
		_maxCols = map.get_cols();
		_maxRows = map.get_rows();
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
