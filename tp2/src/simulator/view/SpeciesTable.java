package simulator.view;

import java.util.List;

import javax.swing.JTable;
import javax.swing.table.TableColumn;

import simulator.model.State;
public class SpeciesPanel extends JPanel {
		
	class SpeciesTable extends AbstractTableModel {
		private static List<String> columnNames;

		
	}
	
	private void iniColumnNames() {
		columnNames.add("Species");
		for (State st : State.values()) {
			columnNames.add(st.name());
		}
	}
	
	public SpeciesTable() {
		iniColumnNames();
		for (String col : columnNames) {
			TableColumn tc = new TableColumn();
			tc.setHeaderValue(col);
			this.getColumnModel().addColumn(tc);			
		}
	}
}
