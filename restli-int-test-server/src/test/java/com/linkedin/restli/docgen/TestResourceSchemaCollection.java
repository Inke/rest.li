/*
   Copyright (c) 2012 LinkedIn Corp.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

package com.linkedin.restli.docgen;


import com.linkedin.restli.internal.server.model.ResourceModel;
import com.linkedin.restli.internal.server.model.ResourceType;
import com.linkedin.restli.internal.server.model.RestLiApiBuilder;
import com.linkedin.restli.restspec.ResourceSchema;
import com.linkedin.restli.server.RestLiConfig;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * @author Keren Jin
 */
public class TestResourceSchemaCollection
{
  public TestResourceSchemaCollection()
  {
    final RestLiConfig config = new RestLiConfig();
    config.addResourcePackageNames("com.linkedin.restli.examples.groups.server.rest.impl",
                                   "com.linkedin.restli.examples.greetings.server",
                                   "com.linkedin.restli.examples.typeref.server");
    final Map<String, ResourceModel> rootResources = new RestLiApiBuilder(config).build();
    _schemas = ResourceSchemaCollection.loadOrCreateResourceSchema(rootResources);
  }

  @Test
  public void testRootWithResourceModel()
  {
    final Map<String, ResourceType> expectedTypes = new HashMap<String, ResourceType>();
    expectedTypes.put("com.linkedin.restli.examples.greetings.client.actions", ResourceType.ACTIONS);
    expectedTypes.put("com.linkedin.restli.examples.greetings.client.customTypes", ResourceType.COLLECTION);
    expectedTypes.put("com.linkedin.restli.examples.greetings.client.customTypes2", ResourceType.COLLECTION);
    expectedTypes.put("com.linkedin.restli.examples.greetings.client.customTypes3", ResourceType.ASSOCIATION);
    expectedTypes.put("com.linkedin.restli.examples.greetings.client.customTypes2.customTypes4", ResourceType.COLLECTION);
    expectedTypes.put("com.linkedin.restli.examples.greetings.client.exceptions", ResourceType.COLLECTION);
    expectedTypes.put("com.linkedin.restli.examples.greetings.client.exceptions2", ResourceType.COLLECTION);
    expectedTypes.put("com.linkedin.restli.examples.greetings.client.greetings", ResourceType.COLLECTION);
    expectedTypes.put("com.linkedin.restli.examples.greetings.client.greetingsAuth", ResourceType.COLLECTION);
    expectedTypes.put("com.linkedin.restli.examples.greetings.client.greetingsCallback", ResourceType.COLLECTION);
    expectedTypes.put("com.linkedin.restli.examples.greetings.client.greetingsPromise", ResourceType.COLLECTION);
    expectedTypes.put("com.linkedin.restli.examples.greetings.client.greetingsPromiseCtx", ResourceType.COLLECTION);
    expectedTypes.put("com.linkedin.restli.examples.greetings.client.greetingsTask", ResourceType.COLLECTION);
    expectedTypes.put("com.linkedin.restli.examples.greetings.client.greetingsold", ResourceType.COLLECTION);
    expectedTypes.put("com.linkedin.restli.examples.greetings.client.mixed", ResourceType.COLLECTION);
    expectedTypes.put("com.linkedin.restli.examples.greetings.client.stringKeys", ResourceType.COLLECTION);
    expectedTypes.put("com.linkedin.restli.examples.greetings.client.stringKeys.stringKeysSub", ResourceType.COLLECTION);
    expectedTypes.put("com.linkedin.restli.examples.greetings.client.complexKeys", ResourceType.COLLECTION);
    expectedTypes.put("com.linkedin.restli.examples.greetings.client.associations", ResourceType.ASSOCIATION);
    expectedTypes.put("com.linkedin.restli.examples.groups.client.groupMemberships", ResourceType.ASSOCIATION);
    expectedTypes.put("com.linkedin.restli.examples.groups.client.groupMembershipsComplex", ResourceType.COLLECTION);
    expectedTypes.put("com.linkedin.restli.examples.groups.client.groups", ResourceType.COLLECTION);
    expectedTypes.put("com.linkedin.restli.examples.groups.client.groups.contacts", ResourceType.COLLECTION);
    expectedTypes.put("noNamespace", ResourceType.COLLECTION);
    expectedTypes.put("noNamespace.noNamespaceSub", ResourceType.COLLECTION);
    expectedTypes.put("noNamespace.noNamespaceSub.subSub", ResourceType.COLLECTION);
    expectedTypes.put("com.linkedin.restli.examples.noNamespace.noNamespace", ResourceType.COLLECTION);
    expectedTypes.put("com.linkedin.restli.examples.typeref.client.typeref", ResourceType.COLLECTION);

    for (Map.Entry<String, ResourceSchema> entry: _schemas.getResources().entrySet())
    {
      final ResourceSchema schema = entry.getValue();
      final ResourceType actualType;
      if (schema.hasCollection())
      {
        actualType = ResourceType.COLLECTION;
      }
      else if (schema.hasAssociation())
      {
        actualType = ResourceType.ASSOCIATION;
      }
      else
      {
        Assert.assertTrue(schema.hasActionsSet());
        actualType = ResourceType.ACTIONS;
      }

      final String schemaFullName = getResourceSchemaFullName(schema, entry.getKey());
      final ResourceType expectedType = expectedTypes.get(schemaFullName);
      Assert.assertNotNull(expectedType);
      Assert.assertSame(actualType, expectedType, schemaFullName);
    }
  }

  @Test
  public void testSubresource()
  {
    final ResourceSchema groupsResource = _schemas.getResource("groups");
    final List<ResourceSchema> groupsSubresources = _schemas.getSubResources(groupsResource);
    Assert.assertEquals(groupsSubresources.size(), 1);
    final ResourceSchema groupsContactsResource = groupsSubresources.get(0);
    Assert.assertEquals(groupsContactsResource.getName(), "contacts");
    Assert.assertEquals(groupsContactsResource.getNamespace(), groupsResource.getNamespace());

    final ResourceSchema noNamespaceResource = _schemas.getResource("noNamespace");
    final List<ResourceSchema> actualNoNamespaceSubresources = _schemas.getSubResources(noNamespaceResource);
    Assert.assertEquals(actualNoNamespaceSubresources.size(), 2);

    final Set<String> expectedNoNamespaceSubresources = new HashSet<String>();
    expectedNoNamespaceSubresources.add("noNamespaceSub");
    expectedNoNamespaceSubresources.add("com.linkedin.restli.examples.noNamespace");

    for (ResourceSchema sub: actualNoNamespaceSubresources)
    {
      final String schemaFullName = getResourceSchemaFullName(sub, sub.getName());
      Assert.assertTrue(expectedNoNamespaceSubresources.contains(schemaFullName));
    }
  }

  // TODO ResourceSchemaCollection.createFromIdls()

  private String getResourceSchemaFullName(ResourceSchema schema, String name)
  {
    return (schema.hasNamespace() ? schema.getNamespace() + "." : "") + name;
  }

  private final ResourceSchemaCollection _schemas;
}
