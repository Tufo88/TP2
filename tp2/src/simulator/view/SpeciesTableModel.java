
public class SpeciesTableModel extends AbstractTableModel implements EcoSysObserver {
	Map<String, Map<State, Integer>> _data;//mapa de genetic code a mapa de state a num de animales en ese state
	
	private static List<String> columnNames;

	SpeciesTableModel(Controller ctrl) {
		ctrl.addObserver(this);
	}

}
