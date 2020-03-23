/**
 * Copyright (C) 2014-2018 LinkedIn Corp. (pinot-core@linkedin.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.pinot.thirdeye.detection.alert.filter;

import org.apache.pinot.thirdeye.constant.AnomalyFeedbackType;
import org.apache.pinot.thirdeye.datalayer.dto.AnomalyFeedbackDTO;
import org.apache.pinot.thirdeye.datalayer.dto.DetectionAlertConfigDTO;
import org.apache.pinot.thirdeye.datalayer.dto.MergedAnomalyResultDTO;
import org.apache.pinot.thirdeye.detection.ConfigUtils;
import org.apache.pinot.thirdeye.detection.MockDataProvider;
import org.apache.pinot.thirdeye.detection.alert.DetectionAlertFilter;
import org.apache.pinot.thirdeye.detection.alert.DetectionAlertFilterNotification;
import org.apache.pinot.thirdeye.detection.alert.DetectionAlertFilterResult;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.apache.pinot.thirdeye.detection.DetectionTestUtils.*;


public class ToAllRecipientsDetectionAlertFilterTest {

  private static final String PROP_RECIPIENTS = "recipients";
  private static final String PROP_CLASS_NAME = "className";
  private static final String PROP_EMAIL_SCHEME = "emailScheme";
  private static final String PROP_JIRA_SCHEME = "jiraScheme";
  private static final String PROP_ASSIGNEE = "assignee";
  private static final String PROP_TO = "to";
  private static final String PROP_CC = "cc";
  private static final String PROP_BCC = "bcc";
  private static final Set<String> PROP_EMPTY_TO_VALUE = new HashSet<>();
  private static final Set<String> PROP_TO_VALUE = new HashSet<>(Arrays.asList("test@test.com", "test@test.org"));
  private static final Set<String> PROP_CC_VALUE = new HashSet<>(Arrays.asList("cctest@test.com", "cctest@test.org"));
  private static final Set<String> PROP_BCC_VALUE = new HashSet<>(Arrays.asList("bcctest@test.com", "bcctest@test.org"));
  private static final String PROP_DETECTION_CONFIG_IDS = "detectionConfigIds";
  private static final List<Long> PROP_ID_VALUE = Arrays.asList(1001L, 1002L);
  private static final String PROP_SEND_ONCE = "sendOnce";

  private DetectionAlertFilter alertFilter;
  private List<MergedAnomalyResultDTO> detectedAnomalies;
  private MockDataProvider provider;
  private DetectionAlertConfigDTO alertConfig;

  @BeforeMethod
  public void beforeMethod() {
    this.detectedAnomalies = new ArrayList<>();
    this.detectedAnomalies.add(makeAnomaly(1001L, 1500, 2000));
    this.detectedAnomalies.add(makeAnomaly(1001L,0, 1000));
    this.detectedAnomalies.add(makeAnomaly(1002L,0, 1000));
    this.detectedAnomalies.add(makeAnomaly(1002L,1100, 1500));
    this.detectedAnomalies.add(makeAnomaly(1002L,3333, 9999));
    this.detectedAnomalies.add(makeAnomaly(1003L,1100, 1500));

    // Anomalies generated by legacy pipeline
    this.detectedAnomalies.add(makeAnomaly(null, 1000L, 1100, 1500));
    this.detectedAnomalies.add(makeAnomaly(null, 1002L, 0, 1000));
    this.detectedAnomalies.add(makeAnomaly(null, 1002L, 1100, 2000));

    this.provider = new MockDataProvider()
        .setAnomalies(this.detectedAnomalies);

    this.alertConfig = createDetectionAlertConfig();
  }

  private DetectionAlertConfigDTO createDetectionAlertConfig() {
    DetectionAlertConfigDTO alertConfig = new DetectionAlertConfigDTO();

    Map<String, Object> properties = new HashMap<>();
    properties.put(PROP_DETECTION_CONFIG_IDS, PROP_ID_VALUE);
    alertConfig.setProperties(properties);

    Map<String, Object> alertSchemes = new HashMap<>();
    Map<String, Object> emailScheme = new HashMap<>();
    Map<String, Set<String>> recipients = new HashMap<>();
    recipients.put(PROP_TO, PROP_TO_VALUE);
    recipients.put(PROP_CC, PROP_CC_VALUE);
    recipients.put(PROP_BCC, PROP_BCC_VALUE);
    emailScheme.put(PROP_RECIPIENTS, recipients);
    alertSchemes.put(PROP_EMAIL_SCHEME, emailScheme);
    alertConfig.setAlertSchemes(alertSchemes);

    Map<Long, Long> vectorClocks = new HashMap<>();
    vectorClocks.put(PROP_ID_VALUE.get(0), 0L);
    alertConfig.setVectorClocks(vectorClocks);

    return alertConfig;
  }

  private DetectionAlertConfigDTO createDetectionAlertConfigWithJira() {
    DetectionAlertConfigDTO alertConfig = new DetectionAlertConfigDTO();

    Map<String, Object> properties = new HashMap<>();
    properties.put(PROP_DETECTION_CONFIG_IDS, PROP_ID_VALUE);
    alertConfig.setProperties(properties);

    Map<String, Object> alertSchemes = new HashMap<>();
    Map<String, Object> jiraScheme = new HashMap<>();
    jiraScheme.put(PROP_ASSIGNEE, "test");
    alertSchemes.put(PROP_JIRA_SCHEME, jiraScheme);
    alertConfig.setAlertSchemes(alertSchemes);

    Map<Long, Long> vectorClocks = new HashMap<>();
    vectorClocks.put(PROP_ID_VALUE.get(0), 0L);
    alertConfig.setVectorClocks(vectorClocks);

    return alertConfig;
  }

  @Test
  public void testGetAlertFilterResultWithJira() throws Exception {
    DetectionAlertConfigDTO alertConfig = createDetectionAlertConfigWithJira();
    this.alertFilter = new ToAllRecipientsDetectionAlertFilter(this.provider, alertConfig,2500L);

    DetectionAlertFilterResult result = this.alertFilter.run();

    DetectionAlertFilterNotification notification = AlertFilterUtils.makeJiraNotifications(this.alertConfig, "test");
    Assert.assertEquals(result.getResult().get(notification), new HashSet<>(this.detectedAnomalies.subList(0, 4)));
  }

  @Test
  public void testGetAlertFilterResult() throws Exception {
    this.alertFilter = new ToAllRecipientsDetectionAlertFilter(this.provider, this.alertConfig,2500L);

    DetectionAlertFilterResult result = this.alertFilter.run();

    DetectionAlertFilterNotification notification = AlertFilterUtils.makeEmailNotifications(
        this.alertConfig, PROP_TO_VALUE, PROP_CC_VALUE, PROP_BCC_VALUE);
    Assert.assertEquals(result.getResult().get(notification), new HashSet<>(this.detectedAnomalies.subList(0, 4)));
  }

  @Test
  public void testAlertFilterFeedback() throws Exception {
    this.alertConfig.getProperties().put(PROP_DETECTION_CONFIG_IDS, Collections.singletonList(1003L));
    this.alertFilter = new ToAllRecipientsDetectionAlertFilter(this.provider, this.alertConfig,2500L);

    AnomalyFeedbackDTO feedbackAnomaly = new AnomalyFeedbackDTO();
    feedbackAnomaly.setFeedbackType(AnomalyFeedbackType.ANOMALY);

    AnomalyFeedbackDTO feedbackNoFeedback = new AnomalyFeedbackDTO();
    feedbackNoFeedback.setFeedbackType(AnomalyFeedbackType.NO_FEEDBACK);

    MergedAnomalyResultDTO anomalyWithFeedback = makeAnomaly(1003L, 1234, 9999);
    anomalyWithFeedback.setFeedback(feedbackAnomaly);

    MergedAnomalyResultDTO anomalyWithoutFeedback = makeAnomaly(1003L, 1235, 9999);
    anomalyWithoutFeedback.setFeedback(feedbackNoFeedback);

    MergedAnomalyResultDTO anomalyWithNull = makeAnomaly(1003L, 1236, 9999);
    anomalyWithNull.setFeedback(null);

    this.detectedAnomalies.add(anomalyWithFeedback);
    this.detectedAnomalies.add(anomalyWithoutFeedback);
    this.detectedAnomalies.add(anomalyWithNull);

    DetectionAlertFilterResult result = this.alertFilter.run();
    Assert.assertEquals(result.getResult().size(), 1);

    DetectionAlertFilterNotification notification = AlertFilterUtils.makeEmailNotifications(
        this.alertConfig, PROP_TO_VALUE, PROP_CC_VALUE, PROP_BCC_VALUE);
    Assert.assertTrue(result.getResult().containsKey(notification));
    Assert.assertEquals(result.getResult().get(notification).size(), 3);
    Assert.assertTrue(result.getResult().get(notification).contains(this.detectedAnomalies.get(5)));
    Assert.assertTrue(result.getResult().get(notification).contains(anomalyWithoutFeedback));
    Assert.assertTrue(result.getResult().get(notification).contains(anomalyWithNull));
  }

  @Test
  public void testAlertFilterNoResend() throws Exception {
    // Assume below 2 anomalies have already been notified
    MergedAnomalyResultDTO existingOld = makeAnomaly(1001L, 1000, 1100);
    existingOld.setCreatedTime(1100L);
    MergedAnomalyResultDTO existingNew = makeAnomaly(1001L, 1100, 1200);
    existingNew.setCreatedTime(1200L);

    // This newly detected anomaly needs to be notified to the user
    MergedAnomalyResultDTO existingFuture = makeAnomaly(1001L, 1200, 1300);
    existingFuture.setCreatedTime(1300L);

    this.detectedAnomalies.clear();
    this.detectedAnomalies.add(existingOld);
    this.detectedAnomalies.add(existingNew);
    this.detectedAnomalies.add(existingFuture);

    this.alertConfig.setVectorClocks(Collections.singletonMap(1001L, 1200L));

    this.alertFilter = new ToAllRecipientsDetectionAlertFilter(this.provider, this.alertConfig,2500L);

    DetectionAlertFilterResult result = this.alertFilter.run();

    DetectionAlertFilterNotification notification = AlertFilterUtils.makeEmailNotifications(
        this.alertConfig, PROP_TO_VALUE, PROP_CC_VALUE, PROP_BCC_VALUE);
    Assert.assertTrue(result.getResult().containsKey(notification));
    Assert.assertEquals(result.getResult().get(notification).size(), 1);
    Assert.assertTrue(result.getResult().get(notification).contains(existingFuture));
  }

  @Test
  public void testAlertFilterResend() throws Exception {
    MergedAnomalyResultDTO existingOld = makeAnomaly(1001L, 1000, 1100);
    existingOld.setCreatedTime(1100L);

    MergedAnomalyResultDTO existingNew = makeAnomaly(1001L, 1100, 1200);
    existingNew.setCreatedTime(1200L);

    MergedAnomalyResultDTO existingFuture = makeAnomaly(1001L, 1200, 1300);
    existingFuture.setCreatedTime(1300L);

    this.detectedAnomalies.clear();
    this.detectedAnomalies.add(existingOld);
    this.detectedAnomalies.add(existingNew);
    this.detectedAnomalies.add(existingFuture);

    this.alertConfig.setVectorClocks(Collections.singletonMap(1001L, 1100L));
    this.alertConfig.getProperties().put(PROP_SEND_ONCE, false);

    this.alertFilter = new ToAllRecipientsDetectionAlertFilter(this.provider, this.alertConfig,2500L);

    DetectionAlertFilterResult result = this.alertFilter.run();

    DetectionAlertFilterNotification notification = AlertFilterUtils.makeEmailNotifications(
        this.alertConfig, PROP_TO_VALUE, PROP_CC_VALUE, PROP_BCC_VALUE);
    Assert.assertEquals(result.getResult().get(notification).size(), 2);
    Assert.assertTrue(result.getResult().get(notification).contains(existingNew));
    Assert.assertTrue(result.getResult().get(notification).contains(existingFuture));
  }

  @Test
  public void testGetAlertFilterResultWhenNoRecipient() throws Exception {
    Map<String, Object> properties = ConfigUtils.getMap(this.alertConfig.getProperties().get(PROP_RECIPIENTS));
    properties.put(PROP_TO, PROP_EMPTY_TO_VALUE);
    this.alertConfig.setProperties(properties);
    this.alertFilter = new ToAllRecipientsDetectionAlertFilter(this.provider, this.alertConfig,2500L);

    DetectionAlertFilterResult result = this.alertFilter.run();
    Assert.assertEquals(result.getResult().size(), 1);
  }
}