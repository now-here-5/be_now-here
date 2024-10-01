package com.now_here5.now_here.infra.slack.payload;


public class InquiryPayload implements SlackMessagePayload {

    private final Long inquiryId;
    private final String inquiryContent;
    private final String sourceInfo;

    public InquiryPayload(Long inquiryId, String inquiryContent, String sourceInfo) {
        this.inquiryId = inquiryId;
        this.inquiryContent = inquiryContent;
        this.sourceInfo = sourceInfo;
    }

    @Override
    public String getFormattedMessage() {
        return String.format("{\"channel\": \"#운영-문의사항\", " +
                        "\"text\": \"*[문의사항]*\\n\\n" +
                        "*문의 ID :*  %d\\n" +
                        "*이메일 :*  %s\\n" +
                        "*내용 :*  %s\"}",
                inquiryId, sourceInfo, inquiryContent.replace("\n", "\\n").replace("\"", "\\\""));
    }
}
