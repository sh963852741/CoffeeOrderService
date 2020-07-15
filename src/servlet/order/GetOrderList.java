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
import javax.servlet.ServletInputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Servlet implementation class GetOrderList
 */
@WebServlet("/api/ordermanage/getOrderList")
public class GetOrderList extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetOrderList() {
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
		PrintWriter out=response.getWriter();
		Connection conn=null;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn=DriverManager.getConnection("jdbc:mysql://106.13.201.225:3306/coffee?useSSL=false&serverTimezone=GMT","coffee","TklRpGi1");
			Statement stmt=conn.createStatement();
			ServletInputStream is;
			try {
				String sql="select * from orders";
				String sql2 = "select userName from user where userId=?";
				String sql3 = "select mealId, amount from meal_order where orderId=?";
				String sql4 = "select mealName from meal where mealId=?";
				PreparedStatement ps=conn.prepareStatement(sql);
				ResultSet rs=ps.executeQuery();
				JSONObject jsonobj=new JSONObject();
				JSONObject jsonobj2=new JSONObject();
				JSONArray jsonarray = new JSONArray();
				while(rs.next()) {
					String userId = rs.getString("userId");
					String orderId = rs.getString("orderId");
					PreparedStatement ps2=conn.prepareStatement(sql2);
					ps2.setString(1, userId);
					ResultSet rs2=ps2.executeQuery();
					rs2.next();
					jsonobj2.put("userName",rs2.getString("userName"));
					jsonobj2.put("createdTime",rs.getString("createdTime"));
					JSONArray jsonarray2 = new JSONArray();
					PreparedStatement ps3=conn.prepareStatement(sql3);
					ps3.setString(1, orderId);
					ResultSet rs3=ps3.executeQuery();
					int totalAmount = 0;
					while(rs3.next()) {
						JSONObject jsonobj3 = new JSONObject();
						String mealId = rs3.getString("mealId");
						int amount = rs3.getInt("amount");
						totalAmount+=amount;
						PreparedStatement ps4=conn.prepareStatement(sql4);
						ps4.setString(1, mealId);
						ResultSet rs4=ps4.executeQuery();
						rs4.next();
						String mealName = rs4.getString("mealName");
						jsonobj3.put("amount", amount);
						jsonobj3.put("mealName", mealName);
						jsonarray2.add(jsonobj3);
					}
					jsonobj2.put("totalAmount", totalAmount);
					jsonobj2.put("Orders", jsonarray2);
					jsonarray.add(jsonobj2);
					totalAmount = 0;
				}
				jsonobj.put("success",true);
				jsonobj.put("msg","操作成功");
				jsonobj.put("data",jsonarray);
				out=response.getWriter();
				out.println(jsonobj);
				rs.close();
				stmt.close();
				conn.close();
			}catch(IOException e) {
				e.printStackTrace();
			}
		}catch(SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

}
