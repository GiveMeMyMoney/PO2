package com.company;

import com.company.interfaces.ImageGeneratorConfigurationInterface;
import com.company.interfaces.ImageGeneratorInterface;
import com.company.interfaces.ImageGeneratorPenInterface;
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

        imageGenerator.up(4);
        imageGenerator.undo(1);
        imageGenerator.redo(1);
        imageGenerator.undo(1);
        imageGenerator.redo(1);
        imageGenerator.right(10);
        /*imageGenerator.repeat(3);
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
    PEN_UP,
    PEN_DOWN,
    SET_COLOR
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

class ImageGenerator implements ImageGeneratorConfigurationInterface, ImageGeneratorInterface, ImageGeneratorPenInterface {
    private int commands; //TODO max repeat(commands) && wspolnie max undo(sigma[commands]) ORAZ wspolnie max redo(sigma[commands])
    boolean[][] canvas; //TODO poczatkowa wartosc moze byc jakakolwiek
    Pair<Integer, Integer> colRowCursor;
    Pair<Integer, Integer> colRowCursorInitPosition;

    int pointerIndexList;
    List<Operation> operationList;

    //acutal PEN_STATE oraz actual COLOR

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
        colRowCursorInitPosition = new Pair<>(col, row);
        colRowCursor = new Pair<>(col, row);
        canvas[col][row] = true;
    }

    @Override
    public void maxUndoRedoRepeatCommands(int commands) {
        this.commands = commands;
    }

    //ImageGeneratorPenInterface





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


