package sec02.ex01;

import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class MemberController
 */
/*@WebServlet("/member/*")*/	//브라우저에서 요청 시 두 단계로 요청이 이루어짐
public class MemberController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	MemberDAO memberDAO;

	public void init() throws ServletException {
		memberDAO = new MemberDAO();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doHandle(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doHandle(request, response);
	}

	private void doHandle(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String nextPage = null;
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		String action = request.getPathInfo();  //URL에서 요청명을 가져옴
		System.out.println("action:" + action);
		if (action == null || action.equals("/listMembers.do")) { //최초 요청이거나 action 값이 /memberList.do 면 회원 목록을 출력
			List<MemberVO> membersList = memberDAO.listMembers();
			request.setAttribute("membersList", membersList);
			nextPage = "/test02/listMembers.jsp";	//test02폴더의 listMember.jsp로 포워딩
		} else if (action.equals("/addMember.do")) {  //action 값이 /addMember.do면 전송된 회원 정보를 가져와서 테이블에 추가
			String id = request.getParameter("id");
			String pwd = request.getParameter("pwd");
			String name = request.getParameter("name");
			String email = request.getParameter("email");
			MemberVO memberVO = new MemberVO(id, pwd, name, email);
			memberDAO.addMember(memberVO);
			nextPage = "/member/listMembers.do";	//회원 등록 후 다시 회원 목록을 출력

		} else if (action.equals("/memberForm.do")) {	//action 값이 /memberForm.do면 회원 가입창을 화면에 출력
			nextPage = "/test02/memberForm.jsp";  //test02폴더의 memberForm.jsp로 포워딩
		} else {	//그 외 다른 action값은 회원 목록을 출력
			List<MemberVO> membersList = memberDAO.listMembers();
			request.setAttribute("membersList", membersList);
			nextPage = "/test02/listMembers.jsp";
		}
		RequestDispatcher dispatch = request.getRequestDispatcher(nextPage); //nextPage에 지정한 요청명으로 다시 서블릿에 요청
		dispatch.forward(request, response);
	}

}