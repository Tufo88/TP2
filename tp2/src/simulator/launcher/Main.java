package simulator.launcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.json.JSONObject;
import org.json.JSONTokener;

import simulator.control.Controller;
import simulator.factories.Builder;
import simulator.factories.BuilderBasedFactory;
import simulator.factories.DefaultRegionBuilder;
import simulator.factories.DynamicSupplyRegionBuilder;
import simulator.factories.Factory;
import simulator.factories.SelectClosestBuilder;
import simulator.factories.SelectFirstBuilder;
import simulator.factories.SelectYoungestBuilder;
import simulator.factories.SheepBuilder;
import simulator.factories.WolfBuilder;
import simulator.misc.Utils;
import simulator.model.Animal;
import simulator.model.Region;
import simulator.model.SelectionStrategy;
import simulator.model.Simulator;
import simulator.view.MainWindow;

public class Main {

	private enum ExecMode {
		BATCH("batch", "Batch mode"), GUI("gui", "Graphical User Interface mode");

		private String _tag;
		private String _desc;

		private ExecMode(String modeTag, String modeDesc) {
			_tag = modeTag;
			_desc = modeDesc;
		}

		public String get_tag() {
			return _tag;
		}

		public String get_desc() {
			return _desc;
		}
	}

	// default values for some parameters
	//
	private final static Double _default_time = 10.0; // in seconds
	private final static Double _default_dt = 0.03;
	// some attributes to stores values corresponding to command-line parameters
	//
	private static Double _time = null;
	private static String _in_file = null;
	private static String _out_file = null;
	private static ExecMode _mode = null;
	public static Double _dt = null;
	private static boolean _sv = false;
	public static Factory<SelectionStrategy> selection_strategy_factory;
	public static Factory<Region> _regions_factory;
	public static Factory<Animal> _animals_factory;

	private static void parse_args(String[] args) {

		// define the valid command line options
		//
		Options cmdLineOptions = build_options();

		// parse the command line as provided in args
		//
		CommandLineParser parser = new DefaultParser();
		try {
			CommandLine line = parser.parse(cmdLineOptions, args);
			parse_help_option(line, cmdLineOptions);
			parse_in_file_option(line);
			parse_time_option(line);
			parse_out_file_option(line);
			parse_dt_option(line);
			parse_sv_option(line);
			parse_mode_option(line);
			// if there are some remaining arguments, then something wrong is
			// provided in the command line!
			//
			String[] remaining = line.getArgs();
			if (remaining.length > 0) {
				String error = "Illegal arguments:";
				for (String o : remaining)
					error += (" " + o);
				throw new ParseException(error);
			}

		} catch (ParseException e) {
			System.err.println(e.getLocalizedMessage());
			System.exit(1);
		}

	}

	private static Options build_options() {
		Options cmdLineOptions = new Options();

		// help
		cmdLineOptions.addOption(Option.builder("h").longOpt("help").desc("Print this message.").build());

		// input file
		cmdLineOptions.addOption(Option.builder("i").longOpt("input").hasArg().desc("A configuration file.").build());

		// output file
		cmdLineOptions.addOption(
				Option.builder("o").longOpt("output").hasArg().desc("Output file, where output is written.").build());

		cmdLineOptions.addOption(
				Option.builder("sv").longOpt("simple-viewer").desc("Show the viewer window in console mode.").build());

		cmdLineOptions.addOption(
				Option.builder("dt").longOpt("delta-time").hasArg().desc("A double representing actual time, in\n"
						+ "seconds, per simulation step. Default value: " + _default_dt + ".").build());
		// steps
		cmdLineOptions.addOption(Option.builder("t").longOpt("time").hasArg()
				.desc("An real number representing the total simulation time in seconds. Default value: "
						+ _default_time + ".")
				.build());
		
		cmdLineOptions.addOption(Option.builder("m").longOpt("mode").hasArg().desc("Execution Mode. Possible values: 'batch' (Batch\n"
				+ "mode), 'gui' (Graphical User Interface mode).\n"
				+ "Default value: 'gui'.").build());
		return cmdLineOptions;
	}

