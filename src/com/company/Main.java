package com.company;

import com.company.interfaces.ImageGeneratorConfigurationInterface;
import com.company.interfaces.ImageGeneratorInterface;
import com.company.interfaces.ImageGeneratorPenInterface;
import com.company.interfaces.ImageGeneratorPenStyleInterface;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        ImageGenerator imageGenerator = new ImageGenerator();
        boolean[][] canvas = {{true, false, true, false, true, false, false, false, false, false, false},
                {false, false, false, false, false, false, false, false, false, false, false},
                {false, false, false, false, false, false, false, false, false, false, false},
                {false, false, false, false, false, false, false, false, false, false, false},
                {false, false, false, false, false, false, false, false, false, false, false},
                {false, false, false, false, false, false, false, false, false, false, false},
                {false, false, false, false, false, false, false, false, false, false, false},
                {false, false, false, false, false, false, false, false, false, false, false},
                {false, false, false, false, false, false, false, false, false, false, false},
                {false, false, false, false, false, false, false, false, false, false, false},
                {false, false, false, false, false, false, false, false, false, false, true}};
        imageGenerator.setCanvas(canvas);
        imageGenerator.setInitialPosition(0, 0);
        imageGenerator.setPenStyle(ImageGeneratorPenStyleInterface.PenStyle.INVERSE);
        imageGenerator.up(5);
        imageGenerator.right(5);
        imageGenerator.setPenStyle(ImageGeneratorPenStyleInterface.PenStyle.AND);
        imageGenerator.undo(1);
        imageGenerator.redo(1);
        imageGenerator.repeat(1);
        /*imageGenerator.undo(1);
        imageGenerator.redo(1);
        imageGenerator.undo(1);
        imageGenerator.redo(1);
        imageGenerator.right(10);
        imageGenerator.repeat(3);
        imageGenerator.undo(2);
        imageGenerator.redo(1);
        imageGenerator.up(3);
        imageGenerator.repeat(1);
        imageGenerator.right(1);
        imageGenerator.down(1);
        imageGenerator.left(1);
        imageGenerator.repeat(1);
        imageGenerator.undo(2);
        imageGenerator.redo(1);*/

        System.out.print(imageGenerator.toString());


        System.out.println(canvas[5][1]);
    }
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

class AffectedColRowIdxColor {
    private Pair<Integer, Integer> affectedColRowIdx;
    private boolean color;

    public AffectedColRowIdxColor() {
    }

    public AffectedColRowIdxColor(Pair<Integer, Integer> affectedColRowIdx, boolean color) {
        this.affectedColRowIdx = affectedColRowIdx;
        this.color = color;
    }

    public Pair<Integer, Integer> getAffectedColRowIdx() {
        return affectedColRowIdx;
    }

    public boolean getColor() {
        return color;
    }
}

class Operation {
    private Pair<ENameOperation, Integer> nameValuePair;
    private List<AffectedColRowIdxColor> affectedColRowIdxs;

    private Pair<Integer, Integer> colRowCursorPosition;

    public Operation() {
    }

    public Operation(Pair<ENameOperation, Integer> nameValuePair, List<AffectedColRowIdxColor> affectedColRowIdxs) {
        this.nameValuePair = nameValuePair;
        this.affectedColRowIdxs = affectedColRowIdxs;
    }

    public Operation(List<AffectedColRowIdxColor> affectedColRowIdxs) {
        this.affectedColRowIdxs = affectedColRowIdxs;
    }

    public Pair<ENameOperation, Integer> getNameValuePair() {
        return nameValuePair;
    }

    public List<AffectedColRowIdxColor> getAffectedColRowIdxs() {
        return affectedColRowIdxs;
    }

    public void setColRowCursorPosition(Pair<Integer, Integer> colRowCursorPosition) {
        this.colRowCursorPosition = colRowCursorPosition;
    }

    public Pair<Integer, Integer> getColRowCursorPosition() {
        return colRowCursorPosition;
    }
}

class OperationStyle extends Operation {
    private ImageGeneratorPenStyleInterface.PenStyle newStyle;
    private ImageGeneratorPenStyleInterface.PenStyle oldStyle;

    public OperationStyle(ImageGeneratorPenStyleInterface.PenStyle newStyle, ImageGeneratorPenStyleInterface.PenStyle oldStyle, List<AffectedColRowIdxColor> affectedColRowIdxs) {
        super(affectedColRowIdxs);
        this.newStyle = newStyle;
        this.oldStyle = oldStyle;
    }

    public ImageGeneratorPenStyleInterface.PenStyle getNewStyle() {
        return newStyle;
    }

