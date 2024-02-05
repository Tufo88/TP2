package simulator.model;

import org.json.JSONObject;

import simulator.misc.Utils;
import simulator.misc.Vector2D;

public abstract class Animal implements Entity, AnimalInfo {
	
	static final double _SCALE_FACTOR = 60.0;

	static final double _RANDOM_TOLERANCE = 0.2;

	static final double _RANDOM_SPEED_TOLERANCE = 0.1;

	static final double _INITIAL_ENERGY = 100.0;

	static final double _MIN_ENERGY = 0;

	static final double _MAX_ENERGY = 100.0;
	
	static final double _MIN_DESIRE = 0;

	static final double _MAX_DESIRE = 100.0;
	
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
		_speed = Utils.get_randomized_parameter(init_speed, Animal._RANDOM_SPEED_TOLERANCE);
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
		_pos = p1.get_position().plus(Vector2D.get_random_vector(-1,1).scale(_SCALE_FACTOR*(Utils._rand
				.nextGaussian()+1)));
		_sight_range = Utils.
				get_randomized_parameter(
				(p1.get_sight_range()+p2.get_sight_range())/2, Animal._RANDOM_TOLERANCE);
		_speed = Utils.
				get_randomized_parameter(
				(p1.get_speed()+p2.get_speed())/2, Animal._RANDOM_TOLERANCE);		
	}
	
	void init(AnimalMapView reg_mngr) {
		
		_region_mngr = reg_mngr;
		if (_pos == null) {
			_pos = Vector2D.get_random_vector(_region_mngr.get_width()  -1, _region_mngr.get_height() - 1);
		} else {
			_pos = Vector2D.adjust_vector(_pos, _region_mngr.get_width()  -1, _region_mngr.get_height() - 1);
		}
		_dest = Vector2D.get_random_vector(_region_mngr.get_width()  -1, _region_mngr.get_height() - 1);
		
	}
	
	Animal deliver_baby() {
		Animal a = _baby;
		_baby = null;
		return a;
	}

	
	protected void move(double speed) {
		_pos = _pos.plus(_dest.minus(_pos).direction().scale(speed));
	}
	
	public JSONObject as_JSON() {
		return new JSONObject().put("pos", _pos.toString())
				.put("gcode", _genetic_code)
				.put("diet", _diet.toString())
				.put("state", _state.toString());
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

	protected void updateEnergy(double x) {
		_energy += x;
		_energy = Utils.constrain_value_in_range(_energy, _MIN_ENERGY, _MAX_ENERGY);
		
	}
	
	protected void updateDesire(double x) {
		_desire += x;
		_desire = Utils.constrain_value_in_range(_desire, _MIN_DESIRE, _MAX_DESIRE);
	}
	
	protected boolean isInSight(Animal a) {
		return _pos.distanceTo(a._pos) <= _sight_range;
	}
	
	protected void modoSexo(Animal a) {
		_desire = a._desire = _MIN_DESIRE;
		if (_baby == null && ) {
		
		}
	}
	
	@Override
	public abstract void update(double dt);

}
