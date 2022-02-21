/*
 * Copyright (c) 2022 StarTree Inc. All rights reserved.
 * Confidential and Proprietary Information of StarTree Inc.
 */

package ai.startree.thirdeye.detection.anomaly.alert.grouping.filter;

import java.util.Collections;
import java.util.Map;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

public class AlertGroupFilterFactory {

  public static final String GROUP_FILTER_TYPE_KEY = "type";
  private static final AlertGroupFilter DUMMY_ALERT_GROUP_FILTER = new DummyAlertGroupFilter();

  public enum GroupFilterType {
    DUMMY, SIZE_SEVERITY
  }

  public static AlertGroupFilter fromSpec(Map<String, String> spec) {
    if (MapUtils.isEmpty(spec)) {
      spec = Collections.emptyMap();
    }
    AlertGroupFilter alertGroupFilter = fromStringType(spec.get(GROUP_FILTER_TYPE_KEY));
    alertGroupFilter.setParameters(spec);

    return alertGroupFilter;
  }

  private static AlertGroupFilter fromStringType(String type) {
    if (StringUtils.isBlank(type)) { // backward compatibility
      return DUMMY_ALERT_GROUP_FILTER;
    }

    GroupFilterType filterType = GroupFilterType.DUMMY;
    for (GroupFilterType enumFilterType : GroupFilterType.values()) {
      if (enumFilterType.name().compareToIgnoreCase(type) == 0) {
        filterType = enumFilterType;
        break;
      }
    }

    switch (filterType) {
      case DUMMY: // speed optimization for most use cases
        return DUMMY_ALERT_GROUP_FILTER;
      case SIZE_SEVERITY:
        return new SizeSeverityAlertGroupFilter();
      default:
        return DUMMY_ALERT_GROUP_FILTER;
    }
  }
}