    public ImageGeneratorPenStyleInterface.PenStyle getOldStyle() {
        return oldStyle;
    }
}

class ImageGenerator implements ImageGeneratorConfigurationInterface, ImageGeneratorInterface, ImageGeneratorPenInterface, ImageGeneratorPenStyleInterface {
    private final ImageGeneratorPenInterface.PenState PEN_UP = ImageGeneratorPenInterface.PenState.UP;
    private final ImageGeneratorPenInterface.PenState PEN_DOWN = ImageGeneratorPenInterface.PenState.DOWN;
    private int commands; //TODO max repeat(commands) && wspolnie max undo(sigma[commands]) ORAZ wspolnie max redo(sigma[commands])
    boolean[][] canvas; //TODO poczatkowa wartosc moze byc jakakolwiek
    Pair<Integer, Integer> colRowCursor;
    Pair<Integer, Integer> colRowCursorInitPosition;

    int pointerIndexList;
    List<Operation> operationList;

    ImageGeneratorPenInterface.PenState actualPenState;
    ImageGeneratorPenStyleInterface.PenStyle actualStyle;
    boolean actualColor;

    public ImageGenerator() {
        operationList = new ArrayList<>();
        this.pointerIndexList = -1;

        //stan poczatkowy obiektu
        actualPenState = PEN_DOWN;
        actualColor = true;
        actualStyle = PenStyle.SOLID;
    }

    private boolean getActualCanvasColor() {
        return canvas[colRowCursor.getKey()][colRowCursor.getValue()];
    }

    //region ImageGeneratorPenStyleInterface

    @Override
    public void setPenStyle(PenStyle style) {
        //actualStyle = style; //style - nowy styl, actualStyle - stary styl.
        //List<Pair<Integer, Integer>> affectedColRowIdx = new ArrayList<>();
        List<AffectedColRowIdxColor> affectedColRowIdxList = new ArrayList<>();

        if (PEN_DOWN == actualPenState) {
            boolean canvasColor = getActualCanvasColor(); //stara wartosc
            canvas[colRowCursor.getKey()][colRowCursor.getValue()] = style.value(actualColor, canvasColor);
            if (canvasColor != getActualCanvasColor()) {
                AffectedColRowIdxColor affectedColRowIdxColor = new AffectedColRowIdxColor(colRowCursor, !canvasColor);
                affectedColRowIdxList.add(affectedColRowIdxColor);
            }
        }

        OperationStyle operation = new OperationStyle(style, actualStyle, affectedColRowIdxList); //style - nowy styl, actualStyle - stary styl.
        operationList.add(++pointerIndexList, operation);

        operation.setColRowCursorPosition(colRowCursor);

        actualStyle = style;
    }

    @Override
    public PenStyle getPenStyle() {
        return actualStyle;
    }

    //endregion

    //region ImageGeneratorPenInterface

    @Override
    public void setPenState(PenState state) {
        actualPenState = state;

        //List<Pair<Integer, Integer>> affectedColRowIdx = new ArrayList<>();
        List<AffectedColRowIdxColor> affectedColRowIdxList = new ArrayList<>();
        if (actualPenState == PEN_DOWN) {
            boolean canvasColor = getActualCanvasColor(); //stara wartosc
            canvas[colRowCursor.getKey()][colRowCursor.getValue()] = this.actualStyle.value(actualColor, canvasColor);
            if (canvasColor != getActualCanvasColor()) {
                AffectedColRowIdxColor affectedColRowIdxColor = new AffectedColRowIdxColor(colRowCursor, !canvasColor);
                affectedColRowIdxList.add(affectedColRowIdxColor);
            }
        }

        //state - DOWN = true = 1, UP = false = 0
        Integer stateInt = (state == PEN_DOWN) ? 1 : 0;
        Operation operation = new Operation(new Pair<>(ENameOperation.PEN_STATE, stateInt), affectedColRowIdxList);
        operationList.add(++pointerIndexList, operation);

        operation.setColRowCursorPosition(colRowCursor);
    }

