package uz.pdp.bot;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class MailingInfo {
    private String recipient;
    private String subject;
    private String content;
}
