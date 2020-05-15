package sec03.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class FileDownloadController
 */
@WebServlet("/download.do")
public class FileDownloadController extends HttpServlet {
	private static String ARTICLE_IMAGE_REPO = "D:\\board\\article_image";

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)	throws ServletException, IOException {
		doHandle(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)	throws ServletException, IOException {
		doHandle(request, response);
	}

	private void doHandle(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html; charset=utf-8");
		String imageFileName = (String) request.getParameter("imageFileName"); //이미지 파일 이름과 글 번호를 가져옴
		String articleNO = request.getParameter("articleNO");
		System.out.println("imageFileName=" + imageFileName);
		OutputStream out = response.getOutputStream();
		String path = ARTICLE_IMAGE_REPO + "\\" + articleNO + "\\" + imageFileName; //글 번호에 대한 파일 경로를 설정
		File imageFile = new File(path);

		response.setHeader("Cache-Control", "no-cache");
		response.addHeader("Content-disposition", "attachment;fileName=" + imageFileName); //이미지 파일을 내려받는데 필요한 response에 헤더 정보 설정
		FileInputStream in = new FileInputStream(imageFile);
		byte[] buffer = new byte[1024 * 8]; //버퍼를 이용해 한번에 8kb씩 전송
		while (true) {
			int count = in.read(buffer);
			if (count == -1)
				break;
			out.write(buffer, 0, count);
		}
		in.close();
		out.close();
	}

}
