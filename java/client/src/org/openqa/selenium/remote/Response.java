// Licensed to the Software Freedom Conservancy (SFC) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The SFC licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package org.openqa.selenium.remote;

import java.util.Objects;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class Response {

  private volatile Object value;
  private volatile String sessionId;
  private volatile Integer status;
  private volatile String state;
  private byte[] content;
  public Response() {
  }

  public Response(SessionId sessionId) {
    this.sessionId = String.valueOf(sessionId);
  }

  public Integer getStatus() {
    return status;
  }

  public void setStatus(Integer status) {
    this.status = status;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public void setValue(Object value) {
    this.value = value;
  }

  public Object getValue() {
    return value;
  }

  public void setSessionId(String sessionId) {
    this.sessionId = sessionId;
  }

  public String getSessionId() {
    return sessionId;
  }

  @Override
  public String toString() {
    return String.format("(Response: SessionID: %s, Status: %s, Value: %s)", getSessionId(), getStatus(), getValue());
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Response)) {
      return false;
    }

    Response that = (Response) o;
    return Objects.equals(value, that.value) &&
           Objects.equals(sessionId, that.sessionId) &&
           Objects.equals(status, that.status) &&
           Objects.equals(state, that.state);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value, sessionId, status, state);
  }

  public void saveToFile(String fileName){
    try{
      if(fileName != null){
        FileOutputStream fos = new FileOutputStream(fileName);
        try{
          System.out.println(" File Output ="+content);
          fos.write(content);
          fos.close();
        }catch (Exception e){System.out.println("Exception IN Resopnse while saving the data");e.printStackTrace();}
      }
    }catch(Exception e){e.printStackTrace();} 
  }
  public void setByteArrayResponse(byte[] content){
    this.content=content;
  }
  public String getContentString(){
    return getValue().toString();
  } 
}
