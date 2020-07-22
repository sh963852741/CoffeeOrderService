package servlet.order;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.sf.json.JSONObject;

/**
 * Servlet implementation class FinishOrder
 */
@WebServlet("/api/ordermanage/setOrderStatus")
public class FinishOrder extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FinishOrder() {
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
		request.setCharacterEncoding("UTF-8");
		BufferedReader reader = request.getReader();
		JsonObject requestJson = JsonParser.parseReader(reader).getAsJsonObject();
		PrintWriter out=response.getWriter();
		
		Connection conn=null;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn=DriverManager.getConnection("jdbc:mysql://106.13.201.225:3306/coffee?serverTimezone=Asia/Shanghai","coffee","TklRpGi1");
			/*以创建订单时返回的orderId为传入参数*/
			String orderId = requestJson.get("orderId").getAsString();
			String targetStatus = requestJson.get("targetStatus").getAsString();
			String sql="Update orders SET status = ? Where orderId= ?;";
			PreparedStatement ps=conn.prepareStatement(sql);
			ps.setString(1, targetStatus);
			ps.setString(2, orderId);
			ps.executeUpdate();
			ps.close();
			
			JsonObject jsonobj=new JsonObject();
			jsonobj.addProperty("success", true);
			jsonobj.addProperty("msg","订单状态修改成功");
			out.println(jsonobj);
		}catch(SQLException e) {
			e.printStackTrace();
			JsonObject responseJson = new JsonObject();
			responseJson.addProperty("success", false);
			responseJson.addProperty("msg", e.getMessage());
			out.println(responseJson);
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}catch(ClassNotFoundException e) {
			e.printStackTrace();
		}finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
