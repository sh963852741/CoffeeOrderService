package servlet.rbac;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.sf.json.JSONObject;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class login
 */
@WebServlet("/api/usermanage/login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		Map<String,String>userTokenMap = new HashMap<String, String>();
		this.getServletContext().setAttribute("userTokenMap", userTokenMap);
	}
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
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://106.13.201.225:3306/coffee?useSSL=false&serverTimezone=GMT","coffee","TklRpGi1");
			Statement stmt = conn.createStatement();
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
				PreparedStatement ps = conn.prepareStatement(sql);
				ps.setString(1, userName);
				ps.setString(2, password);
				ResultSet rs = ps.executeQuery();
				JSONObject jsonobj = new JSONObject();
				if(rs.next()){
					jsonobj.put("success",true);
					String token = UUID.randomUUID().toString().replace("-", "");
					jsonobj.put("token",token);
					Map<String,String> temp = (Map<String,String>)this.getServletContext().getAttribute("userTokenMap");
					temp.put(rs.getString("userId"), token);
					this.getServletContext().setAttribute("userTokenMap",temp);
				}
				else {
					jsonobj.put("success",false);
					jsonobj.put("msg","用户名或密码错误");
				}
				out = response.getWriter();
				out.println(jsonobj);
				rs.close();
				stmt.close();
				conn.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

}
