import java.util.ArrayList;
import java.util.Arrays;
import tester.*;
import javalib.impworld.*;
import java.awt.Color;
import javalib.worldimages.*;

class Game extends World {

  int size;

  ArrayList<Cell> cells;
  
  Color activePlayer;

  Game(int n) {

    if ((n >= 3) && (n % 2 != 0)) {

      this.size = n;

    }

    else {

      throw new IllegalArgumentException("Invalid Game Size");

    }

    this.cells = new Utils().makeCells(n);

    new Utils().initNeighbors(cells);
    
    this.activePlayer = Color.BLUE;

  }

  // draws the game with all cells represented on the background

  public WorldScene makeScene() {

    WorldScene bg = new WorldScene(this.size * 50, this.size * 50);

    for (Cell c : cells) {

      c.drawOn(bg);

    }

    return bg;

  }

  
// turns a cell the color of the current player if the mouse is clicked on it
  public void onMouseClicked(Posn pos) {

    for (Cell c : cells) {

      if (pos.x <= c.x + 25 && pos.x >= c.x - 25 && pos.y <= c.y + 25 && pos.y >= c.y - 25) {
        c.turn(this.activePlayer);
      }
    }

    if (this.activePlayer == Color.MAGENTA) {
      this.activePlayer = Color.BLUE;
    }

    else if (this.activePlayer == Color.BLUE) {
      this.activePlayer = Color.MAGENTA;
    }
  }
  
  //onTick
  public void onTick() {
    
    Color winner = null;
    
    for (Cell c : cells) { //in one column and one row corresponding to color

      if ((c.x == 25 || c.y == 25) && c.color != Color.LIGHT_GRAY) { //start from the left and top cells
        
        if (c.bottom.findPath(this.size, c.color, new ArrayList<Cell>())
            || c.right.findPath(this.size, c.color, new ArrayList<Cell>())) {
          
          winner = c.color;
        }
      }
    }
    
     if (winner == Color.MAGENTA) {
       this.endOfWorld("Magenta Won!");
     }
     
     if (winner == Color.BLUE) {
       this.endOfWorld("Blue Won!");
     }
    
  }
  
  //last scene
  public WorldScene lastScene(String msg) {
    
    WorldImage message = new TextImage(msg, 5 * size, Color.BLACK);

    WorldScene scene = new WorldScene(this.size * 50, this.size * 50);
    scene.placeImageXY(message, this.size * 25, this.size * 25);
    return scene;
  }
  
}

class Cell {

  int sideLength;

  int x;

  int y;
  
  boolean open;

  Color color;

  Cell top;

  Cell bottom;

  Cell left;

  Cell right;

  Cell(int size, int row, int col, Color color) {

    this.sideLength = size;

    this.x = col * this.sideLength - this.sideLength / 2;

    this.y = row * this.sideLength - this.sideLength / 2;
    
    this.open = false;

    this.color = color;

    this.top = this;

    this.bottom = this;

    this.left = this;

    this.right = this;

  }

  // draws this cell onto a given background

  public void drawOn(WorldScene bg) {

    WorldImage pic = new RectangleImage(sideLength, sideLength, OutlineMode.SOLID, color);

    bg.placeImageXY(pic, this.x, this.y);

  }

  // makes this cell neighbors with the given cell if appropriate

  public void neighbors(Cell b) {

    if (this.x == b.x + 50 && this.y == b.y) {

      this.left = b;

      b.right = this;

    }

    if (this.x == b.x - 50 && this.y == b.y) {

      this.right = b;

      b.left = this;

    }

    if (this.y == b.y + 50 && this.x == b.x) {

      this.top = b;

      b.bottom = this;

    }

    if (this.y == b.y - 50 && this.x == b.x) {

      this.bottom = b;

      b.top = this;

    }
  }
  
  //turns the color of the cell to the appropriate color
  public void turn(Color activePlayer) {

    if (this.color == Color.LIGHT_GRAY) {

      if (this.open && activePlayer == Color.MAGENTA) {

        this.color = Color.MAGENTA;
      }

      if (this.open && activePlayer == Color.BLUE) {

        this.color = Color.BLUE;
      }
    }
  }

