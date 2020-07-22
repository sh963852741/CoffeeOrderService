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
		response.setCharacterEncoding("UTF-8");
    	response.setHeader("Allow", "POST");
    	response.sendError(405);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
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
			conn = DriverManager.getConnection("jdbc:mysql://106.13.201.225:3306/coffee?serverTimezone=Asia/Shanghai","coffee","TklRpGi1");
			String countSql = "Select count(*) as total From orders;";
			String orderDetailSql = "SELECT * FROM coffee.orders Natural Join coffee.user Limit ?, ?;";
			String orderPriceSql = "Select sum(amount*price) as totalPrice from meal_order where orderId=?;";
			PreparedStatement countPs = conn.prepareStatement(countSql);
			PreparedStatement orderDetailPs = conn.prepareStatement(orderDetailSql);
			PreparedStatement orderPricePs = conn.prepareStatement(orderPriceSql);
			orderDetailPs.setInt(1, (page - 1) * pageSize);
			orderDetailPs.setInt(2, pageSize);
			
			/* 获取总页数 */
			JsonObject responseJson = new JsonObject();
			ResultSet countRs = countPs.executeQuery();
			countRs.next();
			responseJson.addProperty("totalRows", countRs.getInt("total"));
			responseJson.addProperty("page", page);
			responseJson.addProperty("pageSize", pageSize);
			countRs.close();
			
			ResultSet rs = orderDetailPs.executeQuery();
			JsonArray  jsonarray = new JsonArray();
			while(rs.next()){
				JsonObject jsonobj = new JsonObject();
				jsonobj.addProperty("userName",rs.getString("userName"));
				jsonobj.addProperty("createdTime",rs.getString("createdTime"));
				jsonobj.addProperty("orderId",rs.getString("orderId"));
				jsonobj.addProperty("userId",rs.getString("userId"));
				jsonobj.addProperty("isTakeOut",rs.getBoolean("isTakeOut"));
				/* 获取总价 */
				orderPricePs.setString(1, rs.getString("orderId"));
				ResultSet rs3 = orderPricePs.executeQuery();
				rs3.next();
				jsonobj.addProperty("totalPrice", rs3.getDouble("totalPrice"));
				rs3.close();
				
				jsonarray.add(jsonobj);
			}
			rs.close();
			
			responseJson.addProperty("success", true);
			responseJson.addProperty("msg", "操作成功");
			responseJson.add("data",jsonarray);
			out = response.getWriter();
			out.print(responseJson);
			conn.close();
		} catch (SQLException | ClassNotFoundException e) {
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
