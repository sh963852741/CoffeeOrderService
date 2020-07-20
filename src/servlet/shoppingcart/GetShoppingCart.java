package servlet.shoppingcart;


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
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Servlet implementation class getUserInfo
 */
@WebServlet("/api/shoppingcart/getShoppingCart")
public class GetShoppingCart extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetShoppingCart() {
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
		PrintWriter out = response.getWriter();
		Connection conn = null;
		HttpSession session = request.getSession();
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
				String userId = (String)session.getAttribute("userId");
				String sql = "select * from user_meal where userId= ?";
				PreparedStatement ps = conn.prepareStatement(sql);
				ps.setString(1, userId);
				ResultSet rs = ps.executeQuery();
				JSONObject jsonobj = new JSONObject();
				JSONObject jsonobj2 = new JSONObject();
				JSONArray jsonarray = new JSONArray();
				while(rs.next()){
					String sql_next = "select * from meal where mealId= ?";
					PreparedStatement ps_next = conn.prepareStatement(sql_next);
					ps_next.setString(1,rs.getString("mealId"));
					ResultSet rs_next = ps_next.executeQuery();
					while(rs_next.next())
					{
						jsonobj2.put("mealName",rs_next.getString("mealName")==null?"":rs_next.getString("mealName"));
						jsonobj2.put("mealId",rs.getString("mealId")==null?"":rs.getString("mealId"));
						jsonobj2.put("userId",rs.getString("userId")==null?"":rs.getString("userId"));
						jsonobj2.put("quality",rs.getObject("quality")==null?"":rs.getInt("quality"));				
						jsonobj2.put("price",rs.getObject("price")==null?"":rs.getInt("price"));
						jsonarray.add(jsonobj2);
					}
				}
				jsonobj.put("success", true);
				jsonobj.put("msg", "操作成功");
				jsonobj.put("data", jsonarray);
				out = response.getWriter();
				out.println(jsonobj);
				rs.close();
				stmt.close();
				conn.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

}
