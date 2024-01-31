package simulator.model;

import simulator.misc.Utils;
import simulator.misc.Vector2D;

public abstract class Animal implements Entity, AnimalInfo {
	
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
		if(genetic_code.equals("") || sight_range < 0 || init_speed < 0 || mate_strategy == null) 
			throw new Exception("Puta");
		
		_genetic_code = genetic_code;
		_diet = diet;
		_sight_range = sight_range;
		_speed = Utils.get_randomized_parameter(init_speed, 0.1);
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
	
	protected void move(double speed) {
		_pos = _pos.plus(_dest.minus(_pos).direction().scale(speed));
	}
	
	@Override
	public State get_state() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Vector2D get_position() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String get_genetic_code() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Diet get_diet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double get_speed() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double get_sight_range() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double get_energy() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double get_age() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Vector2D get_destination() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean is_pregnant() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public abstract void update(double dt);

}
