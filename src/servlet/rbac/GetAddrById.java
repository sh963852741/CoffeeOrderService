package servlet.rbac;

import java.io.BufferedReader;
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

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Servlet implementation class GetAddrById
 */
@WebServlet("/api/usermanage/GetAddrById")
public class GetAddrById extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetAddrById() {
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
		BufferedReader reader = request.getReader();
		JsonObject requestJson = JsonParser.parseReader(reader).getAsJsonObject();
		String id = requestJson.get("id").getAsString();
		
		Connection conn = null;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://106.13.201.225:3306/coffee?serverTimezone=GMT","coffee","TklRpGi1");
			String sql = "select * from user_addr where id = ?";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, id);
			ResultSet rs = ps.executeQuery();
			
			rs.next();
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
			rs.close();
			
			jsonobj.addProperty("success", true);
			jsonobj.addProperty("msg", "");
			out = response.getWriter();
			out.print(jsonobj);
			
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
