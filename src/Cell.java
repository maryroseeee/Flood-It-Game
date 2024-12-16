import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import tester.Tester;
import javalib.impworld.*;
import java.awt.Color;
import javalib.worldimages.*;

abstract class ACell {

  // returns an image representing this cell
  abstract WorldImage drawCell();

  // EFFECT: floods this cell
  abstract void flood();

  // EFFECT: changes the color of this cell to the given color
  abstract void updateColor(Color c);

  // checks if this cell is flooded and has the given color
  abstract boolean sameColorAndFloodedHelp(Color c);
}

// acts as a null cell on the edges
class BorderCell extends ACell {

  // returns an image representing this cell
  public WorldImage drawCell() {
    return new EmptyImage();
  }

  // EFFECT: floods this cell
  void flood() {
    // dont need to do anything to the border cell, just needs to be here bc it is
    // in the abstract ACell class
  }

  // EFFECT: changes the color of this cell to the given color
  void updateColor(Color c) {
    // dont need to do anything to the border cell, just needs to be here bc it is
    // in the abstract ACell class
  }

  // checks if this cell is flooded and has the given color
  boolean sameColorAndFloodedHelp(Color c) {
    return false;
  }
}

// Represents a single square of the game area
class Cell extends ACell {
  // In logical coordinates, with the origin at the top-left corner of the screen
  int x;
  int y;
  Color color;
  boolean flooded;
  // the four adjacent cells to this one
  ACell left;
  ACell top;
  ACell right;
  ACell bottom;

  Cell(int x, int y, Color color) {
    this.x = x;
    this.y = y;
    this.color = color;
    this.flooded = false;
    this.left = new BorderCell();
    this.top = new BorderCell();
    this.right = new BorderCell();
    this.bottom = new BorderCell();
  }

  Cell(int x, int y, Random rand, ArrayList<Color> allColors) {
    this.x = x;
    this.y = y;
    this.color = new Utils().randColor(rand, allColors);
    this.flooded = false;
    this.left = new BorderCell();
    this.top = new BorderCell();
    this.right = new BorderCell();
    this.bottom = new BorderCell();
  }

  // EFFECT: updates all adjacent cells of this cell
  void updateAdjacent(ACell l, ACell r, ACell t, ACell b) {
    this.updateLeft(l);
    this.updateRight(r);
    this.updateTop(t);
    this.updateBottom(b);
  }

  // EFFECT: updates which cell is left of this cell
  void updateLeft(ACell l) {
    this.left = l;
  }

  // EFFECT: updates which cell is right of this cell
  void updateRight(ACell r) {
    this.right = r;
  }

  // EFFECT: updates which cell is above of this cell
  void updateTop(ACell t) {
    this.top = t;
  }

  // EFFECT: updates which cell is below of this cell
  void updateBottom(ACell b) {
    this.bottom = b;
  }

  // EFFECT: updates the color of this cell to the given color
  void updateColor(Color c) {
    this.color = c;
  }

  // EFFECT: floods this cell
  void flood() {
    this.flooded = true;
  }

  // returns an image representing this cell
  public WorldImage drawCell() {
    return new RectangleImage(20, 20, "solid", this.color);
  }

  // checks if this cell has the given Posn inside it
  boolean wasClicked(Posn pos, int boardSize) {
    int xmin;
    int xmax;
    int ymin;
    int ymax;
    int half = boardSize / 2;
    if (boardSize % 2 == 0) {
      xmin = ((this.x - half) * 20) + 250;
      xmax = ((this.x - half + 1) * 20) + 250;
      ymin = ((this.y - half) * 20) + 250;
      ymax = ((this.y - half + 1) * 20) + 250;
    }
    else {
      xmin = ((this.x - (boardSize / 2)) * 20) + 250 - 10;
      xmax = ((this.x - (boardSize / 2)) * 20) + 250 + 10;
      ymin = ((this.y - (boardSize / 2)) * 20) + 250 - 10;
      ymax = ((this.y - (boardSize / 2)) * 20) + 250 + 10;

    }
    return (pos.x >= xmin && pos.x < xmax) && (pos.y >= ymin && pos.y < ymax);
  }

