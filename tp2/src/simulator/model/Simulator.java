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

	double _dt;

	public Simulator(int cols, int rows, int width, int height, Factory<Animal> animals_factory,
			Factory<Region> regions_factory) {
		_dt = 0.0;
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
		// TODO: Factory
		this.set_region(row, col, r);
	}

	private void add_animal(Animal a) {
		_animals.add(a);
		_reg_mngr.register_animal(a);
	}
	
	public void add_animal(JSONObject a_json) {
		Animal a = null;
		// TODO: Factory
		this.add_animal(a);
	}
	
	public MapInfo get_map_info() {
		return _reg_mngr;
	}
	
	public List<? extends AnimalInfo> get_animals() {
		return Collections.unmodifiableList(_animals);
	}
	
	public double get_time() {
		return _dt;
	}
	
	public void advance(double dt) {
		_dt += dt;
		Iterator<Animal> i = _animals.iterator();
		List<Animal> newAnimals = new ArrayList<Animal>();
		if(_animals.isEmpty()) return;
		while(i.hasNext()) {
			Animal a = i.next();
			if(a.is_dead()) {
				_reg_mngr.unregister_animal(a);
				i.remove();
			}
			else {
				a.update(dt);
				if(a.is_pregnant()) newAnimals.add(a.deliver_baby());
			}
		}
		_animals.addAll(newAnimals);
		_reg_mngr.update_all_regions(dt);
	}
	
	@Override
	public JSONObject as_JSON() {
		return new JSONObject().append("time", _dt).append("state", _reg_mngr.as_JSON());
	}
}
