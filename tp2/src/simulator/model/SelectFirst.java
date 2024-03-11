package simulator.model;

import java.util.List;

public class SelectFirst implements SelectionStrategy {

	@Override
	public Animal select(Animal a, List<Animal> as) {
		return !as.isEmpty() ? as.get(0) : null;
	}
}
