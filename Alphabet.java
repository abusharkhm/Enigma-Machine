package enigma;

/** An alphabet of encodable characters.  Provides a mapping from characters
 *  to and from indices into the alphabet.
 *  @author P. N. Hilfinger
 */
class Alphabet {
    /** Custom alphabet. */
    private String newalph;
    /** Constructor. Takes in either a range or a String of chars.
     * @param chrs to go into alphabet. */
    Alphabet(String chrs) {
        if (chrs.length() == 3) {
            if (chrs.charAt(0) == 'H') {
                newalph = new String("HIJKLMNOPQ");
            } else {
                newalph = new String("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
            }
        } else {
            newalph = chrs;
        }
    }

    /** Returns the size of the alphabet. */
    int size() {
        return  newalph.length();
    }

    /** Returns true if preprocess(CH) is in this alphabet. */
    boolean contains(char ch) {
        return newalph.indexOf(ch) != -1;
    }

    /** Returns character number INDEX in the alphabet, where
     *  0 <= INDEX < size(). */
    char toChar(int index) {
        if (index < 0 || index >= size()) {
            throw new EnigmaException("index doesn't exist.");
        }
        return newalph.charAt(index);
    }

    /** Returns the index of character preprocess(CH), which must be in
     *  the alphabet. This is the inverse of toChar().
     *  @param c char to convert*/
    int toInt(char c) {
        if (contains(c)) {
            return newalph.indexOf(c);
        } else {
            throw new EnigmaException("charr not found.");
        }
    }
}
