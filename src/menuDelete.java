

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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
/**
 * Servlet implementation class delete
 */

@WebServlet("/api/menu/menuDelete")
public class menuDelete extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public menuDelete() {
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
			if(str.isEmpty()) return;
			JSONObject jsonObj = JSONObject.fromObject(str);
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://106.13.201.225:3306/coffee?useSSL=false&serverTimezone=GMT","coffee","TklRpGi1");
			Statement stmt = conn.createStatement();
			String mealId = jsonObj.getString("mealId");
			Double price = jsonObj.getDouble("price");
			int amount = jsonObj.getInt("amount");
			String menuId = jsonObj.getString("menuId");
			String type = jsonObj.getString("type");
			String sql = "delete from meal where mealId=? and price=? and amount=? and menuId=? and type=?";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, mealId);
			ps.setDouble(2, price);
			ps.setInt(3, amount);
			ps.setString(4, menuId);
			ps.setString(5, type);
			try {
				int rowCount = ps.executeUpdate();
				JSONObject jsonobj = new JSONObject();
				if(rowCount>0){
					jsonobj.put("success",true);
					jsonobj.put("msg","É¾³ý³É¹¦");
				}
				out = response.getWriter();
				out.println(jsonobj);
				stmt.close();
				conn.close();
			}
			catch(Exception e) {
				JSONObject jsonobj = new JSONObject();
				jsonobj.put("success",false);
				jsonobj.put("msg","É¾³ýÊ§°Ü");
				out = response.getWriter();
				out.println(jsonobj);
				stmt.close();
				conn.close();
			}
		} catch (SQLException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}