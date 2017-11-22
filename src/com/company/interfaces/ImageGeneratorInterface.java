package com.company.interfaces;

public interface ImageGeneratorInterface {

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
