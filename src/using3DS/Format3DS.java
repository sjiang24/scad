/*
* Parent class of all 3DS Formatted Data
*/
package using3DS;

import java.util.ArrayList;

abstract class Format3DS {

    private String fileName;

    protected byte[] data;

    // Take in file from user and use to create sub objects
    public Format3DS(String fileName) { this.fileName = fileName; }

    // Super constructor for children to store data
    protected Format3DS(byte[] data) { this.data = data; }

    public byte[] getData() { return data; }
    
    public abstract <T> ArrayList<T> parseBytes();
}