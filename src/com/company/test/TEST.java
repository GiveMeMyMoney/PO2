import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

interface ImageGeneratorConfigurationInterface {
    /**
     * Metoda ustawia plotno, po ktorym nalezy rysowac.
     *
     * @param canvas
     *            plotno obrazu
     */
    public void setCanvas(boolean[][] canvas);

    /**
     * Metoda ustawia poczatkowa pozycje piora. Pioro zostaje ustawione na danej
     * pozycji i zostawia na niej slad.
     *
     * @param col
     *            kolumna, w ktorej umieszczane jest pioro
     * @param row
     *            wiersz, w ktorym umieszczane jest pioro
     */
    public void setInitialPosition(int col, int row);

    /**
     * Ustalenie liczby maksymalnej liczby polecen, ktore mozna cofnac, przywrocic
     * lub powtorzyc.
     *
     * @param commands
     *            maksymalna liczba polecen, ktorej moga dotyczyc operacje
     *            undo/redo/repeat. Uwaga: w przypadku undo/redo chodzi o
     *            <b>laczna</b> liczbe polecen, ktore sa wycofywane. Czyli, gdy
     *            commands to 10, to moge wykonac undo(5); undo(3); undo(2).
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
     * @param steps
     *            liczba krokow o jaka pioro przesunie sie w gore.
     */
    public void up(int steps);

    /**
     * Pioro przemieszcza sie steps krokow do dolu. Jesli wiersz, w ktorym
     * poczatkowo znajduje sie pisak to row, to po wykonaniu polecenia pioro
     * przemiesza sie do wiersza o numerze row - steps. Pozycje plotna od
     * [col][row-1] do [col][row-N] zostaja zamalowane.
     *
     * @param steps
     *            liczba krokow o jaka pioro przesunie sie w dol.
     */
    public void down(int steps);

    /**
     * Pioro przemieszcza sie steps krokow w lewo. Jesli kolumna, w ktorej
     * poczatkowo znajduje sie pisak to col, to po wykonaniu polecenia pioro
     * przemiesza sie do kolumny o numerze col - steps. Pozycje plotna od
     * [col-1][row] do [col-N][row] zostaja zamalowane.
     *
     * @param steps
     *            liczba krokow o jaka pioro przesunie sie w lewo.
     */
    public void left(int steps);

    /**
     * Pioro przemieszcza sie steps krokow w prawo. Jesli kolumna, w ktorej
     * poczatkowo znajduje sie pisak to col, to po wykonaniu polecenia pioro
     * przemiesza sie do kolumny o numerze col + steps. Pozycje plotna od
     * [col+1][row] do [col+N][row] zostaja zamalowane.
     *
     * @param steps
     *            liczba krokow o jaka pioro przesunie sie w prawo.
     */
    public void right(int steps);

    /**
     * Polecenie powtorzenia ostatnich commands polecen. Polecenie nie laczy sie z
     * undo/redo. Czyli, sekwencja undo(1) repeat(1) oznacza powtorzenie polecenia,
     * ktore "odslonila" operacja undo, nie zas dodatkowe wykonanie operacji undo.
     *
     * @param commands
     *            liczba polecen do powtorzenia
     */
    public void repeat(int commands);

    /**
     * Usuniecie efektu ostatnich commands polecen. Undo nie jest traktowane jako
     * polecene, czyli sekwencja undo(2) i undo(1) prowadzi do wycofania ostatnich 3
     * polecen, a nie do przywrocenia 2 polecen wycofanych za pomoca pierwszego
     * uzycia undo.
     *
     * @param commands
     *            liczba polecen do wycofania
     */
    public void undo(int commands);

    /**
     * Przywrocenie efektu commands wycofanych polecen. Redo nie jest traktowane
     * jako polecenie. Sekwencja redo(2) i redo(1) ma doprowadzic do odtworzenia
     * dzialania 3 polecen usunietych przez undo.
     *
     * @param commands
     *            liczba polecen, ktorych efekt nalezy przywrocic
     */
    public void redo(int commands);
}

enum ENameOperation {
    UP,
    DOWN,
    RIGHT,
    LEFT,
    REPEAT
}

class Operation {
    private Pair<ENameOperation, Integer> nameValuePair;
    private List<Pair<Integer, Integer>> affectedColRowIdx;

    private Pair<Integer, Integer> colRowCursorPosition;

    public Operation(Pair<ENameOperation, Integer> nameValuePair, List<Pair<Integer, Integer>> affectedColRowIdx) {
        this.nameValuePair = nameValuePair;
        this.affectedColRowIdx = affectedColRowIdx;
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

    public Pair<Integer, Integer> getColRowCursorPosition() {
        return colRowCursorPosition;
    }
}

class ImageGenerator implements ImageGeneratorConfigurationInterface, ImageGeneratorInterface {
    private int commands; //TODO max repeat(commands) && wspolnie max undo(sigma[commands]) ORAZ wspolnie max redo(sigma[commands])
    boolean[][] canvas; //TODO poczatkowa wartosc moze byc jakakolwiek
    Pair<Integer, Integer> colRowCursor;

