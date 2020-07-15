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

import net.sf.json.JSONObject;
import net.sf.json.JSONArray;

/**
 * Servlet implementation class GetOrderListByUserId
 */
@WebServlet("/GetOrderListByUserId")
public class GetOrderListByUserId extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetOrderListByUserId() {
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
				is=request.getInputStream();
				int nRead=1;
				int nTotalRead=0;
				byte[] bytes=new byte[10240];
				while(nRead>0) {
					nRead=is.read(bytes,nTotalRead,bytes.length-nTotalRead);
					if(nRead>0)
						nTotalRead+=nRead;
				}
				String str=new String(bytes,0,nTotalRead,"utf-8");
				JSONObject jsonObj=JSONObject.fromObject(str);
				String userId=jsonObj.getString("userId");
				String sql="select * from orders where userId= ?";
				PreparedStatement ps=conn.prepareStatement(sql);
				ps.setString(1,userId);
				ResultSet rs=ps.executeQuery();
				JSONObject jsonobj=new JSONObject();
				JSONObject jsonobj2=new JSONObject();
				JSONArray jsonarray = new JSONArray();
				while(rs.next()) {
					jsonobj2.put("orderId",rs.getString("orderId"));
					jsonobj2.put("createdTime",rs.getString("createdTime"));
					jsonarray.add(jsonobj2);
				}
				if(jsonobj2.isEmpty()) {
					jsonobj.put("success",false);
					jsonobj.put("msg","获取失败");
				}else {
					jsonobj.put("success",true);
					jsonobj.put("msg","获取成功");
				}
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