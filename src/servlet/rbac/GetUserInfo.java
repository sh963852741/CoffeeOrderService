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
 * Servlet implementation class regist
 */
@WebServlet("/api/usermanage/getUserInfo")
public class GetUserInfo extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetUserInfo() {
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
		
		/* 读取请求内容 */
		request.setCharacterEncoding("UTF-8");
		BufferedReader reader = request.getReader();
		
		/* 解析JSON获取数据 */
		JsonObject jsonObj = JsonParser.parseReader(reader).getAsJsonObject();
		
		String userId = jsonObj.get("userId") == null ? null : jsonObj.get("userId").getAsString();
		if(userId == null) {
			HttpSession session = request.getSession();
			userId = (String) session.getAttribute("userId");
		}
		
		Connection conn = null;
		try {
			/* 连接数据库 */
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://106.13.201.225:3306/coffee?serverTimezone=GMT", "coffee", "TklRpGi1");
			
			/* 构建SQL语句  */
			String sql1 = "select * from user where userId=?;";
			PreparedStatement ps1 = conn.prepareStatement(sql1);
			ps1.setString(1, userId);
			
			String sql2 = "select * from role_user where userId=?;";
			PreparedStatement ps2 = conn.prepareStatement(sql2);
			ps2.setString(1, userId);
			
			/* 执行SQL语句  */
			ResultSet rs1 = ps1.executeQuery();
			ResultSet rs2 = ps2.executeQuery();
			
			/* 处理执行结果 */
			JsonObject responseJson = new JsonObject();
			JsonArray jsonarray = new JsonArray();
			
			while(rs1.next()){
				responseJson.addProperty("userId", rs1.getString("userId"));
				responseJson.addProperty("userName", rs1.getString("userName"));
				responseJson.addProperty("telephone", rs1.getString("telephone"));
				responseJson.addProperty("email", rs1.getString("email"));
			}
			
			while(rs2.next()){
				jsonarray.add(rs2.getString("roleId"));
			}
			responseJson.add("role", jsonarray);
			
			rs1.close();
			rs2.close();
			responseJson.addProperty("success", true);
			responseJson.addProperty("msg","");
			out.print(responseJson.toString());
		} catch (SQLException e) {
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
		} catch (ClassNotFoundException e) {
			e.fillInStackTrace();
		} finally {
			/* 无论如何关闭连接 */
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}	
	}

}