	private static void parse_help_option(CommandLine line, Options cmdLineOptions) {
		if (line.hasOption("h")) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp(Main.class.getCanonicalName(), cmdLineOptions, true);
			System.exit(0);
		}
	}

	private static void parse_in_file_option(CommandLine line) throws ParseException {
		_in_file = line.getOptionValue("i");
		if (_mode == ExecMode.BATCH && _in_file == null) {
			throw new ParseException("In batch mode an input configuration file is required");
		}
	}

	private static void parse_out_file_option(CommandLine line) throws ParseException {
		_out_file = line.getOptionValue("o");
		if (_mode == ExecMode.BATCH && _out_file == null) {
			throw new ParseException("In batch mode an output configuration file is required");
		}
	}

	private static void parse_sv_option(CommandLine line) throws ParseException {
		_sv = line.hasOption("sv");
	}

	private static void parse_time_option(CommandLine line) throws ParseException {
		String t = line.getOptionValue("t", _default_time.toString());
		try {
			_time = Double.parseDouble(t);
			assert (_time >= 0);
		} catch (Exception e) {
			throw new ParseException("Invalid value for time: " + t);
		}
	}

	private static void parse_dt_option(CommandLine line) throws ParseException {
		String dt = line.getOptionValue("dt", _default_dt.toString());
		try {
			_dt = Double.parseDouble(dt);
			assert (_dt >= 0);
		} catch (Exception e) {
			throw new ParseException("Invalid value for time: " + dt);
		}
	}
	
	private static void parse_mode_option(CommandLine line) throws ParseException {
		String m = line.getOptionValue("m", ExecMode.GUI.toString());
		try {
			_mode = ExecMode.valueOf(m.toUpperCase());
			
		} catch (Exception e) {
			throw new ParseException("Invalid value for mode: " + m);
		}
	}
	private static void init_factories() {
		// SelectionStrategies
		List<Builder<SelectionStrategy>> selection_strategy_builders = new ArrayList<>();
		selection_strategy_builders.add(new SelectFirstBuilder());
		selection_strategy_builders.add(new SelectClosestBuilder());
		selection_strategy_builders.add(new SelectYoungestBuilder());

		selection_strategy_factory = new BuilderBasedFactory<SelectionStrategy>(selection_strategy_builders);

		List<Builder<Region>> region_builders = new ArrayList<>();
		region_builders.add(new DefaultRegionBuilder());
		region_builders.add(new DynamicSupplyRegionBuilder());

		_regions_factory = new BuilderBasedFactory<Region>(region_builders);

		List<Builder<Animal>> animal_builders = new ArrayList<>();
		animal_builders.add(new SheepBuilder(selection_strategy_factory));
		animal_builders.add(new WolfBuilder(selection_strategy_factory));

		_animals_factory = new BuilderBasedFactory<Animal>(animal_builders);

	}

	private static JSONObject load_JSON_file(InputStream in) {
		return new JSONObject(new JSONTokener(in));
	}

	private static void start_batch_mode() throws Exception {

		InputStream is = new FileInputStream(new File(_in_file));
		JSONObject input = load_JSON_file(is);

		OutputStream os = new FileOutputStream(new File(_out_file));
		Simulator so = new Simulator(input.getInt("cols"), input.getInt("rows"), input.getInt("width"),
				input.getInt("height"), _animals_factory, _regions_factory);
		Controller co = new Controller(so);
		co.load_data(input);
		co.run(_time, _dt, _sv, os);
		os.close();
	}

	private static void start_GUI_mode() throws Exception {
		InputStream is = new FileInputStream(new File(_in_file));
		JSONObject input = load_JSON_file(is);

		Simulator so = new Simulator(input.getInt("cols"), input.getInt("rows"), input.getInt("width"),
				input.getInt("height"), _animals_factory, _regions_factory);
		Controller co = new Controller(so);
		co.load_data(input);
		SwingUtilities.invokeAndWait(() -> new MainWindow(co));
	}

	private static void start(String[] args) throws Exception {
		init_factories();
		parse_args(args);
		switch (_mode) {
		case BATCH:
			start_batch_mode();
			break;
		case GUI:
			start_GUI_mode();
			break;
		}
	}

	public static void main(String[] args) {
		Utils._rand.setSeed(2147483647l);

		try {
			start(args);
		} catch (Exception e) {
			System.err.println("Something went wrong ...");
			System.err.println();
			e.printStackTrace();
		}

	}
}
