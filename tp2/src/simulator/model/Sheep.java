package simulator.model;

import simulator.misc.Vector2D;

public class Sheep extends Animal {

	static final double _MAX_AGE = 8.0;
	static final double _ENERGY_MATE_COEF = 1.2;
	static final double _MIN_DESIRE_UPDATE_VALUE = 65.0;
	static final double _INITIAL_SIGHT = 40.0;
	static final double _INITIAL_SPEED = 35.0;
	static final Diet _INITIAL_DIET = Diet.HERVIBORE;
	static final double _ENERGY_COEF = 0.007;
	static final double _ENERGY_DANGER_COEF = 1.2;
	static final double _ENERGY_DECREASE_COEF = 20.0;
	static final double _DESIRE_INCREASE_COEF = 40.0;
	static final double _MAX_DESIRE = 100.0;
	static final double _MAX_ENERGY = 100.0;
	static final double _REACH_DEST_DIST = 8.0;
	static final double _SPEED_FACTOR = 2.0;

	protected Animal _danger_source;
	protected SelectionStrategy _danger_strategy;

	public Sheep(SelectionStrategy mate_strategy, SelectionStrategy danger_strategy, Vector2D pos)
			throws IllegalArgumentException {
		super("Sheep", _INITIAL_DIET, _INITIAL_SIGHT, _INITIAL_SPEED, mate_strategy, pos);
		
		if (danger_strategy == null)
			throw new IllegalArgumentException("Danger strategy is null");
		_danger_strategy = danger_strategy;
		_danger_source = null;

	}

	protected Sheep(Sheep p1, Animal p2) {
		super(p1, p2);
		_danger_strategy = p1._danger_strategy;
		_danger_source = null;
	}

	@Override
	protected void updateState(State state) {
		if (state == State.NORMAL) {
			_danger_source = null;
			_mate_target = null;
		} else if (state == State.MATE) {
			_danger_source = null;
		} else if (state == State.DANGER) {
			_mate_target = null;
		}
		_state = state;
	}

	@Override
	public void updateState() {
		if (isDead())
			return;
		if (_energy <= 0.0 || _age > _MAX_AGE) {
			updateState(State.DEAD);
		} else {
			if (_danger_source == null || !isInSight(_danger_source)) {
				_danger_source = _danger_strategy.select(this,
						_region_mngr.get_animals_in_range(this, (Animal a) -> a.get_diet() == Diet.CARNIVORE));
			}
			if (_danger_source != null) {
				updateState(State.DANGER);
			} else if (_desire < _MIN_DESIRE_UPDATE_VALUE) {
				updateState(State.NORMAL);
			} else
				updateState(State.MATE);
		}

	}

	private void normal_state_update(double dt) {
		if (this.get_position().distanceTo(this.get_destination()) < _REACH_DEST_DIST) {
			changeRandomDest();
		}

		this.move(this.get_speed() * dt * Math.exp((this.get_energy() - Animal._INITIAL_ENERGY) * _ENERGY_COEF));

		updateEnergy(-_ENERGY_DECREASE_COEF * dt);
		updateDesire(_DESIRE_INCREASE_COEF * dt);
	}

	private void danger_state_update(double dt) {
		if (_danger_source != null && _danger_source.isDead())
			_danger_source = null;

		if (_danger_source == null) {
			normal_state_update(dt);
		} else {
			_dest = _pos.plus(_pos.minus(_danger_source.get_position()).direction());
			move(_SPEED_FACTOR * this.get_speed() * dt * Math.exp((this.get_energy() - _MAX_ENERGY) * _ENERGY_COEF));
			updateEnergy(-_ENERGY_DECREASE_COEF * _ENERGY_DANGER_COEF * dt);
			updateDesire(_DESIRE_INCREASE_COEF * dt);
		}
	}

	private void mate_state_update(double dt) {
		if (_mate_target != null && (_mate_target.isDead() || !isInSight(_mate_target)))
			_mate_target = null;

		if (_mate_target == null)
			_mate_target = _mate_strategy.select(this, _region_mngr.get_animals_in_range(this,
					(Animal a) -> a.get_genetic_code() == this.get_genetic_code()));

		if (_mate_target == null) {
			normal_state_update(dt);
		}

		else {
			_dest = _mate_target.get_position();

			move(_SPEED_FACTOR * this.get_speed() * dt * Math.exp((this.get_energy() - _MAX_ENERGY) * _ENERGY_COEF));
			updateEnergy(-_ENERGY_DECREASE_COEF * _ENERGY_MATE_COEF * dt);
			updateDesire(_DESIRE_INCREASE_COEF * dt);
			if (this.get_position().distanceTo(this.get_destination()) < _REACH_DEST_DIST) {
				matingLogic();
			}
		}

	}

	@Override
	protected Animal generateDescendency() {
		return new Sheep(this, _mate_target);
	}

	@Override
	protected void requestFood(double dt) {
		if (!isDead()) {
			updateEnergy(_region_mngr.get_food(this, dt));
		}
	}

	@Override
	protected void stateLogic(double dt) {
		if (this.get_state() == State.NORMAL) {
			normal_state_update(dt);
		} else if (this.get_state() == State.DANGER) {
			danger_state_update(dt);
		} else if (_state == State.MATE) {
			mate_state_update(dt);
		}
	}
}
