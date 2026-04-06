package game;

import game.ants.Ant;
import game.ants.Queen;
import game.biomes.AntHill;
import game.buildings.*;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import game.shapes.*;
import javafx.stage.StageStyle;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author Owen Culver
 * Ant Simulator Games View File
 * notes for work:
 * add more usful comments, remove/move logic code into appropriate classes, also, fix the way buildings are recongise as built...
 */
public class AntGameView extends Application {

    private final boolean cheatMode = false; //set to true to test game

    //variables for start up/set up
    private GraphicsContext gc;
    private final double screenY = (Screen.getPrimary().getVisualBounds()).getHeight();
    private final double screenX = (Screen.getPrimary().getVisualBounds()).getWidth();
    private final double usw = 1440; //usable screens length/w
    private final double ush = 816; //usable screens height
    private final double usx = (screenX - usw)/2;//the amount of space between the black and usable background (x)
    private final double usy = (screenY - ush)/2;//the amount of space between the black and usable background (y)

    //variables for drawings and images and stuff like that.
    private ArrayList<Shape> background = new ArrayList<>();

    //main variables that NEED to be saved for sure (the main objects)
    private Nest nest;
    private Map map = new Map(usx,usy,usw,ush);
    /*building stuff*/
    private int spot;
    private boolean buildCancel = false;
    private double buildingX;
    private double buildingY;
    private boolean flip = false; //determines the direction of a new building.
    private List<BuildingSpot> build = new ArrayList<BuildingSpot>();
    private List<PathSpot> path = new ArrayList<PathSpot>();

    //start up ui features
    private TextField name;
    private Button confName;
    //UI features
    Alert alert = new Alert(Alert.AlertType.INFORMATION); // the alert
    ButtonType closeButton = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
    ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
    private Button nextDay;
    private Button layEggButton;
    private Button createAntButton;
    private Text population;
    private Text aphids;
    private Text food;
    private Text protein;
    private Text ants;
    private Text larvas;
    private Text eggs;
    private Text antsInUse;
    private Button mapButton;
    private String title = "";
    private String messege1 = "";
    private String messege2 = "";
    private String messege3 = "";

    /*UI for the build method*/
    private Button buildButton;
    private Button barracksButton;
    private Button pathsButton;
    private Text requirements1; //for paths and barracks
    private Button foodStorageButton;
    private Text requirements2; //for just food storage so far
    private Button proteinStorageButton;
    private Text requirements3; //just for protein storage so far
    private Button aphidFarmButton;
    private Text requirements4; //just for protein storage so far



    // stuff for the map (everything here except for the map button
    private boolean mapDrawn = false;
    private Spinner mapSelect = new Spinner(0,(map.biomes.size())-1,0);
    private Button mapSelectButton;
    private boolean selected = false;
    private Button cancelSelection;
    private int num = 0;//this is just for sending stuff and selecting stuff, and storing that number.
    private double mousex = 0;
    private double mousey = 0;
    private boolean click = true;

    private Button dictionaryButton;



    //all stuff for prior to the game starting (for now)
    /// erasing the text for collecting the name
    public void nameErase(MouseEvent me){
        name.setText("");
    }

    /// start new game method, for when you are starting a brand-new game.
    public void startNewGame(ActionEvent e){
        confName.setDisable(true);
        confName.setFocusTraversable(false);
        name.setFocusTraversable(false);
        buildButton.setFocusTraversable(true);
        nextDay.setFocusTraversable(true);
        mapButton.setFocusTraversable(true);
        dictionaryButton.setFocusTraversable(true);
        nest = new Nest(name.getText());
        //starter buildings
        nest.addBuilding(new ThroneRoom(usx+550,350));
        nest.getBuildings().get(0).addAnts(new Queen());
        nest.addBuilding(new Barracks(usx+760,350,flip));
        //checks if cheats on to give amount of ants
        int antAmount;
        if (cheatMode) {
            antAmount = 500;
            nest.cheatMode();
            map.cheatMode();
        } else {
            antAmount = 5;
        }
        for (int i = 0; i < antAmount; i++) {
            nest.getBuildings().get(1).addAnts(new Ant());
        }
        nest.calcAll();
        //the background (don't touch unless your improving graphics)
        background.add(new Rect(0,0,screenX,screenY,Color.rgb(135, 206, 235)));
        background.add(new Rect(0,200,screenX,screenY,Color.GREEN));
        background.add(new Rect(0,210,screenX,screenY,Color.rgb(61, 35, 13)));
        background.add(nest.Graphics(usx,0).get(0));
        background.add(nest.Graphics(usx,0).get(1));
        background.add(nest.Graphics(usx,0).get(2));
        background.add(new Rect(0,screenY-(ush-780),screenX,100,Color.DARKGRAY));
        background.add(new Circle(20,20,55,Color.YELLOW));
        background.add(new Rect(5,5,235,80,Color.WHITE));
        background.add(new RectS(5,5,235,80,Color.BLACK));
        background.add(new Texts(screenX - (usw-1377),264,"5 Protein",Color.WHITE)); // Egg Laying
        background.add(new Texts(screenX - (usw-1377 + 88),324,"1 Larva, 5 Protein, 5 Food",Color.WHITE)); // hatching larva
                //drawing background first
        reDraw();
        /*drawing / updating everything else*/
        confName.relocate(screenX + screenX,screenY+screenY);
        name.relocate(screenX + screenX,screenY+screenY);
        buildButton.relocate(screenX-(usw-1310),10);
        mapButton.relocate(screenX-(usw-1258),10);
        dictionaryButton.relocate(screenX-(usw-1166),10);
        nextDay.relocate(screenX-(usw-1365),10);
        aphids.relocate(15, 30);
        population.relocate(15,10);
        food.relocate(15,45);
        protein.relocate(15,60);
        ants.relocate(125,30);
        larvas.relocate(125,45);
        eggs.relocate(125,60);
        antsInUse.relocate(125,10);
        updateStats();
        showButtons();

        // tutorial stuff
        tutorial();
        reDraw();
    }

    // Helper method to reduce repeat code
    private boolean showStep() {
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == okButton;
    }

