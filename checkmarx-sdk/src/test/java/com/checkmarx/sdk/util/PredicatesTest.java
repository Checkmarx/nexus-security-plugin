package com.checkmarx.sdk.util;

import java.util.ArrayList;
import java.util.List;

import com.checkmarx.sdk.model.Issue;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class PredicatesTest {

  @Test
  void distinctByKey_sameKey() {
    // given
    List<Issue> issues = new ArrayList<>();
    Issue firstIssue = new Issue();
    firstIssue.id = "checkmarx-JAVA-ID-1";
    Issue secondIssue = new Issue();
    secondIssue.id = "checkmarx-JAVA-ID-1";
    issues.add(firstIssue);
    issues.add(secondIssue);

    // when
    long count = issues.stream()
                       .filter(Predicates.distinctByKey(i -> i.id))
                       .count();

    // then
    Assertions.assertEquals(1, count);
  }

  @Test
  void distinctByKey_differentKey() {
    // given
    List<Issue> issues = new ArrayList<>();
    Issue firstIssue = new Issue();
    firstIssue.id = "checkmarx-JAVA-ID-1";
    Issue secondIssue = new Issue();
    secondIssue.id = "checkmarx-JAVA-ID-2";
    issues.add(firstIssue);
    issues.add(secondIssue);

    // when
    long count = issues.stream()
                       .filter(Predicates.distinctByKey(i -> i.id))
                       .count();

    // then
    Assertions.assertEquals(2, count);
  }

}
