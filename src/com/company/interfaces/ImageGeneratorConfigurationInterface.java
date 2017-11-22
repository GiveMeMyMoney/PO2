package com.company.interfaces;

public interface ImageGeneratorConfigurationInterface {
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
