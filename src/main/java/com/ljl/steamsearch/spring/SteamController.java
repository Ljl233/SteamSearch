package com.ljl.steamsearch.spring;

import com.google.gson.Gson;
import com.ljl.steamsearch.index.CommentIndexBuilder;
import com.ljl.steamsearch.index.GameIndexBuilder;
import com.ljl.steamsearch.model.Comment;
import com.ljl.steamsearch.model.CommentResponse;
import com.ljl.steamsearch.model.Game;
import com.ljl.steamsearch.model.repo.CommentRepository;
import com.ljl.steamsearch.model.repo.GameRepository;
import com.ljl.steamsearch.parse.RetrievePage;
import com.ljl.steamsearch.util.FileUtils;
import com.ljl.steamsearch.util.MathUtils;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.store.NIOFSDirectory;
import org.jsoup.Jsoup;
import org.jsoup.helper.HttpConnection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static com.ljl.steamsearch.parse.PageParser.getGameCommentRequestFromHref;
import static com.ljl.steamsearch.parse.PageParser.getGameId;

@RestController
public class SteamController {

    private static String INDEX_STORE_PATH = "E:\\IdeaProject\\java2\\index";
    private static String INDEX_GAME_STORE_PATH = "E:\\IdeaProject\\java2\\index\\game";
    private static String INDEX_COMMENT_STORE_PATH = "E:\\IdeaProject\\java2\\index\\comment";

    @Autowired
    private GameRepository mGameRepo;

    @Autowired
    private CommentRepository mCommentRepo;

    private String requestUrl;
    private String mGameName;
    private String mGameId;


    @GetMapping("/crawl")
    public String saveGameAndComments() {
        HashSet<String> hrefs = FileUtils.readFileByLines("gamesUrls.txt");
        for (String href : hrefs) {
            saveGame(href);
            saveComments();
        }
        return "crawl steam and save game info to database";
    }

