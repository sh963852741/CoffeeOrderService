package servlet.menu;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

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
		response.setContentType("text/json; charset=utf-8");
		PrintWriter out = response.getWriter();
		Connection conn = null;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://106.13.201.225:3306/coffee?useSSL=false&serverTimezone=GMT","coffee","TklRpGi1");
			Statement stmt = conn.createStatement();
			String sql = "select menuId,menuName from menu where active = 1";
			String sql2 = "select distinct type from meal where menuId = ?";
			String sql3 = "select * from meal where menuId = ?";
			PreparedStatement ps = conn.prepareStatement(sql);
			PreparedStatement ps2 = conn.prepareStatement(sql2);
			PreparedStatement ps3 = conn.prepareStatement(sql3);
			Map<String,JSONArray> types2 = new HashMap<String,JSONArray>();
			ResultSet rs = ps.executeQuery();
			rs.next();
			String menuId = rs.getString("menuId");
			rs.close();
			ps2.setString(1, menuId);
			ps3.setString(1, menuId);
			ResultSet rs2 = ps2.executeQuery();
			while(rs2.next()) {
				String type = rs2.getString("type");
				JSONArray jsonarray = new JSONArray();
				types2.put(type,jsonarray);
			}
			rs2.close();
			ResultSet rs3 = ps3.executeQuery();
			JSONObject jsonobj = new JSONObject();
			while(rs3.next()) {
				String type = rs3.getString("type");
				if(types2.containsKey(type)) {
					jsonobj.put("mealId",rs3.getString("mealId"));
					jsonobj.put("mealName",rs3.getString("mealName")==null?"":rs3.getString("mealName"));
					jsonobj.put("price",rs3.getObject("price")==null?"":rs3.getDouble("price"));
					jsonobj.put("amount",rs3.getObject("amount")==null?"":rs3.getInt("amount"));
					jsonobj.put("menuId",rs3.getString("menuId"));
					jsonobj.put("type",type);
					types2.get(type).add(jsonobj);
				}
			}
			JSONObject jsonobj2 = new JSONObject();
			jsonobj2.putAll(types2);
			jsonobj2.put("success",true);
			out.println(jsonobj2);
			out.close();
			stmt.close();
		} catch (ClassNotFoundException | SQLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
	}

}
