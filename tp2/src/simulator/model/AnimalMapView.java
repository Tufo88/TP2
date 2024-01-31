package simulator.model;

import java.util.List;
import java.util.function.Predicate;

public interface AnimalMapView {
	public List<Animal> get_animals_in_range(Animal e, Predicate<Animal> filter);
}
