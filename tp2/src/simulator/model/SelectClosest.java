package simulator.model;

import java.util.List;

public class SelectClosest implements SelectionStrategy {

	@Override
	public Animal select(Animal a, List<Animal> as) {
		if (as.isEmpty())
			return null;

		Animal closest = as.get(0);
		double minDistance = closest.get_position().distanceTo(a.get_position());

		for (Animal i : as) {
			double actualDistance = i.get_position().distanceTo(a.get_position());
			if (actualDistance < minDistance) {
				closest = i;
				minDistance = actualDistance;
			}
		}
		return closest;
	}

}
