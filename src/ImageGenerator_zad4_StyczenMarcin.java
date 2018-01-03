
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

interface ImageGeneratorConfigurationInterface {
    /**
     * Metoda ustawia plotno, po ktorym nalezy rysowac.
     *
     * @param canvas plotno obrazu
     */
    public void setCanvas(boolean[][] canvas);

    /**
     * Metoda ustawia poczatkowa pozycje piora. Pioro zostaje ustawione na danej
     * pozycji i zostawia na niej slad.
     *
     * @param col kolumna, w ktorej umieszczane jest pioro
     * @param row wiersz, w ktorym umieszczane jest pioro
     */
    public void setInitialPosition(int col, int row);

    /**
     * Ustalenie liczby maksymalnej liczby polecen, ktore mozna cofnac, przywrocic
     * lub powtorzyc.
     *
     * @param commands maksymalna liczba polecen, ktorej moga dotyczyc operacje
     *                 undo/redo/repeat. Uwaga: w przypadku undo/redo chodzi o
     *                 <b>laczna</b> liczbe polecen, ktore sa wycofywane. Czyli, gdy
     *                 commands to 10, to moge wykonac undo(5); undo(3); undo(2).
     */
    public void maxUndoRedoRepeatCommands(int commands);
}

interface ImageGeneratorInterface {

    /**
     * Pioro przemieszcza sie steps krokow do gory. Jesli wiersz, w ktorym
     * poczatkowo znajduje sie pisak to row, to po wykonaniu polecenia pioro
     * przemiesza sie do wiersza o numerze row + steps. Pozycje plotna od
     * [col][row+1] do [col][row+N] zostaja zamalowane.
     *
     * @param steps liczba krokow o jaka pioro przesunie sie w gore.
     */
    public void up(int steps);

    /**
     * Pioro przemieszcza sie steps krokow do dolu. Jesli wiersz, w ktorym
     * poczatkowo znajduje sie pisak to row, to po wykonaniu polecenia pioro
     * przemiesza sie do wiersza o numerze row - steps. Pozycje plotna od
     * [col][row-1] do [col][row-N] zostaja zamalowane.
     *
     * @param steps liczba krokow o jaka pioro przesunie sie w dol.
     */
    public void down(int steps);

    /**
     * Pioro przemieszcza sie steps krokow w lewo. Jesli kolumna, w ktorej
     * poczatkowo znajduje sie pisak to col, to po wykonaniu polecenia pioro
     * przemiesza sie do kolumny o numerze col - steps. Pozycje plotna od
     * [col-1][row] do [col-N][row] zostaja zamalowane.
     *
     * @param steps liczba krokow o jaka pioro przesunie sie w lewo.
     */
    public void left(int steps);

    /**
     * Pioro przemieszcza sie steps krokow w prawo. Jesli kolumna, w ktorej
     * poczatkowo znajduje sie pisak to col, to po wykonaniu polecenia pioro
     * przemiesza sie do kolumny o numerze col + steps. Pozycje plotna od
     * [col+1][row] do [col+N][row] zostaja zamalowane.
     *
     * @param steps liczba krokow o jaka pioro przesunie sie w prawo.
     */
    public void right(int steps);

    /**
     * Polecenie powtorzenia ostatnich commands polecen. Polecenie nie laczy sie z
     * undo/redo. Czyli, sekwencja undo(1) repeat(1) oznacza powtorzenie polecenia,
     * ktore "odslonila" operacja undo, nie zas dodatkowe wykonanie operacji undo.
     *
     * @param commands liczba polecen do powtorzenia
     */
    public void repeat(int commands);

    /**
     * Usuniecie efektu ostatnich commands polecen. Undo nie jest traktowane jako
     * polecene, czyli sekwencja undo(2) i undo(1) prowadzi do wycofania ostatnich 3
     * polecen, a nie do przywrocenia 2 polecen wycofanych za pomoca pierwszego
     * uzycia undo.
     *
     * @param commands liczba polecen do wycofania
     */
    public void undo(int commands);

    /**
     * Przywrocenie efektu commands wycofanych polecen. Redo nie jest traktowane
     * jako polecenie. Sekwencja redo(2) i redo(1) ma doprowadzic do odtworzenia
     * dzialania 3 polecen usunietych przez undo.
     *
     * @param commands liczba polecen, ktorych efekt nalezy przywrocic
     */
    public void redo(int commands);
}

