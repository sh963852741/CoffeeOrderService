package servlet.rbac;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

/**
 * Servlet implementation class RetrievePassword
 */
@WebServlet("/api/usermanage/retrievePassword")
public class RetrievePassword extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RetrievePassword() {
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
		response.setContentType("text/json; charset=utf-8");
		PrintWriter out = response.getWriter();
		Connection conn = null;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://106.13.201.225:3306/coffee?serverTimezone=GMT","coffee","TklRpGi1");
			ServletInputStream is;
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
			
			HttpSession session = request.getSession();
			JSONObject jsonObj = JSONObject.fromObject(str);
			String password = jsonObj.getString("password");
			String VerificationCode_user = jsonObj.getString("code");
			String VerificationCode_session =(String)session.getAttribute("VerificationCode");
			JSONObject jsonobj = new JSONObject();
			if(VerificationCode_user.equals(VerificationCode_session))
			{
				String sql = "Update user Set password=? Where userId = ?";
				PreparedStatement ps = conn.prepareStatement(sql);
				ps.setString(1, password);
				ps.setNString(2, (String)session.getAttribute("userId"));
				ps.executeUpdate();
				session.invalidate();
				jsonobj.put("success", true);
				jsonobj.put("msg", "已更新密码");
			} else {
				jsonobj.put("success", false);
				jsonobj.put("msg", "验证码错误");
			}
			out.print(jsonobj);
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

}
