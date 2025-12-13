package haven.custom;

import haven.*;

import java.awt.*;

public class AutoGiveButton extends Widget {
    public static Tex bg = Resource.loadtex("gfx/hud/combat/knapp/knapp");
    public static Tex ol = Resource.loadtex("gfx/hud/combat/knapp/ol");
    public static Tex or = Resource.loadtex("gfx/hud/combat/knapp/or");
    public static Tex sl = Resource.loadtex("gfx/hud/combat/knapp/sl");
    public static Tex sr = Resource.loadtex("gfx/hud/combat/knapp/sr");
    public int state;
    private Fightview.Relation rel;

    public AutoGiveButton(Fightview.Relation rel, long gobid) {
        super(bg.sz());
        this.rel = rel;
        if (gobid > 0 && this.rel.gobid == gobid) {
            this.state = 1;
            rel.peace();
        } else {
            this.state = 0;
        }
    }

    public void draw(GOut g) {
        if(state == 0)
            g.chcolor(200, 200, 200, 255);
        else if(state == 1)
            g.chcolor(0, 255, 0, 255);
        g.image(bg, Coord.z, sz);
        g.chcolor();
        if((state & 1) != 0)
            g.image(ol, Coord.z, sz);
        else
            g.image(sl, Coord.z, sz);
        if((state & 2) != 0)
            g.image(or, Coord.z, sz);
        else
            g.image(sr, Coord.z, sz);
    }

    @Override
    public Object tooltip(Coord c, Widget prev) {
        return RichText.render("Re-aggro current target " + (state == 0 ? "(Off)" : "(On)"), 0);
    }

    @Override
	public boolean mousedown(MouseDownEvent ev) {
        this.state = ++this.state % 2;
        if (this.state == 1)
            rel.peace();
        else{
            rel.give.wdgmsg("click", 1);
        }
        return(true);
    }

    public void remoteTrigger(){ //there's probably a better way to click this from outside this class, but I'm dumb so this works instead. -Ard
        this.state = ++this.state %2;
        if (this.state == 1)
            rel.peace();
        else{
            rel.give.wdgmsg("click", 1);
        }
    }
}
