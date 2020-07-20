package servlet.menu;

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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mysql.cj.jdbc.Driver;

/**
 * Servlet implementation class Get
 */
@WebServlet("/api/menu/getMealByKind")
public class GetMealByKind extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetMealByKind() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/json; charset=utf-8");
		PrintWriter out = response.getWriter();
		BufferedReader reader = request.getReader();
		JsonObject requestJson = JsonParser.parseReader(reader).getAsJsonObject();
		
		Connection conn = null;
		try {
			conn = DriverManager.getConnection("jdbc:mysql://106.13.201.225:3306/coffee?serverTimezone=GMT", "coffee", "TklRpGi1");
			String sql = "select distinct type from meal";
			String sql2 = "select distinct type from meal where menuId = ?";
			
			PreparedStatement ps = conn.prepareStatement(sql);
			PreparedStatement ps2 = conn.prepareStatement(sql2);
			
			ResultSet rs = null;
			if(requestJson.get("menuId") == null) {
				rs = ps.executeQuery();
			} else {
				ps2.setString(1, requestJson.get("menuId").getAsString());
				rs = ps2.executeQuery();
			}
				
			/* 获取所有类别 */
			JsonArray type = new JsonArray();
			while(rs.next()) {
				type.add(rs.getString("type"));
			}
			rs.close();
			
			JsonObject responseJson = new JsonObject();
			responseJson.add("data", type);
			responseJson.addProperty("success",true);
			out.print(responseJson);
		} catch (SQLException e) {
			JsonObject responseJson = new JsonObject();
			responseJson.addProperty("success",false);
			responseJson.addProperty("msg", e.getMessage());
			out.print(responseJson);
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
	}

}
