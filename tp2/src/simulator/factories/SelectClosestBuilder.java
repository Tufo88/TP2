package simulator.factories;

import org.json.JSONObject;

import simulator.model.SelectClosest;

public class SelectClosestBuilder extends Builder<SelectClosest> {
	public SelectClosestBuilder(String type_tag) {
		super(type_tag, "Select Closest Strategy");
		
	}

	@Override
	protected SelectClosest create_instance(JSONObject data) {
		
		return new SelectClosest();
	}
}