  // checks if this cell is adjacent to a cell that is flooded and has the given
  // color
  boolean sameColorAndFlooded(Color c) {
    return this.left.sameColorAndFloodedHelp(c) || this.top.sameColorAndFloodedHelp(c)
        || this.right.sameColorAndFloodedHelp(c) || this.bottom.sameColorAndFloodedHelp(c);
  }

  // checks if this cell is flooded and has the given color
  boolean sameColorAndFloodedHelp(Color c) {
    return this.flooded && this.color.equals(c);
  }

}

class FloodItWorld extends World {
  // All the cells of the game
  ArrayList<ArrayList<Cell>> board;
  int size;
  int numOfColors;
  ArrayList<Color> allColors = new ArrayList<Color>(Arrays.asList(Color.blue, Color.cyan,
      Color.green, Color.magenta, Color.orange, Color.pink, Color.red, Color.yellow));
  Random rand;
  boolean flooding;
  Color clicked;
  int numClicks;
  int maxClicks;
  long startTime = System.currentTimeMillis();

  // assumes numOfColors is below 8
  FloodItWorld(int size, int numOfColors) {
    this.size = size;
    this.numOfColors = numOfColors;
    this.rand = new Random();
    this.allColors = new Utils().randColorSet(this.rand, this.allColors, numOfColors);
    this.flooding = false;
    this.clicked = Color.white; // using white as a null color
    this.numClicks = 0;
    this.maxClicks = ((25 * (2 * size) * numOfColors) / 168) + 1;
    this.board = this.makeBoard();
  }

  // assumes numOfColors is below 8
  FloodItWorld(int size, int numOfColors, int seed) {
    this.size = size;
    this.numOfColors = numOfColors;
    this.rand = new Random(seed);
    this.allColors = new Utils().randColorSet(this.rand, this.allColors, numOfColors);
    this.flooding = false;
    this.clicked = Color.white;
    this.numClicks = 0;
    this.maxClicks = ((25 * (2 * size) * numOfColors) / 168) + 1;
    this.board = this.makeBoard();

  }

  // assumes numOfColors is below 8
  FloodItWorld(ArrayList<ArrayList<Cell>> board, int size, int numOfColors) {
    this.board = board;
    this.size = size;
    this.numOfColors = numOfColors;
    this.rand = new Random();
    this.allColors = new Utils().randColorSet(this.rand, this.allColors, numOfColors);
    this.flooding = false;
    this.clicked = Color.white;
    this.numClicks = 0;
    this.maxClicks = ((25 * (2 * size) * numOfColors) / 168) + 1;
  }

  // creates a random board
  ArrayList<ArrayList<Cell>> makeBoard() {
    ArrayList<ArrayList<Cell>> ans = new ArrayList<ArrayList<Cell>>();
    for (int i = 0; i < this.size; i++) {
      ans.add(this.makeColumn(i));
    }
    Cell topLeft = ans.get(0).get(0);
    topLeft.flooded = true;
    for (int x = 0; x < this.size; x++) {
      for (int y = 0; y < this.size; y++) {
        Cell cell = ans.get(x).get(y);
        ACell left;
        ACell right;
        ACell top;
        ACell bottom;
        if (cell.x - 1 >= 0) {
          left = ans.get(x - 1).get(y);
        }
        else {
          left = new BorderCell();
        }
        if (cell.x + 1 < this.size) {
          right = ans.get(x + 1).get(y);
        }
        else {
          right = new BorderCell();
        }
        if (cell.y - 1 >= 0) {
          top = ans.get(x).get(y - 1);
        }
        else {
          top = new BorderCell();
        }
        if (cell.y + 1 < this.size) {
          bottom = ans.get(x).get(y + 1);
        }
        else {
          bottom = new BorderCell();
        }
        cell.updateAdjacent(left, right, top, bottom);
        if (cell.sameColorAndFlooded(topLeft.color) && cell.color.equals(topLeft.color)) {
          cell.flooded = true;
        }
      }
    }
    return ans;
  }

  // creates a random column
  ArrayList<Cell> makeColumn(int x) {
    ArrayList<Cell> ans = new ArrayList<Cell>();
    for (int i = 0; i < this.size; i++) {
      ans.add(new Cell(x, i, this.rand, this.allColors));
    }
    return ans;
  }

