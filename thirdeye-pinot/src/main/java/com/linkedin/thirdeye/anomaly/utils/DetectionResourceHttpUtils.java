package com.linkedin.thirdeye.anomaly.utils;

import com.linkedin.thirdeye.anomalydetection.performanceEvaluation.PerformanceEvaluationMethod;
import java.io.IOException;

import org.apache.http.HttpHost;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;

/**
 * Utility classes for calling detector endpoints to execute/schedule jobs
 */
public class DetectionResourceHttpUtils extends AbstractResourceHttpUtils {

  private static final String DETECTION_JOB_ENDPOINT = "/api/detection-job/";
  private static final String ADHOC = "/ad-hoc";
  private static final String BACKFILL = "/generateAnomaliesInRange";
  private static final String AUTOTUNE_FILTER = "autotune/filter/";
  private static final String EVAL_FILTER = "eval/filter/";
  private static final String EVAL_AUTOTUNE = "eval/autotune/";
  private static final String CHECK_HAS_LABELS = "initautotune/checkhaslabel/";
  private static final String INIT_AUTOTUNE = "initautotune/filter/";
  private static final String REPLAY_FUNCTION = "replay/function/";

  public DetectionResourceHttpUtils(String detectionHost, int detectionPort) {
    super(new HttpHost(detectionHost, detectionPort));
  }

  public String enableAnomalyFunction(String id) throws ClientProtocolException, IOException {
    HttpPost req = new HttpPost(DETECTION_JOB_ENDPOINT + id);
    return callJobEndpoint(req);
  }

  public String disableAnomalyFunction(String id) throws ClientProtocolException, IOException {
    HttpDelete req = new HttpDelete(DETECTION_JOB_ENDPOINT + id);
    return callJobEndpoint(req);
  }

  public String runAdhocAnomalyFunction(String id, String startTimeIso, String endTimeIso)
      throws ClientProtocolException, IOException {
    HttpPost req = new HttpPost(
        DETECTION_JOB_ENDPOINT + id + ADHOC + "?start=" + startTimeIso + "&end=" + endTimeIso);
    return callJobEndpoint(req);
  }

  public String runBackfillAnomalyFunction(String id, String startTimeIso, String endTimeIso, boolean forceBackfill)
      throws ClientProtocolException, IOException {
    HttpPost req = new HttpPost(
        DETECTION_JOB_ENDPOINT + id + BACKFILL + "?start=" + startTimeIso + "&end=" + endTimeIso + "&force=" + forceBackfill);
    return callJobEndpoint(req);
  }

  public String runAutoTune(Long functionId, Long startTime, Long endTime, String autoTuneType, String holidayStarts, String holidayEnds) throws Exception {
    HttpPost req = new HttpPost(
        DETECTION_JOB_ENDPOINT + AUTOTUNE_FILTER + functionId
            + "?startTime=" + startTime
        + "&endTime=" + endTime
        + "&autoTuneType=" + autoTuneType
        + "&holidayStarts=" + holidayStarts
        + "&holidayEnds=" + holidayEnds
    );
    return callJobEndpoint(req);
  }

  public String getEvalStatsAlertFilter(Long functionId, Long startTime, Long endTime, String holidayStarts, String holidayEnds) throws Exception{
    HttpPost req = new HttpPost(
        DETECTION_JOB_ENDPOINT + EVAL_FILTER + functionId
            + "?startTime=" + startTime
            + "&endTime=" + endTime
            + "&holidayStarts=" + holidayStarts
            + "&holidayEnds=" + holidayEnds
    );
    return callJobEndpoint(req);
  }

  public String evalAutoTune(long autotuneId, long startTime, long endTime, String holidayStarts, String holidayEnds) throws Exception{
    HttpPost req = new HttpPost(
        DETECTION_JOB_ENDPOINT + EVAL_AUTOTUNE + autotuneId
        + "?startTime=" + startTime
        + "&endTime=" + endTime
        + "&holidayStarts=" + holidayStarts
        + "&holidayEnds=" + holidayEnds
    );
    return callJobEndpoint(req);
  }

  public String checkHasLabels(long functionId, long startTime, long endTime, String holidayStarts, String holidayEnds) throws IOException {
    HttpPost req = new HttpPost(
        DETECTION_JOB_ENDPOINT + CHECK_HAS_LABELS + functionId
        + "?startTime=" + startTime
        + "&endTime=" + endTime
        + "&holidayStarts=" + holidayStarts
        + "&holidayEnds=" + holidayEnds
    );
    return callJobEndpoint(req);
  }

  public String initAutoTune(Long functionId, Long startTime, Long endTime, String autoTuneType, int nExpected, String holidayStarts, String holidayEnds)
      throws IOException {
    HttpPost req = new HttpPost(
        DETECTION_JOB_ENDPOINT + INIT_AUTOTUNE + functionId
            + "?startTime=" + startTime
            + "&endTime=" + endTime
            + "&autoTuneType=" + autoTuneType
            + "&nExpected=" + nExpected
            + "&holidayStarts=" + holidayStarts
            + "&holidayEnds=" + holidayEnds
    );
    return callJobEndpoint(req);
  }

  public String runFunctionReplay(Long id, String startTimeISO, String endTimeISO, String tuningParameters,
      PerformanceEvaluationMethod evaluationMethod, double goal, boolean speedup)
      throws ClientProtocolException, IOException {
    HttpPost req = new HttpPost(
        DETECTION_JOB_ENDPOINT + REPLAY_FUNCTION + id + "?start=" + startTimeISO + "&end=" + endTimeISO
            + "&tune=" + tuningParameters + "&goal=" + goal + "&evalMethod=" + evaluationMethod.name()
            + "&speedup=" + Boolean.toString(speedup));
    return callJobEndpoint(req);
  }
}
