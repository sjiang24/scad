/*
* Concrete class for 3DS format
*/
package using3DS;

import java.util.ArrayList;

class VerticesList extends Format3DS {

    private ArrayList<Double> list;

    VerticesList(byte[] data) { super(data); }

    @Override
    public <Double> ArrayList<Double> parseBytes() {
        list = new ArrayList<>();
        
        return list;
    }
}
