package servlet.rbac;


import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
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
 * Servlet implementation class getRoleList
 */
@WebServlet("/api/usermanage/getRoleList")
public class GetRoleList extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetRoleList() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request,response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/json; charset=utf-8");
		PrintWriter out = response.getWriter();
		Connection conn = null;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://106.13.201.225:3306/coffee?useSSL=false&serverTimezone=GMT","coffee","TklRpGi1");
			Statement stmt = conn.createStatement();
			String sql = "select * from role";
			String sql2 = "select * from privilege";
			ResultSet rs = stmt.executeQuery(sql);
			JSONArray jsonarray = new JSONArray();
			JSONArray jsonarray2 = new JSONArray();
			JSONObject jsonobj = new JSONObject();
			/*temp用来存role对象数组*/
			JSONArray temp = new JSONArray();
			while(rs.next()){
				String roleName = rs.getString("roleName");
				String roleId = rs.getString("roleId");
				jsonarray.add(roleName);
				JSONObject tempObj = new JSONObject();
				tempObj.put("roleName", roleName);
				tempObj.put("roleId", roleId);
				temp.add(tempObj);
			}
			ResultSet rs2 = stmt.executeQuery(sql2);
			while(rs2.next()){
				JSONObject jsonobj2 = new JSONObject();
				jsonobj2.put("privilegeName", rs2.getString("name_zh"));
				jsonobj2.put("privilegeNameEN", rs2.getString("name_en"));
				jsonobj2.put("privilegeId", rs2.getString("id"));
				jsonarray2.add(jsonobj2);
			}
			jsonobj.put("permissions", jsonarray2);
			/*roleObj即为roleId+roleName的对象数组*/
			jsonobj.put("roleObj", temp);
			jsonobj.put("roles", jsonarray);
			jsonobj.put("success",true);
			out = response.getWriter();
			out.println(jsonobj);
			rs.close();
			stmt.close();
			conn.close();
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

}
