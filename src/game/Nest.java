package game;

import game.ants.IAnt;
import game.buildings.Building;
import javafx.scene.paint.Color;
import game.shapes.Circle;
import game.shapes.Rect;
import game.shapes.Shape;

import java.util.ArrayList;

public class Nest {
    private boolean cheatMode = false; //gets updated on AntGameView

    //more complex stats (not just numbers/integers)
    private String name;
    private ArrayList<Building> buildings = new ArrayList<Building>();
    private ArrayList<IAnt> ants = new ArrayList<IAnt>();
    private ArrayList<Eggs> eggs = new ArrayList<Eggs>();
    private ArrayList<Larva> larva = new ArrayList<Larva>();
    //basic stats
    private int antsInUse = 0; //increase this, but it cant get higher than you amount of ants (ant.size())
    private int population; //stores the population of everything that eats food(ants, and larva)
    private int maxAphids = 0;
    private int aphids = 0;
    private int food; //carbohydrates for feeding ants (mostly)
    private int protein; //protein for creating new ants (mostly)
    private int maxFood;
    private int maxProtein;
    private int maxAnts;
    private int maxEggs;
    private int maxLarva;
    private int days = 1;
    private double foodGeneration = 0; //(from aphids, each aphids gives 0.2 per a turn)




    // making a new Nest (starting the game)
    public Nest(String name){
        this.name = name;
    }


    //overrides calculate food (not any other calculate)
    public void cheatMode() {
        this.food = 100000;
        this.aphids = 10000;
        this.protein = 10000;
        this.cheatMode = true;
    }


    //calculating resources and such (constantly re-occurring methods)
    public void calcPopulation(){ // calculates the total of the population stats, and adds to the ant lists
        population = 0;
        ants.clear();
        eggs.clear();
        larva.clear();
        for(int i = 0; i < buildings.size(); i++){
            this.ants.addAll(buildings.get(i).getAnts());
            this.eggs.addAll(buildings.get(i).getEggs());
            this.larva.addAll(buildings.get(i).getLarva());
            this.population += buildings.get(i).getPopulation();
        }
    }

    public void calcFood(){
        if (!cheatMode) {
            this.aphids = 0;
            this.food = 0;
            this.protein = 0;
            this.maxFood = 0;
            this.maxProtein = 0;
            this.maxAphids = 0;
            for (int i = 0; i < buildings.size(); i++) {
                this.aphids += buildings.get(i).getAphids();
                this.food += buildings.get(i).getFood();
                this.protein += buildings.get(i).getProtein();
                this.maxProtein += buildings.get(i).getMaxProtein();
                this.maxFood += buildings.get(i).getMaxFood();
                this.maxAphids += buildings.get(i).getMaxAphids();
            }
        }
    }
    public void calcMaxAnts(){
        this.maxAnts = 0;
        this.maxLarva = 0;
        this.maxEggs = 0;
        for (int i = 0; i < buildings.size(); i++) {
            this.maxEggs += buildings.get(i).getMaxEggs();
            this.maxLarva += buildings.get(i).getMaxLarva();
            this.maxAnts += buildings.get(i).getMaxAnts();
        }
    }

    //get methods
    public String getName(){
        return this.name;
    }
    public ArrayList<Building> getBuildings(){
        return this.buildings;
    }
    public ArrayList<IAnt> getAnts(){
        calcPopulation();
        return this.ants;
    }
    public ArrayList<Eggs> getEggs(){
        calcPopulation();
        return this.eggs;
    }
    public ArrayList<Larva> getLarva(){
        calcPopulation();
        return this.larva;
    }
    public int getPopulation(){
        calcPopulation();
        return population;
    }
    public int getFood(){
        calcFood();
        return food;
    }
    public int getProtein(){
        calcFood();
        return protein;
    }
    public int getMaxFood(){
        return maxFood;
    }
    public int getMaxProtein(){
        return maxProtein;
    }
    public int getMaxAnts(){
        return maxAnts;
    }
    public int getMaxEggs(){
        return maxEggs;
    }
    public int getMaxLarva(){
        return maxLarva;
    }
    public int getAntsInUse() {
        return antsInUse;
    }
    public int getDays() {
        return days;
    }
    public int getAphids() {
        calcFood();
        return this.aphids;
    }
    public int getMaxAphids(){
        return this.maxAphids;
    }