    @Override
    public void setColor(boolean color) {
        actualColor = color;
        //List<Pair<Integer, Integer>> affectedColRowIdx = new ArrayList<>();
        List<AffectedColRowIdxColor> affectedColRowIdxList = new ArrayList<>();
        if (actualPenState == PEN_DOWN) {
            boolean canvasColor = getActualCanvasColor(); //stara wartosc
            canvas[colRowCursor.getKey()][colRowCursor.getValue()] = this.actualStyle.value(actualColor, canvasColor);
            if (canvasColor != getActualCanvasColor()) {
                AffectedColRowIdxColor affectedColRowIdxColor = new AffectedColRowIdxColor(colRowCursor, !canvasColor);
                affectedColRowIdxList.add(affectedColRowIdxColor);
            }
        }

        //color - true = 1, false = 0
        Integer colorInt = color ? 1 : 0;
        Operation operation = new Operation(new Pair<>(ENameOperation.COLOR, colorInt), affectedColRowIdxList);
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
        //List<Pair<Integer, Integer>> affectedColRowIdx = new ArrayList<>();
        List<AffectedColRowIdxColor> affectedColRowIdxList = new ArrayList<>();
        if (actualPenState == PEN_DOWN) {
            for (int i = 1; i <= steps; i++) {
                Integer y = colRowCursor.getValue() + i;
                boolean canvasColor = canvas[colRowCursor.getKey()][y]; //stara wartosc
                canvas[colRowCursor.getKey()][y] = this.actualStyle.value(actualColor, canvasColor);
                if (canvasColor != canvas[colRowCursor.getKey()][y]) {
                    AffectedColRowIdxColor affectedColRowIdxColor = new AffectedColRowIdxColor(new Pair<>(colRowCursor.getKey(), y), !canvasColor);
                    affectedColRowIdxList.add(affectedColRowIdxColor);
                }
            }
        }
        colRowCursor = new Pair<>(colRowCursor.getKey(), colRowCursor.getValue() + steps);

        Operation operation = new Operation(new Pair<>(ENameOperation.UP, steps), affectedColRowIdxList);
        operationList.add(++pointerIndexList, operation);

        operation.setColRowCursorPosition(colRowCursor);
    }

    // [][ROW=-STEPs]
    @Override
    public void down(int steps) {
        //List<Pair<Integer, Integer>> affectedColRowIdx = new ArrayList<>();
        List<AffectedColRowIdxColor> affectedColRowIdxList = new ArrayList<>();
        if (actualPenState == PEN_DOWN) {
            for (int i = 1; i <= steps; i++) {
                Integer y = colRowCursor.getValue() - i;
                boolean canvasColor = canvas[colRowCursor.getKey()][y]; //stara wartosc
                canvas[colRowCursor.getKey()][y] = this.actualStyle.value(actualColor, canvasColor);
                if (canvasColor != canvas[colRowCursor.getKey()][y]) {
                    AffectedColRowIdxColor affectedColRowIdxColor = new AffectedColRowIdxColor(new Pair<>(colRowCursor.getKey(), y), !canvasColor);
                    affectedColRowIdxList.add(affectedColRowIdxColor);
                }
            }
        }
        colRowCursor = new Pair<>(colRowCursor.getKey(), colRowCursor.getValue() - steps);

        Operation operation = new Operation(new Pair<>(ENameOperation.DOWN, steps), affectedColRowIdxList);
        operationList.add(++pointerIndexList, operation);

        operation.setColRowCursorPosition(colRowCursor);
    }

    // [COL=-STEP][]
    @Override
    public void left(int steps) {
        //List<Pair<Integer, Integer>> affectedColRowIdx = new ArrayList<>();
        List<AffectedColRowIdxColor> affectedColRowIdxList = new ArrayList<>();
        if (actualPenState == PEN_DOWN) {
            for (int i = 1; i <= steps; i++) {
                Integer x = colRowCursor.getKey() - i;
                boolean canvasColor = canvas[x][colRowCursor.getValue()]; //stara wartosc
                canvas[x][colRowCursor.getValue()] = this.actualStyle.value(actualColor, canvasColor);
                if (canvasColor != canvas[x][colRowCursor.getValue()]) {
                    AffectedColRowIdxColor affectedColRowIdxColor = new AffectedColRowIdxColor(new Pair<>(x, colRowCursor.getValue()), !canvasColor);
                    affectedColRowIdxList.add(affectedColRowIdxColor);
                }
            }
        }
        colRowCursor = new Pair<>(colRowCursor.getKey() - steps, colRowCursor.getValue());

        Operation operation = new Operation(new Pair<>(ENameOperation.LEFT, steps), affectedColRowIdxList);
        operationList.add(++pointerIndexList, operation);

        operation.setColRowCursorPosition(colRowCursor);
    }

