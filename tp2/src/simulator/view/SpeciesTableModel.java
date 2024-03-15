
public class SpeciesTableModel extends AbstractTableModel implements EcoSysObserver {
	List</*XD*/> _data;
	private static List<String> columnNames;

	SpeciesTableModel(Controller ctrl) {
		ctrl.addObserver(this);
	}

}
