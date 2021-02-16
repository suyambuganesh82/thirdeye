/**
 * Copyright (C) 2014-2018 LinkedIn Corp. (pinot-core@linkedin.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.pinot.thirdeye.datasource.csv;

import com.google.inject.Injector;
import java.net.URL;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import org.apache.pinot.thirdeye.common.ThirdEyeConfiguration;
import org.apache.pinot.thirdeye.dataframe.DataFrame;
import org.apache.pinot.thirdeye.dataframe.util.DataFrameUtils;
import org.apache.pinot.thirdeye.dataframe.util.MetricSlice;
import org.apache.pinot.thirdeye.dataframe.util.RequestContainer;
import org.apache.pinot.thirdeye.datalayer.bao.TestDbEnv;
import org.apache.pinot.thirdeye.datalayer.dto.DatasetConfigDTO;
import org.apache.pinot.thirdeye.datalayer.dto.MetricConfigDTO;
import org.apache.pinot.thirdeye.datasource.DAORegistry;
import org.apache.pinot.thirdeye.datasource.ThirdEyeCacheRegistry;
import org.apache.pinot.thirdeye.datasource.ThirdEyeResponse;
import org.apache.pinot.thirdeye.datasource.mock.MockThirdEyeDataSourceIntegrationTest;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class CSVThirdEyeDataSourceIntegrationTest {

  private DAORegistry daoRegistry;

  @BeforeMethod
  void beforeMethod() {
    final Injector injector = new TestDbEnv().getInjector();
    daoRegistry = injector.getInstance(DAORegistry.class);
  }

  @AfterMethod(alwaysRun = true)
  void afterMethod() {
  }

  @Test
  public void integrationTest() throws Exception {
    URL dataSourcesConfig = this.getClass().getResource("data-sources-config.yml");

    DatasetConfigDTO datasetConfigDTO = new DatasetConfigDTO();

    datasetConfigDTO.setDataset("business");
    datasetConfigDTO.setDataSource("CSVThirdEyeDataSource");
    datasetConfigDTO.setTimeDuration(1);
    datasetConfigDTO.setTimeUnit(TimeUnit.HOURS);

    daoRegistry.getDatasetConfigDAO().save(datasetConfigDTO);
    Assert.assertNotNull(datasetConfigDTO.getId());

    MetricConfigDTO configDTO = new MetricConfigDTO();
    configDTO.setName("views");
    configDTO.setDataset("business");
    configDTO.setAlias("business::views");

    daoRegistry.getMetricConfigDAO().save(configDTO);
    Assert.assertNotNull(configDTO.getId());

    ThirdEyeConfiguration thirdEyeConfiguration = new ThirdEyeConfiguration();
    thirdEyeConfiguration.setDataSources(dataSourcesConfig.toString());

    final ThirdEyeCacheRegistry thirdEyeCacheRegistry = TestDbEnv
        .getInstance(ThirdEyeCacheRegistry.class);

    thirdEyeCacheRegistry.initializeCaches(thirdEyeConfiguration);

    MetricSlice slice = MetricSlice.from(configDTO.getId(), 0, 7200000);
    RequestContainer requestContainer = MockThirdEyeDataSourceIntegrationTest
        .makeAggregateRequest(slice, Collections.emptyList(), -1, "ref",
            thirdEyeCacheRegistry);
    ThirdEyeResponse response = thirdEyeCacheRegistry.getDataSourceCache()
        .getQueryResult(requestContainer.getRequest());
    DataFrame df = DataFrameUtils.evaluateResponse(response, requestContainer,
        thirdEyeCacheRegistry);

    Assert.assertEquals(df.getDoubles(DataFrame.COL_VALUE).toList(),
        Collections.singletonList(1503d));
  }
}
