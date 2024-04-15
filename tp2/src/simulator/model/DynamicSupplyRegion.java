package simulator.model;

import simulator.misc.Utils;

public class DynamicSupplyRegion extends Region {
	static final double _FOOD_MAX_COEF = 2.0;
	static final double _FOOD_ANIMAL_CONSTANT = 5.0;
	static final double _FOOD_CONSUME_FACTOR = 60.0;
	private double _food;
	private double _factor;

	public DynamicSupplyRegion(double foodAmount, double growRate) {
		_food = foodAmount;
		_factor = growRate;
	}

	@Override
	public double get_food(Animal a, double dt) {
		if (a.get_diet() == Diet.CARNIVORE)
			return 0.0;

		double amount = Math.min(_food, _FOOD_CONSUME_FACTOR * Math.exp(
				-Math.max(0, getAnimals((Animal b) -> b.get_diet() == Diet.HERVIBORE).size() - _FOOD_ANIMAL_CONSTANT)
						* _FOOD_MAX_COEF)
				* dt);
		_food -= amount;

		return amount;
	}

	@Override
	public void update(double dt) {
		if (Utils._rand.nextDouble() <= 0.5) {
			_food += dt * _factor;
		}
	}

	@Override
	public String toString() {
		return "dynamic";
	}

}
