/*
 * Copyright (c) 2022 StarTree Inc. All rights reserved.
 * Confidential and Proprietary Information of StarTree Inc.
 */

package ai.startree.thirdeye.detection.alert.scheme;

import ai.startree.thirdeye.detection.alert.DetectionAlertFilterResult;
import ai.startree.thirdeye.spi.datalayer.dto.SubscriptionGroupDTO;

/**
 * Used in tests
 */
@SuppressWarnings("unused")
public class AnotherRandomAlerter extends NotificationScheme {

  @Override
  public void run(
      final SubscriptionGroupDTO subscriptionGroup,
      final DetectionAlertFilterResult result) {

  }
}