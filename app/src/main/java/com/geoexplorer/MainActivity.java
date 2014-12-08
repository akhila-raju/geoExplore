package com.geoexplorer;

import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import com.qualcomm.toq.smartwatch.api.v1.deckofcards.DeckOfCardsEventListener;
import com.qualcomm.toq.smartwatch.api.v1.deckofcards.card.ListCard;
import com.qualcomm.toq.smartwatch.api.v1.deckofcards.card.NotificationTextCard;
import com.qualcomm.toq.smartwatch.api.v1.deckofcards.card.SimpleTextCard;
import com.qualcomm.toq.smartwatch.api.v1.deckofcards.remote.DeckOfCardsManager;
import com.qualcomm.toq.smartwatch.api.v1.deckofcards.remote.RemoteDeckOfCards;
import com.qualcomm.toq.smartwatch.api.v1.deckofcards.remote.RemoteDeckOfCardsException;
import com.qualcomm.toq.smartwatch.api.v1.deckofcards.remote.RemoteResourceStore;
import com.qualcomm.toq.smartwatch.api.v1.deckofcards.remote.RemoteToqNotification;
import com.qualcomm.toq.smartwatch.api.v1.deckofcards.resource.CardImage;

public class MainActivity extends Activity {

    private Random rand = new Random();
    private DeckOfCardsManager mDeckOfCardsManager;
    private RemoteDeckOfCards mRemoteDeckOfCards;
    private RemoteResourceStore mRemoteResourceStore;
    private DeckOfCardsEventListener mEventListener;

    private class DeckOfCardsEventListenerImpl implements DeckOfCardsEventListener{
        public void onCardOpen(final String cardId){
            if (cardId.equals("card0")) {
                ListCard listCard = mRemoteDeckOfCards.getListCard();
                SimpleTextCard headerCard = (SimpleTextCard) listCard.get(cardId);
                String[] messageTxt = new String[1];
                messageTxt[0] = "At your earliest convenience use your phone to update the Point of Interest's information.";
                headerCard.setMessageText(messageTxt);
                headerCard.setTitleText("Location Saved!");
                try {
                    mDeckOfCardsManager.updateDeckOfCards(mRemoteDeckOfCards);
                } catch (RemoteDeckOfCardsException e) {}
            }
            runOnUiThread(new Runnable(){
                public void run(){
                    Intent intent = new Intent(MainActivity.this, AddPOIActivity.class);
                    startActivity(intent);
                }
            });
        }
        public void onCardVisible(final String cardId){}
        public void onCardInvisible(final String cardId){}
        public void onCardClosed(final String cardId) {
            if (cardId.equals("card0")) {
                ListCard listCard = mRemoteDeckOfCards.getListCard();
                SimpleTextCard headerCard = (SimpleTextCard) listCard.get(cardId);
                String[] messageTxt = new String[1];
                messageTxt[0] = "";
                headerCard.setMessageText(messageTxt);
                headerCard.setTitleText("");
                try {
                    mDeckOfCardsManager.updateDeckOfCards(mRemoteDeckOfCards);
                } catch (RemoteDeckOfCardsException e) {}
            }
        }
        public void onMenuOptionSelected(final String cardId, final String menuOption){}
        public void onMenuOptionSelected(final String cardId, final String menuOption, final String quickReplyOption){}
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toq);

        mDeckOfCardsManager = DeckOfCardsManager.getInstance(getApplicationContext());
        mEventListener = new DeckOfCardsEventListenerImpl();
        mDeckOfCardsManager.addDeckOfCardsEventListener(mEventListener);

        init();

        install();
    }

    private void install() {
        updateDeckOfCardsFromUI();
        try {
            mDeckOfCardsManager.installDeckOfCards(mRemoteDeckOfCards, mRemoteResourceStore);
        }
        catch (RemoteDeckOfCardsException e){
            //e.printStackTrace();
            //Toast.makeText(this, "Application already installed", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateDeckOfCardsFromUI() {
        if (mRemoteDeckOfCards == null) {
            mRemoteDeckOfCards = createDeckOfCards();
        }
        ListCard listCard = mRemoteDeckOfCards.getListCard();
        SimpleTextCard simpleTextCard = (SimpleTextCard) listCard.childAtIndex(0);
        simpleTextCard.setHeaderText("Create New POI");
        simpleTextCard.setReceivingEvents(true);
        simpleTextCard.setShowDivider(true);
    }

    private RemoteDeckOfCards createDeckOfCards() {
        ListCard listCard = new ListCard();
        SimpleTextCard simpleTextCard = new SimpleTextCard("card0");
        listCard.add(simpleTextCard);
        return new RemoteDeckOfCards(this, listCard);
    }

    //    Initialise
    private void init() {

        // Create the resource store for icons and images
        mRemoteResourceStore = new RemoteResourceStore();
        // Try to retrieve a stored deck of cards
        try {
            mRemoteDeckOfCards = createDeckOfCards();
        }
        catch (Throwable th){
            th.printStackTrace();
        }
    }

    /**
     * @see android.app.Activity#onStart()
     */
    protected void onStart(){
        super.onStart();

        // If not connected, try to connect
        if (!mDeckOfCardsManager.isConnected()){
            try{
                mDeckOfCardsManager.connect();
            }
            catch (RemoteDeckOfCardsException e){
                e.printStackTrace();
            }
        }
    }

}
