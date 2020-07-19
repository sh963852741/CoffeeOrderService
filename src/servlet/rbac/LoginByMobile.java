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

import net.sf.json.JSONObject;

/**
 * Servlet implementation class getUserInfo
 */
@WebServlet("/api/usermanage/loginByMobile")
public class LoginByMobile extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginByMobile() {
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
			HttpSession session = request.getSession();
			JSONObject jsonObj = JSONObject.fromObject(str);
			String VerificationCode_user = jsonObj.getString("code");
			String VerificationCode_session =(String)session.getAttribute("VerificationCode");
			JSONObject jsonobj = new JSONObject();
			if(VerificationCode_user.equals(VerificationCode_session))
			{
				session.setAttribute("login", true);
				session.removeAttribute("VerificationCode");
				session.setMaxInactiveInterval(-1); // 永不过时
				jsonobj.put("success", true);
				jsonobj.put("msg", "登录成功");
			}
			else
			{
				jsonobj.put("success", false);
				jsonobj.put("msg", "验证码不匹配");
			}
			if(jsonobj.isEmpty()) {
				jsonobj.put("success", false);
				jsonobj.put("msg", "操作失败");
			}
			out = response.getWriter();
			out.println(jsonobj);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
