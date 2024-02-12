package simulator.model;

import simulator.misc.Vector2D;

public class Sheep extends Animal {
	
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
	Animal _danger_source;
	SelectionStrategy _danger_strategy;
	
	public Sheep(SelectionStrategy mate_strategy, SelectionStrategy danger_strategy,
			Vector2D pos) throws Exception {
		super("Sheep", _INITIAL_DIET,_INITIAL_SIGHT, _INITIAL_SPEED, mate_strategy, pos);
		
		_danger_strategy = danger_strategy;
		_danger_source = null;
		
	}

	protected Sheep(Sheep p1, Animal p2) {
		super(p1, p2);
		_danger_strategy = p1._danger_strategy;
		_danger_source = null;
	}
	
	@Override
	public void update(double dt) {
		if(_state == State.DEAD) return;
		
		if (_state == State.NORMAL) { //1
			normal_state_update(dt);
		} 
		
		else if(_state == State.DANGER) {
			if(_danger_source != null && _danger_source._state == State.DEAD) _danger_source = null;
			if(_danger_source == null) {
				normal_state_update(dt);
			} else {
				_pos.plus(_pos.minus(_danger_source.get_position()).direction());
				move(_SPEED_FACTOR*_speed*dt*Math.exp((_energy - _MAX_ENERGY)* _ENERGY_COEF));
				_age += dt;
				updateEnergy(-_ENERGY_DECREASE_COEF * _ENERGY_DANGER_COEF * dt);
				updateDesire(_DESIRE_INCREASE_COEF * dt);
			}
		}
		
		else if (_state == State.MATE) {
			if (_mate_target != null && 
			   (_mate_target._state == State.DEAD || !isInSight(_mate_target)))
					_mate_target = null;
			if (_mate_target == null) {
				_mate_target = _mate_strategy.select(this, _region_mngr.get_animals_in_range(this, (Animal a) -> a._diet == Diet.HERVIBORE));
				if (_mate_target == null) normal_state_update(dt);
				else {
					_dest = _mate_target.get_position();
					move(_SPEED_FACTOR*_speed*dt*Math.exp((_energy - _MAX_ENERGY)* _ENERGY_COEF));
					_age += dt;
					updateEnergy(-_ENERGY_DECREASE_COEF * _ENERGY_MATE_COEF * dt);
					updateDesire(_DESIRE_INCREASE_COEF * dt);
					if (_pos.distanceTo(_dest) < _REACH_DEST_DIST) {
						matingLogic();
					}
				}
			}
		}
		
		updateState(); 
		
	}
	
	public void updateState() {
		if(_danger_source == null || !isInSight(_danger_source)) {
			_danger_source = _danger_strategy.select(this, _region_mngr.get_animals_in_range(this, (Animal a) -> a._diet == Diet.CARNIVORE));
		}
		if (_danger_source != null) {
			_state = State.DANGER;
		}
		else if (_desire < _MIN_DESIRE_UPDATE_VALUE){
			_state = State.NORMAL;
		}  else _state = State.MATE;
	}
	
	private void normal_state_update(double dt) {
		if (_pos.distanceTo(_dest) < _REACH_DEST_DIST) {
			_dest = Vector2D.get_random_vector(0, 243);
		}
		
		this.move(_speed*dt*Math.exp((_energy-Animal._INITIAL_ENERGY)*_ENERGY_COEF));
		_age += dt;
		
		updateEnergy(-_ENERGY_DECREASE_COEF * dt);
		updateDesire(_DESIRE_INCREASE_COEF * dt);
	}

	@Override
	protected Animal generateDescendency() {
		return new Sheep(this, _mate_target);
	}
}
