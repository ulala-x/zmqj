package org.zeromq;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class ZMessage extends LinkedList<ZFrame> implements AutoCloseable {

    public ZMessage(){}
    public ZMessage(ZFrame ...args){
        Arrays.stream(args).forEach(arg->{
            //parts.add(arg);
            super.add(arg);
        });
    }

    @Override
    public void close()  {
        super.stream().forEach( frame -> {
            try {
                frame.close();
            } catch (Exception e) {
                // todo logging
                //throw new RuntimeException(e);
            }
        });
        super.clear();
    }
    public int length(int index){
        if(index >= super.size()){
            throw new IndexOutOfBoundsException("attempting to request a message part outside the valid range");
        }
        return super.get(index).length();
    }
    public long contentSize()
    {
        long size = 0;

        for(int i=0;i< super.size();++i){
            size +=super.get(i).length();
        }

        return size;
    }
    public byte[] data(int index){
        if(index >= super.size()){
            throw new IndexOutOfBoundsException("attempting to request a message part outside the valid range");
        }
        return super.get(index).data();
    }

    public ZFrame frame(int index){
        if(index >= super.size()){
            throw new IndexOutOfBoundsException("attempting to request a message part outside the valid range");
        }
        return super.get(index);
    }

    public void removeClose(int index) throws Exception {
        if(index >= super.size()){
            throw new IndexOutOfBoundsException("attempting to request a message part outside the valid range");
        }
        super.remove(index).close();
    }


    public void sent(int index) {
        if(index >= super.size()){
            throw new IndexOutOfBoundsException("attempting to request a message part outside the valid range");
        }

        if(!super.get(index).isSent()){
            super.get(index).markSent();
        }
    }


}
