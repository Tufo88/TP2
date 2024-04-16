package simulator.view;

import java.awt.FlowLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import simulator.control.Controller;
import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.RegionInfo;

public class StatusBar extends JPanel implements EcoSysObserver {
	private JLabel _timeText, _totalAnimalsText, _dimensionsText;
	private JLabel _actualTime, _actualAnimals, _actualDimensions;

	public StatusBar(Controller ctrl) {
		initGUI();
		ctrl.addObserver(this);
	}

	private void initGUI() {
		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		this.setBorder(BorderFactory.createBevelBorder(1));

		JSeparator s = new JSeparator(JSeparator.VERTICAL);

		_timeText = new JLabel("Time :");
		_totalAnimalsText = new JLabel("Total Animals: ");
		_dimensionsText = new JLabel("Dimension: ");

		_actualTime = new JLabel();
		_actualAnimals = new JLabel();
		_actualDimensions = new JLabel();

		this.add(_timeText);
		this.add(this._actualTime);
		this.add(s);

		this.add(this._totalAnimalsText);
		this.add(this._actualAnimals);
		this.add(s);

		this.add(this._dimensionsText);
		this.add(this._actualDimensions);

	}

	@Override
	public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
		this._actualTime.setText(String.format("%.2f", time));
		this._actualAnimals.setText(String.valueOf(animals.size()));
		this._actualDimensions.setText(
				String.format("%dx%d %dx%d", map.get_width(), map.get_height(), map.get_rows(), map.get_cols()));
	}

	@Override
	public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
		onRegister(time, map, animals);

	}

	@Override
	public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {
		this._actualTime.setText(String.format("%.2f", time));
		this._actualAnimals.setText(String.valueOf(animals.size()));
	}

	@Override
	public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {
		return;
	}

	@Override
	public void onAdvance(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
		this._actualTime.setText(String.format("%.2f", time));
		this._actualAnimals.setText(String.valueOf(animals.size()));
	}
}
