package simulator.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.json.JSONObject;

import simulator.factories.Factory;

public class Simulator implements JSONable {

	Factory<Animal> _animals_factory;
	Factory<Region> _regions_factory;

	RegionManager _reg_mngr;
	List<Animal> _animals;

	double _time;

	public Simulator(int cols, int rows, int width, int height, Factory<Animal> animals_factory,
			Factory<Region> regions_factory) throws IllegalArgumentException {
		if (animals_factory == null)
			throw new IllegalArgumentException("Animal factory is null");
		if (regions_factory == null)
			throw new IllegalArgumentException("Region factory is null");

		_time = 0.0;
		this._animals_factory = animals_factory;
		this._regions_factory = regions_factory;
		_reg_mngr = new RegionManager(cols, rows, width, height);
		_animals = new ArrayList<Animal>();
	}

	private void set_region(int row, int col, Region r) {
		_reg_mngr.set_region(row, col, r);
	}

	public void set_region(int row, int col, JSONObject r_json) {
		Region r = null;

		r = _regions_factory.create_instance(r_json);
		this.set_region(row, col, r);
	}

	private void add_animal(Animal a) {
		_animals.add(a);
		_reg_mngr.register_animal(a);
	}

	public void add_animal(JSONObject a_json) {
		Animal a = null;
		a = _animals_factory.create_instance(a_json);
		this.add_animal(a);
	}

	public MapInfo get_map_info() {
		return _reg_mngr;
	}

	public List<? extends AnimalInfo> get_animals() {
		return Collections.unmodifiableList(_animals);
	}

	public double get_time() {
		return _time;
	}

	public void advance(double dt) {
		_time += dt;
		Iterator<Animal> i = _animals.iterator();
		List<Animal> newAnimals = new ArrayList<Animal>();
		if (_animals.isEmpty())
			return;
		while (i.hasNext()) {
			Animal a = i.next();
			if (a.isDead()) {
				_reg_mngr.unregister_animal(a);
				i.remove();
			} else {
				a.update(dt);
				_reg_mngr.update_animal_region(a);
				if (a.is_pregnant()) {
					Animal b = a.deliver_baby();
					// no podemos hacer directamente add_animal porque si no nos quedariamos sin
					// iterador
					_reg_mngr.register_animal(b);
					newAnimals.add(b);
				}
			}
		}
		_animals.addAll(newAnimals); // hacemos el _animals.add(a) que tambien hace el add_animal
		_reg_mngr.update_all_regions(dt);
	}

	@Override
	public JSONObject as_JSON() {
		return new JSONObject().put("time", _time).put("state", _reg_mngr.as_JSON());
	}
}
