package servlet.rbac;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Servlet implementation class GetAddrListByUserId
 */
@WebServlet("/api/usermanage/getAddrListByUserId")
public class GetAddrListById extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetAddrListById() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
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
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://106.13.201.225:3306/coffee?serverTimezone=GMT","coffee","TklRpGi1");
			String sql = "select * from user_addr where userId = ?";
			PreparedStatement ps = conn.prepareStatement(sql);
			HttpSession session = request.getSession();
			ps.setString(1, (String)session.getAttribute("userId"));
			ResultSet rs = ps.executeQuery();
			JsonArray jsonArray = new JsonArray();
			JsonObject jsonobj2 = new JsonObject();
			while(rs.next()){
				JsonObject jsonobj = new JsonObject();
				jsonobj.addProperty("address",rs.getString("address"));
				jsonobj.addProperty("userId",rs.getString("userId"));
				jsonobj.addProperty("id",rs.getString("id"));
				jsonobj.addProperty("provence",rs.getString("provence"));
				jsonobj.addProperty("city",rs.getString("city"));
				jsonobj.addProperty("street",rs.getString("street"));
				jsonobj.addProperty("zipcode",rs.getString("zipcode"));
				jsonobj.addProperty("country",rs.getString("country"));
				jsonobj.addProperty("isDefaultAddr",rs.getBoolean("isDefaultAddr"));
				jsonobj.addProperty("receiver",rs.getString("receiver"));
				jsonobj.addProperty("telephone",rs.getString("telephone"));
				jsonArray.add(jsonobj);
			}
			rs.close();
			jsonobj2.addProperty("success",true);
			jsonobj2.add("data", jsonArray);
			out = response.getWriter();
			out.print(jsonobj2);
			
			conn.close();
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
			/* 处理执行结果 */
			JsonObject responseJson = new JsonObject();
			responseJson.addProperty("success",false);
			responseJson.addProperty("msg", e.getMessage());
			out.println(responseJson);
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
	}

}
