package game.biomes;

import game.shapes.Circle;
import game.shapes.Images;
import game.shapes.Rect;
import game.shapes.RectS;
import javafx.scene.paint.Color;

import static game.biomes.BiomeType.*;

//the picnic spot will also every once in a while have a random human appear on it, which will prevent you from using the space.
public class Picnic extends Biomes {

    private static final BiomeType[] TYPE_CHANCES = new BiomeType[]{
            CRUMBS,
            HUMAN,
            BEETLES,
            PROTEIN
    };


    private BiomeType type;

    public Picnic(double usx, double usy, double x, double y) {
        //array of contents
        //"crumbs" /*food*/, "beetle", "human";
        //amounts (food = 50), (beetle = 5) human... doesn't matter, you just cant use this space.
        //its ant multiplier is 3, meaning each regular ant can take 3 items from this grassy patch.
        super(usx, usy, x, y, 3);
        //selecting what it contains
        type = TYPE_CHANCES[rand.nextInt(TYPE_CHANCES.length)];
        content = type.name().toLowerCase();
        //selecting how much it contains... except if its human... then the spot just doesn't work.
        switch (type) {
            case CRUMBS -> this.amount = rand.nextInt(25, 50);
            case PROTEIN -> this.amount = rand.nextInt(15, 35);
            case BEETLES -> {
                this.amount = rand.nextInt(1, 5);
                this.isBug = true;
                this.strength = (int) (amount * 4);
            }
            default -> content = "Human, stay away from them...";
        }
    }

    @Override
    public void loadGraphics() {
        if(super.getContent().equals("protein")){
            type = PROTEIN;
        }
        drawables.clear();
        drawables.add(new Circle(usx + (x) - 100, usy + (y) - 100, 100, Color.GREEN));
        int yCord = 50;
        for (int i = 4; i <= 8; i++) {
            drawables.add(new Rect(usx + (x) - 55, usy + (y) - yCord, 90, 15, Color.rgb(250, 250, 250)));
            yCord -= 15;
            //this next loop is for the red squares.
            int xCord = 55;
            if (i % 2 == 0) {
                xCord -= 15;
            }
            for (int j = 0; j <= 2; j++) {
                drawables.add(new RectS(usx + (x) - xCord, usy + (y) - yCord - 15, 15, 15, Color.BLACK));
                drawables.add(new Rect(usx + (x) - xCord, usy + (y) - yCord - 15, 15, 15, Color.rgb(250, 5, 10)));
                xCord -= 30;
            }
        }
        if (amount > 0) {
            switch (type) {
                case CRUMBS -> {
                    drawables.add(new Circle(usx + x - 6, usy + y - 8, 17, 10, Color.BEIGE));
                    drawables.add(new Circle(usx + x - 6 + 10, usy + y - 8 - 20, 17, 10, Color.BEIGE));
                    drawables.add(new Circle(usx + x - 6 - 18, usy + y - 8 - 20, 17, 10, Color.BEIGE));
                    drawables.add(new Circle(usx + x - 6 - 40, usy + y - 8 - 20, 17, 10, Color.BEIGE));
                    drawables.add(new Circle(usx + x - 6 - 5, usy + y - 8 + 20, 17, 10, Color.BEIGE));
                    drawables.add(new Circle(usx + x - 6 - 20, usy + y - 8 + 56, 17, 10, Color.BEIGE));
                    drawables.add(new Circle(usx + x - 6 + 5, usy + y - 8 + 35, 17, 10, Color.BEIGE));
                    drawables.add(new Circle(usx + x - 6 + 29, usy + y - 8 + 45, 17, 10, Color.BEIGE));
                    drawables.add(new Circle(usx + x - 6 + 11, usy + y - 8 + 25, 17, 10, Color.BEIGE));
                }
                case BEETLES -> {
                    drawables.add(new Images(usx - 50 + x, usy - 50 + y, "beetle.png", 0.13));
                }
                case PROTEIN -> {
                    drawables.add(new Circle(usx + x - 35, usy + y + 35, 10, Color.RED));
                    drawables.add(new Circle(usx + x + 25, usy + y + 40, 10, Color.RED));
                    drawables.add(new Circle(usx + x - 10, usy + y - 10, 10, Color.RED));
                }
            }
        }
        super.loadGraphics();
    }
}
