package servlet.rbac;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

/**
 * Servlet implementation class AddRoleList
 */
@WebServlet("/api/usermanage/addRoleList")
public class AddRoleList extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AddRoleList() {
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
		/* 设置响应头部 */
    	response.setCharacterEncoding("UTF-8");
		response.setContentType("text/json; charset=utf-8");
		PrintWriter out = response.getWriter();
		
		/* 读取请求内容*/
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
		String roleName = jsonObj.getString("roleName");
		String roleId = UUID.randomUUID().toString();
		Connection conn = null;
		try {
			/* 连接数据库 */
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://106.13.201.225:3306/coffee?useSSL=false&serverTimezone=GMT","coffee","TklRpGi1");
			
			/* 构建SQL语句*/			
			String sql = "insert into role(roleName,roleId) values(?,?)";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, roleName);
			ps.setString(2, roleId);
			
			/*执行*/
			ps.executeUpdate();
			
			/* 处理执行结果 */
			JSONObject responseJson = new JSONObject();
			responseJson.put("success", true);
			responseJson.put("msg","新增成功");
			out.println(responseJson);
		}
		catch (SQLException e) {
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
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

}
