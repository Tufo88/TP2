package simulator.model;

public class DefaultRegion extends Region {
	static final double _FOOD_MAX_COEF = 2.0;
	static final double _FOOD_ANIMAL_CONSTANT = 5.0;
	static final double _FOOD_CONSUME_FACTOR = 60.0;
	@Override
	public double get_food(Animal a, double dt) {
		if(a.get_diet() == Diet.CARNIVORE) return 0.0;

		return _FOOD_CONSUME_FACTOR*Math.exp(-Math.max(0,getAnimals((Animal b) -> b.get_diet() == Diet.HERVIBORE).size() - _FOOD_ANIMAL_CONSTANT)*_FOOD_MAX_COEF)*dt;
	}

	@Override
	public void update(double dt) {
		
	}
	
}