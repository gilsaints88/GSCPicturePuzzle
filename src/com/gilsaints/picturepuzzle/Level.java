package com.gilsaints.picturepuzzle;

public class Level {

        private String name;
        private int rows = 0;
        private int columns = 0;

        public Level(String name, int rows, int columns) {
                super();
                this.name = name;
                this.rows = rows;
                this.columns = columns;
        }

        public String getName() {
                return name;
        }

        public int getRows() {
                return rows;
        }

        public int getColumns() {
                return columns;
        }

}
