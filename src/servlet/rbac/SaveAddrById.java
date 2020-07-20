package servlet.rbac;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Servlet implementation class SaveAddrByUserId
 */
@WebServlet("/api/usermanage/SaveAddrById")
public class SaveAddrById extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SaveAddrById() {
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
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/json; charset=utf-8");
		PrintWriter out = response.getWriter();
		BufferedReader reader = request.getReader();
		JsonObject requestJson = JsonParser.parseReader(reader).getAsJsonObject();
		
		Connection conn = null;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://106.13.201.225:3306/coffee?serverTimezone=GMT","coffee","TklRpGi1");
			
			HttpSession session = request.getSession();
			String userId = (String)session.getAttribute("userId");
			String address = requestJson.get("address").getAsString();
			String provence = requestJson.get("provence").getAsString();
			String city = requestJson.get("city").getAsString();
			String street = requestJson.get("street").getAsString();
			String zipcode = requestJson.get("zipcode").getAsString();
			String country = requestJson.get("country").getAsString();
			String telephone = requestJson.get("telephone").getAsString();
			String receiver = requestJson.get("receiver").getAsString();
			boolean isDefaultAddr = requestJson.get("isDefaultAddr").getAsBoolean();
			/* 如果这个是默认地址 */
			if(isDefaultAddr) {
				PreparedStatement ps = conn.prepareStatement("Update user_addr Set isDefaultAddr = false Where isDefaultAddr = true;");
				ps.executeUpdate();
			}
			if(requestJson.get("id") == null || requestJson.get("id").isJsonNull()) {
				String id = UUID.randomUUID().toString();
				/* 没有id则新建 */
				String sql = "insert into user_addr(address, userId, id, provence, city, street, zipcode, country, isDefaultAddr, telephone, receiver)"
						+ "values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
				PreparedStatement ps = conn.prepareStatement(sql);
				ps.setString(1, address);
				ps.setString(2, userId);
				ps.setString(3, id);
				ps.setString(4, provence);
				ps.setString(5, city);
				ps.setString(6, street);
				ps.setString(7, zipcode);
				ps.setString(8, country);
				ps.setBoolean(9, isDefaultAddr);
				ps.setString(10, telephone);
				ps.setString(11, receiver);
				ps.executeUpdate();
			} else {
				String id = requestJson.get("id").getAsString();
				String sql = "update user_addr set address=?, userId=?, provence=?, city=?,"
						+ "street=?, zipcode=?, country=?, isDefaultAddr=?, telephone=?, receiver=? where id=?";
				PreparedStatement ps = conn.prepareStatement(sql);
				ps.setString(1, address);
				ps.setString(2, userId);
				ps.setString(3, provence);
				ps.setString(4, city);
				ps.setString(5, street);
				ps.setString(6, zipcode);
				ps.setString(7, country);
				ps.setBoolean(8, isDefaultAddr);
				ps.setString(9, telephone);
				ps.setString(10, receiver);
				ps.setString(11, id);
				ps.executeUpdate();
			}
			JsonObject jsonobj = new JsonObject();
			jsonobj.addProperty("success",true);
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