interface ImageGeneratorPenInterface {
    /**
     * Typ wyliczeniowy reprezentujacy stan piora.
     *
     * @author oramus
     */
    enum PenState {
        /**
         * Pioro podniesione do gory nie zostawia na plotnie zadnego sladu.
         */
        UP,
        /**
         * Pioro opuszczone - pozostawia slad na plotnie. Slad obejmuje rowniez ta
         * pozycje piora, w ktorej doszlo do jego opuszczenia (oczywiscie slad moze byc
         * niewidoczny np. gdy pioro ustawione na pisanie w pewnym "kolorze"
         * przemieszczane jest nad obszarem o tym samym "kolorze".)
         */
        DOWN;
    }

    /**
     * Polecenie ustawienia stanu piora na state. Jesli pioro bylo podniesione, to
     * zmiana stanu na DOWN moze spowodowac zmiane stanu canvas na aktualnej pozycji
     * piora. Polecenie jest uwzgledniane w pracy metod undo/redo/repeat.
     *
     * @param state nowy stan piora
     */
    public void setPenState(PenState state);

    /**
     * Polecenie ustawienia "koloru" piora. Jesli "kolor" ustawiony jest np. na
     * true, to pioro (o ile jest opuszczone) pozostawia slad umieszczajac pod
     * odpowiednimi indeksami tablicy canvas wartosc true. Jesli pioro jest
     * opuszczone to zmiana "koloru" natychmiast zmienia odpowiednia pozycje tablicy
     * canvas. Polecenie jest uwzgledniane w pracy metod undo/redo/repeat.
     *
     * @param color nowy "kolor" piora.
     */
    public void setColor(boolean color);

    /**
     * Metoda zwraca aktualny stan piora. Wykonanie tej metody nie ma wplywu na
     * wynik pracy metod undo/redo/repeat.
     *
     * @return aktualny stan piora
     */
    public PenState getPenState();

    /**
     * Metoda zwraca aktualny "kolor" piora. Wykonanie tej metody nie ma wplywu na
     * wynik pracy metod undo/redo/repeat.
     *
     * @return aktualny kolor piora
     */
    public boolean getColor();
}

/**
 * Interfejs pozwalajacy doprecyzowac jakie slady zostawia na plotnie pisak
 * jesli jest w stanie DOWN i o okreslonym kolorze.
 *
 */
interface ImageGeneratorPenStyleInterface {
    public enum PenStyle {
        SOLID {
            @Override
            public boolean value(boolean penColor, boolean canvasColor) {
                return penColor;
            }
        },
        AND {
            @Override
            public boolean value(boolean penColor, boolean canvasColor) {
                return penColor && canvasColor;
            }
        },
        OR {
            @Override
            public boolean value(boolean penColor, boolean canvasColor) {
                return penColor || canvasColor;
            }
        },
        XOR {
            @Override
            public boolean value(boolean penColor, boolean canvasColor) {
                return penColor ^ canvasColor;
            }
        },
        INVERSE {
            @Override
            public boolean value(boolean penColor, boolean canvasColor) {
                return !canvasColor;
            }
        };

        /**
         * Metoda zwraca wynik uzycia pisaka znajdujacego sie w stanie DOWN (!!) i o
         * kolorze penColor na plotno bedace w stanie canvasColor.
         *
         * @param penColor
         *            kolor pisaka
         * @param canvasColor
         *            kolor plotna
         * @return koncowy kolor plotna
         */
        public abstract boolean value(boolean penColor, boolean canvasColor);
    }

    /**
     * Metoda ustawia styl piora
     *
     * @param style
     *            nowy styl piora
     */
    public void setPenStyle(PenStyle style);

    /**
     * Metoda pobiera styl piora
     *
     * @return aktualnie uzywany styl piora
     */
    public PenStyle getPenStyle();
}

enum ENameOperation {
    UP,
    DOWN,
    RIGHT,
    LEFT,
    REPEAT,
    PEN_STATE,
    COLOR
}

class Operation {
    private Pair<ENameOperation, Integer> nameValuePair;
    private List<Pair<Integer, Integer>> affectedColRowIdx;
    private boolean affectedColRowIdxColor;

