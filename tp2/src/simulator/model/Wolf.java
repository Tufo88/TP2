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
	static final double _MIN_ENERGY_STATE_UPDATE= 50.0;
	static final double _REACH_DEST_DIST = 8.0;
	static final double _MIN_DESIRE_UPDATE_VALUE = 65.0;
	static final double _MAX_ENERGY = 100.0;
	static final double _ENERGY_HUNGER_COEF = 1.2;
	
	
	Animal _hunt_target;
	SelectionStrategy _hunting_strategy;
	
	public Wolf(SelectionStrategy mate_strategy, SelectionStrategy hunting_strategy,
			Vector2D pos) throws Exception {
		super("Wolfangkillers", _INITIAL_DIET,_INITIAL_SIGHT, _INITIAL_SPEED, mate_strategy, pos);
		
		_hunting_strategy = hunting_strategy;
		_hunt_target = null;
		
	}
	
	@Override
	public void update(double dt) {
		
		if (_state == State.NORMAL) { 
			normal_state_update(dt);
		}
		else if (_state == State.HUNGER) {
			if (_hunt_target == null || _hunt_target._state == State.DEAD || !isInSight(_hunt_target)) {
				_hunt_target = _hunting_strategy.select(this, _region_mngr.get_animals_in_range(this, (Animal a) -> a._diet == Diet.HERVIBORE));
			}
			if (_hunt_target == null) {
				normal_state_update(dt);
			}
			else {
				_dest = _hunt_target.get_position();
				this.move(_SPEED_FACTOR*_speed*dt*Math.exp((_energy-Animal._INITIAL_ENERGY)*_ENERGY_COEF));
				_age += dt;
				
				updateEnergy(-_ENERGY_HUNGER_COEF*_ENERGY_DECREASE_COEF * dt);
				updateDesire(_DESIRE_INCREASE_COEF * dt);
				
				if (_pos.distanceTo(_dest) < _REACH_DEST_DIST) {
					_hunt_target._state = State.DEAD;
					_hunt_target = null;
					updateEnergy(_HUNT_REGEN);
				}
			}
			
		}
		else if (_state == State.MATE) {
			
			if (_mate_target != null && 
					   (_mate_target._state == State.DEAD || !isInSight(_mate_target)))
							_mate_target = null;
					if (_mate_target == null) {
						_mate_target = _mate_strategy.select(this, _region_mngr.get_animals_in_range(this, (Animal a) -> a._diet == Diet.CARNIVORE));
						if (_mate_target == null) normal_state_update(dt);
						else {
							_dest = _mate_target.get_position();
							move(_SPEED_FACTOR*_speed*dt*Math.exp((_energy - _MAX_ENERGY)* _ENERGY_COEF));
							_age += dt;
							updateEnergy(-_ENERGY_DECREASE_COEF * _ENERGY_MATE_COEF * dt);
							updateDesire(_DESIRE_INCREASE_COEF * dt);
							if (_pos.distanceTo(_dest) < _REACH_DEST_DIST) {
								matingLogic();
								updateEnergy(-_MATING_ENERGY_DECREASE);
							}
						}
					}
			
		}
		
				
		updateState();
	}

	@Override
	protected Animal generateDescendency() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateState() {
		if (_energy < _MIN_ENERGY_STATE_UPDATE) _state = State.HUNGER;
		else if (_desire < _MIN_DESIRE_UPDATE_VALUE) _state = State.NORMAL;
		else _state = State.MATE;
		
	}
	
	protected void normal_state_update(double dt) {
		if (_pos.distanceTo(_dest) < _REACH_DEST_DIST) {
			_dest = Vector2D.get_random_vector(0, 243);
		}
		
		this.move(_speed*dt*Math.exp((_energy-Animal._INITIAL_ENERGY)*_ENERGY_COEF));
		_age += dt;
		
		updateEnergy(-_ENERGY_DECREASE_COEF * dt);
		updateDesire(_DESIRE_INCREASE_COEF * dt);
	}

}
