
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashSet;
import javax.swing.*;

public class Game {
    /**
     * A static variable storing the most recent instance instantiated, where older
     * ones will be eligible for garbage
     * collection.
     */
    private static Game instance;

    /**
     * A static constant holding the width of current window.
     */
    private static final int WINDOW_WIDTH = 600;

    /**
     * A static constant holding the height of current window.
     */
    private static final int WINDOW_HEIGHT = 850;

    /**
     * A static constant holding the width of each content box.
     */
    private static final int CONTENT_WIDTH = 500;

    /**
     * A static constant holding the height of each content box.
     */
    private static final int CONTENT_HEIGHT = 100;

    /**
     * A static constant holding the interval size of contents of current window.
     */
    private static final int CONTENT_MARGIN = 50;

    /**
     * A static constant holding the size ratio of cell size to cell interval size
     * of current window.
     */
    private static final int SIZE_RATIO = 8;

    /**
     * A boolean holding the status that whether the user opened the helper window.
     */
    private boolean isOpenedHelper = false;

    /**
     * A {@code JFrame} holding the instance of current window.
     */
    private JFrame window;

    /**
     * A {@code ArrayList} holding the instances of {@code JTextField} that displays
     * guessed letters typed by the user.
     */
    private ArrayList<JTextField> fields;

    /**
     * A {@code JTextField} holding the instance of {@code JTextField} that displays
     * hint messages.
     */
    private JTextField messageBoard;

    /**
     * A String holding the word in the current line.
     */
    private String currentWord;

    /**
     * An int holding current line number that counts from zero.
     */
    private int currentLine;

    /**
     * An {@code ArrayList} holding score of each confirmed input, where 0 is for
     * grey, 1 is for yellow, 2 is for green.
     */
    private ArrayList<Integer> scoreByOrder;

    /**
     * A {@code JFrame} holding the instance of helper window.
     */
    private JFrame helperWindow;

    /**
     * A {@code JTextField} holding the instance of {@code JTextField} that displays
     * helper output.
     */
    private JTextArea helperOutput;

    private JTextField timerField; // para mostrar el tiempo restante @ByGamer01

    private int segundosRestantes; // para mostrar los segundos que le quedan al jugador @ByGamer01

    private Timer countdownTimer;

