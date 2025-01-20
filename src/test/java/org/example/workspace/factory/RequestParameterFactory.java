package org.example.workspace.factory;

import org.springframework.boot.test.context.TestComponent;

import java.util.List;

@TestComponent
public class RequestParameterFactory {

    public String createInvalidLoginId(int nowCount, int totalCount) {
        List<String> invalidList = List.of(
                "a1",
                "한글",
                "a1",
                "123",
                "a".repeat(17)
        );
        return getNowValue(nowCount, totalCount, invalidList);
    }

    public String createInvalidPassword(int nowCount, int totalCount) {
        List<String> list = List.of(
                "!a1",
                "a".repeat(8),
                "1".repeat(8),
                "!".repeat(8),
                "a1".repeat(4),
                "a!".repeat(4),
                "1!".repeat(4),
                "!a1".repeat(6)
        );
        return getNowValue(nowCount, totalCount, list);
    }

    public String createInvalidEmail(int nowCount, int totalCount) {
        List<String> invalidList = List.of(
                "asdf",
                "@asdf.asdf",
                "sdfasdfasdfasdf.adsf",
                "asdfasdf@sdf@asdf",
                "asdfas@@gmail.com",
                "asdfas@@gmail.com"
        );
        return getNowValue(nowCount, totalCount, invalidList);
    }

    public String createInvalidPhoneNumber(int nowCount, int totalCount) {
        List<String> invalidList = List.of(
                "a".repeat(11),
                "0101234123a",
                "010-1234-1234"
        );
        return getNowValue(nowCount, totalCount, invalidList);
    }

    public String createInvalidLengthString(int nowCount, int totalCount, int length) {
        List<String> invalidList = List.of(
                "a".repeat(length)
        );
        return getNowValue(nowCount, totalCount, invalidList);
    }

    private String getNowValue(int nowCount, int totalCount, List<String> invalidList) {
        if (invalidList.size() > totalCount)
            throw new IllegalArgumentException("Invalid list size is smaller than total repetitions!");

        return invalidList.get(nowCount % invalidList.size());
    }

}
