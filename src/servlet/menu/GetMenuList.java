package servlet.menu;

import java.io.IOException;
import java.io.PrintWriter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Servlet implementation class regist
 */
@WebServlet("/api/menu/getMenuList")
public class GetMenuList extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetMenuList() {
        super();
        // TODO Auto-generated constructor stub
       
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    @Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	doPost(request, response);
	}
    
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
    @Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	/* 设置响应头部 */
    	response.setCharacterEncoding("UTF-8");
		response.setContentType("text/json; charset=utf-8");
		PrintWriter out = response.getWriter();
			
		Connection conn = null;
		Statement stmt = null;
		try {
			/* 连接数据库 */
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://106.13.201.225:3306/coffee?useSSL=false&serverTimezone=GMT","coffee","TklRpGi1");
			stmt = conn.createStatement();
			
			/* 构建SQL语句  */
			String sql1 = "select * from menu;";
			PreparedStatement ps1 = conn.prepareStatement(sql1);
			String sql2 = "Select count(*) as count From meal "
					+ "Where exists (Select * from menu Where meal.menuId = ?);";
			PreparedStatement ps2 = conn.prepareStatement(sql2);
			
			
			/* 执行SQL语句  */
			ResultSet rs1 = ps1.executeQuery();
			
			/* 处理执行结果 */
			JSONObject responseJson = new JSONObject();
			JSONArray jsonarray = new JSONArray();
			while(rs1.next()){
				/* 查询餐点数 */
				ps2.setString(1, rs1.getString("menuId"));
				ResultSet rs2 = ps2.executeQuery();
				rs2.next();
				
				JSONObject jsonobj = new JSONObject();
				jsonobj.put("menuId",rs1.getString("menuId"));
				jsonobj.put("active",rs1.getBoolean("active"));
				jsonobj.put("type",rs1.getString("type") == null ? "" : rs1.getString("type"));
				jsonobj.put("menuName",rs1.getString("menuName"));
				jsonobj.put("mealCount",rs2.getString("count"));
				jsonarray.add(jsonobj);
				rs2.close();
			}
			rs1.close();
			
			responseJson.put("success", true);
			responseJson.put("msg","");
			responseJson.put("data", jsonarray);
			out.println(responseJson);
		} catch (SQLException e) {
			e.printStackTrace();
			/* 处理执行结果 */
			JSONObject responseJson = new JSONObject();
			responseJson.put("success",false);
			responseJson.put("msg", e.getMessage());
			out.println(responseJson);
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} catch (ClassNotFoundException e) {
			e.fillInStackTrace();
		} finally {
			/* 无论如何关闭连接 */
			try {
				stmt.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}	
	}

}