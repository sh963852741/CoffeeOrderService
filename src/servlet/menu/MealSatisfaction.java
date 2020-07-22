package servlet.menu;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Servlet implementation class MealSatisfaction
 */
@WebServlet("/api/menu/MealSatisfaction")
public class MealSatisfaction extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MealSatisfaction() {
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
		/* 设置响应头部 */
    	response.setCharacterEncoding("UTF-8");
		response.setContentType("text/json; charset=utf-8");
		PrintWriter out = response.getWriter();
		Connection conn = null;
		try {
			/* 连接数据库 */
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://106.13.201.225:3306/coffee?serverTimezone=GMT", "coffee", "TklRpGi1");
			Statement stmt = conn.createStatement();
			
			/* 构建SQL语句  */
			String sql2 = "select type,COUNT(*) AS amount" + 
					" FROM meal_order, meal, orders WHERE meal_order.mealId = meal.mealId "
					+ "and meal_order.orderId = orders.orderId and createdTime BETWEEN DATE_SUB(NOW(), INTERVAL 1 MONTH) AND NOW()"
					+ " GROUP BY type ORDER BY amount DESC;";
			/*过去一个月订单量变化趋势*/
			String sql3 = "select COUNT(*) AS amount, DATE_FORMAT(createdTime, '%d') AS time FROM orders where"
					+ " createdTime BETWEEN DATE_SUB(NOW(), INTERVAL 1 MONTH) AND NOW()"
					+ " GROUP BY DATE(createdTime) ORDER BY DATE(createdTime);";
			/*过去一个月支付方式统计*/
			String sql4 = "select payment,COUNT(*) AS amount, DATE(createdTime) AS time FROM orders"
					+ " WHERE createdTime BETWEEN DATE_SUB(NOW(), INTERVAL 1 MONTH) AND NOW()" + 
					" GROUP BY payment ORDER BY payment DESC";
			/*各餐点数量,降序*/
			String sql5 = "select meal_order.mealId, mealName, COUNT(*) AS amount" + 
					" FROM meal_order join meal on meal_order.mealId = meal.mealId GROUP BY mealId" + 
					" ORDER BY amount DESC;";
			/*过去一个月订单量Top5*/
			String sql6 = "select COUNT(*) AS amount, DATE(createdTime) AS time FROM orders"
					+ " WHERE createdTime BETWEEN DATE_SUB(NOW(), INTERVAL 1 MONTH) AND NOW()"
					+ " GROUP BY DATE(createdTime) ORDER BY amount DESC LIMIT 10;";
			/*过去一个月销售额*/
			String sql7 ="select Sum(totalPrice) as money FROM orders"
					+ " WHERE createdTime BETWEEN DATE_SUB(NOW(), INTERVAL 1 MONTH) AND NOW()";
			/*处理数据*/
			int totalOrders = 0;
			ResultSet rs2 = stmt.executeQuery(sql2);
			JsonArray jsonarray2 = new JsonArray();
			while(rs2.next()) {
				int amount = rs2.getInt("amount");
				String type = rs2.getString("type");
				JsonObject jsonobj =new JsonObject();
				jsonobj.addProperty("amount", amount);
				jsonobj.addProperty("type", type);
				jsonarray2.add(jsonobj);
			}
			ResultSet rs3 = stmt.executeQuery(sql3);
			JsonArray jsonarray31 = new JsonArray();
			JsonArray jsonarray32 = new JsonArray();
			while(rs3.next()) {
				int amount = rs3.getInt("amount");
				totalOrders+=amount;
				String time = rs3.getString("time");
				jsonarray31.add(amount);
				jsonarray32.add(time);
			}
			ResultSet rs4 = stmt.executeQuery(sql4);
			JsonArray jsonarray4 = new JsonArray();
			while(rs4.next()) {
				JsonObject jsonobj = new JsonObject();
				int amount = rs4.getInt("amount");
				String payment = rs4.getString("payment");
				Date time = rs4.getDate("time");
				jsonobj.addProperty("value", amount);
				jsonobj.addProperty("name", payment);
				jsonobj.addProperty("date", time.toString());
				jsonarray4.add(jsonobj);
			}
			ResultSet rs5 = stmt.executeQuery(sql5);
			JsonArray jsonarray5 = new JsonArray();
			while(rs5.next()) {
				JsonObject jsonobj = new JsonObject();
				int amount = rs5.getInt("amount");
				String mealId = rs5.getString("mealId");
				String mealName = rs5.getString("mealName");
				jsonobj.addProperty("value", amount);
				jsonobj.addProperty("mealId", mealId);
				jsonobj.addProperty("name", mealName);
				jsonarray5.add(jsonobj);
			}
			ResultSet rs6 = stmt.executeQuery(sql6);
			JsonArray jsonarray6 = new JsonArray();
			while(rs6.next()) {
				JsonObject jsonobj = new JsonObject();
				int amount = rs6.getInt("amount");
				Date time = rs6.getDate("time");
				jsonobj.addProperty("amount", amount);
				jsonobj.addProperty("date", time.toString());
				jsonarray6.add(jsonobj);
			}
			ResultSet rs7 = stmt.executeQuery(sql7);
			rs7.next();
			Double moneyPerMonth = rs7.getDouble("money");
			/* 处理执行结果 */
			JsonObject responseJson1 = new JsonObject();
			responseJson1.addProperty("totalOrders",totalOrders);
			responseJson1.add("typeList", jsonarray2);
			responseJson1.add("OrderChangeListX", jsonarray32);
			responseJson1.add("OrderChangeListY", jsonarray31);
			responseJson1.add("paymentWay", jsonarray4);
			responseJson1.add("mealList", jsonarray5);
			responseJson1.add("topFiveOrders", jsonarray6);
			responseJson1.addProperty("moneyPerMonth", moneyPerMonth);
			responseJson1.addProperty("success", true);
			out.print(responseJson1);
		} catch (SQLException e) {
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
		} catch (ClassNotFoundException e) {
			e.fillInStackTrace();
		} finally {
			/* 无论如何关闭连接 */
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

}