    // setters/adders/next day function? why did I put that there.
    public int nextDay(){ //next day also carries out the 'add larva' function that would have been created, but is instead integrated here.
        this.days++;
        int numEggsHatched = 0;
        //this is scanning through each building
        for (int j = 0; j < buildings.size(); j++) {
            int numEggsHatching = 0;
            for (int i = 0; i < buildings.get(j).getEggs().size(); i++) { //this will only run if the building contains eggs
                //for each egg, check if it can turn into a larva;
                if (buildings.get(j).getEggs().get(i).getDaysAlive() >= 2) {
                    if (larva.size() != maxLarva) {
                        numEggsHatching++;
                        buildings.get(j).addLarva(new Larva());
                    }
                } else {
                    buildings.get(j).getEggs().get(i).addDaysAlive();
                }
            }
            buildings.get(j).deleteEgg(numEggsHatching);
            numEggsHatched += numEggsHatching;
        }
        return numEggsHatched;
    }
    public void addBuilding(Building building){
        buildings.add(building);
        //gathering the population and stats
        calcAll();
    }
    public void calcAll() {
        calcPopulation();
        calcFood();
        calcMaxAnts();
    }
    public boolean AddAntsInUse(int num){
        if (this.antsInUse + num <= ants.size() - 1){
            antsInUse += num;
            return true;
        } else {
            return false;
        }
    }

    public void clearAntsInUse(){
        this.antsInUse = 0;
    }
    public void minusAntsInUse(int num){
        this.antsInUse -= num;
    }
    public boolean minusAphids(int num){
        if (aphids - num < 0 ){
            return false;
        } else {
            int numLeft = num;
            for (int i = 0; i < buildings.size(); i++) {
                if (numLeft != 0) {
                    if (buildings.get(i).getAphids() >= 1) {
                        if (buildings.get(i).getAphids() >= numLeft) {
                            buildings.get(i).minusAphids(numLeft);
//                            break;
                        } else {
                            buildings.get(i).minusAphids(buildings.get(i).getAphids());
                            numLeft -= buildings.get(i).getAphids();
                        }
                    }
                }
            }
            calcFood();
            return true;
        }
    }
    public boolean minusFood(int num){
        if (food - num < 0 ){
            return false;
        } else {
            int numLeft = num;
            for (int i = 0; i < buildings.size(); i++) {
                if (numLeft != 0) {
                    if (buildings.get(i).getFood() >= 1) {
                        if (buildings.get(i).getFood() >= numLeft) {
                            buildings.get(i).minusFood(numLeft);
//                            break;
                        } else {
                            buildings.get(i).minusFood(buildings.get(i).getFood());
                            numLeft -= buildings.get(i).getFood();
                        }
                    }
                }
            }
            calcFood();
            return true;
        }
    }
    public boolean minusProtein(int num){
        if (protein - num < 0 ){
            return false; //this isn't perfect modify it for use with the next day thingy
        } else {
            int numLeft = num;
            for (int i = 0; i < buildings.size(); i++) {
                if (numLeft != 0) {
                    if (buildings.get(i).getProtein() >= 1) {
                        if (buildings.get(i).getProtein() >= numLeft) {
                            buildings.get(i).minusProtein(numLeft);
//                            break;
                        } else {
                            buildings.get(i).minusProtein(buildings.get(i).getProtein());
                            numLeft -= buildings.get(i).getProtein();
                        }
                    }
                }
            }
            calcFood();
            return true;
        }
    }
    public boolean minusAnts(int num) {
        if (ants.size() - 1 - num <= 0) {
            for (int i = 1; i < buildings.size(); i++){
                buildings.get(i).deleteAnt(buildings.get(i).getAnts().size());
            }
            return false; //this isn't perfect modify it for use with the next day thingy // cant remember what this meant, awesome.
        } else {
            int numLeft = num;
            for (int i = 1; i < buildings.size(); i++) {
                if (numLeft > 0) {
                    if (!buildings.get(i).getAnts().isEmpty()) {
                        if (buildings.get(i).getAnts().size() >= numLeft) {
                            buildings.get(i).deleteAnt(numLeft);
//                            break;
                        } else {
                            buildings.get(i).deleteAnt(buildings.get(i).getAnts().size());
                            numLeft -= buildings.get(i).getAnts().size();
                        }
                    }
                }
            }
            calcAll();
            return true;
        }
    }
    //for when we remove the oldest larva because he is being made into an ant.
    public void minusLarva(){
        for (int i = 0; i < buildings.size(); i++){
            if (!buildings.get(i).getLarva().isEmpty()) { //this is basically asking 'as long as the building isn't empty of larva'
                buildings.get(i).getLarva().removeFirst();
            }
        }
    }
    //for when we are removing the youngest larva, because we don't have enough food.
    public void minusLarva(int num){
        for (int j = 0; j < num; j ++) {
            try {
                for (int i = 0; i < buildings.size(); i++) {
                    if (!buildings.get(i).getLarva().isEmpty()) { //this is basically asking 'as long as the building isn't empty of larva'
                        buildings.get(i).getLarva().removeLast();
                    }
                }
            } catch (Exception e){
                j = num;
            }
        }
    }
    public int addProtein(int num){
        int numLeft = num;
        for (int i = 0; i < buildings.size(); i++) {
            int max = buildings.get(i).getMaxProtein() - buildings.get(i).getProtein();
            if (numLeft <= max){
                buildings.get(i).addProtein(numLeft);
                numLeft = 0;
            } else {
                buildings.get(i).addProtein(max);
                numLeft -= max;
            }
        }
        return numLeft;
    }
    public int addFood(int num){
        int numLeft = num;
        for (int i = 0; i < buildings.size(); i++) {
            int max = buildings.get(i).getMaxFood() - buildings.get(i).getFood(); //max amount more that a building can take
            if (numLeft <= max){
                buildings.get(i).addFood(numLeft);
                numLeft = 0;
            } else {
                buildings.get(i).addFood(max);
                numLeft -= max;
            }
        }
        return numLeft;
    }
    public int addAphids(int num){
        int numLeft = num;
        for (int i = 0; i < buildings.size(); i++) {
            int max = buildings.get(i).getMaxAphids() - buildings.get(i).getAphids(); //max amount more that a building can take
            if (numLeft <= max){
                buildings.get(i).addAphids(numLeft);
                numLeft = 0;
            } else {
                buildings.get(i).addAphids(max);
                numLeft -= max;
            }
        }
        return numLeft;
    }
    public Boolean addEgg(){
        for (int i = 0; i < buildings.size(); i++) {
            if (buildings.get(i).getEggs().size() < buildings.get(i).getMaxEggs()){
                buildings.get(i).addEggs(new Eggs());
                return true;
            }
        }
        return false;
    }
    public Boolean addAnt(){
        for (int i = 0; i < buildings.size(); i++) {
            if (buildings.get(i).getAnts().size() < buildings.get(i).getMaxAnts()){
                //game.buildings.get(i).addAnts(new game.ants.Ants()); //for some reason, doing this doesn't allow graphics for game.ants to work, so just use this function to check if the main can add another ant... until we can fix it.
                return true;
            }
        }
        return false;
    }

