package simulator.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.json.JSONObject;

import simulator.factories.Factory;

public class Simulator implements JSONable, Observable<EcoSysObserver> {

	Factory<Animal> _animals_factory;
	Factory<Region> _regions_factory;

	RegionManager _reg_mngr;
	List<Animal> _animals;
	
	List<EcoSysObserver> _observers;
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
		_observers = new ArrayList<EcoSysObserver>();
	}
	
	public void reset(int cols, int rows, int width, int height) {
		
		_animals.clear();
		_reg_mngr = new RegionManager(cols, rows, width, height);
		_time = 0.0;
		_observers.forEach((o) -> o.onReset(get_time(), get_map_info(), get_animals_info()));
	}

	private void set_region(int row, int col, Region r) {
		_reg_mngr.set_region(row, col, r);
		_observers.forEach((o) -> o.onRegionSet(row, col, get_map_info(), r));
	}

	public void set_region(int row, int col, JSONObject r_json) {
		Region r = null;

		r = _regions_factory.create_instance(r_json);
		this.set_region(row, col, r);
	}

	private void add_animal(Animal a) {
		_animals.add(a);
		_reg_mngr.register_animal(a);
		
		_observers.forEach((o) -> o.onAnimalAdded(get_time(), get_map_info(), get_animals_info(), a));
	}

	public void add_animal(JSONObject a_json) {
		final Animal a;
		a = _animals_factory.create_instance(a_json);
		this.add_animal(a);
	}

	public MapInfo get_map_info() {
		return _reg_mngr;
	}

	public List<AnimalInfo> get_animals_info() {
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
		
		_observers.forEach((o) -> o.onAdvance(get_time(), get_map_info(), get_animals_info(), dt));
	}

	@Override
	public JSONObject as_JSON() {
		return new JSONObject().put("time", _time).put("state", _reg_mngr.as_JSON());
	}

	@Override
	public void addObserver(EcoSysObserver o) {
		_observers.add(o);
		o.onRegister(get_time(), get_map_info(), get_animals_info());
	}

	@Override
	public void removeObserver(EcoSysObserver o) {
		_observers.remove(o);
		
	}
}
