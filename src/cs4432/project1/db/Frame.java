package cs4432.project1.db;

public class Frame {
    String content;
    Boolean dirty;
    Boolean pinned;
    int queryForId;
    int blockId;

    public Frame(String content, int blockId, int queryForId) {
        this.initialize(content, blockId, queryForId);
    }

    private void initialize(String content, int blockId, int queryForId) {
        this.content = content;
        this.dirty = false;
        this.pinned = false;
        this.blockId = blockId;
        this.queryForId = queryForId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.dirty = true;
        this.content = content;
    }

    public Boolean getDirty() {
        return dirty;
    }

    public void setDirty(Boolean dirty) {
        this.dirty = dirty;
    }

    public Boolean getPinned() {
        return pinned;
    }

    public void setPinned(Boolean pinned) {
        this.pinned = pinned;
    }

    public int getQueryForId() {
        return queryForId;
    }

    public void setQueryForId(int queryForId) {
        this.queryForId = queryForId;
    }

    public int getBlockId() {
        return blockId;
    }

    public void setBlockId(int blockId) {
        this.blockId = blockId;
    }

    @Override
    public String toString() {
        return "Frame{" +
                "content='" + content + '\'' +
                ", dirty=" + dirty +
                ", pinned=" + pinned +
                ", queryForId=" + queryForId +
                ", blockId=" + blockId +
                '}';
    }
}
