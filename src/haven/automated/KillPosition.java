package haven.automated;

import haven.*;

import java.awt.*;
import java.util.List;
import java.util.*;

public class KillPosition implements Runnable {
    private GameUI gui;
    private Gob playerGob;
    private Gob selectedGob;
    private Coord2d gobcoords, playercoords;
    private double dist = 40, distance, ratiox, ratioy, distx, disty, defdist = 19.45, lookArea = 150, coordx, coordy;
    private List<String> vehicles = new ArrayList<>(Arrays.asList("gfx/terobjs/vehicle/rowboat",
            "gfx/terobjs/vehicle/dugout", "gfx/terobjs/vehicle/snekkja", "gfx/terobjs/vehicle/knarr"));
    private Map<String, Double> vehicle = new HashMap<>();
    private List<String> animals = new ArrayList<>(Arrays.asList("gfx/kritter/bear/bear", "gfx/kritter/moose/moose",
            "gfx/kritter/lynx/lynx", "gfx/kritter/badger/badger", "gfx/kritter/fox/fox", "gfx/kritter/wolf/wolf",
            "gfx/kritter/horse/horse", "gfx/kritter/adder/adder", "gfx/kritter/orca/orca",
            "gfx/kritter/cavelouse/cavelouse", "gfx/kritter/mammoth/mammoth", "gfx/kritter/ant/ant",
            "gfx/kritter/goat/wildgoat",
            "gfx/kritter/caveangler/caveangler", "gfx/kritter/wolverine/wolverine", "gfx/kritter/cattle/cattle",
            "gfx/kritter/spermwhale/spermwhale", "gfx/kritter/boar/boar", "gfx/kritter/sheep/sheep",
            "gfx/kritter/reddeer/reddeer"));
    private Map<String, Double> animal = new HashMap<>();

    public KillPosition(GameUI gui) {
        this.gui = gui;
        vehicle.put("gfx/terobjs/vehicle/rowboat", 13.3);
        vehicle.put("gfx/terobjs/vehicle/dugout", 7.4);
        vehicle.put("gfx/terobjs/vehicle/snekkja", 29.35);
        vehicle.put("gfx/terobjs/vehicle/knarr", 54.5);
        vehicle.put("gfx/kritter/horse/stallion", 5.4);
        vehicle.put("gfx/kritter/horse/mare", 5.4);
        animal.put("gfx/kritter/bear/bear", 5.7);
        animal.put("gfx/kritter/moose/moose", 6.0);
        animal.put("gfx/kritter/lynx/lynx", 1.0);
        animal.put("gfx/kritter/badger/badger", 0.9);
        animal.put("gfx/kritter/fox/fox", -0.9);
        animal.put("gfx/kritter/wolf/wolf", 6.0);
        animal.put("gfx/kritter/horse/horse", 4.0);
        animal.put("gfx/kritter/adder/adder", -1.9);
        animal.put("gfx/kritter/wolverine/wolverine", 2.0);
        animal.put("gfx/kritter/spermwhale/spermwhale", 93.2);
        animal.put("gfx/kritter/sheep/sheep", 20.0);
        animal.put("gfx/kritter/reddeer/reddeer", 6.0);
        animal.put("gfx/kritter/cattle/cattle", 8.0);
        animal.put("gfx/kritter/goat/wildgoat", -0.1);
        animal.put("gfx/kritter/ant/ant", -3.8);
        animal.put("gfx/kritter/mammoth/mammoth", 11.3);
        animal.put("gfx/kritter/caveangler/caveangler", 8.2);
        animal.put("gfx/kritter/cavelouse/cavelouse", 3.0);
        animal.put("gfx/kritter/orca/orca", 30.25);
        animal.put("gfx/kritter/boar/boar", 6.1);
        animal.put("gfx/borka/body", 8.0);
    }

    @Override
    public void run() {
        try {
            playerGob = gui.map.player();
            selectedGob = KillPosition.findGobByNamesList(gui.ui, lookArea, animals);
            if (selectedGob == null) {
                KillPosition.sysMsg(gui.ui, "No mob found to fight.", Color.WHITE);
                return;
            }
            gobcoords = selectedGob.rc;
            playercoords = playerGob.rc;
            distance = Math.sqrt(Math.pow(playercoords.x - gobcoords.x, 2) + Math.pow(playercoords.y - gobcoords.y, 2));
            ratiox = distance / (playercoords.x - gobcoords.x);
            ratioy = distance / (playercoords.y - gobcoords.y);
            dist = defdist + vehicleMod() + animalMod();
            distx = dist / ratiox;
            disty = dist / ratioy;
            coordx = (gobcoords.x + distx);
            coordy = (gobcoords.y + disty);
            KillPosition.mapClick(gui.ui, coordx, coordy, 1, 0);
        } catch (Exception ex) {
        } // ignore, NPE here means there isnt something in range or something.
    }

    double animalMod() {
        Gob gob = KillPosition.findGobByNamesList(gui.ui, lookArea, animals);
        if (gob != null)
            return animal.get(gob.getres().name);
        else
            return 0;
    }

    double vehicleMod() {
        Gob gob = KillPosition.findGobByNamesList(gui.ui, 1, vehicles);
        if (gob != null)
            return vehicle.get(gob.getres().name);
        else
            return 0;
    }

    public static Gob findGobByNamesList(UI ui, double radius, List<String> names) {
        Coord2d plc = ui.gui.map.player().rc;
        double min = radius;
        Gob nearest = null;
        for (Gob gob : ui.sess.glob.oc.getallgobs()) {
            double dist = gob.rc.dist(plc);
            if (dist < min) {
                boolean matches = false;
                try {
                    for (String p : names) {
                        if (gob.getres() != null && p.equals(gob.getres().name)) {
                            matches = true;
                            break;
                        }
                    }
                } catch (Loading l) {
                }
                if (matches) {
                    min = dist;
                    nearest = gob;
                }
            }
        }
        if (nearest == null)
            return null;
        else
            return nearest;
    }

    public static void mapClick(UI ui, int x, int y, int btn, int mod) {
        ui.gui.map.wdgmsg("click", getCenterScreenCoord(ui), new Coord2d(x, y).floor(OCache.posres), btn, mod);
    }

    public static void mapClick(UI ui, double x, double y, int btn, int mod) {
        ui.gui.map.wdgmsg("click", getCenterScreenCoord(ui), new Coord2d(x, y).floor(OCache.posres), btn, mod);
    }

    public static Coord getCenterScreenCoord(UI ui) {
        Coord sc, sz;
        sz = ui.gui.map.sz;
        sc = new Coord((int) Math.round(Math.random() * 200 + sz.x / 2f - 100),
                (int) Math.round(Math.random() * 200 + sz.y / 2f - 100));
        return sc;
    }

    public static void sysMsg(UI ui, String str, Color col) {
        if (ui.gui != null)
            ui.gui.msg(str, col);
    }
}