    // The Tutorial Sequence
    public void tutorial() {
        alert.setTitle("Tutorial: The Ant Hill");
        alert.setHeaderText("Welcome to The Ant Hill Tutorial!");
        alert.setContentText("Click okay to continue! \n(click 'close' to end tutorial)");
        reDraw();
        new Rect(0, 0, screenX, screenY, Color.rgb(128, 128, 128, 0.8)).draw(gc);
        if (!showStep()) return;

        alert.setHeaderText("See Your Resource Bar");
        alert.setContentText("This is your resource bar...\nIt holds the information concerning your nest.");
        reDraw();
        new Rect(245, 0, screenX, 90, Color.rgb(128, 128, 128, 0.8)).draw(gc);
        new Rect(0, 90, screenX, screenY, Color.rgb(128, 128, 128, 0.8)).draw(gc);
        if (!showStep()) return;

        alert.setHeaderText("Population");
        alert.setContentText("This is the amount of food consumers in your nest.\nEach time you click end day, you lose this much food.");
        reDraw();
        new Rect(105, 0, screenX, 30, Color.rgb(128, 128, 128, 0.8)).draw(gc);
        new Rect(0, 30, screenX, screenY, Color.rgb(128, 128, 128, 0.8)).draw(gc);
        if (!showStep()) return;

        alert.setHeaderText("Population");
        alert.setContentText("Your ant Queen is counted in this number, plus your 5 worker ants.\nThis also includes your larva.");
        if (!showStep()) return;

        alert.setHeaderText("Food");
        alert.setContentText("The first number is the food you have stored. \nThe second is the maximum you can store (according to your nests food storages) ");
        reDraw();
        new Rect(0, 0, screenX, 45, Color.rgb(128, 128, 128, 0.8)).draw(gc);
        new Rect(105, 45, screenX, 15, Color.rgb(128, 128, 128, 0.8)).draw(gc);
        new Rect(0, 60, screenX, screenY, Color.rgb(128, 128, 128, 0.8)).draw(gc);
        if (!showStep()) return;

        alert.setHeaderText("Food");
        alert.setContentText("The first number is the food you have stored. \nThe second is the maximum you can store (according to your nests food storages) ");
        if (!showStep()) return;

        alert.setHeaderText("Food");
        alert.setContentText("Running out of food results in your ants dying.\nIf all ants die (population 0), you loose your nest. (restarts game)");
        if (!showStep()) return;

        alert.setHeaderText("Map");
        alert.setContentText("This button takes you to the map. \nThis is where you can explore and collect more resources!");
        reDraw();
        new Rect(0, 0, screenX-(usw-1254), 40, Color.rgb(128, 128, 128, 0.8)).draw(gc);
        new Rect(screenX-(usw-1304), 0, screenX, 40, Color.rgb(128, 128, 128, 0.8)).draw(gc);
        new Rect(0, 40, screenX, screenY, Color.rgb(128, 128, 128, 0.8)).draw(gc);
        if (!showStep()) return;

        drawMap(null);
        alert.setHeaderText("Map");
        alert.setContentText("The Map is comprised of biomes with different resources and enemies. \nBiomes that haven't been explored are covered by a cloud.");
        new Rect(0, 0, screenX, screenY/2-125, Color.rgb(128, 128, 128, 0.8)).draw(gc);
        new Rect(0, screenY/2-125, screenX/2-555, 225, Color.rgb(128, 128, 128, 0.8)).draw(gc);
        new Rect(screenX/2-555+225, screenY/2-125, screenX, 225, Color.rgb(128, 128, 128, 0.8)).draw(gc);
        new Rect(0, screenY/2-125+225, screenX, screenY, Color.rgb(128, 128, 128, 0.8)).draw(gc);
        if (!showStep()) return;

        drawMap(null);
        drawMap(null);
        alert.setHeaderText("Map");
        alert.setContentText("Biomes right next to an explored biome have more transparent clouds.\n Use this to your advantage.");
        new Rect(0, 0, screenX, screenY/2-125, Color.rgb(128, 128, 128, 0.8)).draw(gc);
        new Rect(0, screenY/2-125, screenX/2-325, 225, Color.rgb(128, 128, 128, 0.8)).draw(gc);
        new Rect(screenX/2-325+225, screenY/2-125, screenX, 225, Color.rgb(128, 128, 128, 0.8)).draw(gc);
        new Rect(0, screenY/2-125+225, screenX, screenY, Color.rgb(128, 128, 128, 0.8)).draw(gc);
        if (!showStep()) return;

        drawMap(null);
        drawMap(null);
        alert.setHeaderText("Exploration");
        alert.setContentText("Clicking on a biome will allow you to explore it. \n (I'll clicked it for you)");
        new Rect(0, 0, screenX, 90, Color.rgb(128, 128, 128, 0.8)).draw(gc);
        new Rect(0, 90, screenX, screenY/2-125-90, Color.rgb(128, 128, 128, 0.8)).draw(gc);
        new Rect(0, screenY/2-125, screenX/2-325, 225, Color.rgb(128, 128, 128, 0.8)).draw(gc);
        new Rect(screenX/2-325+225, screenY/2-125, screenX, 225, Color.rgb(128, 128, 128, 0.8)).draw(gc);
        new Rect(0, screenY/2-125+225, screenX, screenY, Color.rgb(128, 128, 128, 0.8)).draw(gc);
        if (!showStep()) return;

        alert.setHeaderText("Exploration");
        alert.setContentText("exploring a new biome takes 5 ants.\nI'll click the 'send' button for you");
        mapSelect.getValueFactory().setValue(map.selected(screenX/2-200, screenY/2));
        cancelSelectionMethod(null);
        sending(null);
        new Rect(0, 0, 243, 90, Color.rgb(128, 128, 128, 0.8)).draw(gc);
        new Rect(465, 0, screenX, 90, Color.rgb(128, 128, 128, 0.8)).draw(gc);
        new Rect(0, 90, screenX, screenY/2-125-90, Color.rgb(128, 128, 128, 0.8)).draw(gc);
        new Rect(0, screenY/2-125, screenX/2-325, 225, Color.rgb(128, 128, 128, 0.8)).draw(gc);
        new Rect(screenX/2-325+225, screenY/2-125, screenX, 225, Color.rgb(128, 128, 128, 0.8)).draw(gc);
        new Rect(0, screenY/2-125+225, screenX, screenY, Color.rgb(128, 128, 128, 0.8)).draw(gc);
        if (!showStep()) return;

        sending(null); //sending to the selected spot.

        alert.setHeaderText("Collecting");
        alert.setContentText("Once a biome is discovered, information on it can be found next to your resource bar.");
        mapSelect.getValueFactory().setValue(map.selected(screenX/2-200, screenY/2));
        cancelSelectionMethod(null);
        sending(null);
        new Rect(0, 0, 243, 90, Color.rgb(128, 128, 128, 0.8)).draw(gc);
        new Rect(465, 0, screenX, 90, Color.rgb(128, 128, 128, 0.8)).draw(gc);
        new Rect(0, 90, screenX, screenY/2-125-90, Color.rgb(128, 128, 128, 0.8)).draw(gc);
        new Rect(0, screenY/2-125, screenX/2-325, 225, Color.rgb(128, 128, 128, 0.8)).draw(gc);
        new Rect(screenX/2-325+225, screenY/2-125, screenX, 225, Color.rgb(128, 128, 128, 0.8)).draw(gc);
        new Rect(0, screenY/2-125+225, screenX, screenY, Color.rgb(128, 128, 128, 0.8)).draw(gc);
        if (!showStep()) return;

        alert.setHeaderText("Collecting");
        alert.setContentText("You can see its name, \nwhat is held there, \nand how many items per ant you can collect.");
        mapSelect.getValueFactory().setValue(map.selected(screenX/2-200, screenY/2));
        cancelSelectionMethod(null);
        sending(null);
        new Rect(0, 0, 243, 90, Color.rgb(128, 128, 128, 0.8)).draw(gc);
        new Rect(465, 0, screenX, 90, Color.rgb(128, 128, 128, 0.8)).draw(gc);
        new Rect(0, 90, screenX, screenY, Color.rgb(128, 128, 128, 0.8)).draw(gc);
        if (!showStep()) return;

        alert.setHeaderText("Collecting");
        alert.setContentText("When collecting resources you will be stopped from sending to many ants.\n" +
                "However, if your storage is full, your ants will still be sent, but will fail to store the food.");
        mapSelect.getValueFactory().setValue(map.selected(screenX/2-200, screenY/2));
        cancelSelectionMethod(null);
        sending(null);
        new Rect(0, 0, screenX, screenY, Color.rgb(128, 128, 128, 0.8)).draw(gc);
        if (!showStep()) return;


        alert.setHeaderText("Available Ants");
        alert.setContentText("Throughout the game, you will need to use your ants to complete tasks");
        drawMap(null);
        drawMap(null);
        new Rect(0, 0, 115, 27, Color.rgb(128, 128, 128, 0.8)).draw(gc);
        new Rect(245, 0, screenX, 27, Color.rgb(128, 128, 128, 0.8)).draw(gc);
        new Rect(0, 27, screenX, screenY, Color.rgb(128, 128, 128, 0.8)).draw(gc);
        if (!showStep()) return;

        alert.setHeaderText("Available Ants");
        alert.setContentText("This is where you can see how many ants you can still use this day. \nToday we used all 5");
        if (!showStep()) return;

        drawMap(null);
        drawMap(null);
        new Rect(0, 0, screenX-80, screenY, Color.rgb(128, 128, 128, 0.8)).draw(gc);
        new Rect(screenX-80, 40, 80, screenY, Color.rgb(128, 128, 128, 0.8)).draw(gc);
        alert.setHeaderText("Next Day");
        alert.setContentText("To get your ants back, click the 'next day' button.\n" +
                "This will cause the population of your nest to eat food, but will make all live ants available for work.");
        if (!showStep()) return;

        drawMap(null);
        drawMap(null);
        new Rect(0, 0, screenX-80, screenY, Color.rgb(128, 128, 128, 0.8)).draw(gc);
        new Rect(screenX-80, 40, 80, screenY, Color.rgb(128, 128, 128, 0.8)).draw(gc);
        alert.setHeaderText("Next Day");
        alert.setContentText("I will click 'Next Day' for you, but will return any food that is consumed");
        if (!showStep()) return;

        nextDayMethod(null);
        nest.addFood(6);
        System.out.println(nest.getFood());
        updateStats();

        alert.setHeaderText("Day Counter");
        alert.setContentText("This is your day counter, for keeping track of how long you have survived.");
        new Rect(0, 0, screenX, screenY-80, Color.rgb(128, 128, 128, 0.8)).draw(gc);
        new Rect(0, screenY-80, screenX-50, 80, Color.rgb(128, 128, 128, 0.8)).draw(gc);
        if (!showStep()) return;

        reDraw();
        alert.setHeaderText("Tutorial End");
        alert.setContentText("There is much more for you to experience and learn but now is not the time,\n Good luck!" +
                "Tutorial completed for: "+nest.getName());
        new Rect(0, 0, screenX, screenY, Color.rgb(128, 128, 128, 0.8)).draw(gc);
        if (!showStep()) return;

    }


