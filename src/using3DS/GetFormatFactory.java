/*
* Factory for creating format objects
*/
// package using3DS;

class getFormatFactory {
    public Format3DS getFormatFactory(Integer code, byte[] data) {
        switch(code) {
            case 0x4D4D:
                // return new;
            case 0x0002:
                // return new;
            case 0x3D3D:
                // return new;
            case 0x4000:
                // return new; 
            case 0x4100:
                // return new;
            case 0x4110:
                return new VerticesList(data);
            case 0x4120:
                // return new;
            case 0x4130:
                // return new;
            case 0x4150:
                // return new;
            case 0x4140:
                // return new;
            case 0x4160:
                // return new;
            case 0x4600:
                // return new;
            case 0x4610:
                // return new;
            case 0x4700:
                // return new;
            case 0xAFFF:
                // return new;
            case 0xA000:
                // return new;
            case 0xA010:
                // return new;
            case 0xA020:
                // return new;
            case 0xA030:
                // return new;
            case 0xA200:
                // return new;
            case 0xA230:
                // return new;
            case 0xA220:
                // return new;
            case 0xA300:
                // return new;
            case 0xA351:
                // return new;
            case 0xB000:
                // return new;
            case 0xB002:
                // return new;
            case 0xB007:
                // return new;
            case 0xB008:
                // return new;
            case 0xB010:
                // return new;
            case 0xB013:
                // return new;
            case 0xB020:
                // return new;
            case 0xB021:
                // return new;
            case 0xB022:
                // return new;
            case 0xB030:
                // return new;
            default:
                return null;
        }
    }
}
