package org.example.workspace.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MailTemplate {

    @Value("${app.domain}")
    private String domain;

    public String getSignupConfirmTemplate(String token) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    .container {
                        max-width: 600px;
                        margin: 0 auto;
                        padding: 20px;
                        font-family: Arial, sans-serif;
                    }
                    .header {
                        background-color: #4A90E2;
                        color: white;
                        padding: 20px;
                        text-align: center;
                        border-radius: 5px 5px 0 0;
                    }
                    .content {
                        background-color: #ffffff;
                        padding: 30px;
                        border: 1px solid #dddddd;
                        border-radius: 0 0 5px 5px;
                    }
                    .button {
                        display: inline-block;
                        padding: 12px 24px;
                        background-color: #4A90E2;
                        color: white;
                        text-decoration: none;
                        border-radius: 5px;
                        margin: 20px 0;
                    }
                    .footer {
                        margin-top: 20px;
                        text-align: center;
                        color: #666666;
                        font-size: 12px;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>이메일 인증</h1>
                    </div>
                    <div class="content">
                        <h2>안녕하세요!</h2>
                        <p>회원가입을 완료하기 위해 아래 버튼을 클릭하여 이메일 인증을 진행해주세요.</p>
                        <p>이 링크는 24시간 동안 유효합니다.</p>
                        <div style="text-align: center;">
                            <a href="%s/user/signup/confirm/key?%s" class="button">이메일 인증하기</a>
                        </div>
                    </div>
                    <div class="footer">
                        <p>본 메일은 회원가입 인증을 위해 발송된 메일입니다.</p>
                        <p>본인이 요청하지 않았다면 이 메일을 무시하셔도 됩니다.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(domain, token, domain);
    }

    public String getRecoveryTemplate(String loginId, String token) {
        return """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="UTF-8">
            <style>
                .container {
                    max-width: 600px;
                    margin: 0 auto;
                    padding: 20px;
                    font-family: Arial, sans-serif;
                }
                .header {
                    background-color: #4A90E2;
                    color: white;
                    padding: 20px;
                    text-align: center;
                    border-radius: 5px 5px 0 0;
                }
                .content {
                    background-color: #ffffff;
                    padding: 30px;
                    border: 1px solid #dddddd;
                    border-radius: 0 0 5px 5px;
                }
                .button {
                    display: inline-block;
                    padding: 12px 24px;
                    background-color: #4A90E2;
                    color: white;
                    text-decoration: none;
                    border-radius: 5px;
                    margin: 20px 0;
                }
                .footer {
                    margin-top: 20px;
                    text-align: center;
                    color: #666666;
                    font-size: 12px;
                }
            </style>
        </head>
        <body>
            <div class="container">
                <div class="header">
                    <h1>비밀번호 복구</h1>
                </div>
                <div class="content">
                    <h2>안녕하세요!</h2>
                    <p>로그인 아이디: <strong>%s</strong></p>
                    <p>비밀번호를 변경하려면 아래 버튼을 클릭하세요.</p>
                    <p>이 링크는 24시간 동안 유효합니다.</p>
                    <div style="text-align: center;">
                        <a href="http://localhost:8080/api/v1/users/password?token=%s" class="button">비밀번호 변경하기</a>
                    </div>
                </div>
                <div class="footer">
                    <p>본 메일은 비밀번호 복구 요청에 따라 발송된 메일입니다.</p>
                    <p>본인이 요청하지 않았다면 이 메일을 무시하셔도 됩니다.</p>
                </div>
            </div>
        </body>
        </html>
        """.formatted(loginId, token);
    }
}
