package servlet.order;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
		PrintWriter out = response.getWriter();
		Connection conn = null;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://106.13.201.225:3306/coffee?useSSL=false&serverTimezone=GMT","coffee","TklRpGi1");
			Statement stmt = conn.createStatement();
			ServletInputStream is;
			try {
				is = request.getInputStream();
				int nRead = 1;
				int nTotalRead = 0;
				byte[] bytes = new byte[10240];
				while (nRead > 0) {
					nRead = is.read(bytes, nTotalRead, bytes.length - nTotalRead);
					if (nRead > 0)
						nTotalRead = nTotalRead + nRead;
				}
				String str = new String(bytes, 0, nTotalRead, "utf-8");
				JSONObject jsonObj = JSONObject.fromObject(str);
				String orderId = jsonObj.getString("orderId");
				String sql = "select mealId, amount, price from meal_order where orderId= ?";
				String sql2 = "select mealName from meal where mealId=?";
				PreparedStatement ps = conn.prepareStatement(sql);
				ps.setString(1, orderId);
				ResultSet rs = ps.executeQuery();
				JSONObject jsonobj = new JSONObject();
				JSONArray  array = new JSONArray();
				while(rs.next()){
					String mealId = rs.getString("mealId");
					int amount = rs.getInt("amount");
					jsonobj.put("amount",amount);
					jsonobj.put("mealId",mealId);
					jsonobj.put("price", rs.getDouble("price"));
					PreparedStatement ps2 = conn.prepareStatement(sql2);
					ps2.setString(1, mealId);
					ResultSet rs2 = ps2.executeQuery();
					rs2.next();
					jsonobj.put("mealName", rs2.getString("mealName"));
					array.add(jsonobj);
				}
				JSONObject jsonobj2 = new JSONObject();
				jsonobj2.put("data", array);
				jsonobj2.put("success", true);
				out = response.getWriter();
				out.println(jsonobj2);
				rs.close();
				stmt.close();
				conn.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
