package servlet.menu;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mysql.cj.jdbc.Driver;



/**
 * Servlet implementation class GetMealBySort
 */
@WebServlet("/api/menu/getMealBySort")
public class GetMealBySort extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetMealBySort() {
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
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/json; charset=utf-8");
		PrintWriter out = response.getWriter();
		
		Connection conn = null;
		try {
			conn = DriverManager.getConnection("jdbc:mysql://106.13.201.225:3306/coffee?serverTimezone=GMT","coffee","TklRpGi1");
			String sql = "select menuId,menuName from menu where active = 1";
			String sql2 = "select distinct type from meal where menuId = ?";
			String sql3 = "select * from meal where menuId = ?";
			PreparedStatement ps = conn.prepareStatement(sql);
			PreparedStatement ps2 = conn.prepareStatement(sql2);
			PreparedStatement ps3 = conn.prepareStatement(sql3);
			// 最终结果集
			Map<String,JsonArray> types2 = new HashMap<String,JsonArray>();
			/* 获取激活的菜单Id */
			ResultSet rs = ps.executeQuery();
			rs.next();
			String menuId = rs.getString("menuId");
			rs.close();
			
			ps2.setString(1, menuId);
			ps3.setString(1, menuId);
			/* 获取所有类别 */
			ResultSet rs2 = ps2.executeQuery();
			while(rs2.next()) {
				String type = rs2.getString("type");
				JsonArray jsonarray = new JsonArray();
				types2.put(type,jsonarray);
			}
			rs2.close();
			
			ResultSet rs3 = ps3.executeQuery();
			while(rs3.next()) {
				String type = rs3.getString("type");
				JsonObject jsonobj = new JsonObject();
				jsonobj.addProperty("mealId",rs3.getString("mealId"));
				jsonobj.addProperty("mealName",rs3.getString("mealName"));
				jsonobj.addProperty("price",rs3.getDouble("price"));
				jsonobj.addProperty("amount",rs3.getInt("amount"));
				jsonobj.addProperty("menuId",rs3.getString("menuId"));
				jsonobj.addProperty("type",type);
				types2.get(type).add(jsonobj);
			}
			/* Map转Json */
			Gson gson = new Gson();
			JsonObject responseJson = new JsonObject();
			responseJson.add("data", gson.toJsonTree(types2));
			responseJson.addProperty("success", true);
			out.print(responseJson);
			out.close();
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