    /**
     * This method launches the game window with settings given.
     *
     * @param wordSource a String describing the specific source type, included in
     *                   <var>wordSourceOption</var>.
     * @param initWord   a String holding the word to be guessed.
     * @param hashtag    a String holding the hashtag of this game.
     */
    public void playGame(String wordSource, String initWord, String hashtag) {
        System.out.println("jugant eWordle amb el tòpic " + wordSource
                + " sent la primera paraula " + initWord + " " +
                hashtag);
        // Initialize related variables.
        int wordLength = initWord.length();
        currentLine = 0;
        currentWord = "";
        fields = new ArrayList<>();
        scoreByOrder = new ArrayList<>();

        // Configure window.
        window = new JFrame("eWordle");
        window.setFocusable(true);
        window.setResizable(false);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel windowPanel = new JPanel();
        windowPanel.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        window.add(windowPanel);
        window.pack();
        windowPanel.setFocusable(false);
        windowPanel.setFocusTraversalKeysEnabled(false);
        windowPanel.setBackground(new Color(57, 142, 62));
        windowPanel.setLayout(null);

        // Add hashtag board to the current window panel.
        JTextField hashtagBoard = Settings.textInit("Hashtag: " + hashtag, "Comic Sans MS",
                JTextField.CENTER, Font.BOLD, CONTENT_MARGIN, 0, CONTENT_WIDTH, CONTENT_MARGIN, 15,
                false, false);
        windowPanel.add(hashtagBoard);

        // Add hashtag board to the current window panel.
        JTextField wordSourceBoard = Settings.textInit("Diccionari actual: " + wordSource,
                "Comic Sans MS", JTextField.CENTER, Font.BOLD, CONTENT_MARGIN, CONTENT_MARGIN / 2,
                CONTENT_WIDTH, CONTENT_MARGIN, 15, false, false);
        wordSourceBoard.setFocusable(false);
        windowPanel.add(wordSourceBoard);

        // Espacio para el Timer que vamos a añadir @ByGamer01
        timerField = Settings.textInit("Temps: ", "Comic Sans MS", JTextField.CENTER, Font.BOLD,
                CONTENT_MARGIN, CONTENT_MARGIN, // 5 minutos para el usuario
                CONTENT_WIDTH, CONTENT_MARGIN, 15, false, false); // Le ponemos los mismos parametros que el
                                                                  // WordSourceBoard

        timerField.setFocusable(false); // No se le puede clickar @ByGamer01
        windowPanel.add(timerField); // Lo añadimos a la pantalla
        segundosRestantes = 300; // 5 minutos = 300 segundos | Ya que sino el usuario automaticamente pierde ya
                                 // que el timer por defecto se pone con 0 segundos @ByGamer01

        countdownTimer = new Timer(1000, e -> { // Clase Timer @ByGamer01
            int min = segundosRestantes / 60;
            int seg = segundosRestantes % 60;
            timerField.setText(String.format("Temps: %d:%02d", min, seg));

            if (segundosRestantes <= 0) {
                countdownTimer.stop();
                closeHelperWindow();
                Results.getInstance().showResults(initWord, currentLine, false, scoreByOrder, isOpenedHelper);
                window.dispose(); // asi el timer se para siempre que termina la partida, ya sea por victoria, por
                                  // intentos o por tiempo @ByGamer01
            }
            segundosRestantes--; // que se vayan quitando los segundos

        });

        // Add message board to the window panel.
        messageBoard = Settings.textInit("", "Comic Sans MS", JTextField.CENTER, Font.BOLD,
                CONTENT_MARGIN, CONTENT_MARGIN, CONTENT_WIDTH, CONTENT_HEIGHT, 20, false,
                false);
        messageBoard.setForeground(Color.RED);
        messageBoard.setFocusable(false);
        windowPanel.add(messageBoard);

        // Add text fields that display letter typed by the user. The number of lines of
        // text fields is wordLength+1
        final double smallMarginSize = 1.0 * (WINDOW_WIDTH - CONTENT_MARGIN * 2) /
                ((SIZE_RATIO + 1) * wordLength - 1);
        final double blockSize = smallMarginSize * SIZE_RATIO;
        for (int row = 0; row <= wordLength; row++)
            for (int column = 0; column < wordLength; column++) {
                int x = (int) (CONTENT_MARGIN + column * smallMarginSize * (SIZE_RATIO + 1));
                int y = (int) (CONTENT_MARGIN * 2 + CONTENT_HEIGHT + row * smallMarginSize * (SIZE_RATIO + 1));
                JTextField field = Settings.textInit("", "", JTextField.CENTER, Font.BOLD, x, y,
                        (int) blockSize, (int) blockSize, 30, true, false);
                field.setBackground(Color.WHITE);
                field.setFocusable(false);
                fields.add(field);
                windowPanel.add(field);
            }

        // Add helper icon.

        JLabel helperTxt = new JLabel("?");
        helperTxt.setBounds(WINDOW_WIDTH - CONTENT_MARGIN, WINDOW_HEIGHT - CONTENT_MARGIN, CONTENT_MARGIN,
                CONTENT_MARGIN);
        // Label del boton de ayuda @ByGamer01
        JButton helper = Settings.initButton(WINDOW_WIDTH - CONTENT_MARGIN,
                WINDOW_HEIGHT - CONTENT_MARGIN, CONTENT_MARGIN, CONTENT_MARGIN, 25,
                event -> createHelperWindow());
        helper.setToolTipText("Obrir pistes (un asterisc \"*\" es mostrarà en el resultat)");
        helper.add(helperTxt);
        windowPanel.add(helper);

        window.addKeyListener(newKeyboardListener(initWord, wordSource));
        hashtagBoard.addKeyListener(newKeyboardListener(initWord, wordSource));
        countdownTimer.start(); // Iniciamos el timer

        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }

