/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package using3DS;

import java.io.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

class Import3DS {

    private String fileName;

    private byte[] buffer;
    
    private static HashMap<String, Integer> fileFormat;

    private static String[] chunkIDs = {
        "Main", "M3D Version", "3D Editor", "Object Block", "Triangular Mesh", 
        "Vertices List", "Faces Description", "Faces Material", "Smoothing Group List", "Mapping Coordinates List",
        "Local Coordinates System", "Light", "Spotlight", "Camera", "Material Block",
        "Material Name", "Ambient Color", "Diffuse Color", "Specular Color", "Texture Map 1",
        "Bump Map", "Reflection Map", "Mapping Filename", "Mapping Parameters", "KeyFramer Chunk",
        "Mesh Information Block", "Spot Light Information Block", "Frames", "Object Name", "Object Pivot Point",
        "Position Track", "Rotation Track", "Scale Track", "Hierarchy Position"
    };

    private static Integer[] chunkCodes = {
        0x4D4D, 0x0002, 0x3D3D, 0x4000, 0x4100, 0x4110, 0x4120, 0x4130, 0x4150, 0x4140, 0x4160, 0x4600, 0x4610, 0x4700,
        0xAFFF, 0xA000, 0xA010, 0xA020, 0xA030, 0xA200, 0xA230, 0xA220, 0xA300, 0xA351, 0xB000, 0xB002, 0xB007, 0xB008,
        0xB010, 0xB013, 0xB020, 0xB021, 0xB022, 0xB030 
    };

    public Import3DS(String fileName) {
        this.fileName = fileName;
    }

    public static void main(String args[]) {
        fileFormat = new HashMap<>();
        loadFormat();

        Import3DS in = new Import3DS("data/cube.3ds");
        in.read();
        in.getChunk("Material Name");
    }

    private byte[] getChunk(String key) {
        int startOf = 0;
        for(int i = 0; i < buffer.length/2; i++) {
            int currentByte = (((buffer[i*2+1] << 8) & 0x0000FF00) 
                    | (buffer[i*2] & 0x00FF));
            System.out.println(String.format("%04X",currentByte));
            if(currentByte == fileFormat.get(key)) {
                startOf = i*2+2;
            }
        }
        
        /*
        System.out.println(""+startOf);
        System.out.println(String.format("%02X",buffer[startOf]));
        System.out.println(String.format("%02X",buffer[startOf+1]));
        System.out.println(String.format("%02X",buffer[startOf+2]));
        System.out.println(String.format("%02X",buffer[startOf+3]));
        */
        
        int size = (buffer[startOf] & 0x000000FF) 
                | ((buffer[startOf+1] << 8) & 0x0000FF00)
                | ((buffer[startOf+2] << 16) & 0x00FF0000) 
                | (buffer[startOf+3] << 24 & 0xFF000000);
        
        //System.out.println(String.format("%08X",size));
        
        System.out.println(size);
        return Arrays.copyOfRange(buffer, startOf + 4, startOf + 4 + size - 6);
    }

    private <T> void parseChunk(String key) {
        byte[] bytes = getChunk(key);
        T[] result = new T[bytes.length];
        for(int i = 0; i < bytes.length; i++) {
            if(T instanceof Integer) {
                result[i] = bytes[i].intValue();
            } else if(T instanceof float) {
                result[i] = bytes[i].floatValue();
            } else if(T instanceof double) {
                result[i] = bytes[i].doubleValue();
            } else if(T instanceof byte) {
                result[i] = bytes[i];
            } else if(T instanceof short) {
                result[i] = bytes[i].shortValue();
            } else {
                System.out.println("Issue...");
            }

            result[i] = bytes[i];
        }
    }

    private static void loadFormat() {
        for(int x = 0; x < chunkIDs.length; x++) {
            fileFormat.put(chunkIDs[x], chunkCodes[x]);
        }
    }

    public void read() {
        try {
            buffer = new byte[1000];
            FileInputStream inStream = new FileInputStream(fileName);

            while(inStream.read(buffer) != -1) {}
            
            for (byte b : buffer) {
                //System.out.print(String.format("%02X", b));
            }

            inStream.close();
        } catch(IOException e) {
            System.out.println(e);
        }
    }
}
