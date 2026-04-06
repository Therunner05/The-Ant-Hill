package game.biomes;

import game.shapes.Circle;
import game.shapes.Images;
import game.shapes.Rect;
import javafx.scene.paint.Color;

import static game.biomes.BiomeType.*;

public class GrassyPatch extends Biomes {

    private static final BiomeType[] TYPE_CHANCES = new BiomeType[]{
            MUSHROOMS,
            NOTHING, NOTHING,
            BEETLES,
            GRASSHOPPERS, GRASSHOPPERS,
            APHIDS
    };

    private BiomeType type;

    public GrassyPatch(double usx, double usy, double x, double y) {
        //array of contents
        //"mushroom" /*food*/, "nothing", "nothing", "beetle", "grasshopper", "grasshopper" /* aphids eventually */;
        //amounts (food = 10), (beetle = 2) (grasshopper = 4)
        //its ant multiplier is 4, meaning each regular ant can take 4 items from this grassy patch.
        super(usx, usy, x, y, 4);

        //selecting what it contains
        type = TYPE_CHANCES[rand.nextInt(TYPE_CHANCES.length)];
        content = type.name().toLowerCase();

        //selecting how many or how much it contains
        switch (type) {
            case MUSHROOMS -> amount = rand.nextInt(1, 11);
            case BEETLES -> {
                amount = rand.nextInt(1, 3);
                strength = (int) (amount * 4);
                isBug = true;
            }
            case GRASSHOPPERS -> {
                amount = rand.nextInt(1, 5);
                strength = (int) (amount * 2);
                isBug = true;
            }
            case APHIDS -> amount = 1;
        }

        drawables.add(new Circle(usx + (x) - 100, usy + (y) - 100, 100, Color.GREEN));
    }

    @Override
    public void loadGraphics() {
        if(super.getContent().equals("protein")){
            type = PROTEIN;
        }
        drawables.clear();
        drawables.add(new Circle(usx + (x) - 100, usy + (y) - 100, 100, Color.GREEN));

        if (amount > 0) {
            switch (type) {
                case MUSHROOMS -> {
                    drawables.add(new Circle(usx + x - 6, usy + y - 8, 17, 10, Color.WHITE));
                    drawables.add(new Rect(usx + x, usy + y, 5, 10, Color.WHITE));
                    drawables.add(new Circle(usx + x - 6 + 10, usy + y - 8 - 20, 17, 10, Color.WHITE));
                    drawables.add(new Rect(usx + x + 10, usy + y - 20, 5, 10, Color.WHITE));
                    drawables.add(new Circle(usx + x - 6 - 18, usy + y - 8 - 20, 17, 10, Color.WHITE));
                    drawables.add(new Rect(usx + x - 18, usy + y - 20, 5, 10, Color.WHITE));
                }
                case BEETLES -> {
                    drawables.add(new Images(usx - 50 + x, usy - 50 + y, "beetle.png", 0.1));
                    if (amount > 1){
                        drawables.add(new Images(usx - 25 + x, usy - 35 + y, "beetle.png", 0.1));
                        if (amount >= 3){
                            drawables.add(new Images(usx -40 + x, usy - 20 + y, "beetle.png", 0.1));
                        }
                    }
                }
                case GRASSHOPPERS -> {
                    drawables.add(new Images(usx + x-10, usy + y, "grassHoper.png", 0.5));
                    if(amount > 1){
                        drawables.add(new Images(usx + x-59, usy + y-70, "grassHoper.png", 0.4));
                        if (amount >= 3){
                            drawables.add(new Images(usx + x-90, usy + y-30, "grassHoper.png", 0.4));
                        }
                    }
                }
                case PROTEIN -> {
                    drawables.add(new Circle(usx + x - 35, usy + y + 35, 10, Color.RED));
                    drawables.add(new Circle(usx + x + 25, usy + y + 40, 10, Color.RED));
                    drawables.add(new Circle(usx + x - 10, usy + y - 10, 10, Color.RED));
                }
                case APHIDS -> drawables.add(new Circle(usx + x - 25, usy + y - 25, 5, 10, Color.YELLOWGREEN));
            }
        }
        super.loadGraphics();
    }
}
