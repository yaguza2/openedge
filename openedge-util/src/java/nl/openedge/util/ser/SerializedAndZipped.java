/*
 * $Id$
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
package nl.openedge.util.ser;

import java.io.Serializable;

/**
 * Struct for serialized and zipped objects.
 * 
 * @author Eelco Hillenius
 */
public final class SerializedAndZipped implements Serializable, Cloneable
{

    /**
     * Member compressed data data, value = 'compressedData'.
     */
    public static final String COMPRESSED_DATA = "compressedData";

    /**
     * zipped object data array.
     */
    private byte[] compressedData;

    /**
     * Member uncompressed data length, value = 'uncompressedDataLength'.
     */
    public static final String UNCOMPRESSED_DATA_LENGTH = "uncompressedDataLength";

    /**
     * de original length of object data array.
     */
    private int uncompressedDataLength;

    /**
     * Construct.
     */
    public SerializedAndZipped()
    {
        // nothing here
    }
    /**
     * Construct with original length and compressed data.
     * 
     * @param uncompressedDataLength
     * @param compressedData
     */
    public SerializedAndZipped(int uncompressedDataLength, byte[] compressedData)
    {
        this.uncompressedDataLength = uncompressedDataLength;
        this.compressedData = compressedData;
    }

    /**
     * Get objectData.
     * 
     * @return byte[] Returns the objectData.
     */
    public byte[] getCompressedData()
    {
        return compressedData;
    }

    /**
     * Set objectData.
     * 
     * @param objectData objectData to set.
     */
    public void setCompressedData(byte[] objectData)
    {
        this.compressedData = objectData;
    }

    /**
     * Get originalLength.
     * 
     * @return int Returns the originalLength.
     */
    public int getUncompressedDataLength()
    {
        return uncompressedDataLength;
    }

    /**
     * Set originalLength.
     * 
     * @param originalLength originalLength to set.
     */
    public void setUncompressedDataLength(int originalLength)
    {
        this.uncompressedDataLength = originalLength;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return "SerializedAndZipped {uncompressedDataLength = "
            + uncompressedDataLength + ", compressedData = "
            + compressedData + "}";
    }

    /**
     * @see java.lang.Object#clone()
     */
    protected Object clone()
    {
        try
        {
            return super.clone();
        }
        catch(CloneNotSupportedException e)
        {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}