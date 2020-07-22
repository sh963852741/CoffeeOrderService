package servlet.menu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Servlet implementation class SetMenuActive
 */
@WebServlet("/api/menu/setMenuActive")
public class SetMenuActive extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SetMenuActive() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/json; charset=utf-8");
		PrintWriter out = response.getWriter();
		BufferedReader reader = request.getReader();
		JsonObject requestJson = JsonParser.parseReader(reader).getAsJsonObject();
		String menuId = requestJson.get("menuId").getAsString();
		
		Connection conn = null;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://106.13.201.225:3306/coffee?serverTimezone=GMT","coffee","TklRpGi1");
			String sql1 = "Update menu Set active = false Where active = true;";
			String sql2 = "Update menu Set active = true Where menuId = ?;";
			PreparedStatement ps1 = conn.prepareStatement(sql1);
			PreparedStatement ps2 = conn.prepareStatement(sql2);
			ps1.executeUpdate();
			ps2.setString(1, menuId);
			ps2.executeUpdate();
			
			JsonObject jsonobj = new JsonObject();
			
			jsonobj.addProperty("success", true);
			jsonobj.addProperty("msg", "");
			out = response.getWriter();
			out.print(jsonobj);
			
			conn.close();
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
			/* 处理执行结果 */
			JsonObject responseJson = new JsonObject();
			responseJson.addProperty("success",false);
			responseJson.addProperty("msg", e.getMessage());
			out.println(responseJson);
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
	}

}
