package servlet.rbac;


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
import javax.servlet.ServletInputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Servlet implementation class setUserInfo
 */
@WebServlet("/api/usermanage/setUserInfo")
public class SetUserInfo extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SetUserInfo() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
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
			JSONArray roleArray = jsonObj.getJSONArray("updates");
			Map<String,String>changeInfo = new HashMap<String,String>();
			for(int i=0;i<roleArray.size();i++) {
				JSONObject temp = roleArray.getJSONObject(i);
				for(Object key:temp.keySet()) {
					changeInfo.put((String)key, (String)temp.get(key));
				}
			}
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://106.13.201.225:3306/coffee?useSSL=false&serverTimezone=GMT","coffee","TklRpGi1");
			Statement stmt = conn.createStatement();
			int error = 0;//判断是否出错
			if(changeInfo.isEmpty()) error=1;
			else {				
				for(String key:changeInfo.keySet()) {
					String sql = "update user set "+key+" = ? where userId = ?";
					PreparedStatement ps = conn.prepareStatement(sql);
					ps.setString(1,changeInfo.get(key));
					ps.setString(2,userId);
					try {
						ps.executeUpdate();
					}
					catch(Exception e) {
						error =1;
					}
				}
			}
			if(error==1) {
				JSONObject jsonobj = new JSONObject();
				jsonobj.put("success",false);
				jsonobj.put("msg","操作错误,修改字段为空或�?�名称不正确");
				out = response.getWriter();
				out.println(jsonobj);
				stmt.close();
				conn.close();
			}
			else {
				JSONObject jsonobj = new JSONObject();
				jsonobj.put("success",false);
				jsonobj.put("msg","修改成功");
				out = response.getWriter();
				out.println(jsonobj);
				stmt.close();
				conn.close();
			}
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

}
