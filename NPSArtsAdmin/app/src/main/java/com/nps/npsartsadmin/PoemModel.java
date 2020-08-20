package com.nps.npsartsadmin;

public class PoemModel {
    public PoemModel() {
    }
  private   String Composer ,Poem ,Heading;
    public String getComposer() {
        return Composer;
    }
    public void setComposer(String composer) {
        Composer = composer;
    }

    public String getPoem() {
        return Poem;
    }

    public void setPoem(String poem) {
        Poem = poem;
    }

    public String getHeading() {
        return Heading;
    }

    public void setHeading(String heading) {
        Heading = heading;
    }
}
