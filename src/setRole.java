

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
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Servlet implementation class setRole
 */
@WebServlet("/api/usermanage/setRole")
public class setRole extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public setRole() {
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
			ServletInputStream is = request.getInputStream();
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
			String userId = jsonObj.getString("userId");
			JSONArray roleArray = jsonObj.getJSONArray("roles");
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://106.13.201.225:3306/coffee?useSSL=false&serverTimezone=GMT","coffee","TklRpGi1");
			Statement stmt = conn.createStatement();
			String sql0 = "select * from role";
			ResultSet rs = stmt.executeQuery(sql0);
			Map<String,String>roleMap = new HashMap<String,String>();
			while(rs.next()) {
				roleMap.put(rs.getString("type"),rs.getString("roleId"));
			}
			int error = 0;//判断是否出错
			for(int i=0;i<roleArray.size();i++) {
				String sql = "insert into role_user(userId,roleId) values(?,?)";
				PreparedStatement ps = conn.prepareStatement(sql);
				String roleId = roleMap.get(roleArray.get(i));
				if(roleId == null) continue;
				ps.setString(1, userId);
				ps.setString(2,roleId);
				try {
					ps.executeUpdate();
				}
				catch(Exception e) {
					error = 1;
				}
			}
			if(error==1) {
				JSONObject jsonobj = new JSONObject();
				jsonobj.put("success",false);
				jsonobj.put("msg","操作错误,可能是角色不正确或用户已经具有该角色");
				out = response.getWriter();
				out.println(jsonobj);
				stmt.close();
				conn.close();
			}
			else {
				JSONObject jsonobj = new JSONObject();
				jsonobj.put("success",false);
				jsonobj.put("msg","设置成功");
				out = response.getWriter();
				out.println(jsonobj);
				stmt.close();
				conn.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
