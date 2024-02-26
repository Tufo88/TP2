package simulator.factories;

import org.json.JSONObject;

import simulator.model.SelectYoungest;

public class SelectYoungestBuilder extends Builder<SelectYoungest> {
	public SelectYoungestBuilder(String type_tag) {
		super(type_tag, "Select Youngest Strategy");
		
	}

	@Override
	protected SelectYoungest create_instance(JSONObject data) {
		
		return new SelectYoungest();
	}
}
