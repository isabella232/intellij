/*
 * Copyright 2019 The Bazel Authors. All rights reserved.
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
package com.google.idea.blaze.base.settings.ui;

import static com.google.common.collect.ImmutableList.toImmutableList;

import com.google.idea.blaze.base.settings.Blaze;
import com.google.idea.blaze.base.settings.SearchableOptionsHelper;
import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.options.CompositeConfigurable;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.options.UnnamedConfigurable;
import com.intellij.ui.components.panels.VerticalLayout;
import java.util.Arrays;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JPanel;

/** Settings configurable combining settings from multiple nested configurables. */
public class BlazeUserSettingsCompositeConfigurable
    extends CompositeConfigurable<UnnamedConfigurable> implements SearchableConfigurable {

  /** Allows other modules to contribute user settings. */
  public interface UiContributor {
    ExtensionPointName<UiContributor> EP_NAME =
        ExtensionPointName.create("com.google.idea.blaze.SettingsUiContributor");

    UnnamedConfigurable getConfigurable(SearchableOptionsHelper helper);
  }

  public static final String ID = "blaze.view";

  @Override
  public String getId() {
    return ID;
  }

  @Override
  public String getDisplayName() {
    return Blaze.defaultBuildSystemName() + " Settings";
  }

  @Override
  public JComponent createComponent() {
    JPanel panel = new JPanel(new VerticalLayout(/* gap= */ 0));
    for (UnnamedConfigurable configurable : getConfigurables()) {
      JComponent component = configurable.createComponent();
      if (component != null) {
        panel.add(component);
      }
    }
    return panel;
  }

  @Override
  protected List<UnnamedConfigurable> createConfigurables() {
    SearchableOptionsHelper helper = new SearchableOptionsHelper(this);
    return Arrays.stream(UiContributor.EP_NAME.getExtensions())
        .map(c -> c.getConfigurable(helper))
        .collect(toImmutableList());
  }
}