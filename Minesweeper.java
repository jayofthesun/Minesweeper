import java.util.*;
import tester.*;
import javalib.impworld.*;
import java.awt.Color;
import javalib.worldimages.*;

public class Minesweeper extends World {

  int height; // Number of cells height wise
  int width; // Number of cells width wise
  int numOfMines;
  ArrayList<ArrayList<Cell>> cells = new ArrayList<ArrayList<Cell>>();
  int cellSize = 20;
  Random rand;

  Minesweeper(int height, int width, int numOfMines, Random rand) {
    this.height = height;
    this.width = width;
    this.rand = rand;
    if (numOfMines > this.height * this.width) {
      this.numOfMines = this.height * this.width;
    }
    else {
      this.numOfMines = numOfMines;
    }

    this.initCells();
    this.initMines();
  }

  Minesweeper(int height, int width, int numOfMines) {
    this(height, width, numOfMines, new Random());
  }

  // initializes the cells on the board
  void initCells() {

    // Creates and Connects all the Cells
    for (int i = 0; i < this.height; i++) {
      ArrayList<Cell> newRow = new ArrayList<Cell>();

      for (int j = 0; j < this.width; j++) {
        Cell newCell = new Cell();

        // Connect to the previous
        if (j > 0) {
          newCell.connect(newRow.get(j - 1));
        }
        // Connect to the top
        if (i > 0) {
          newCell.connect(cells.get(i - 1).get(j));
        }
        // Connect to the diagonal left
        if (i > 0 && j > 0) {
          newCell.connect(cells.get(i - 1).get(j - 1));
        }
        // Connect to the diagonal right
        if (i > 0 && j < this.width - 1) {
          newCell.connect(cells.get(i - 1).get(j + 1));
        }
        newRow.add(newCell);
      }
      cells.add(newRow);
    }

  }

  // initializes the mines on the board
  void initMines() {
    // Copy Array
    ArrayList<ArrayList<Cell>> tempCells = new ArrayList<ArrayList<Cell>>();
    for (ArrayList<Cell> row : cells) {
      ArrayList<Cell> tempRow = new ArrayList<Cell>();
      for (Cell cell : row) {
        tempRow.add(cell);
      }
      tempCells.add(tempRow);
    }

    // Randomizes and adds mines
    for (int i = 0; i < this.numOfMines; i += 1) {
      int randRowIndex = rand.nextInt(tempCells.size());
      ArrayList<Cell> randRow = tempCells.get(randRowIndex);

      int randCellIndex = rand.nextInt(tempCells.get(randRowIndex).size());
      randRow.remove(randCellIndex).hasBomb = true;

      // Removes the row from cells if its an empty ArrayList<Cell>
      if (randRow.size() == 0) {
        tempCells.remove(randRowIndex);
      }
    }
  }

  @Override
  // Returns a WorldScene representing the current world state
  public WorldScene makeScene() {

    WorldImage board = new EmptyImage();

    for (ArrayList<Cell> row : cells) {
      WorldImage rowImage = new EmptyImage();
      for (Cell cell : row) {
        rowImage = cell.draw(rowImage);
      }
      board = new AboveImage(board, rowImage);
    }

    WorldScene ws = new WorldScene((int) (board.getWidth() / 2), (int) (board.getHeight() / 2));
    ws.placeImageXY(board, (int) (board.getWidth() / 2), (int) (board.getHeight() / 2));

    return ws;
  }

  // Changes the world on the mouse click
  public void onMouseClicked(Posn pos, String button) {

    int yUpperBound = this.cellSize;
    for (ArrayList<Cell> row : cells) {
      int xUpperBound = this.cellSize;

      // Finds if the y position is correct, then looks through the row
      if (pos.y < yUpperBound && pos.y > yUpperBound - this.cellSize) {
        for (Cell cell : row) {

          if (pos.x < xUpperBound && pos.x > xUpperBound - this.cellSize) {

            // Changes the cell based on the click
            if (button.equals("LeftButton")) {
              cell.isLeftClicked = true;
            }
            else if (button.equals("RightButton")) {
              if (cell.flagged) {
                cell.flagged = false;
              }
              else {
                cell.flagged = true;
              }
            }
          }
          xUpperBound += this.cellSize;
        }
      }
      yUpperBound += this.cellSize;
    }
  }

