package com.fzenner.datademo.web.outmsg;

import java.util.ArrayList;

public class MsgGuiDef extends MsgJsonOut {

    public GuiDef guiDef;

    public MsgGuiDef(GuiDef guiDef) {
        super(MsgKewebsiOut.GUI_DEF);
        this.guiDef = guiDef;
    }

}