    public ArrayList<Shape> Graphics(double usx, double usy){
        ArrayList<Shape> graphics = new ArrayList<Shape>();
        if (buildings.size() <= 3){
            graphics.add(new Circle(usx+700,usy+193,30,Color.rgb(61, 35, 13)));
            graphics.add(new Rect(usx+720,usy+193,20,211,Color.rgb(186, 155, 74)));
            graphics.add(new Rect(usx+700,usy+185,60,10,Color.rgb(135, 206, 235)));
        } else if (buildings.size() <= 5){
            graphics.add(new Circle(usx+697,usy+186,65,85,Color.rgb(61, 35, 13)));
            graphics.add(new Rect(usx+720,usy+183,20,221,Color.rgb(186, 155, 74)));
            graphics.add(new Rect(usx+700,usy+180,60,10,Color.rgb(135, 206, 235)));
        } else {
            graphics.add(new Circle(usx+697,usy+186,65,85,Color.rgb(61, 35, 13)));
            graphics.add(new Rect(usx+720,usy+183,20,221,Color.rgb(186, 155, 74)));
            graphics.add(new Rect(usx+700,usy+180,60,10,Color.rgb(135, 206, 235)));
        }
        return graphics;
    }

    /**
     * calculates how much food is generated by the aphid farms, and returns that number after also adding it to total food.
     * @return the amount of food that you've gained, (to be printed to the user)
     */
    public int aphidFoodGeneration(){
        int foodReturning = 0;
        for(int i = aphids; i > 0; i--){
            foodGeneration += 0.2;
            if (foodGeneration == 1){
                foodGeneration = 0;
                foodReturning++;
            }
        }
        addFood(foodReturning);
        return foodReturning;
    }


    @Override
    public String toString() {
        return "Nest{" +
                "name='" + name + '\'' +
                ", buildings=" + buildings +
                ", ants=" + ants +
                ", eggs=" + eggs +
                ", larva=" + larva +
                ", population=" + population +
                ", food=" + food +
                ", protein=" + protein +
                ", maxFood=" + maxFood +
                ", maxProtein=" + maxProtein +
                ", maxAnts=" + maxAnts +
                ", maxEggs=" + maxEggs +
                ", maxLarva=" + maxLarva +
                ", daysAlive=" + days +
                '}';
    }
}
