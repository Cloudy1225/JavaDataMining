package main.java.utils.io;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * A simple Iterator over the lines in a {@code Reader}.
 *
 * @author Cloudy1225
 * @see FileTool
 */
public class LineIterator implements Iterable<String>, Iterator<String>, Closeable {

    /**
     * The reader that is being read.
     */
    private BufferedReader in;

    /**
     * The line that will be gotten.
     */
    private String cachedLine;

    /**
     * Whether ignores comments line.
     */
    private boolean skipComments;

    /**
     * Whether ignores blank line.
     */
    private boolean skipBlanks;

    /**
     * Holds identifiers of comments. ("#", "//", "%" are default)
     */
    private final ArrayList<String> commentIdentifiers;

    /**
     * Constructs an iterator of the lines for a {@link Reader}.
     *
     * @param reader the {@link Reader} to read from, not null
     * @param skipComments true if ignoring comments line
     * @param skipBlanks true if ignoring blank line
     * @throws IOException  If an I/O error occurs
     */
    public LineIterator(Reader reader, boolean skipComments, boolean skipBlanks) throws IOException {
        if (reader instanceof BufferedReader) {
            this.in = (BufferedReader) reader;
        } else {
            this.in = new BufferedReader(reader);
        }
        this.cachedLine = this.in.readLine();
        if (this.cachedLine == null) {
            this.in.close();
        }
        this.commentIdentifiers = new ArrayList<>();
        this.commentIdentifiers.add("#");
        this.commentIdentifiers.add("//");
        this.commentIdentifiers.add("%");
        // 此处必须使用setter，并非简单的赋值
        this.setSkipComments(skipComments);
        this.setSkipBlanks(skipBlanks);
    }

    @Override
    public Iterator<String> iterator() {
        return this;
    }

    @Override
    public boolean hasNext() {
        return this.cachedLine != null;
    }

    @Override
    public String next() {
        String currentLine = this.cachedLine;
        this.refreshCachedLine();
        return currentLine;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("This operation is not possible on a LineIterator");
    }

    /**
     * Closes the underlying {@link Reader}.
     *
     * @throws IOException if closing the underlying {@link Reader} fails.
     */
    @Override
    public void close() throws IOException {
        if (this.in == null) {
            this.cachedLine = null;
            return;
        }
        this.in.close();
        this.in = null;
        this.cachedLine = null;
    }

    /**
     * Refreshes the cachedLine.
     */
    private void refreshCachedLine() {
        try {
            do {
                this.cachedLine = this.in.readLine();
            } while (this.cachedLine != null &&
                    (this.skipBlanks && this.cachedLine.length() == 0 ||
                            this.skipComments && this.isComment(this.cachedLine)));
            if (this.cachedLine == null) {
                this.in.close();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Checks if the line is a comments line.
     *
     * @param line a line
     * @return true/false
     */
    private boolean isComment(String line) {
        Iterator<String> iter = this.commentIdentifiers.iterator();

        String prefix;
        do {
            if (!iter.hasNext()) {
                return false;
            }
            prefix = iter.next();
        } while(!line.startsWith(prefix));

        return true;
    }

    /**
     * Sets whether ignores comments line or not.
     *
     * @param skipComments true/false
     */
    public final void setSkipComments(boolean skipComments) {
        this.skipComments = skipComments;
        if (this.skipComments && this.cachedLine != null && this.isComment(this.cachedLine)) {
            this.refreshCachedLine();
        }

    }

    /**
     * Sets whether ignores blank line or not.
     *
     * @param skipBlanks true/false
     */
    public final void setSkipBlanks(boolean skipBlanks) {
        this.skipBlanks = skipBlanks;
        if (this.cachedLine != null && this.skipBlanks && this.cachedLine.length() == 0) {
            this.refreshCachedLine();
        }

    }

    /**
     * Sets the identifier of comments when skipping comments line.
     *
     * @param commentIdentifier such as "#", "//"
     */
    public final void setCommentIdentifier(String commentIdentifier) {
        this.commentIdentifiers.clear();
        this.addCommentIdentifier(commentIdentifier);
    }

    /**
     * Adds the identifier of comments when skipping comments line.
     *
     * @param commentIdentifier such as "#", "//"
     */
    public final void addCommentIdentifier(String commentIdentifier) {
        this.commentIdentifiers.add(commentIdentifier);
        if (this.cachedLine != null && this.skipComments && this.isComment(this.cachedLine)) {
            this.refreshCachedLine();
        }
    }
}

