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
		double _factor = data.optDouble("factor");
		double _food = data.optDouble("food");
		double factor = Double.isNaN(_factor) ? _DEFAULT_FACTOR : _factor;
		double food = Double.isNaN(_food) ? _DEFAULT_FOOD : _food;
		
		return new DynamicSupplyRegion(factor, food);
	}

}
