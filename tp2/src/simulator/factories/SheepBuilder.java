package simulator.factories;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import simulator.misc.Vector2D;
import simulator.model.Animal;
import simulator.model.SelectFirst;
import simulator.model.SelectionStrategy;
import simulator.model.Sheep;

public class SheepBuilder extends Builder<Animal> {

	Factory<SelectionStrategy> _ss_factory;

	public SheepBuilder(Factory<SelectionStrategy> factory) {
		super("sheep", "Sheep");
		_ss_factory = factory;
	}

	@Override
	protected Animal create_instance(JSONObject data) throws IllegalArgumentException {

		JSONObject _mate_strat = data.optJSONObject("mate_strategy");
		JSONObject _danger_strat = data.optJSONObject("danger_strategy");

		SelectionStrategy mate_strategy = _mate_strat == null ? new SelectFirst()
				: _ss_factory.create_instance(_mate_strat);

		SelectionStrategy danger_strategy = _danger_strat == null ? new SelectFirst()
				: _ss_factory.create_instance(_danger_strat);

		JSONObject _pos = data.optJSONObject("pos");
		Vector2D pos = null;

		if (_pos != null) {
			JSONArray _posX = _pos.getJSONArray("x_range");
			JSONArray _posY = _pos.getJSONArray("y_range");
			try {
				pos = Vector2D.get_random_vector(_posX.getDouble(0), _posX.getDouble(1), _posY.getDouble(0),
						_posY.getDouble(1));
			} catch (JSONException e) {
				throw new IllegalArgumentException("An Invalid pos was provided");
			}

		}

		return new Sheep(mate_strategy, danger_strategy, pos);
	}

}
