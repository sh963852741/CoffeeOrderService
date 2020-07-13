package servlet.rbac;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

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
		String msg = null;
		StringBuilder message= new StringBuilder();
		while ((msg = reader.readLine()) != null){			
			message.append(msg);
		}		
		String jsonStr = message.toString();
		
		/* 处理请求内容为空的情况 */
		if(jsonStr.isEmpty()) 
		{
			response.sendError(400);
			return;
		}
		
		/* 解析JSON获取数据 */
		JSONObject jsonObj = JSONObject.fromObject(jsonStr);
		String userId = "";
		try {
			userId = jsonObj.getString("userId");
		}
		catch(Exception e) {
			userId = null;
		}
		if(userId==null) {
			userId = "";
			HttpServletRequest req = (HttpServletRequest)request;
			HttpSession session = req.getSession();
			userId = (String) session.getAttribute("userId");
		}
		Connection conn = null;
		Statement stmt = null;
		try {
			/* 连接数据库 */
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://106.13.201.225:3306/coffee?useSSL=false&serverTimezone=GMT","coffee","TklRpGi1");
			stmt = conn.createStatement();
			
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
			JSONObject responseJson = new JSONObject();
			JSONArray jsonarray = new JSONArray();
			
			while(rs1.next()){
				responseJson.put("userId", rs1.getString("userId"));
				responseJson.put("userName", rs1.getString("userName"));
				responseJson.put("password", rs1.getString("password"));
				responseJson.put("telephone",rs1.getString("telephone") == null ? "" : rs1.getString("telephone"));
				responseJson.put("email",rs1.getString("email") == null ? "" : rs1.getString("email"));
			}
			
			while(rs2.next()){
				jsonarray.add(rs2.getString("roleName"));
			}
			responseJson.put("role", jsonarray);
			
			rs1.close();
			rs2.close();
			responseJson.put("success", true);
			responseJson.put("msg","");
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