  int sceneSize = 500;
  WorldScene background = new WorldScene(sceneSize, sceneSize);

  // draws this board
  WorldImage drawBoard() {
    ArrayList<Cell> column1 = this.board.get(0);
    WorldImage ans = new Utils().drawColumn(column1);
    for (ArrayList<Cell> column : this.board) {
      if (column != column1) {
        ans = new BesideImage(ans, new Utils().drawColumn(column));
      }
    }
    return ans;
  }

  // draws the game
  public WorldScene makeScene() {
    WorldScene bg = new WorldScene(500, 500);
    bg.placeImageXY(this.drawBoard(), 250, 250);
    WorldImage turnCounter = new TextImage(this.numClicks + "/" + this.maxClicks, Color.black);
    bg.placeImageXY(turnCounter, 250, 450);
    WorldImage timer = new TextImage(
        String.valueOf((System.currentTimeMillis() - this.startTime) / 1000) + " seconds",
        Color.black);
    bg.placeImageXY(timer, 250, 100);
    if (this.numClicks > this.maxClicks) {
      bg.placeImageXY(new TextImage("You Lose :(", 20, Color.black), 250, 475);
    }
    return bg;
  }

  // updates board every tick
  // EFFECT: updates world state
  public void onTick() {
    if (this.flooding) {
      this.floodNext();
    }
  }

  // EFFECT: changes the color of the next cells that need to be flooded to create
  // the waterfall effect
  void floodNext() {
    boolean wasChange = false;
    for (int i = this.size - 1; i >= 0; i--) {
      ArrayList<Cell> column = this.board.get(i);
      for (int j = this.size - 1; j >= 0; j--) {
        Cell cell = column.get(j);
        if (cell.flooded && cell.color.equals(clicked)) {
          // prevents the other branches of the else-if from occuring so that wasChange is
          // not updated and we can leave the flooding state of the game
        }
        else if (cell.flooded && cell.sameColorAndFlooded(clicked)) {
          cell.updateColor(clicked);
          wasChange = true;
        }
        else if (cell.sameColorAndFlooded(clicked) && cell.color.equals(this.clicked)) {
          cell.flooded = true;
          wasChange = true;
        }
      }
    }
    if (!wasChange) {
      this.flooding = false;
    }
  }

  // handles mouse clicks
  // EFFECT: updates world state
  public void onMouseClicked(Posn pos) {
    if (!this.flooding) {
      Color c = this.findClickedCellColor(pos);
      if (c != Color.white) {
        this.clicked = c;
        this.flooding = true;
        this.numClicks = this.numClicks + 1;
        this.board.get(0).get(0).flooded = true;
        this.board.get(0).get(0).color = this.clicked;
      }
    }
  }

  public WorldEnd worldEnds() {
    if (this.numClicks <= this.maxClicks && this.allFlooded()) {
      WorldScene bg = new WorldScene(500, 500);
      bg.placeImageXY(new RectangleImage(500, 500, "solid", Color.green.darker()), 250, 250);
      bg.placeImageXY(new TextImage("YOU WIN!!!", 50, Color.white), 250, 250);
      bg.placeImageXY(new TextImage(":)", 75, Color.white), 250, 325);
      String finalTime = String.valueOf((System.currentTimeMillis() - this.startTime) / 1000)
          + " seconds";
      bg.placeImageXY(new TextImage("It took " + finalTime, Color.white), 250, 400);
      bg.placeImageXY(new TextImage("and " + this.numClicks + " clicks", Color.white), 250, 450);
      return new WorldEnd(true, bg);
    }
    else {
      return new WorldEnd(false, this.makeScene());
    }
  }

  Color findClickedCellColor(Posn pos) {
    for (ArrayList<Cell> column : this.board) {
      for (Cell cell : column) {
        if (cell.wasClicked(pos, this.size)) {
          return cell.color;
        }
      }
    }
    return Color.white;
  }

