package com.example.prakritibansal.dermahack;

/**
 * Created by prakritibansal on 2/11/18.
 */

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;


public class Conversation {
    private static List<TextMessage> currentConversation;
    private static TreeSet<Integer> mCardSet;
    private static TreeSet<Integer> mSentMsg;

    public static void clear() {
        currentConversation = new ArrayList<TextMessage>();
        mCardSet = new TreeSet<Integer>();
        mSentMsg = new TreeSet<Integer>();
    }

    public static void add(final TextMessage message) {
        if (currentConversation == null) {
            clear();
        }

        currentConversation.add(message);
        if("cd".equals(message.getFrom())){
            mCardSet.add(getCount()-1);
        }
        if("tx".equals(message.getFrom())){

            mSentMsg.add(getCount()-1);
        }

    }

    public static boolean inCardSet(int pos){

        return mCardSet.contains(pos);
    }

    public static boolean inSentSet(int pos){

        return mSentMsg.contains(pos);
    }

    public static TextMessage getMessage(final int pos) {
        if (currentConversation == null) {
            return null;
        }
        return currentConversation.get(pos);
    }

    public static int getCount() {
        return currentConversation == null ? 0 : currentConversation.size();
    }
}

