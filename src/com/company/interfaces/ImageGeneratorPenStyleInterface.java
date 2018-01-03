package com.company.interfaces;

public interface ImageGeneratorPenStyleInterface {
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