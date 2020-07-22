package servlet.order;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.mysql.cj.jdbc.Driver;

import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.*;

/**
 * Servlet implementation class CreateOrder
 */
@WebServlet("/api/ordermanage/createOrder")
public class CreateOrder extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CreateOrder() {
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
		/* 设置响应头部 */
    	response.setCharacterEncoding("UTF-8");
		response.setContentType("text/json; charset=utf-8");
		PrintWriter out = response.getWriter();
		JsonObject responseJson = new JsonObject();
		
		/* 读取请求内容 */
		request.setCharacterEncoding("UTF-8");
		BufferedReader reader = request.getReader();
		HttpSession session = request.getSession();
		
		/* 解析JSON获取数据 */
		JsonElement jsonEle = JsonParser.parseReader(reader);
		JsonObject jsonObj = jsonEle.getAsJsonObject();
		String orderId = UUID.randomUUID().toString();
		String userId = (String) session.getAttribute("userId");
		String addrId;
		if(jsonObj.get("addrId") == null || jsonObj.get("addrId").isJsonNull()) {
			addrId = null;
		} else {
			addrId = jsonObj.get("addrId").getAsString();
		}
		 
		String remark = jsonObj.get("remark").getAsString();
		String payment = jsonObj.get("payment").getAsString();
		float packingCharges = jsonObj.get("packingCharges").getAsFloat();
		float deliveryFee = jsonObj.get("deliveryFee").getAsFloat();
		boolean isTakeOut = (boolean) jsonObj.get("isTakeOut").getAsBoolean();
		JsonArray data = jsonObj.getAsJsonArray("data");
		
		Connection conn = null;
		try {
			conn = DriverManager.getConnection("jdbc:mysql://106.13.201.225:3306/coffee?serverTimezone=Asia/Shanghai","coffee","TklRpGi1");
			
			String addOrderMealSql = "INSERT INTO meal_order(mealId, orderId, amount, price) VALUES(?, ?, ?, ?);";
			String selectMealSql = "SELECT * FROM meal Where mealId = ?;";
			String addOrderSql = "INSERT INTO orders(orderId, userId, addrId, isTakeOut, remark,"
					+ "payment, packingCharges, deliveryFee) VALUES(?, ?, ?, ?, ?, ?, ?, ?);";
			PreparedStatement selectMealPs = conn.prepareStatement(selectMealSql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
			PreparedStatement addOrderMealPs = conn.prepareStatement(addOrderMealSql);
			PreparedStatement assOrderPs = conn.prepareStatement(addOrderSql);
			
			/* 创建订单 1/2 */
			assOrderPs.setString(1, orderId);
			assOrderPs.setString(2, userId);
			assOrderPs.setString(3, addrId);
			assOrderPs.setBoolean(4, isTakeOut);
			assOrderPs.setString(5, remark);
			assOrderPs.setString(6, payment);
			assOrderPs.setFloat(7, packingCharges);
			assOrderPs.setFloat(8, deliveryFee);
			assOrderPs.executeUpdate();
			
			float totalPrice = 0;

			for(JsonElement item :data) {
				JsonObject itemObj = item.getAsJsonObject();
				/* 设置订单餐品对应 */
				selectMealPs.setString(1, itemObj.get("mealId").getAsString());
				ResultSet selectMealRs = selectMealPs.executeQuery();
				selectMealRs.next();
				float price = selectMealRs.getFloat("price");
				addOrderMealPs.setString(1, itemObj.get("mealId").getAsString());
				addOrderMealPs.setString(2, orderId);
				addOrderMealPs.setInt(3, itemObj.get("amount").getAsInt());
				addOrderMealPs.setFloat(4, price);
				addOrderMealPs.executeUpdate();
				totalPrice += price * itemObj.get("amount").getAsInt();
				/* 减少库存 */
				int newAmount = selectMealRs.getInt("amount") - itemObj.get("amount").getAsInt();
				if(newAmount>=0) {
					selectMealRs.updateInt("amount", newAmount);
					selectMealRs.updateRow();
				}
				else {
					responseJson.addProperty("success", false);
					responseJson.addProperty("msg", "餐点：" + itemObj.get("mealId").getAsString() + "的余量不足");
					out.print(responseJson.toString());
					return;
				}
				selectMealRs.close();
			}
			/* 创建订单 2/2 */
			String setTotalPriceSql = "Update orders set totalPrice = ? Where orderId = ?;";
			PreparedStatement setTotalPricePs = conn.prepareStatement(setTotalPriceSql);
			setTotalPricePs.setFloat(1, totalPrice + packingCharges + deliveryFee);
			setTotalPricePs.setString(2, orderId);
			setTotalPricePs.executeUpdate();
			
			setTotalPricePs.close();
			selectMealPs.close();
			assOrderPs.close();
			addOrderMealPs.close();
			
			responseJson.addProperty("success", true);
			out.print(responseJson.toString());
		} catch(SQLException e) {
			responseJson.addProperty("success", false);
			responseJson.addProperty("msg", e.getMessage());
			out.print(responseJson.toString());
			try {
				conn.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

}
