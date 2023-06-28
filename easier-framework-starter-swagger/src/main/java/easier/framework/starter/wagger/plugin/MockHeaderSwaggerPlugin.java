/*
 *
 *  Copyright 2015-2019 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */

package easier.framework.starter.wagger.plugin;

import cn.hutool.core.collection.CollUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.AllowableListValues;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

import static springfox.documentation.swagger.common.SwaggerPluginSupport.SWAGGER_PLUGIN_ORDER;

@Order
@RequiredArgsConstructor
public class MockHeaderSwaggerPlugin implements OperationBuilderPlugin {
    @Override
    public boolean supports(DocumentationType delimiter) {
        return SwaggerPluginSupport.pluginDoesApply(delimiter);
    }

    @Override
    public void apply(OperationContext context) {
        AllowableListValues allowableValues = new AllowableListValues(CollUtil.newArrayList("false", "true"), Boolean.class.getTypeName());
        Parameter header = new ParameterBuilder()
                .name("mockData")
                .description("模拟数据")
                .defaultValue("false")
                .required(false)
                .allowMultiple(false)
                .allowableValues(allowableValues)
                .parameterType("header")
                .order(SWAGGER_PLUGIN_ORDER)
                .modelRef(new ModelRef("boolean"))
                .build();
        context.operationBuilder().parameters(CollUtil.newArrayList(header));
    }


}


