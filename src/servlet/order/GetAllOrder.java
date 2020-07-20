package servlet.order;


import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Servlet implementation class getUserInfo
 */
@WebServlet("/api/ordermanage/getAllOrder")
public class GetAllOrder extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetAllOrder() {
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
		HttpSession session = request.getSession();
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://106.13.201.225:3306/coffee?useSSL=false&serverTimezone=GMT","coffee","TklRpGi1");
			Statement stmt = conn.createStatement();
			try {
				String sql = "select * from orders";
				String sql2 = "select userName from user where userId=?";
				String sql3 = "select sum(amount*price)as totalPrice from meal_order where orderId=?";
				PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery();
				JSONObject jsonobj = new JSONObject();
				JSONObject jsonobj2 = new JSONObject();
				JSONArray  jsonarray = new JSONArray();
				while(rs.next()){
					String userId = rs.getString("userId");
					String orderId = rs.getString("orderId");
					jsonobj2.put("orderId",rs.getString("orderId"));
					PreparedStatement ps2=conn.prepareStatement(sql2);
					ps2.setString(1, userId);
					ResultSet rs2=ps2.executeQuery();
					rs2.next();
					PreparedStatement ps3=conn.prepareStatement(sql3);
					ps3.setString(1, orderId);
					ResultSet rs3=ps3.executeQuery();
					rs3.next();
					jsonobj2.put("userName",rs2.getString("userName"));
					jsonobj2.put("createdTime",rs.getString("createdTime"));
					jsonobj2.put("totalPrice",rs3.getDouble("totalPrice"));
					jsonarray.add(jsonobj2);
				}
				if(jsonarray.isEmpty()) {
					jsonobj.put("success", false);
					jsonobj.put("msg", "为空");
				}
				else {
					jsonobj.put("success", true);
					jsonobj.put("msg", "操作成功");
					jsonobj.put("data",jsonarray);
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
