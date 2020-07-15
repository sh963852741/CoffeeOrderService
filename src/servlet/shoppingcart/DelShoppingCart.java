package servlet.shoppingcart;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

/**
 * Servlet implementation class regist
 */
@WebServlet("/api/shoppingcart/delShoppingCart")
public class DelShoppingCart extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DelShoppingCart() {
        super();
        // TODO Auto-generated constructor stub
       
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    @Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	response.setCharacterEncoding("UTF-8");
    	response.setHeader("Allow", "POST");
    	response.sendError(405);
	}
    
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
    @Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	/* ������Ӧͷ�� */
    	response.setCharacterEncoding("UTF-8");
		response.setContentType("text/json; charset=utf-8");
		PrintWriter out = response.getWriter();
		HttpSession session = request.getSession();
		/* ��ȡ�������� */
		request.setCharacterEncoding("UTF-8");
		BufferedReader reader = request.getReader();
		String msg = null;
		StringBuilder message= new StringBuilder();
		while ((msg = reader.readLine()) != null){			
			message.append(msg);
		}		
		String jsonStr = message.toString();
		
		/* ������������Ϊ�յ���� */
		if(jsonStr.isEmpty()) 
		{
			response.sendError(400);
			return;
		}
		
		/* ����JSON��ȡ���� */
		JSONObject jsonObj = JSONObject.fromObject(jsonStr);
		String mealId = jsonObj.getString("mealId");
		String userId = (String) session.getAttribute("userId");
		Connection conn = null;
		Statement stmt = null;
		try {
			/* �������ݿ� */
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection
				("jdbc:mysql://106.13.201.225:3306/coffee?useSSL=false&serverTimezone=GMT", "coffee", "TklRpGi1");
			stmt = conn.createStatement();
			
			/* ����SQL���  */
			String sql = "delete from user_meal where mealId=? and userId=?";
			PreparedStatement ps = conn.prepareStatement(sql);
			
			ps.setString(1, mealId);
			ps.setString(2, userId);
			
			/* ִ��SQL���  */
			ps.executeUpdate();
			
			/* ����ִ�н�� */
			JSONObject responseJson = new JSONObject();
			responseJson.put("success", true);
			responseJson.put("msg","操作成功");
			out.println(responseJson);
		} catch (SQLException e) {
			e.printStackTrace();
			/* ����ִ�н�� */
			JSONObject responseJson = new JSONObject();
			responseJson.put("success",false);
			responseJson.put("msg", e.getMessage());
			out.println(responseJson);
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} catch (ClassNotFoundException e) {
			e.fillInStackTrace();
		} finally {
			/* ������ιر����� */
			try {
				stmt.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}	
	}
}