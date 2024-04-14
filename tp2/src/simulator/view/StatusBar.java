package simulator.view;

import java.awt.FlowLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.RegionInfo;

public class StatusBar extends JPanel implements EcoSysObserver {
	private JLabel _timeText, _totalAnimalsText, _dimensionsText;
	private JLabel _actualTime, _actualAnimals, _actualDimensions;
	public StatusBar() {
		initGUI();
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
	}

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
