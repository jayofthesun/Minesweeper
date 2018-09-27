import java.util.ArrayList;
import java.awt.Color;
import javalib.worldimages.*;

class Cell {

  // Default fields, none need to passed because they are just mutated later if
  // needed
  ArrayList<Cell> neighbors = new ArrayList<Cell>();
  boolean hasBomb = false;
  boolean isLeftClicked = false;
  boolean flagged = false;
  int cellSize = 20;

  // connects this cell with the given cell
  public void connect(Cell other) {
    this.neighbors.add(other);
    other.neighbors.add(this);
  }

  // returns the number of mines around this cell
  public int mineCount() {
    int count = 0;
    for (Cell cell : neighbors) {
      if (cell.hasBomb) {
        count += 1;
      }
    }
    return count;
  }

  // draws this cell next to the given scene
  public WorldImage draw(WorldImage scene) {

    WorldImage cell;

    if (flagged) {
      cell = new OverlayImage(new EquilateralTriangleImage(this.cellSize / 2, "solid", Color.red),
          new RectangleImage(this.cellSize, this.cellSize, "solid", Color.LIGHT_GRAY));
    }
    else if (this.hasBomb && this.isLeftClicked) {
      cell = new OverlayImage(new CircleImage(5, OutlineMode.SOLID, Color.black),
          new RectangleImage(this.cellSize, this.cellSize, "solid", Color.DARK_GRAY));
    }
    else if (this.isLeftClicked) {
      cell = new OverlayImage(new TextImage(Integer.toString(this.mineCount()), Color.black),
          new RectangleImage(this.cellSize, this.cellSize, "solid", Color.LIGHT_GRAY));
    }
    else {
      cell = new RectangleImage(this.cellSize, this.cellSize, "solid", Color.LIGHT_GRAY);
    }

    cell = new FrameImage(cell);
    cell = new BesideImage(scene, cell);

    return cell;
  }

}