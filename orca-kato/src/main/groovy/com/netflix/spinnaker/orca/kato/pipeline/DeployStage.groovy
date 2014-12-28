/*
 * Copyright 2014 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.netflix.spinnaker.orca.kato.pipeline

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import com.google.common.annotations.VisibleForTesting
import com.netflix.spinnaker.orca.kato.tasks.CreateDeployTask
import com.netflix.spinnaker.orca.kato.tasks.MonitorKatoTask
import com.netflix.spinnaker.orca.kato.tasks.ServerGroupCacheForceRefreshTask
import com.netflix.spinnaker.orca.kato.tasks.WaitForUpInstancesTask
import com.netflix.spinnaker.orca.pipeline.model.Stage
import org.springframework.batch.core.Step
import org.springframework.stereotype.Component

@Component
@CompileStatic
class DeployStage extends DeployStrategyStage {

  public static final String MAYO_CONFIG_TYPE = "deploy"

  DeployStage() {
    super(MAYO_CONFIG_TYPE)
  }

  @VisibleForTesting
  @Override
  protected List<Step> basicSteps() {
    def step1 = buildStep("createDeploy", CreateDeployTask)
    def step2 = buildStep("monitorDeploy", MonitorKatoTask)
    def step3 = buildStep("forceCacheRefresh", ServerGroupCacheForceRefreshTask)
    def step4 = buildStep("waitForUpInstances", WaitForUpInstancesTask)
    def step5 = buildStep("forceCacheRefresh", ServerGroupCacheForceRefreshTask)
    [step1, step2, step3, step4, step5]
  }

  @Override
  protected ClusterConfig determineClusterForCleanup(Stage stage) {
    ClusterConfig.fromContext(stage.context)
  }

  @Override
  @CompileDynamic
  protected String strategy(Stage stage) {
    stage.context.containsKey("cluster") ? stage.context.cluster?.strategy : stage.context.strategy
  }
}
