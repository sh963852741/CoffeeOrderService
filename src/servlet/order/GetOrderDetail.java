package servlet.order;

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
 * Servlet implementation class GetOrderDetail
 */
@WebServlet("/api/ordermanage/getOrderDetail")
public class GetOrderDetail extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetOrderDetail() {
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
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
		// TODO Auto-generated method stub
		response.setContentType("text/json; charset=utf-8");
		request.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		BufferedReader reader = request.getReader();
		JsonObject requestJson = JsonParser.parseReader(reader).getAsJsonObject();
		String orderId = requestJson.get("orderId").getAsString();
		
		Connection conn = null;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://106.13.201.225:3306/coffee?serverTimezone=Asia/Shanghai","coffee","TklRpGi1");

			String mealListSql = "select mealId, amount, price from meal_order where orderId = ?";
			String mealSql = "select mealName, pictureUrl from meal where mealId = ?";
			String orderSql = "select createdTime, status, addrId, isTakeOut, payment, remark,"
					+ "packingCharges, totalPrice, deliveryFee from orders where orderId = ?;";
			PreparedStatement mealListPs = conn.prepareStatement(mealListSql);
			PreparedStatement orderPs = conn.prepareStatement(orderSql);
			PreparedStatement mealPs = conn.prepareStatement(mealSql);
			mealListPs.setString(1, orderId);
			orderPs.setString(1, orderId);
			
			ResultSet mealListRs = mealListPs.executeQuery();
			JsonArray array = new JsonArray();
			/* 对于每一个菜品 */
			while(mealListRs.next()) {
				JsonObject jsonobj = new JsonObject();
				String mealId = mealListRs.getString("mealId");
				jsonobj.addProperty("amount", mealListRs.getInt("amount"));
				jsonobj.addProperty("mealId", mealId);
				jsonobj.addProperty("price", mealListRs.getDouble("price"));
				/* 获取菜品详情 */
				mealPs.setString(1, mealId);
				ResultSet mealRs = mealPs.executeQuery();
				mealRs.next();
				jsonobj.addProperty("mealName", mealRs.getString("mealName"));
				jsonobj.addProperty("pictureUrl", mealRs.getString("pictureUrl"));
				mealRs.close();
				
				array.add(jsonobj);
			}
			mealListRs.close();
			/* 获取订单详情 */
			ResultSet orderRs = orderPs.executeQuery();
			JsonObject responseJson = new JsonObject();
			orderRs.next();
			responseJson.addProperty("createdTime", orderRs.getTimestamp("createdTime").toString());
			responseJson.addProperty("status", orderRs.getString("status"));
			responseJson.addProperty("addrId", orderRs.getString("addrId"));
			responseJson.addProperty("isTakeOut", orderRs.getBoolean("isTakeOut"));
			responseJson.addProperty("payment", orderRs.getString("payment"));
			responseJson.addProperty("remark", orderRs.getString("remark"));
			responseJson.addProperty("packingCharges", orderRs.getFloat("packingCharges"));
			responseJson.addProperty("totalPrice", orderRs.getFloat("totalPrice"));
			responseJson.addProperty("deliveryFee", orderRs.getFloat("deliveryFee"));
			orderRs.close();
			
			responseJson.add("meals", array);
			responseJson.addProperty("success", true);
			
			out = response.getWriter();
			out.print(responseJson);
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
