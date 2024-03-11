package simulator.model;

import java.util.List;

public class SelectClosest implements SelectionStrategy {

	@Override
	public Animal select(Animal a, List<Animal> as) {
		if (as.isEmpty())
			return null;

		Animal closest = as.get(0);
		double minDistance = a.distanceTo(closest);

		for (Animal i : as) {
			double actualDistance = a.distanceTo(i);
			if (actualDistance < minDistance) {
				closest = i;
				minDistance = actualDistance;
			}
		}
		return closest;
	}

}
