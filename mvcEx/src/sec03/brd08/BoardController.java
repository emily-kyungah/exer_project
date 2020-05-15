package sec03.brd08;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;

/**
 * Servlet implementation class BoardController
 */
@WebServlet("/board/*")
public class BoardController extends HttpServlet {
	private static String ARTICLE_IMAGE_REPO = "D:\\board\\article_image";	//클래스에 첨부한 이미지 저장 위치를 상수로 선언
	BoardService boardService;
	ArticleVO articleVO;
 
	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		boardService = new BoardService();	//서블릿 초기화 시 BoardService 객체 생성
		articleVO = new ArticleVO();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)  throws ServletException, IOException {
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
		String nextPage = "";
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html; charset=utf-8");
		HttpSession session;  //답글에 대한 부모글번호를 저장하기 위해 세션 사용
		String action = request.getPathInfo();	//요청명 가져옴
		System.out.println("action:" + action);
		try {
			List<ArticleVO> articlesList = new ArrayList<ArticleVO>();
			if (action==null){	//최초요청시
				String _section=request.getParameter("section");
				String _pageNum=request.getParameter("pageNum");
				//section과 pageNum없으면 각 1로 초기화
				int section = Integer.parseInt(((_section==null)? "1":_section) );  
				int pageNum = Integer.parseInt(((_pageNum==null)? "1":_pageNum));
				//hashmap에 저장한 후 메소드로 넘김
				Map<String, Integer> pagingMap = new HashMap<String, Integer>();
				pagingMap.put("section", section);
				pagingMap.put("pageNum", pageNum);
				Map articlesMap=boardService.listArticles(pagingMap);
				articlesMap.put("section", section);
				articlesMap.put("pageNum", pageNum);
				request.setAttribute("articlesMap", articlesMap);
				nextPage = "/board07/ listArticles.jsp";
				}else if(action.equals("/listArticles.do")){  //action 값이 listArticles.do면 전체 글 조회		
				//글 목록에서 명시적으로 페이지 번호를 눌러서 요청한 경우 section값과 pagenum 값을 가져옴
				String _section=request.getParameter("section");
				String _pageNum=request.getParameter("pageNum");
				int section = Integer.parseInt(((_section==null)? "1":_section) );
				int pageNum = Integer.parseInt(((_pageNum==null)? "1":_pageNum));
				Map pagingMap=new HashMap();
				pagingMap.put("section", section);
				pagingMap.put("pageNum", pageNum);
				Map articlesMap=boardService.listArticles(pagingMap);
				articlesMap.put("section", section);
				articlesMap.put("pageNum", pageNum);
				request.setAttribute("articlesMap", articlesMap);
				nextPage = "/board07/listArticles.jsp";
			} else if (action.equals("/articleForm.do")) {	//articleForm.do로 요청시 글쓰기창이 나타남
				nextPage = "/board07/articleForm.jsp";
			} else if (action.equals("/addArticle.do")) {
				int articleNO = 0;
				Map<String, String> articleMap = upload(request, response);	//파일업로드기능 사용을 위해 upload로 요청 전달
				//articleMap에 저장된 글 정보를 다시 가져옴
				String title = articleMap.get("title");		
				String content = articleMap.get("content");
				String imageFileName = articleMap.get("imageFileName");
				//글쓰기창에서 입력된 정보를 ArticleVO 객체에 설정한 후 addArticle()로 전달
				articleVO.setParentNO(0); //새 글의 부모 글 번호를 0으로 설정
				articleVO.setId("emily"); //계정정보 설정
				articleVO.setTitle(title);
				articleVO.setContent(content);
				articleVO.setImageFileName(imageFileName);
				articleNO = boardService.addArticle(articleVO); //새 글에 대한 글 번호 가져오기
				if (imageFileName != null && imageFileName.length() != 0) { //파일을 첨부한 경우에만 수행
					//temp 폴더에 임시로 업로드 된 파일 객체를 생성
					File srcFile = new File(ARTICLE_IMAGE_REPO + "\\" + "temp" + "\\" + imageFileName);
					File destDir = new File(ARTICLE_IMAGE_REPO + "\\" + articleNO);
					destDir.mkdirs();//글 번호로 폴더 생성
					//temp폴더의 파일을 글 번호를 이름으로 하는 폴더로 이동
					FileUtils.moveFileToDirectory(srcFile, destDir, true);
				}
				PrintWriter pw = response.getWriter();
				pw.print("<script>" + "  alert('새 글을 추가했습니다.');" + " location.href='" + request.getContextPath()
						+ "/board/listArticles.do';" + "</script>");

				return;
			} else if (action.equals("/viewArticle.do")) {
				String articleNO = request.getParameter("articleNO"); //상세보기 요청시 articleNo 값 가져옴
				articleVO = boardService.viewArticle(Integer.parseInt(articleNO));
				request.setAttribute("article", articleVO); //article속성으로 글 정보를 바인딩
				nextPage = "/board07/viewArticle.jsp";
			} else if (action.equals("/modArticle.do")) { //수정하기 인 경우
				Map<String, String> articleMap = upload(request, response); //upload 메소드를 이용, 수정된 데이터를 map에 저장하고 반환
				int articleNO = Integer.parseInt(articleMap.get("articleNO"));
				articleVO.setArticleNO(articleNO);
				String title = articleMap.get("title");
				String content = articleMap.get("content");
				String imageFileName = articleMap.get("imageFileName");
				articleVO.setParentNO(0);
				articleVO.setId("emily");
				articleVO.setTitle(title);
				articleVO.setContent(content);
				articleVO.setImageFileName(imageFileName);
				boardService.modArticle(articleVO); //전송된 글 정보를 이용해 글을 수정
				if (imageFileName != null && imageFileName.length() != 0) {
					String originalFileName = articleMap.get("originalFileName");
					File srcFile = new File(ARTICLE_IMAGE_REPO + "\\" + "temp" + "\\" + imageFileName);
					File destDir = new File(ARTICLE_IMAGE_REPO + "\\" + articleNO);
					destDir.mkdirs();
					FileUtils.moveFileToDirectory(srcFile, destDir, true);
					
					File oldFile = new File(ARTICLE_IMAGE_REPO + "\\" + articleNO + "\\" + originalFileName);
					oldFile.delete();  //기존 파일 삭제
				}
				PrintWriter pw = response.getWriter();
				pw.print("<script>" + "  alert('글을 수정하였습니다.');" + " location.href='" + request.getContextPath()
						+ "/board/viewArticle.do?articleNO=" + articleNO + "';" + "</script>");
				return;
			} else if (action.equals("/removeArticle.do")) { //게시글 삭제
				int articleNO = Integer.parseInt(request.getParameter("articleNO"));
				List<Integer> articleNOList = boardService.removeArticle(articleNO); //articleNo값에 대한 글을 삭제한 후 삭제된 부모그로가 자식글의 articleNo목록 가져옴
				for (int _articleNO : articleNOList) { //삭제된 글들의 이미지 저장폴더 삭제
					File imgDir = new File(ARTICLE_IMAGE_REPO + "\\" + _articleNO);
					if (imgDir.exists()) {
						FileUtils.deleteDirectory(imgDir);
					}
				}

				PrintWriter pw = response.getWriter();
				pw.print("<script>" + "  alert('글을 삭제하였습니다.');" + " location.href='" + request.getContextPath()
						+ "/board/listArticles.do';" + "</script>");
				return;

			} else if (action.equals("/replyForm.do")) { //답글 작성 구현
				int parentNO = Integer.parseInt(request.getParameter("parentNO"));
				session = request.getSession();
				session.setAttribute("parentNO", parentNO);
				nextPage = "/board07/replyForm.jsp";
			} else if (action.equals("/addReply.do")) { //답글 전송
				session = request.getSession();
				int parentNO = (Integer) session.getAttribute("parentNO");
				session.removeAttribute("parentNO");
				Map<String, String> articleMap = upload(request, response);
				String title = articleMap.get("title");
				String content = articleMap.get("content");
				String imageFileName = articleMap.get("imageFileName");
				articleVO.setParentNO(parentNO);
				articleVO.setId("choco");
				articleVO.setTitle(title);
				articleVO.setContent(content);
				articleVO.setImageFileName(imageFileName);
				int articleNO = boardService.addReply(articleVO);
				if (imageFileName != null && imageFileName.length() != 0) {
					File srcFile = new File(ARTICLE_IMAGE_REPO + "\\" + "temp" + "\\" + imageFileName);
					File destDir = new File(ARTICLE_IMAGE_REPO + "\\" + articleNO);
					destDir.mkdirs();
					FileUtils.moveFileToDirectory(srcFile, destDir, true);
				}
				PrintWriter pw = response.getWriter();
				pw.print("<script>" + "  alert('답글을 추가했습니다.');" + " location.href='" + request.getContextPath()
						+ "/board/viewArticle.do?articleNO="+articleNO+"';" + "</script>");
				return;
			}

			RequestDispatcher dispatch = request.getRequestDispatcher(nextPage);
			dispatch.forward(request, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Map<String, String> upload(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Map<String, String> articleMap = new HashMap<String, String>();
		String encoding = "utf-8";
		File currentDirPath = new File(ARTICLE_IMAGE_REPO); //글 이미지 저장 폴더에 대해 파일 객체 생성
		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setRepository(currentDirPath);
		factory.setSizeThreshold(1024 * 1024);
		ServletFileUpload upload = new ServletFileUpload(factory);
		try {
			List items = upload.parseRequest(request);
			for (int i = 0; i < items.size(); i++) {
				FileItem fileItem = (FileItem) items.get(i);
				if (fileItem.isFormField()) {
					System.out.println(fileItem.getFieldName() + "=" + fileItem.getString(encoding));
					//파일 업로드로 같이 전송된 새 글 관련 매개변수를 Map에 저장한 후 반환하고, 새 글과 관련된 title, content를 Map에 저장
					articleMap.put(fileItem.getFieldName(), fileItem.getString(encoding));
				} else {
					System.out.println("파라미터이름:" + fileItem.getFieldName());
					System.out.println("파일이름:" + fileItem.getName());
					System.out.println("파일크기:" + fileItem.getSize() + "bytes");
					
					//articleMap.put(fileItem.getFieldName(), fileItem.getName());
					//업로드한 파일이 존재하는 경우 업로드한 파일의 파일 이름으로 저장소에 업로드
					if (fileItem.getSize() > 0) {
						int idx = fileItem.getName().lastIndexOf("\\");
						if (idx == -1) {
							idx = fileItem.getName().lastIndexOf("/");
						}

						String fileName = fileItem.getName().substring(idx + 1);
						System.out.println("���ϸ�:" + fileName);
								articleMap.put(fileItem.getFieldName(), fileName);  //�ͽ��÷η����� ���ε� ������ ��� ���� �� map�� ���ϸ� ����);
						File uploadFile = new File(currentDirPath + "\\temp\\" + fileName); //첨부한 파일을 먼저 temp폴더에 업로드
						fileItem.write(uploadFile);

					} // end if
				} // end if
			} // end for
		} catch (Exception e) {
			e.printStackTrace();
		}
		return articleMap;
	}

}
