package com.king.app.coolg.model.http.bean.request;

public class FolderRequest {

    private String folder;

    /**
     * see HttpConstants.FOLDER_TYPE_XX
     */
    private String type;

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
