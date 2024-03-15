package simulator.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import org.json.JSONObject;

public class RegionManager implements AnimalMapView {

	int _cols;
	int _rows;
	int _width;
	int _height;
	int _region_height;
	int _region_width;

	List<List<Region>> _regions;

	Map<Animal, Region> _animal_region;

	public RegionManager(int cols, int rows, int width, int height) throws IllegalArgumentException {
		if (width < cols)
			throw new IllegalArgumentException("Width has to be greater than cols"); // if width < cols, region_width =
																						// 0
		if (height < rows)
			throw new IllegalArgumentException("Height has to be greater than rows");
		
		if (width % cols != 0 || height % rows != 0) throw new IllegalArgumentException("Height and Width must be divisible by rows and cols");
		this._cols = cols;
		this._rows = rows;

		this._region_width = width / this._cols;
		this._region_height = height / this._rows;

		this._height = height;
		this._width = width;

		initRegions();

		this._animal_region = new HashMap<Animal, Region>();
	}

	private void initRegions() {
		_regions = new ArrayList<List<Region>>(this._rows);
		for (int i = 0; i < this._rows; i++) {
			List<Region> r = new ArrayList<Region>(this._cols);
			for (int j = 0; j < this._cols; j++) {
				r.add(new DefaultRegion());
			}
			_regions.add(r);
		}
	}

	void set_region(int row, int col, Region r) {
		if (row >= _rows)
			throw new IllegalArgumentException("Trying to access row [" + row + "] of [" + _rows + "]");
		if (col >= _cols)
			throw new IllegalArgumentException("Trying to access col [" + col + "] of [" + _cols + "]");
		Region prev = _regions.get(row).get(col);

		prev.getAnimals().forEach((a) -> {
			r.add_animal(a);
			_animal_region.put(a, r);
		});

		_regions.get(row).set(col, r);
	}

	private Region getRegionFromAnimal(Animal a) {
		return _regions.get((int) a.get_position().getY() / _region_height)
				.get((int) a.get_position().getX() / _region_width);
	}

	void register_animal(Animal a) {
		a.init(this);

		Region r = getRegionFromAnimal(a);

		r.add_animal(a);

		_animal_region.put(a, r);
	}

	void unregister_animal(Animal a) {
		Region r = _animal_region.get(a);

		r.remove_animal(a);

		_animal_region.remove(a);
	}

	void update_animal_region(Animal a) {
		Region r = getRegionFromAnimal(a);

		if (r.equals(_animal_region.get(a)))
			return;

		unregister_animal(a);
		register_animal(a);
	}

	void update_all_regions(double dt) {
		for (List<Region> row : _regions) {
			for (Region reg : row) {
				reg.update(dt);
			}
		}
	}

	@Override
	public int get_cols() {
		return _cols;
	}

	@Override
	public int get_rows() {
		return _rows;
	}

	@Override
	public int get_width() {
		return _width;
	}

	@Override
	public int get_height() {
		return _height;
	}

	@Override
	public int get_region_width() {
		return _region_width;
	}

	@Override
	public int get_region_height() {
		return _region_height;
	}

	@Override
	public double get_food(Animal a, double dt) {
		return _animal_region.get(a).get_food(a, dt);
	}

	@Override
	public List<Animal> get_animals_in_range(Animal e, Predicate<Animal> filter) {
		int i = 0, j = 0;
		int regActI = (int) e.get_position().getY() / _region_height;
		int regActJ = (int) e.get_position().getX() / _region_width;
		int maxRangeI = (int) e.get_sight_range() / _region_height;
		int maxRangeJ = (int) e.get_sight_range() / _region_width;

		List<Animal> a = new ArrayList<Animal>();
		for (List<Region> row : _regions) {
			for (Region r : row) {
				if (i <= regActI + maxRangeI && i >= regActI - maxRangeI && j <= regActJ + maxRangeJ
						&& j >= regActJ - maxRangeJ) {
					a.addAll(r.getAnimals((Animal b) -> e.isInSight(b) && !e.equals(b) && filter.test(b)));
				}
				j++;
			}
			j = 0;
			i++;
		}
		return a;
	}

	@Override
	public JSONObject as_JSON() {

		JSONObject obj = new JSONObject();
		int i = 0, j = 0;
		for (List<Region> row : _regions) {
			for (Region r : row) {
				JSONObject reg_obj = new JSONObject().put("row", i).put("col", j).put("data", r.as_JSON());
				obj.append("regiones", reg_obj);
				j++;
			}
			i++;
			j = 0;
		}
		return obj;
	}

}
