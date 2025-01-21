package org.example.workspace.service;

import lombok.RequiredArgsConstructor;
import org.example.workspace.common.ApplicationConstant;
import org.example.workspace.util.MailTemplate;
import org.example.workspace.util.WorkspaceMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MailService {
    private final WorkspaceMailSender workspaceMailSender;
    private final MailTemplate mailTemplate;

    public void sendSignupConfirmMail(String token, String to) {

        workspaceMailSender.sendHtmlMessage(
                to,
                ApplicationConstant.Email.SIGNUP_CONFIRM_TITLE,
                mailTemplate.getSignupConfirmTemplate(token)
        );
    }
}
