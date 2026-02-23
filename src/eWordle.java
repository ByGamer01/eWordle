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
    private static final String[] wordLengths = new String[]{"5", "6", "7", "8"};
    /**
     * A constant String array that lists word source options ordered by difficulty.
     */
    private static final String[] wordSources = new String[]{"CET-4", "CET-6", "TOEFL", "GRE", "Oxford Dictionary",
            "All"};

    /**
     * This method launches the <var>setting</var> window with default setting and initialize <var>service</var>.
     *
     * @param args a default String array which is not used by this program.
     */
    public static void main(String[] args) {
        Settings.getInstance().configSettings(5, "All", wordLengths, wordSources);
        String initResult = Service.getInstance().initService(wordSources, wordLengths);
        if (initResult.length() > 0) {
            System.out.println("Error while initialization:" + initResult);
            return;
        }
        Settings.getInstance().setVisibleStatus(true);
    }
}
