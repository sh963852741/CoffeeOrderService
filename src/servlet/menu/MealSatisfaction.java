package servlet.menu;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
			
			/*用来记录mealId,mealName对应关系*/
			Map<String,String>meals = new HashMap<String,String>();
			/*用来记录mealId,订餐amount*/
			Map<String,Integer>mealAmounts = new HashMap<String,Integer>();
			
			/* 构建SQL语句  */
			String sql1 = "select mealId,amount from meal_order;";
			String sql2 = "select mealId,mealName from meal where mealId=?;";
			PreparedStatement ps2 = conn.prepareStatement(sql2);
			
			/*执行第一个sql*/
			ResultSet rs = stmt.executeQuery(sql1);
						
			while(rs.next()){
				String mealId = rs.getString("mealId");
				int amount = rs.getInt("amount");
				if(mealAmounts.containsKey(mealId)) {
					mealAmounts.put(mealId, mealAmounts.get(mealId)+amount);
				}
				else mealAmounts.put(mealId, amount);
				ps2.setString(1, mealId);
				ResultSet rs2 = ps2.executeQuery();
				rs2.next();
				String mealName = rs2.getString("mealName");
				meals.put(mealId, mealName);				
			}
			/*从大到小排序*/
			List<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(mealAmounts.entrySet()); //转换为list
			list.sort(new Comparator<Map.Entry<String, Integer>>() {
	            @Override
	            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
	                return o2.getValue().compareTo(o1.getValue());
	            }
	        });
			/* 处理执行结果 */
			JsonObject responseJson1 = new JsonObject();
			JsonArray jsonarray1 = new JsonArray();
			for(Map.Entry<String,Integer> e : list) {
				JsonObject jsonobj = new JsonObject();
				jsonobj.addProperty("mealName", meals.get(e.getKey()));
				jsonobj.addProperty("totalAmount", e.getValue());
				jsonarray1.add(jsonobj);
			}
			/*排序方式倒序*/
			responseJson1.addProperty("Order","Descending");
			responseJson1.add("data", jsonarray1);		
			rs.close();
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
