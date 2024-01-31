package simulator.model;

import simulator.misc.Vector2D;

public class Wolf extends Animal {

	static final double _INITIAL_SIGHT = 50.0;
	static final double _INITIAL_SPEED = 60.0;
	static final Diet _INITIAL_DIET = Diet.CARNIVORE; 
	static final double _ENERGY_COEF = 0.007;
	static final double _ENERGY_DECREASE_COEF = 18.0;
	static final double _DESIRE_INCREASE_COEF = 30.0;
	static final double _MAX_DESIRE = 100.0;
	static final double _MIN_ENERGY = 0;
	static final double _REACH_DEST_DIST = 8.0;
	
	Animal _hunt_target;
	SelectionStrategy _hunting_strategy;
	
	public Wolf(SelectionStrategy mate_strategy, SelectionStrategy hunting_strategy,
			Vector2D pos) throws Exception {
		super("paquito", _INITIAL_DIET,_INITIAL_SIGHT, _INITIAL_SPEED, mate_strategy, pos);
		
		_hunting_strategy = hunting_strategy;
		_hunt_target = null;
		
	}
	
	@Override
	public void update(double dt) {
		
		if (_state == State.NORMAL) {
			if (_pos.distanceTo(_dest) < _REACH_DEST_DIST) {
				_dest = Vector2D.get_random_vector(0, 243);
			}
			
			this.move(_speed*dt*Math.exp((_energy-Animal._INITIAL_ENERGY)*_ENERGY_COEF));
			_age += dt;
			
			_energy -= ((_energy - _ENERGY_DECREASE_COEF * dt) < _MIN_ENERGY ? 
						_energy : 
						_ENERGY_DECREASE_COEF * dt);
			_desire += ((_desire + _DESIRE_INCREASE_COEF * dt) > _MAX_DESIRE ? 
						_MAX_DESIRE - _desire : 
						_DESIRE_INCREASE_COEF * dt);
		}
		
	}

}
