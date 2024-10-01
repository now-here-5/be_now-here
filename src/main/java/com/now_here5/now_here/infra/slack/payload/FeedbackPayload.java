package com.now_here5.now_here.infra.slack.payload;

public class FeedbackPayload implements SlackMessagePayload {

    private final Long feedId;
    private final Long memberId;
    private final String nickname;
    private final String content;
    private final int rate;

    public FeedbackPayload(Long feedId, Long memberId, String nickname, int rate, String content) {
        this.feedId = feedId;
        this.memberId = memberId;
        this.nickname = nickname;
        this.rate = rate;
        this.content = content;
    }

    @Override
    public String getFormattedMessage() {
        return String.format("{\"channel\": \"#운영-피드백\", " +
                        "\"text\": \"*[피드백 #%d]*\\n\\n" +
                        "*사용자 ID :*  %d\\n" +
                        "*닉네임 :*  %s\\n" +
                        "*평점 :*  %d\\n" +
                        "*내용 :*  %s\"}",
                feedId, memberId, nickname, rate, content.replace("\n", "\\n").replace("\"", "\\\""));
    }
}

