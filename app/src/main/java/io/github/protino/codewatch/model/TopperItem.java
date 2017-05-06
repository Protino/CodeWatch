/*
 * Copyright 2017 Gurupad Mamadapur
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package io.github.protino.codewatch.model;

import java.util.List;

/**
 * @author Gurupad Mamadapur
 */

public class TopperItem {
    private List<DefaultLeaderItem> defaultLeaderItems;

    public TopperItem(List<DefaultLeaderItem> defaultLeaderItems) {
        this.defaultLeaderItems = defaultLeaderItems;
    }

    public List<DefaultLeaderItem> getDefaultLeaderItems() {
        return defaultLeaderItems;
    }

    public void setDefaultLeaderItems(List<DefaultLeaderItem> defaultLeaderItems) {
        this.defaultLeaderItems = defaultLeaderItems;
    }
}
