package com.fzenner.datademo.web.servlets;


import com.fzenner.datademo.entity.taco.TacoAssistant;
import com.fzenner.datademo.gui.EntityNavigatorPage;
import com.fzenner.datademo.web.UserSession;
import com.fzenner.datademo.web.UserSessionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;

import static com.fzenner.datademo.web.servlets.WebFrontendDispatchServlet.TACO_EDITOR_PAGENAME;

@RestController
public class WebGui {

    @Autowired
    UserSessionHandler userSessionHandler;

    @GetMapping("/webFrontend/hello")
    //read the provided form data
    public String display(HttpSession session, @RequestParam("name") String name, @RequestParam("pass") String pass, Model m)
    {
        if(pass.equals("admin"))
        {
            String msg="Hello "+ name;
            //add a message to the model
            m.addAttribute("message", msg);
            return "<h1>hello world<h1>";
        }
        else
        {
            String msg="Sorry "+ name+". You entered an incorrect password";
            m.addAttribute("message", msg);
            return "errorpage";
        }
    }




    @GetMapping("/webFrontend/gui")
    //read the provided form data
    public String servePage(HttpSession session, @RequestParam("tacoId") String tacoId, Model m)
    {
        UserSession userSession = userSessionHandler.getUserSession(session);

        EntityNavigatorPage entityNavigatorPage = (EntityNavigatorPage) userSession.getPageIfExists(TACO_EDITOR_PAGENAME);
        if (entityNavigatorPage == null) {
            entityNavigatorPage = new EntityNavigatorPage(TACO_EDITOR_PAGENAME, userSession, 1L,  TacoAssistant.getGlobalInstance());
        }
        String result = entityNavigatorPage.getHtml();
        entityNavigatorPage.getBody().resetModificationMarkersRecursively(); // Sollte nicht notwendig sein. Bei Zeiten überprüfen.
        return result;

    }




}
