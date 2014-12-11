package com.geoexplorer;

import java.util.ArrayList;
import java.util.HashMap;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import com.qualcomm.toq.smartwatch.api.v1.deckofcards.DeckOfCardsEventListener;
import com.qualcomm.toq.smartwatch.api.v1.deckofcards.ResourceStoreException;
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
    private Button createPOIBtn;
    private ArrayList<HashMap<String,String>> cardList;
    private int uniqueID = 0;
    private int numPOIs = 0;

    private class DeckOfCardsEventListenerImpl implements DeckOfCardsEventListener{
        public void onCardOpen(final String cardId){
            if (cardId.equals("card0")) {
                ListCard listCard = mRemoteDeckOfCards.getListCard();
                SimpleTextCard headerCard = (SimpleTextCard) listCard.get(cardId);
                String[] messageTxt = new String[1];
                messageTxt[0] = "At your earliest convenience use your phone to update the Point of Interest's information.";
                headerCard.setMessageText(messageTxt);
                headerCard.setTitleText("Location Saved!");
                numPOIs += 1;
                try {
                    mDeckOfCardsManager.updateDeckOfCards(mRemoteDeckOfCards);
                } catch (RemoteDeckOfCardsException e) {}
            }
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

    private View.OnClickListener mCreatePOIOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (numPOIs > 0) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Intent intent = new Intent(MainActivity.this, AddPOIActivity.class);
                        intent.putExtra("cardList", cardList);
                        startActivity(intent);
                    }
                });
            } else {
                Toast.makeText(getApplicationContext(), "No positions saved yet! Please save one using your Toq.", Toast.LENGTH_LONG).show();
            }
        }
    };

    private int getUniqueID() {
        uniqueID += 1;
        return uniqueID;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toq);

        createPOIBtn = (Button)findViewById(R.id.create_btn);
        createPOIBtn.setOnClickListener(mCreatePOIOnClickListener);
        mDeckOfCardsManager = DeckOfCardsManager.getInstance(getApplicationContext());
        mEventListener = new DeckOfCardsEventListenerImpl();
        mDeckOfCardsManager.addDeckOfCardsEventListener(mEventListener);
        cardList = (ArrayList<HashMap<String,String>>)getIntent().getSerializableExtra("cardList");
        if (cardList == null) {
            cardList = new ArrayList<HashMap<String,String>>();
        }

        init();

        install();

        for (HashMap<String,String> newCardData : cardList) {
            addDestinationCard(newCardData.get("name"), newCardData.get("description"), newCardData.get("difficulty"), "10 mi", "90 min", newCardData.get("imagePath"));
        }
    }

    private SimpleTextCard addDestinationCard(String name, String description, String difficulty, String distance, String eta, String imagePath) {
        Bitmap image = null;
        if (!imagePath.equals("")) {
            image = BitmapFactory.decodeFile(imagePath);
        }
        return addDestinationCard(name, description, difficulty, distance, eta, image);
    }
    private SimpleTextCard addDestinationCard(String name, String description, String difficulty, String distance, String eta, Bitmap image) {
        SimpleTextCard newCard = new SimpleTextCard("newcard" + getUniqueID());
        newCard.setHeaderText(name);
        String messages[] = new String[4];
        messages[0] = "Distance: " + distance;
        messages[1] = "Difficulty: " + difficulty;
        messages[2] = "ETA: " + eta;
        messages[3] = description;
        newCard.setMessageText(messages);
        if (image != null) {
            Bitmap scaledImage = Bitmap.createScaledBitmap(image, 250, 288, false);
            newCard.setCardImage(mRemoteResourceStore, new CardImage("image" + Integer.toString(getUniqueID()), scaledImage));
        }
        mRemoteDeckOfCards.getListCard().add(newCard);
        try {
            mDeckOfCardsManager.updateDeckOfCards(mRemoteDeckOfCards, mRemoteResourceStore);
        } catch (RemoteDeckOfCardsException e) {}
        return newCard;
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
        addDestinationCard("Campanile","A famous bell tower in Berkeley. A must see!","1","0.5 mi","10 min",BitmapFactory.decodeResource(this.getApplicationContext().getResources(), R.drawable.campanile));
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
