package com.ljl.steamsearch.index;

import com.ljl.steamsearch.model.Game;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.NIOFSDirectory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class GameIndexBuilder {

    private List<Game> mGames;
    private final String INDEX_STORE_PATH;
    private Directory mFsDir;

    public GameIndexBuilder(List<Game> games, String storePath) {
        mGames = games;
        INDEX_STORE_PATH = storePath;
        try {
            mFsDir = new NIOFSDirectory(Paths.get(storePath));
            buildIndex();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void buildIndex() throws IOException {
        List<Document> docs = new ArrayList<>();
        mGames.forEach(game -> {
            Document doc = new Document();

            doc.add(new StringField("gameName", game.getName(), Field.Store.YES));
            doc.add(new TextField("type", game.getType(), Field.Store.YES));
            doc.add(new StringField("url",
                    "https://store.steampowered.com/app/" + game.getId(), Field.Store.YES));
            doc.add(new StringField("publishDate", game.getPublishDate(), Field.Store.YES));
            doc.add(new StringField("comments", String.valueOf(game.getComments()), Field.Store.YES));

            docs.add(doc);
        });


        IndexWriterConfig config = new IndexWriterConfig();
        IndexWriter writer = new IndexWriter(mFsDir, config);

        writer.addDocuments(docs);
        writer.close();

    }

}
