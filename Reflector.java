package enigma;

import static enigma.EnigmaException.*;

/** Class that represents a reflector in the enigma.
 *  @author Mohammed Abu-Sharkh
 */
class Reflector extends FixedRotor {

    /**
     * A non-moving rotor named NAME whose permutation at the 0 setting
     * is PERM.
     */
    Reflector(String name, Permutation perm) {
        super(name, perm);
    }

    @Override
    boolean reflecting() {
        return true;
    }

    @Override
    void set(int posn) {
        assert posn == 0 : "Reflector can only be at 0th position.";
    }

    @Override
    int convertBackward(int e) {
        throw new EnigmaException("cant do it.");
    }
}

