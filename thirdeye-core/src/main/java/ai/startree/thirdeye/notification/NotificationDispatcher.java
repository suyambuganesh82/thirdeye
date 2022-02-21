/*
 * Copyright (c) 2022 StarTree Inc. All rights reserved.
 * Confidential and Proprietary Information of StarTree Inc.
 */

package ai.startree.thirdeye.notification;

import ai.startree.thirdeye.detection.alert.DetectionAlertFilterResult;
import ai.startree.thirdeye.spi.api.NotificationPayloadApi;
import ai.startree.thirdeye.spi.datalayer.dto.EmailSchemeDto;
import ai.startree.thirdeye.spi.datalayer.dto.MergedAnomalyResultDTO;
import ai.startree.thirdeye.spi.datalayer.dto.SubscriptionGroupDTO;
import ai.startree.thirdeye.spi.datalayer.dto.WebhookSchemeDto;
import ai.startree.thirdeye.spi.notification.NotificationService;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

@Singleton
public class NotificationDispatcher {

  private final NotificationPayloadBuilder notificationPayloadBuilder;
  private final NotificationServiceRegistry notificationServiceRegistry;

  private final SmtpConfiguration smtpConfig;

  @Inject
  public NotificationDispatcher(
      final NotificationPayloadBuilder notificationPayloadBuilder,
      final NotificationServiceRegistry notificationServiceRegistry,
      final NotificationConfiguration notificationConfiguration) {
    this.notificationPayloadBuilder = notificationPayloadBuilder;
    this.notificationServiceRegistry = notificationServiceRegistry;
    this.smtpConfig = notificationConfiguration.getSmtpConfiguration();
  }

  public void dispatch(
      final SubscriptionGroupDTO subscriptionGroup,
      final DetectionAlertFilterResult result) {
    final Set<MergedAnomalyResultDTO> anomalies = getAnomalies(subscriptionGroup, result);

    final NotificationPayloadApi payload = notificationPayloadBuilder.buildNotificationPayload(
        subscriptionGroup,
        anomalies);

    fireNotifications(subscriptionGroup, payload);
  }

  private void fireNotifications(final SubscriptionGroupDTO sg,
      final NotificationPayloadApi payload) {
    // Send out emails
    final EmailSchemeDto emailScheme = sg.getNotificationSchemes().getEmailScheme();
    if (emailScheme != null) {
      final Map<String, String> properties = buildEmailProperties();
      fireNotification("email", properties, payload);
    }

    // fire webhook
    final WebhookSchemeDto webhookScheme = sg.getNotificationSchemes().getWebhookScheme();
    if (webhookScheme != null) {
      final Map<String, String> properties = buildWebhookProperties(webhookScheme);
      fireNotification("webhook", properties, payload);
    }
  }

  public Set<MergedAnomalyResultDTO> getAnomalies(SubscriptionGroupDTO subscriptionGroup,
      final DetectionAlertFilterResult results) {
    return results
        .getResult()
        .entrySet()
        .stream()
        .filter(result -> subscriptionGroup.equals(result.getKey().getSubscriptionConfig()))
        .findFirst()
        .map(Entry::getValue)
        .orElse(null);
  }

  public Map<String, String> buildEmailProperties() {
    final Map<String, String> properties = new HashMap<>();
    properties.put("host", smtpConfig.getHost());
    properties.put("port", String.valueOf(smtpConfig.getPort()));
    properties.put("user", smtpConfig.getUser());
    properties.put("password", smtpConfig.getPassword());

    return properties;
  }

  private Map<String, String> buildWebhookProperties(final WebhookSchemeDto webhookScheme) {
    final Map<String, String> properties = new HashMap<>();
    properties.put("url", webhookScheme.getUrl());
    if (webhookScheme.getHashKey() != null) {
      properties.put("hashKey", webhookScheme.getHashKey());
    }
    return properties;
  }

  private void fireNotification(final String name,
      final Map<String, String> properties,
      final NotificationPayloadApi entity) {
    final NotificationService service = notificationServiceRegistry.get(name, properties);
    service.notify(entity);
  }
}