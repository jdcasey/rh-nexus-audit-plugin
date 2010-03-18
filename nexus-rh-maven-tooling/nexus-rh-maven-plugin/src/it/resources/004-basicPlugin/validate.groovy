/*
 * Copyright (C) 2006-2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
def pluginModel = new XmlSlurper().parseText(content);
def pomModel = new XmlSlurper().parse(pomFile);

java.util.List<String> result = new java.util.ArrayList<String>();

if ( pluginModel.groupId != pomModel.parent.groupId ){result.add( "Group ID" );}
if ( pluginModel.artifactId != pomModel.artifactId ){result.add( "Artifact ID" );}
if ( pluginModel.version != pomModel.version ){result.add( "Version" );}
if ( pluginModel.name != pomModel.name ){result.add( "Name" );}
if ( pluginModel.description != pomModel.description ){result.add( "Description" );}

// FIXME: Not sure this is still something that's included in the plugin descriptor.
//def components = pluginModel.components.'*'.text();
//
//if ( 3 != pluginModel.components.component.size() ){result.add( "Should contain 3 components" );}
//
//check for expected components
//[ "org.sonatype.plugin.test.ComponentExtentionPoint",
//  "org.sonatype.plugin.test.ManagedViaInterface",
//  "org.sonatype.plugin.test.ComponentManaged"
//].each {
//
//  if ( !components.contains(it) ){result.add( "components should contain: ${it}" );}
//  
//}

//dependencies
def expectedDepCount = pomModel.dependencies.dependency.findAll{
  it.scope.text().equals("") || it.scope.text().equals("compile") || it.scope.text().equals("runtime")
}.size();

if ( expectedDepCount != pluginModel.classpathDependencies.classpathDependency.size()) {
  result.add( "Found: "+ pluginModel.classpathDependencies.classpathDependency.size() +
              " dependencies, expected: "+ expectedDepCount );
}

return result;