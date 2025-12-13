package haven.custom;

import haven.Coord;
import haven.GameUI;
import haven.Gob;
import haven.OCache;

public class GameUIUtils {

    public static boolean aggro(GameUI gui, long gobid) {
        Gob gob = gui.map.glob.oc.getgob(gobid);
        if (gob != null && gui != null && gui.map != null) {
            gui.act("aggro");
            gui.map.wdgmsg("click", Coord.z, gob.rc.floor(OCache.posres), 1, 0, 0, (int) gob.id,
                    gob.rc.floor(OCache.posres), 0, -1);
            gui.map.wdgmsg("click", Coord.z, gui.map.player().rc.floor(OCache.posres), 3, 0);
            return true;
        }
        return false;
    }

}