  // handles keystrokes
  // EFFECT: updates world state
  public void onKeyEvent(String k) {
    if (k.equals("r")) {
      this.board = this.makeBoard();
      this.flooding = false;
      this.clicked = Color.white;
      this.numClicks = 0;
      this.startTime = System.currentTimeMillis();
    }
  }

  // checks if all cells are flooded in this board
  boolean allFlooded() {
    for (ArrayList<Cell> column : this.board) {
      for (Cell cell : column) {
        if (!cell.flooded) {
          return false;
        }
      }
    }
    return true;
  }
}

class Utils {
  // draws a given list of cells in a column
  WorldImage drawColumn(ArrayList<Cell> arr) {
    WorldImage ans = arr.get(0).drawCell();
    for (ACell c : arr) {
      if (arr.get(0) != c) {
        ans = new AboveImage(ans, c.drawCell());
      }
    }
    return ans;
  }

  // returns a random color in the given lsit
  Color randColor(Random rand, ArrayList<Color> allColors) {
    int idx = rand.nextInt(allColors.size());
    return allColors.get(idx);
  }

  // returns a random subset of colors of allColors with the given size
  ArrayList<Color> randColorSet(Random rand, ArrayList<Color> allColors, int size) {
    ArrayList<Color> all = new ArrayList<Color>();
    all.addAll(allColors);
    ArrayList<Color> ans = new ArrayList<Color>();
    for (int i = 0; i < size; i++) {
      int idx = rand.nextInt(all.size());
      ans.add(all.remove(idx));
    }
    return ans;
  }
}

class ExamplesFloodItWorld {

  Cell x0y0;
  Cell x0y1;
  Cell x0y2;
  Cell x0y3;
  Cell x1y0;
  Cell x1y1;
  Cell x1y2;
  Cell x1y3;
  Cell x2y0;
  Cell x2y1;
  Cell x2y2;
  Cell x2y3;
  Cell x3y0;
  Cell x3y1;
  Cell x3y2;
  Cell x3y3;
  BorderCell border;

  Cell flooded1;
  Cell flooded2;
  Cell flooded3;
  Cell flooded4;
  ArrayList<Cell> column1flooded;
  ArrayList<Cell> column2flooded;
  ArrayList<ArrayList<Cell>> boardWin;
  FloodItWorld floodItWin;

  ArrayList<Cell> column0;
  ArrayList<Cell> column1;
  ArrayList<Cell> column2;
  ArrayList<Cell> column3;
  ArrayList<ArrayList<Cell>> board1;
  FloodItWorld floodIt1;

  WorldImage c0img;
  WorldImage c1img;
  WorldImage c2img;
  WorldImage c3img;
  WorldImage b1img;

  Random rand1;
  ArrayList<Color> colors;

  FloodItWorld floodIt2;

