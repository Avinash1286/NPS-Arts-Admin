package com.nps.npsartsadmin;

public class NotificationModel {
    public NotificationModel() {
    }

   private String postKey,username,proLink,notificationType,heading,currentUserss;

    public String getPostKey() {
        return postKey;
    }

    public void setPostKey(String postKey) {
        this.postKey = postKey;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProLink() {
        return proLink;
    }

    public void setProLink(String proLink) {
        this.proLink = proLink;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getCurrentUserss() {
        return currentUserss;
    }

    public void setCurrentUserss(String currentUserss) {
        this.currentUserss = currentUserss;
    }
}
