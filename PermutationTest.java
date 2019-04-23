package enigma;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;
import static org.junit.Assert.*;

import static enigma.TestUtils.*;

/** The suite of all JUnit tests for the Permutation class.
 *  @author Mohammed Abu-Sharkh
 */
public class PermutationTest {

    /**
     * Testing time limit.
     */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    /* ***** TESTING UTILITIES ***** */

    private Permutation perm;
    private String alpha = UPPER_STRING;

    /**
     * Check that perm has an alphabet whose size is that of
     * FROMALPHA and TOALPHA and that maps each character of
     * FROMALPHA to the corresponding character of FROMALPHA, and
     * vice-versa. TESTID is used in error messages.
     */
    private void checkPerm(String testId,
                           String fromAlpha, String toAlpha) {
        int N = fromAlpha.length();
        assertEquals(testId + " (wrong length)", N, perm.size());
        for (int i = 0; i < N; i += 1) {
            char c = fromAlpha.charAt(i), e = toAlpha.charAt(i);
            assertEquals(msg(testId, "wrong translation of '%c'", c),
                    e, perm.permute(c));
            assertEquals(msg(testId, "wrong inverse of '%c'", e),
                    c, perm.invert(e));
            int ci = alpha.indexOf(c), ei = alpha.indexOf(e);
            assertEquals(msg(testId, "wrong translation of %d", ci),
                    ei, perm.permute(ci));
            assertEquals(msg(testId, "wrong inverse of %d", ei),
                    ci, perm.invert(ei));
        }
    }

    /* ***** TESTS ***** */

    @Test
    public void checkIdTransform() {
        perm = new Permutation("", UPPER);
        checkPerm("identity", UPPER_STRING, UPPER_STRING);
    }

    @Test
    public void splitterTest() {
        String cycles = "(ABCDE) (FGH) (IJKL)";
        String[] expected = {"ABCDE", "FGH", "IJKL"};
        assertArrayEquals(expected, Permutation.splitter(cycles));
    }

    Alphabet letters = new Alphabet("ABCDEFGHIJKLMNOPQRSTUVWXYZ");

    @Test
    public void permuteTestwCyclicFeature() {
        perm = new Permutation("(ZADT) (YUR)", letters);
        assertEquals('D', perm.permute('A'));
        assertEquals('Z', perm.permute('T'));
        assertEquals('Y', perm.permute('R'));

    }

    @Test
    public void invertTestwCyclicFeature() {
        perm = new Permutation("(ZADT) (YUR)", letters);
        assertEquals('D', perm.invert('T'));
        assertEquals('T', perm.invert('Z'));
        assertEquals('U', perm.invert('R'));
    }

    @Test
    public void permTestUsingInts() {
        Permutation test = new Permutation(NAVALA.get("III"), UPPER);
        assertEquals(1, test.permute(0));
        assertEquals(7, test.permute(3));

    }

    @Test
    public void invTestUsingInts() {
        Permutation test = new Permutation(NAVALA.get("III"), UPPER);
        assertEquals(15, test.invert(4));
        assertEquals(6, test.invert(2));

    }
}
