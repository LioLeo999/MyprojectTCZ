package com.example.myprojecttcz.model;

public class Drill {

    protected String id;
    protected String name;
    protected String explantion;
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
    protected Boolean forwardbeckhand;

    protected String gif;
    protected String video;

    // ---- New fields ----
    protected String trainingTools;   // עזרי אימון
    protected String age;             // גיל
    protected String playerLevel;     // רמת שחקן
    protected String ballColor;       // צבע כדור
    protected String courtSize;       // גודל מגרש
    // --------------------

    public Drill() {}

    public Drill(String id, String name, String explantion, String time, String level,
                 String minplayers, String maxplayers,
                 Boolean forehand, Boolean backhand,
                 Boolean volleyforehand, Boolean volleybackhand,
                 Boolean driveforehand, Boolean drivebackhand,
                 Boolean serve, Boolean smash,
                 Boolean forwardforehand, Boolean forwardbeckhand,
                 String gif, String video,
                 String trainingTools, String age, String playerLevel,
                 String ballColor, String courtSize) {

        this.id = id;
        this.name = name;
        this.explantion = explantion;
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
        this.forwardbeckhand = forwardbeckhand;

        this.gif = gif;
        this.video = video;

        this.trainingTools = trainingTools;
        this.age = age;
        this.playerLevel = playerLevel;
        this.ballColor = ballColor;
        this.courtSize = courtSize;
    }

    // Getters & Setters

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getExplantion() { return explantion; }
    public void setExplantion(String explantion) { this.explantion = explantion; }

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

    public Boolean getForwardbeckhand() { return forwardbeckhand; }
    public void setForwardbeckhand(Boolean forwardbeckhand) { this.forwardbeckhand = forwardbeckhand; }

    public String getGif() { return gif; }
    public void setGif(String gif) { this.gif = gif; }

    public String getVideo() { return video; }
    public void setVideo(String video) { this.video = video; }

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

    @Override
    public String toString() {
        return "Drill{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", explantion='" + explantion + '\'' +
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
                ", forwardbeckhand=" + forwardbeckhand +
                ", gif='" + gif + '\'' +
                ", video='" + video + '\'' +
                ", trainingTools='" + trainingTools + '\'' +
                ", age='" + age + '\'' +
                ", playerLevel='" + playerLevel + '\'' +
                ", ballColor='" + ballColor + '\'' +
                ", courtSize='" + courtSize + '\'' +
                '}';
    }
}
