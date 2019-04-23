package enigma;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;

import static enigma.EnigmaException.*;

/** Class that represents a complete enigma machine.
 *  @author Mohammed Abu-Sharkh
 */
class Machine {
    /** Number of Rotors. */
    private int _numRotors;
    /** Number of Pawls. */
    private int _pawls;
    /** Collection of all Rotors. */
    private Collection<Rotor> _allRotors;
    /**
     * Common alphabet of my rotors.
     */
    private final Alphabet _alphabet;
    /** Plugboard. */
    private Permutation _plugboard;
    /** ArrayList containing rotor names. */
    private ArrayList<Rotor> rotorNames = new ArrayList<>();
    /** Hashmap mapping names of rotors to rotor objects. */
    private HashMap<String, Rotor> bigBank = new HashMap<>();

    /**
     * A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     * and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     * available rotors.
     */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        if (numRotors > 1 && pawls < numRotors && pawls >= 0) {
            _alphabet = alpha;
            _numRotors = numRotors;
            _pawls = pawls;
            _allRotors = allRotors;
            for (Rotor toAdd : allRotors) {
                bigBank.put(toAdd.name().toUpperCase(), toAdd);
            }
        } else {
            throw new EnigmaException("Specs not feasible");
        }
    }

    /**
     * Return the number of rotor slots I have.
     */
    int numRotors() {
        return _numRotors;
    }


    /** Returns arraylist of rotorsinuse. */
    Collection<Rotor> rotorTing() {
        return (Collection<Rotor>) rotorNames;
    }

    /**
     * Return the number pawls (and thus rotating rotors) I have.
     */
    int numPawls() {
        return _pawls;
    }

    /**
     * Set my rotor slots to the rotors named ROTORS from my set of
     * available rotors (ROTORS[0] names the reflector).
     * Initially, all rotors are set at their 0 setting.
     */
    void insertRotors(String[] rotors) {
        if (bigBank.size() < rotors.length) {
            throw new EnigmaException("Not enough rotors.");
        }
        if (!bigBank.get(rotors[0]).reflecting()) {
            throw new EnigmaException("0th rotor must be reflecting.");
        }
        if (!(rotors.length == _numRotors)) {
            throw new EnigmaException("Must be equal");
        }
        for (int i = 0; i < rotors.length; i++) {
            if (rotorNames.contains(rotors[i])) {
                throw new EnigmaException("Rotor name already  passed.");
            } else {
                rotorNames.add(i, bigBank.get(rotors[i]));
            }
        }
    }


    /**
     * Set my rotors according to SETTING, which must be a string of
     * numRotors()-1 upper-case letters. The first letter refers to the
     * leftmost rotor setting (not counting the reflector).
     */
    void setRotors(String setting) {
        if (setting.length() == numRotors() - 1) {
            for (int i = 0; i < setting.length(); i++) {
                rotorNames.get(i + 1).set(setting.charAt(i));
            }
        } else {
            throw new EnigmaException("bad configuration.");
        }
    }

    /**
     * Set the plugboard to PLUGBOARD.
     */
    void setPlugboard(Permutation plugboard) {
        _plugboard = plugboard;
    }

    /**
     * Returns the result of converting the input character C (as an
     * index in the range 0..alphabet size - 1), after first advancing
     * <p>
     * the machine.
     */
    int convert(int c) {
        List<Integer> readyToMove = new LinkedList<>();
        if (rotorNames.size() != numRotors()) {
            throw new EnigmaException("bad config.");
        }
        for (int i = numRotors() - numPawls(); i < rotorNames.size(); i++) {
            if (i != numRotors() - numPawls()) {
                if (rotorNames.get(i).atNotch()) {
                    readyToMove.add(i);
                }
            } else if ((i == numRotors() - numPawls())
                    && (i < rotorNames.size() - 1)
                    && (rotorNames.get(i + 1).atNotch())) {
                readyToMove.add(i);
            }
        }
        ArrayList<Rotor> moved = new ArrayList<>();
        for (int i : readyToMove) {
            if (!moved.contains(rotorNames.get(i))) {
                rotorNames.get(i).advance();
                moved.add(rotorNames.get(i));
            }
            if (!moved.contains(rotorNames.get(i - 1))) {
                rotorNames.get(i - 1).advance();
                moved.add(rotorNames.get(i - 1));
            }
        }
        if (!moved.contains(rotorNames.get(rotorNames.size() - 1))) {
            rotorNames.get(rotorNames.size() - 1).advance();
        }

        if (_plugboard != null) {
            c = _plugboard.permute(c);
        }

        for (int i = rotorNames.size() - 1; i > -1; i -= 1) {
            c = rotorNames.get(i).convertForward(c);
        }
        for (int i = 1; i < rotorNames.size(); i += 1) {
            c = rotorNames.get(i).convertBackward(c);
        }

        if (_plugboard != null) {
            c = _plugboard.permute(c);
        }

        return c;

    }




    /**
     * Returns the encoding/decoding of MSG, updating the state of
     * the rotors accordingly.
     */
    String convert(String msg) {
        String conv = "";
        msg = msg.toUpperCase();
        for (int i = 0; i < msg.length(); i++) {
            char ch = msg.charAt(i);
            if (ch == ' ') {
                conv += " ";
            } else {
                ch = (char) _alphabet.toChar(convert(_alphabet.toInt(ch)));
                conv += ch;
            }
        }
        return conv;
    }
}



