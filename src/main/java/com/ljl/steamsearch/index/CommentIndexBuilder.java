package com.ljl.steamsearch.index;

import com.ljl.steamsearch.model.Comment;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CommentIndexBuilder {
    private List<Comment> mComments;
    private Directory mFsDir;

    public CommentIndexBuilder(List<Comment> comments, String storePath) {
        mComments = comments;
        try {
            mFsDir = new NIOFSDirectory(Paths.get(storePath));
            buildIndex();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void buildIndex() throws IOException {
        List<Document> docs = new ArrayList<>();
        mComments.forEach(comment -> {
            Document doc = new Document();

            doc.add(new StringField("id", String.valueOf(comment.getId()), Field.Store.YES));
            doc.add(new StringField("gameName", comment.getGameName(), Field.Store.YES));
            doc.add(new TextField("content", comment.getContent(), Field.Store.YES));
            doc.add(new StringField("url",
                    "https://store.steampowered.com/app/" + comment.getId(), Field.Store.YES));
            doc.add(new StringField("approveCnt", String.valueOf(comment.getApproveCount()), Field.Store.YES));
            doc.add(new StringField("postData", comment.getPostDate(), Field.Store.YES));
            docs.add(doc);

        });

        IndexWriterConfig config = new IndexWriterConfig();
        IndexWriter writer = new IndexWriter(mFsDir, config);
        writer.addDocuments(docs);
        writer.close();

    }
}