  void reset() {
    border = new BorderCell();

    x0y0 = new Cell(0, 0, Color.red);
    x0y1 = new Cell(0, 1, Color.green);
    x0y2 = new Cell(0, 2, Color.blue);
    x0y3 = new Cell(0, 3, Color.yellow);

    x1y0 = new Cell(1, 0, Color.blue);
    x1y1 = new Cell(1, 1, Color.green);
    x1y2 = new Cell(1, 2, Color.blue);
    x1y3 = new Cell(1, 3, Color.red);

    x2y0 = new Cell(2, 0, Color.yellow);
    x2y1 = new Cell(2, 1, Color.green);
    x2y2 = new Cell(2, 2, Color.red);
    x2y3 = new Cell(2, 3, Color.blue);

    x3y0 = new Cell(3, 0, Color.red);
    x3y1 = new Cell(3, 1, Color.blue);
    x3y2 = new Cell(3, 2, Color.yellow);
    x3y3 = new Cell(3, 3, Color.yellow);

    x0y0.updateAdjacent(border, x1y0, border, x0y1);
    x0y1.updateAdjacent(border, x1y1, x0y0, x0y2);
    x0y2.updateAdjacent(border, x1y2, x0y1, x0y3);
    x0y3.updateAdjacent(border, x1y3, x0y2, border);
    x1y0.updateAdjacent(x0y0, x2y0, border, x1y1);
    x1y1.updateAdjacent(x0y1, x2y1, x1y0, x1y2);
    x1y2.updateAdjacent(x0y2, x2y2, x1y1, x1y3);
    x1y3.updateAdjacent(x0y3, x2y3, x1y2, border);
    x2y0.updateAdjacent(x1y0, x3y0, border, x2y1);
    x2y1.updateAdjacent(x1y1, x3y1, x2y0, x2y2);
    x2y2.updateAdjacent(x1y2, x3y2, x2y1, x2y3);
    x2y3.updateAdjacent(x1y3, x3y3, x2y2, border);
    x3y0.updateAdjacent(x2y0, border, border, x3y1);
    x3y1.updateAdjacent(x2y1, border, x3y0, x3y2);
    x3y2.updateAdjacent(x2y2, border, x3y1, x3y3);
    x3y3.updateAdjacent(x2y3, border, x3y2, border);

    column0 = new ArrayList<Cell>(Arrays.asList(x0y0, x0y1, x0y2, x0y3));
    column1 = new ArrayList<Cell>(Arrays.asList(x1y0, x1y1, x1y2, x1y3));
    column2 = new ArrayList<Cell>(Arrays.asList(x2y0, x2y1, x2y2, x2y3));
    column3 = new ArrayList<Cell>(Arrays.asList(x3y0, x3y1, x3y2, x3y3));
    board1 = new ArrayList<ArrayList<Cell>>(Arrays.asList(column0, column1, column2, column3));

    floodIt1 = new FloodItWorld(board1, 4, 4);
    floodIt2 = new FloodItWorld(5, 3, 23546798);

    flooded1 = new Cell(0, 0, Color.red);
    flooded2 = new Cell(0, 1, Color.blue);
    flooded3 = new Cell(1, 0, Color.yellow);
    flooded4 = new Cell(1, 1, Color.yellow);
    flooded1.updateAdjacent(border, flooded2, border, flooded3);
    flooded2.updateAdjacent(flooded1, border, border, flooded4);
    flooded3.updateAdjacent(border, flooded4, flooded1, border);
    flooded4.updateAdjacent(flooded3, border, flooded2, border);
    flooded1.flooded = true;
    flooded2.flooded = true;
    flooded3.flooded = true;
    flooded4.flooded = true;
    column1flooded = new ArrayList<Cell>(Arrays.asList(flooded1, flooded3));
    column2flooded = new ArrayList<Cell>(Arrays.asList(flooded2, flooded4));
    boardWin = new ArrayList<ArrayList<Cell>>(Arrays.asList(column1flooded, column2flooded));
    floodItWin = new FloodItWorld(boardWin, 2, 3);

    rand1 = new Random(1530698);
    colors = new ArrayList<Color>(Arrays.asList(Color.blue, Color.cyan, Color.green, Color.magenta,
        Color.orange, Color.pink, Color.red, Color.yellow));
  }

  void testMakeScene(Tester t) {
    this.reset();
    WorldScene background = new WorldScene(500, 500);
    background.placeImageXY(floodIt1.drawBoard(), 250, 250);
    WorldImage turnCounter = new TextImage("0/5", Color.black);
    background.placeImageXY(turnCounter, 250, 450);
    WorldImage timer = new TextImage(
        String.valueOf((System.currentTimeMillis() - floodIt1.startTime) / 1000) + " seconds",
        Color.black);
    background.placeImageXY(timer, 250, 100);
    t.checkExpect(floodIt1.makeScene(), background);
  }

  void testRandColor(Tester t) {
    this.reset();
    t.checkExpect(new Utils().randColor(rand1, colors), Color.red);
  }

  void testRandColorSet(Tester t) {
    this.reset();
    ArrayList<Color> colorSet = new Utils().randColorSet(rand1, colors, 5);
    t.checkExpect(colorSet.size(), 5);
  }

  void testWasClicked(Tester t) {
    this.reset();
    t.checkExpect(x0y0.wasClicked(new Posn(215, 217), 4), true);
    t.checkExpect(x2y3.wasClicked(new Posn(260, 280), 4), true);
    t.checkExpect(x3y2.wasClicked(new Posn(260, 280), 4), false);
    t.checkExpect(floodIt1.clicked, Color.white);

    floodIt1.onMouseClicked(new Posn(215, 217));
    t.checkExpect(floodIt1.flooding, true);

    t.checkExpect(floodIt1.clicked, Color.red);
  }

