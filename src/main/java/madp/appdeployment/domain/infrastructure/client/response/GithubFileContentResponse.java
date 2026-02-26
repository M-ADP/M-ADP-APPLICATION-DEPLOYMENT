package madp.appdeployment.domain.infrastructure.client.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GithubFileContentResponse(
        @JsonProperty("name")
        String name,
        
        @JsonProperty("path")
        String path,
        
        @JsonProperty("content")
        String content,
        
        @JsonProperty("encoding")
        String encoding,
        
        @JsonProperty("size")
        Long size
) {
    public String getDecodedContent() {
        if ("base64".equals(encoding) && content != null) {
            return new String(java.util.Base64.getDecoder().decode(content.replaceAll("\\s", "")));
        }
        return content;
    }
}