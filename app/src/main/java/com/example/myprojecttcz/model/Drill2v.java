package com.example.myprojecttcz.model;

public class Drill2v {

    protected String id;
    protected String name;
    protected String explanation;

    protected String time;
    protected String level;
    protected String minplayers;
    protected String maxplayers;

    protected Boolean forehand;
    protected Boolean backhand;
    protected Boolean volleyforehand;
    protected Boolean volleybackhand;
    protected Boolean driveforehand;
    protected Boolean drivebackhand;
    protected Boolean serve;
    protected Boolean smash;
    protected Boolean forwardforehand;
    protected Boolean forwardbackhand;
    protected Boolean slicebackhand;
    protected Boolean sliceforehand;

    protected String gif;
    protected String video1;
    protected String video2;

    protected String trainingTools;
    protected String age;
    protected String playerLevel;
    protected String ballColor;
    protected String courtSize;

    public Drill2v() {}

    // ----------------------------------------------------
    // FULL CONSTRUCTOR (מסודר לפי AddDrill.java!)
    // ----------------------------------------------------

    public Drill2v(String id, String name, String explanation, String time, String level, String minplayers, String maxplayers, Boolean forehand, Boolean backhand, Boolean volleyforehand, Boolean volleybackhand, Boolean driveforehand, Boolean drivebackhand, Boolean serve, Boolean smash, Boolean forwardforehand, Boolean forwardbackhand, Boolean slicebackhand, Boolean sliceforehand, String gif, String video1, String video2, String trainingTools, String age, String playerLevel, String ballColor, String courtSize) {
        this.id = id;
        this.name = name;
        this.explanation = explanation;
        this.time = time;
        this.level = level;
        this.minplayers = minplayers;
        this.maxplayers = maxplayers;
        this.forehand = forehand;
        this.backhand = backhand;
        this.volleyforehand = volleyforehand;
        this.volleybackhand = volleybackhand;
        this.driveforehand = driveforehand;
        this.drivebackhand = drivebackhand;
        this.serve = serve;
        this.smash = smash;
        this.forwardforehand = forwardforehand;
        this.forwardbackhand = forwardbackhand;
        this.slicebackhand = slicebackhand;
        this.sliceforehand = sliceforehand;
        this.gif = gif;
        this.video1 = video1;
        this.video2 = video2;
        this.trainingTools = trainingTools;
        this.age = age;
        this.playerLevel = playerLevel;
        this.ballColor = ballColor;
        this.courtSize = courtSize;
    }


    // ----------------------------------------------------
    // Getters & Setters
    // ----------------------------------------------------

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }

    public String getMinplayers() { return minplayers; }
    public void setMinplayers(String minplayers) { this.minplayers = minplayers; }

    public String getMaxplayers() { return maxplayers; }
    public void setMaxplayers(String maxplayers) { this.maxplayers = maxplayers; }

    public Boolean getForehand() { return forehand; }
    public void setForehand(Boolean forehand) { this.forehand = forehand; }

    public Boolean getBackhand() { return backhand; }
    public void setBackhand(Boolean backhand) { this.backhand = backhand; }

    public Boolean getVolleyforehand() { return volleyforehand; }
    public void setVolleyforehand(Boolean volleyforehand) { this.volleyforehand = volleyforehand; }

    public Boolean getVolleybackhand() { return volleybackhand; }
    public void setVolleybackhand(Boolean volleybackhand) { this.volleybackhand = volleybackhand; }

    public Boolean getDriveforehand() { return driveforehand; }
    public void setDriveforehand(Boolean driveforehand) { this.driveforehand = driveforehand; }

    public Boolean getDrivebackhand() { return drivebackhand; }
    public void setDrivebackhand(Boolean drivebackhand) { this.drivebackhand = drivebackhand; }

    public Boolean getServe() { return serve; }
    public void setServe(Boolean serve) { this.serve = serve; }

    public Boolean getSmash() { return smash; }
    public void setSmash(Boolean smash) { this.smash = smash; }

    public Boolean getForwardforehand() { return forwardforehand; }
    public void setForwardforehand(Boolean forwardforehand) { this.forwardforehand = forwardforehand; }

    public Boolean getForwardbackhand() { return forwardbackhand; }
    public void setForwardbackhand(Boolean forwardbackhand) { this.forwardbackhand = forwardbackhand; }

    public String getGif() { return gif; }
    public void setGif(String gif) { this.gif = gif; }

    public String getVideo1() { return video1; }
    public void setVideo1(String video1) { this.video1 = video1; }

    public String getVideo2() { return video2; }
    public void setVideo2(String video2) { this.video2 = video2; }

    public String getTrainingTools() { return trainingTools; }
    public void setTrainingTools(String trainingTools) { this.trainingTools = trainingTools; }

    public String getAge() { return age; }
    public void setAge(String age) { this.age = age; }

    public String getPlayerLevel() { return playerLevel; }
    public void setPlayerLevel(String playerLevel) { this.playerLevel = playerLevel; }

    public String getBallColor() { return ballColor; }
    public void setBallColor(String ballColor) { this.ballColor = ballColor; }

    public String getCourtSize() { return courtSize; }
    public void setCourtSize(String courtSize) { this.courtSize = courtSize; }

    public Boolean getSlicebackhand() {
        return slicebackhand;
    }

    public void setSlicebackhand(Boolean slicebackhand) {
        this.slicebackhand = slicebackhand;
    }

    public Boolean getSliceforehand() {
        return sliceforehand;
    }

    public void setSliceforehand(Boolean sliceforehand) {
        this.sliceforehand = sliceforehand;
    }

    @Override
    public String toString() {
        return "Drill2v{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", explanation='" + explanation + '\'' +
                ", time='" + time + '\'' +
                ", level='" + level + '\'' +
                ", minplayers='" + minplayers + '\'' +
                ", maxplayers='" + maxplayers + '\'' +
                ", forehand=" + forehand +
                ", backhand=" + backhand +
                ", volleyforehand=" + volleyforehand +
                ", volleybackhand=" + volleybackhand +
                ", driveforehand=" + driveforehand +
                ", drivebackhand=" + drivebackhand +
                ", serve=" + serve +
                ", smash=" + smash +
                ", forwardforehand=" + forwardforehand +
                ", forwardbackhand=" + forwardbackhand +
                ", slicebackhand=" + slicebackhand +
                ", sliceforehand=" + sliceforehand +
                ", gif='" + gif + '\'' +
                ", video1='" + video1 + '\'' +
                ", video2='" + video2 + '\'' +
                ", trainingTools='" + trainingTools + '\'' +
                ", age='" + age + '\'' +
                ", playerLevel='" + playerLevel + '\'' +
                ", ballColor='" + ballColor + '\'' +
                ", courtSize='" + courtSize + '\'' +
                '}';
    }
}
