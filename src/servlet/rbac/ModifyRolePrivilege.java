package servlet.rbac;

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

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Servlet implementation class ModifyRolePrivilege
 */
@WebServlet("/api/usermanage/modifyRolePri")
public class ModifyRolePrivilege extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ModifyRolePrivilege() {
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
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/json; charset=utf-8");
		PrintWriter out = response.getWriter();
		
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
		String roleId = jsonObj.getString("roleId");
		JSONArray permissions = jsonObj.getJSONArray("permission");
		Connection conn = null;
		try {
			/* 连接数据库 */
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://106.13.201.225:3306/coffee?useSSL=false&serverTimezone=GMT","coffee","TklRpGi1");
			
			/* 构建SQL语句*/			
			String sql = "delete from privilege_role where roleId=?";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, roleId);
			
			/*删除与权限的对应关系*/
			ps.executeUpdate();
			
			/*重新插入*/
			String sql2 = "insert into privilege_role(privilegeId,roleId) values(?,?)";
			PreparedStatement ps2 = conn.prepareStatement(sql2);
			ps2.setString(2, roleId);
			for(int i=0;i<permissions.size();i++) {
				String privilegeId = permissions.getString(i);
				ps2.setString(1,privilegeId);
				ps2.executeUpdate();
			}

			/* 处理执行结果 */
			JSONObject responseJson = new JSONObject();
			responseJson.put("success", true);
			responseJson.put("msg","修改成功");
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