  // counts the number of mines on the board (for testing purposes)
  int mineCounter() {
    int count = 0;

    for (ArrayList<Cell> row : cells) {
      for (Cell cell : row) {
        if (cell.hasBomb) {
          count += 1;
        }
      }
    }
    return count;
  }

}

class ExamplesMinesweeper {
  Minesweeper empty;
  Minesweeper m1;
  Minesweeper m2;
  Minesweeper m3;
  Minesweeper m4;
  Cell c1;
  Cell c2;
  Cell c3;
  ArrayList<Cell> a1;
  ArrayList<Cell> a2;
  ArrayList<Cell> a3;
  WorldScene ws1;

  void initData() {
    empty = new Minesweeper(0, 0, 0);
    m1 = new Minesweeper(3, 3, 2, new Random(10));
    m2 = new Minesweeper(3, 3, 15);
    m3 = new Minesweeper(25, 20, 30);
    m4 = new Minesweeper(1, 2, 1, new Random(15));

    c1 = new Cell();
    c2 = new Cell();
    c3 = new Cell();

    a1 = new ArrayList<Cell>();
    a2 = new ArrayList<Cell>();
    a3 = new ArrayList<Cell>();

    ws1 = m1.getEmptyScene();
  }

  void testMinesweeper(Tester t) {
    this.initData();
    m1.bigBang(m1.cellSize * m1.width, m1.cellSize * m1.height);
  }

  void testConnect(Tester t) {
    this.initData();
    t.checkExpect(c1.neighbors, a1);
    t.checkExpect(c2.neighbors, a1);
    t.checkExpect(c3.neighbors, a1);

    c1.connect(c2);
    a1.add(c2);
    a2.add(c1);
    t.checkExpect(c1.neighbors, a1);
    t.checkExpect(c2.neighbors, a2);

    c3.connect(c1);
    a1.add(c3);
    a3.add(c1);
    t.checkExpect(c1.neighbors, a1);
    t.checkExpect(c3.neighbors, a3);
  }

  void testMineCount(Tester t) {
    this.initData();

    t.checkExpect(c1.mineCount(), 0);
    t.checkExpect(m1.cells.get(0).get(0).mineCount(), 1);
    t.checkExpect(m1.cells.get(0).get(1).mineCount(), 1);
    t.checkExpect(m1.cells.get(0).get(2).mineCount(), 1);

    t.checkExpect(m1.cells.get(1).get(0).mineCount(), 2);
    t.checkExpect(m1.cells.get(1).get(1).mineCount(), 2);
    t.checkExpect(m1.cells.get(1).get(2).mineCount(), 1);

    t.checkExpect(m1.cells.get(2).get(0).mineCount(), 0);
    t.checkExpect(m1.cells.get(2).get(2).mineCount(), 0);
    t.checkExpect(m1.cells.get(2).get(0).mineCount(), 0);
  }

  void testDraw(Tester t) {
    this.initData();
    WorldImage drawnCell1 = new RectangleImage(20, 20, "solid", Color.LIGHT_GRAY);
    drawnCell1 = new FrameImage(drawnCell1);
    drawnCell1 = new BesideImage(new EmptyImage(), drawnCell1);
    t.checkExpect(c1.draw(new EmptyImage()), drawnCell1);

    c1.flagged = true;
    WorldImage drawnCell2 = new OverlayImage(
        new EquilateralTriangleImage(20 / 2, "solid", Color.red),
        new RectangleImage(20, 20, "solid", Color.LIGHT_GRAY));
    drawnCell2 = new FrameImage(drawnCell2);
    drawnCell2 = new BesideImage(new EmptyImage(), drawnCell2);
    t.checkExpect(c1.draw(new EmptyImage()), drawnCell2);

    c2.isLeftClicked = true;
    WorldImage drawnCell3 = new OverlayImage(new TextImage(Integer.toString(0), Color.black),
        new RectangleImage(20, 20, "solid", Color.LIGHT_GRAY));
    drawnCell3 = new FrameImage(drawnCell3);
    drawnCell3 = new BesideImage(new EmptyImage(), drawnCell3);
    t.checkExpect(c2.draw(new EmptyImage()), drawnCell3);

    c2.hasBomb = true;
    WorldImage drawnCell4 = new OverlayImage(new CircleImage(5, OutlineMode.SOLID, Color.black),
        new RectangleImage(20, 20, "solid", Color.DARK_GRAY));
    drawnCell4 = new FrameImage(drawnCell4);
    drawnCell4 = new BesideImage(new EmptyImage(), drawnCell4);
    t.checkExpect(c2.draw(new EmptyImage()), drawnCell4);
  }

