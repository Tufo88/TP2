package simulator.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
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

	@Override
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

			initMapfromCell(_cells.size() - 1);

			this.insertAnimalsinMap(_cells.size() - 1, reg.r().getAnimalsInfo());
		}

		fireTableDataChanged();

	}

	@Override
	public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
		_cells.clear();
		onRegister(time, map, animals);

	}

	@Override
	public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {
		int i = 0;

		Iterator<RegionData> it = map.iterator();
		while (it.hasNext() && !it.next().r().getAnimalsInfo().contains(a)) { //sale cuando lo encontremos
			i++;
		}

		Info info = _cells.get(i); //suponemos que todo animal que busquemos lo encontraremos porque no tendria sentido que no lo encontrasemos
		int actualAmount =  info.map.get(a.get_diet());
		info.map.put(a.get_diet(), actualAmount + 1);
	}

	@Override
	public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {

		int pos = this._syscols*row + col;
		Info info = new Info(r.toString(), new HashMap<>());

		_cells.set(pos, info);
		this.initMapfromCell(pos);
		this.insertAnimalsinMap(pos, r.getAnimalsInfo());

		fireTableDataChanged();

	}

	@Override
	public void onAdvance(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
		clearMaps();
		insertAnimalsInCells(map);
		fireTableDataChanged();

	}

	private void clearMaps() {
		for (Info cell : _cells) {
			for (Diet val : Diet.values()) {
				cell.map.put(val, 0);
			}
		}
	}

	private void initMapfromCell(int pos) {
		for (Diet val : Diet.values()) {
			_cells.get(pos).map.put(val, 0);
		}
	}

	private void insertAnimalsInCells(MapInfo map) {
		int i = 0;
		for (RegionData reg : map) {
			insertAnimalsinMap(i, reg.r().getAnimalsInfo());

			i++;
		}
	}

	private void insertAnimalsinMap(int i, List<AnimalInfo> animals) {
		for (AnimalInfo a : animals) {

			int num =_cells.get(i).map().get(a.get_diet());
			_cells.get(i).map().put(a.get_diet(), num+1);

		}
	}

}
