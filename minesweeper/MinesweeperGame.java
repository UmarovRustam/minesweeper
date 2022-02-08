package com.javarush.games.minesweeper;

import com.javarush.engine.cell.Color;
import com.javarush.engine.cell.Game;

import java.util.ArrayList;
import java.util.List;

public class MinesweeperGame extends Game {
    private static final int SIDE = 9;
    private GameObject[][] gameField = new GameObject[SIDE][SIDE];
    private int countMinesOnField;
    private static final String MINE = "\uD83D\uDCA3";
    private static final String FLAG = "\uD83D\uDEA9";
    private int countFlags;
    private boolean isGameStopped;
    private int countClosedTiles = SIDE*SIDE;
    private int score;

    @Override
    public void initialize() {
        setScreenSize(SIDE, SIDE);
        createGame();
    }

    @Override
    public void onMouseLeftClick(int x, int y) {
        if(isGameStopped){
            restart();
            return;
        }
        openTile(x, y);

    }

    @Override
    public void onMouseRightClick(int x, int y) { markTile(x, y); }

    private void createGame() {
        for (int y = 0; y < SIDE; y++) {
            for (int x = 0; x < SIDE; x++) {
                boolean isMine = getRandomNumber(10) < 2;
                if (isMine) {
                    countMinesOnField++;
                }
                gameField[y][x] = new GameObject(x, y, isMine);
                setCellColor(x, y, Color.ORANGE);
                setCellValue(x, y, "");
            }
        }
        countMineNeighbors();
        countFlags = countMinesOnField;
       // isGameStopped = false;
    }

    private List<GameObject> getNeighbors(GameObject gameObject) {
        List<GameObject> result = new ArrayList<>();
        for (int y = gameObject.y - 1; y <= gameObject.y + 1; y++) {
            for (int x = gameObject.x - 1; x <= gameObject.x + 1; x++) {
                if (y < 0 || y >= SIDE) {
                    continue;
                }
                if (x < 0 || x >= SIDE) {
                    continue;
                }
                if (gameField[y][x] == gameObject) {
                    continue;
                }
                result.add(gameField[y][x]);
            }
        }
        return result;
    }

    private void countMineNeighbors(){
        for (int i = 0; i < SIDE; i++) {
            for (int j = 0; j < SIDE; j++) {
                GameObject gameObject = gameField[i][j];
                if(!gameObject.isMine){
                    for (GameObject neighbor : getNeighbors(gameObject)){
                        if(neighbor.isMine){
                            gameObject.countMineNeighbors++;
                        }
                    }
                }
            }
        }
    }

    private void openTile(int x, int y){

        GameObject gameObject = gameField[y][x];

        if(gameObject.isFlag == true || isGameStopped == true || gameObject.isFlag){
            return;
        }

        gameObject.isOpen = true;
        countClosedTiles--;
        setCellColor(x, y, Color.GREEN);
        if(gameObject.isOpen & !gameObject.isMine){
            score += 5;
        }
        setScore(score);
        if(gameObject.isMine){
            setCellValueEx(gameObject.x, gameObject.y, Color.RED, MINE);
            gameOver();

        } else if(gameObject.countMineNeighbors == 0){
           setCellValue(gameObject.x, gameObject.y, "");
           List<GameObject> neighbors = getNeighbors(gameObject);
               for(GameObject neighbor : neighbors){
                   if(!neighbor.isOpen){
                       openTile(neighbor.x, neighbor.y);
                   }
               }
            } else{
                    setCellNumber(x, y, gameObject.countMineNeighbors);
        } if(countClosedTiles == countMinesOnField){
            if(gameObject.isMine){
                setCellValueEx(gameObject.x, gameObject.y, Color.RED, MINE);
                gameOver();
            } else
           win();
        } 
    }

    private void markTile(int x, int y){
       GameObject gameObject = gameField[y][x];
        if (isGameStopped == true){
            return;
        }
       if (countFlags == 0 && gameObject.isFlag == false){
           return;
       } else if(gameObject.isFlag == false){
           gameObject.isFlag = true;
           countFlags--;
           setCellValue(x, y, FLAG);
           setCellColor(x, y, Color.YELLOW);
       } else if (gameObject.isFlag == true){
           gameObject.isFlag = false;
           countFlags++;
           setCellValue(x, y, "");
           setCellColor(x, y, Color.ORANGE);
        }
    }

    private void gameOver(){
        isGameStopped = true;
        printAllMines();
        showMessageDialog(Color.RED,"Ты проиграл", Color.BLACK,70);
    }
    private void win(){
        isGameStopped = true;
        showMessageDialog(Color.AQUA, "Ты выиграл", Color.ANTIQUEWHITE, 70);
    }

    private void printAllMines(){
        for (int i = 0; i < SIDE; i++) {
            for (int j = 0; j < SIDE; j++) {
                GameObject gameobject = gameField[j][i];
                if(gameobject.isMine) {
                    setCellValue(i, j, MINE);
                } else continue;
            }
        }
    }

    private void restart(){
            isGameStopped = false;
            countClosedTiles = SIDE * SIDE;
            score = 0;
            setScore(score);
            countMinesOnField = 0;
            createGame();
    }
}