  //finds a path to the edge
  public boolean findPath(int boardsize, Color prevColor, ArrayList<Cell> seen) {

    if ((! this.color.equals(prevColor)) || seen.contains(this)) {
      
      return false;
    }
    
    else if (this.x == boardsize * 50 - 25 || this.y == boardsize * 50 - 25) {
      
      return this.color.equals(prevColor);
      
    }

    seen.add(this); 
    
    return 
        
        this.top.findPath(boardsize, this.color, seen)
        || this.bottom.findPath(boardsize, this.color, seen) 
        || this.left.findPath(boardsize, this.color, seen) 
        || this.right.findPath(boardsize, this.color, seen);
  }
}


class Utils {

  // constructs a group of cells
  public ArrayList<Cell> makeCells(int boardsize) {

    ArrayList<Cell> list0 = new ArrayList<Cell>();

    for (int i = 1 ; i < boardsize + 1 ; i++) {
      
      Cell newCell = null;
      
      if (i % 2 != 0) {

        for (int j = 1 ; j < boardsize +1 ; j++) {

          if (j % 2 == 0) {
            
            newCell = new Cell(50, i, j, Color.MAGENTA);
            list0.add(newCell);
          }

          if (j % 2 != 0) {
            newCell = (new Cell(50, i, j, Color.LIGHT_GRAY));
            list0.add(newCell);
          }
          
          if (i != 1 && i != boardsize 
              && j != 1 && j != boardsize 
              && newCell.color == Color.LIGHT_GRAY) {
            newCell.open = true;
          }
        }
      }

      if (i % 2 == 0) {

        for (int j = 1 ; j < boardsize + 1 ; j++) {
          
          if (j % 2 != 0) {
            newCell = (new Cell(50, i, j, Color.BLUE));
            list0.add(newCell);
          }

          if (j % 2 == 0) {
            newCell = (new Cell(50, i, j, Color.LIGHT_GRAY));
            list0.add(newCell);
          }
          
          if (i != 1 && i != boardsize 
              && j != 1 && j != boardsize 
              && newCell.color == Color.LIGHT_GRAY) {
            newCell.open = true;
          }
        }
      }
    }

    return list0;
  }

  // initializes the neighbors of all of the cells in an ArrayList<Cell>
  public void initNeighbors(ArrayList<Cell> a) {
    
    for (Cell c : a) {
      for (Cell b : a) {
        c.neighbors(b);
      }
    }
  }
}

class Examples {

  Game Game11;
  Cell cell1;
  Cell cell2;
  Cell cell3;
  Cell cell4;
  WorldScene background;
  RectangleImage redCell;
  RectangleImage blueCell; 

  public void initData() {

    Game11 = new Game(11);
    cell1 = new Cell(50, 1, 3, Color.RED);
    cell2 = new Cell(50, 2, 3, Color.RED);
    cell3 = new Cell(50, 1, 2, Color.BLUE);
    redCell = new RectangleImage(50, 50, OutlineMode.SOLID, Color.RED);
    blueCell = new RectangleImage(50, 50, OutlineMode.SOLID, Color.BLUE);
    background = new WorldScene(150, 150);
  }

  // Testing for illegal game creation -> illegal size

  void testIllegalGameSize(Tester t) {

    this.initData();

    // legal game size constructs

    t.checkExpect(this.Game11.size, 11);

    // testing illegal game <size (< 3) >

    t.checkConstructorException(new IllegalArgumentException("Invalid Game Size"), "Game", 2);

    // testing illegal game <even size>

    t.checkConstructorException(new IllegalArgumentException("Invalid Game Size"), "Game", 4);

  }

  // test big bang

  void testBigBang(Tester t) {

    Game world = new Game(11);

    int worldWidth = world.size * 50;

    int worldHeight = world.size * 50;

    double tickRate = .02;

    world.bigBang(worldWidth, worldHeight, tickRate);

  }

  // test neighbors

  void testNeighbors(Tester t) {

    this.initData();

    cell1.neighbors(cell2); // testing that the cells can recognize neighbors above and below them

    t.checkExpect(cell1.bottom, cell2);
    t.checkExpect(cell2.top, cell1);
    t.checkExpect(cell1.x, 125); // testing the positioning of the two cells
    t.checkExpect(cell1.y, 25);
    t.checkExpect(cell2.x, 125);
    t.checkExpect(cell2.y, 75);

    // testing left and right neighbors
    cell1.neighbors(cell3);
    t.checkExpect(cell1.left, cell3);
    t.checkExpect(cell3.right, cell1);

    // testing the positioning of cell 3
    
    // testing that cells reference neighbors to themselves when there is no
    // neighbor

    t.checkExpect(cell1.top, cell1);
    t.checkExpect(cell2.bottom, cell2);

    t.checkExpect(cell3.top, cell3);

  }

