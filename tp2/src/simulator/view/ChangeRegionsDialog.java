package simulator.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.util.List;
import java.util.Vector;

import javax.swing.Box;
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
	private JComboBox<String> _fromRowBox;
	private JComboBox<String> _toRowBox;
	private JComboBox<String> _fromColBox;
	private JComboBox<String> _toColBox;

	private String region_type;
	private JSONObject region_data;
	private JSONObject _regions;

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
		mainPanel.add(Box.createVerticalStrut(10));

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
		mainPanel.add(Box.createVerticalStrut(5));
		_regionsModel = new DefaultComboBoxModel<>();

		for (JSONObject obj : _regionsInfo) {
			String type = obj.optString("type");
			if (type != null)
				_regionsModel.addElement(type);
		}

		JComboBox<String> regionsComboBox = new JComboBox<>(_regionsModel);

		_fromRowModel = new DefaultComboBoxModel<>();
		_toRowModel = new DefaultComboBoxModel<>();

		_fromColModel = new DefaultComboBoxModel<>();
		_toColModel = new DefaultComboBoxModel<>();

		JLabel regionTypeLabel = new JLabel("Region type:");

		comboBoxPanel.add(Box.createHorizontalStrut(5));
		comboBoxPanel.add(regionTypeLabel, BorderLayout.CENTER);
		comboBoxPanel.add(Box.createHorizontalStrut(5));
		comboBoxPanel.add(regionsComboBox, BorderLayout.CENTER);

		regionsComboBox.addActionListener(e -> {
			for (JSONObject obj : _regionsInfo) {

				String type = obj.optString("type");
				if (((String) regionsComboBox.getSelectedItem()).equals(type)) {
					JSONObject data = obj.optJSONObject("data");
					_dataTableModel.setRowCount(0);

					if (data != null) {

						for (String k : data.keySet()) {
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

		JLabel fromToRowLabel = new JLabel("Row from/to:");
		_fromRowBox = new JComboBox<>(_fromRowModel);
		_toRowBox = new JComboBox<>(_toRowModel);

		this.insertNumbersUptoMaxComboBox(_fromRowBox, _toRowBox, _maxRows);

		JLabel fromToColLabel = new JLabel("Column from/to:");
		_fromColBox = new JComboBox<>(_fromColModel);
		_toColBox = new JComboBox<>(_toColModel);

		this.insertNumbersUptoMaxComboBox(_fromColBox, _toColBox, _maxCols);

		comboBoxPanel.add(Box.createHorizontalStrut(5));
		comboBoxPanel.add(fromToRowLabel, BorderLayout.CENTER);
		comboBoxPanel.add(Box.createHorizontalStrut(5));
		comboBoxPanel.add(_fromRowBox, BorderLayout.CENTER);
		comboBoxPanel.add(Box.createHorizontalStrut(5));

		comboBoxPanel.add(_toRowBox, BorderLayout.CENTER);
		comboBoxPanel.add(Box.createHorizontalStrut(5));

		comboBoxPanel.add(fromToColLabel, BorderLayout.CENTER);
		comboBoxPanel.add(Box.createHorizontalStrut(5));

		comboBoxPanel.add(_fromColBox, BorderLayout.CENTER);
		comboBoxPanel.add(Box.createHorizontalStrut(5));

		comboBoxPanel.add(_toColBox, BorderLayout.CENTER);
		comboBoxPanel.add(Box.createHorizontalStrut(5));

		mainPanel.add(comboBoxPanel, BorderLayout.CENTER);
		mainPanel.add(Box.createVerticalStrut(10));
		JButton cancel = new JButton("Cancel");

		cancel.addActionListener((e) -> {
			ChangeRegionsDialog.this.setVisible(false);
		});

		JButton ok = new JButton("OK");

		ok.addActionListener((e) -> {
			try {
				createJSON();
				_ctrl.set_regions(_regions);
				ChangeRegionsDialog.this.setVisible(false);
			} catch (Exception ex) {
				ViewUtils.showErrorMsg(ex.getLocalizedMessage());
			}

		});

		buttonsPanel.add(cancel);
		buttonsPanel.add(Box.createHorizontalStrut(5));
		buttonsPanel.add(ok);

		mainPanel.add(buttonsPanel, BorderLayout.CENTER);
		mainPanel.add(Box.createVerticalStrut(10));
		setPreferredSize(new Dimension(800, 400));
		pack();
		setResizable(false);
		setVisible(false);
	}

	public void open(Frame parent) {
		setLocation(//
				parent.getLocation().x + 2 * parent.getWidth() / 3 - getWidth() / 2, //
				parent.getLocation().y + 2 * parent.getHeight() / 3 - getHeight() / 2);
		pack();
		setVisible(true);
	}

	private void createJSON() throws Exception {

		_regions = new JSONObject();
		JSONObject regionsObj = new JSONObject();

		// comprobamos que se hayan seleccionado en todos
		if (_fromRowBox.getSelectedIndex() == -1 || _toRowBox.getSelectedIndex() == -1
				|| _fromColBox.getSelectedIndex() == -1 || _toColBox.getSelectedIndex() == -1
				|| this._regionsModel.getSelectedItem() == null)
			throw new Exception("ComboBoxes must all have valid selections");

		if (_fromRowBox.getSelectedIndex() > _toRowBox.getSelectedIndex()
				|| _fromColBox.getSelectedIndex() > _toColBox.getSelectedIndex())
			throw new Exception("Initial row/col must be smaller or equal than finish row/col");

		regionsObj.append("row", _fromRowBox.getSelectedIndex());
		regionsObj.append("row", _toRowBox.getSelectedIndex());

		regionsObj.append("col", _fromColBox.getSelectedIndex());
		regionsObj.append("col", _toColBox.getSelectedIndex());

		JSONObject specObj = new JSONObject();

		this.region_type = (String) this._regionsModel.getSelectedItem();
		specObj.put("type", this.region_type);

		// creamos el region_data
		this.region_data = new JSONObject();
		Vector<Vector> rows = this._dataTableModel.getDataVector();
		for (Vector<String> row : rows) {
			try {
				double val = Double.parseDouble(row.get(1));
				this.region_data.put(row.get(0), val);
			} catch (NumberFormatException ex) {
				if (!row.get(1).equals("")) {
					// if it was a non valid string we throw the Exception with the message
					throw new Exception(ex);
				}
				// if it was an empty string we do nothing as we will later get the default value in the builder
			}
		}
		specObj.put("data", region_data);

		regionsObj.put("spec", specObj);

		_regions.append("regions", regionsObj);

	}

	private void insertNumbersUptoMaxComboBox(JComboBox<String> from, JComboBox<String> to, int max) {

		for (int i = 0; i < max; i++) {
			from.addItem(Integer.toString(i));
			to.addItem(Integer.toString(i));
		}
	}

	@Override
	public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
		onReset(time, map, animals);
	}

	@Override
	public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
		_maxCols = map.get_cols();
		_maxRows = map.get_rows();

		clearComboBoxes();

		this.insertNumbersUptoMaxComboBox(_fromColBox, _toColBox, _maxCols);
		this.insertNumbersUptoMaxComboBox(_fromRowBox, _toRowBox, _maxRows);
	}

	private void clearComboBoxes() {
		this._fromColBox.removeAllItems();
		this._fromRowBox.removeAllItems();
		this._toColBox.removeAllItems();
		this._toRowBox.removeAllItems();

	}

	@Override
	public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {
		return;
	}

	@Override
	public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {
		return;
	}

	@Override
	public void onAdvance(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
		return;
	}

}
