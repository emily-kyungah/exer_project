package sec02.ex02;

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
@WebServlet("/member/*")
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
	protected void doPost(HttpServletRequest request, HttpServletResponse response)	throws ServletException, IOException {
		doHandle(request, response);
	}

	private void doHandle(HttpServletRequest request, HttpServletResponse response)	throws ServletException, IOException {
		String nextPage = null;
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		String action = request.getPathInfo();
		System.out.println("action:" + action);
		if (action == null || action.equals("/listMembers.do")) {
			List<MemberVO> membersList = memberDAO.listMembers();
			request.setAttribute("membersList", membersList);
			nextPage = "/test03/listMembers.jsp";
		} else if (action.equals("/addMember.do")) {
			String id = request.getParameter("id");
			String pwd = request.getParameter("pwd");
			String name = request.getParameter("name");
			String email = request.getParameter("email");
			MemberVO memberVO = new MemberVO(id, pwd, name, email);
			memberDAO.addMember(memberVO);
			request.setAttribute("msg", "addMember");
			nextPage = "/member/listMembers.do";
		} else if (action.equals("/memberForm.do")) {
			nextPage = "/test03/memberForm.jsp";
		}else if(action.equals("/modMemberForm.do")){ //회원 수정창 요청 시 ID로 회원 정보를 조회한 후 수정창으로 포워딩
		     String id=request.getParameter("id");
		     MemberVO memInfo = memberDAO.findMember(id); //회원 정보 수정창을 요청하면서 전송된 ID를 이용해 수정 전 회원 정보 조회
		     request.setAttribute("memInfo", memInfo);	//request에 바인딩하여 회원 정보 수정창에 수정하기 전 회원 정보를 전달
		     nextPage="/test03/modMemberForm.jsp";
		}else if(action.equals("/modMember.do")){	//테이블의 회원 정보 수정
		     String id=request.getParameter("id");
		     String pwd=request.getParameter("pwd");
		     String name= request.getParameter("name");
	         String email= request.getParameter("email");
	         //전송된 수정회원 정보를 가져온 후 MemberVO 객체 속성에 설정
		     MemberVO memberVO = new MemberVO(id, pwd, name, email);
		     memberDAO.modMember(memberVO);
		     request.setAttribute("msg", "modified"); //회원 목록창으로 수정작업완료 메시지 전달
		     nextPage="/member/listMembers.do";
		}else if(action.equals("/delMember.do")){	//회원ID를 SQL문으로 전달해 테이블의 회원 정보를 삭제
		     String id=request.getParameter("id");
		     memberDAO.delMember(id);
		     request.setAttribute("msg", "deleted"); //회원 목록창으로 삭제 작업 완료 메시지 전달
		     nextPage="/member/listMembers.do";
		}else {
			List<MemberVO> membersList = memberDAO.listMembers();
			request.setAttribute("membersList", membersList);
			nextPage = "/test03/listMembers.jsp";
		}
		RequestDispatcher dispatch = request.getRequestDispatcher(nextPage);
		dispatch.forward(request, response);
	}

}
