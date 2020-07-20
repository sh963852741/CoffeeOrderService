package servlet.order;

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

import net.sf.json.JSONObject;

/**
 * Servlet implementation class FinishOrder
 */
@WebServlet("/api/ordermanage/finishOrder")
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
		PrintWriter out=response.getWriter();
		Connection conn=null;
		Statement stmt = null;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn=DriverManager.getConnection("jdbc:mysql://106.13.201.225:3306/coffee?useSSL=false&serverTimezone=GMT","coffee","TklRpGi1");
			stmt=conn.createStatement();
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
				/*以创建订单时返回的orderId为传入参数*/
				String orderId = jsonObj.getString("orderId");
				String sql="update orders SET status ='已完成' where orderId= ?";
				PreparedStatement ps=conn.prepareStatement(sql);
				ps.setString(1, orderId);
				ps.executeUpdate();
				JSONObject jsonobj=new JSONObject();
				jsonobj.put("success", true);
				jsonobj.put("msg","订单状态修改成功");
				out=response.getWriter();
				out.println(jsonobj);
				} catch (IOException e) {
				e.printStackTrace();
				}
			}catch(SQLException e) {
				e.printStackTrace();
				JSONObject responseJson = new JSONObject();
				responseJson.put("success",false);
				responseJson.put("msg", e.getMessage());
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
					stmt.close();
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

}