    /// redDraw method that draws the background, then the majority of the foreground.
    public void reDraw(){
        for(int i = 0; i < background.size(); i++){
            background.get(i).draw(gc);
        }
        for(int i = 0; i < nest.getBuildings().size(); i++){
            nest.getBuildings().get(i).draw(gc);
            for (int j = 0; j < nest.getBuildings().get(i).getAnts().size(); j++){
                nest.getBuildings().get(i).getAnts().get(j).drawAnt(
                        nest.getBuildings().get(i).getX(), //no need for usx since the building already has these added.
                        nest.getBuildings().get(i).getY(), //no need for usx since the building already has these added.
                        gc, j);
            }
        }
        new Texts(screenX - 40, screenY - 43, "Day " + nest.getDays(), Color.WHITE).draw(gc);
    }

    //building methods (take up most of the file honestly... def was a MUCH better way to do this.)
    /// this build method is what starts the process, call it again to end building.
    /// it searches through the different building spots to check if they are being used... this is inefficient...
    public void build(ActionEvent e){//starting building
        showButtons();
        mapSelectButton.relocate(-100,-100);
        mapSelectButton.setFocusTraversable(false);
        mapSelect.relocate(-100,-100);
        mapSelect.setFocusTraversable(false);
        mapButton.setText("Map");
        mapDrawn = false;
        cancelSelectionMethod(e);
        reDraw();
        //displaying the building places
        for(int i = 0; i < build.size(); i++){
            if ((i < 2 || path.getFirst().isBuildingDone())) {
                build.get(i).spaceOpen(gc);
            }
        }
        for(int i = 0; i < path.size(); i++){
            path.get(i).spaceOpen(gc);
        }
        //deciding if it is canceling building, or not.
        if (!buildCancel) {
            //turning the build button into a cancel button
            buildButton.setText("Cancel");
            buildCancel = true;
        } else { //ending building using the new cancel button
            doneBuilding();
        }
    }

    /// This is for recording which building spot you are selecting to build on, and then displaying what you can build.
    public void theBuildSpace(BuildingSpot building, int i){ //for other buildings (not paths)
        buildingX = building.getBuildingX();
        buildingY = building.getBuildingY();
        barracksButton.relocate(10, 220);
        barracksButton.setFocusTraversable(true);
        requirements1.setText("5 Ants not working, 10 Food");
        foodStorageButton.relocate(10,280);
        foodStorageButton.setFocusTraversable(true);
        requirements2.setText("5 Ants not working, 5 Food, 5 Protein");
        proteinStorageButton.relocate(10,340);
        proteinStorageButton.setFocusTraversable(true);
        requirements3.setText("5 Ants not working, 5 Food, 5 Protein");
        aphidFarmButton.relocate(10,400);
        aphidFarmButton.setFocusTraversable(true);
        requirements4.setText("10 Ants not working, 10 protein");
        pathsButton.relocate(-100,-100);
        pathsButton.setFocusTraversable(false);
        flip = building.getFlip();
        spot = i;
    }
    /// This is for recording which building spot (for only paths) you are selecting to build on, and then displaying what you can build.
    public void theBuildSpace(PathSpot building, int i){ //for only paths
        buildingX = building.getBuildingX();
        buildingY = building.getBuildingY();
        pathsButton.relocate(10, 220);
        pathsButton.setFocusTraversable(true);
        requirements1.setText("5 Ants not working, 5 Food");
        barracksButton.relocate(-100,-100);
        barracksButton.setFocusTraversable(false);
        foodStorageButton.relocate(-100,-100);
        foodStorageButton.setFocusTraversable(false);
        requirements2.setText("");
        proteinStorageButton.relocate(-100, -100);
        proteinStorageButton.setFocusTraversable(false);
        requirements3.setText("");
        aphidFarmButton.relocate(-100,-100);
        aphidFarmButton.setFocusTraversable(false);
        requirements4.setText("");
        spot = i;
    }

    /// these are all for assigning the buildHere buttons functions...
    public void buildHere(MouseEvent me) {
        if(buildCancel) {
            //displaying the building plots
            for(int i = 0; i < build.size(); i++){
                if ((i < 2 || path.getFirst().isBuildingDone())) {
                    build.get(i).spaceOpen(gc);
                }
            }
            for(int i = 0; i < path.size(); i++){
                path.get(i).spaceOpen(gc);
            }
            boolean complete = false;
            for (int i = 0; i < path.size() && !complete; i++) {
                if (me.getX() >= path.get(i).getBuildingX() && me.getX() <= path.get(i).getBuildingX() + 20 &&
                        me.getY() >= path.get(i).getBuildingY() && me.getY() <= path.get(i).getBuildingY() + 250) {
                    if (!path.get(i).isBuildingDone()) {
                        theBuildSpace(path.get(i),i);
                        complete = true;
                        path.get(i).getBuild().draw(gc);
                    }
                }
            }
            for (int i = 0; i < build.size() && !complete; i++) {
                if (me.getX() >= build.get(i).getBuildingX() && me.getX() <= build.get(i).getBuildingX() + 150 &&
                        me.getY() >= build.get(i).getBuildingY() && me.getY() <= build.get(i).getBuildingY() + 75) {
                    if (!build.get(i).isBuildingDone() && (i < 2 || path.getFirst().isBuildingDone())) {
                        theBuildSpace(build.get(i), i);
                        complete = true;
                        build.get(i).getBuild().draw(gc);
                    }
                }
            }
            if (!complete){
                hideBuildButtons();
            }
        }

    }

