package servlet.rbac;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Servlet implementation class getUserInfo
 */
@WebServlet("/api/usermanage/loginByMobile")
public class LoginByMobile extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginByMobile() {
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
		response.setContentType("text/json; charset=utf-8");
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		
		Connection conn = null;
		try {
			conn = DriverManager.getConnection("jdbc:mysql://106.13.201.225:3306/coffee?useSSL=false&serverTimezone=GMT","coffee","TklRpGi1");
			
			BufferedReader reader = request.getReader();
			JsonObject requestJson = JsonParser.parseReader(reader).getAsJsonObject();
			HttpSession session = request.getSession();
			String VerificationCode_user = requestJson.get("code").getAsString();
			String VerificationCode_session =(String)session.getAttribute("VerificationCode");
			
			JsonObject jsonobj = new JsonObject();
			if(VerificationCode_user.equals(VerificationCode_session))
			{
				String privilegeSql = "SELECT name_en FROM privilege JOIN privilege_role ON privilege_role.privilegeId = privilege.id "
						+ "JOIN role_user ON privilege_role.roleId = role_user.roleId WHERE userId = ?;";
				PreparedStatement privilegePs = conn.prepareStatement(privilegeSql);
				privilegePs.setString(1, (String)session.getAttribute("userId"));
				ResultSet privilegeRs = privilegePs.executeQuery();
				JsonArray privileges = new JsonArray();
				while(privilegeRs.next()) {
					privileges.add(privilegeRs.getString("name_en"));
				}
				privilegeRs.close();
				
				session.setAttribute("login", true);
				session.removeAttribute("VerificationCode");
				session.setMaxInactiveInterval(-1); // 永不过时
				session.setAttribute("privileges", privileges);
				
				jsonobj.add("privileges", privileges);
				jsonobj.addProperty("success", true);
				jsonobj.addProperty("msg", "登录成功");
			}
			else
			{
				jsonobj.addProperty("success", false);
				jsonobj.addProperty("msg", "验证码错误");
			}
			
			out = response.getWriter();
			out.println(jsonobj);
		} catch (SQLException | IOException e) {
			JsonObject responseJson = new JsonObject();
			responseJson.addProperty("success",false);
			responseJson.addProperty("msg", e.getMessage());
			out.println(responseJson);
			try {
				conn.rollback();
				conn.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
	}

}
