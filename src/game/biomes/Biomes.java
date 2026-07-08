package game.biomes;

import game.IDrawable;
import game.shapes.*;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Screen;

import java.util.ArrayList;
import java.util.Random;

public abstract class Biomes implements IDrawable {

    //universal variables (location stuff and some graphics related things) (also the randomizer)
    protected Random rand = new Random();
    private final double screenY = (Screen.getPrimary().getVisualBounds()).getHeight();
    private final double screenX = (Screen.getPrimary().getVisualBounds()).getWidth();
    protected double usx;
    protected double usy;
    protected double usw;
    protected double ush;
    protected double x;
    protected double y;
    protected boolean found = false;
    protected boolean adjacent = false;
    protected final int thisNumber; // records which number was printed on it
    static int number;// this counts how many biomes there are so far in the list, so that it prints that number onto the current biome ( while its being generated or printed)... just don't touch it.
    //graphics
    ArrayList<IDrawable> drawables = new ArrayList<>();
    // specific variables;
    protected String content = "nothing";
    protected double amount = 0;
    protected int antMultiplier = 0;
    protected Boolean isBug = false;
    protected int strength = 0;

    public Biomes(double usx, double usy, double x, double y, int antMultiplier) {
        this.x = x;
        this.y = y;
        this.usx = usx;
        this.usy = usy;
        this.antMultiplier = antMultiplier;
        thisNumber = number; //setting its number/ID
        number = number + 1;
    }

    //this is only used by the AntHill biome
    public Biomes(double usx, double usy, double x, double y) {
        this(usx, usy, x, y, 0);
    }

    //drawing everything method (cycles through stuff)
    public void draw(GraphicsContext gc) {
        loadGraphics(); //ensuring that what we print is the most up-to-date thing.
        for (int i = 0; i < drawables.size(); i++) {
            drawables.get(i).draw(gc);
        }
    }

    //selected method
    public void selected(GraphicsContext gc) {
        new Circle(usx + (x) - 103, usy + (y) - 103, 103, Color.WHITE).draw(gc);
        draw(gc);
        new Rect(5,5,235,80,Color.WHITE).draw(gc);
        new RectS(5,5,235,80,Color.BLACK).draw(gc);
    }

    public void restock(){};

    //graphics
    public void loadGraphics() {
        //for the clouds when a place isn't discovered yet.
        if (!this.found) {
            if (this.isAdjacent()) {
                drawables.add(new Images(usx - 110 + x, usy - 100 + y, "clouds_transparent.png", 1));
            } else {
                drawables.add(new Images(usx - 110 + x, usy - 100 + y, "clouds.png", 1));
            }
        }
    }

    //battling and aftermath
    public int battle(int numOfAnts) {
        //enemy power
        int bugPower = rand.nextInt(strength / 2 - strength, strength / 2) + strength;
        //your power is your numOfAnts (this will change later when you can send different types of ants...)
        if (numOfAnts >= bugPower) {
            this.content = "protein";
            this.amount = amount * strength;
            this.isBug = false;
            this.strength = 0;
            return numOfAnts - bugPower + rand.nextInt(0, bugPower);
        } else {
            return numOfAnts;
        }
    }

    //methods for movement of the map
    public void right(int num) {
        this.x = this.x - num;
    }

    public void left(int num) {
        this.x = this.x + num;
    }

    public void up(int num) {
        this.y = this.y + num;
    }

    public void down(int num) {
        this.y = this.y - num;
    }


    //getters
    public double getY() {
        return this.y;
    }

    public double getX() {
        return this.x;
    }

    public boolean getFound() {
        return found;
    }

    public int getStrength() {
        return strength;
    }

    public Boolean getIsBug() {
        return isBug;
    }

    public String getContent() {
        return content;
    }

    public int getAmount() {
        return (int) amount;
    }

    public int getAntMultiplier() {
        return antMultiplier;
    }

    public boolean isAdjacent() {
        return adjacent;
    }

    public String getType() {
        if (this instanceof GrassyPatch) {
            return "Grassy Patch";
        } else if (this instanceof AntHill) {
            return "Ant Hill";
        } else if (this instanceof Picnic) {
            return "Picnic Spot";
        } else if (this instanceof FlowerField) {
            return "Flower Field";
        } else {
            return "unknown";
        }
    }

    //setters
    public void setFound(boolean found) {
        this.found = found;
    }

    public void setAdjacent(boolean adjacent) {
        this.adjacent = adjacent;
    }

    //adders
    public void addAmount(int num) {
        amount += num;
    }

    //subtractors
    public void subtractAmount(int num) {
        amount -= num;
    }

    public int getThisNumber() {
        return this.thisNumber;
    }

    //location
    public double[] location() {
        return new double[]{
                x - 100 + usx,
                x + 100 + usx,
                y - 100 + usy,
                y + 100 + usy
        };
    }

}