  // test makeScene

  void testMakeScene(Tester t) {

    this.initData();

    Game g0 = new Game(3);
    g0.cells = new ArrayList<Cell>();
    t.checkExpect(g0.makeScene(), new WorldScene(150, 150));
    
    
    Game g = new Game(3);
    WorldScene expected = new WorldScene(150, 150);
    Cell cellm = new Cell(50, 1, 2, Color.MAGENTA);
    Cell cellm2 = new Cell(50, 3, 2, Color.MAGENTA);
    Cell cellb = new Cell(50, 2, 1, Color.BLUE);
    Cell cellb2 = new Cell(50, 2, 3, Color.BLUE);
    Cell cellg = new Cell(50, 1, 1, Color.LIGHT_GRAY);
    Cell cellg2 = new Cell(50, 1, 3, Color.LIGHT_GRAY);
    Cell cellg3 = new Cell(50, 2, 2, Color.LIGHT_GRAY);
    Cell cellg4 = new Cell(50, 3, 1, Color.LIGHT_GRAY); 
    Cell cellg5 = new Cell(50, 3, 3, Color.LIGHT_GRAY);

    cellg5.drawOn(expected);
    cellg4.drawOn(expected);
    cellg3.drawOn(expected);
    cellg2.drawOn(expected);
    cellg.drawOn(expected);
    cellb2.drawOn(expected);
    cellb.drawOn(expected);
    cellm2.drawOn(expected);
    cellm.drawOn(expected);


    t.checkExpect(g.makeScene(), expected);
  }

  // test drawOn
  void testDrawOn(Tester t) {

    this.initData();

    t.checkExpect(this.background, new WorldScene(150, 150));
    
    // drawing a cell onto the background
    this.cell1.drawOn(this.background);

    // drawing the cell in it's correct location dependent on 
    // row and column information
    WorldScene check1 = new WorldScene(150, 150);
    check1.placeImageXY(redCell, 125, 25); 
    t.checkExpect(this.background, check1);

    // drawing another cell in a different column 
    this.initData();
    this.cell2.drawOn(this.background);
    WorldScene check2 = new WorldScene(150, 150);
    check2.placeImageXY(redCell, 125, 75);
    t.checkExpect(this.background, check2);

    // drawing a cell of another color in a different location
    this.initData();
    this.cell3.drawOn(this.background);
    WorldScene check3 = new WorldScene(150, 150);
    check3.placeImageXY(blueCell, 75, 25);
    t.checkExpect(this.background, check3);


    //drawing another cell onto the same place -> supports more than one cell
    this.cell1.drawOn(background);
    WorldScene check4 = check3;
    check4.placeImageXY(redCell, 125, 25);
    t.checkExpect(this.background, check4);
    
  }

  // test makeCells <From Utils Class>
  void testMakeCells(Tester t) {
    this.initData();

    ArrayList<Cell> list0 = new Utils().makeCells(0);
    t.checkExpect(list0, new ArrayList<Cell>());

    ArrayList<Cell> list1 = new Utils().makeCells(1);
    Cell cell11 = new Cell(50, 1, 1, Color.LIGHT_GRAY);
    ArrayList<Cell> expect1 = new ArrayList<Cell>(Arrays.asList(cell11));
    t.checkExpect(list1, expect1);

    ArrayList<Cell> list2 = new Utils().makeCells(2);
    Cell cell12 = new Cell(50, 1, 2, Color.MAGENTA);
    Cell cell21 = new Cell(50, 2, 1, Color.BLUE);
    Cell cell22 = new Cell(50, 2, 2, Color.LIGHT_GRAY);
    ArrayList<Cell> expect2 = new ArrayList<Cell>(Arrays.asList(cell11, cell12, cell21, cell22));
    t.checkExpect(list2, expect2);
  }