  void testSameColorAndFlooded(Tester t) {
    this.reset();
    x0y0.flooded = true;
    t.checkExpect(x1y0.sameColorAndFlooded(Color.red), true);
    t.checkExpect(x1y0.sameColorAndFlooded(Color.yellow), false);
    t.checkExpect(x1y3.sameColorAndFlooded(Color.red), false);
  }

  void testAllFlooded(Tester t) {
    this.reset();
    t.checkExpect(floodIt1.allFlooded(), false);
    t.checkExpect(floodItWin.allFlooded(), true);
  }

  void testOnTick(Tester t) {
    this.reset();
    t.checkExpect(floodIt1.board, board1);
    floodIt1.onTick();
    t.checkExpect(floodIt1.board, board1);
    floodIt1.flooding = true;
    floodIt1.onTick();
    t.checkExpect(floodIt1.flooding, false);
  }

  void testFloodNext(Tester t) {
    this.reset();
    floodItWin.clicked = Color.red;
    floodItWin.floodNext();
    t.checkExpect(floodItWin.board.get(0).get(1).color, Color.red);
    t.checkExpect(floodItWin.board.get(1).get(0).color, Color.red);
    t.checkExpect(floodItWin.board.get(1).get(1).color, Color.yellow);
  }

  void testOnMouseClicked(Tester t) {
    this.reset();
    floodIt1.onMouseClicked(new Posn(260, 280));
    t.checkExpect(floodIt1.numClicks, 1);
    t.checkExpect(floodIt1.flooding, true);
    t.checkExpect(floodIt1.clicked, Color.blue);
  }

  void testWorldEnds(Tester t) {
    this.reset();
    t.checkExpect(floodIt1.worldEnds(), new WorldEnd(false, floodIt1.makeScene()));

    WorldScene end = new WorldScene(500, 500);
    end.placeImageXY(new RectangleImage(500, 500, "solid", Color.green.darker()), 250, 250);
    end.placeImageXY(new TextImage("YOU WIN!!!", 50, Color.white), 250, 250);
    end.placeImageXY(new TextImage(":)", 75, Color.white), 250, 325);
    end.placeImageXY(new TextImage("It took 0 seconds", Color.white), 250, 400);
    end.placeImageXY(new TextImage("and 0 clicks", Color.white), 250, 450);

    t.checkExpect(floodItWin.worldEnds(), new WorldEnd(true, end));
  }

  void testFindClickedCellColor(Tester t) {
    this.reset();
    t.checkExpect(floodIt1.findClickedCellColor(new Posn(215, 217)), Color.red);
    t.checkExpect(floodIt1.findClickedCellColor(new Posn(260, 280)), Color.blue);
    t.checkExpect(floodIt1.findClickedCellColor(new Posn(200, 200)), Color.white);

    t.checkExpect(floodIt2.findClickedCellColor(new Posn(215, 217)), Color.cyan);
    t.checkExpect(floodIt2.findClickedCellColor(new Posn(400, 260)), Color.white);
    t.checkExpect(floodIt2.findClickedCellColor(new Posn(268, 200)), Color.pink);
  }

  void testOnKeyEvent(Tester t) {
    this.reset();
    floodIt1.onKeyEvent("e");
    t.checkExpect(floodIt1.board == board1, true);
    floodIt1.flooding = true;
    floodIt1.numClicks = 25;
    floodIt1.onKeyEvent("r");
    t.checkExpect(floodIt1.board == board1, false);
    t.checkExpect(floodIt1.flooding, false);
    t.checkExpect(floodIt1.numClicks, 0);

    t.checkExpect(floodItWin.allFlooded(), true);
    floodItWin.onKeyEvent("r");
    t.checkExpect(floodItWin.allFlooded(), false);
  }

  void testMakeBoard(Tester t) {
    this.reset();
    FloodItWorld floodIt3 = new FloodItWorld(9, 3);
    t.checkExpect(floodIt3.allColors.size(), 3);

    floodIt3.bigBang(500, 500, .1);
  }

}
