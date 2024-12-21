package com.fzenner.datademo.gui;

import com.fasterxml.jackson.databind.JsonNode;
import com.fzenner.datademo.entity.taco.TacoAssistant;
import com.fzenner.datademo.web.UserSession;
import com.fzenner.datademo.web.inmsg.ClientEventHandlerConsts;
import com.fzenner.datademo.web.outmsg.MsgAjaxResponse;
import com.kewebsi.errorhandling.CodingErrorException;
import com.kewebsi.html.*;
import org.apache.catalina.User;

import static com.fzenner.datademo.web.servlets.WebFrontendDispatchServlet.TACO_EDITOR_PAGENAME;

public class NavigatorPage extends HtmlPage {
    protected UserSession userSession;

    public NavigatorPage(int navigatorId, UserSession userSession) {
        super("Navigator", "Navigator");
        this.userSession = userSession;

        HtmlDiv2 headerAndStuffBelow = new HtmlDiv2("headerAndStuffBelow", "vertStretchDiv100vh");
        HtmlLabel headerLabel = new HtmlLabel("Hello WorldXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
        headerAndStuffBelow.addChild(headerLabel);

        HtmlDiv2 sideNavAndContent = new HtmlDiv2("sideNavAndContent", "sidenavParent");


        HtmlDiv2 sidebarNav = new HtmlDiv2("sideNav2", "sidenav2");
        sidebarNav.putNonStandardAttribute(ClientEventHandlerConsts.EVENT_TO_HANDLE_ATTR_NAME_1,      "click");
        sidebarNav.putNonStandardAttribute(ClientEventHandlerConsts.CLIENT_EVENT_HANDLER_ATTR_NAME_1, "CEH_closeNav");

        sidebarNav.addChild(new HtmlLabel("Hello World"));

//        HtmlDiv vertStretch = new HtmlDiv("vertStretchDiv");
//        vertStretch.addChild(sidebarNav);

        HtmlDiv2 contentDiv = new HtmlDiv2("contentRightToSidebar", "heightToMinContent");

        HtmlButtonStandard button = new HtmlButtonStandard("testid", "Dummy") {
            @Override
            public MsgAjaxResponse handleClick(JsonNode rootNode, UserSession userSession) {
                return MsgAjaxResponse.createSuccessMsg();
            }
        };

        var enp = new EntityNavigatorDiv(userSession, 123L, TacoAssistant.getGlobalInstance());

        contentDiv.addChild(button);
        contentDiv.addChild(enp);

        // button.addCssClass("heightToMinContent");


        sideNavAndContent.addChildren(sidebarNav, contentDiv);

        // addChild(new HtmlLabel("Hammer!"));

        // topStretchDiv.addChild(sideNavAndContent);

        headerAndStuffBelow.addChild(sideNavAndContent);

        body.addChild(headerAndStuffBelow);


    }

    @Override
    public PageState getPageState() {
        throw new CodingErrorException("Error found!");
    }
}
