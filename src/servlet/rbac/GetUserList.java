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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Servlet implementation class getUserList
 */
@WebServlet("/api/usermanage/getUserList")
public class GetUserList extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetUserList() {
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
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/json; charset=utf-8");
		PrintWriter out = response.getWriter();
		BufferedReader reader = request.getReader();
		JsonObject requestJson = JsonParser.parseReader(reader).getAsJsonObject();
		
		/* 设置分页默认值 */
		int page, pageSize;
		if(requestJson.get("page") == null || requestJson.get("page").isJsonNull()) {
			page = 1;
		} else {
			page = requestJson.get("page").getAsInt();
		}
		if(requestJson.get("pageSize") == null || requestJson.get("pageSize").isJsonNull()) {
			pageSize = 20;
		} else {
			pageSize = requestJson.get("pageSize").getAsInt();
		}
		
		Connection conn = null;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://106.13.201.225:3306/coffee?serverTimezone=GMT","coffee","TklRpGi1");
			String sql = "select * from user limit ?, ?";
			String countSql ="select count(*) as total from user";
			PreparedStatement ps = conn.prepareStatement(sql);
			PreparedStatement countPs = conn.prepareStatement(countSql);
			ps.setInt(1, (page - 1) * pageSize);
			ps.setInt(2, pageSize);
			ResultSet rs = ps.executeQuery();
			ResultSet countRs = countPs.executeQuery();
			
			JsonObject jsonobj2 = new JsonObject();
			countRs.next();
			jsonobj2.addProperty("totalRows", countRs.getInt("total"));
			jsonobj2.addProperty("page", page);
			jsonobj2.addProperty("pageSize", pageSize);
			
			JsonArray jsonarray = new JsonArray();
			while(rs.next()){
				JsonObject jsonobj = new JsonObject();
				jsonobj.addProperty("userName",rs.getString("userName"));
				jsonobj.addProperty("userId",rs.getString("userId"));
				jsonobj.addProperty("telephone",rs.getString("telephone"));
				jsonobj.addProperty("email",rs.getString("email"));
				jsonarray.add(jsonobj);
			}
			rs.close();
			jsonobj2.addProperty("success",true);
			jsonobj2.add("data", jsonarray);
			
			out = response.getWriter();
			out.print(jsonobj2);
			conn.close();
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
			
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