    private Pair<Integer, Integer> colRowCursorPosition;

    public Operation(Pair<ENameOperation, Integer> nameValuePair, List<Pair<Integer, Integer>> affectedColRowIdx, boolean color) {
        this.nameValuePair = nameValuePair;
        this.affectedColRowIdx = affectedColRowIdx;
        this.affectedColRowIdxColor = color;
    }

    public void setColRowCursorPosition(Pair<Integer, Integer> colRowCursorPosition) {
        this.colRowCursorPosition = colRowCursorPosition;
    }

    public Pair<ENameOperation, Integer> getNameValuePair() {
        return nameValuePair;
    }

    public List<Pair<Integer, Integer>> getAffectedColRowIdx() {
        return affectedColRowIdx;
    }

    public boolean isAffectedColRowIdxColor() {
        return affectedColRowIdxColor;
    }

    public Pair<Integer, Integer> getColRowCursorPosition() {
        return colRowCursorPosition;
    }
}

class ImageGenerator implements ImageGeneratorConfigurationInterface, ImageGeneratorInterface, ImageGeneratorPenInterface {
    private final ImageGeneratorPenInterface.PenState PEN_UP = ImageGeneratorPenInterface.PenState.UP;
    private final ImageGeneratorPenInterface.PenState PEN_DOWN = ImageGeneratorPenInterface.PenState.DOWN;
    private int commands; //TODO max repeat(commands) && wspolnie max undo(sigma[commands]) ORAZ wspolnie max redo(sigma[commands])
    boolean[][] canvas; //TODO poczatkowa wartosc moze byc jakakolwiek
    Pair<Integer, Integer> colRowCursor;
    Pair<Integer, Integer> colRowCursorInitPosition;

    int pointerIndexList;
    List<Operation> operationList;

    ImageGeneratorPenInterface.PenState actualPenState;
    boolean actualColor;

    public ImageGenerator() {
        operationList = new ArrayList<>();
        this.pointerIndexList = -1;
    }

    //region ImageGeneratorPenInterface

    @Override
    public void setPenState(PenState state) {
        actualPenState = state;

        List<Pair<Integer, Integer>> affectedColRowIdx = new ArrayList<>();
        if (actualPenState == PEN_DOWN && canvas[colRowCursor.getKey()][colRowCursor.getValue()] != actualColor) {
            canvas[colRowCursor.getKey()][colRowCursor.getValue()] = actualColor;
            affectedColRowIdx.add(colRowCursor);
        }

        //state - DOWN = true = 1, UP = false = 0
        Integer stateInt = (state == PEN_DOWN) ? 1 : 0;
        Operation operation = new Operation(new Pair<>(ENameOperation.PEN_STATE, stateInt), affectedColRowIdx, actualColor);
        operationList.add(++pointerIndexList, operation);

        operation.setColRowCursorPosition(colRowCursor);
    }

    @Override
    public void setColor(boolean color) {
        actualColor = color;
        List<Pair<Integer, Integer>> affectedColRowIdx = new ArrayList<>();
        if (actualPenState == PEN_DOWN && canvas[colRowCursor.getKey()][colRowCursor.getValue()] != color) {
            canvas[colRowCursor.getKey()][colRowCursor.getValue()] = color;
            affectedColRowIdx.add(colRowCursor);
        }

        //color - true = 1, false = 0
        Integer colorInt = color ? 1 : 0;
        Operation operation = new Operation(new Pair<>(ENameOperation.COLOR, colorInt), affectedColRowIdx, actualColor);
        operationList.add(++pointerIndexList, operation);

        operation.setColRowCursorPosition(colRowCursor);

    }

    @Override
    public PenState getPenState() {
        return actualPenState;
    }

    @Override
    public boolean getColor() {
        return actualColor;
    }


    //endregion

    // [][ROW=+STEPs]
    @Override
    public void up(int steps) {
        List<Pair<Integer, Integer>> affectedColRowIdx = new ArrayList<>();
        if (actualPenState == PEN_DOWN) {
            for (int i = 1; i <= steps; i++) {
                if (canvas[colRowCursor.getKey()][colRowCursor.getValue() + i] != actualColor) {
                    canvas[colRowCursor.getKey()][colRowCursor.getValue() + i] = actualColor;
                    affectedColRowIdx.add(new Pair<>(colRowCursor.getKey(), colRowCursor.getValue() + i));
                }

            }
        }
        colRowCursor = new Pair<>(colRowCursor.getKey(), colRowCursor.getValue() + steps);

        Operation operation = new Operation(new Pair<>(ENameOperation.UP, steps), affectedColRowIdx, actualColor);
        operationList.add(++pointerIndexList, operation);

        operation.setColRowCursorPosition(colRowCursor);
    }

