package servlet.menu;


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

import net.sf.json.JSONObject;

/**
 * Servlet implementation class getUserInfo
 */
@WebServlet("/api/menu/getMealByMenuId")
public class GetMealByMenuId extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetMealByMenuId() {
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
				String menuId = jsonObj.getString("menuId");
				String sql = "select * from meal where menuId= ?";
				PreparedStatement ps = conn.prepareStatement(sql);
				ps.setString(1, menuId);
				ResultSet rs = ps.executeQuery();
				JSONObject jsonobj = new JSONObject();
				while(rs.next()){
					jsonobj.put("mealId",rs.getString("mealId")==null?"":rs.getString("mealId"));
					jsonobj.put("price",rs.getObject("price")==null?"":rs.getDouble("price"));
					jsonobj.put("amount",rs.getObject("amount")==null?"":rs.getInt("amount"));
					jsonobj.put("menuId",rs.getString("menuId")==null?"":rs.getString("menuId"));
					jsonobj.put("type",rs.getString("type")==null?"":rs.getString("type"));
				}
				if(jsonobj.isEmpty()) {
					jsonobj.put("success", false);
					jsonobj.put("msg", "ªÒ»° ß∞‹");
				}
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