    /**
     * Returns a new instance of current class, where the older copy of this class,
     * if exists, will be eligible for
     * garbage collection.
     *
     * @return a new instance of current class.
     */
    public static Game createInstance() {
        Game.instance = new Game();
        return Game.instance;
    }

    /**
     * This static method modifies foreground and background colors of given
     * {@code JTextField} respectively.
     *
     * @param field      a {@code JTextField} to be modified.
     * @param foreground a {@code Color} holding the color of the preferred
     *                   foreground.
     * @param background a {@code Color} holding the color of the preferred
     *                   background.
     */
    public static void setColor(JTextField field, Color foreground, Color background) {
        field.setForeground(foreground);
        field.setBackground(background);
    }

    /**
     * This method returns a new keyboard listener.
     *
     * @param wordSource a String describing the specific source type, included in
     *                   <var>wordSourceOption</var>.
     * @param initWord   a String holding the word to be guessed.
     * @return a {@code KeyAdapter} processing keyboard inputs.
     */
    private KeyAdapter newKeyboardListener(String initWord, String wordSource) {
        int wordLength = initWord.length();
        return new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                messageBoard.setText("");
                // All possible letters will be converted to uppercase.
                char c = Character.toUpperCase(e.getKeyChar());
                // Typed enter.
                if (c == '\n') {
                    if (currentWord.length() == wordLength) {
                        // Word guessed correct.
                        if (currentWord.equals(initWord)) {
                            for (int i = 0; i < initWord.length(); i++)
                                scoreByOrder.add(2);
                            closeHelperWindow();
                            Results.getInstance().showResults(initWord, currentLine + 1, true,
                                    scoreByOrder, isOpenedHelper);
                            instance = null;
                            countdownTimer.stop(); // Finalizamos el timer en la logica de victoria del usuario
                                                   // @ByGamer01
                            window.dispose();
                        }
                        // Word guessed exists in word source of current difficulty level but incorrect.
                        else if (Service.getInstance().checkExistence(currentWord, wordSource).length() == 0) {
                            HashSet<Character> charRemainIncorrect = new HashSet<>();
                            for (int i = 0; i < wordLength; i++)
                                if (currentWord.charAt(i) == initWord.charAt(i))
                                    setColor(fields.get(currentLine * wordLength + i), Color.white,
                                            new Color(121, 167, 107)); // rojo
                                else
                                    charRemainIncorrect.add(initWord.charAt(i));
                            for (int i = 0; i < wordLength; i++)
                                if (currentWord.charAt(i) != initWord.charAt(i)) {
                                    if (charRemainIncorrect.contains(currentWord.charAt(i))) {
                                        scoreByOrder.add(1);
                                        setColor(fields.get(currentLine * wordLength + i), Color.white,
                                                new Color(198, 180, 102));
                                    } else {
                                        scoreByOrder.add(0);
                                        setColor(fields.get(currentLine * wordLength + i), Color.white,
                                                new Color(121, 124, 126));
                                    }
                                } else
                                    scoreByOrder.add(2);
                            currentWord = "";
                            // Maximum guess tries reached.
                            if (++currentLine > wordLength) {
                                closeHelperWindow();
                                Results.getInstance().showResults(initWord, currentLine, false, scoreByOrder,
                                        isOpenedHelper);
                                countdownTimer.stop(); // Lo pausamos aqui tambien en la parte de derrota @ByGamer01
                                window.dispose();
                            }
                        } else
                            messageBoard.setText("No es troba al llistat");
                    } else
                        messageBoard.setText("No és suficientment llarga");
                }
                // Typed letters.
                else if ('A' <= c && c <= 'Z') {
                    if (currentWord.length() < wordLength) {
                        JTextField field = fields.get(currentLine * wordLength + currentWord.length());
                        setColor(field, Color.black, Color.white);
                        field.setText("" + c);
                        currentWord += c;
                    } else
                        messageBoard.setText("És moment de prémer \"intro\"");
                }
                // Typed backspace.
                else if (c == '\b') {
                    if (currentWord.length() > 0) {
                        JTextField field = fields.get(currentLine * wordLength + currentWord.length() - 1);
                        field.setText("");
                        setColor(field, Color.black, Color.white);
                        currentWord = currentWord.substring(0, currentWord.length() - 1);
                    } else
                        messageBoard.setText("No hi ha més caràcters a esborrar");
                }
                // Illegal input.
                else
                    messageBoard.setText("Només s'accepted caràcters de l'alfabet");
            }
        };
    }

    /**
     * This method closes the helper window if exists.
     */
    private void closeHelperWindow() {
        if (helperWindow != null)
            helperWindow.dispose();
    }

    /**
     * This method creates a new helper window.
     */
    private void createHelperWindow() {
        // Return if already opened one.
        if (helperWindow != null)
            return;
        isOpenedHelper = true;
        // Configure current helper window.
        final int helperWindowWidth = 600;

        final int helperWindowHeight = 800;
        helperWindow = new JFrame("Pistes");
        JPanel helperWindowPanel = new JPanel();
        helperWindowPanel.setPreferredSize(new Dimension(helperWindowWidth, helperWindowHeight));
        helperWindow.setFocusable(true);
        helperWindowPanel.setFocusable(false);
        helperWindowPanel.setFocusTraversalKeysEnabled(false);
        helperWindowPanel.setBackground(new Color(57, 142, 62));
        helperWindowPanel.setLayout(null);
        helperWindow.setResizable(false);
        helperWindow.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                helperWindow = null;
            }
        });
        helperWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        helperOutput = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(helperOutput);

        // Add word source board to the helper window.
        int currentHelperHeight = 0;
        JTextField wordSourceBoard = Settings.textInit("Cercant diccionaris: " + Settings.getWordSource() +
                ", Llargària de paraula: " + Settings.getInitWord().length(), "Comic Sans MS",
                JTextField.CENTER, Font.PLAIN, CONTENT_MARGIN, currentHelperHeight, CONTENT_WIDTH, CONTENT_MARGIN,
                15, false, false);
        wordSourceBoard.setFocusable(false);
        helperWindowPanel.add(wordSourceBoard);

        // Add input board to the helper window.
        currentHelperHeight += CONTENT_MARGIN;
        JTextField inputBoard = Settings.textInit("", "", JTextField.LEFT, Font.PLAIN, CONTENT_MARGIN,
                currentHelperHeight, CONTENT_WIDTH, CONTENT_MARGIN, 20, true, true);
        helperWindowPanel.add(inputBoard);

        // Add search button.
        currentHelperHeight += CONTENT_MARGIN + CONTENT_MARGIN;
        JLabel helperButtonTxt = new JLabel("Cerca");
        helperButtonTxt.setBounds(CONTENT_MARGIN,
                currentHelperHeight, CONTENT_WIDTH, CONTENT_MARGIN);
        JButton helperButton = Settings.initButton(CONTENT_MARGIN,
                currentHelperHeight, CONTENT_WIDTH, CONTENT_MARGIN, 20,
                event -> {
                    // Handle search.
                    String[] response = Service.getInstance().validateHelperInput(inputBoard.getText()).split("\\$");
                    if (response[0].length() == 0)
                        helperOutput.setText(response[1]);
                    else
                        helperOutput.setText(response[0]);
                    helperOutput.setCaretPosition(0);
                });
        helperButton.add(helperButtonTxt);
        helperButton.setToolTipText(
                "Cerca candidats en el diccionari actual. DAVID Exemple: *****(DAV*), D*V**(AI), *****(DAV*)[AB]");
        helperWindowPanel.add(helperButton);

        // Add helper output text field.
        currentHelperHeight += CONTENT_MARGIN * 2;
        final int helperOutputHeight = helperWindowHeight - CONTENT_MARGIN - currentHelperHeight;
        helperOutput.setEditable(false);
        helperOutput.setOpaque(true);
        scrollPane.setBorder(null);
        scrollPane.setBounds(CONTENT_MARGIN, currentHelperHeight, CONTENT_WIDTH, helperOutputHeight);
        helperWindowPanel.add(scrollPane);

        helperWindow.add(helperWindowPanel);
        helperWindow.pack();
        helperWindow.setVisible(true);
    }
}
