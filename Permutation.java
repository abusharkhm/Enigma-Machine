package enigma;

import java.util.ArrayList;
import static enigma.EnigmaException.*;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author Mohammed Abu-Sharkh
 */
class Permutation {
    /** ArrayList that holds indexes of characters.*/
    private ArrayList<Integer> a;
    /** String array of cycles. */
    private String[] _cycles;
    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        int x = 0;
        a = new ArrayList<Integer>();
        while (x < _alphabet.size()) {
            a.add(x);
            x++;
        }
        for (String s: splitter(cycles)) {
            for (int i = 0; i < s.length(); i++) {
                if (s.length() == 0) {
                    continue;
                } else if (i == (s.length() - 1)) {
                    a.set(_alphabet.toInt(s.charAt(i)),
                            _alphabet.toInt(s.charAt(0)));
                } else {
                    a.set(_alphabet.toInt(s.charAt(i)),
                            _alphabet.toInt(s.charAt(i + 1)));
                }
            }
        }
        _cycles = splitter(cycles);
    }
    /** Cycle pre-processing.
     * @param cycles the cycles
     * @return processed cycles string*/
    public static String[] splitter(String cycles) {
        cycles = cycles.replaceAll("[(]", "");
        cycles = cycles.replaceAll("[)]", "");
        String[] stringcycle = cycles.split(" ");
        return stringcycle;
    }

    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm. */
    private void addCycle(String cycle) {
        return;
    }

    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }
    /** Modified version of wrap, includes size.
     * @param p int to mod
     * @param size number to mod by
     * @return r modded*/
    private int modder(int p, int size) {
        int r = p % size;
        if (r < 0) {
            r += size;
        }
        return r;
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        return _alphabet.size();
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        char x = _alphabet.toChar(wrap(p)); char y;
        for (String cyc: _cycles) {
            for (int n = 0; n < cyc.length(); n++) {
                if (cyc.charAt(n) == x) {
                    y = cyc.charAt((n + 1) % cyc.length());
                    return _alphabet.toInt(y);
                }
            }
        }
        return p;
    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        char x = _alphabet.toChar(wrap(c)); char y;
        for (String cyc: _cycles) {
            for (int n = 0; n < cyc.length(); n++) {
                if (cyc.charAt(n) == x) {
                    y = cyc.charAt(modder(n - 1, cyc.length()));
                    return _alphabet.toInt(y);
                }
            }
        }
        return c;

    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        return _alphabet.toChar(permute(_alphabet.toInt(p)));
    }

    /** Return the result of applying the inverse of this permutation to C. */
    int invert(char c) {
        return _alphabet.toChar(invert(_alphabet.toInt(c)));
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        boolean isd = true;
        for (int i = 0; i < _alphabet.size(); i++) {
            if (i == permute(i)) {
                isd = false;
            }
        }
        return isd;
    }

    /** Alphabet of this permutation. */
    private final Alphabet _alphabet;


}
