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

import scala.language.implicitConversions

type FormInputType = Tag

given Conversion[(String, String, AttributeValueType), FormInputType] with 
  def apply(tup3: (String, String, AttributeValueType)) = input(tpe:=tup3._1, name:=tup3._2, value:=tup3._3)

given Conversion[(String, AttributeValueType), FormInputType] with 
  def apply(tup2: (String, AttributeValueType)) = input(name:=tup2._1, value:=tup2._2)

object Form:
  import com.greenfossil.htmltags.{action as _action, method as _method, tpe as _tpe}

  /**
    *
    * @param tups - a seq of tuple2 (name, value) or tuple3 (type, name, value)
    * @return - a list of inputs where value is not null
    */
  def inputs(tups: FormInputType *): Seq[Tag] = 
    tups.filter(_.attr("value").exists(_.notNull))

  /**
    *
    * @param tpe - e.g. text, hidden
    * @param tups (name, value)
    * @return
    */
  def inputs(tpe: String, tups: (String, AttributeValueType)*): Seq[Tag] = 
    tups.map(tup => input(_tpe:=tpe, name := tup._1, value:=tup._2))

  /**
    *
    * @param tups
    * @return - Seq of hidden input tags
    */
  def hidden(tups:(String, AttributeValueType)*): Seq[Tag] = 
    tups.map(tup => input(_tpe:= "hidden", name:=tup._1, value:=tup._2))

  /**
    *
    * @param tups
    * @return - Seq of hidden input tags
    */
  def text(tups: (String, AttributeValueType)*): Seq[Tag] = 
    tups.map(tup => input(tpe:="text", name := tup._1, value:=tup._2))

  /**
    * Method is POST
    * @param action
    * @param tups - a seq of tup2 (name, value) or tup3 (type, name, value)
    * @return
    */
  def urlencoded(action: String, tups: FormInputType*): Tag = 
    urlencoded(action, "POST")(inputs(tups*))

  /**
    * Create a urlencoded form
    *
    * @param action
    * @param method
    * @return
    */
  def urlencoded(action: String, method: String): Tag = 
    form(_method:=method, _action:=action, enctype:="application/x-www-form-urlencoded")

  /**
    * Create a multipart form
    * @param action
    * @param tups - a seq of tup2 (name, value) or tup3 (type, name, value)
    * @return
    */
  def multipart(action: String, tups: FormInputType*): Tag = 
    multipart(action, "POST")(inputs(tups*)*)

  /**
    *
    * @param action
    * @param method
    * @return
    */
  def multipart(action: String, method: String): Tag =  
    form(_method:=method, _action:=action, enctype:="multipart/form-data")

  /**
    *
    * @param url
    * @param tups - a seq of tup2 (name, value) or tup3 (type, name, value)
    * @return
    */
  def text(url: String, tups: FormInputType*): Tag = 
    text(url, "POST")(inputs(tups*)*)

  /**
    *
    * @param action
    * @param method
    * @param fields
    * @return
    */
  def text(action: String, method: String): Tag =  
    form(_method:=method, _action:=action, enctype:="text/plain")

  def submitButton(action: String, hiddenInputs: (String, AttributeValueType)*)(btnAttrs: Node*): Tag = 
    urlencoded(action)(hidden(hiddenInputs*), button(tpe:="submit", btnAttrs))

end Form