    @GetMapping("/query/games/{keywords}")
    public List<Game> searchGames(@PathVariable String keywords) {
        List<Game> games = new ArrayList<>();
        try {
            IndexReader reader = DirectoryReader.open(new NIOFSDirectory(Paths.get(INDEX_GAME_STORE_PATH)));
            IndexSearcher searcher = new IndexSearcher(reader);
            Term term = new Term("gameName", keywords);
            Query query = new TermQuery(term);
            TopDocs topDocs = searcher.search(query, 10);

            ScoreDoc[] scoreDocs = topDocs.scoreDocs;
            for (ScoreDoc scoreDoc : scoreDocs) {
                org.apache.lucene.document.Document doc = searcher.doc(scoreDoc.doc);
                System.out.println("id:" + doc.getField("id").stringValue());
                System.out.println("gameName:" + doc.getField("gameName").stringValue());
//                Game game = mGameRepo.findById(Integer.parseInt(doc.getField("id").stringValue())).orElse(null);
//                games.add(game);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return games;
    }

    @GetMapping("/query/comments/{keywords}")
    public List<Comment> searchComments(@PathVariable String keywords) {
        List<Comment> comments = new ArrayList<>();
        try {
            IndexReader reader = DirectoryReader.open(new NIOFSDirectory(Paths.get(INDEX_COMMENT_STORE_PATH)));
            IndexSearcher searcher = new IndexSearcher(reader);
            Term term = new Term("content", keywords);
            Query query = new TermQuery(term);
            TopDocs topDocs = searcher.search(query, 10);
            ScoreDoc[] scoreDocs = topDocs.scoreDocs;
            for (ScoreDoc scoreDoc : scoreDocs) {
                org.apache.lucene.document.Document doc = searcher.doc(scoreDoc.doc);

                Comment comment = mCommentRepo.findById(Integer.parseInt(doc.getField("id").stringValue())).orElse(null);
                comments.add(comment);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return comments;
    }

    public boolean saveGame(String href) {
        Game game = new Game();
        String publishDate;

        requestUrl = getGameCommentRequestFromHref(href);
        try {
            Document document;
            String gameName;
            document = HttpConnection.connect(href)
                    .header("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8")
                    .get();

            Elements apphubAppName = document.getElementsByClass("apphub_AppName");
            if (apphubAppName.size() > 0) {
                gameName = apphubAppName.get(0).text();
                mGameName = gameName;
            } else {
                mGameName = document.title().substring(9);
                game.setName(mGameName);
                mGameRepo.save(game);
                return false;
            }
            game.setName(mGameName);

            mGameId = getGameId(href);
            game.setGameId(mGameId);


            Elements dates = document.getElementsByClass("date");
            if (dates.size() > 0) {
                publishDate = dates.get(0).text();
                game.setPublishDate(publishDate);
            }

            if (document.getElementsByClass("summary_section").size() > 0) {
                Elements span = document.getElementsByClass("summary_section").get(0)
                        .getElementsByTag("span");
                int commentCnt = 0;
                if (span.size() > 1) {
                    String span1 = span.get(1).text();
                    commentCnt = MathUtils.parseIntRemoveSplit(span1.substring(1, span1.length() - 5));
                }
                game.setComments(commentCnt);
            } else {
                System.out.println("href(summary_section==0)" + href);
            }


            Elements typeElements = document.getElementsByClass("details_block").get(0)
                    .getElementsByTag("a");
            StringBuilder type = new StringBuilder();
            for (Element typeElement : typeElements) {
                type.append(typeElement.text());
                type.append(",");
            }
            if (type.length() > 0)
                game.setType(type.substring(0, type.length() - 1));

            mGameRepo.save(game);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public void saveComments() {
        RetrievePage.downloadPage(requestUrl,
                content -> {
                    Gson gson = new Gson();
                    CommentResponse commentResponse = gson.fromJson(content, CommentResponse.class);
                    parseCommentsHtml(commentResponse.getHtml());
                });
    }

    public void parseCommentsHtml(String html) {
        Document document = Jsoup.parse(html);
        Elements reviewBox = document.getElementsByClass("review_box   ");
        Elements reviewsBoxPartial = document.getElementsByClass("review_box    partial");

        for (Element element : reviewBox) {
            saveComment(element);
        }
        for (Element element : reviewsBoxPartial) {
            saveComment(element);
        }
    }

    public void saveComment(Element element) {
        int approveCount = 0;
        String postDate;
        String content;
        String publisher;
        int publisherCommentCount;
        int publisherGamesCount;

        postDate = element.getElementsByClass("postedDate").get(0).text();
        postDate = postDate.substring(4);
        System.out.println("postDate:" + postDate);

        String voteInfo = element.getElementsByClass("vote_info").get(0).text();
        if (voteInfo.length() > 11) {
            System.out.println("voteinfo:" + voteInfo);
            String[] split = voteInfo.split(" ");

            try {
                approveCount = MathUtils.parseIntRemoveSplit(split[1]);
            } catch (NumberFormatException e) {
                approveCount = MathUtils.parseIntRemoveSplit(split[0]);
            }
            System.out.println("approveCount:" + approveCount);
        } else {
            System.out.println("voteinfo:" + voteInfo);
        }

        content = element.getElementsByClass("content").get(0).text();
        System.out.println("content:" + content);

        publisher = element.getElementsByClass("persona_name").get(0)
                .getElementsByTag("a").get(0).text();
        System.out.println("publisher:" + publisher);

        String numOwnedGames = element.getElementsByClass("num_owned_games").get(0)
                .getElementsByTag("a").get(0).text();
        publisherGamesCount = MathUtils.parseIntRemoveSplit(numOwnedGames.substring(6, numOwnedGames.length() - 4));
        System.out.println("publisherGamesCount:" + publisherGamesCount);

        String numReviews = element.getElementsByClass("num_reviews").get(0)
                .getElementsByTag("a").get(0).text();
        publisherCommentCount = Integer.parseInt(numReviews.substring(0, numReviews.length() - 4));
        System.out.println("publisherCommentCount:" + publisherCommentCount);

        Comment comment = new Comment();
        comment.setApproveCount(approveCount);
        comment.setContent(content);
        comment.setGameName(mGameName);
        comment.setGameId(mGameId);
        comment.setPostDate(postDate);
        comment.setPublisher(publisher);
        comment.setPublisherCommentCount(publisherCommentCount);
        comment.setPublisherGamesCount(publisherGamesCount);
        mCommentRepo.save(comment);
    }

    @GetMapping("/index/game")
    public String produceGameIndex() {
        List<Game> gameList = mGameRepo.findAll();
        String path = INDEX_GAME_STORE_PATH;
        GameIndexBuilder gameIndexBuilder = new GameIndexBuilder(gameList, path);
        try {
            gameIndexBuilder.buildIndex();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "build game index end";
    }

    @GetMapping("/index/comment")
    public String produceCommentIndex() {
        List<Comment> commentList = mCommentRepo.findAll();
        String path = INDEX_COMMENT_STORE_PATH;
        CommentIndexBuilder commentIndexBuilder = new CommentIndexBuilder(commentList, path);
        try {
            commentIndexBuilder.buildIndex();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "build comment index end";
    }

    @GetMapping("/hello")
    public String sayHello() {
        return "hello";
    }

}
