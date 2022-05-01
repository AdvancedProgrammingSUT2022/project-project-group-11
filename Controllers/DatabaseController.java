package Controllers;

import Model.*;
import Model.Resources.ResourceTypes;
import Model.Technologies.Technology;
import Model.Technologies.TechnologyTypes;
import Model.Units.*;

import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;

public class DatabaseController {
    private Database database;

    public DatabaseController(Database database) {
        this.database = database;
    }

    public void addUser(User user) {
        this.database.addUser(user);
    }

    public Database getDatabase() {
        return this.database;
    }

    public void setDatabase(Database database) {
        this.database = database;
    }

    public Map getMap() {

        return this.database.getMap();
    }

    public String createUser(Matcher matcher) {
        String username = matcher.group("username");
        String password = matcher.group("password");
        String nickname = matcher.group("nickname");

        ArrayList<User> users = this.database.getUsers();

        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return "user with username " + username + " already exists";
            }
            if (user.getNickname().equals(nickname)) {
                System.out.println();
                return "user with nickname " + nickname + " already exists";
            }
        }

        User newUser = new User(username, password, nickname, null);
        this.database.addUser(newUser);
        return "user created successfully!";
    }

    public User userLogin(Matcher matcher) {
        String username = matcher.group("username");
        String password = matcher.group("password");
        User user = this.database.getUserByUsernameAndPassword(username, password);
        if (user != null) {
            System.out.println("user logged in successfully!");
            return user;
        }
        System.out.println("Username and password didn't match!");
        return null;
    }

    public User getUserByUsername(String username) {
        return this.database.getUserByUsername(username);
    }

    public String changeUserNickname(Matcher matcher, User player) {
        String newNickname = matcher.group("newNickname");

        User user = database.getUserByNickname(newNickname);
        if (user != null) {
            return "user with nickname " + newNickname + " already exists";
        }
        player.setNickname(newNickname);
        return "nickname changed successfully!";
    }

    public String changePassword(Matcher matcher, User user) {
        String currentPassword = matcher.group("currentPassword");
        String newPassword = matcher.group("newPassword");

        if (!user.getPassword().equals(currentPassword)) {
            return "current password is invalid";
        }
        if (currentPassword.equals(newPassword)) {
            return "please enter a new password";
        }
        user.setPassword(newPassword);
        return "password changed successfully!";
    }

    public String selectAndDeslectCombatUnit(User user, int x, int y) {
        if (user.getCivilization().containsUnit((Unit) this.database.getMap().getTerrain()[x][y].getCombatUnit())) {
            boolean initialIsSelectedValue = this.database.getMap().getTerrain()[x][y].getCombatUnit().getIsSelected();
            this.database.getMap().getTerrain()[x][y].getCombatUnit().setIsSelected(!initialIsSelectedValue);
            return "Combat unit was selected";
        }
        return "you do not have access to this unit";
    }

    public String selectAndDeslectNonCombatUnit(User user, int x, int y) {
        if (user.getCivilization().containsUnit((Unit) this.database.getMap().getTerrain()[x][y].getNonCombatUnit())) {
            boolean initialIsSelectedValue = this.database.getMap().getTerrain()[x][y].getNonCombatUnit()
                    .getIsSelected();
            this.database.getMap().getTerrain()[x][y].getNonCombatUnit().setIsSelected(!initialIsSelectedValue);
            return "Noncombat unit was selected";
        }
        return "you do not have access to this unit";
    }

    public String changingTheStateOfACombatUnit(CombatUnit combatUnit, String action) {
        if (action.equals("sleep")) {
            combatUnit.setIsAsleep(true);
            combatUnit.setIsSelected(false);
        } else if (action.equals("alert")) {
            combatUnit.setAlert(true);
        } else if (action.equals("fortify")) {
            combatUnit.setFortify(true);
        } else if (action.equals("fortify until heal")) {
            combatUnit.setFortifyUntilHeal(true);
        } else if (action.equals("garrison")) {
            combatUnit.setIsGarrisoned(true);
        } else if (action.equals("wake")) {
            combatUnit.setIsAsleep(false);
        } else if (action.equals("delete")) {
            combatUnit = null;
        } else if (action.equals("setup ranged")) {
            if (combatUnit instanceof RangedCombatUnit) {
                RangedCombatUnit rangedCombatUnit = (RangedCombatUnit) combatUnit;
                rangedCombatUnit.setIsSetUpForRangedAttack(true);
            } else {
                return "this unit is not a ranged combat unit!";
            }
        }
        combatUnit.setIsFinished(true);
        return "action completed";
    }

    public String changingTheStateOfANonCombatUnit(NonCombatUnit nonCombatUnit, String action) {
        if (action.equals("sleep")) {
            nonCombatUnit.setIsAsleep(true);
        } else if (action.equals("wake")) {
            nonCombatUnit.setIsAsleep(false);
        } else if (action.equals("delete")) {
            nonCombatUnit = null;
        }
        nonCombatUnit.setIsFinished(true);
        return "action completed";
    }

    public String changingTheStateOfAUnit(String action) {
        CombatUnit combatUnit = getSelectedCombatUnit();
        NonCombatUnit nonCombatUnit = getSelectedNonCombatUnit();

        if (combatUnit != null) {
            return changingTheStateOfACombatUnit(combatUnit, action);
        } else {
            return changingTheStateOfANonCombatUnit(nonCombatUnit, action);
        }

    }

    public boolean HasoneUnitBeenSelected() {
        boolean isSelected = false;
        int row = this.database.getMap().getROW();
        int column = this.database.getMap().getCOL();
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < column; j++) {
                if (this.database.getMap().getTerrain()[i][j].getCombatUnit().isIsSelected() == true
                        || this.database.getMap().getTerrain()[i][j].getNonCombatUnit().isIsSelected() == true) {
                    isSelected = true;
                    break;
                }
            }
        }
        return isSelected;
    }

    public CombatUnit getSelectedCombatUnit() {
        int row = this.database.getMap().getROW();
        int column = this.database.getMap().getCOL();
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < column; j++) {
                if (this.database.getMap().getTerrain()[i][j].getCombatUnit().isIsSelected() == true) {
                    return this.database.getMap().getTerrain()[i][j].getCombatUnit();
                }
            }
        }
        return null;
    }

    public NonCombatUnit getSelectedNonCombatUnit() {
        int row = this.database.getMap().getROW();
        int column = this.database.getMap().getCOL();
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < column; j++) {
                if (this.database.getMap().getTerrain()[i][j].getNonCombatUnit().isIsSelected() == true) {
                    return this.database.getMap().getTerrain()[i][j].getNonCombatUnit();
                }
            }
        }
        return null;
    }

    public boolean isAllTasksFinished(User user) {
        for (Unit unit : user.getCivilization().getUnits()) {
            if (unit.getIsFinished() == false) {
                return false;
            }
        }
        return true;
    }

    public void setAllUnitsUnifinished(User user) {
        for (Unit unit : user.getCivilization().getUnits()) {
            if (unit.getIsAsleep() == false) {
                unit.setIsFinished(false);
            }

        }
    }

    public String unitMovement(int x_final, int y_final, User user) {
        Map map = this.getMap();
        int mapRows = map.getROW();
        int mapColumns = map.getCOL();
        if (x_final > mapRows || x_final < 0 || y_final > mapColumns || y_final < 0) {
            return "there is no tile with these coordinates";
        }
        CombatUnit combatUnit = getSelectedCombatUnit();
        NonCombatUnit nonCombatUnit = getSelectedNonCombatUnit();

        if (combatUnit != null) {
            if (user.getCivilization().containsUnit((Unit) combatUnit)) {
                return "you have another combat unit in this tile";
            }
            ArrayList<Terrain> path = new ArrayList<>();
            ArrayList<ArrayList<Terrain>> allPaths = new ArrayList<ArrayList<Terrain>>();
            addingAllPath(0, combatUnit.getX(), combatUnit.getY(), x_final, y_final, map, path, allPaths);
            combatUnit.setNextTerrain(findingTheShortestPath(allPaths));
        } else if (nonCombatUnit != null) {
            if (user.getCivilization().containsUnit((Unit) nonCombatUnit)) {
                return "you have another non combat unit in this tile";
            }
            ArrayList<Terrain> path = new ArrayList<>();
            ArrayList<ArrayList<Terrain>> allPaths = new ArrayList<ArrayList<Terrain>>();
            addingAllPath(0, nonCombatUnit.getX(), nonCombatUnit.getY(), x_final, y_final, map, path, allPaths);
            nonCombatUnit.setNextTerrain(findingTheShortestPath(allPaths));
        }

        return "action completed";
    }

    public void movementAsLongAsItHasMP(Unit unit) {
        int indexOfLastTerrain;
        int movementCost = 0;

        for (indexOfLastTerrain = 0; indexOfLastTerrain < unit.getNextTerrain().size(); indexOfLastTerrain++) {
            movementCost += unit.getNextTerrain().get(indexOfLastTerrain).getTerrainTypes().getMovementCost();
            if (movementCost > unit.getUnitType().getMovement()) {
                break;
            } else {
                Terrain terrain = findingTheContainorTerrain(unit);
                if (unit instanceof CombatUnit) {
                    terrain.setCombatUnit(null);
                    unit.getNextTerrain().get(indexOfLastTerrain).setCombatUnit((CombatUnit) unit);
                } else if (unit instanceof NonCombatUnit) {
                    terrain.setNonCombatUnit(null);
                    unit.getNextTerrain().get(indexOfLastTerrain).setNonCombatUnit((NonCombatUnit) unit);
                }

                unit.setXAndY(unit.getNextTerrain().get(indexOfLastTerrain).getX(),
                        unit.getNextTerrain().get(indexOfLastTerrain).getY());

            }
            deletingTerrainsFromListofUnitTerrains(indexOfLastTerrain, unit);
        }

    }

    public void deletingTerrainsFromListofUnitTerrains(int indexOfLastTerrain, Unit unit) {
        for (int i = 0; i < indexOfLastTerrain; i++) {
            unit.getNextTerrain().remove(unit.getNextTerrain().get(i));
        }
    }

    public Terrain findingTheContainorTerrain(Unit unit) {
        Map map = this.getMap();
        int mapRows = map.getROW();
        int mapColumns = map.getCOL();
        for (int i = 0; i < mapRows; i++) {
            for (int j = 0; j < mapColumns; j++) {
                if (map.getTerrain()[i][j].containsUnit(unit)) {
                    return map.getTerrain()[i][j];
                }
            }
        }
        return null;
    }

    public void addingAllPath(int turn, int x_beginning, int y_beginning, int x_final, int y_final,
            Map map, ArrayList<Terrain> path, ArrayList<ArrayList<Terrain>> allPaths) {
        Terrain[][] copy_map = map.getTerrain();
        if (turn == 10 || (x_beginning == x_final && y_beginning == y_final)) {
            allPaths.add(path);

        } else {
            for (int i = -1; i < 2; i++) {
                for (int j = -1; j < 2; j++) {
                    if (x_beginning + i < 0 || x_beginning + i >= map.getROW() || y_beginning + j < 0
                            || y_beginning + j >= map.getCOL()) {
                        continue;
                    } else if (y_beginning % 2 == 0) {
                        if ((i == 0 && j == 0) || (i == -1 && j == 1) || (i == -1 && j == -1)) {
                            continue;
                        }
                    } else if (y_beginning % 2 == 1) {
                        if ((i == 0 && j == 0) || (i == 1 && j == 1) || (i == 1 && j == -1)) {
                            continue;
                        }

                    } else {
                        ArrayList<Terrain> path_copy = new ArrayList<Terrain>();
                        for (Terrain terrain : path) {
                            path_copy.add(terrain);
                        }
                        path_copy.add(copy_map[x_beginning + i][y_beginning + j]);
                        if (map.hasRiver(copy_map[x_beginning][y_beginning],
                                copy_map[x_beginning + i][y_beginning + j]) != null) {
                            continue;
                        }
                        addingAllPath(
                                turn + 1, x_beginning + i, y_beginning + j, x_final, y_final, map, path_copy, allPaths);
                    }

                }
            }
        }
    }

    public ArrayList<Terrain> findingTheShortestPath(ArrayList<ArrayList<Terrain>> allPaths) {
        int movementCostOfTheShortestPath = 9999999;
        ArrayList<Terrain> shortestPath = new ArrayList<>();
        for (ArrayList<Terrain> path : allPaths) {
            if (calculatingTheMovementCost(path) < movementCostOfTheShortestPath) {
                movementCostOfTheShortestPath = calculatingTheMovementCost(path);
                shortestPath = path;
            }
        }
        return shortestPath;
    }

    public int calculatingTheMovementCost(ArrayList<Terrain> path) {
        int movementCost = 0;
        for (Terrain terrain : path) {
            movementCost += terrain.getTerrainTypes().getMovementCost();
        }
        return movementCost;
    }

    public String createUnit(User user, Matcher matcher, Terrain tile) {
        Civilization civilization = user.getCivilization();
        String unitName = matcher.group("unitName");
        int money = civilization.getGold();
        String notEnoughMoney = "You do not have enough gold to construct this unit";
        String lackTechnology = "You lack the required technology to construct this unit";
        String lackResources = "You lack the required resources to construct this unit";
        String unitAlreadyExists = "There is already a unit in this tile";
        String noCityHere = "There is no city in the tile you selected";
        if (tile.getCity() == null) {
            return noCityHere;
        }

        if (unitName.equals("ARCHER")) {
            if (money < UnitTypes.ARCHER.getCost()) {
                return notEnoughMoney;
            } else if (!civilization.getTechnologies().contains(UnitTypes.ARCHER.getTechnologyRequirements())) {
                return lackTechnology;
            } else if (tile.getCombatUnit() != null) {
                return unitAlreadyExists;
            } else {
                RangedCombatUnit newArcher = new RangedCombatUnit(tile.getX(), tile.getY(), 0, 0, 0, 0, false, false,
                        UnitTypes.ARCHER, false, false, false, false, false, false);
                civilization.setGold(money - UnitTypes.ARCHER.getCost());
                civilization.addUnit((Unit) newArcher);
                tile.setCombatUnit((CombatUnit) newArcher);
            }
        } else if (unitName.equals("CHARIOT_ARCHER")) {
            if (money < UnitTypes.CHARIOT_ARCHER.getCost()) {
                return notEnoughMoney;
            } else if (!civilization.getTechnologies().contains(UnitTypes.CHARIOT_ARCHER.getTechnologyRequirements())) {
                return lackTechnology;
            } else if (!tile.getTerrainResource().getResourceType().equals(ResourceTypes.HORSES)) {
                return lackResources;
            } else if (tile.getCombatUnit() != null) {
                return unitAlreadyExists;
            } else {
                RangedCombatUnit newChariotArcher = new RangedCombatUnit(tile.getX(), tile.getY(), 0, 0, 0, 0, false,
                        false, UnitTypes.CHARIOT_ARCHER, false, false, false, false, false, false);
                civilization.setGold(money - UnitTypes.CHARIOT_ARCHER.getCost());
                civilization.addUnit((Unit) newChariotArcher);
                tile.setCombatUnit((CombatUnit) newChariotArcher);
            }

        } else if (unitName.equals("SCOUT")) {
            if (money < UnitTypes.SCOUT.getCost()) {
                return notEnoughMoney;
            } else if (tile.getCombatUnit() != null) {
                return unitAlreadyExists;
            } else {
                NonRangedCombatUnit newScout = new NonRangedCombatUnit(tile.getX(), tile.getY(), 0, 0, 0, 0, false,
                        false, UnitTypes.SCOUT, false, false, false, false, false);
                civilization.setGold(money - UnitTypes.SCOUT.getCost());
                civilization.addUnit((Unit) newScout);
                tile.setCombatUnit((CombatUnit) newScout);
            }

        } else if (unitName.equals("SETTLER")) {
            if (money < UnitTypes.SETTLER.getCost()) {
                return notEnoughMoney;
            } else if (tile.getNonCombatUnit() != null) {
                return unitAlreadyExists;
            } else {
                NonCombatUnit newSettler = new NonCombatUnit(tile.getX(), tile.getY(), 0, 0, 0, 0, false, false,
                        UnitTypes.SETTLER, false);
                civilization.setGold(money - UnitTypes.SETTLER.getCost());
                civilization.addUnit((Unit) newSettler);
                tile.setNonCombatUnit((NonCombatUnit) newSettler);
            }

        } else if (unitName.equals("SPEARMAN")) {
            if (money < UnitTypes.SPEARMAN.getCost()) {
                return notEnoughMoney;
            } else if (tile.getCombatUnit() != null) {
                return unitAlreadyExists;
            } else {
                NonRangedCombatUnit newScout = new NonRangedCombatUnit(tile.getX(), tile.getY(), 0, 0, 0, 0, false,
                        false, UnitTypes.SPEARMAN, false, false, false, false, false);
                civilization.setGold(money - UnitTypes.SPEARMAN.getCost());
                civilization.addUnit((Unit) newScout);
                tile.setCombatUnit((CombatUnit) newScout);
            }

        } else if (unitName.equals("WARRIOR")) {
            if (money < UnitTypes.WARRIOR.getCost()) {
                return notEnoughMoney;
            } else if (tile.getCombatUnit() != null) {
                return unitAlreadyExists;
            } else {
                NonRangedCombatUnit newWarrior = new NonRangedCombatUnit(tile.getX(), tile.getY(), 0, 0, 0, 0, false,
                        false, UnitTypes.WARRIOR, false, false, false, false, false);
                civilization.setGold(money - UnitTypes.WARRIOR.getCost());
                civilization.addUnit((Unit) newWarrior);
                tile.setCombatUnit((CombatUnit) newWarrior);
            }

        } else if (unitName.equals("WORKER")) {
            if (money < UnitTypes.WORKER.getCost()) {
                return notEnoughMoney;
            } else if (tile.getNonCombatUnit() != null) {
                return unitAlreadyExists;
            } else {
                NonCombatUnit newWorker = new NonCombatUnit(tile.getX(), tile.getY(), 0, 0, 0, 0, false, false,
                        UnitTypes.WORKER, false);
                civilization.setGold(money - UnitTypes.WORKER.getCost());
                civilization.addUnit((Unit) newWorker);
                tile.setNonCombatUnit((NonCombatUnit) newWorker);
            }

        } else if (unitName.equals("CATAPULT")) {
            if (money < UnitTypes.CATAPULT.getCost()) {
                return notEnoughMoney;
            } else if (!tile.getTerrainResource().getResourceType()
                    .equals(UnitTypes.CATAPULT.getResourceRequirements())) {
                return lackResources;
            } else if (!civilization.getTechnologies().contains(UnitTypes.CATAPULT.getTechnologyRequirements())) {
                return lackTechnology;
            } else if (tile.getCombatUnit() != null) {
                return unitAlreadyExists;
            } else {
                RangedCombatUnit newCatapult = new RangedCombatUnit(tile.getX(), tile.getY(), 0, 0, 0, 0, false, false,
                        UnitTypes.CATAPULT, false, false, false, false, false, false);
                civilization.setGold(money - UnitTypes.CATAPULT.getCost());
                civilization.addUnit((Unit) newCatapult);
                tile.setCombatUnit((CombatUnit) newCatapult);
            }

        } else if (unitName.equals("HORSESMAN")) {
            if (money < UnitTypes.HORSESMAN.getCost()) {
                return notEnoughMoney;
            } else if (!tile.getTerrainResource().getResourceType()
                    .equals(UnitTypes.HORSESMAN.getResourceRequirements())) {
                return lackResources;
            } else if (!civilization.getTechnologies().contains(UnitTypes.HORSESMAN.getTechnologyRequirements())) {
                return lackTechnology;
            } else if (tile.getCombatUnit() != null) {
                return unitAlreadyExists;
            } else {
                NonRangedCombatUnit newHorsesman = new NonRangedCombatUnit(tile.getX(), tile.getY(), 0, 0, 0, 0, false,
                        false, UnitTypes.HORSESMAN, false, false, false, false, false);
                civilization.setGold(money - UnitTypes.HORSESMAN.getCost());
                civilization.addUnit((Unit) newHorsesman);
                tile.setCombatUnit((CombatUnit) newHorsesman);
            }

        } else if (unitName.equals("SWORDSMAN")) {
            if (money < UnitTypes.SWORDSMAN.getCost()) {
                return notEnoughMoney;
            } else if (!tile.getTerrainResource().getResourceType()
                    .equals(UnitTypes.SWORDSMAN.getResourceRequirements())) {
                return lackResources;
            } else if (!civilization.getTechnologies().contains(UnitTypes.SWORDSMAN.getTechnologyRequirements())) {
                return lackTechnology;
            } else if (tile.getCombatUnit() != null) {
                return unitAlreadyExists;
            } else {
                NonRangedCombatUnit newSwordsman = new NonRangedCombatUnit(tile.getX(), tile.getY(), 0, 0, 0, 0, false,
                        false, UnitTypes.SWORDSMAN, false, false, false, false, false);
                civilization.setGold(money - UnitTypes.SWORDSMAN.getCost());
                civilization.addUnit((Unit) newSwordsman);
                tile.setCombatUnit((CombatUnit) newSwordsman);
            }

        } else if (unitName.equals("CROSSBOWMAN")) {
            if (money < UnitTypes.CROSSBOWMAN.getCost()) {
                return notEnoughMoney;
            } else if (!civilization.getTechnologies().contains(UnitTypes.CROSSBOWMAN.getTechnologyRequirements())) {
                return lackTechnology;
            } else if (tile.getCombatUnit() != null) {
                return unitAlreadyExists;
            } else {
                RangedCombatUnit newCrossbowman = new RangedCombatUnit(tile.getX(), tile.getY(), 0, 0, 0, 0, false,
                        false, UnitTypes.CROSSBOWMAN, false, false, false, false, false, false);
                civilization.setGold(money - UnitTypes.CROSSBOWMAN.getCost());
                civilization.addUnit((Unit) newCrossbowman);
                tile.setCombatUnit((CombatUnit) newCrossbowman);
            }

        } else if (unitName.equals("KNIGHT")) {
            if (money < UnitTypes.HORSESMAN.getCost()) {
                return notEnoughMoney;
            } else if (!tile.getTerrainResource().getResourceType()
                    .equals(UnitTypes.KNIGHT.getResourceRequirements())) {
                return lackResources;
            } else if (!civilization.getTechnologies().contains(UnitTypes.HORSESMAN.getTechnologyRequirements())) {
                return lackTechnology;
            } else if (tile.getCombatUnit() != null) {
                return unitAlreadyExists;
            } else {
                NonRangedCombatUnit newKnight = new NonRangedCombatUnit(tile.getX(), tile.getY(), 0, 0, 0, 0, false,
                        false, UnitTypes.KNIGHT, false, false, false, false, false);
                civilization.setGold(money - UnitTypes.KNIGHT.getCost());
                civilization.addUnit((Unit) newKnight);
                tile.setCombatUnit((CombatUnit) newKnight);

            }

        } else if (unitName.equals("LONGSWORDSMAN")) {
            if (money < UnitTypes.LONGSWORDSMAN.getCost()) {
                return notEnoughMoney;
            } else if (!tile.getTerrainResource().getResourceType()
                    .equals(UnitTypes.LONGSWORDSMAN.getResourceRequirements())) {
                return lackResources;
            } else if (!civilization.getTechnologies().contains(UnitTypes.LONGSWORDSMAN.getTechnologyRequirements())) {
                return lackTechnology;
            } else if (tile.getCombatUnit() != null) {
                return unitAlreadyExists;
            } else {
                NonRangedCombatUnit newLong = new NonRangedCombatUnit(tile.getX(), tile.getY(), 0, 0, 0, 0, false,
                        false, UnitTypes.LONGSWORDSMAN, false, false, false, false, false);
                civilization.setGold(money - UnitTypes.LONGSWORDSMAN.getCost());
                civilization.addUnit((Unit) newLong);
                tile.setCombatUnit((CombatUnit) newLong);
            }

        } else if (unitName.equals("PIKEMAN")) {
            if (money < UnitTypes.PIKEMAN.getCost()) {
                return notEnoughMoney;
            } else if (!civilization.getTechnologies().contains(UnitTypes.PIKEMAN.getTechnologyRequirements())) {
                return lackTechnology;
            } else if (tile.getCombatUnit() != null) {
                return unitAlreadyExists;
            } else {
                NonRangedCombatUnit newPikeman = new NonRangedCombatUnit(tile.getX(), tile.getY(), 0, 0, 0, 0, false,
                        false, UnitTypes.PIKEMAN, false, false, false, false, false);
                civilization.setGold(money - UnitTypes.PIKEMAN.getCost());
                civilization.addUnit((Unit) newPikeman);
                tile.setCombatUnit((CombatUnit) newPikeman);
            }

        } else if (unitName.equals("TREBUCHET")) {
            if (money < UnitTypes.TREBUCHET.getCost()) {
                return notEnoughMoney;
            } else if (!tile.getTerrainResource().getResourceType()
                    .equals(UnitTypes.TREBUCHET.getResourceRequirements())) {
                return lackResources;
            } else if (!civilization.getTechnologies().contains(UnitTypes.TREBUCHET.getTechnologyRequirements())) {
                return lackTechnology;
            } else if (tile.getCombatUnit() != null) {
                return unitAlreadyExists;
            } else {
                RangedCombatUnit newTrebuchet = new RangedCombatUnit(tile.getX(), tile.getY(), 0, 0, 0, 0, false, false,
                        UnitTypes.TREBUCHET, false, false, false, false, false, false);
                civilization.setGold(money - UnitTypes.TREBUCHET.getCost());
                civilization.addUnit((Unit) newTrebuchet);
                tile.setCombatUnit((CombatUnit) newTrebuchet);

            }

        } else if (unitName.equals("CANNON")) {
            if (money < UnitTypes.CANNON.getCost()) {
                return notEnoughMoney;
            } else if (!civilization.getTechnologies().contains(UnitTypes.CANNON.getTechnologyRequirements())) {
                return lackTechnology;
            } else if (tile.getCombatUnit() != null) {
                return unitAlreadyExists;
            } else {
                RangedCombatUnit newCannon = new RangedCombatUnit(tile.getX(), tile.getY(), 0, 0, 0, 0, false, false,
                        UnitTypes.CANNON, false, false, false, false, false, false);
                civilization.setGold(money - UnitTypes.CANNON.getCost());
                civilization.addUnit((Unit) newCannon);
                tile.setCombatUnit((CombatUnit) newCannon);

            }

        } else if (unitName.equals("CAVALRY")) {
            if (money < UnitTypes.CAVALRY.getCost()) {
                return notEnoughMoney;
            } else if (!tile.getTerrainResource().getResourceType()
                    .equals(UnitTypes.CAVALRY.getResourceRequirements())) {
                return lackResources;
            } else if (!civilization.getTechnologies().contains(UnitTypes.CAVALRY.getTechnologyRequirements())) {
                return lackTechnology;
            } else if (tile.getCombatUnit() != null) {
                return unitAlreadyExists;
            } else {
                NonRangedCombatUnit newUnit = new NonRangedCombatUnit(tile.getX(), tile.getY(), 0, 0, 0, 0, false,
                        false, UnitTypes.CAVALRY, false, false, false, false, false);
                civilization.setGold(money - UnitTypes.CAVALRY.getCost());
                civilization.addUnit((Unit) newUnit);
                tile.setCombatUnit((CombatUnit) newUnit);

            }

        } else if (unitName.equals("LANCER")) {
            if (money < UnitTypes.LANCER.getCost()) {
                return notEnoughMoney;
            } else if (!tile.getTerrainResource().getResourceType()
                    .equals(UnitTypes.LANCER.getResourceRequirements())) {
                return lackResources;
            } else if (!civilization.getTechnologies().contains(UnitTypes.LANCER.getTechnologyRequirements())) {
                return lackTechnology;
            } else if (tile.getCombatUnit() != null) {
                return unitAlreadyExists;
            } else {
                NonRangedCombatUnit newUnit = new NonRangedCombatUnit(tile.getX(), tile.getY(), 0, 0, 0, 0, false,
                        false, UnitTypes.LANCER, false, false, false, false, false);
                civilization.setGold(money - UnitTypes.LANCER.getCost());
                civilization.addUnit((Unit) newUnit);
                tile.setCombatUnit((CombatUnit) newUnit);
            }

        } else if (unitName.equals("MUSKETMAN")) {
            if (money < UnitTypes.MUSKETMAN.getCost()) {
                return notEnoughMoney;
            } else if (!civilization.getTechnologies().contains(UnitTypes.MUSKETMAN.getTechnologyRequirements())) {
                return lackTechnology;
            } else if (tile.getCombatUnit() != null) {
                return unitAlreadyExists;
            } else {
                NonRangedCombatUnit newUnit = new NonRangedCombatUnit(tile.getX(), tile.getY(), 0, 0, 0, 0, false,
                        false, UnitTypes.MUSKETMAN, false, false, false, false, false);
                civilization.setGold(money - UnitTypes.MUSKETMAN.getCost());
                civilization.addUnit((Unit) newUnit);
                tile.setCombatUnit((CombatUnit) newUnit);
            }

        } else if (unitName.equals("RIFLEMAN")) {
            if (money < UnitTypes.RIFLEMAN.getCost()) {
                return notEnoughMoney;
            } else if (!civilization.getTechnologies().contains(UnitTypes.RIFLEMAN.getTechnologyRequirements())) {
                return lackTechnology;
            } else if (tile.getCombatUnit() != null) {
                return unitAlreadyExists;
            } else {
                NonRangedCombatUnit newUnit = new NonRangedCombatUnit(tile.getX(), tile.getY(), 0, 0, 0, 0, false,
                        false, UnitTypes.RIFLEMAN, false, false, false, false, false);
                civilization.setGold(money - UnitTypes.RIFLEMAN.getCost());
                civilization.addUnit((Unit) newUnit);
                tile.setCombatUnit((CombatUnit) newUnit);

            }

        } else if (unitName.equals("ANTI_TANKGUN")) {
            if (money < UnitTypes.ANTI_TANKGUN.getCost()) {
                return notEnoughMoney;
            } else if (!civilization.getTechnologies().contains(UnitTypes.ANTI_TANKGUN.getTechnologyRequirements())) {
                return lackTechnology;
            } else if (tile.getCombatUnit() != null) {
                return unitAlreadyExists;
            } else {
                NonRangedCombatUnit newUnit = new NonRangedCombatUnit(tile.getX(), tile.getY(), 0, 0, 0, 0, false,
                        false, UnitTypes.ANTI_TANKGUN, false, false, false, false, false);
                civilization.setGold(money - UnitTypes.ANTI_TANKGUN.getCost());
                civilization.addUnit((Unit) newUnit);
                tile.setCombatUnit((CombatUnit) newUnit);
            }

        } else if (unitName.equals("ARTILLERY")) {
            if (money < UnitTypes.ARTILLERY.getCost()) {
                return notEnoughMoney;
            } else if (!civilization.getTechnologies().contains(UnitTypes.ARTILLERY.getTechnologyRequirements())) {
                return lackTechnology;
            } else if (tile.getCombatUnit() != null) {
                return unitAlreadyExists;
            } else {
                RangedCombatUnit newUnit = new RangedCombatUnit(tile.getX(), tile.getY(), 0, 0, 0, 0, false, false,
                        UnitTypes.ARTILLERY, false, false, false, false, false, false);
                civilization.setGold(money - UnitTypes.ARTILLERY.getCost());
                civilization.addUnit((Unit) newUnit);
                tile.setCombatUnit((CombatUnit) newUnit);
            }

        } else if (unitName.equals("INFANTRY")) {
            if (money < UnitTypes.INFANTRY.getCost()) {
                return notEnoughMoney;
            } else if (!civilization.getTechnologies().contains(UnitTypes.INFANTRY.getTechnologyRequirements())) {
                return lackTechnology;
            } else if (tile.getCombatUnit() != null) {
                return unitAlreadyExists;
            } else {
                NonRangedCombatUnit newUnit = new NonRangedCombatUnit(tile.getX(), tile.getY(), 0, 0, 0, 0, false,
                        false, UnitTypes.INFANTRY, false, false, false, false, false);
                civilization.setGold(money - UnitTypes.INFANTRY.getCost());
                civilization.addUnit((Unit) newUnit);
                tile.setCombatUnit((CombatUnit) newUnit);
            }

        } else if (unitName.equals("PANZER")) {
            if (money < UnitTypes.PANZER.getCost()) {
                return notEnoughMoney;
            } else if (!civilization.getTechnologies().contains(UnitTypes.PANZER.getTechnologyRequirements())) {
                return lackTechnology;
            } else if (tile.getCombatUnit() != null) {
                return unitAlreadyExists;
            } else {
                NonRangedCombatUnit newUnit = new NonRangedCombatUnit(tile.getX(), tile.getY(), 0, 0, 0, 0, false,
                        false, UnitTypes.PANZER, false, false, false, false, false);
                civilization.setGold(money - UnitTypes.PANZER.getCost());
                civilization.addUnit((Unit) newUnit);
                tile.setCombatUnit((CombatUnit) newUnit);
            }

        } else if (unitName.equals("TANK")) {
            if (money < UnitTypes.TANK.getCost()) {
                return notEnoughMoney;
            } else if (!civilization.getTechnologies().contains(UnitTypes.TANK.getTechnologyRequirements())) {
                return lackTechnology;
            } else if (tile.getCombatUnit() != null) {
                return unitAlreadyExists;
            } else {
                NonRangedCombatUnit newUnit = new NonRangedCombatUnit(tile.getX(), tile.getY(), 0, 0, 0, 0, false,
                        false, UnitTypes.TANK, false, false, false, false, false);
                civilization.setGold(money - UnitTypes.TANK.getCost());
                civilization.addUnit((Unit) newUnit);
                tile.setCombatUnit((CombatUnit) newUnit);
            }

        }
        return "invalid unit name";
    }

    public void changingUnitsParameters(User user) {
        for (Unit unit : user.getCivilization().getUnits()) {
            if (unit instanceof CombatUnit) {
                changingCombatUnitsParameters((CombatUnit) unit);
            } else {
                changingNonCombatUnitParameters((NonCombatUnit) unit);
            }
        }
    }

    public void changingCombatUnitsParameters(CombatUnit combatUnit) {
        if (combatUnit.getIsAsleep()) {

        } else if (combatUnit.getAlert()) {

        } else if (combatUnit.getIsGarrisoned()) {

        } else if (combatUnit.getFortify()) {

        } else if (combatUnit.getFortifyUntilHeal()) {

        }
    }

    public void changingNonCombatUnitParameters(NonCombatUnit nonCombatUnit) {
        if (nonCombatUnit.getIsAsleep()) {

        }
    }

    public String choosingATechnologyToStudy(User user, TechnologyTypes technologyType) {
        for (TechnologyTypes technologyType2 : technologyType.getRequirements()) {
            if (!isContainTechnology(user, technologyType2)) {
                return "you do not have required prerequisites";
            } else if (isContainTechnology(user, technologyType2)
                    && getTechnologyByTechnologyType(user, technologyType).getIsAvailabe() == true) {
                return "you do not have required prerequisites";
            }
        }
        for (Technology technology : user.getCivilization().getTechnologies()) {
            technology.setUnderResearch(false);
        }
        user.getCivilization().getTechnologies().add(new Technology(true, 0, technologyType, false));
        return "Technology is under research";
    }

    public boolean isContainTechnology(User user, TechnologyTypes technologyType) {
        for (Technology technology : user.getCivilization().getTechnologies()) {
            if (technology.getTechnologyType().equals(technologyType)) {
                return true;
            }
        }
        return false;
    }

    public Technology getTechnologyByTechnologyType(User user, TechnologyTypes technologyType) {
        for (Technology technology : user.getCivilization().getTechnologies()) {
            if (technology.getTechnologyType().equals(technologyType)) {
                return technology;
            }
        }
        return null;
    }

    public String researchInfo(User user) {
        return getUnderResearchTechnology(user).toString();
    }

    public String unitsInfo(User user) {
        StringBuilder unitsInformation = new StringBuilder();
        for (Unit unit : user.getCivilization().getUnits()) {
            unitsInformation.append(unit.toString());
        }
        return unitsInformation.toString();
    }

    public Technology getUnderResearchTechnology(User user) {
        for (Technology technology : user.getCivilization().getTechnologies()) {
            if (technology.getUnderResearch()) {
                return technology;
            }
        }
        return null;
    }

    public Terrain getTerrainByCoordinates(int x, int y) {
        return this.database.getMap().getTerrain()[x][y];
    }

    public void setTerrainsOfEachCivilization(User user) {

        ArrayList<Terrain> terrains = new ArrayList<>();

        for (Unit unit : user.getCivilization().getUnits()) {
            terrains.add(getTerrainByCoordinates(unit.getX(), unit.getY()));
        }

        // todo add cities tiles

        user.getCivilization().setTerrains(terrains);
    }

    public void setCivilizations(ArrayList<User> users) {

        setCivilizationsName();
        ArrayList<Integer> indeces = setIndeces(users);
        int i = 0;
        for (User user : users) {

            Civilization civilization = new Civilization(null, null, null, 10000, 100, null,
                    this.database.getCivilizationsName().get(indeces.get(i)));
            user.setCivilization(civilization);
            createUnitForEachCivilization(user);
            setTerrainsOfEachCivilization(user);
            i++;
        }
    }

    public void setCivilizationsName() {
        this.database.getCivilizationsName().add("Incan");
        this.database.getCivilizationsName().add("Aztec");
        this.database.getCivilizationsName().add("Roman");
        this.database.getCivilizationsName().add("Ancient Greek");
        this.database.getCivilizationsName().add("Chinese");
        this.database.getCivilizationsName().add("Maya");
        this.database.getCivilizationsName().add("Ancient Egyptian");
        this.database.getCivilizationsName().add("Indus Valley");
        this.database.getCivilizationsName().add("Mesopotamian");
    }

    public ArrayList<Integer> setIndeces(ArrayList<User> users) {
        Random rand = new Random();
        ArrayList<Integer> indeces = new ArrayList<>();
        for (int i = 0; i < users.size(); i++) {
            int nextIndex = rand.nextInt(10);
            while (isConatainInteger(indeces, nextIndex)) {
                nextIndex = rand.nextInt();
            }

            indeces.add(nextIndex);
        }

        return indeces;

    }

    public boolean isConatainInteger(ArrayList<Integer> indeces, int random) {
        for (Integer integer : indeces) {
            if (integer.intValue() == random) {

                return true;
            }
        }
        return false;
    }

    public void createUnitForEachCivilization(User user) {
        ArrayList<Integer> unitsCoordinates = findingEmptyTiles();
        NonCombatUnit newSettler = new NonCombatUnit(unitsCoordinates.get(0), unitsCoordinates.get(1) + 1, 0, 0, 0, 0,
                false, false,
                UnitTypes.SETTLER, false);
        NonRangedCombatUnit newWarrior = new NonRangedCombatUnit(unitsCoordinates.get(0), unitsCoordinates.get(1) + 1,
                0, 0, 0, 0, false,
                false, UnitTypes.WARRIOR, false, false, false, false, false);
        getMap().getTerrain()[unitsCoordinates.get(0)][unitsCoordinates.get(1)].setCombatUnit(newWarrior);
        getMap().getTerrain()[unitsCoordinates.get(0)][unitsCoordinates.get(1) + 1].setNonCombatUnit(newSettler);
        user.getCivilization().getUnits().add((Unit) newSettler);
        user.getCivilization().getUnits().add((Unit) newWarrior);

    }

    public ArrayList<Integer> findingEmptyTiles() {
        Random rand = new Random();
        ArrayList<Integer> coordinates = new ArrayList<>();
        int x = rand.nextInt(5, 25);
        int y = rand.nextInt(5, 13);
        while (!isTerrainEmpty(x, y)) {
            x = rand.nextInt(5, 25);
            y = rand.nextInt(5, 13);
        }
        coordinates.add(x);
        coordinates.add(y);
        return coordinates;

    }

    public boolean isTerrainEmpty(int x, int y) {

        if (this.getMap().getTerrain()[x][y].getCombatUnit() != null
                || this.getMap().getTerrain()[x][y + 1].getNonCombatUnit() != null) {
            return false;
        }
        return true;
    }

}