    // [COL=+STEP][]
    @Override
    public void right(int steps) {
        //List<Pair<Integer, Integer>> affectedColRowIdx = new ArrayList<>();
        List<AffectedColRowIdxColor> affectedColRowIdxList = new ArrayList<>();
        if (actualPenState == PEN_DOWN) {
            for (int i = 1; i <= steps; i++) {
                Integer x = colRowCursor.getKey() + i;
                boolean canvasColor = canvas[x][colRowCursor.getValue()]; //stara wartosc
                canvas[x][colRowCursor.getValue()] = this.actualStyle.value(actualColor, canvasColor);
                if (canvasColor != canvas[x][colRowCursor.getValue()]) {
                    AffectedColRowIdxColor affectedColRowIdxColor = new AffectedColRowIdxColor(new Pair<>(x, colRowCursor.getValue()), !canvasColor);
                    affectedColRowIdxList.add(affectedColRowIdxColor);
                }
            }
        }
        colRowCursor = new Pair<>(colRowCursor.getKey() + steps, colRowCursor.getValue());

        Operation operation = new Operation(new Pair<>(ENameOperation.RIGHT, steps), affectedColRowIdxList);
        operationList.add(++pointerIndexList, operation);

        operation.setColRowCursorPosition(colRowCursor);
    }

    // zapisujemy w stosie zadan
    @Override
    public void repeat(int commands) {
        //List<Pair<Integer, Integer>> affectedColRowIdx = new ArrayList<>();
        List<AffectedColRowIdxColor> affectedColRowIdxList = new ArrayList<>();
        pointerIndexList -= commands - 1;
        for (int i = 0; i < commands; i++) {
            Operation operation = operationList.get(pointerIndexList);
            //TODO sprawdzic
            if (operation instanceof OperationStyle) {
                OperationStyle operationStyle = (OperationStyle) operationList.get(pointerIndexList);
                setPenStyle(operationStyle.getNewStyle());
            } else {
                Pair<ENameOperation, Integer> idxOperation = operation.getNameValuePair();
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
            }

            //nie powielamy w operationList przy repeat
            Operation operationTemp = operationList.get(pointerIndexList);
            affectedColRowIdxList.addAll(operationTemp.getAffectedColRowIdxs());
            operationList.remove(pointerIndexList);
        }

        //ew. problem z actualColor, bo tutaj moze raz malowac na false, raz na true
        // - problem chyba znikl przy zmianach dla PO6
        Operation operation = new Operation(new Pair<>(ENameOperation.REPEAT, commands), affectedColRowIdxList);
        operationList.add(pointerIndexList, operation);

        operation.setColRowCursorPosition(colRowCursor);
    }

    @Override
    public void undo(int commands) {
        for (int i = 0; i < commands; i++, pointerIndexList--) {
            Operation operation = operationList.get(pointerIndexList);
            //boolean affectedColor = operation.isAffectedColRowIdxColor();
            operation.getAffectedColRowIdxs().forEach((colRowColor) -> {
                canvas[colRowColor.getAffectedColRowIdx().getKey()][colRowColor.getAffectedColRowIdx().getValue()] = !colRowColor.getColor();
            });

            //NA ODWROT
            //mozliwy problem gdy np 2x setColor(true) - nie wiem czy to nie jest niedopuszczalne
            //TODO sprawdzic
            if (operation instanceof OperationStyle) {
                OperationStyle operationStyle = (OperationStyle) operation;
                actualStyle = operationStyle.getOldStyle();
            } else {
                if (ENameOperation.COLOR == operation.getNameValuePair().getKey()) {
                    actualColor = !(operation.getNameValuePair().getValue() == 1);
                }
                if (ENameOperation.PEN_STATE == operation.getNameValuePair().getKey()) {
                    actualPenState = (operation.getNameValuePair().getValue() == 1) ? PEN_UP : PEN_DOWN;
                }
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
            //boolean affectedColor = operation.isAffectedColRowIdxColor();
            operation.getAffectedColRowIdxs().forEach((colRowColor) -> {
                canvas[colRowColor.getAffectedColRowIdx().getKey()][colRowColor.getAffectedColRowIdx().getValue()] = colRowColor.getColor();
            });

            //mozliwy problem gdy np 2x setColor(true) - nie wiem czy to nie jest niedopuszczalne
            if (operation instanceof OperationStyle) {
                OperationStyle operationStyle = (OperationStyle) operation;
                actualStyle = operationStyle.getNewStyle();
            } else {
                if (ENameOperation.COLOR == operation.getNameValuePair().getKey()) {
                    actualColor = operation.getNameValuePair().getValue() == 1;
                }
                if (ENameOperation.PEN_STATE == operation.getNameValuePair().getKey()) {
                    actualPenState = operation.getNameValuePair().getValue() == 1 ? PEN_DOWN : PEN_UP;
                }
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