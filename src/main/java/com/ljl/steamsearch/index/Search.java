package com.ljl.steamsearch.index;

import com.ljl.steamsearch.model.Comment;
import com.ljl.steamsearch.model.Game;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.store.NIOFSDirectory;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Search {
    private static String INDEX_GAME_STORE_PATH = "E:\\IdeaProject\\java2\\index\\game";
    private static String INDEX_COMMENT_STORE_PATH = "E:\\IdeaProject\\java2\\index\\comment";

    public static void main(String[] args) {
        System.out.println("search game--------------------------");
        searchGames("SteamWorld");

        System.out.println("search comment-----------------------");
        searchGames("免费");

        System.out.println("search both---------");
        searchBoth("free");
    }

    public static void searchGames(String keywords) {
        try {
            IndexReader reader = DirectoryReader.open(new NIOFSDirectory(Paths.get(INDEX_GAME_STORE_PATH)));
            IndexSearcher searcher = new IndexSearcher(reader);
            Term term = new Term("gameName", keywords);
            FuzzyQuery query = new FuzzyQuery(term);
            TopDocs topDocs = searcher.search(query, 10);

            ScoreDoc[] scoreDocs = topDocs.scoreDocs;
            for (ScoreDoc scoreDoc : scoreDocs) {
                org.apache.lucene.document.Document doc = searcher.doc(scoreDoc.doc);
                System.out.println("id:" + doc.getField("id").stringValue());
                System.out.println("gameName:" + doc.getField("gameName").stringValue());
                System.out.println("type:" + doc.getField("type").stringValue());
                System.out.println("url" + doc.getField("url").stringValue());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
                System.out.println("id:" + doc.getField("id").stringValue());
                System.out.println("gameName:" + doc.getField("gameName").stringValue());
                System.out.println("url" + doc.getField("url").stringValue());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return comments;
    }

    public static void searchBoth(String keywords) {
        try {
            IndexReader reader = DirectoryReader.open(new NIOFSDirectory(Paths.get(INDEX_COMMENT_STORE_PATH)));
            IndexSearcher searcher = new IndexSearcher(reader);
            Term term = new Term("content", keywords);
            Query query = new TermQuery(term);

            Term term1 = new Term("gameName", keywords);
            Query query1 = new FuzzyQuery(term1);

            BooleanQuery booleanQuery = (new BooleanQuery.Builder())
                    .add(query, BooleanClause.Occur.SHOULD)
                    .add(query1, BooleanClause.Occur.SHOULD)
                    .build();

            TopDocs topDocs = searcher.search(query, 10);

            ScoreDoc[] scoreDocs = topDocs.scoreDocs;
            for (ScoreDoc scoreDoc : scoreDocs) {
                org.apache.lucene.document.Document doc = searcher.doc(scoreDoc.doc);
                System.out.println("id:" + doc.getField("id").stringValue());
                System.out.println("gameName:" + doc.getField("gameName").stringValue());
                System.out.println("url" + doc.getField("url").stringValue());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
