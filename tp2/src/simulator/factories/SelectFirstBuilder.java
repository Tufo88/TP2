package simulator.factories;

import org.json.JSONObject;

import simulator.model.SelectFirst;
import simulator.model.SelectionStrategy;

public class SelectFirstBuilder extends Builder<SelectionStrategy> {

	public SelectFirstBuilder(String type_tag) {
		super(type_tag, "Select First Strategy");
		
	}

	@Override
	protected SelectionStrategy create_instance(JSONObject data) {
		
		return new SelectFirst();
	}

}
