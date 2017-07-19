package ru.alvioneurope;

/**
 * Created by GSergeev on 13.07.2017.
 */
public class DCTInfo {
    private String uid;
    private String cid;
    private String city;
    private String source;
    private String medium;
    private String campaign;
    private String content;
    private String term;
    private int duration;
    private String currentPage;
    private String contextId;
    private String entryId;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getMedium() {
        return medium;
    }

    public void setMedium(String medium) {
        this.medium = medium;
    }

    public String getCampaign() {
        return campaign;
    }

    public void setCampaign(String campaign) {
        this.campaign = campaign;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(String currentPage) {
        this.currentPage = currentPage;
    }

    public String getContextId() {
        return contextId;
    }

    public void setContextId(String contextId) {
        this.contextId = contextId;
    }

    public String getEntryId() {
        return entryId;
    }

    public void setEntryId(String entryId) {
        this.entryId = entryId;
    }

    @Override
    public String toString() {
        return "DCTInfo{" +
                "uid='" + uid + '\'' +
                ", cid='" + cid + '\'' +
                ", city='" + city + '\'' +
                ", source='" + source + '\'' +
                ", medium='" + medium + '\'' +
                ", campaign='" + campaign + '\'' +
                ", content='" + content + '\'' +
                ", term='" + term + '\'' +
                ", duration=" + duration +
                ", currentPage='" + currentPage + '\'' +
                ", contextId='" + contextId + '\'' +
                ", entryId='" + entryId + '\'' +
                '}';
    }
}
