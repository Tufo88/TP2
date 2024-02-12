package simulator.model;

public class DefaultRegion extends Region {
	
	@Override
	public double get_food(Animal a, double dt) {
		if(a.get_diet() == Diet.CARNIVORE) return 0.0;

		return 60.0*Math.exp(-Math.max(0,getAnimals((Animal b) -> b.get_diet() == Diet.HERVIBORE).size() - 5.0)*2.0)*dt;
	}

	@Override
	public void update(double dt) {
		
	}
	
}