    /// the function that runs when you select that you want to build a Barrack.
    public void buildABarrack(ActionEvent e){
        if (nest.AddAntsInUse(5)){
            if (nest.minusFood(10)) {
                nest.addBuilding(new Barracks(buildingX, buildingY, flip, build.get(spot)));
                title = "Building Completed";
                messege1 = "You built a barrack";
                messege2 = "+10 max ants";
                build.get(spot).used();
            } else {
                nest.minusAntsInUse(5);
                title = "Insufficient Resources";
                messege1 = "Not enough food";
            }
        } else {
            title = "Not Enough Ants";
            messege1 = "You don't have enough unused ants";
        }
        doneBuilding();
        food.setText("food: " + nest.getFood() + " / " + nest.getMaxFood());
        antsInUse.setText("available ants: " + ((nest.getAnts().size() - 1 ) - (nest.getAntsInUse())) + " / " + (nest.getAnts().size() - 1));
        ants.setText("ants: " + (nest.getAnts().size()-1) + " / " + (nest.getMaxAnts()-1));
    }
    /// the method for building a FoodStorage
    public void buildAFoodStorage(ActionEvent e){
        if (nest.AddAntsInUse(5)) {
            if (nest.minusFood(5)) {
                if (nest.minusProtein(5)) {
                    nest.addBuilding(new FoodStorage(buildingX, buildingY, flip, build.get(spot)));
                    title = "Building Completed";
                    messege1 = "You built a food storage";
                    messege2 = "+25 max food";
                    build.get(spot).used();
                } else {
                    nest.addFood(5);
                    nest.minusAntsInUse(5);
                    title = "Insufficient Resources";
                    messege1 = "Not enough protein";
                }
            } else {
                nest.minusAntsInUse(5);
                title = "Insufficient Resources";
                messege1 = "Not enough food";
            }
        } else {
            title = "Not Enough Ants";
            messege1 = "You don't have enough unused ants";
        }
        doneBuilding();
        food.setText("food: " + nest.getFood() + " / " + nest.getMaxFood());
        protein.setText("protein: " + nest.getProtein() + " / " + nest.getMaxProtein());
        antsInUse.setText("available ants: " + ((nest.getAnts().size() - 1 ) - (nest.getAntsInUse())) + " / " + (nest.getAnts().size() - 1));
    }
    /// the method for building a protein storage.
    public void buildAProteinStorage(ActionEvent e){
        if (nest.AddAntsInUse(5)) {
            if (nest.minusFood(5)) {
                if (nest.minusProtein(5)) {
                    nest.addBuilding(new ProteinStorage(buildingX, buildingY, flip, build.get(spot)));
                    title = "Building Completed";
                    messege1 = "You built a protein storage";
                    messege2 = "+25 max protein";
                    build.get(spot).used();
                } else {
                    nest.addFood(5);
                    nest.minusAntsInUse(5);
                    title = "Insufficient Resources";
                    messege1 = "Not enough protein";
                }
            } else {
                nest.minusAntsInUse(5);
                title = "Insufficient Resources";
                messege1 = "Not enough food";
            }
        } else {
            title = "Not Enough Ants";
            messege1 = "You don't have enough unused ants";
        }
        doneBuilding();
        food.setText("food: " + nest.getFood() + " / " + nest.getMaxFood());
        protein.setText("protein: " + nest.getProtein() + " / " + nest.getMaxProtein());
        antsInUse.setText("available ants: " + ((nest.getAnts().size() - 1 ) - (nest.getAntsInUse())) + " / " + (nest.getAnts().size() - 1));
    }
    /// the method for building an aphid farm.
    public void buildAAphidFarm(ActionEvent e){
        if (nest.AddAntsInUse(10)) {
            if (nest.minusProtein(10)) {
                nest.addBuilding(new AphidFarm(buildingX, buildingY, flip, build.get(spot)));
                title = "Building Completed";
                messege1 = "You built an aphid farm";
                messege2 = "+5 aphid storage space";
                build.get(spot).used();
            } else {
                nest.minusAntsInUse(10);
                title = "Insufficient Resources";
                messege1 = "Not enough protein";
            }
        } else {
            title = "Not Enough Ants";
            messege1 = "You don't have enough unused ants";
        }
        doneBuilding();
        aphids.setText("aphids: " + nest.getAphids() + " / "+nest.getMaxAphids());
        protein.setText("protein: " + nest.getProtein() + " / " + nest.getMaxProtein());
        antsInUse.setText("available ants: " + ((nest.getAnts().size() - 1 ) - (nest.getAntsInUse())) + " / " + (nest.getAnts().size() - 1));
    }
    /// the method for building a Path
    public void buildAPath(ActionEvent e){
        if (nest.AddAntsInUse(5)){
            if (nest.minusFood(5)) {
                nest.addBuilding(new Path(usx + buildingX, usy + buildingY, path.get(spot)));
                title = "Building Completed";
                messege1 = "You built a new path";
                messege2 = "new building spots available";
                path.get(spot).used();
            } else {
                nest.minusAntsInUse(5);
                title = "Insufficient Resources";
                messege1 = "Not enough food";
            }
        } else {
            title = "Not Enough Ants";
            messege1 = "You don't have enough unused ants";
        }
        doneBuilding();
        food.setText("food: " + nest.getFood() + " / " + nest.getMaxFood());
        antsInUse.setText("available ants: " + ((nest.getAnts().size() - 1 ) - (nest.getAntsInUse())) + " / " + (nest.getAnts().size() - 1));
    }
    ///universal shut down for building
    public void doneBuilding(){
        background.add(nest.Graphics(usx,0).get(0));
        background.add(nest.Graphics(usx,0).get(1));
        background.add(nest.Graphics(usx,0).get(2));
        hideBuildButtons();
        reDraw();
        buildCancel = false;
        buildButton.setText("Build");
        if (!Objects.equals(title, "")) {
            showTextBox();
        }
    }
    //hides the build buttons (like build barrack, or build path)
    public void hideBuildButtons(){
        barracksButton.relocate(-100,-100);
        barracksButton.setFocusTraversable(false);
        pathsButton.relocate(-100,-100);
        pathsButton.setFocusTraversable(false);
        requirements1.setText("");
        foodStorageButton.relocate(-100,-100);
        foodStorageButton.setFocusTraversable(false);
        requirements2.setText("");
        proteinStorageButton.relocate(-100,-100);
        proteinStorageButton.setFocusTraversable(false);
        requirements3.setText("");
        aphidFarmButton.relocate(-100,-100);
        aphidFarmButton.setFocusTraversable(false);
        requirements4.setText("");
    }













