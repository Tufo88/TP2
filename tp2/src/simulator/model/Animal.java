package simulator.model;

import simulator.misc.Utils;
import simulator.misc.Vector2D;

public abstract class Animal implements Entity, AnimalInfo {
	
	private static final double OTRO_NUMERIN = 0.2;

	static final double NOSEBRO = 0.1;

	static final double _INITIAL_ENERGY = 100.0;
	
	String _genetic_code;
	Diet _diet;
	State _state;
	Vector2D _pos;
	Vector2D _dest;
	double _energy;
	double _speed;
	double _age;
	double _desire;
	double _sight_range;
	Animal _mate_target;
	Animal _baby;
	AnimalMapView _region_mngr;
	SelectionStrategy _mate_strategy;
	
	protected Animal(String genetic_code, Diet diet, double sight_range, 
			double init_speed, SelectionStrategy mate_strategy, Vector2D pos) throws Exception {
		/*if(genetic_code.equals("") || sight_range < 0 || init_speed < 0 || mate_strategy == null) 
			throw new Exception("A");*/
		
		_genetic_code = genetic_code;
		_diet = diet;
		_sight_range = sight_range;
		_speed = Utils.get_randomized_parameter(init_speed, Animal.NOSEBRO);
		_mate_strategy= mate_strategy;
		_pos = pos;
		_state = State.NORMAL;
		_energy = _INITIAL_ENERGY;
		_desire = 0;
		//_dest = null;
		double x = Utils._rand.nextDouble(800);
		double y = Utils._rand.nextDouble(600);
		Vector2D v = new Vector2D(x, y);
		_dest = v;
		_mate_target = null;
		_baby = null;
		_region_mngr = null;
	}
	
	protected Animal(Animal p1, Animal p2) {
		_dest = null;
		_baby = null;
		_mate_target = null;
		_region_mngr = null;
		_state = State.NORMAL;
		_desire = 0.0;
		_genetic_code = p1._genetic_code;
		_diet = p1._diet;
		_energy = (p1._energy + p2._energy) / 2;
		_pos = p1.get_position().plus(Vector2D.get_random_vector(-1,1).scale(60.0*(Utils._rand
				.nextGaussian()+1)));
		_sight_range = Utils.
				get_randomized_parameter(
				(p1.get_sight_range()+p2.get_sight_range())/2, Animal.OTRO_NUMERIN);
		_speed = Utils.
				get_randomized_parameter(
				(p1.get_speed()+p2.get_speed())/2, Animal.OTRO_NUMERIN);		
	}
	
	void init(AnimalMapView reg_mngr) {
		
		_region_mngr = reg_mngr;
		if (_pos == null) {
			double x, y;
			//TODO
		}
		
	}
	
	Animal deliver_baby() {
		Animal a = _baby;
		_baby = null;
		return a;
	}

	
	protected void move(double speed) {
		_pos = _pos.plus(_dest.minus(_pos).direction().scale(speed));
	}
	
	protected final boolean isOutOfBounds() {
		return _pos.isInsideRectangle(0, 800, 0, 600);
	}
	
	@Override
	public State get_state() {
		return _state;
	}

	@Override
	public Vector2D get_position() {
		return new Vector2D(_pos);
	}

	@Override
	public String get_genetic_code() {
		return _genetic_code;
	}

	@Override
	public Diet get_diet() {
		return _diet;
	}

	@Override
	public double get_speed() {
		return _speed;
	}

	@Override
	public double get_sight_range() {
		return _sight_range;
	}

	@Override
	public double get_energy() {
		return _energy;
	}

	@Override
	public double get_age() {
		return _age;
	}

	@Override
	public Vector2D get_destination() {
		return _dest; // hay que pasar copia o referencia ??
	}

	@Override
	public boolean is_pregnant() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public abstract void update(double dt);

}
