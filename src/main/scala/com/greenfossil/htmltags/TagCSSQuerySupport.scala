/*
 * Copyright 2022 Greenfossil Pte Ltd
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

package com.greenfossil.htmltags

import com.greenfossil.htmltags.css.parser.*

trait TagCSSQuerySupport[T]:
      @throws(classOf[IllegalArgumentException])
      def cssQuery(selectorString: String): Seq[Tag]

      def cssQuery(selectorGroups: List[SelectorGroup]): Seq[Tag]

      def $(selectorString: String): Seq[Tag] = cssQuery(selectorString)

      def $(selectorGroups: List[SelectorGroup]): Seq[Tag] = cssQuery(selectorGroups)

      def addNodes(selectorString: String, newNodes: Node*): T

      def deleteTags(selectorString: String, deleteAttrs: com.greenfossil.htmltags.Attribute*): T

      def replaceNodes(selectorString: String, newNodes: Node*): T

      def extractTags(cssSelector: String): (Tag | Seq[Tag], Seq[Tag])

