package simulator.factories;

import org.json.JSONObject;

import simulator.model.SelectFirst;

public class SelectFirstBuilder extends Builder<SelectFirst> {

	public SelectFirstBuilder(String type_tag) {
		super(type_tag, "Select First Strategy");
		
	}

	@Override
	protected SelectFirst create_instance(JSONObject data) {
		
		return new SelectFirst();
	}

}
