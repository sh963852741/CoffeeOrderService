package servlet.global;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;


public class InitListener implements ServletContextListener {  
	
    /* @Override  
    public void contextDestroyed(ServletContextEvent context) {  
          
    } */
  
    @Override  
    public void contextInitialized(ServletContextEvent context) {
    	ServletContext sc = context.getServletContext();
    	Map<String,String>loginedUser = new HashMap<String, String>();
    	sc.setAttribute("loginedUser", loginedUser);
    }  
}