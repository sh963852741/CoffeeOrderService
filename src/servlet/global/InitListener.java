package servlet.global;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class InitListener implements ServletContextListener {  
	
    /* @Override  
    public void contextDestroyed(ServletContextEvent context) {  
          
    } */
  
    @Override  
    public void contextInitialized(ServletContextEvent context) {
    	//ServletContext sc = context.getServletContext();
    	
    	String mealImgSavePath=context.getServletContext().getRealPath("/MenuImage/");
    	File savefloder = new File(mealImgSavePath);
        if (!savefloder.exists()) {
        	savefloder.mkdir();
        }
    }  
}