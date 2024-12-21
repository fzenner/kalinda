package com.kewebsi.html.popup;

import com.fzenner.datademo.web.outmsg.GuiDef;

public class ModalStandardDialogDef extends GuiDef {



    public String mainText;
    public GuiDef cancelButton;
    public GuiDef confirmButton;
    public GuiDef cancelXButton;

    // public String popupUnderId;

    public ModalStandardDialogDef(String id) {
        super("modalstandarddialog", id);
    }

//    public void setModalDialogSpecificData(String mainText, GuiDef cancelButton, GuiDef confirmButton, GuiDef cancelXButton, String popupUnderId) {
//        this.mainText = mainText;
//        this.cancelButton = cancelButton;
//        this.confirmButton = confirmButton;
//        this.cancelXButton = cancelXButton;
//        this.popupUnderId = popupUnderId;
//    }

    public void setModalDialogSpecificData(String mainText, GuiDef cancelButton, GuiDef confirmButton, GuiDef cancelXButton) {
        this.mainText = mainText;
        this.cancelButton = cancelButton;
        this.confirmButton = confirmButton;
        this.cancelXButton = cancelXButton;
    }

}