    // [][ROW=-STEPs]
    @Override
    public void down(int steps) {
        List<Pair<Integer, Integer>> affectedColRowIdx = new ArrayList<>();
        if (actualPenState == PEN_DOWN) {
            for (int i = 1; i <= steps; i++) {
                if (canvas[colRowCursor.getKey()][colRowCursor.getValue() - i] != actualColor) {
                    canvas[colRowCursor.getKey()][colRowCursor.getValue() - i] = actualColor;
                    affectedColRowIdx.add(new Pair<>(colRowCursor.getKey(), colRowCursor.getValue() - i));
                }
            }
        }
        colRowCursor = new Pair<>(colRowCursor.getKey(), colRowCursor.getValue() - steps);

        Operation operation = new Operation(new Pair<>(ENameOperation.DOWN, steps), affectedColRowIdx, actualColor);
        operationList.add(++pointerIndexList, operation);

        operation.setColRowCursorPosition(colRowCursor);
    }

    // [COL=-STEP][]
    @Override
    public void left(int steps) {
        List<Pair<Integer, Integer>> affectedColRowIdx = new ArrayList<>();
        if (actualPenState == PEN_DOWN) {
            for (int i = 1; i <= steps; i++) {
                if (canvas[colRowCursor.getKey() - i][colRowCursor.getValue()] != actualColor) {
                    canvas[colRowCursor.getKey() - i][colRowCursor.getValue()] = actualColor;
                    affectedColRowIdx.add(new Pair<>(colRowCursor.getKey() - i, colRowCursor.getValue()));
                }
            }
        }
        colRowCursor = new Pair<>(colRowCursor.getKey() - steps, colRowCursor.getValue());

        Operation operation = new Operation(new Pair<>(ENameOperation.LEFT, steps), affectedColRowIdx, actualColor);
        operationList.add(++pointerIndexList, operation);

        operation.setColRowCursorPosition(colRowCursor);
    }

    // [COL=+STEP][]
    @Override
    public void right(int steps) {
        List<Pair<Integer, Integer>> affectedColRowIdx = new ArrayList<>();
        if (actualPenState == PEN_DOWN) {
            for (int i = 1; i <= steps; i++) {
                if (canvas[colRowCursor.getKey() + i][colRowCursor.getValue()] != actualColor) {
                    canvas[colRowCursor.getKey() + i][colRowCursor.getValue()] = actualColor;
                    affectedColRowIdx.add(new Pair<>(colRowCursor.getKey() + i, colRowCursor.getValue()));
                }
            }
        }
        colRowCursor = new Pair<>(colRowCursor.getKey() + steps, colRowCursor.getValue());

        Operation operation = new Operation(new Pair<>(ENameOperation.RIGHT, steps), affectedColRowIdx, actualColor);
        operationList.add(++pointerIndexList, operation);

        operation.setColRowCursorPosition(colRowCursor);
    }

    // zapisujemy w stosie zadan
    @Override
    public void repeat(int commands) {
        List<Pair<Integer, Integer>> affectedColRowIdx = new ArrayList<>();
        pointerIndexList -= commands - 1;
        for (int i = 0; i < commands; i++) {
            Pair<ENameOperation, Integer> idxOperation = operationList.get(pointerIndexList).getNameValuePair();
            if (ENameOperation.UP == idxOperation.getKey()) {
                up(idxOperation.getValue());
            } else if (ENameOperation.DOWN == idxOperation.getKey()) {
                down(idxOperation.getValue());
            } else if (ENameOperation.LEFT == idxOperation.getKey()) {
                left(idxOperation.getValue());
            } else if (ENameOperation.RIGHT == idxOperation.getKey()) {
                right(idxOperation.getValue());
            } else if (ENameOperation.PEN_STATE == idxOperation.getKey()) {
                PenState penState = (idxOperation.getValue() == 1) ? PEN_DOWN : PEN_UP;
                setPenState(penState);
            } else if (ENameOperation.COLOR == idxOperation.getKey()) {
                setColor(idxOperation.getValue() == 1);
            }

            //nie powielamy w operationList przy repeat
            Operation operation = operationList.get(pointerIndexList);
            affectedColRowIdx.addAll(operation.getAffectedColRowIdx());
            operationList.remove(pointerIndexList);
        }

        //ew. problem z actualColor, bo tutaj moze raz malowac na false, raz na true
        Operation operation = new Operation(new Pair<>(ENameOperation.REPEAT, commands), affectedColRowIdx, actualColor);
        operationList.add(pointerIndexList, operation);

        operation.setColRowCursorPosition(colRowCursor);
    }