    int pointerIndexList;
    List<Operation> operationList;

    public ImageGenerator() {
        operationList = new ArrayList<>();
        this.pointerIndexList = -1;
    }

    // [][ROW=+STEPs]
    @Override
    public void up(int steps) {
        List<Pair<Integer, Integer>> affectedColRowIdx = new ArrayList<>();
        for (int i = 1; i <= steps; i++) {
            if (!canvas[colRowCursor.getKey()][colRowCursor.getValue() + i]) {
                canvas[colRowCursor.getKey()][colRowCursor.getValue() + i] = true;
                affectedColRowIdx.add(new Pair<>(colRowCursor.getKey(), colRowCursor.getValue() + i));
            }

        }
        colRowCursor = new Pair<>(colRowCursor.getKey(), colRowCursor.getValue() + steps);

        Operation operation = new Operation(new Pair<>(ENameOperation.UP, steps), affectedColRowIdx);
        operationList.add(++pointerIndexList, operation);

        operation.setColRowCursorPosition(colRowCursor);
    }

    // [][ROW=-STEPs]
    @Override
    public void down(int steps) {
        List<Pair<Integer, Integer>> affectedColRowIdx = new ArrayList<>();
        for (int i = 1; i <= steps; i++) {
            if (!canvas[colRowCursor.getKey()][colRowCursor.getValue() - i]) {
                canvas[colRowCursor.getKey()][colRowCursor.getValue() - i] = true;
                affectedColRowIdx.add(new Pair<>(colRowCursor.getKey(), colRowCursor.getValue() - i));
            }
        }
        colRowCursor = new Pair<>(colRowCursor.getKey(), colRowCursor.getValue() - steps);

        Operation operation = new Operation(new Pair<>(ENameOperation.DOWN, steps), affectedColRowIdx);
        operationList.add(++pointerIndexList, operation);

        operation.setColRowCursorPosition(colRowCursor);
    }

    // [COL=-STEP][]
    @Override
    public void left(int steps) {
        List<Pair<Integer, Integer>> affectedColRowIdx = new ArrayList<>();
        for (int i = 1; i <= steps; i++) {
            if (!canvas[colRowCursor.getKey() - i][colRowCursor.getValue()]) {
                canvas[colRowCursor.getKey() - i][colRowCursor.getValue()] = true;
                affectedColRowIdx.add(new Pair<>(colRowCursor.getKey() - i, colRowCursor.getValue()));
            }
        }
        colRowCursor = new Pair<>(colRowCursor.getKey() - steps, colRowCursor.getValue());

        Operation operation = new Operation(new Pair<>(ENameOperation.LEFT, steps), affectedColRowIdx);
        operationList.add(++pointerIndexList, operation);

        operation.setColRowCursorPosition(colRowCursor);
    }

    // [COL=+STEP][]
    @Override
    public void right(int steps) {
        List<Pair<Integer, Integer>> affectedColRowIdx = new ArrayList<>();
        for (int i = 1; i <= steps; i++) {
            if (!canvas[colRowCursor.getKey() + i][colRowCursor.getValue()]) {
                canvas[colRowCursor.getKey() + i][colRowCursor.getValue()] = true;
                affectedColRowIdx.add(new Pair<>(colRowCursor.getKey() + i, colRowCursor.getValue()));
            }
        }
        colRowCursor = new Pair<>(colRowCursor.getKey() + steps, colRowCursor.getValue());

        Operation operation = new Operation(new Pair<>(ENameOperation.RIGHT, steps), affectedColRowIdx);
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
            }

            Operation operation = operationList.get(pointerIndexList);
            affectedColRowIdx.addAll(operation.getAffectedColRowIdx());
            operationList.remove(pointerIndexList);
        }

        Operation operation = new Operation(new Pair<>(ENameOperation.REPEAT, commands), affectedColRowIdx);
        operationList.add(pointerIndexList, operation);

        operation.setColRowCursorPosition(colRowCursor);
    }

    @Override
    public void undo(int commands) {
        for (int i = 0; i < commands; i++, pointerIndexList--) {
            Operation operation = operationList.get(pointerIndexList);
            operation.getAffectedColRowIdx().forEach(colRow -> canvas[colRow.getKey()][colRow.getValue()] = false);
        }

        Operation operation = operationList.get(pointerIndexList);
        colRowCursor = operation.getColRowCursorPosition();
    }

    @Override
    public void redo(int commands) {
        for (int i = 0; i < commands; i++) {
            Operation operation = operationList.get(++pointerIndexList);
            operation.getAffectedColRowIdx().forEach(colRow -> canvas[colRow.getKey()][colRow.getValue()] = true);
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
        colRowCursor = new Pair<>(col, row);
        canvas[col][row] = true;
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
