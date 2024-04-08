package simulator.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import simulator.control.Controller;
import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.RegionInfo;
import simulator.model.State;

public class SpeciesTableModel extends AbstractTableModel implements EcoSysObserver {
	Map<String, Map<State, Integer>> _data;//mapa de genetic code a mapa de state a num de animales en ese state
	
	private static List<String> columnNames;

	SpeciesTableModel(Controller ctrl) {
		_data = new HashMap();

		columnNames = new ArrayList<String>();
		columnNames.add("Species");
		for (State st : State.values()) {
			columnNames.add(st.name());
		}
		
		ctrl.addObserver(this);
	}

	@Override
	public int getRowCount() {

		return _data.size();
	}

	@Override
	public int getColumnCount() {
		
		return columnNames.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if(columnIndex == 0) {
			return _data.keySet().toArray(new String[_data.size()])[rowIndex];
		} else {
			String key = _data.keySet().toArray(new String[_data.size()])[rowIndex];
			
			return _data.get(key).get(State.values()[columnIndex-1]);
		}
		
	}

	@Override
	public String getColumnName(int col) {
		return columnNames.get(col);
	}
	
	@Override
	public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
		registerAll(animals);
		fireTableDataChanged();
	}

	@Override
	public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
		_data.clear();
		fireTableDataChanged();
	}

	@Override
	public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {
		Map<State, Integer> info = _data.get(a.get_genetic_code());
		if(info != null) {
			int am = info.get(a.get_state());
			info.put(a.get_state(), am+1);
			_data.put(a.get_genetic_code(), info);
		}
		fireTableDataChanged();
	}

	@Override
	public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {

	}

	@Override
	public void onAdvance(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
		_data.clear();
		registerAll(animals);
		fireTableDataChanged();
	}

	private void registerAll(List<AnimalInfo> animals) {
		for(AnimalInfo a: animals) {
			Map<State, Integer> info = _data.get(a.get_genetic_code());
			if(info != null) {
				if(info.get(a.get_state()) == null) {
					info.put(a.get_state(), 1);
				} else {
					int am = info.get(a.get_state());
					info.put(a.get_state(), am+1);
					_data.put(a.get_genetic_code(), info);
				}
				
			} else {
				HashMap<State, Integer> m = new HashMap<State, Integer>();
				m.put(a.get_state(), 1);
				_data.put(a.get_genetic_code(), m);
			}
		}
		
		for(String k: _data.keySet()) {
			Map<State, Integer> m = _data.get(k);
			for (State st : State.values()) {
				m.putIfAbsent(st, 0);
				}
			_data.put(k, m);		
		}
	}
}
