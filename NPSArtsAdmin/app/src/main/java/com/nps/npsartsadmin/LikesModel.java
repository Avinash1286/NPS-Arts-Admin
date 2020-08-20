package com.nps.npsartsadmin;

public class LikesModel {

    public LikesModel() {
    }

    String nameInLike,proLinkInLike,uid;


    public LikesModel(String nameInLike, String proLinkInLike, String uid) {
        this.nameInLike = nameInLike;
        this.proLinkInLike = proLinkInLike;
        this.uid = uid;
    }

    public String getNameInLike() {
        return nameInLike;
    }

    public void setNameInLike(String nameInLike) {
        this.nameInLike = nameInLike;
    }

    public String getProLinkInLike() {
        return proLinkInLike;
    }

    public void setProLinkInLike(String proLinkInLike) {
        this.proLinkInLike = proLinkInLike;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}

