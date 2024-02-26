package simulator.control;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import simulator.model.AnimalInfo;
import simulator.model.MapInfo;
import simulator.model.Simulator;
import simulator.view.SimpleObjectViewer.ObjInfo;
import simulator.view.SimpleObjectViewer;

public class Controller {

	Simulator _sim;

	public Controller(Simulator sim) {
		_sim = sim;
	}

	public void load_data(JSONObject data) {
		JSONArray regions = data.optJSONArray("regions");
		if (regions != null) {
			for (Object obj : regions) {

				JSONObject jObj = (JSONObject) obj;

				JSONArray row = jObj.getJSONArray("row");
				int rf = row.getInt(0), rt = row.getInt(1);

				JSONArray col = jObj.getJSONArray("row");
				int cf = col.getInt(0), ct = col.getInt(1);

				JSONObject spec = jObj.getJSONObject("spec");

				for (int i = rf; i <= rt; i++) {
					for (int j = cf; j <= ct; j++) {
						_sim.set_region(i, j, spec);
					}
				}
			}

		}

		JSONArray animals = data.getJSONArray("animals");
		for (Object obj : animals) {
			JSONObject animal = (JSONObject) obj;

			for (int i = 0; i < animal.getInt("amount"); i++)
				_sim.add_animal(animal.getJSONObject("spec"));
		}
	}

	public void run(double t, double dt, boolean sv, OutputStream out) {
		JSONObject obj = new JSONObject().append("in", _sim.as_JSON());

		SimpleObjectViewer view = null;
		if (sv) {
			MapInfo m = _sim.get_map_info();
			view = new SimpleObjectViewer("[ECOSYSTEM]", m.get_width(), m.get_height(), m.get_cols(), m.get_rows());
			view.update(to_animals_info(_sim.get_animals()), _sim.get_time(), dt);
		}

		while (_sim.get_time() <= t) {
			_sim.advance(dt);
			if (sv)
				view.update(to_animals_info(_sim.get_animals()), _sim.get_time(), dt);

		}
		if (sv)
			view.close();
		obj.append("out", _sim.as_JSON());
		PrintStream p = new PrintStream(out);
		p.println(obj);

	}

	private List<ObjInfo> to_animals_info(List<? extends AnimalInfo> animals) {

		List<ObjInfo> ol = new ArrayList<>(animals.size());
		for (AnimalInfo a : animals)
			ol.add(new ObjInfo(a.get_genetic_code(), (int) a.get_position().getX(), (int) a.get_position().getY(), 8));
		return ol;
	}

}
