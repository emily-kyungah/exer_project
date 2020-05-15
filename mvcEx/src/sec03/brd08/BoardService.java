package sec03.brd08;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BoardService {
	BoardDAO boardDAO;

	public BoardService() {
		boardDAO = new BoardDAO();	//생성자 호출 시 BoardDAO 객체 생성
	}

	public Map listArticles(Map<String, Integer> pagingMap) {
		Map articlesMap = new HashMap();
		List<ArticleVO> articlesList = boardDAO.selectAllArticles(pagingMap); //전달된 pagingMap 사용해 글목록 조회
		int totArticles = boardDAO.selectTotArticles(); //전체 글 수 조회
		articlesMap.put("articlesList", articlesList); //조회된 글 목록을 ArrayList에 저장한 후 다시 articlesMap에 저장
		articlesMap.put("totArticles", totArticles); //전체 글 수를 articleMap에저장
		//articlesMap.put("totArticles", 170);
		return articlesMap;
	}

	public List<ArticleVO> listArticles() {
		List<ArticleVO> articlesList = boardDAO.selectAllArticles();
		return articlesList;
	}

	public int addArticle(ArticleVO article) {
		return boardDAO.insertNewArticle(article); //insertNewArticle()메소드를 호출하면서 글 정보를 인자로 전달. 새 글번호를 컨트롤러로 반환
	}

	public ArticleVO viewArticle(int articleNO) { //글 번호로 selectArticle()메소드 호출
		ArticleVO article = null;
		article = boardDAO.selectArticle(articleNO);
		return article;
	}

	public void modArticle(ArticleVO article) {
		boardDAO.updateArticle(article);
	}

	public List<Integer> removeArticle(int articleNO) {
		List<Integer> articleNOList = boardDAO.selectRemovedArticles(articleNO); //글을 삭제하기 전 글 번호들을 ArrayList객체에 저장
		boardDAO.deleteArticle(articleNO);
		return articleNOList; //삭제한 글 번호 목록을 컨트롤러로 반환
	}

	public int addReply(ArticleVO article) {
		return boardDAO.insertNewArticle(article);
	}

}
