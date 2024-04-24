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

@SuppressWarnings("serial")
public class SpeciesTableModel extends AbstractTableModel implements EcoSysObserver {
	Map<String, Map<State, Integer>> _data;// mapa de genetic code a mapa de state a num de animales en ese state

	private static List<String> columnNames;

	SpeciesTableModel(Controller ctrl) {
		_data = new HashMap<>();

		columnNames = new ArrayList<>();
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
		String key = _data.keySet().toArray(new String[_data.size()])[rowIndex];

		if (columnIndex == 0) {
			return key;
		} else {
			return _data.get(key).get(State.values()[columnIndex - 1]);
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
		this.clearMap();
		fireTableDataChanged();
	}

	@Override
	public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {
		updateStateMap(_data.get(a.get_genetic_code()), a);
		fireTableDataChanged();
	}

	@Override
	public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {
		return;
	}

	@Override
	public void onAdvance(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
		this.clearMap();
		registerAll(animals);
		fireTableDataChanged();
	}

	private void registerAll(List<AnimalInfo> animals) {
		for (AnimalInfo a : animals) {
			updateStateMap(_data.get(a.get_genetic_code()), a);
		}

	}

	private void clearMap() {

		for (String k : _data.keySet()) {
			Map<State, Integer> m = _data.get(k);
			for (State st : State.values())
				m.put(st, 0);
		}

	}

	private void updateStateMap(Map<State, Integer> info, AnimalInfo i) {
		if (info == null) { // si no existe el mapa creamos un nuevo mapa que añada todos los posibles
							// estados

			HashMap<State, Integer> m = new HashMap<>();
			for (State st : State.values()) {
				m.put(st, 0);
			}
			_data.put(i.get_genetic_code(), m);

		}

		info = _data.get(i.get_genetic_code());
		int am = info.get(i.get_state());
		info.put(i.get_state(), am + 1);
	}

}
