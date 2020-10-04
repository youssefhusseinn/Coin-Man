package com.youssef.coinman.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.Random;

public class CoinMan extends ApplicationAdapter {
	SpriteBatch batch;

	Texture background;
	Texture[] man;
	int manState=0;
	int pause = 0;
	float gravity = 0.6f;
	float velocity = 0;
	int manY = 0 ;
	Rectangle manRectangle;
	BitmapFont font;
	Random random;
	int score = 0;
	int gameState = 0;
	int speed = 1;
	ArrayList<Integer> coinXs = new ArrayList<>();
	ArrayList<Integer> coinYs = new ArrayList<>();
	ArrayList<Rectangle> coinRectangles = new ArrayList<>();
	Texture coin;
	int coinCount;

	ArrayList<Integer> bombXs = new ArrayList<>();
	ArrayList<Integer> bombYs = new ArrayList<>();
	ArrayList<Rectangle> bombRectangles = new ArrayList<>();
	Texture bomb;
	int bombCount;


	public void makeCoin(){
		float height = random.nextFloat()*Gdx.graphics.getHeight();
		coinYs.add((int)height);
		coinXs.add(Gdx.graphics.getWidth());
	}

	public void makeBomb(){
		float height = random.nextFloat()*Gdx.graphics.getHeight();
		bombYs.add((int)height);
		bombXs.add(Gdx.graphics.getWidth());
	}

	@Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("bg.png");
		man = new Texture[]{new Texture("frame-1.png"),new Texture("frame-2.png"),new Texture("frame-3.png"),new Texture("frame-4.png")};
		manY = Gdx.graphics.getHeight()/2;

		coin = new Texture("coin.png");
		bomb = new Texture("bomb.png");

		random = new Random();

		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(10);
	}

	@Override
	public void render () {
		batch.begin();
		batch.draw(background,0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());

		if(gameState == 1){
			//GAME IS LIVE
			if(bombCount<300){
				bombCount++;
			}else {
				bombCount = 0;
				makeBomb();
				speed = speed == 10? 10 : speed+1;
			}
			bombRectangles.clear();
			for(int i=0;i<bombXs.size();++i){
				batch.draw(bomb,bombXs.get(i),bombYs.get(i));
				bombXs.set(i,bombXs.get(i)-(8+speed));
				bombRectangles.add(new Rectangle(bombXs.get(i),bombYs.get(i),bomb.getWidth(),bomb.getHeight()));
			}

			if(coinCount < 80)
				coinCount++;
			else{
				coinCount = 0;
				makeCoin();
			}

			coinRectangles.clear();
			for(int i=0;i<coinXs.size();++i){
				batch.draw(coin,coinXs.get(i),coinYs.get(i));
				coinXs.set(i,coinXs.get(i)-(8+speed));
				coinRectangles.add(new Rectangle(coinXs.get(i),coinYs.get(i),coin.getWidth(),coin.getHeight()));
			}
			if(Gdx.input.justTouched()){
				velocity = -20-speed;
			}

			if(pause<8)
				pause++;
			else {
				pause = 0;
				if (manState < 3) {
					manState++;
				} else
					manState = 0;
			}
			velocity += gravity;
			manY -= (velocity+speed);
			if(manY<0)
				manY = 0;
		}else if(gameState == 0){
			//WAITING TO START
			BitmapFont start = new BitmapFont();
			start.setColor(Color.WHITE);
			start.getData().setScale(5);
			GlyphLayout glyphLayout = new GlyphLayout();
			String text = "TAP TO START";
			glyphLayout.setText(start,text);
			float w = glyphLayout.width;
			start.draw(batch,"TAP TO START",Gdx.graphics.getWidth()/2-w/2,Gdx.graphics.getHeight()/2);
			if(Gdx.input.justTouched()){
				gameState = 1;
				start.dispose();
			}
		}else if(gameState == 2){
			//GAME OVER
			batch.draw(new Texture("dizzy-1.png"),Gdx.graphics.getWidth()/2-man[manState].getWidth()/2,manY);
			if(Gdx.input.justTouched()){
				gameState = 1;
				manY = Gdx.graphics.getHeight()/2;
				score = 0;
				velocity = 0;
				speed = 0;
				coinXs.clear();
				coinYs.clear();
				coinRectangles.clear();
				coinCount = 0;
				bombXs.clear();
				bombYs.clear();
				bombRectangles.clear();
				bombCount = 0;
			}
			else {
				batch.end();
				return;
			}
		}

		batch.draw(man[manState],Gdx.graphics.getWidth()/2-man[manState].getWidth()/2,manY);
		manRectangle = new Rectangle(Gdx.graphics.getWidth()/2 - man[manState].getWidth()/2,manY,man[manState].getWidth(),man[manState].getHeight());
		for(int i=0;i<coinRectangles.size();++i){
			if(Intersector.overlaps(manRectangle,coinRectangles.get(i))){
				score++;
				coinRectangles.remove(i);
				coinXs.remove(i);
				coinYs.remove(i);
				break;
			}
		}

		for(int i=0;i<bombRectangles.size();++i){
			if(Intersector.overlaps(manRectangle,bombRectangles.get(i))){
				gameState = 2;
			}
		}
		font.draw(batch,String.valueOf(score),100,200);
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
