/*
 * $Header$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 * Copyright (c) 2003, Open Edge B.V.
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, 
 * this list of conditions and the following disclaimer. Redistributions 
 * in binary form must reproduce the above copyright notice, this list of 
 * conditions and the following disclaimer in the documentation and/or other 
 * materials provided with the distribution. Neither the name of OpenEdge B.V. 
 * nor the names of its contributors may be used to endorse or promote products 
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF 
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
package nl.openedge.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author	Hillenius
 * 
 * Wrapper om messages te kunnen 'wrappen'. Handig icm Velocity en template 'messages.vm'
 * wat er volgt uit kan zien:
 * 
 *	#if( $GUI_MESSAGE )
 *	  <table class="messagebackground" width="100%">
 *	  <!-- ------------------------- Begin messages ------------------------- -->
 *	  #set( $class = "infoMsg" )
 *	  
 *	  #if($GUI_MESSAGE.SingleMessage || $GUI_MESSAGE.MultipleMessage)
 *	      #if( $GUI_MESSAGE.Type == 0 )#set( $class = "errorMsg" ) #end
 *	      #if( $GUI_MESSAGE.Type == 1 )#set( $class = "informMsg" ) #end
 *	      #if( $GUI_MESSAGE.Type == 2 )#set( $class = "warningMsg" ) #end
 *	      #if( $GUI_MESSAGE.Type == 3 )#set( $class = "questionMsg" ) #end
 *	      #if( $GUI_MESSAGE.SingleMessage )
 *	        <tr align="left">
 *	          <td class="$class">$GUI_MESSAGE.SingleMessage</td>
 *	        </tr>
 *	      #end
 *	      #if( $GUI_MESSAGE.MultipleMessage )
 *	        #foreach( $singleMessage in $GUI_MESSAGE.MultipleMessage )
 *	          <tr align="left">
 *	            <td class="$class">$singleMessage</td>
 *	          </tr>
 *	        #end
 *	      #end
 *	  #end
 *	  
 *	  #if( $GUI_MESSAGE.MultipleGUIMessage )
 *	    #foreach( $singleGuiMessage in $GUI_MESSAGE.MulitpleGUIMessage )
 *	      #if( $singleGuiMessage.Type == 0 )#set( $class = "errorMsg" ) #end
 *	      #if( $singleGuiMessage.Type == 1 )#set( $class = "informMsg" ) #end
 *	      #if( $singleGuiMessage.Type == 2 )#set( $class = "warningMsg" ) #end
 *	      #if( $singleGuiMessage.Type == 3 )#set( $class = "questionMsg" ) #end
 *	      <tr align="left">
 *	        <td class="$class">$singleGuiMessage.SingleMessage</td>
 *	      </tr>
 *	    #end
 *	  #end
 *	  <!-- ------------------------- End messages ------------------------- -->
 *	  </table>
 *	#end
 * 
 * Deze kan bijv. weer in een andere macro worden opgenomen als volgt: 
 * #parse( 'messages.vm' )
 */
public final class GUIMessage {
  
  /**
   * Message type for errors. Value == 0
   */
  public static int MSGTYPE_ERROR = 0;
  
  /**
   * Message type for information. Value == 1
   */
  public static int MSGTYPE_INFORM = 1;
  
  /**
   * Message type for warnings. Value == 2
   */
  public static int MSGTYPE_WARNING = 2;
  
  /**
   * Message type for questions. Value == 3
   */
  public static int MSGTYPE_QUESTION = 3;

  /*
   * single message
   */
  private String singleMessage = null;
  
  /*
   * multiple (single) messages; all the same type
   */
  private List multipleMessage = null;
  
  /*
   * embedded/ nested message wrappers
   */
  private List multipleGUIMessage = null;
  
  /*
   * type of single message(s)
   */
  private int type;

  /**
   * Empty constructor
   */
  public GUIMessage() {
  	//	
  }

  /**
   * Construct a single messge with given type
   * @param singleMessage		the message
   * @param type				type of message (one of the static fields of this class)
   */
  public GUIMessage(String singleMessage, int type) {
    this.singleMessage = singleMessage;
    this.type = type;
  }

  /**
   * Construct multiple messages of (same) given type
   * @param multipleMessage	the messages
   * @param type				type of message (one of the static fields of this class)
   */
  public GUIMessage(String[] messages, int type) {
    multipleMessage = new ArrayList(messages.length);
    for(int i = 0; i < messages.length; i++) {
    	multipleMessage.add(messages[i]);	
    }
    this.type = type;
  }

  /**
   * add multiple message wrappers
   * @param messages		message wrappers
   */
  public GUIMessage(GUIMessage[] messages) {
    if(multipleGUIMessage == null) {
    	multipleGUIMessage = new ArrayList(messages.length);	
    }
    for(int i = 0; i < messages.length; i++) {
    	multipleGUIMessage.add(messages[i]);	
    }
  }
  
  /**
   * Add message wrapper
   * @param message
   */
  public void addGUIMessage(GUIMessage message) {
    if(multipleGUIMessage == null) {
    	multipleGUIMessage = new ArrayList();	
    }
    multipleGUIMessage.add(message);	
  }

  /**
   * get the single message
   * @return String	the message, possibly null
   */
  public String getSingleMessage() {
    return singleMessage;
  }

  /**
   * get the messages
   * @return List	the messages, possibly null
   */
  public List getMultipleMessage() {
    return multipleMessage;
  }

  /**
   * get the embedded messages
   * @return List	the message wrappers, possibly null
   */
  public List getMultipleGUIMessage() {
    return multipleGUIMessage;
  }

  /**
   * get the type of the single message(s)
   * type of message (one of the static fields of this class) 
   */
  public int getType() {
    return type;
  }
  
  /**
   * returns true if there is any message, false if none
   * @return boolean
   */
  public boolean containsMessage() {
  	return ( (singleMessage != null) ||
  		( (multipleMessage != null) && (!multipleMessage.isEmpty()) ) || 
  		( (multipleGUIMessage != null) && (!multipleGUIMessage.isEmpty()) ) );	
  }
  
}
