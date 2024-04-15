package simulator.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import simulator.control.Controller;
import simulator.model.AnimalInfo;
import simulator.model.Diet;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.MapInfo.RegionData;
import simulator.model.RegionInfo;

@SuppressWarnings("serial")
public class RegionsTableModel extends AbstractTableModel implements EcoSysObserver {

	record Info(String desc, Map<Diet, Integer> map) {

	}

	List<Info> _cells;

	List<String> _columnNames;

	int _syscols;
	int _sysrows;

	RegionsTableModel(Controller ctrl) {

		_cells = new ArrayList<>();
		_columnNames = new ArrayList<>();

		_columnNames.addAll(Arrays.asList("Row", "Col", "Desc."));
		for (Diet val : Diet.values()) {
			_columnNames.add(val.name());
		}

		ctrl.addObserver(this);

	}

	public String getColumnName(int col) {
		return _columnNames.get(col);
	}

	@Override
	public int getRowCount() {
		return _cells.size();
	}

	@Override
	public int getColumnCount() {
		return _columnNames.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {

		if (columnIndex == 0)
			return rowIndex / _syscols;
		else if (columnIndex == 1)
			return rowIndex % _syscols;
		else if (columnIndex == 2)
			return _cells.get(rowIndex).desc();
		else {
			return _cells.get(rowIndex).map.get(Diet.values()[columnIndex - 3]);
		}
	}

	@Override
	public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
		this._syscols = map.get_cols();
		this._sysrows = map.get_rows();

		for (RegionData reg : map) {

			_cells.add(new Info(reg.r().toString(), new HashMap<>()));

			for (Diet val : Diet.values()) {
				_cells.get(_cells.size() - 1).map.put(val, 0);
			}
		}

	}

	@Override
	public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
		_cells.clear();
		onRegister(time, map, animals);

	}

	@Override
	public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {
		
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
