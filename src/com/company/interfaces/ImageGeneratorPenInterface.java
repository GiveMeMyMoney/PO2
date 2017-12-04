package com.company.interfaces;

public interface ImageGeneratorPenInterface {
	/**
	 * Typ wyliczeniowy reprezentujacy stan piora.
	 * 
	 * @author oramus
	 *
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
	 * @param state
	 *            nowy stan piora
	 */
	public void setPenState(PenState state);

	/**
	 * Polecenie ustawienia "koloru" piora. Jesli "kolor" ustawiony jest np. na
	 * true, to pioro (o ile jest opuszczone) pozostawia slad umieszczajac pod
	 * odpowiednimi indeksami tablicy canvas wartosc true. Jesli pioro jest
	 * opuszczone to zmiana "koloru" natychmiast zmienia odpowiednia pozycje tablicy
	 * canvas. Polecenie jest uwzgledniane w pracy metod undo/redo/repeat.
	 * 
	 * @param color
	 *            nowy "kolor" piora.
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
