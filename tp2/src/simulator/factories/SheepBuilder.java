package simulator.factories;

import org.json.JSONObject;

import simulator.model.Animal;
import simulator.model.SelectionStrategy;
import simulator.model.Sheep;

public class SheepBuilder extends Builder<Animal> {

	public SheepBuilder(String type_tag) {
		super(type_tag, "Sheep");
	}

	@Override
	protected Animal create_instance(JSONObject data) {
		BuilderBasedFactory BF = new BuilderBasedFactory();
		JSONObject _mate_strat = data.optJSONObject("mate_strategy");
		JSONObject _danger_strat = data.optJSONObject("danger_strategy");
		
		SelectionStrategy mate_strategy = null;
		return null;
	}

}
