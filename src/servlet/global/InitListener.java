package servlet.global;

import java.io.File;
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
    	
    	String mealImgSavePath=context.getServletContext().getRealPath("/");
    	mealImgSavePath += ".." + File.separator + "Attachment" + File.separator + "MenuImage" + File.separator;
    	File savefloder = new File(mealImgSavePath);
        if (!savefloder.exists()) {
        	savefloder.mkdir();
        }
    }  
}