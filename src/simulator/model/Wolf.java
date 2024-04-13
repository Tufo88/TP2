package simulator.model;

import simulator.misc.Vector2D;

public class Wolf extends Animal {

	static final double _MATING_ENERGY_DECREASE = 10.0;
	static final double _ENERGY_MATE_COEF = 1.2;
	static final double _HUNT_REGEN = 50.0;
	static final double _SPEED_FACTOR = 3.0;
	static final double _INITIAL_SIGHT = 50.0;
	static final double _INITIAL_SPEED = 60.0;
	static final Diet _INITIAL_DIET = Diet.CARNIVORE;
	static final double _ENERGY_COEF = 0.007;
	static final double _ENERGY_DECREASE_COEF = 18.0;
	static final double _DESIRE_INCREASE_COEF = 30.0;
	static final double _MAX_DESIRE = 100.0;
	static final double _MIN_ENERGY = 0;
	static final double _MIN_ENERGY_STATE_UPDATE = 50.0;
	static final double _REACH_DEST_DIST = 8.0;
	static final double _MIN_DESIRE_UPDATE_VALUE = 65.0;
	static final double _MAX_ENERGY = 100.0;
	static final double _ENERGY_HUNGER_COEF = 1.2;
	static final double _MAX_AGE = 14.0;

	protected Animal _hunt_target;
	protected SelectionStrategy _hunting_strategy;

	public Wolf(SelectionStrategy mate_strategy, SelectionStrategy hunting_strategy, Vector2D pos)
			throws IllegalArgumentException {
		super("Wolf", _INITIAL_DIET, _INITIAL_SIGHT, _INITIAL_SPEED, mate_strategy, pos);
		if (hunting_strategy == null)
			throw new IllegalArgumentException("Hunt strategy is null");
		_hunting_strategy = hunting_strategy;
		_hunt_target = null;

	}

	protected Wolf(Wolf p1, Animal p2) {
		super(p1, p2);
		_hunting_strategy = p1._hunting_strategy;
		_hunt_target = null;
	}

	@Override
	protected Animal generateDescendency() {
		return new Wolf(this, _mate_target);
	}

	@Override
	protected void updateState(State state) {
		if (state == State.NORMAL) {
			_hunt_target = null;
			_mate_target = null;
		} else if (state == State.MATE) {
			_hunt_target = null;
		} else if (state == State.HUNGER) {
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
		} else if (_energy < _MIN_ENERGY_STATE_UPDATE)
			updateState(State.HUNGER);
		else if (_desire < _MIN_DESIRE_UPDATE_VALUE)
			updateState(State.NORMAL);
		else
			updateState(State.MATE);

	}

	protected void normal_state_update(double dt) {
		if (this.get_position().distanceTo(this.get_destination()) < _REACH_DEST_DIST) {
			changeRandomDest();
		}

		this.move(this.get_speed() * dt * Math.exp((this.get_energy() - Animal._INITIAL_ENERGY) * _ENERGY_COEF));

		updateEnergy(-_ENERGY_DECREASE_COEF * dt);
		updateDesire(_DESIRE_INCREASE_COEF * dt);
	}

	private void hunger_state_update(double dt) {
		if (_hunt_target == null || _hunt_target.isDead() || !isInSight(_hunt_target)) {
			_hunt_target = _hunting_strategy.select(this,
					_region_mngr.get_animals_in_range(this, (Animal a) -> a.get_diet() == Diet.HERVIBORE));
		}

		if (_hunt_target == null) {
			normal_state_update(dt);
		} else {
			_dest = _hunt_target.get_position();
			this.move(_SPEED_FACTOR * this.get_speed() * dt
					* Math.exp((this.get_energy() - Animal._INITIAL_ENERGY) * _ENERGY_COEF));

			updateEnergy(-_ENERGY_HUNGER_COEF * _ENERGY_DECREASE_COEF * dt);
			updateDesire(_DESIRE_INCREASE_COEF * dt);

			if (_pos.distanceTo(this.get_destination()) < _REACH_DEST_DIST) {
				_hunt_target._state = State.DEAD;
				_hunt_target = null;
				updateEnergy(_HUNT_REGEN);
			}
		}
	}

	private void mate_state_update(double dt) {
		if (_mate_target != null && (_mate_target.isDead() || !isInSight(_mate_target)))
			_mate_target = null;
		if (_mate_target == null)
			_mate_target = _mate_strategy.select(this, _region_mngr.get_animals_in_range(this,
					(Animal a) -> a.get_genetic_code() == this.get_genetic_code()));

		if (_mate_target == null)
			normal_state_update(dt);
		else {
			_dest = _mate_target.get_position();
			move(_SPEED_FACTOR * this.get_speed() * dt * Math.exp((this.get_energy() - _MAX_ENERGY) * _ENERGY_COEF));
			updateEnergy(-_ENERGY_DECREASE_COEF * _ENERGY_MATE_COEF * dt);
			updateDesire(_DESIRE_INCREASE_COEF * dt);
			if (this.get_position().distanceTo(this.get_destination()) < _REACH_DEST_DIST) {
				matingLogic();
				updateEnergy(-_MATING_ENERGY_DECREASE);
			}
		}
	}

	@Override
	protected void requestFood(double dt) {
		if (!isDead()) {
			updateEnergy(_region_mngr.get_food(this, dt));
		}
	}

	@Override
	protected void stateLogic(double dt) {
		switch(this.get_state()) {
			case NORMAL:
				normal_state_update(dt);
				break;
			case HUNGER:
				hunger_state_update(dt);
				break;
			case MATE:
				mate_state_update(dt);
				break;
			default:
				break;
		}
	}
}
