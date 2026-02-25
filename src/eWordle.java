/**
 * The {@code eWordle} class stores the default settings and launch the game.
 *
 * <p>
 * The classes {@code Settings}, {@code Results} and {@code Service} should be access only through static
 * {@code Class.getInstance()} method to guarantee only one instance will be generated all the time so that the
 * latest preference will be reused and memory usage will be minimized.
 *
 * <p>
 * Similarly, the {@code Game} class should be access only through static {@link Game#createInstance()} every time
 * the caller start a new game. After {@link Game#createInstance()}, the older, if any, instance will be eligible
 * for garbage collection.
 *
 * @author Mingchun Zhuang
 * @version 1.0
*/
public class eWordle {
    /**
     * A constant ordered String array that lists word length options.
     */
    private static final String[] wordLengths = new String[]{"5"};
    /**
     * A constant String array that lists word source options ordered by difficulty.
     */
    private static final String[] wordSources = new String[]{"Futbol", "Matemàtiques", "Tot"};

    /**
     * This method launches the <var>setting</var> window with default setting and initialize <var>service</var>.
     *
     * @param args a default String array which is not used by this program.
     */
    public static void main(String[] args) {
        Settings.getInstance().configSettings(5, "Tot", wordLengths, wordSources);
        String initResult = Service.getInstance().initService(wordSources, wordLengths);
        if (initResult.length() > 0) {
            System.out.println("Error durant l'inicialització:" + initResult);
            return;
        }
        Settings.getInstance().setVisibleStatus(true);
    }
}
//WORDLE//Botones