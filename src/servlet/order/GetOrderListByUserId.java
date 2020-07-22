package servlet.order;

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
 * Servlet implementation class GetOrderListByUserId
 */
@WebServlet("/api/ordermanage/getOrderListByUserId")
public class GetOrderListByUserId extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetOrderListByUserId() {
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
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/json; charset=utf-8");
		PrintWriter out = response.getWriter();
		HttpSession session = request.getSession();
		// BufferedReader reader = request.getReader();
		// JsonObject requestJson = JsonParser.parseReader(reader).getAsJsonObject();
		
		Connection conn=null;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn=DriverManager.getConnection("jdbc:mysql://106.13.201.225:3306/coffee?serverTimezone=Asia/Shanghai", "coffee", "TklRpGi1");

			String userId = (String)session.getAttribute("userId");
			String sql="select * from orders where userId= ? order by createdTime desc";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1,userId);
			ResultSet rs = ps.executeQuery();
			JsonObject responseJson = new JsonObject();
			JsonArray dataArray = new JsonArray();
			while(rs.next()) {
				JsonObject obj = new JsonObject();
				obj.addProperty("orderId", rs.getString("orderId"));
				obj.addProperty("createdTime", rs.getTimestamp("createdTime").toString());
				obj.addProperty("status", rs.getString("status"));
				obj.addProperty("addrId", rs.getString("addrId"));
				obj.addProperty("isTakeOut", rs.getBoolean("isTakeOut"));
				obj.addProperty("payment", rs.getString("payment"));
				obj.addProperty("remark", rs.getString("remark"));
				obj.addProperty("packingCharges", rs.getFloat("packingCharges"));
				obj.addProperty("totalPrice", rs.getFloat("totalPrice"));
				obj.addProperty("deliveryFee", rs.getFloat("deliveryFee"));
				dataArray.add(obj);
			}
			rs.close();
			responseJson.addProperty("success", true);
			responseJson.addProperty("msg","订单记录获取成功");
			responseJson.add("data",dataArray);
			out = response.getWriter();
			out.print(responseJson);
			conn.close();
		}catch(SQLException | ClassNotFoundException e) {
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