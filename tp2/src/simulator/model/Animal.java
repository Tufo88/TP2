package simulator.model;

import org.json.JSONArray;
import org.json.JSONObject;

import simulator.misc.Utils;
import simulator.misc.Vector2D;

public abstract class Animal implements Entity, AnimalInfo {

	static final double _NEW_BABY_PROBABILITY = 0.9;

	static final double _SCALE_FACTOR = 60.0;

	static final double _RANDOM_TOLERANCE = 0.2;

	static final double _RANDOM_SPEED_TOLERANCE = 0.1;

	static final double _INITIAL_ENERGY = 100.0;

	static final double _MIN_ENERGY = 0;

	static final double _MAX_ENERGY = 100.0;

	static final double _MIN_DESIRE = 0;

	static final double _MAX_DESIRE = 100.0;

	protected String _genetic_code;
	protected Diet _diet;
	protected State _state;
	protected Vector2D _pos;
	protected Vector2D _dest;
	protected double _energy;
	protected double _speed;
	protected double _age;
	protected double _desire;
	protected double _sight_range;
	protected Animal _mate_target;
	protected Animal _baby;
	protected AnimalMapView _region_mngr;
	protected SelectionStrategy _mate_strategy;

	protected Animal(String genetic_code, Diet diet, double sight_range, double init_speed,
			SelectionStrategy mate_strategy, Vector2D pos) throws IllegalArgumentException {

		if (genetic_code.equals(""))
			throw new IllegalArgumentException("Genetic code is empty");
		if (sight_range < 0)
			throw new IllegalArgumentException("Negative sight range");
		if (init_speed < 0)
			throw new IllegalArgumentException("Negative initial speed");
		if (mate_strategy == null)
			throw new IllegalArgumentException("Mate strategy is null");

		_genetic_code = genetic_code;
		_diet = diet;
		_sight_range = sight_range;
		_speed = Utils.get_randomized_parameter(init_speed, Animal._RANDOM_SPEED_TOLERANCE);
		_mate_strategy = mate_strategy;
		_pos = pos;
		_state = State.NORMAL;
		_energy = _INITIAL_ENERGY;
		_desire = 0.0;
		_dest = null;
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
		_mate_strategy = p2._mate_strategy;
		_energy = (p1._energy + p2._energy) / 2;
		_pos = p1.get_position()
				.plus(Vector2D.get_random_vector(-1, 1).scale(_SCALE_FACTOR * (Utils._rand.nextGaussian() + 1)));
		_sight_range = Utils.get_randomized_parameter((p1.get_sight_range() + p2.get_sight_range()) / 2,
				Animal._RANDOM_TOLERANCE);
		_speed = Utils.get_randomized_parameter((p1.get_speed() + p2.get_speed()) / 2, Animal._RANDOM_TOLERANCE);
	}

	void init(AnimalMapView reg_mngr) {

		_region_mngr = reg_mngr;
		if (_pos == null) {
			_pos = Vector2D.get_random_vector(0, _region_mngr.get_width() - 1, 0, _region_mngr.get_height() - 1);
		} else {
			adjust_pos(_region_mngr.get_width(), _region_mngr.get_height());
		}
		changeRandomDest();
	}

	private void adjust_pos(int width, int height) {
		while (_pos.getX() >= width)
			_pos = _pos.minus(new Vector2D(width, 0));
		while (_pos.getX() < 0)
			_pos = _pos.plus(new Vector2D(width, 0));
		while (_pos.getY() >= height)
			_pos = _pos.minus(new Vector2D(0, height));
		while (_pos.getY() < 0)
			_pos = _pos.plus(new Vector2D(0, height));
	}

	private boolean isInsideRectangle(int x1, int x2, int y1, int y2) { // xi anchura yi altura
		return _pos.getX() <= x2 && _pos.getX() >= x1 && _pos.getY() <= y2 && _pos.getY() >= y1;
	}

	boolean isInMap() {
		return isInsideRectangle(0, _region_mngr.get_width() - 1, 0, _region_mngr.get_height() - 1);
	}

	void changeRandomDest() {
		_dest = Vector2D.get_random_vector(0, _region_mngr.get_width() - 1, 0, _region_mngr.get_height() - 1);
	}

	Animal deliver_baby() {
		Animal a = _baby;
		_baby = null;
		return a;
	}

	protected void move(double speed) {
		_pos = _pos.plus(_dest.minus(_pos).direction().scale(speed));
	}

	@Override
	public JSONObject as_JSON() {
		return new JSONObject()
				.put("pos", new JSONArray().put(this.get_position().getX()).put(this.get_position().getY()))
				.put("gcode", _genetic_code).put("diet", _diet.toString()).put("state", _state.toString());
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
		return new Vector2D(_dest);
	}

	@Override
	public boolean is_pregnant() {
		return _baby != null;
	}

	public final double distanceTo(Animal a) {
		return this._pos.distanceTo(a.get_position());
	}

	protected void setDesire(double x) {
		_desire = x;
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

	protected void matingLogic() {
		setDesire(_MIN_DESIRE);
		_mate_target.setDesire(_MIN_DESIRE);

		if (_baby == null && Utils._rand.nextDouble() < _NEW_BABY_PROBABILITY) {
			_baby = generateDescendency();
		}

		_mate_target = null;
	}

	public boolean isDead() {
		return _state == State.DEAD;
	}

	@Override
	public void update(double dt) {
		if (isDead())
			return;

		stateLogic(dt);

		_age += dt;

		adjustPosition();

		updateState();

		requestFood(dt);
	}

	protected abstract void requestFood(double dt);

	private void adjustPosition() {
		if (!isInMap()) {
			adjust_pos(_region_mngr.get_width(), _region_mngr.get_height());
			updateState(State.NORMAL);
		}
	}

	protected abstract void stateLogic(double dt);

	protected abstract Animal generateDescendency();

	protected abstract void updateState();

	protected abstract void updateState(State state);
}
