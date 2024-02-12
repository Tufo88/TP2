package simulator.model;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import org.json.JSONObject;
import org.json.JSONArray;
public abstract class Region implements FoodSupplier, RegionInfo, Entity {

	protected List<Animal> list;
	
	final void add_animal(Animal a) {
		list.add(a);
	}
	
	final void remove_animal(Animal a) {
		list.remove(a);
	}
	
	final List<Animal> getAnimals() {
		return Collections.unmodifiableList(list);
	}
	
	public List<Animal> getAnimals(Predicate<? super Animal> predicate) {
		return Collections.unmodifiableList(getAnimals().stream().filter(predicate).toList());
	}
	public JSONObject as_JSON() {
	
		JSONObject obj = new JSONObject().append("animals", new JSONArray());
		
		for(Animal a: list) {
			obj.append("animals", a.as_JSON());
		}
		
		return obj;
		
	}
}
