package game.buildings;

import game.shapes.Rect;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import game.shapes.Shape;

public class PathSpot {
    private boolean buildingDone = false;
    private final double buildingX;
    private final double buildingY;
    private final Shape build; //little highlighted area

    public PathSpot(int buildingX, int buildingY, double usx, double usy) {
        this.buildingX = buildingX + usx;
        this.buildingY = buildingY + usy;
        build = new Rect(this.buildingX,this.buildingY,20,250,Color.SADDLEBROWN);
    }

    public void spaceOpen(GraphicsContext gc){
        if(!buildingDone){
            build.draw(gc);
        }
    }

    public void used(){
        buildingDone = true;
    }

    //getters
    public boolean isBuildingDone() {
        return buildingDone;
    }
    public double getBuildingX() {
        return buildingX;
    }
    public double getBuildingY() {
        return buildingY;
    }
    public Shape getBuild() {
        return new Rect(buildingX+2,buildingY+2,16,246,Color.SANDYBROWN);
    }
}
