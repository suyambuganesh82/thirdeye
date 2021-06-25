/*
 *
 *  * Licensed to the Apache Software Foundation (ASF) under one
 *  * or more contributor license agreements.  See the NOTICE file
 *  * distributed with this work for additional information
 *  * regarding copyright ownership.  The ASF licenses this file
 *  * to you under the Apache License, Version 2.0 (the
 *  * "License"); you may not use this file except in compliance
 *  * with the License.  You may obtain a copy of the License at
 *  *
 *  *   http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing,
 *  * software distributed under the License is distributed on an
 *  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  * KIND, either express or implied.  See the License for the
 *  * specific language governing permissions and limitations
 *  * under the License.
 *
 *
 */
package org.apache.pinot.thirdeye.spi.detection.v2;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.pinot.thirdeye.spi.api.AnomalyApi;
import org.apache.pinot.thirdeye.spi.api.DetectionDataApi;
import org.apache.pinot.thirdeye.spi.api.DetectionEvaluationApi;
import org.apache.pinot.thirdeye.spi.datalayer.dto.MergedAnomalyResultDTO;
import org.apache.pinot.thirdeye.spi.detection.model.TimeSeries;
import org.apache.pinot.thirdeye.spi.util.ApiBeanMapper;

/**
 * The detection result. Contains a list of anomalies detected and time series
 * (which can include time stamps, predicted baseline, current value, upper/lower bounds)
 */
public class DetectionResult implements DetectionPipelineResult {

  private final List<MergedAnomalyResultDTO> anomalies;
  private final TimeSeries timeseries;

  private DetectionResult(List<MergedAnomalyResultDTO> anomalies, TimeSeries timeseries) {
    this.anomalies = anomalies;
    this.timeseries = timeseries;
  }

  /**
   * Create a empty detection result
   *
   * @return the empty detection result
   */
  public static DetectionResult empty() {
    return new DetectionResult(Collections.emptyList(), TimeSeries.empty());
  }

  /**
   * Create a detection result from a list of anomalies
   *
   * @param anomalies the list of anomalies generated
   * @return the detection result contains the list of anomalies
   */
  public static DetectionResult from(List<MergedAnomalyResultDTO> anomalies) {
    return new DetectionResult(anomalies, TimeSeries.empty());
  }

  /**
   * Create a detection result from a list of anomalies and time series
   *
   * @param anomalies the list of anomalies generated
   * @param timeSeries the time series which including the current, predicted baseline and
   *     optionally upper and lower bounds
   * @return the detection result contains the list of anomalies and the time series
   */
  public static DetectionResult from(List<MergedAnomalyResultDTO> anomalies,
      TimeSeries timeSeries) {
    return new DetectionResult(anomalies, timeSeries);
  }

  public List<MergedAnomalyResultDTO> getAnomalies() {
    return anomalies;
  }

  public TimeSeries getTimeseries() {
    return timeseries;
  }

  @Override
  public String toString() {
    return "DetectionResult{" + "anomalies=" + anomalies + ", timeseries=" + timeseries + '}';
  }

  public DetectionEvaluationApi toApi() {
    DetectionEvaluationApi api = new DetectionEvaluationApi();
    final List<AnomalyApi> anomalyApis = new ArrayList<>();
    for (MergedAnomalyResultDTO anomalyDto : anomalies) {
      anomalyApis.add(ApiBeanMapper.toApi(anomalyDto));
    }
    api.setAnomalies(anomalyApis);
    api.setData(getData());
    return api;
  }

  private DetectionDataApi getData() {
    final DetectionDataApi detectionDataApi = new DetectionDataApi();
    detectionDataApi.setCurrent(timeseries.getCurrent().toList());
    detectionDataApi.setExpected(timeseries.getPredictedBaseline().toList());
    detectionDataApi.setTimestamp(timeseries.getTime().toList());
    if (timeseries.hasLowerBound()) {
      detectionDataApi.setLowerBound(timeseries.getPredictedLowerBound().toList());
    }
    if (timeseries.hasUpperBound()) {
      detectionDataApi.setUpperBound(timeseries.getPredictedUpperBound().toList());
    }
    return detectionDataApi;
  }

  @Override
  public List<DetectionResult> getDetectionResults() {
    return ImmutableList.of(this);
  }
}