    ///next day function
    /// this takes care of the day counter, how much food is used up, eggs hatching, and dying.
    public void nextDayMethod(ActionEvent e){
        //removing the map buttons after returning to main screen, and returning buttons that should appear on the nest screen.
        mapSelectButton.relocate(-100,-100);
        mapSelectButton.setFocusTraversable(false);
        mapSelect.relocate(-100,-100);
        mapSelect.setFocusTraversable(false);
        mapButton.setText("Map");
        showButtons();
        //actual end day function
        int previousAnts = nest.getAnts().size();//recording how many game.ants we had
        int foodGenerated = nest.aphidFoodGeneration(); //gets how much food is generated by farms, AND adds that food to the nest.
        mapDrawn = false;
        cancelSelectionMethod(e);
        doneBuilding();
        nest.clearAntsInUse();
        /*calculating, how many ants should die (if any)*/
        int remainingFood = nest.getFood();
        if (!nest.minusFood(nest.getAnts().size())){
            nest.minusFood(nest.getFood());
            //checking for dead ants and a loss of the game.
            int deaths =  nest.getAnts().size() - remainingFood;
            if (!nest.minusAnts(deaths)){ //game lost
                gc.setFill(Color.BLACK);
                gc.fillRect(0,0,screenX,screenY);
                //removing all remaining gui things
                nextDay.relocate(-100,-100);
                buildButton.relocate(-100,-100);
                mapButton.relocate(-100,-100);
                hideButtons();
                requirements1.relocate(usx + usw/2,usy + ush/2);
                requirements1.setFill(Color.WHITE);
                requirements1.setText("YOU LOSE");
                messege3 = "dead";
            } else {
                reDraw();
            }
        }
        //checking for the dead larva
        if (!nest.getAnts().isEmpty()) { //basically checking that the nest still alive
            if (!nest.minusFood(nest.getLarva().size())) {
                remainingFood = nest.getFood();
                nest.minusFood(nest.getFood()); // removes all the food to feed what we could.
                int death = nest.getLarva().size() - remainingFood; //calculating total deaths
                nest.minusLarva(death); // actually getting rid of the larva handled elegantly by nest class
                reDraw(); //redraws for when larva graphics are included.
            }
        }
        if(!messege3.equals("dead")) {
            //the next day method also handles whether an egg will hatch.
            messege2 = "Larva hatched: " + nest.nextDay();
            title = "DAY : " + (nest.getDays() - 1) + " ~> " + nest.getDays();
            messege1 = "Total Ants Lost: " + (nest.getAnts().size() - previousAnts);
            if (nest.getAphids() > 0) {
                messege2 = "Food generated by farms: " + foodGenerated;
            }
            showTextBox();
            updateStats();
            new Rect(screenX - 20,screenY - 60,40,20,Color.rgb(61, 35, 13)).draw(gc);
            new Texts(screenX - 40, screenY - 43, "Day " + nest.getDays(), Color.WHITE).draw(gc);
        }
    }
    /// this updates all the states being displayed on the screen, to match the nests stats.
    public void updateStats(){
        population.setText("population: "+(nest.getPopulation()));
        aphids.setText("aphids: " + nest.getAphids() + " / "+nest.getMaxAphids());
        food.setText("food: "+nest.getFood()+" / "+nest.getMaxFood());
        protein.setText("protein: "+nest.getProtein()+" / "+nest.getMaxProtein());
        ants.setText("ants: "+(nest.getAnts().size()-1)+" / "+(nest.getMaxAnts()-1));
        larvas.setText("larva: "+(nest.getLarva().size())+" / "+nest.getMaxLarva());
        eggs.setText("eggs: "+(nest.getEggs().size())+" / "+nest.getMaxEggs());
        antsInUse.setText("available ants: " + ((nest.getAnts().size() - 1 ) - (nest.getAntsInUse())) + " / " + (nest.getAnts().size() - 1));
    }















    //map function(s)
    ///opening and closing map button
    public void drawMap(ActionEvent e){
        if (!mapDrawn) { //opening map

            if (buildCancel) {
                build(e);
            }
            map.updateDraw(gc);
            hideButtons();
            mapDrawn = true;
            mapButton.setText("Nest");
        } else { //closing map
            cancelSelectionMethod(e);
            reDraw();
            showButtons();
            mapSelectButton.relocate(-100,-100);
            mapSelect.relocate(-100,-100);
            mapDrawn = false;
            mapButton.setText("Map");
            new Texts(screenX - 40, screenY - 43, "Day " + nest.getDays(), Color.WHITE).draw(gc);
        }
    }

