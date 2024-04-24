package simulator.factories;

import org.json.JSONObject;

import simulator.model.DynamicSupplyRegion;
import simulator.model.Region;

public class DynamicSupplyRegionBuilder extends Builder<Region> {

	static final double _DEFAULT_FOOD = 1000.0;
	static final double _DEFAULT_FACTOR = 2.0;

	public DynamicSupplyRegionBuilder() {
		super("dynamic", "Dynamic Supply Region");

	}

	@Override
	protected Region create_instance(JSONObject data) throws IllegalArgumentException {
		double factor = data.optDouble("factor", _DEFAULT_FACTOR);
		double food = data.optDouble("food", _DEFAULT_FOOD);

		return new DynamicSupplyRegion(food, factor);
	}

	@Override
	protected void fill_in_data(JSONObject o) {
		o.put("factor", "food increase factor (optional, default " + _DEFAULT_FACTOR);
		o.put("food", "initial amount of food (optional, default " + _DEFAULT_FOOD);
	}
}
