package servlet.rbac;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

/**
 * Servlet implementation class GetUserState
 */
@WebServlet("/api/usermanage/getUserState")
public class GetUserState extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetUserState() {
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
		HttpServletRequest req = (HttpServletRequest)request;
		HttpSession session = req.getSession();
		ServletInputStream is;
		is = request.getInputStream();
		int nRead = 1;
		int nTotalRead = 0;
		byte[] bytes = new byte[10240];
		try {
			while (nRead > 0) {
				nRead = is.read(bytes, nTotalRead, bytes.length - nTotalRead);
				if (nRead > 0)
					nTotalRead = nTotalRead + nRead;
			}
			String str = new String(bytes, 0, nTotalRead, "utf-8");
			JSONObject jsonObj = JSONObject.fromObject(str);
			JSONObject jsonObj2 = new JSONObject();
			String sessionId = jsonObj.getString("sessionId");
			if(sessionId.compareTo(session.getId())==0) {
				String userId = (String) session.getAttribute("userId");
				jsonObj2.put("userId",userId);
				jsonObj2.put("success",true);
			}
			else {
				jsonObj2.put("msg","sessionId错误");
				jsonObj2.put("success",false);
			}
			PrintWriter out = response.getWriter();
			out.println(jsonObj2);
			out.close();
		}
		catch(Exception e) {
			
		}
	}

}
