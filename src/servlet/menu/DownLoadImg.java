package servlet.menu;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class DownLoadImg
 */
@WebServlet("/api/menu/downloadImg")
public class DownLoadImg extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DownLoadImg() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		/* 设置响应头部 */
    	response.setCharacterEncoding("UTF-8");
		response.setContentType("image/png; charset=utf-8");
		ServletOutputStream out = response.getOutputStream();
		
		/* 读取请求内容 */
		request.setCharacterEncoding("UTF-8");
		
		String mealId = request.getParameter("mealId");
		
		Connection conn = null;
		Statement stmt = null;
		try {
			/* 连接数据库 */
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://106.13.201.225:3306/coffee?serverTimezone=GMT", "coffee", "TklRpGi1");
			stmt = conn.createStatement();
			
			/* 构建SQL语句 */
			String sql = "SELECT pictureUrl FROM meal WHERE mealId=?;";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, mealId);
			
			/* 执行SQL语句 */
			ResultSet rs = ps.executeQuery();
			
			/* 处理执行结果 */
			rs.next();
			String imgPath = rs.getString("pictureUrl");
			rs.close();
			
			/* 读取文件 */

			if(imgPath == null) {
				response.sendError(404);
				return;
			}
			FileInputStream picture = new FileInputStream(imgPath);
			out.write(picture.readAllBytes());
			picture.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
			/* 处理执行结果 */
			out.print(e.getMessage());
			try {
				conn.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} catch (ClassNotFoundException e) {
			e.fillInStackTrace();
		} finally {
			/* 无论如何关闭连接 */
			try {
				stmt.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		out.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setCharacterEncoding("UTF-8");
    	response.setHeader("Allow", "GET");
    	response.sendError(405);
	}

}
