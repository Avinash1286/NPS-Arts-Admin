package com.nps.npsartsadmin;

public class OnLineCommentModel {

   private String Commentname,proLink,currentUser,comment;


    public OnLineCommentModel() {

    }

    public OnLineCommentModel(String Commentname, String proLink, String currentUser, String comment) {
        this.Commentname = Commentname;
        this.proLink = proLink;
        this.currentUser = currentUser;
        this.comment = comment;
    }


    public String getCommentname() {
        return Commentname;
    }

    public void setCommentname(String Commentname) {
        this.Commentname = Commentname;
    }

    public String getProLink() {
        return proLink;
    }

    public void setProLink(String proLink) {
        this.proLink = proLink;
    }

    public String getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(String currentUser) {
        this.currentUser = currentUser;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
