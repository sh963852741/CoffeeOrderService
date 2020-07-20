package servlet.rbac;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.JsonObject;

/**
 * Servlet implementation class GetUserState
 */
@WebServlet("/api/usermanage/getUserState")
public class GetUserState extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetUserState() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request,response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		HttpSession session = request.getSession();
		// JsonObject jsonObj = JsonParser.parseReader(reader).getAsJsonObject();
		request.setCharacterEncoding("UTF-8");
		JsonObject jsonObj2 = new JsonObject();
		if(session.getAttribute("userId")!=null) {
			jsonObj2.addProperty("userId", (String)session.getAttribute("userId"));
			jsonObj2.addProperty("success",true);
		}
		else {
			jsonObj2.addProperty("msg"," 查询不到登录信息");
			jsonObj2.addProperty("success", false);
		}
			
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/json; charset=utf-8");
		PrintWriter out = response.getWriter();
		out.println(jsonObj2.toString());
		out.close();
	}
}
