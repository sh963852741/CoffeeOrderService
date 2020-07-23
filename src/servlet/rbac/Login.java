package servlet.rbac;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.sf.json.JSONObject;

/**
 * Servlet implementation class login
 */
@WebServlet("/api/usermanage/login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Login() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setCharacterEncoding("UTF-8");
    	response.setHeader("Allow", "POST");
    	response.sendError(405);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setContentType("text/json; charset=utf-8");
		PrintWriter out = response.getWriter();
		Connection conn = null;
		HttpSession session = request.getSession();
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://106.13.201.225:3306/coffee?useSSL=false&serverTimezone=GMT","coffee","TklRpGi1");
			ServletInputStream is;
			try {
				is = request.getInputStream();
				int nRead = 1;
				int nTotalRead = 0;
				byte[] bytes = new byte[10240];
				while (nRead > 0) {
					nRead = is.read(bytes, nTotalRead, bytes.length - nTotalRead);
					if (nRead > 0)
						nTotalRead = nTotalRead + nRead;
				}
				String str = new String(bytes, 0, nTotalRead, "utf-8");
				JSONObject jsonObj = JSONObject.fromObject(str);
				String userName = jsonObj.getString("userName");
				String password = jsonObj.getString("password");
				String sql = "select * from user where userName=? and password=?";
				String privilegeSql = "SELECT name_en FROM privilege JOIN privilege_role ON privilege_role.privilegeId = privilege.id "
						+ "JOIN role_user ON privilege_role.roleId = role_user.roleId WHERE userId = ?;";
				PreparedStatement ps = conn.prepareStatement(sql);
				PreparedStatement privilegePs = conn.prepareStatement(privilegeSql);
				ps.setString(1, userName);
				ps.setString(2, password);
				
				ResultSet rs = ps.executeQuery();
				JsonObject jsonobj = new JsonObject();
				if(rs.next()){
					String userId = rs.getString("userId");
					privilegePs.setString(1, userId);
					ResultSet privilegeRs = privilegePs.executeQuery();
					JsonArray privileges = new JsonArray();
					while(privilegeRs.next()) {
						privileges.add(privilegeRs.getString("name_en"));
					}
					privilegeRs.close();
					session.setAttribute("userId", userId);
					session.setAttribute("login", true);
					session.setAttribute("privileges", privileges);
					jsonobj.addProperty("success",true);
					jsonobj.add("privileges", privileges);
				}
				else {
					jsonobj.addProperty("success",false);
					jsonobj.addProperty("msg", "用户名或密码错误");
				}
				out = response.getWriter();
				out.print(jsonobj);
				rs.close();
				conn.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

}
