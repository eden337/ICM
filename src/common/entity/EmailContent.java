package common.entity;

import java.io.Serializable;

public class EmailContent implements Serializable {
    private String mailTo;
    private String title;
    private String content;

    public EmailContent(String mailTo, String title, String content) {
        this.mailTo = mailTo;
        this.title = title;
        this.content = content;
    }

    public String getMailTo() {
        return mailTo;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "MailContent{" +
                "mailTo='" + mailTo + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