  // test initNeighbors <From Utils Class>
  void testInitNeighbors(Tester t) {
    this.initData();
    
    Cell cell5 = new Cell(50, 1, 1, Color.BLACK);
    Cell cell6 = new Cell(50, 2, 1, Color.BLACK); 
    ArrayList<Cell> list = new ArrayList<Cell>(Arrays.asList(cell5, cell6));

    t.checkExpect(cell5.bottom, cell5);
    new Utils().initNeighbors(list);
    t.checkExpect(cell5.bottom, cell6);
    
    Cell cell7 = new Cell(50, 1, 1, Color.BLACK);
    Cell cell8 = new Cell(50, 1, 2, Color.BLACK); 
    ArrayList<Cell> list2 = new ArrayList<Cell>(Arrays.asList(cell7, cell8));

    t.checkExpect(cell7.right, cell7);
    new Utils().initNeighbors(list2);
    t.checkExpect(cell7.right, cell8);
    t.checkExpect(cell8.left, cell7);

  }
  
  //test findPath
  void testfindPath(Tester t) {
    
    this.initData();
    
    Cell cb1 = new Cell(50, 2, 1, Color.BLUE);
    Cell cb2 = new Cell(50, 2, 2, Color.BLUE); 
    Cell cb3 = new Cell(50, 2, 3, Color.BLUE); 
    
    ArrayList<Cell> cells = new ArrayList<Cell>(Arrays.asList(cb1, cb2, cb3));
    new Utils().initNeighbors(cells);
    
   // t.checkExpect(cb1.findPath(3, cb1.color, new ArrayList<Cell>()), true);  
   // cb2.color = Color.LIGHT_GRAY;
   // t.checkExpect(cb1.findPath(3, cb1.color, new ArrayList<Cell>()), false); 
   
    Game g1 = new Game(3);
  
    ArrayList<Cell> first = new ArrayList<Cell>();
    
    for(Cell c : g1.cells) {
      if (c.x == 25 && c.y == 75) {
        first.add(c);
      }
    }
    
    t.checkExpect(first.get(0).x, 25);
    t.checkExpect(first.get(0).y, 75);

   ArrayList<Cell> second = new ArrayList<Cell>();
    
    for(Cell c : g1.cells) {
      if (c.x == 75 && c.y == 75) {
        second.add(c);
      }
    }
    
    t.checkExpect(second.get(0).x, 75);
    t.checkExpect(second.get(0).y, 75);


   ArrayList<Cell> third = new ArrayList<Cell>();
    
    for(Cell c : g1.cells) {
      if (c.x == 125 && c.y == 75) {
        third.add(c);
      }
    }
    
    t.checkExpect(third.get(0).x, 125);
    t.checkExpect(third.get(0).y, 75);
    
    t.checkExpect(third.get(0).left, second.get(0));
    t.checkExpect(second.get(0).right, third.get(0));
    
    t.checkExpect(first.get(0).color, Color.BLUE);
    t.checkExpect(second.get(0).color, Color.LIGHT_GRAY);
    t.checkExpect(third.get(0).color, Color.BLUE);
    
    g1.onMouseClicked(new Posn(75, 75));
    t.checkExpect(second.get(0).color, Color.BLUE);
    g1.onTick();
    
    t.checkExpect(third.get(0).findPath(3, third.get(0).color, new ArrayList<Cell>()), true); //passes
    
    
    t.checkExpect(second.get(0).findPath(3, second.get(0).color, new ArrayList<Cell>()), true);
    t.checkExpect(second.get(0).color, second.get(0).color);
    t.checkExpect(new ArrayList<Cell>().contains(second.get(0)), false);
    t.checkExpect((! second.get(0).color.equals(second.get(0).color)) || new ArrayList<Cell>().contains(second.get(0)) , false);
    t.checkExpect(second.get(0).x == g1.size * 50 - 25, false);
    t.checkExpect(second.get(0).y == g1.size * 50 - 25, false);
    t.checkExpect(second.get(0).right, third.get(0));
    t.checkExpect(third.get(0).y, 75);
    
   // t.checkExpect(first.get(0).findPath(3, first.get(0).color, new ArrayList<Cell>()), true);
    
  //  Game g0 = new Game(3);
  //  g0.onMouseClicked(new Posn(75, 75));
  //  g0.onTick();
    
 
    
    
  }
  

}