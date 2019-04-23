package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author Mohammed Abu-Sharkh
 */
public final class Main {
    /** List of rotors. */
    private String[] rotors;
    /** Split up list of settings. */
    private String[] splitSettings;
    /** Initialized empty string for holding cycles. */
    private String cycles;
    /** Rotor name. */
    private String nombre;
    /** Current permutation. */
    private String currentP = "";
    /** Whole line of permutations. */
    private String wholeLine;
    /** The notch currently in use. */
    private String notchInUse;
    /** # of Rotors. */
    private int countRotors;
    /** # of Pawls. */
    private int countPawls;
    /** Rotor Array List. */
    private ArrayList<Rotor> rotorList = new ArrayList<>();

    /** Process a sequence of encryptions and decryptions, as
     *  specified by ARGS, where 1 <= ARGS.length <= 3.
     *  ARGS[0] is the name of a configuration file.
     *  ARGS[1] is optional; when present, it names an input file
     *  containing messages.  Otherwise, input comes from the standard
     *  input.  ARGS[2] is optional; when present, it names an output
     *  file for processed messages.  Otherwise, output goes to the
     *  standard output. Exits normally if there are no errors in the input;
     *  otherwise with code 1. */
    public static void main(String... args) {
        try {
            new Main(args).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Check ARGS and open the necessary files (see comment on main). */
    Main(String[] args) {
        if (args.length < 1 || args.length > 3) {
            throw error("Only 1, 2, or 3 command-line arguments allowed");
        }

        _config = getInput(args[0]);

        if (args.length > 1) {
            _input = getInput(args[1]);
        } else {
            _input = new Scanner(System.in);
        }

        if (args.length > 2) {
            _output = getOutput(args[2]);
        } else {
            _output = System.out;
        }
    }

    /** Return a Scanner reading from the file named NAME. */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Return a PrintStream writing to the file named NAME. */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Configure an Enigma machine from the contents of configuration
     *  file _config and apply it to the messages in _input, sending the
     *  results to _output. */
    private void process() {
        Machine enig = readConfig();
        String x = _input.nextLine().toUpperCase();
        setUp(enig, x.substring(2));
        while (_input.hasNextLine()) {
            String currentLine = _input.nextLine().toUpperCase();
            if (currentLine.startsWith("*")) {
                enig = new Machine(_alphabet, countRotors,
                        countPawls, rotorList);
                setUp(enig, currentLine.substring(2));
            } else {
                printMessageLine(enig.convert(currentLine));
            }
        }
    }


    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        try {
            String emptySpace = " "; countRotors = 0; countPawls = 0;
            String u = _config.next().toString();
            boolean isRotors = _config.hasNextInt();
            boolean isPawls = _config.hasNextInt();
            if (!_config.hasNext()) {
                throw new EnigmaException("space required.");
            }
            _alphabet = new Alphabet(u);
            if (!isRotors) {
                throw new EnigmaException("not accessing num rotors.");
            } else {
                countRotors = _config.nextInt();
            }
            if (!isPawls) {
                throw new EnigmaException("not accessing num pawls");
            } else {
                countPawls = _config.nextInt();
            }
            if (countPawls > countRotors) {
                throw new EnigmaException("must hav more rotors than pawls.");
            }

            nombre = _config.next().toUpperCase();
            while (_config.hasNext()) {
                notchInUse = _config.next(); currentP = _config.next();
                wholeLine = "";
                while (_config.hasNext() && currentP.contains("(")) {
                    if (!currentP.contains("(")) {
                        throw new EnigmaException("config format wrong.");
                    }
                    wholeLine = wholeLine + currentP + emptySpace;
                    currentP = _config.next();
                }
                if (!_config.hasNext()) {
                    wholeLine = wholeLine + currentP + emptySpace;
                }
                wholeLine = wholeLine.substring(0, wholeLine.length() - 1);
                if (wholeLine.contains(")(")) {
                    wholeLine = wholeLine.replace(")(", ") (");
                }
                if (wholeLine.charAt(wholeLine.length() - 1) != ')') {
                    throw new EnigmaException("Missing parentheses.");
                }
                rotorList.add(readRotor());
                nombre = currentP.toUpperCase();
            }
            return new Machine(_alphabet, countRotors, countPawls, rotorList);
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }
    /** Pulls out rotor specs based on config file.
     * @return rotor*/
    private Rotor readRotor() {
        Rotor rotor = null;
        try {
            char chr = notchInUse.charAt(0);
            if (chr == 'N') {
                rotor = (new FixedRotor(nombre,
                        new Permutation(wholeLine, _alphabet)));
            } else if (chr == 'M') {
                rotor = (new MovingRotor(nombre,
                        new Permutation(wholeLine, _alphabet),
                        notchInUse.substring(1)));
            } else if (chr == 'R') {
                rotor = (new Reflector(nombre,
                        new Permutation(wholeLine, _alphabet)));
            }
        } catch (NoSuchElementException excp) {
            throw error("rotor description won't work");
        }
        return rotor;
    }


    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        splitSettings = settings.split(" ");
        rotors = new String[M.numRotors()];

        if (rotors.length == 0) {
            throw new EnigmaException("No rotors detected.");
        }
        if (splitSettings.length < M.numRotors() + 1) {
            throw new EnigmaException("Not enough args.");
        }
        for (int i = 0; i < M.numRotors(); i++) {
            rotors[i] = splitSettings[i];
        }
        for (int i = 0; i < rotors.length - 1; i++) {
            if (rotors[i] == rotors[i + 1]) {
                throw new EnigmaException("Duplicate rotor.");
            }
        }

        if (settings.contains("(")) {
            String plug = settings.substring(settings.indexOf("("));
            Permutation newPerm = new Permutation(plug.substring(0,
                    plug.length() - 1),
                    _alphabet);
            M.setPlugboard(newPerm);
        }

        M.insertRotors(rotors);
        M.setRotors(splitSettings[rotors.length]);

    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        String messageLine = msg.replaceAll(" ", "");
        if (messageLine.length() == 0) {
            _output.println();
        } else {
            while (messageLine.length() > 0) {
                if (messageLine.length() <= 5) {
                    _output.println(messageLine);
                    messageLine = "";
                } else {
                    _output.print(messageLine.substring(0, 5) + " ");
                    messageLine = messageLine.substring(5);
                }
            }
        }
    }



    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;
}