  void testMineCounter(Tester t) {
    t.checkExpect(empty.mineCounter(), 0);
    t.checkExpect(m1.mineCounter(), 2);
    t.checkExpect(m2.mineCounter(), 9);
  }

  void testInitializedCorrectly(Tester t) {
    this.initData();

    t.checkExpect(empty.cells, new ArrayList<Cell>());
    t.checkExpect(empty.mineCounter(), 0);
    t.checkExpect(m1.mineCounter(), 2);
    t.checkExpect(m2.mineCounter(), 9);
    t.checkExpect(m3.mineCounter(), 30);

    a1.add(m1.cells.get(0).get(1));
    a1.add(m1.cells.get(1).get(0));
    a1.add(m1.cells.get(1).get(1));
    t.checkExpect(m1.cells.get(0).get(0).neighbors, a1);

    a2.add(m1.cells.get(1).get(0));
    a2.add(m1.cells.get(0).get(1));
    a2.add(m1.cells.get(0).get(0));
    a2.add(m1.cells.get(0).get(2));

    a2.add(m1.cells.get(1).get(2));
    a2.add(m1.cells.get(2).get(0));
    a2.add(m1.cells.get(2).get(1));
    a2.add(m1.cells.get(2).get(2));
    t.checkExpect(m1.cells.get(1).get(1).neighbors, a2);

  }

  void testMakeScene(Tester t) {
    this.initData();

    t.checkExpect(empty.makeScene(), ws1);

    WorldImage img = new EmptyImage();
    img = m4.cells.get(0).get(0).draw(img);
    img = m4.cells.get(0).get(1).draw(img);
    WorldScene ws = new WorldScene((int) (img.getWidth() / 2), (int) (img.getHeight() / 2));
    ws.placeImageXY(img, (int) (img.getWidth() / 2), (int) (img.getHeight() / 2));
    t.checkExpect(m4.makeScene(), ws);
  }

  void testOnMouseClicked(Tester t) {
    this.initData();
    Posn p1 = new Posn(10, 10);
    Posn p2 = new Posn(30, 30);

    t.checkExpect(m1.cells.get(0).get(0).isLeftClicked, false);
    m1.onMouseClicked(p1, "LeftButton");
    t.checkExpect(m1.cells.get(0).get(0).isLeftClicked, true);

    t.checkExpect(m1.cells.get(0).get(0).flagged, false);
    m1.onMouseClicked(p1, "RightButton");
    t.checkExpect(m1.cells.get(0).get(0).flagged, true);
    m1.onMouseClicked(p1, "RightButton");
    t.checkExpect(m1.cells.get(0).get(0).flagged, false);

    t.checkExpect(m1.cells.get(1).get(1).isLeftClicked, false);
    m1.onMouseClicked(p2, "LeftButton");
    t.checkExpect(m1.cells.get(1).get(1).isLeftClicked, true);

    t.checkExpect(m1.cells.get(1).get(1).flagged, false);
    m1.onMouseClicked(p2, "RightButton");
    t.checkExpect(m1.cells.get(1).get(1).flagged, true);
    m1.onMouseClicked(p2, "RightButton");
    t.checkExpect(m1.cells.get(1).get(1).flagged, false);
    m1.onMouseClicked(p2, "RightButton");
    t.checkExpect(m1.cells.get(1).get(1).flagged, true);
  }

}