package bgu.spl.net.impl;


import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.impl.messages.Message;
import java.lang.reflect.Constructor;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class CourseRegEncoderDecoder implements MessageEncoderDecoder<Message> {

    private byte[] bytes = new byte[1 << 10];
    private int len;
    private int counterOfDelimiter;
    private boolean messageReady;
    private String[] classesNames;
    private short opcode;
    private static final String messages_location = "bgu.spl.net.impl.messages.";

    public CourseRegEncoderDecoder(){
        // array to match opcode to its proper string
        classesNames = new String[]{"","USERREG","USERREG","LOGIN","LOGOUT","COURSEREG","KDAMCHECK",
                "COURSESTAT","STUDENTSTAT","ISREGISTERED","UNREGISTER","MYCOURSES"};
        len = 0;
        counterOfDelimiter = 0;
        messageReady = false;
    }

    public Message decodeNextByte(byte nextByte)  {
        if (checkEndOfMessage(nextByte))
            return createMessage();
        pushByte(nextByte);
        if (len == 2){
            opcode = bytesToShort(bytes,0);
            if (opcode ==4 | opcode == 11) messageReady = true;
        }
        if (messageReady) return createMessage();
        return null;
    }

    public short bytesToShort(byte[] byteArr, int ind) {
        short result = (short)((byteArr[ind] & 0xff) << 8);
        result += (short)(byteArr[ind+1] & 0xff);
        return result;
    }

    public byte[] shortToBytes(short num) {
        byte[] bytesArr = new byte[2];
        bytesArr[0] = (byte)((num >> 8) & 0xFF);
        bytesArr[1] = (byte)(num & 0xFF);
        return bytesArr;
    }

    private Message createMessage() {
        Message result = null;
        short messageType = bytesToShort(bytes,0);
        try{
            // create the right message object according to it`s opcode
            Class classType = Class.forName(messages_location + classesNames[messageType]);
            Constructor<? extends Message> con = classType.getConstructor(Short.class);
            result = con.newInstance(messageType);
            // generic build according to message type
            if (result.getType()){
                short courseNum = bytesToShort(bytes,2);
                result.build(courseNum);
            }
            else {
                String messageBody = new String(bytes, 2, len, StandardCharsets.UTF_8);
                String[] messageParams = messageBody.split("\0");
                result.build(messageParams);
            }

        }catch (Exception e){}
        // cleaning the array for a new message
        bytes = new byte[1 << 10];
        len = 0;
        counterOfDelimiter = 0;
        messageReady = false;
        return result;
    }


    private void pushByte(byte nextByte) {
        if (len >= bytes.length)
            bytes = Arrays.copyOf(bytes, len*2);
        bytes[len] = nextByte;
        len++;
    }


    public byte[] encode(Message msg){
        byte[] opcode = shortToBytes(msg.getResultType());
        byte[] messageOpcode = shortToBytes(msg.getOpcode());
        byte[] info;
        byte[] result;
        // error case- return only 2 opcodes
        if (msg.getResultType() == (short)13)
            result = new byte[opcode.length + messageOpcode.length];
        else {
            // ack case, depending on message type
            if (msg.getResult() != null){
                info = msg.getResult().getBytes();
                result = new byte[opcode.length + messageOpcode.length + msg.getResult().length() +1];
                System.arraycopy(info,0, result,opcode.length+messageOpcode.length,info.length);
            }
            else result = new byte[opcode.length + messageOpcode.length +1];
            result[result.length-1] = '\0';
        }
        System.arraycopy(opcode, 0, result, 0,opcode.length);
        System.arraycopy(messageOpcode, 0, result,opcode.length, messageOpcode.length);
        return result;
    }

    boolean checkEndOfMessage(byte nextByte) {
        // this function checks if the message is complete
        // cases depends on opcode formats
        if (len < 2) {// still reading opcode
            return false;
        }else{
            if (opcode < (short)4) {
                if (nextByte == '\0') counterOfDelimiter = counterOfDelimiter+1;
                return (counterOfDelimiter == 2);
            } else if(opcode == (short) 8) {
                if (nextByte == '\0') counterOfDelimiter = +1;
                return (counterOfDelimiter == 1);
            }else if (len == 3){
                messageReady = true;
                return (len == 4);
            }
            return false;
        }
    }
}