    //map interactions (super large... unfortunately)
    /// this allows the enter button to control the spinner(the selector).
    public void sendings(KeyEvent e){
        if (e.getCode() == KeyCode.ENTER) {
            sending(null);
        }
    }
    /// the sending function, which is also first the selecting function for the map.
    /// selects a space on the map, then sends stuff to it after running this method again.
    /// It also handles all the interactions between the nest and map, and all prints concerning that... will move SOME
    /// of these into the map file.
    public void sending(ActionEvent e){ // the action can be null, so don't program anything here that would need a non-null event...
        if (mapDrawn) {
            map.draw(gc); // just ensuring that all graphics are correct... for if a bug occurred... (one was occurring, but putting this here fixed it)
            boolean complete = false; //tracts if the actual sending function is completed.
            int exploreNum = 5; //the minimum number of game.ants it takes to explore an area
            if (!selected) { //selecting something
                num = (Integer) mapSelect.getValue();
                map.biome(num).selected(gc);
                //displaying square
                new Rect(245,5,220,80,Color.WHITE).draw(gc);
                new RectS(245,5,220,80,Color.BLACK).draw(gc);
                new Texts(254, 24, map.biome(num).getType()).draw(gc);
                //collecting and displaying info
                if (!map.biome(num).getFound()) { //current statement for seeing if a biome is found yet.
                    if (map.biome(num).isAdjacent()) {
                        new Rect(245,5,220,80,Color.WHITE).draw(gc);
                        new RectS(245,5,220,80,Color.BLACK).draw(gc);
                        new Texts(254, 24, "Area not yet Explored").draw(gc);
                        new Texts(254, 39, "Send " + exploreNum + " ants to explore this tile?").draw(gc);
                        mapSelect.relocate(-100, -100);
                        mapSelect.setFocusTraversable(false);
                        mapSelectButton.relocate(usx + 675, 10);
                        mapSelectButton.setFocusTraversable(true);
                        cancelSelection.setFocusTraversable(true);
                    } else {
                        new Rect(245,5,220,80,Color.WHITE).draw(gc);
                        new RectS(245,5,220,80,Color.BLACK).draw(gc);
                        new Texts(254, 24, "Cannot Explore").draw(gc);
                        new Texts(254, 39, "Area not next to an explored tile.").draw(gc);
                        selected = true; // causes the whole interaction to reset early. (not go through with the sending)
                    }

                    // for spots that cannot be interacted with (whether that be changed later or not) (other than spots that contain nothing)
                } else if (map.biome(num) instanceof AntHill || map.biome(num).getContent().equals("Human, stay away from them...")
                        || map.biome(num).getContent().equals("nothing") || map.biome(num).getAmount() == 0) {
                    new Texts(254, 39, map.biome(num).getContent()).draw(gc);
                    if (map.biome(num).getAmount() == 0) {
                        new Texts(254, 54, "Empty").draw(gc);
                    }
                    selected = true;
                } else {//other spots typical display for their content.
                    mapSelectButton.relocate(usx + 775,  10);
                    mapSelect.relocate(usx + 600, 10);
                    cancelSelection.setFocusTraversable(true);
                    mapSelect.setFocusTraversable(true);
                    new Texts(254, 39, map.biome(num).getContent() + " :  " + map.biome(num).getAmount()).draw(gc);
                    if (map.biome(num).getContent().equals("aphids")) {
                        new Texts(254, 54, "items per ant :  " + 1).draw(gc);
                    } else {
                        new Texts(254, 54, "items per ant :  " + map.biome(num).getAntMultiplier()).draw(gc);
                    }
                    // extra info for when a bug is on the space
                    if (map.biome(num).getIsBug()) {
                        new Texts(254,  69, "Total Bug Strength :  " + map.biome(num).getStrength()).draw(gc);
                    }
                }
                //noinspection unchecked
                mapSelect.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 1000000000));
                //noinspection unchecked
                mapSelect.getValueFactory().setValue(null); // resetting the spinner

                //end of selecting function
                if (!selected) {
                    new Rect(5, 5, 235, 80, Color.WHITE).draw(gc);
                    new RectS(5,  5, 235, 80, Color.BLACK).draw(gc);
                    mapSelectButton.setText("Send");
                    mapSelectButton.setFocusTraversable(true);
                    selected = true;
                    if (map.biome(num).isAdjacent() && !map.biome(num).getFound()) {
                        cancelSelection.relocate(usx + 725, 10);
                    } else {
                        cancelSelection.relocate(usx + 835, 10);
                    }
                } else {
                    selected = false;
                }
                //start of sending function
            } else {
                //actual function for interaction //(could prolly be stored somewhere else other than in this file)... willllll I change it? prolly not.
                title = "";
                messege1 = "";
                messege2 = "";
                messege3 = "";
                if (!map.biome(num).getFound()) { //for when the interaction is with an un-found/undiscovered biome/area
                    if (nest.AddAntsInUse(exploreNum)) { //for when it is adjacent.
                        title = "Area Explored";
                        messege1 = "Explored a " + map.biome(num).getType() + ".";
                        map.biome(num).setFound(true);
                        map.draw(gc);
                    } else { // the only other thing possible here now is if the player send the 'exploreNum' amount of game.ants.
                        title = "Cannot Send";
                        messege1 = "You don't have enough unused ants";
                    }
                    //end of un-found area interaction

                    //for when you are sending no ants
                } else if (mapSelect.getValue() == null || mapSelect.getValue().equals(0)) {
                    title = "No ants sent";
                    messege1 = "You sent no ants.";

                } else if (mapSelect.getValue() != null && nest.AddAntsInUse((Integer) mapSelect.getValue())) {
                    if (map.biome(num).getAmount() == 0) { //for when it is an empty space or has been used up
                        nest.minusAntsInUse((Integer) mapSelect.getValue());
                        title = "Empty Space";
                        messege1 = "Don't send ants here!";
                        //end of empty space interaction

                        //this next if statement is for ALL battling bugs.
                    } else if (map.biome(num).getIsBug()) {
                        if ((Integer) mapSelect.getValue() > 0) {
                            int antsLost = (map.biome(num).battle((Integer) mapSelect.getValue()));
                            nest.minusAnts(antsLost);
                            if (antsLost == (Integer) mapSelect.getValue()) {
                                title = "Battle Lost";
                                messege1 = "Your sent ants died";
                            } else {
                                title = "Battle Won!";
                                messege1 = "Space changed into protein";
                                map.draw(gc);
                            }
                            messege2 = "Total deaths :  " + antsLost;
                        } else {
                            title = "No ants sent";
                            messege1 = "You sent no ants";
                        }
                        //end of bug interaction

                        //this statement is for seeds
                    } else if (map.biome(num).getContent().equals("seeds")) {
                        for (int i = 0; i <= map.biome(num).getAntMultiplier(); i++) {
                            if (map.biome(num).getAmount() >= ((Integer) mapSelect.getValue() * map.biome(num).getAntMultiplier()) - i) {
                                map.biome(num).subtractAmount((Integer) mapSelect.getValue() * map.biome(num).getAntMultiplier() - i);
                                int foodLeft = nest.addFood((Integer) mapSelect.getValue() * map.biome(num).getAntMultiplier() - i);
                                int proteinLeft = nest.addProtein((Integer) mapSelect.getValue() * map.biome(num).getAntMultiplier() - i);
                                title = "Ants Sent :  " + mapSelect.getValue();
                                messege1 = "You collected " + ((((int) mapSelect.getValue() * map.biome(num).getAntMultiplier()) - i) - foodLeft) + " food";
                                messege2 = "You collected " + ((((int) mapSelect.getValue() * map.biome(num).getAntMultiplier()) - i) - proteinLeft) + " protein";
                                if (foodLeft > 0 && proteinLeft > 0) {
                                    if (foodLeft < proteinLeft) {
                                        map.biome(num).addAmount(foodLeft);
                                        messege3 = foodLeft + " seeds left behind. (storage is full)";
                                    } else {
                                        map.biome(num).addAmount(proteinLeft);
                                        messege3 = proteinLeft + " seeds left behind. (storage is full)";
                                    }
                                }
                                complete = true;
                                break;
                            }
                        }
                        if (!complete) {
                            nest.minusAntsInUse((Integer) mapSelect.getValue());
                            title = "To Many Ants!";
                            messege1 = "You tried sending more ants then";
                            messege2 = "needed";
                        }
                        //end of seeds interactions

                        //this next statement is for food
                    } else if (map.biome(num).getContent().equals("mushrooms") || map.biome(num).getContent().equals("crumbs") || map.biome(num).getContent().equals("nectar")) {
                        for (int i = 0; i <= map.biome(num).getAntMultiplier(); i++) {
                            if (map.biome(num).getAmount() >= ((Integer) mapSelect.getValue() * map.biome(num).getAntMultiplier()) - i) {
                                map.biome(num).subtractAmount((Integer) mapSelect.getValue() * map.biome(num).getAntMultiplier() - i);
                                int foodLeft = nest.addFood((Integer) mapSelect.getValue() * map.biome(num).getAntMultiplier() - i);
                                title = "Ants Sent :  " + mapSelect.getValue();
                                messege1 = "You collected " + ((((int) mapSelect.getValue() * map.biome(num).getAntMultiplier()) - i) - foodLeft) + " food";
                                if (foodLeft > 0) {
                                    map.biome(num).addAmount(foodLeft);
                                    messege2 = foodLeft + " food left behind. (storage is full)";
                                }
                                complete = true;
                                break;
                            }
                        }
                        if (!complete) {
                            nest.minusAntsInUse((Integer) mapSelect.getValue());
                            title = "To Many Ants!";
                            messege1 = "You tried sending more ants then";
                            messege2 = "needed";
                        }
                        //end of food interactions

                        //start of protein interaction
                    } else if (map.biome(num).getContent().equals("protein") || map.biome(num).getContent().equals("pollen")) {
                        for (int i = 0; i <= map.biome(num).getAntMultiplier(); i++) {
                            if (map.biome(num).getAmount() >= ((Integer) mapSelect.getValue() * map.biome(num).getAntMultiplier()) - i) {
                                map.biome(num).subtractAmount((Integer) mapSelect.getValue() * map.biome(num).getAntMultiplier() - i);
                                int proteinLeft = nest.addProtein((Integer) mapSelect.getValue() * map.biome(num).getAntMultiplier() - i);
                                title = "Ants Sent :  " + mapSelect.getValue();
                                messege1 = "You collected " + ((((int) mapSelect.getValue() * map.biome(num).getAntMultiplier()) - i) - proteinLeft) + " protein";
                                if (proteinLeft > 0) {
                                    map.biome(num).addAmount(proteinLeft);
                                    messege2 = proteinLeft + " protein left behind. (storage is full)";
                                }
                                complete = true;
                                break;
                            }
                        }
                        if (!complete) {
                            nest.minusAntsInUse((Integer) mapSelect.getValue());
                            title = "To Many Ants!";
                            messege1 = "You tried sending more ants then";
                            messege2 = "needed";
                        }
                        //end of protein interaction

                        //start of aphid interaction
                    } else if (map.biome(num).getContent().equals("aphids")) {
                        if (nest.getMaxAphids() > 0) {
                            if (map.biome(num).getAmount() >= ((Integer) mapSelect.getValue())) {
                                map.biome(num).subtractAmount((Integer) mapSelect.getValue());
                                int aphidsLeft = nest.addAphids((Integer) mapSelect.getValue());
                                title = "Ants Sent :  " + mapSelect.getValue();
                                messege1 = "You collected " + ((int) mapSelect.getValue() - aphidsLeft) + " aphids";
                                if (aphidsLeft > 0) {
                                    map.biome(num).addAmount(aphidsLeft);
                                    messege2 = aphidsLeft + " aphid(s) left behind. (storage is full)";
                                }
                                complete = true;
                            }
                            if (!complete) {
                                nest.minusAntsInUse((Integer) mapSelect.getValue());
                                title = "To Many Ants!";
                                messege1 = "You tried sending more ants then";
                                messege2 = "needed";
                            }
                        } else {
                            nest.minusAntsInUse((Integer) mapSelect.getValue());
                            title = "No Aphid storage!";
                            messege1 = "Your ant nest needs an aphid farm";
                            messege2 = "in-order to store aphids.";
                        }
                        //end of aphids interaction

                    } else { // end of interactions, catching if a 'bug' occurs and returning your game.ants (not using them up)
                        nest.minusAntsInUse((Integer) mapSelect.getValue());
                        title = "Error";
                        messege1 = "'Bug' occurred, all ants returned";
                    }
                } else { //nothing could be done, as you don't have enough game.ants
                    title = "Cannot Send";
                    messege1 = "You don't have enough unused ants";
                }

                //end of sending function (drawing an updated version of everything)
                mapSelect.relocate(-100, -100);
                mapSelect.setFocusTraversable(false);
                mapSelectButton.relocate(-100, -100);
                mapSelectButton.setFocusTraversable(false);
                cancelSelection.relocate(-100, -100);
                cancelSelection.setFocusTraversable(false);
                selected = false;
                aphids.setText("aphids: " + nest.getAphids() + " / " + nest.getMaxAphids());
                food.setText("food: " + nest.getFood() + " / " + nest.getMaxFood());
                protein.setText("protein: " + nest.getProtein() + " / " + nest.getMaxProtein());
                antsInUse.setText("available ants: " + ((nest.getAnts().size() - 1) - (nest.getAntsInUse())) + " / " + (nest.getAnts().size() - 1));
                ants.setText("ants: " + (nest.getAnts().size() - 1) + " / " + (nest.getMaxAnts() - 1));
                population.setText("population: " + (nest.getPopulation()));
                map.updateDraw(gc);
                showTextBox();
                //noinspection unchecked
                mapSelect.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, map.biomes.size() - 1));
                //noinspection unchecked
                mapSelect.getValueFactory().setValue(null); //resetting the spinner
            }
        }
    }

    /// detects when the mouse first goes down.
    public void mousePressed(MouseEvent me){
        mousex = me.getX();
        mousey = me.getY();
        cancelSelectionMethod(null);
    }

    /// cancels the selecting that you've made on the map.
    public void cancelSelectionMethod(ActionEvent e){
        mapSelectButton.setText("Select");
        selected = false;
        if (mapDrawn) {
            map.draw(gc);
        }
        //returning buttons to normal
        mapSelectButton.relocate(-100,-100);
        mapSelectButton.setFocusTraversable(false);
        mapSelect.relocate(-100,-100);
        mapSelect.setFocusTraversable(false);
        cancelSelection.relocate(-100,-100);
        cancelSelection.setFocusTraversable(false);
    }

    /// detects when the mouse pointer has been pressed, and records that
    public void mouseMoving(MouseEvent me) {
        //deciding if the mouse moved far enough for the next click to not count.
        if (Math.abs(Math.abs(mousex) - Math.abs(me.getX())) > 15  || Math.abs(Math.abs(mousey) - Math.abs(me.getY())) > 15)
            click = false;
        //canceling your selection
        mapSelectButton.setText("Select");
        selected = false;
        if (mapDrawn) {
            map.draw(gc);
        }
        cancelSelection.relocate(-100, -100);
        cancelSelection.setFocusTraversable(false);

        //making the map move using the mouse... does work right YET
        if (mapDrawn) {

            if (mousex > me.getX()) {
                map.right(gc, (int) (mousex - me.getX()));
                mousex = me.getX();
                map.draw(gc);
            } else if (mousex < me.getX()) {
                map.left(gc, (int) (me.getX() - mousex));
                mousex = me.getX();
                map.draw(gc);
            }
            if (mousey < me.getY()) {
                map.up(gc, (int) (me.getY() - mousey));
                mousey = me.getY();
                map.draw(gc);
            } else if (mousey > me.getY()) {
                map.down(gc, (int) (mousey - me.getY()));
                mousey = me.getY();
                map.draw(gc);
            }

        }
    }

    /**
     *  this method allows us to select biome/area tiles by clicking them
     * @param me (mouseEvent)
     */
    public void selecting(MouseEvent me){
        if(click) {
            mapSelect.requestFocus();
            if (mapDrawn) {
                int select = map.selected(me.getX(), me.getY());
                if (select != -1) {
                    mapSelect.getValueFactory().setValue(select);
                    cancelSelectionMethod(null);
                    sending(null);
                }
            }
        } else {
            click = true;
        }
    }










    ///adding game. ants and eggs code and stuff, and what not. graphics for these things will be located in the game.ants.Ants, or game.Eggs file.
    public void layingEgg(ActionEvent e) {
        if (nest.getProtein() >= 5) {
            nest.minusProtein(5);
            if (!nest.addEgg()){
                title = "Insufficient Egg Space";
                messege1 = "You've hit your maximum amount";
                messege2 = "of eggs";
            } else { //this is when success happens
                title = "New Egg!";
                messege1 = "Your Queen laid an egg";
                messege2 = "Thank her";
            }
        } else {
            title = "Insufficient Resources";
            messege1 = "Not enough protein";
        }
        showTextBox();
        updateStats();
    }

    public void hatchingLarva(ActionEvent e) {
        if (!nest.getLarva().isEmpty()) {
            //nest.delete a larva at some point after this
            if (nest.getFood() >= 5) {
                nest.minusFood(5);
                if (nest.getProtein() >= 5) {
                    nest.minusProtein(5);
                    if (!nest.addAnt()) {
                        title = "Insufficient Ant Space";
                        messege1 = "You've hit your maximum amount";
                        messege2 = "of Ants";
                        nest.addFood(5);
                        nest.addProtein(5);
                    } else { //this is when success happens
                        //find a way to put this bellow calculation into a dif file, but right now, if I don't do it this way, the game.ants graphics won't appear for some reason...
                        for (int i = 0; i < nest.getBuildings().size(); i++) {
                            if (nest.getBuildings().get(i).getAnts().size() < nest.getBuildings().get(i).getMaxAnts()) {
                                nest.getBuildings().get(i).addAnts(new Ant());
                            }
                        }
                        title = "New Ant";
                        messege1 = "A larva as turned into an ant";
                        messege2 = "+ 1 ant";
                        nest.minusLarva();
                        reDraw();
                    }
                } else {
                    title = "Insufficient Resources";
                    messege1 = "You need at least 5 protein";
                    nest.addFood(5);
                }
            } else {
                title = "Insufficient Resources";
                messege1 = "You need at least 5 food";
            }
        } else {
            title = "Insufficient Resources";
            messege1 = "You have no larva";
            messege2 = "Ants come from larva";
        }
        showTextBox();
        updateStats();
    }










    /// hides the buttons that don't already hide.
    private void hideButtons(){
        layEggButton.relocate(-100,-100);
        layEggButton.setFocusTraversable(false);
        createAntButton.relocate(-100,-100);
        createAntButton.setFocusTraversable(false);
    }
    /// shows the buttons that hide because of the last method.
    private void showButtons(){
        layEggButton.relocate(screenX-(usw-1366), 220);
        layEggButton.setFocusTraversable(true);
        createAntButton.relocate(screenX-(usw-1345),280);
        createAntButton.setFocusTraversable(true);
    }
    /// this shows the text box, for updates to what happened. 
    private void showTextBox(){
        new Rect(245,5,220,80,Color.WHITE).draw(gc);
        new RectS(245,5,220,80,Color.BLACK).draw(gc);
        new Texts(254,24,title).draw(gc);
        new Texts(254,39, messege1).draw(gc);
        new Texts(254,54, messege2).draw(gc);
        new Texts(254,69, messege3).draw(gc);
        title = "";
        messege1 = "";
        messege2 = "";
        messege3 = "";
    }

    private void showDictionary(ActionEvent event){
        System.out.println("Dictionary Button Clicked");
    }
    /**
     * @param stage The main stage
     * @throws Exception: IDK
     */
    @Override
    public void start(Stage stage) throws Exception {
        // Create the screen and things
        Pane root = new Pane();
        stage.setTitle("The Ant Hill"); // set the window title here
        stage.setFullScreenExitKeyCombination(javafx.scene.input.KeyCombination.NO_MATCH);
        stage.initStyle(StageStyle.UNDECORATED);
        Scene scene = new Scene(root, screenX, screenY); // set the size here
        stage.setScene(scene);
        Canvas canvas = new Canvas(screenX, screenY);
        gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        gc.fillRect(0,0, screenX, screenY); //this just gives you the original black screen that we will turn into a menu screen

        // Create the GUI components
        name = new TextField("Nest Name");
        confName = new Button("confirm");
        nextDay = new Button("End Day");
        dictionaryButton = new Button("Dictionary");
        createAntButton = new Button("Hatch Larva");
        layEggButton = new Button("Lay Egg");
        alert.getButtonTypes().setAll(okButton, closeButton);

        /*map stuffs*/
        mapButton = new Button("Map");
        //noinspection unchecked
        mapSelect.getValueFactory().setValue(null); // ensuring there is no value in it.
        mapSelect.setEditable(true);
        mapSelectButton = new Button("Select");
        cancelSelection = new Button("Cancel");

        //building things
        //making the building spots
        int tempY = 225;
        for (int i = 0; i < 4; i++){
            if (tempY != 350) { //ensuring that you don't put building spots on top of the starter buildings.
                build.add(new BuildingSpot(true, 550, tempY, usx, 0)); //left side
                build.add(new BuildingSpot(false, 760, tempY, usx, 0)); //right side
            }
            tempY += 125;
        }
        //making the buildingPaths
        for (int i = 0; i < 1; i++){
            path.add(new PathSpot(720,404,usx,0));
        }
        //other building things
        buildButton = new Button("Build");
        barracksButton = new Button("Barrack");
        pathsButton = new Button("Path");
        population = new Text(-100,-100,"");
        aphids = new Text (-100,-100,"");
        food = new Text(-100,-100,"");
        protein = new Text(-100,-100,"");
        larvas= new Text();
        eggs = new Text();
        ants = new Text();
        antsInUse = new Text();
        requirements1 = new Text();
        foodStorageButton = new Button("food storage");
        requirements2 = new Text();
        proteinStorageButton = new Button("protein storage");
        requirements3 = new Text();
        aphidFarmButton = new Button("aphid farm");
        requirements4 = new Text();

        // 3. Add components to the root
        root.getChildren().addAll(canvas,food,population,protein,ants,eggs,larvas,name,confName,aphids,
                mapButton,buildButton,nextDay,requirements1,antsInUse,
                requirements2, mapSelect, mapSelectButton, cancelSelection,layEggButton,createAntButton,requirements3,
                barracksButton, pathsButton, foodStorageButton, proteinStorageButton, aphidFarmButton, requirements4, dictionaryButton);

        // 4. Configure the components (this is now done within the startGameMethod
        nextDay.relocate(-100,-100);
        nextDay.setFocusTraversable(false);
        hideButtons();
        name.relocate(usx+(usw/2)-50,usy+(ush/2));
        name.setFocusTraversable(false);
        confName.relocate(usx+(usw/2),usy+(ush/2)+50);

        /*map stuff*/
        mapButton.relocate(-100,-100);
        mapButton.setFocusTraversable(false);
        mapSelect.relocate(-100,-100);
        mapSelect.setFocusTraversable(false);
        mapSelectButton.relocate(-100,-100);
        mapSelectButton.setFocusTraversable(false);
        cancelSelection.relocate(-100,-100);
        cancelSelection.setFocusTraversable(false);

        /*build buttons*/
        buildButton.relocate(-100,-100);
        buildButton.setFocusTraversable(false);
        barracksButton.relocate(-100,-100);
        barracksButton.setFocusTraversable(false);
        pathsButton.relocate(-100,-100);
        pathsButton.setFocusTraversable(false);
        requirements1.relocate( 10, 250);
        requirements1.setFill(Color.WHITE);
        foodStorageButton.relocate(-100,-100);
        foodStorageButton.setFocusTraversable(false);
        requirements2.relocate(10, 310);
        requirements2.setFill(Color.WHITE);
        proteinStorageButton.relocate(-100,-100);
        proteinStorageButton.setFocusTraversable(false);
        requirements3.relocate(10, 370);
        requirements3.setFill(Color.WHITE);
        aphidFarmButton.relocate(-100,-100);
        aphidFarmButton.setFocusTraversable(false);
        requirements4.relocate(10, 430);
        requirements4.setFill(Color.WHITE);

        // 5. Add Event Handlers and do final setup
        nextDay.setOnAction(this::nextDayMethod);
        confName.setOnAction(this::startNewGame);
        name.addEventHandler(MouseEvent.MOUSE_PRESSED, this::nameErase);
        mapButton.setOnAction(this::drawMap);
        mapSelectButton.setOnAction(this::sending);
        cancelSelection.setOnAction(this::cancelSelectionMethod);
        layEggButton.setOnAction(this::layingEgg);
        createAntButton.setOnAction(this::hatchingLarva);
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, this::mousePressed);
        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, this::mouseMoving);
        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, this::selecting);
        buildButton.setOnAction(this::build);
        barracksButton.setOnAction(this::buildABarrack);
        pathsButton.setOnAction(this::buildAPath);
        foodStorageButton.setOnAction(this::buildAFoodStorage);
        proteinStorageButton.setOnAction(this::buildAProteinStorage);
        aphidFarmButton.setOnAction(this::buildAAphidFarm);
        mapSelect.addEventHandler(KeyEvent.KEY_PRESSED, this::sendings);
        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, this::buildHere);

        dictionaryButton.relocate(-100, -100);
        dictionaryButton.setFocusTraversable(false);
        dictionaryButton.setOnAction(this::showDictionary);
        // 7. Show the stage
        stage.show();

        // starting tutorial text
        new Texts( screenX/2-50,screenY/2 - 300,"Welcome to 'The Ant Hill!'", Color.WHITE).draw(gc);
        new Texts( screenX/2-40,screenY/2 - 285,"(Alpha version DEMO)", Color.WHITE).draw(gc);
        new Texts( screenX/2-275,screenY/2 - 260,"In this game, your goal is to create the largest, self sustaining ant colony," +
                " and survive the most amount of days.", Color.WHITE).draw(gc);
        new Texts(screenX/2-120,screenY/2 - 242,"The following tutorial will help show you how (a bit).",Color.WHITE).draw(gc);
        new Texts(screenX/2-43,screenY/2 -15,"Name your nest to begin",Color.WHITE).draw(gc);
    }


    public static void main(String[] args) {
        launch(args);
    }
}