    @Override
    public void undo(int commands) {
        for (int i = 0; i < commands; i++, pointerIndexList--) {
            Operation operation = operationList.get(pointerIndexList);
            boolean affectedColor = operation.isAffectedColRowIdxColor();
            operation.getAffectedColRowIdx().forEach((colRow) -> {
                if (affectedColor) {
                    canvas[colRow.getKey()][colRow.getValue()] = false;
                } else {
                    canvas[colRow.getKey()][colRow.getValue()] = true;
                }
            });

            //NA ODWROT
            //mozliwy problem gdy np 2x setColor(true) - nie wiem czy to nie jest niedopuszczalne
            if (ENameOperation.COLOR == operation.getNameValuePair().getKey()) {
                actualColor = !(operation.getNameValuePair().getValue() == 1);
            }
            if (ENameOperation.PEN_STATE == operation.getNameValuePair().getKey()) {
                actualPenState = (operation.getNameValuePair().getValue() == 1) ? PEN_UP : PEN_DOWN;
            }
        }

        if (pointerIndexList < 0) {
            colRowCursor = colRowCursorInitPosition;
        } else {
            Operation operation = operationList.get(pointerIndexList);
            colRowCursor = operation.getColRowCursorPosition();
        }
    }

    @Override
    public void redo(int commands) {
        for (int i = 0; i < commands; i++) {
            Operation operation = operationList.get(++pointerIndexList);
            boolean affectedColor = operation.isAffectedColRowIdxColor();
            operation.getAffectedColRowIdx().forEach((colRow) -> {
                if (affectedColor) {
                    canvas[colRow.getKey()][colRow.getValue()] = true;
                } else {
                    canvas[colRow.getKey()][colRow.getValue()] = false;
                }
            });

            //mozliwy problem gdy np 2x setColor(true) - nie wiem czy to nie jest niedopuszczalne
            if (ENameOperation.COLOR == operation.getNameValuePair().getKey()) {
                actualColor = operation.getNameValuePair().getValue() == 1;
            }
            if (ENameOperation.PEN_STATE == operation.getNameValuePair().getKey()) {
                actualPenState = operation.getNameValuePair().getValue() == 1 ? PEN_DOWN : PEN_UP;
            }
        }

        Operation operation = operationList.get(pointerIndexList);
        colRowCursor = operation.getColRowCursorPosition();
    }

    @Override
    public void setCanvas(boolean[][] canvas) {
        this.canvas = canvas;
    }

    @Override
    public void setInitialPosition(int col, int row) {
        colRowCursorInitPosition = new Pair<>(col, row);
        colRowCursor = new Pair<>(col, row);
        actualPenState = PEN_UP;
        actualColor = true;
        canvas[col][row] = false;
    }

    @Override
    public void maxUndoRedoRepeatCommands(int commands) {
        this.commands = commands;
    }

    @Override
    public String toString() {
        for (int i1 = canvas.length - 1; i1 >= 0; i1--) {
            boolean[] row = canvas[i1];
            for (int i2 = 0; i2 < row.length; i2++) {
                boolean i = row[i2];
                if (canvas[i2][i1]) {
                    System.out.print((char) 27 + "[31mX" + (char) 27 + "[0m");
                } else {
                    System.out.print('-');
                }
                System.out.print("\t");
            }
            System.out.println();
        }

        System.out.println();
        System.out.println("colRowCursor: " + colRowCursor);
        System.out.println();
        System.out.println(pointerIndexList);
        System.out.println();
        return operationList.toString();
    }

}