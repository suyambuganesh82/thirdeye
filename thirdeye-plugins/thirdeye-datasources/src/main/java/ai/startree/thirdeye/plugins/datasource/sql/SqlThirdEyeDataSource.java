/*
 * Copyright (c) 2022 StarTree Inc. All rights reserved.
 * Confidential and Proprietary Information of StarTree Inc.
 */

package ai.startree.thirdeye.plugins.datasource.sql;

import ai.startree.thirdeye.spi.datalayer.bao.DatasetConfigManager;
import ai.startree.thirdeye.spi.datalayer.bao.MetricConfigManager;
import ai.startree.thirdeye.spi.datalayer.dto.DatasetConfigDTO;
import ai.startree.thirdeye.spi.datasource.DataSourceUtils;
import ai.startree.thirdeye.spi.datasource.RelationalQuery;
import ai.startree.thirdeye.spi.datasource.ThirdEyeDataSource;
import ai.startree.thirdeye.spi.datasource.ThirdEyeDataSourceContext;
import ai.startree.thirdeye.spi.datasource.ThirdEyeRequestV2;
import ai.startree.thirdeye.spi.datasource.resultset.ThirdEyeResultSetGroup;
import ai.startree.thirdeye.spi.detection.v2.DataTable;
import com.google.common.cache.LoadingCache;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SqlThirdEyeDataSource implements ThirdEyeDataSource {

  private static final Logger LOG = LoggerFactory.getLogger(SqlThirdEyeDataSource.class);

  private LoadingCache<RelationalQuery, ThirdEyeResultSetGroup> sqlResponseCache;
  private MetricConfigManager metricConfigManager;
  private SqlResponseCacheLoader sqlResponseCacheLoader;
  private String name;

  @Override
  public void init(final ThirdEyeDataSourceContext context) {
    Map<String, Object> properties = context.getDataSourceDTO().getProperties();

    metricConfigManager = context.getMetricConfigManager();
    final DatasetConfigManager datasetConfigManager = context.getDatasetConfigManager();
    sqlResponseCacheLoader = new SqlResponseCacheLoader(properties,
        metricConfigManager,
        datasetConfigManager);
    sqlResponseCache = DataSourceUtils.buildResponseCache(sqlResponseCacheLoader);
    name = MapUtils.getString(properties, "name", SqlThirdEyeDataSource.class.getSimpleName());
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public DataTable fetchDataTable(final ThirdEyeRequestV2 request) throws Exception {
    throw new UnsupportedOperationException();
  }

  /**
   * Returns the cached ResultSetGroup corresponding to the given Presto query.
   *
   * @param SQLQuery the query that is specifically constructed for Presto.
   * @return the corresponding ThirdEyeResultSet to the given Presto query.
   */
  private ThirdEyeResultSetGroup executeSQL(SqlQuery SQLQuery) throws Exception {
    ThirdEyeResultSetGroup thirdEyeResultSetGroup;
    try {
      thirdEyeResultSetGroup = sqlResponseCache.get(SQLQuery);
    } catch (Exception e) {
      throw e;
    }
    return thirdEyeResultSetGroup;
  }

  @Override
  public List<String> getDatasets() throws Exception {
    throw new UnsupportedOperationException();
  }

  @Override
  public void clear() throws Exception {
    // left blank
  }

  @Override
  public void close() throws Exception {
    // left blank
  }

  @Override
  public long getMaxDataTime(final DatasetConfigDTO datasetConfig) throws Exception {
    return sqlResponseCacheLoader.getMaxDataTime(datasetConfig);
  }
}
